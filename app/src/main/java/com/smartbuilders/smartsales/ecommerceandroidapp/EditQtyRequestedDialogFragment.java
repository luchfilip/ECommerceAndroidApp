package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;

/**
 * Created by stein on 5/1/2016.
 */
public class EditQtyRequestedDialogFragment extends DialogFragment {

    private EditText mEditText;
    private Product mProduct;

    public EditQtyRequestedDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static EditQtyRequestedDialogFragment newInstance(Product product){
        EditQtyRequestedDialogFragment editQtyRequestedDialogFragment = new EditQtyRequestedDialogFragment();
        editQtyRequestedDialogFragment.mProduct = product;
        return editQtyRequestedDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_qty_requested, container);
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
        view.findViewById(R.id.addtoshoppingcart_dialog_qty_requested_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "Producto agregado al Carrito de Compras",
                                Toast.LENGTH_LONG).show();
                        dismiss();
                    }
                }
        );
        getDialog().setTitle(mProduct.getName());

        return view;
    }
}
