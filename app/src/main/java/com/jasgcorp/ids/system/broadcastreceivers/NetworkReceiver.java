package com.jasgcorp.ids.system.broadcastreceivers;

import com.jasgcorp.ids.utils.ApplicationUtilities;
import com.jasgcorp.ids.utils.NetworkConnectionUtilities;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
        if(networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
            try {
                Account[] accounts = AccountManager.get(context)
                        .getAccountsByType(context.getString(R.string.authenticator_account_type));
                for(Account account : accounts){
                    if(!ApplicationUtilities.isSyncActive(context, account)){
                        ApplicationUtilities.initSyncByAccount(context, account);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            if(NetworkConnectionUtilities.isOnline(context)){

            }
        }
    }

}
