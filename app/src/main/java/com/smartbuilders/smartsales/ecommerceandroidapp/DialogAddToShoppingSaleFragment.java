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
public class DialogAddToShoppingSaleFragment extends DialogFragment {

    private static final String STATE_CURRENT_PRODUCT = "STATE_CURRENT_PRODUCT";
    private static final String STATE_CURRENT_USER = "STATE_CURRENT_USER";

    private EditText mEditText;
    private Product mProduct;
    private User mUser;

    public DialogAddToShoppingSaleFragment() {
        // Empty constructor required for DialogFragment
    }

    public static DialogAddToShoppingSaleFragment newInstance(Product product, User user){
        DialogAddToShoppingSaleFragment editQtyRequestedDialogFragment = new DialogAddToShoppingSaleFragment();
        editQtyRequestedDialogFragment.mProduct = product;
        editQtyRequestedDialogFragment.mUser = user;
        return editQtyRequestedDialogFragment;
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

        final View view = inflater.inflate(R.layout.fragment_add_to_shopping_sale, container);
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

        view.findViewById(R.id.add_to_shopping_sale_button).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int qtyRequested = Integer
                                .valueOf(((EditText) view.findViewById(R.id.qty_requested_editText)).getText().toString());
                        double productPrice = 0;
                        try {
                            productPrice = Double
                                    .valueOf(((EditText) view.findViewById(R.id.product_price_editText)).getText().toString());
                        } catch (Exception e) { }
                        double productTaxPercentage = 0;
                        try {
                            productTaxPercentage = Double
                                    .valueOf(((EditText) view.findViewById(R.id.product_tax_editText)).getText().toString());
                        } catch (Exception e) { }
                        String result = (new OrderLineDB(getContext(), mUser))
                                .addProductToShoppingSale(mProduct, qtyRequested, productPrice, productTaxPercentage);
                        if(result == null){
                            Toast.makeText(getContext(), R.string.product_moved_to_shopping_sale,
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mUser);
        outState.putParcelable(STATE_CURRENT_PRODUCT, mProduct);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        try {
            if (getActivity()!=null && getActivity().getWindow()!=null) {
                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDismiss(dialog);
    }
}
