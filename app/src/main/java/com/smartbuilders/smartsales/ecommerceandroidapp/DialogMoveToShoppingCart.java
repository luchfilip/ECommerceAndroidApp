package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;

/**
 * Created by stein on 5/1/2016.
 */
public class DialogMoveToShoppingCart extends DialogFragment {

    private static final String STATE_CURRENT_ORDERLINE = "STATE_CURRENT_ORDERLINE";
    private static final String STATE_CURRENT_USER = "STATE_CURRENT_USER";

    private OrderLine mOrderLine;
    private User mUser;

    public DialogMoveToShoppingCart() {
        // Empty constructor required for DialogFragment
    }

    public static DialogMoveToShoppingCart newInstance(OrderLine orderLine, User user){
        DialogMoveToShoppingCart dialogMoveToShoppingCart = new DialogMoveToShoppingCart();
        dialogMoveToShoppingCart.mUser = user;
        dialogMoveToShoppingCart.mOrderLine = orderLine;
        return dialogMoveToShoppingCart;
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
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        final View view = inflater.inflate(R.layout.dialog_move_to_shopping_cart, container);

        ((TextView) view.findViewById(R.id.product_availability_textView))
                .setText(getContext().getString(R.string.availability, mOrderLine.getProduct().getAvailability()));

        view.findViewById(R.id.cancel_button).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            }
        );

        view.findViewById(R.id.move_to_shopping_cart_button).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int qtyRequested = Integer
                                .valueOf(((EditText) view.findViewById(R.id.qty_requested_editText)).getText().toString());
                        OrderLineDB orderLineDB = new OrderLineDB(getContext(), mUser);
                        String result = orderLineDB.moveOrderLineToShoppingCart(mOrderLine, qtyRequested);
                        if(result == null){
                            Toast.makeText(getContext(), R.string.product_moved_to_shopping_cart, Toast.LENGTH_SHORT).show();
                            ((WishListFragment) getTargetFragment()).reloadWishList();
                        } else {
                            throw new Exception(result);
                        }
                        dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        );

        if(view.findViewById(R.id.product_commercial_package) != null){
            if(mOrderLine.getProduct()!=null && mOrderLine.getProduct().getProductCommercialPackage()!=null
                    && !TextUtils.isEmpty(mOrderLine.getProduct().getProductCommercialPackage().getUnitDescription())){
                ((TextView) view.findViewById(R.id.product_commercial_package)).setText(getContext().getString(R.string.commercial_package,
                        mOrderLine.getProduct().getProductCommercialPackage().getUnits() + " " +
                                mOrderLine.getProduct().getProductCommercialPackage().getUnitDescription()));
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
        outState.putParcelable(STATE_CURRENT_ORDERLINE, mOrderLine);
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
