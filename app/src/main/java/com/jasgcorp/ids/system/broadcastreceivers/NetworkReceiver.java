package com.jasgcorp.ids.system.broadcastreceivers;

import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.jasgcorp.ids.utils.ApplicationUtilities;
import com.jasgcorp.ids.utils.NetworkConnectionUtilities;
import com.smartbuilders.smartsales.ecommerceandroidapp.R;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

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
            Toast.makeText(context, "Catálogo: Conexion Wi-Fi enabled. Start Synchronization.", Toast.LENGTH_SHORT).show();
            try {
                Bundle settingsBundle = new Bundle();
                settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                Account[] accounts = AccountManager.get(context)
                        .getAccountsByType(context.getString(R.string.authenticator_acount_type));
                for(Account account : accounts){
                    if(!ApplicationUtilities.isSyncActive(account, DataBaseContentProvider.AUTHORITY)){
                        ContentResolver.requestSync(account, DataBaseContentProvider.AUTHORITY, settingsBundle);
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
