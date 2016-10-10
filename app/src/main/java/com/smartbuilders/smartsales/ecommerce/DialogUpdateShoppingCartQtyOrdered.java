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
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;

/**
 * Created by stein on 26/5/2016.
 */
public class DialogUpdateShoppingCartQtyOrdered extends DialogFragment {

    private static final String STATE_CURRENT_ORDER_LINE = "STATE_CURRENT_ORDER_LINE";
    private static final String STATE_IS_SHOPPING_CART = "STATE_IS_SHOPPING_CART";
    private static final String STATE_CURRENT_USER = "STATE_CURRENT_USER";

    private OrderLine mOrderLine;
    private boolean mIsShoppingCart;
    private User mUser;

    public static DialogUpdateShoppingCartQtyOrdered newInstance(OrderLine orderLine, boolean isShoppingCart, User user){
        DialogUpdateShoppingCartQtyOrdered dialogUpdateShoppingCartQtyOrdered = new DialogUpdateShoppingCartQtyOrdered();
        dialogUpdateShoppingCartQtyOrdered.mUser = user;
        dialogUpdateShoppingCartQtyOrdered.mIsShoppingCart = isShoppingCart;
        dialogUpdateShoppingCartQtyOrdered.mOrderLine = orderLine;
        return dialogUpdateShoppingCartQtyOrdered;
    }

    public interface Callback {
        void reloadShoppingCart();
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
            if(savedInstanceState.containsKey(STATE_CURRENT_ORDER_LINE)){
                mOrderLine = savedInstanceState.getParcelable(STATE_CURRENT_ORDER_LINE);
            }
            if(savedInstanceState.containsKey(STATE_IS_SHOPPING_CART)){
                mIsShoppingCart = savedInstanceState.getBoolean(STATE_IS_SHOPPING_CART);
            }
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        final View view = inflater.inflate(R.layout.dialog_update_qty_ordered, container);

        final EditText qtyOrderedEditText = ((EditText) view.findViewById(R.id.qty_requested_editText));
        qtyOrderedEditText.setText(String.valueOf(mOrderLine.getQuantityOrdered()));
        //se coloca el indicador del focus al final del texto
        qtyOrderedEditText.setSelection(qtyOrderedEditText.length());

        if (Parameter.isManagePriceInOrder(getContext(), mUser)) {
            ((TextView) view.findViewById(R.id.product_price_textView))
                    .setText(getString(R.string.price_detail,
                            mOrderLine.getProduct().getDefaultProductPriceAvailability().getCurrency().getName(),
                            mOrderLine.getProduct().getDefaultProductPriceAvailability().getPrice()));
            view.findViewById(R.id.product_price_textView).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.product_price_textView).setVisibility(View.GONE);
        }

        ((TextView) view.findViewById(R.id.product_availability_textView))
                .setText(getContext().getString(R.string.availability,
                        mOrderLine.getProduct().getDefaultProductPriceAvailability().getAvailability()));

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
                    OrderLineBR.validateQtyOrdered(getContext(), Integer.valueOf(qtyOrderedEditText.getText().toString()),
                            mOrderLine.getProduct());

                    OrderLineBR.fillOrderLine(Integer.valueOf(qtyOrderedEditText.getText().toString()),
                            mOrderLine.getProduct(), mOrderLine);

                    String result = null;
                    if (mIsShoppingCart) {
                        result = (new OrderLineDB(getContext(), mUser)).updateOrderLine(mOrderLine);
                    }
                    if(result == null){
                        if(getTargetFragment() instanceof Callback){
                            ((Callback) getTargetFragment()).reloadShoppingCart();
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
            if(mOrderLine.getProduct()!=null && mOrderLine.getProduct().getProductCommercialPackage()!=null
                    && !TextUtils.isEmpty(mOrderLine.getProduct().getProductCommercialPackage().getUnitDescription())){
                ((TextView) view.findViewById(R.id.product_commercial_package)).setText(getContext().getString(R.string.commercial_package_label_detail,
                        mOrderLine.getProduct().getProductCommercialPackage().getUnitDescription(), mOrderLine.getProduct().getProductCommercialPackage().getUnits()));
            }else{
                view.findViewById(R.id.product_commercial_package).setVisibility(TextView.GONE);
            }
        }

        ((TextView) view.findViewById(R.id.product_name_textView)).setText(mOrderLine.getProduct().getName());
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mUser);
        outState.putBoolean(STATE_IS_SHOPPING_CART, mIsShoppingCart);
        outState.putParcelable(STATE_CURRENT_ORDER_LINE, mOrderLine);
        super.onSaveInstanceState(outState);
    }
}
