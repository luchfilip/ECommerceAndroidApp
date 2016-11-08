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
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;

/**
 * Created by Jesus Sarco, 2.10.2016
 */
public class DialogAddToShoppingSale2 extends DialogFragment {

    private static final String STATE_CURRENT_PRODUCT = "STATE_CURRENT_PRODUCT";
    private static final String STATE_CURRENT_USER = "STATE_CURRENT_USER";

    private Product mProduct;
    private User mUser;

    public DialogAddToShoppingSale2() {
        // Empty constructor required for DialogFragment
    }

    public static DialogAddToShoppingSale2 newInstance(Product product, User user){
        DialogAddToShoppingSale2 dialogAddToShoppingSale2 = new DialogAddToShoppingSale2();
        dialogAddToShoppingSale2.mProduct = product;
        dialogAddToShoppingSale2.mUser = user;
        return dialogAddToShoppingSale2;
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
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        final View view = inflater.inflate(R.layout.dialog_add_to_shopping_sale_2, container);

        ((TextView) view.findViewById(R.id.product_name_textView)).setText(mProduct.getName());

        if(view.findViewById(R.id.product_commercial_package_textView) != null){
            if(mProduct.getProductCommercialPackage()!=null
                    && !TextUtils.isEmpty(mProduct.getProductCommercialPackage().getUnitDescription())){
                ((TextView) view.findViewById(R.id.product_commercial_package_textView))
                        .setText(getContext().getString(R.string.commercial_package_label_detail,
                                mProduct.getProductCommercialPackage().getUnitDescription(),
                                mProduct.getProductCommercialPackage().getUnits()));
            }else{
                view.findViewById(R.id.product_commercial_package_textView).setVisibility(TextView.GONE);
            }
        }

        if (Parameter.showProductTotalPrice(getContext(), mUser)) {
            ((TextView) view.findViewById(R.id.product_total_price_textView))
                    .setText(getString(R.string.product_total_price_detail,
                            mProduct.getProductPriceAvailability().getCurrency().getName(),
                            mProduct.getProductPriceAvailability().getTotalPriceStringFormat()));
        } else if (Parameter.showProductPrice(getContext(), mUser)) {
            ((TextView) view.findViewById(R.id.product_total_price_textView))
                    .setText(getString(R.string.product_price_detail,
                            mProduct.getProductPriceAvailability().getCurrency().getName(),
                            mProduct.getProductPriceAvailability().getPriceStringFormat()));
        } else {
            view.findViewById(R.id.product_total_price_textView).setVisibility(View.GONE);
        }


        ((TextView) view.findViewById(R.id.product_availability_textView))
                .setText(getString(R.string.availability,
                        mProduct.getProductPriceAvailability().getAvailability()));

        view.findViewById(R.id.cancel_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                }
            );

        view.findViewById(R.id.add_to_shopping_sale_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            SalesOrderLineBR.validateQtyOrdered(getContext(),
                                    Integer.valueOf(((EditText) view.findViewById(R.id.qty_requested_editText)).getText().toString()),
                                    mProduct);

                            SalesOrderLine salesOrderLine = new SalesOrderLine();
                            SalesOrderLineBR.fillSalesOrderLine(Integer.valueOf(((EditText) view.findViewById(R.id.qty_requested_editText)).getText().toString()),
                                    mProduct, salesOrderLine);
                            salesOrderLine.setBusinessPartnerId(Utils.getAppCurrentBusinessPartnerId(getContext(), mUser));

                            String result = (new SalesOrderLineDB(getContext(), mUser))
                                    .addSalesOrderLinesToShoppingSale(salesOrderLine);

                            if(result == null){
                                Toast.makeText(getContext(), R.string.product_moved_to_shopping_sale,
                                        Toast.LENGTH_SHORT).show();
                                dismiss();
                            } else {
                                Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), R.string.invalid_qty_requested, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mUser);
        outState.putParcelable(STATE_CURRENT_PRODUCT, mProduct);
        super.onSaveInstanceState(outState);
    }
}
