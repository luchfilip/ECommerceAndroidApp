package com.smartbuilders.smartsales.salesforcesystem;

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

import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.businessRules.SalesOrderLineBR;
import com.smartbuilders.smartsales.ecommerce.data.SalesOrderLineDB;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;
import com.smartbuilders.synchronizer.ids.model.User;

/**
 * Created by Jesus Sarco, 02/10/2016.
 */
public class DialogUpdateShoppingSaleQtyOrdered extends DialogFragment {

    private static final String STATE_CURRENT_PRODUCT = "STATE_CURRENT_PRODUCT";
    private static final String STATE_CURRENT_SALES_ORDER_LINE = "STATE_CURRENT_SALES_ORDER_LINE";
    private static final String STATE_CURRENT_USER = "STATE_CURRENT_USER";

    private Product mProduct;
    private SalesOrderLine mSalesOrderLine;
    private User mUser;

    public static DialogUpdateShoppingSaleQtyOrdered newInstance(Product product, SalesOrderLine salesOrderLine, User user){
        DialogUpdateShoppingSaleQtyOrdered dialogUpdateShoppingSaleQtyOrdered = new DialogUpdateShoppingSaleQtyOrdered();
        dialogUpdateShoppingSaleQtyOrdered.mProduct = product;
        dialogUpdateShoppingSaleQtyOrdered.mUser = user;
        dialogUpdateShoppingSaleQtyOrdered.mSalesOrderLine = salesOrderLine;
        return dialogUpdateShoppingSaleQtyOrdered;
    }

    public interface Callback {
        void reloadShoppingSale();
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
        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(STATE_CURRENT_PRODUCT)){
                mProduct = savedInstanceState.getParcelable(STATE_CURRENT_PRODUCT);
            }
            if(savedInstanceState.containsKey(STATE_CURRENT_SALES_ORDER_LINE)){
                mSalesOrderLine = savedInstanceState.getParcelable(STATE_CURRENT_SALES_ORDER_LINE);
            }
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        final View view = inflater.inflate(R.layout.dialog_update_shopping_sale_qty_ordered, container);

        final EditText qtyOrderedEditText = ((EditText) view.findViewById(R.id.qty_requested_editText));
        qtyOrderedEditText.setText(String.valueOf(mSalesOrderLine.getQuantityOrdered()));
        //se coloca el indicador del focus al final del texto
        qtyOrderedEditText.setSelection(qtyOrderedEditText.length());

        ((TextView) view.findViewById(R.id.product_price_textView))
                .setText(getString(R.string.price_detail,
                        mProduct.getDefaultProductPriceAvailability().getCurrency().getName(),
                        mProduct.getDefaultProductPriceAvailability().getPrice()));
        view.findViewById(R.id.product_price_textView).setVisibility(View.VISIBLE);

        ((TextView) view.findViewById(R.id.product_availability_textView))
                .setText(getContext().getString(R.string.availability,
                        mProduct.getDefaultProductPriceAvailability().getAvailability()));

        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.findViewById(R.id.accept_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SalesOrderLineBR.validateQtyOrdered(getContext(), Integer.valueOf(qtyOrderedEditText.getText().toString()),
                            mProduct);

                    SalesOrderLineBR.fillSalesOrderLine(Integer.valueOf(qtyOrderedEditText.getText().toString()),
                            mProduct, mSalesOrderLine);

                    String result = (new SalesOrderLineDB(getContext(), mUser)).updateSalesOrderLine(mSalesOrderLine);
                    if(result == null){
                        if(getTargetFragment() instanceof Callback){
                            ((Callback) getTargetFragment()).reloadShoppingSale();
                        }else{
                            Toast.makeText(getContext(), getString(R.string.qty_requested_updated), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        throw new Exception(result);
                    }
                    dismiss();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), R.string.invalid_qty_requested, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(view.findViewById(R.id.product_commercial_package) != null){
            if(mProduct!=null && mProduct.getProductCommercialPackage()!=null
                    && !TextUtils.isEmpty(mProduct.getProductCommercialPackage().getUnitDescription())){
                ((TextView) view.findViewById(R.id.product_commercial_package))
                        .setText(getContext().getString(R.string.commercial_package_label_detail,
                                mProduct.getProductCommercialPackage().getUnitDescription(),
                                mProduct.getProductCommercialPackage().getUnits()));
            }else{
                view.findViewById(R.id.product_commercial_package).setVisibility(TextView.GONE);
            }
        }

        ((TextView) view.findViewById(R.id.product_name_textView)).setText(mProduct.getName());
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_PRODUCT, mProduct);
        outState.putParcelable(STATE_CURRENT_USER, mUser);
        outState.putParcelable(STATE_CURRENT_SALES_ORDER_LINE, mSalesOrderLine);
        super.onSaveInstanceState(outState);
    }
}
