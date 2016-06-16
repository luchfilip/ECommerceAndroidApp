package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.SalesOrderLineDB;
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
    private SharedPreferences sharedPref;
    private Spinner businessPartnersSpinner;
    private View buttonsContainer;
    private View registerBusinessPartnerButton;

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

        final View view = inflater.inflate(R.layout.dialog_add_to_shopping_sale, container);
        ((TextView) view.findViewById(R.id.product_availability_dialog_edit_qty_requested_tv))
                .setText(getContext().getString(R.string.availability, mProduct.getAvailability()));

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        businessPartnersSpinner = (Spinner) view.findViewById(R.id.business_partners_spinner);
        buttonsContainer = view.findViewById(R.id.buttons_container);
        registerBusinessPartnerButton = view.findViewById(R.id.register_business_partner_button);

        //TODO: eliminar este codigo duro, leerlo de la tabla ProductTax
        ((EditText) view.findViewById(R.id.product_tax_editText)).setText("12");

        registerBusinessPartnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogCreateBusinessPartner();
            }
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
                        //TODO: mandar estas validaciones a una clase de businessRules
                        if (qtyRequested<=0) {
                            throw new Exception("Cantidad pedida inválida.");
                        }
                        if ((qtyRequested % mProduct.getProductCommercialPackage().getUnits())!=0) {
                            throw new Exception("La cantidad pedida debe ser multiplo del empaque comercial.");
                        }
                        if (qtyRequested > mProduct.getAvailability()) {
                            throw new Exception("La cantidad pedida no puede ser mayor a la disponibilidad.");
                        }
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

                        String result = (new SalesOrderLineDB(getContext(), mUser))
                                .addProductToShoppingSale(mProduct, qtyRequested, productPrice,
                                        productTaxPercentage, sharedPref.getInt("current_business_partner_id", 0));
                        if(result == null){
                            Toast.makeText(getContext(), R.string.product_moved_to_shopping_sale,
                                    Toast.LENGTH_LONG).show();
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Cantidad pedida inválida.", Toast.LENGTH_LONG).show();
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

        ((TextView) view.findViewById(R.id.product_name_textView)).setText(mProduct.getName());

        initViews();
        return view;
    }

    private void showDialogCreateBusinessPartner() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        DialogRegisterBusinessPartner dialogRegisterBusinessPartner =
                DialogRegisterBusinessPartner.newInstance(mUser);
        dialogRegisterBusinessPartner.setTargetFragment(this, 0);
        dialogRegisterBusinessPartner.show(fm, DialogRegisterBusinessPartner.class.getSimpleName());
    }

    public void initViews(){
        final ArrayList<BusinessPartner> businessPartners =
                (new BusinessPartnerDB(getContext(), mUser)).getActiveBusinessPartners();
        if (businessPartners!=null && !businessPartners.isEmpty()) {
            int index = 0;
            int selectedIndex = 0;
            List<String> spinnerArray =  new ArrayList<>();
            for (BusinessPartner businessPartner : businessPartners) {
                if(businessPartner.getId() == sharedPref.getInt("current_business_partner_id", 0)){
                    selectedIndex = index;
                }
                spinnerArray.add(businessPartner.getCommercialName() + " - " +
                        getString(R.string.tax_id, businessPartner.getTaxId()));
                index++;
            }
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
            businessPartnersSpinner.setVisibility(View.VISIBLE);
            buttonsContainer.setVisibility(View.VISIBLE);
            registerBusinessPartnerButton.setVisibility(View.GONE);
        } else {
            businessPartnersSpinner.setVisibility(View.GONE);
            buttonsContainer.setVisibility(View.GONE);
            registerBusinessPartnerButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mUser);
        outState.putParcelable(STATE_CURRENT_PRODUCT, mProduct);
        super.onSaveInstanceState(outState);
    }
}
