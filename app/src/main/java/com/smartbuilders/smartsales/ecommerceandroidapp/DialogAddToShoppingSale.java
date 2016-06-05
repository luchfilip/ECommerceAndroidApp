package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stein on 5/1/2016.
 */
public class DialogAddToShoppingSale extends DialogFragment {

    private static final String STATE_CURRENT_PRODUCT = "STATE_CURRENT_PRODUCT";
    private static final String STATE_CURRENT_USER = "STATE_CURRENT_USER";

    private Product mProduct;
    private User mUser;

    public DialogAddToShoppingSale() {
        // Empty constructor required for DialogFragment
    }

    public static DialogAddToShoppingSale newInstance(Product product, User user){
        DialogAddToShoppingSale editQtyRequestedDialogFragment = new DialogAddToShoppingSale();
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

        final View view = inflater.inflate(R.layout.dialog_add_to_shopping_sale, container);
        ((TextView) view.findViewById(R.id.product_availability_dialog_edit_qty_requested_tv))
                .setText(getContext().getString(R.string.availability, mProduct.getAvailability()));

        final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);


        List<String> spinnerArray =  new ArrayList<>();
        final ArrayList<BusinessPartner> businessPartners =
                (new BusinessPartnerDB(getContext(), mUser)).getActiveBusinessPartners();
        int selectedIndex = 0;
        if (businessPartners!=null) {
            int index = 0;
            for (BusinessPartner businessPartner : businessPartners) {
                if(businessPartner.getId() == sharedPref.getInt("current_business_partner_id", 0)){
                    selectedIndex = index;
                }
                spinnerArray.add(businessPartner.getCommercialName() + " - " +
                        getString(R.string.tax_id, businessPartner.getTaxId()));
                index++;
            }
        } else {
            spinnerArray.add(getString(R.string.no_business_partners_registered));
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner businessPartnersSpinner = (Spinner) view.findViewById(R.id.business_partners_spinner);
        businessPartnersSpinner.setAdapter(adapter);
        businessPartnersSpinner.setSelection(selectedIndex);

        businessPartnersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("current_business_partner_id", businessPartners.get(position).getId());
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

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

        if(view.findViewById(R.id.product_commercial_package) != null){
            if(mProduct.getProductCommercialPackage()!=null
                    && !TextUtils.isEmpty(mProduct.getProductCommercialPackage().getUnitDescription())){
                ((TextView) view.findViewById(R.id.product_commercial_package)).setText(getContext().getString(R.string.commercial_package,
                        mProduct.getProductCommercialPackage().getUnits() + " " +
                                mProduct.getProductCommercialPackage().getUnitDescription()));
            }else{
                view.findViewById(R.id.product_commercial_package).setVisibility(TextView.GONE);
            }
        }

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
                ((InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDismiss(dialog);
    }
}
