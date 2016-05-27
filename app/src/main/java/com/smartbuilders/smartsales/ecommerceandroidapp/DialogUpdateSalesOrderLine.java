package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.ShoppingSaleAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.businessRules.OrderLineBR;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;

/**
 * Created by stein on 26/5/2016.
 */
public class DialogUpdateSalesOrderLine extends DialogFragment {

    private static final String STATE_CURRENT_ORDERLINE = "STATE_CURRENT_ORDERLINE";
    private static final String STATE_CURRENT_USER = "STATE_CURRENT_USER";

    private OrderLine mOrderLine;
    private User mUser;
    private int mFocus;

    public static DialogUpdateSalesOrderLine newInstance(OrderLine orderLine, User user, int focus){
        DialogUpdateSalesOrderLine dialogUpdateSalesOrderLine = new DialogUpdateSalesOrderLine();
        dialogUpdateSalesOrderLine.mUser = user;
        dialogUpdateSalesOrderLine.mOrderLine = orderLine;
        dialogUpdateSalesOrderLine.mFocus = focus;
        return dialogUpdateSalesOrderLine;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_update_product_to_sale, container);

        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(STATE_CURRENT_ORDERLINE)){
                mOrderLine = savedInstanceState.getParcelable(STATE_CURRENT_ORDERLINE);
            }
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        ((TextView) view.findViewById(R.id.product_availability_textView))
                .setText(getContext().getString(R.string.availability, mOrderLine.getProduct().getAvailability()));

        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        try {
            ((EditText) view.findViewById(R.id.qty_requested_editText))
                    .setText(String.valueOf(mOrderLine.getQuantityOrdered()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ((EditText) view.findViewById(R.id.product_price_editText))
                    .setText(String.valueOf(mOrderLine.getPrice()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ((EditText) view.findViewById(R.id.product_tax_editText))
                    .setText(String.valueOf(mOrderLine.getTaxPercentage()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        view.findViewById(R.id.accept_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    try {
                        mOrderLine.setQuantityOrdered(Integer.valueOf(((EditText) view
                                .findViewById(R.id.qty_requested_editText)).getText().toString()));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        mOrderLine.setQuantityOrdered(0);
                    }
                    try {
                        mOrderLine.setPrice(Double.valueOf(((EditText) view
                                .findViewById(R.id.product_price_editText)).getText().toString()));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        mOrderLine.setPrice(0);
                    }
                    try {
                        mOrderLine.setTaxPercentage(Double.valueOf(((EditText) view
                                .findViewById(R.id.product_tax_editText)).getText().toString()));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        mOrderLine.setTaxPercentage(0);
                    }
                    mOrderLine.setTotalLineAmount(OrderLineBR.getTotalLine(mOrderLine));
                    String result = (new OrderLineDB(getContext(), mUser)).updateOrderLine(mOrderLine);
                    if(result == null){
                        ((ShoppingSaleFragment) getTargetFragment()).reloadShoppingSale();
                    } else {
                        throw new Exception(result);
                    }
                    dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        switch (mFocus) {
            case ShoppingSaleAdapter.FOCUS_PRICE:
                view.findViewById(R.id.product_price_editText).requestFocus();
                break;
            case ShoppingSaleAdapter.FOCUS_TAX_PERCENTAGE:
                view.findViewById(R.id.product_tax_editText).requestFocus();
                break;
            case ShoppingSaleAdapter.FOCUS_QTY_ORDERED:
                view.findViewById(R.id.qty_requested_editText).requestFocus();
                break;
        }

        getDialog().setTitle(mOrderLine.getProduct().getName());
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mUser);
        outState.putParcelable(STATE_CURRENT_ORDERLINE, mOrderLine);
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
