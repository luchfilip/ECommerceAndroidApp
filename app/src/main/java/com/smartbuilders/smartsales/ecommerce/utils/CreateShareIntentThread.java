package com.smartbuilders.smartsales.ecommerce.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;

import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.synchronizer.ids.model.User;

/**
 * Jesus Sarco, 19/9/2016.
 */
public class CreateShareIntentThread extends Thread {

    private Activity mActivity;
    private Context mContext;
    private User mUser;
    private Product mProduct;
    private ImageView mShareProductImageView;

    public CreateShareIntentThread(Activity activity, Context context, User user, Product product, ImageView shareProductImageView) {
        mActivity = activity;
        mContext = context;
        mUser = user;
        mProduct = product;
        mShareProductImageView = shareProductImageView;
    }

    public void run() {
        final Intent shareIntent = Intent.createChooser(Utils.createShareProductIntentFromView(mActivity,
                mContext, mUser, mProduct), mContext.getString(R.string.share_image));
        if(mActivity!=null){
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContext.startActivity(shareIntent);
                    if (mShareProductImageView!=null) {
                        mShareProductImageView.setEnabled(true);
                    }
                }
            });
        }
    }
}