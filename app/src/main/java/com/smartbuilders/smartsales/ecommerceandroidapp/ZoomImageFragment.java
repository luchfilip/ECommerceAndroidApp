package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;
import com.smartbuilders.smartsales.ecommerceandroidapp.view.TouchImageView;

/**
 * Created by stein on 27/5/2016.
 */
public class ZoomImageFragment extends Fragment {

    private static final String STATE_CURRENT_USER = "state_current_user";
    private static final String STATE_IMAGE_FILE_NAME = "state_image_file_name";

    private User mCurrentUser;
    private String mImageFileName;


    public ZoomImageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
            if(savedInstanceState.containsKey(STATE_IMAGE_FILE_NAME)){
                mImageFileName = savedInstanceState.getString(STATE_IMAGE_FILE_NAME);
            }
        }

        if(getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null) {
            if(getActivity().getIntent().getExtras().containsKey(ZoomImageActivity.KEY_CURRENT_USER)){
                mCurrentUser = getActivity().getIntent().getExtras().getParcelable(ZoomImageActivity.KEY_CURRENT_USER);
            }
            if(getActivity().getIntent().getExtras().containsKey(ZoomImageActivity.KEY_IMAGE_FILE_NAME)){
                mImageFileName = getActivity().getIntent().getExtras().getString(ZoomImageActivity.KEY_IMAGE_FILE_NAME);
            }
        }


        View view;

        Bitmap img = Utils.getImageByFileName(getContext(), mCurrentUser, mImageFileName);
        if (img != null) {
            TouchImageView touchImageView = new TouchImageView(getContext());
            touchImageView.setImageBitmap(img);
            touchImageView.setMaxZoom(4f);
            view = touchImageView;
        } else {
            img = Utils.getThumbByFileName(getContext(), mCurrentUser, mImageFileName);
            if (img != null) {
                TouchImageView touchImageView = new TouchImageView(getContext());
                touchImageView.setImageBitmap(img);
                touchImageView.setMaxZoom(4f);
                view = touchImageView;
            }else{
                view = inflater.inflate(R.layout.fragment_zoom_image, container, false);
            }
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
        outState.putString(STATE_IMAGE_FILE_NAME, mImageFileName);
        super.onSaveInstanceState(outState);
    }
}
