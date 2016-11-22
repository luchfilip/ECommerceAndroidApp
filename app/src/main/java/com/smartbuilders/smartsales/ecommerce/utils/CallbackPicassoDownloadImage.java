package com.smartbuilders.smartsales.ecommerce.utils;

import android.content.Context;
import android.util.Log;

import com.smartbuilders.synchronizer.ids.model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by stein on 18/6/2016.
 */
public class CallbackPicassoDownloadImage implements com.squareup.picasso.Callback {

    private String mFileName;
    private Context mContext;
    private User mUser;
    private boolean mIsThumb;

    public CallbackPicassoDownloadImage(String fileName, boolean isThumb, User user, Context context) {
        mFileName = fileName;
        mContext = context;
        mUser = user;
        mIsThumb = isThumb;
    }

    @Override
    public void onSuccess() {
        new DownloadAndCreateImage().start();
    }

    @Override
    public void onError() {

    }

    private class DownloadAndCreateImage extends Thread {

        public void run() {
            downloadImage(mFileName, mContext, mUser, mIsThumb);
        }

        // Creates Bitmap from InputStream and returns it
        private void downloadImage(String fileName, Context context, User user, boolean isThumb) {
            try {
                OutputStream outputStream = null;
                InputStream inputStream = null;
                try {
                    inputStream = getHttpConnection(user.getServerAddress() + "/IntelligentDataSynchronizer/"
                            + (isThumb ? "GetThumbImage" : "GetOriginalImage") + "?fileName=" + fileName);
                    // write the inputStream to a FileOutputStream
                    outputStream = new FileOutputStream(new File(isThumb ? Utils.getImagesThumbFolderPath(context)
                            : Utils.getImagesOriginalFolderPath(context), fileName));
                    int read = 0;
                    byte[] bytes = new byte[1024];
                    while ((read = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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
            } catch (SocketTimeoutException e) {
                Log.w("DownloadAndCreateImage", "SocketTimeoutException, " + e.getMessage());
            } catch (SocketException e) {
                Log.w("DownloadAndCreateImage", "SocketException, " + e.getMessage());
            } catch(MalformedURLException e){
                Log.e("DownloadAndCreateImage", "MalformedURLException, " + e.getMessage());
            } catch (IOException e) {
                Log.e("DownloadAndCreateImage", "IOException, " + e.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString) throws Exception {
            try {
                HttpURLConnection httpConnection = (HttpURLConnection) (new URL(urlString)).openConnection();
                httpConnection.setConnectTimeout(1200);
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
}