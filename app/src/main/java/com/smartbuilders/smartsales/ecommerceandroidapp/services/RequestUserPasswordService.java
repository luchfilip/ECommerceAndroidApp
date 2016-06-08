package com.smartbuilders.smartsales.ecommerceandroidapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.jasgcorp.ids.utils.ConsumeWebService;
import com.smartbuilders.smartsales.ecommerceandroidapp.RequestUserPasswordFragment;

import org.ksoap2.serialization.SoapPrimitive;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.LinkedHashMap;

/**
 * Created by stein on 7/6/2016.
 */
public class RequestUserPasswordService extends IntentService {

    private static final String TAG = RequestUserPasswordService.class.getSimpleName();

    public static final String SERVER_ADDRESS = "SERVER_ADDRESS";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_EMAIL = "USER_EMAIL";


    public RequestUserPasswordService() {
        super(RequestUserPasswordService.class.getSimpleName());
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RequestUserPasswordService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        String serverAddress = workIntent.getStringExtra(SERVER_ADDRESS);
        String userName = workIntent.getStringExtra(USER_NAME);
        String userEmail = workIntent.getStringExtra(USER_EMAIL);
        String resultMsg = null;
        try {
            LinkedHashMap<String, Object> parameters = new LinkedHashMap<String, Object>();
            parameters.put("userGroup", "catalogo-febeca");
            parameters.put("userName", userName);
            parameters.put("userEmail", userEmail);
            ConsumeWebService a = new ConsumeWebService(getApplicationContext(),
                                                        serverAddress,
                                                        "/IntelligentDataSynchronizer/services/ManageUser?wsdl",
                                                        "requestUserPassword",
                                                        "urn:requestUserPassword",
                                                        parameters);
            Object response =  a.getWSResponse();
            if(response instanceof SoapPrimitive){
                Log.d(TAG, "response: "+response.toString());
                //TODO: enviar broadcast a la aplicacion
                resultMsg = response.toString();
            }else if (response != null){
                throw new ClassCastException("response classCastException.");
            }else {
                throw new NullPointerException("response is null.");
            }
        } catch(ConnectException e){
            resultMsg = e.getMessage();
            e.printStackTrace();
        } catch(SocketTimeoutException e){
            resultMsg = e.getMessage();
            e.printStackTrace();
        } catch(SocketException e){
            resultMsg = e.getMessage();
            e.printStackTrace();
        } catch (MalformedURLException e) {
            resultMsg = e.getMessage();
            e.printStackTrace();
        } catch(IOException e){
            resultMsg = e.getMessage();
            e.printStackTrace();
        } catch (Exception e) {
            resultMsg = e.getMessage();
            e.printStackTrace();
        }
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(RequestUserPasswordFragment.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(RequestUserPasswordFragment.MESSAGE, resultMsg);
        sendBroadcast(broadcastIntent);
    }
}