package com.smartbuilders.smartsales.ecommerceandroidapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.ImageView;

import com.jasgcorp.ids.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by stein on 22/5/2016.
 */
public class GetFileFromServlet extends AsyncTask<Void, Void, Bitmap> {

    private String url;
    private ImageView mImageView;
    private String mFileName;
    private Context mContext;
    private User mUser;
    private boolean mIsThumb;

    public GetFileFromServlet(String fileName, boolean isThumb, ImageView imageView, User user, Context context) {
        if (isThumb) {
            url = user.getServerAddress() + "/IntelligentDataSynchronizer/GetThumbImage?fileName=" + fileName;
        } else {
            url = user.getServerAddress() + "/IntelligentDataSynchronizer/GetOriginalImage?fileName=" + fileName;
        }
        mImageView = imageView;
        mFileName = fileName;
        mContext = context;
        mUser = user;
        mIsThumb = isThumb;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(mImageView!=null && bitmap!=null){
            mImageView.setImageBitmap(bitmap);
        }
        if(bitmap!=null && mContext!=null && mUser!=null) {
            if(mIsThumb) {
                Utils.createFileInThumbDir(mFileName, bitmap, mUser, mContext);
            } else {
                Utils.createFileInOriginalDir(mFileName, bitmap, mUser, mContext);
            }
        }
    }

    @Override
    protected Bitmap doInBackground(Void... urls) {
        return downloadImage(url);
    }

    // Creates Bitmap from InputStream and returns it
    private Bitmap downloadImage(String url) {
        if(TextUtils.isEmpty(mFileName)){
            return null;
        }
        Bitmap bitmap = null;
        InputStream stream = null;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;

        try {
            stream = getHttpConnection(url);
            bitmap = BitmapFactory.
                    decodeStream(stream, null, bmOptions);
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if(stream!=null){
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    // Makes HttpURLConnection and returns InputStream
    private InputStream getHttpConnection(String urlString)
            throws IOException {
        InputStream stream = null;
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setConnectTimeout(2000);
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = httpConnection.getInputStream();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream;
    }
}