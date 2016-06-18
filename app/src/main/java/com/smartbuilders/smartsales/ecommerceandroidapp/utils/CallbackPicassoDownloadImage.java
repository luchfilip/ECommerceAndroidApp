package com.smartbuilders.smartsales.ecommerceandroidapp.utils;

import android.content.Context;

import com.jasgcorp.ids.model.User;

import java.util.concurrent.ExecutionException;

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
        //Si el archivo no existe entonces se descarga
        GetFileFromServlet getFileFromServlet =
                new GetFileFromServlet(mFileName, mIsThumb, mUser, mContext);
        try {
            if(mIsThumb) {
                Utils.createFileInThumbDir(mFileName,
                        getFileFromServlet.execute().get(),
                        mUser, mContext);
            }else{
                Utils.createFileInOriginalDir(mFileName,
                        getFileFromServlet.execute().get(),
                        mUser, mContext);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError() {

    }
}