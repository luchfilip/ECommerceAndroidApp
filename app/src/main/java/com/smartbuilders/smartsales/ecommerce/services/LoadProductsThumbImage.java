package com.smartbuilders.smartsales.ecommerce.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by stein on 3/7/2016.
 */
public class LoadProductsThumbImage extends IntentService {

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
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                    "SELECT FILE_NAME FROM PRODUCT_IMAGE WHERE IS_ACTIVE='Y' AND PRIORITY=1",
                    null, null);
            if(c!=null){
                while(c.moveToNext()){
                    if(Utils.getFileInThumbDirByFileName(context, c.getString(0))==null){
                        downloadImage(c.getString(0), context, user);
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
