package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;

/**
 * Created by stein on 5/1/2016.
 */
public class DialogCreateBusinessPartner extends DialogFragment {

    private static final String STATE_CURRENT_USER = "STATE_CURRENT_USER";

    private User mUser;

    public DialogCreateBusinessPartner() {
        // Empty constructor required for DialogFragment
    }

    public static DialogCreateBusinessPartner newInstance(User user){
        DialogCreateBusinessPartner editQtyRequestedDialogFragment = new DialogCreateBusinessPartner();
        editQtyRequestedDialogFragment.mUser = user;
        return editQtyRequestedDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        final View view = inflater.inflate(R.layout.fragment_register_business_partner, container);
        

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mUser);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        try {
            if(getActivity()!=null && getActivity().getWindow()!=null){
                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                ((InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDismiss(dialog);
    }
}
