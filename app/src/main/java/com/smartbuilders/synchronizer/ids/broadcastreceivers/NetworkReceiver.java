package com.smartbuilders.synchronizer.ids.broadcastreceivers;

import com.smartbuilders.smartsales.ecommerce.BuildConfig;
import com.smartbuilders.synchronizer.ids.syncadapter.model.AccountGeneral;
import com.smartbuilders.synchronizer.ids.utils.ApplicationUtilities;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.services.LoadProductsThumbImage;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

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
                Account[] accounts = accountManager.getAccountsByType(BuildConfig.AUTHENTICATOR_ACCOUNT_TYPE);
                for(Account account : accounts){
                    if(!ApplicationUtilities.isSyncActive(account, BuildConfig.SYNC_ADAPTER_CONTENT_AUTHORITY)){
                        try {
                            if(!ApplicationUtilities.appRequireInitialLoad(context, account)){
                                if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                                    if(ApplicationUtilities.appRequireFullSync(context, account)) {
                                        ApplicationUtilities.initSyncByAccount(context, account);
                                    }else{
                                        ApplicationUtilities.initSyncDataWithServerService(context,
                                                accountManager.getUserData(account, AccountGeneral.USERDATA_USER_ID));
                                    }
                                }else{
                                    ApplicationUtilities.initSyncDataWithServerService(context,
                                            accountManager.getUserData(account, AccountGeneral.USERDATA_USER_ID));
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

            //if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sync_thumb_images", false)
                        && !Utils.isServiceRunning(context, LoadProductsThumbImage.class)){
                    context.startService(new Intent(context, LoadProductsThumbImage.class));
                }
            //}
        }
    }

}
