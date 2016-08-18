package com.smartbuilders.smartsales.ecommerce.services;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stein on 3/7/2016.
 */
public class LoadProductsThumbImage extends IntentService {

    private static final String TAG = LoadProductsThumbImage.class.getSimpleName();

    public LoadProductsThumbImage() {
        super(LoadProductsThumbImage.class.getSimpleName());
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public LoadProductsThumbImage(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        getProductsThumbImageFromServer(getApplicationContext(), Utils.getCurrentUser(getApplicationContext()));
    }

    private void getProductsThumbImageFromServer(Context context, User user){
        Cursor c = null;
        try {
            List<String> filesName = new ArrayList<>();
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(), null,
                    "SELECT PI.FILE_NAME FROM PRODUCT_IMAGE PI " +
                        " INNER JOIN PRODUCT P ON P.PRODUCT_ID = PI.PRODUCT_ID AND P.IS_ACTIVE = 'Y' " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = 'Y' " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = 'Y' " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = 'Y' " +
                        " INNER JOIN PRODUCT_PRICE_AVAILABILITY PPA ON PPA.PRODUCT_ID = P.PRODUCT_ID AND PPA.IS_ACTIVE = 'Y' " +
                    " WHERE PI.IS_ACTIVE='Y' AND PI.PRIORITY=1",
                    null, null);
            if(c!=null){
                ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                while(c.moveToNext()){
                    filesName.add(c.getString(0));
                    if(Utils.getFileInThumbDirByFileName(context, c.getString(0))==null){
                        if(isServiceRunning(activityManager)){
                            downloadImage(c.getString(0), context, user);
                        }else{
                            break;
                        }
                    }
                }
            }
            //se limpia la carpeta de los archivos que ya no pertenezcan
            List<String> filesInThumbDir = Utils.getListOfFilesInThumbDir(context);
            if(filesInThumbDir!=null && !filesInThumbDir.isEmpty()){
                filesInThumbDir.removeAll(filesName);
                for (String fileNameToRemove : filesInThumbDir) {
                    try {
                        (new File (Utils.getImagesThumbFolderPath(context), fileNameToRemove)).delete();
                    } catch (Exception e) {
                        Log.e(TAG, "Error removing file: \""+String.valueOf(fileNameToRemove)+
                                "\", ExceptionMessage: "+e.getMessage());
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isServiceRunning(ActivityManager activityManager){
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (LoadProductsThumbImage.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    // Creates Bitmap from InputStream and returns it
    private void downloadImage(String fileName, Context context, User user) throws IOException {
        try {
            OutputStream outputStream = null;
            InputStream inputStream = null;
            try {
                inputStream = getHttpConnection(user.getServerAddress() +
                        "/IntelligentDataSynchronizer/GetThumbImage?fileName=" + fileName);
                // write the inputStream to a FileOutputStream
                try{
                    outputStream = new FileOutputStream(new File(Utils.getImagesThumbFolderPath(context), fileName));
                } catch (FileNotFoundException e){
                    (new File(Utils.getImagesThumbFolderPath(context))).mkdirs();
                    outputStream = new FileOutputStream(new File(Utils.getImagesThumbFolderPath(context), fileName));
                }
                int read = 0;
                byte[] bytes = new byte[1024];
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            } catch (IOException e) {
                throw e;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e){
            throw e;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Makes HttpURLConnection and returns InputStream
    private InputStream getHttpConnection(String urlString) throws Exception {
        try {
            HttpURLConnection httpConnection = (HttpURLConnection) (new URL(urlString)).openConnection();
            httpConnection.setConnectTimeout(2000);
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();
            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return httpConnection.getInputStream();
            } else {
                throw new Exception("httpConnection.getResponseCode(): " + httpConnection.getResponseCode());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
}
