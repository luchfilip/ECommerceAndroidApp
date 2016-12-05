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

import com.smartbuilders.smartsales.ecommerce.businessRules.OrderLineBR;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;

/**
 * Created by stein on 5/1/2016.
 */
public class DialogAddToShoppingCart extends DialogFragment {

    private static final String STATE_CURRENT_PRODUCT = "STATE_CURRENT_PRODUCT";
    private static final String STATE_CURRENT_USER = "STATE_CURRENT_USER";

    private Product mProduct;
    private User mUser;

    public DialogAddToShoppingCart() {
        // Empty constructor required for DialogFragment
    }

    public static DialogAddToShoppingCart newInstance(Product product, User user){
        DialogAddToShoppingCart dialogAddToShoppingCart = new DialogAddToShoppingCart();
        dialogAddToShoppingCart.mProduct = product;
        dialogAddToShoppingCart.mUser = user;
        return dialogAddToShoppingCart;
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

        final View view = inflater.inflate(R.layout.dialog_add_to_shopping_cart, container);

        if (Parameter.isManagePriceInOrder(getContext(), mUser)) {
            if (Parameter.showProductTotalPrice(getContext(), mUser)) {
                ((TextView) view.findViewById(R.id.product_price_dialog_edit_qty_requested_tv))
                        .setText(getString(R.string.product_total_price_detail,
                                mProduct.getProductPriceAvailability().getCurrency().getName(),
                                mProduct.getProductPriceAvailability().getTotalPriceStringFormat()));
                view.findViewById(R.id.product_price_dialog_edit_qty_requested_tv).setVisibility(View.VISIBLE);
            } else if (Parameter.showProductPrice(getContext(), mUser)) {
                ((TextView) view.findViewById(R.id.product_price_dialog_edit_qty_requested_tv))
                        .setText(getString(R.string.product_price_detail,
                                mProduct.getProductPriceAvailability().getCurrency().getName(),
                                mProduct.getProductPriceAvailability().getPriceStringFormat()));
                view.findViewById(R.id.product_price_dialog_edit_qty_requested_tv).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.product_price_dialog_edit_qty_requested_tv).setVisibility(View.GONE);
            }
        } else {
            view.findViewById(R.id.product_price_dialog_edit_qty_requested_tv).setVisibility(View.GONE);
        }

        ((TextView) view.findViewById(R.id.product_availability_dialog_edit_qty_requested_tv))
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

        view.findViewById(R.id.add_to_shopping_cart_button).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        OrderLineBR.validateQtyOrdered(getContext(),
                                Integer.valueOf(((EditText) view.findViewById(R.id.qty_requested_editText)).getText().toString()),
                                mProduct);

                        OrderLine orderLine = new OrderLine();
                        orderLine.setProductId(mProduct.getId());
                        orderLine.setProduct(mProduct);
                        orderLine.setQuantityOrdered(Integer.valueOf(((EditText) view.findViewById(R.id.qty_requested_editText)).getText().toString()));
                        //OrderLineBR.fillOrderLine(Integer.valueOf(((EditText) view.findViewById(R.id.qty_requested_editText)).getText().toString()),
                        //        mProduct, orderLine);
                        orderLine.setBusinessPartnerId(Utils.getAppCurrentBusinessPartnerId(getContext(), mUser));

                        String result = (new OrderLineDB(getContext(), mUser)).addOrderLineToShoppingCart(orderLine);
                        if(result == null){
                            Toast.makeText(getContext(), R.string.product_moved_to_shopping_cart,
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

        if(view.findViewById(R.id.product_commercial_package) != null){
            if(mProduct.getProductCommercialPackage()!=null
                    && !TextUtils.isEmpty(mProduct.getProductCommercialPackage().getUnitDescription())){
                ((TextView) view.findViewById(R.id.product_commercial_package)).setText(getContext().getString(R.string.commercial_package_label_detail,
                        mProduct.getProductCommercialPackage().getUnitDescription(), mProduct.getProductCommercialPackage().getUnits()));
            }else{
                view.findViewById(R.id.product_commercial_package).setVisibility(TextView.GONE);
            }
        }

        ((TextView) view.findViewById(R.id.product_name_textView)).setText(mProduct.getName());
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mUser);
        outState.putParcelable(STATE_CURRENT_PRODUCT, mProduct);
        super.onSaveInstanceState(outState);
    }
}
