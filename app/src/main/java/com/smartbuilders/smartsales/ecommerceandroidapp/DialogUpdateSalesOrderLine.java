package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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

    private static final String STATE_ORDERLINE = "STATE_ORDERLINE";
    private static final String STATE_USER = "STATE_USER";
    private static final String STATE_FOCUS = "STATE_FOCUS";

    private SalesOrderLine mSaleOrderLine;
    private User mUser;
    private int mFocus;

    public static DialogUpdateSalesOrderLine newInstance(SalesOrderLine orderLine, User user, int focus){
        DialogUpdateSalesOrderLine dialogUpdateSalesOrderLine = new DialogUpdateSalesOrderLine();
        dialogUpdateSalesOrderLine.mSaleOrderLine = orderLine;
        dialogUpdateSalesOrderLine.mUser = user;
        dialogUpdateSalesOrderLine.mFocus = focus;
        return dialogUpdateSalesOrderLine;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_update_product_to_sale, container);

        mUser = Utils.getCurrentUser(getContext());

        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(STATE_ORDERLINE)){
                mSaleOrderLine = savedInstanceState.getParcelable(STATE_ORDERLINE);
            }
            if(savedInstanceState.containsKey(STATE_USER)){
                mUser = savedInstanceState.getParcelable(STATE_USER);
            }
            if(savedInstanceState.containsKey(STATE_FOCUS)){
                mFocus = savedInstanceState.getInt(STATE_FOCUS);
            }
        }

        final EditText productPriceEditText = (EditText) view.findViewById(R.id.product_price_editText);
        final EditText productTaxEditText = (EditText) view.findViewById(R.id.product_tax_editText);
        final EditText qtyRequestedEditText = (EditText) view.findViewById(R.id.qty_requested_editText);

        ((TextView) view.findViewById(R.id.product_availability_textView))
                .setText(getContext().getString(R.string.availability, mSaleOrderLine.getProduct().getAvailability()));

        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        try {
            productPriceEditText.setText(String.valueOf(mSaleOrderLine.getPrice()));
            productPriceEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFocus = ShoppingSaleAdapter.FOCUS_PRICE;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            productTaxEditText.setText(String.valueOf(mSaleOrderLine.getTaxPercentage()));
            productTaxEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFocus = ShoppingSaleAdapter.FOCUS_TAX_PERCENTAGE;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            qtyRequestedEditText.setText(String.valueOf(mSaleOrderLine.getQuantityOrdered()));
            qtyRequestedEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFocus = ShoppingSaleAdapter.FOCUS_QTY_ORDERED;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        view.findViewById(R.id.accept_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    try {
                        mSaleOrderLine.setQuantityOrdered(Integer.valueOf(qtyRequestedEditText.getText().toString()));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        mSaleOrderLine.setQuantityOrdered(0);
                    }
                    if (mSaleOrderLine.getQuantityOrdered()<=0) {
                        throw new Exception(getString(R.string.invalid_qty_requested));
                    }
                    try {
                        mSaleOrderLine.setPrice(Double.valueOf(productPriceEditText.getText().toString()));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        mSaleOrderLine.setPrice(0);
                    }
                    try {
                        mSaleOrderLine.setTaxPercentage(Double.valueOf(productTaxEditText.getText().toString()));
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
                        mSaleOrderLine.getProduct().getProductCommercialPackage().getUnits(), mSaleOrderLine.getProduct().getProductCommercialPackage().getUnitDescription()));
            }else{
                view.findViewById(R.id.product_commercial_package).setVisibility(TextView.GONE);
            }
        }

        switch (mFocus) {
            case ShoppingSaleAdapter.FOCUS_PRICE:
                productPriceEditText.requestFocus();
                productPriceEditText.setSelection(productPriceEditText.length());
                break;
            case ShoppingSaleAdapter.FOCUS_TAX_PERCENTAGE:
                productTaxEditText.requestFocus();
                productTaxEditText.setSelection(productTaxEditText.length());
                break;
            case ShoppingSaleAdapter.FOCUS_QTY_ORDERED:
                qtyRequestedEditText.requestFocus();
                qtyRequestedEditText.setSelection(qtyRequestedEditText.length());
                break;
        }

        ((TextView) view.findViewById(R.id.product_name_textView)).setText(mSaleOrderLine.getProduct().getName());
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_ORDERLINE, mSaleOrderLine);
        outState.putParcelable(STATE_USER, mUser);
        outState.putInt(STATE_FOCUS, mFocus);
        super.onSaveInstanceState(outState);
    }
}
