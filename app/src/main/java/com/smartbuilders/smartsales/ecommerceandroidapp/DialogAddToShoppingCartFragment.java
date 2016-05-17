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
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;

/**
 * Created by stein on 5/1/2016.
 */
public class DialogAddToShoppingCartFragment extends DialogFragment {

    private EditText mEditText;
    private Product mProduct;
    private User mUser;

    public DialogAddToShoppingCartFragment() {
        // Empty constructor required for DialogFragment
    }

    public static DialogAddToShoppingCartFragment newInstance(Product product, User user){
        DialogAddToShoppingCartFragment editQtyRequestedDialogFragment = new DialogAddToShoppingCartFragment();
        editQtyRequestedDialogFragment.mProduct = product;
        editQtyRequestedDialogFragment.mUser = user;
        return editQtyRequestedDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_to_shopping_cart, container);
        mEditText = (EditText) view.findViewById(R.id.qty_requested_editText);

        ((TextView) view.findViewById(R.id.product_availability_dialog_edit_qty_requested_tv))
                .setText(getContext().getString(R.string.availability, mProduct.getAvailability()));

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
                            int qtyRequested = Integer
                                    .valueOf(((EditText) view.findViewById(R.id.qty_requested_editText)).getText().toString());
                            String result = (new OrderLineDB(getContext(), mUser)).addProductToShoppingCart(mProduct, qtyRequested);
                            if(result == null){
                                Toast.makeText(getContext(), R.string.product_moved_to_shopping_cart,
                                        Toast.LENGTH_LONG).show();
                                dismiss();
                            } else {
                                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
        getDialog().setTitle(mProduct.getName());
        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        try {
            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDismiss(dialog);
    }
}
