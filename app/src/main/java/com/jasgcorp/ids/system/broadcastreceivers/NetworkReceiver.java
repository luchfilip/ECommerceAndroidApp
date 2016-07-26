package com.jasgcorp.ids.system.broadcastreceivers;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.syncadapter.model.AccountGeneral;
import com.jasgcorp.ids.utils.ApplicationUtilities;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.services.LoadProductsThumbImage;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import java.util.Date;

/**
 * Recibe una señal del sistema cuando cambia la conectividad del equipo
 * @author Jesus Sarco
 *
 */
public class NetworkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conn =  (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        if(networkInfo != null){
            try {
                AccountManager accountManager = AccountManager.get(context);
                Account[] accounts = accountManager.getAccountsByType(context.getString(R.string.authenticator_account_type));
                for(Account account : accounts){
                    if(!ApplicationUtilities.isSyncActive(context, account)){
                        try {
                            //Se verifica que la ultima sincronizacion se haya realizado en un tiempo
                            // mayor o igual al periodo de sincronizacion definido.
                            User user = new User(accountManager.getUserData(account, AccountGeneral.USERDATA_USER_ID));
                            Date lastSuccessFullySyncTime = ApplicationUtilities.getLastSuccessfullySyncTime(context, user);
                            if(lastSuccessFullySyncTime!=null){
                                long seconds = (System.currentTimeMillis() - lastSuccessFullySyncTime.getTime())/1000;
                                if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                                    if(seconds >= Parameter.getSyncPeriodicityInSeconds(context, user)) {
                                        ApplicationUtilities.initSyncByAccount(context, account);
                                    }else{
                                        ApplicationUtilities.initSyncDataWithServerService(context, user.getUserId());
                                    }
                                }else{
                                    ApplicationUtilities.initSyncDataWithServerService(context, user.getUserId());
                                }
                            }else{
                                ApplicationUtilities.initSyncByAccount(context, account);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sync_thumb_images", false)
                        && !Utils.isServiceRunning(context, LoadProductsThumbImage.class)){
                    context.startService(new Intent(context, LoadProductsThumbImage.class));
                }
            }
        }
    }

}
