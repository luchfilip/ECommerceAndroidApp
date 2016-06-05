package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.ShoppingSaleAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.businessRules.SalesOrderLineBR;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.SalesOrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.SalesOrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

/**
 * Created by stein on 26/5/2016.
 */
public class DialogUpdateSalesOrderLine extends DialogFragment {

    private static final String STATE_CURRENT_ORDERLINE = "STATE_CURRENT_ORDERLINE";

    private SalesOrderLine mSaleOrderLine;
    private User mUser;
    private int mFocus;

    public static DialogUpdateSalesOrderLine newInstance(SalesOrderLine orderLine, User user, int focus){
        DialogUpdateSalesOrderLine dialogUpdateSalesOrderLine = new DialogUpdateSalesOrderLine();
        dialogUpdateSalesOrderLine.mUser = user;
        dialogUpdateSalesOrderLine.mSaleOrderLine = orderLine;
        dialogUpdateSalesOrderLine.mFocus = focus;
        return dialogUpdateSalesOrderLine;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_update_product_to_sale, container);

        mUser = Utils.getCurrentUser(getContext());

        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(STATE_CURRENT_ORDERLINE)){
                mSaleOrderLine = savedInstanceState.getParcelable(STATE_CURRENT_ORDERLINE);
            }
        }

        ((TextView) view.findViewById(R.id.product_availability_textView))
                .setText(getContext().getString(R.string.availability, mSaleOrderLine.getProduct().getAvailability()));

        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        try {
            ((EditText) view.findViewById(R.id.qty_requested_editText))
                    .setText(String.valueOf(mSaleOrderLine.getQuantityOrdered()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ((EditText) view.findViewById(R.id.product_price_editText))
                    .setText(String.valueOf(mSaleOrderLine.getPrice()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ((EditText) view.findViewById(R.id.product_tax_editText))
                    .setText(String.valueOf(mSaleOrderLine.getTaxPercentage()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        view.findViewById(R.id.accept_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    try {
                        mSaleOrderLine.setQuantityOrdered(Integer.valueOf(((EditText) view
                                .findViewById(R.id.qty_requested_editText)).getText().toString()));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        mSaleOrderLine.setQuantityOrdered(0);
                    }
                    try {
                        mSaleOrderLine.setPrice(Double.valueOf(((EditText) view
                                .findViewById(R.id.product_price_editText)).getText().toString()));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        mSaleOrderLine.setPrice(0);
                    }
                    try {
                        mSaleOrderLine.setTaxPercentage(Double.valueOf(((EditText) view
                                .findViewById(R.id.product_tax_editText)).getText().toString()));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        mSaleOrderLine.setTaxPercentage(0);
                    }
                    mSaleOrderLine.setTotalLineAmount(SalesOrderLineBR.getTotalLine(mSaleOrderLine));
                    String result = (new SalesOrderLineDB(getContext(), mUser)).updateSalesOrderLine(mSaleOrderLine);
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

        if(view.findViewById(R.id.product_commercial_package) != null){
            if(mSaleOrderLine.getProduct()!=null && mSaleOrderLine.getProduct().getProductCommercialPackage()!=null
                    && !TextUtils.isEmpty(mSaleOrderLine.getProduct().getProductCommercialPackage().getUnitDescription())){
                ((TextView) view.findViewById(R.id.product_commercial_package)).setText(getContext().getString(R.string.commercial_package,
                        mSaleOrderLine.getProduct().getProductCommercialPackage().getUnits() + " " +
                                mSaleOrderLine.getProduct().getProductCommercialPackage().getUnitDescription()));
            }else{
                view.findViewById(R.id.product_commercial_package).setVisibility(TextView.GONE);
            }
        }

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

        getDialog().setTitle(mSaleOrderLine.getProduct().getName());
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_ORDERLINE, mSaleOrderLine);
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
