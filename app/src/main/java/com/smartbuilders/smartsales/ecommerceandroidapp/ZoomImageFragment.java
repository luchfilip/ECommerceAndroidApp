package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;
import com.smartbuilders.smartsales.ecommerceandroidapp.view.TouchImageView;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;

/**
 * Created by stein on 27/5/2016.
 */
public class ZoomImageFragment extends Fragment {

    private static final String STATE_IMAGE_FILE_NAME = "state_image_file_name";

    private User mCurrentUser;
    private String mImageFileName;

    public ZoomImageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mCurrentUser = Utils.getCurrentUser(getContext());

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(STATE_IMAGE_FILE_NAME)){
                mImageFileName = savedInstanceState.getString(STATE_IMAGE_FILE_NAME);
            }
        }

        if(getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null) {
            if(getActivity().getIntent().getExtras().containsKey(ZoomImageActivity.KEY_IMAGE_FILE_NAME)){
                mImageFileName = getActivity().getIntent().getExtras()
                        .getString(ZoomImageActivity.KEY_IMAGE_FILE_NAME);
            }
        }

        View view;
        Bitmap img = Utils.getImageFromOriginalDirByFileName(getContext(), mCurrentUser, mImageFileName);
        if (img != null) {
            TouchImageView touchImageView = new TouchImageView(getContext());
            touchImageView.setImageBitmap(img);
            touchImageView.setMaxZoom(4f);
            view = touchImageView;
            view.setBackgroundColor(Color.WHITE);
        } else {
            img = Utils.getImageFromThumbDirByFileName(getContext(), mCurrentUser, mImageFileName);
            if (img != null) {
                TouchImageView touchImageView = new TouchImageView(getContext());
                touchImageView.setImageBitmap(img);
                touchImageView.setMaxZoom(4f);
                view = touchImageView;
                view.setBackgroundColor(Color.WHITE);
            }else{
                view = inflater.inflate(R.layout.fragment_zoom_image, container, false);
            }
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_IMAGE_FILE_NAME, mImageFileName);
        super.onSaveInstanceState(outState);
    }
}
