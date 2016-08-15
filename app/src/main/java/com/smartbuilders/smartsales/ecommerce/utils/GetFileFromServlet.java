package com.smartbuilders.smartsales.ecommerce.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.smartbuilders.synchronizer.ids.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by stein on 22/5/2016.
 */
public class GetFileFromServlet extends AsyncTask<Void, Void, Bitmap> {

    private static final String TAG = GetFileFromServlet.class.getSimpleName();

    private String url;
    private String mFileName;
    private Context mContext;
    private User mUser;
    private boolean mIsThumb;

    public GetFileFromServlet(String fileName, boolean isThumb, User user, Context context) {
        url = user.getServerAddress() + "/IntelligentDataSynchronizer/"
                + (isThumb ? "GetThumbImage" : "GetOriginalImage")
                + "?fileName=" + fileName;
        mFileName = fileName;
        mContext = context;
        mUser = user;
        mIsThumb = isThumb;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap!=null && mContext!=null && mUser!=null) {
            if(mIsThumb) {
                Utils.createFileInThumbDir(mFileName, bitmap, mContext);
            } else {
                Utils.createFileInOriginalDir(mFileName, bitmap, mContext);
            }
        }
    }

    @Override
    protected Bitmap doInBackground(Void ... voids) {
        if(TextUtils.isEmpty(mFileName)){
            return null;
        }
        if(mIsThumb){
            if(Utils.getFileInThumbDirByFileName(mContext, mFileName)!=null){
                return null;
            }
        }else{
            if(Utils.getFileInOriginalDirByFileName(mContext, mFileName)!=null){
                return null;
            }
        }
        return downloadImage(url);
    }

    // Creates Bitmap from InputStream and returns it
    private Bitmap downloadImage(String url) {
        if(TextUtils.isEmpty(mFileName)){
            return null;
        }
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;
        try {
            return BitmapFactory.decodeStream(getHttpConnection(url), null, bmOptions);
        } catch (IOException | NullPointerException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    // Makes HttpURLConnection and returns InputStream
    private InputStream getHttpConnection(String urlString)
            throws IOException {
        InputStream stream = null;
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setConnectTimeout(1200);
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();
            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = httpConnection.getInputStream();
            } else {
                Log.w(TAG, "httpConnection.getResponseCode(): " + httpConnection.getResponseCode());
            }
        } catch (SocketTimeoutException e) {
            Log.w(TAG, "SocketTimeoutException, " + e.getMessage());
        } catch (SocketException e) {
            Log.w(TAG, "SocketException, " + e.getMessage());
        } catch(MalformedURLException e){
            Log.e(TAG, "MalformedURLException, " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException, " + e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream;
    }
}