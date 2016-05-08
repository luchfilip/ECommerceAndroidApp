package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;

/**
 * Created by stein on 5/1/2016.
 */
public class EditQtyRequestedDialogFragment extends DialogFragment {

    private EditText mEditText;
    private Product mProduct;
    private User mUser;
    private String docType;

    public EditQtyRequestedDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static EditQtyRequestedDialogFragment newInstance(Product product, User user, String docType){
        EditQtyRequestedDialogFragment editQtyRequestedDialogFragment = new EditQtyRequestedDialogFragment();
        editQtyRequestedDialogFragment.mProduct = product;
        editQtyRequestedDialogFragment.mUser = user;
        editQtyRequestedDialogFragment.docType = docType;
        return editQtyRequestedDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_edit_qty_requested, container);
        mEditText = (EditText) view.findViewById(R.id.qty_requested_editText);

        ((TextView) view.findViewById(R.id.product_availability_dialog_edit_qty_requested_tv))
                .setText(getContext().getString(R.string.availability, mProduct.getAvailability()));

        view.findViewById(R.id.cancel_dialog_qty_requested_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                }
        );

        if(docType.equals(OrderLineDB.SHOPPING_SALE_DOCTYPE)) {
            ((Button) view.findViewById(R.id.addtoshoppingcart_dialog_qty_requested_button))
                    .setText(R.string.add_to_shopping_sales);
            view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryLight));
            view.findViewById(R.id.addtoshoppingcart_dialog_qty_requested_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int qtyRequested = Integer
                                    .valueOf(((EditText) view.findViewById(R.id.qty_requested_editText)).getText().toString());
                            String result = (new OrderLineDB(getContext(), mUser)).addProductToShoppingSale(mProduct, qtyRequested);
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
        } else if (docType.equals(OrderLineDB.SHOPPING_CART_DOCTYPE)){
            view.findViewById(R.id.addtoshoppingcart_dialog_qty_requested_button).setOnClickListener(
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
        }
        getDialog().setTitle(mProduct.getName());
        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
