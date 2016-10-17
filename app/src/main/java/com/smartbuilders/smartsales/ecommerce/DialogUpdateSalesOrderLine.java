package com.smartbuilders.smartsales.ecommerce;

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

import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.adapters.ShoppingSaleAdapter;
import com.smartbuilders.smartsales.ecommerce.businessRules.SalesOrderLineBR;
import com.smartbuilders.smartsales.ecommerce.data.CurrencyDB;
import com.smartbuilders.smartsales.ecommerce.data.SalesOrderLineDB;
import com.smartbuilders.smartsales.ecommerce.model.Currency;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 * Created by stein on 26/5/2016.
 */
public class DialogUpdateSalesOrderLine extends DialogFragment {

    private static final String STATE_ORDER_LINE = "STATE_ORDER_LINE";
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
            if(savedInstanceState.containsKey(STATE_ORDER_LINE)){
                mSaleOrderLine = savedInstanceState.getParcelable(STATE_ORDER_LINE);
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
                .setText(getContext().getString(R.string.availability,
                        mSaleOrderLine.getProduct().getDefaultProductPriceAvailability().getAvailability()));

        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Currency currency = (new CurrencyDB(getContext(), mUser))
                .getActiveCurrencyById(Parameter.getDefaultCurrencyId(getContext(), mUser));
        ((TextView) view.findViewById(R.id.product_price_label_textView)).setText(currency!=null
                ? getString(R.string.price_currency_label_detail, currency.getName())
                : getString(R.string.price_label));

        try {
            productPriceEditText.setText(String.valueOf(mSaleOrderLine.getProductPrice()));
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
            productTaxEditText.setText(String.valueOf(mSaleOrderLine.getProductTaxPercentage()));
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
                    int qtyOrdered = 0;
                    try {
                        qtyOrdered = Integer.valueOf(qtyRequestedEditText.getText().toString());
                    } catch (NumberFormatException e) {
                        //do nothing
                    }
                    if (qtyOrdered<=0) {
                        throw new Exception(getString(R.string.invalid_qty_requested));
                    }

                    /**********************************************************/
                    Product product = new Product();
                    product.setId(mSaleOrderLine.getProductId());
                    try {
                        product.getDefaultProductPriceAvailability().setPrice(Float.valueOf(productPriceEditText.getText().toString()));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    try {
                        product.getProductTax().setPercentage(Float.valueOf(productTaxEditText.getText().toString()));
                        product.getDefaultProductPriceAvailability().setTax(product.getDefaultProductPriceAvailability().getPrice()
                                * (product.getProductTax().getPercentage() / 100));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    product.getDefaultProductPriceAvailability().setTotalPrice(product.getDefaultProductPriceAvailability().getPrice()
                            + product.getDefaultProductPriceAvailability().getTax());
                    /**********************************************************/

                    SalesOrderLineBR.fillSalesOrderLine(qtyOrdered, product, mSaleOrderLine);

                    String result = (new SalesOrderLineDB(getContext(), mUser)).updateSalesOrderLine(mSaleOrderLine);
                    if(result == null){
                        if (getTargetFragment() instanceof  ShoppingSaleFragment) {
                            ((ShoppingSaleFragment) getTargetFragment()).reloadShoppingSale();
                        }
                    } else {
                        throw new Exception(result);
                    }
                    dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(view.findViewById(R.id.product_commercial_package) != null){
            if(mSaleOrderLine.getProduct()!=null && mSaleOrderLine.getProduct().getProductCommercialPackage()!=null
                    && !TextUtils.isEmpty(mSaleOrderLine.getProduct().getProductCommercialPackage().getUnitDescription())){
                ((TextView) view.findViewById(R.id.product_commercial_package))
                        .setText(getContext().getString(R.string.commercial_package_label_detail,
                                mSaleOrderLine.getProduct().getProductCommercialPackage().getUnitDescription(),
                                mSaleOrderLine.getProduct().getProductCommercialPackage().getUnits()));
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
        outState.putParcelable(STATE_ORDER_LINE, mSaleOrderLine);
        outState.putParcelable(STATE_USER, mUser);
        outState.putInt(STATE_FOCUS, mFocus);
        super.onSaveInstanceState(outState);
    }
}
