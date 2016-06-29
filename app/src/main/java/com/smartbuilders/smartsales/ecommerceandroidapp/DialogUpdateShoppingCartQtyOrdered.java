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
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;

/**
 * Created by stein on 26/5/2016.
 */
public class DialogUpdateShoppingCartQtyOrdered extends DialogFragment {

    private static final String STATE_CURRENT_ORDERLINE = "STATE_CURRENT_ORDERLINE";
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
            if(savedInstanceState.containsKey(STATE_CURRENT_ORDERLINE)){
                mOrderLine = savedInstanceState.getParcelable(STATE_CURRENT_ORDERLINE);
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

        ((TextView) view.findViewById(R.id.product_availability_textView))
                .setText(getContext().getString(R.string.availability, mOrderLine.getProduct().getAvailability()));

        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.findViewById(R.id.accept_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int oldValue = mOrderLine.getQuantityOrdered();
                try {
                    mOrderLine.setQuantityOrdered(Integer.valueOf(qtyOrderedEditText.getText().toString()));
                    //TODO: mandar estas validaciones a una clase de businessRules
                    if (mOrderLine.getQuantityOrdered()<=0) {
                        throw new Exception(getString(R.string.invalid_qty_requested));
                    }
                    if ((mOrderLine.getQuantityOrdered() % mOrderLine.getProduct().getProductCommercialPackage().getUnits())!=0) {
                        throw new Exception("La cantidad pedida debe ser multiplo del empaque comercial.");
                    }
                    if (mOrderLine.getQuantityOrdered() > mOrderLine.getProduct().getAvailability()) {
                        throw new Exception("La cantidad pedida no puede ser mayor a la disponibilidad.");
                    }
                    String result = null;
                    if (mIsShoppingCart) {
                        result = (new OrderLineDB(getContext(), mUser)).updateOrderLine(mOrderLine);
                    }
                    if(result == null){
                        if(getTargetFragment() instanceof Callback){
                            ((Callback) getTargetFragment()).reloadShoppingCart();
                        }else{
                            Toast.makeText(getContext(), "Se actualizó la cantidad pedida.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        throw new Exception(result);
                    }
                    dismiss();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), R.string.invalid_qty_requested, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    mOrderLine.setQuantityOrdered(oldValue);
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        if(view.findViewById(R.id.product_commercial_package) != null){
            if(mOrderLine.getProduct()!=null && mOrderLine.getProduct().getProductCommercialPackage()!=null
                    && !TextUtils.isEmpty(mOrderLine.getProduct().getProductCommercialPackage().getUnitDescription())){
                ((TextView) view.findViewById(R.id.product_commercial_package)).setText(getContext().getString(R.string.commercial_package,
                        mOrderLine.getProduct().getProductCommercialPackage().getUnits(), mOrderLine.getProduct().getProductCommercialPackage().getUnitDescription()));
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
        outState.putParcelable(STATE_CURRENT_ORDERLINE, mOrderLine);
        super.onSaveInstanceState(outState);
    }
}
