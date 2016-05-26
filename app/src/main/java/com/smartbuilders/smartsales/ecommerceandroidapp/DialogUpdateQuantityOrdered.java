package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;

/**
 * Created by stein on 26/5/2016.
 */
public class DialogUpdateQuantityOrdered extends DialogFragment {

    private static final String STATE_CURRENT_ORDERLINE = "STATE_CURRENT_ORDERLINE";
    private static final String STATE_CURRENT_USER = "STATE_CURRENT_USER";

    private OrderLine mOrderLine;
    private User mUser;

    public static DialogUpdateQuantityOrdered newInstance(OrderLine orderLine, User user){
        DialogUpdateQuantityOrdered dialogUpdateQuantityOrdered = new DialogUpdateQuantityOrdered();
        dialogUpdateQuantityOrdered.mUser = user;
        dialogUpdateQuantityOrdered.mOrderLine = orderLine;
        return dialogUpdateQuantityOrdered;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(STATE_CURRENT_ORDERLINE)){
                mOrderLine = savedInstanceState.getParcelable(STATE_CURRENT_ORDERLINE);
            }
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        final View view = inflater.inflate(R.layout.dialog_update_qty_ordered, container);

        getDialog().setTitle("");
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDismiss(dialog);
    }
}
