package com.smartbuilders.smartsales.ecommerce.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.BuildConfig;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.data.OrderDB;
import com.smartbuilders.smartsales.ecommerce.data.SyncDataRealTimeWithServerDB;
import com.smartbuilders.smartsales.ecommerce.model.SyncDataRealTimeWithServer;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.utils.ApplicationUtilities;
import com.smartbuilders.synchronizer.ids.utils.ConsumeWebService;
import com.smartbuilders.synchronizer.ids.utils.DataBaseUtilities;

import net.iharder.Base64;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.util.LinkedHashMap;

/**
 * Created by stein on 22/7/2016.
 */
public class SyncDataRealTimeWithServerService extends IntentService {

    public static final String KEY_USER_ID = "SyncDataRealTimeWithServerService.KEY_USER_ID";
    public static final String KEY_SQL_SELECTION = "SyncDataRealTimeWithServerService.KEY_SQL_SELECTION";
    public static final String KEY_SQL_SELECTION_ARGS = "SyncDataRealTimeWithServerService.KEY_SQL_SELECTION_ARGS";
    public static final String SYNCHRONIZATION_FINISHED = BuildConfig.APPLICATION_ID
            + ".SyncDataRealTimeWithServerService.SYNCHRONIZATION_FINISHED";

    public SyncDataRealTimeWithServerService() {
        super(SyncDataRealTimeWithServerService.class.getSimpleName());
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SyncDataRealTimeWithServerService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        boolean orderWillBeSend = false;
        try {
            User user = ApplicationUtilities
                    .getUserByIdFromAccountManager(getApplicationContext(), workIntent.getStringExtra(KEY_USER_ID));
            if (user != null) {
                SyncDataRealTimeWithServerDB syncDataRealTimeWithServerDB = new SyncDataRealTimeWithServerDB(getApplicationContext(), user);
                /******************************************************************************/
                //si hay un nuevo registro por sincronizar entonces se agrega a la cola de registros por sincronizar
                if (workIntent.getStringArrayExtra(KEY_SQL_SELECTION_ARGS)!=null
                        && !TextUtils.isEmpty(workIntent.getStringExtra(KEY_SQL_SELECTION))) {
                    //se agrega el nuevo registro
                    JSONObject jsonObject = new JSONObject();
                    String[] selectionArgs = workIntent.getStringArrayExtra(KEY_SQL_SELECTION_ARGS);
                    for (int i = 0; i < selectionArgs.length; i++) {
                        if (selectionArgs[i] != null) {
                            jsonObject.put(String.valueOf(i), selectionArgs[i]);
                        }
                    }

                    syncDataRealTimeWithServerDB.insertDataToSyncWithServer(workIntent.getStringExtra(KEY_SQL_SELECTION),
                                    jsonObject.toString(), selectionArgs.length);
                    if (workIntent.getStringExtra(KEY_SQL_SELECTION).toUpperCase().startsWith("INSERT")) {
                        String[] words = workIntent.getStringExtra(KEY_SQL_SELECTION).trim().toUpperCase().replaceAll("\\s+", " ").split(" ");
                        orderWillBeSend = words[0].equals("INSERT") && words[1].equals("INTO") && words[2].equals("ECOMMERCE_ORDER");
                    }
                }
                /*****************************************************************************/
                if (orderWillBeSend) {
                    // create a handler to post messages to the main thread
                    (new Handler(getMainLooper())).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(SyncDataRealTimeWithServerService.this,
                                        R.string.sending_order, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                //do nothing
                            }
                        }
                    });
                }
                //se sincronizan los registros encolados para sincronizar en tiempo real
                JSONArray totalData = new JSONArray();
                for(SyncDataRealTimeWithServer syncDataRealTimeWithServer : syncDataRealTimeWithServerDB.getAllDataToSyncWithServer()){
                    totalData.put(new JSONObject()
                            .put("1", String.valueOf(syncDataRealTimeWithServer.getId()))
                            .put("2", syncDataRealTimeWithServer.getSelection())
                            .put("3", syncDataRealTimeWithServer.getSelectionArgs())
                            .put("4", syncDataRealTimeWithServer.getColumnCount()));
                }
                if (totalData.length()>0) {
                    /**
                     * el manejo del OrderDB en este servicio es un parche en el cual se actualiza
                     * el sequenceId de todos los pedidos a 1 asumiendo que si este proceso termina
                     * exitosamente entonces se enviaron todos los pedidos que estaban pendientes por
                     * enviar. Esto es necesario para el atributo orderWasDelivery de la clase order
                     * que indica que el pedido ya fue transmitido.
                     */
                    OrderDB orderDB = new OrderDB(getApplicationContext(), user);
                    //maximo pedido creado hasta antes de correr el proceso de envio de datos
                    int maxOrderId = orderDB.getMaxOrderIdNotSentToServer();
                    syncDataRealTimeWithServerDB.deleteDataToSyncWithServer(DataBaseUtilities.unGzip(Base64.decode(sendDataToServer(user,
                            Base64.encodeBytes(DataBaseUtilities.gzip(totalData.toString()), Base64.GZIP)), Base64.GZIP)));
                    if (maxOrderId>0) {
                        //actualizacion del sequenceId a uno de todos los pedidos con id menor igual a maxOrderId y sequenceId igual a cero
                        orderDB.markAsSentToServer(maxOrderId);
                    }
                    //se envia un broadcast notificando que acaba de finalizar una sincronizacion de los datos
                    getApplicationContext().sendBroadcast(new Intent(SYNCHRONIZATION_FINISHED));
                }
            } else {
                throw new Exception("user is null.");
            }
        } catch (Exception e) {
            if (orderWillBeSend) {
                // create a handler to post messages to the main thread
                (new Handler(getMainLooper())).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toast.makeText(SyncDataRealTimeWithServerService.this,
                                    R.string.error_sending_order, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            //do nothing
                        }
                    }
                });
            }
            //e.printStackTrace();
        }
    }

    private String sendDataToServer(User user, String data) throws Exception {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("authToken", user.getAuthToken());
        parameters.put("userGroupName", user.getUserGroup());
        parameters.put("userId", user.getServerUserId());
        parameters.put("syncSessionId", user.getServerSyncSessionId());
        parameters.put("appVersionCode", Utils.getAppVersionCode(getApplicationContext()));
        parameters.put("data", data);
        ConsumeWebService a = new ConsumeWebService(getApplicationContext(),
                user.getServerAddress(),
                "/IntelligentDataSynchronizer/services/ManageDBDataTransfer?wsdl",
                "syncDataRealTimeFromClient",
                "urn:syncDataRealTimeFromClient",
                parameters,
                4000, 0);
        Object response = a.getWSResponse();
        if (response instanceof SoapPrimitive) {
            return response.toString();
        } else if (response != null) {
            throw new ClassCastException("response classCastException.");
        } else {
            throw new NullPointerException("response is null.");
        }
    }
}
