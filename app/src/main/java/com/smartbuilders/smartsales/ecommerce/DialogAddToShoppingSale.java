package com.smartbuilders.smartsales.ecommerce;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.smartbuilders.ids.model.User;
import com.smartbuilders.ids.model.UserProfile;
import com.smartbuilders.smartsales.ecommerce.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.data.CurrencyDB;
import com.smartbuilders.smartsales.ecommerce.data.SalesOrderLineDB;
import com.smartbuilders.smartsales.ecommerce.data.ProductTaxDB;
import com.smartbuilders.smartsales.ecommerce.data.UserBusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerce.model.Currency;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.ecommerce.model.ProductTax;

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
    private Spinner businessPartnersSpinner;
    private View buttonsContainer;
    private View registerBusinessPartnerButton;
    private ArrayList<BusinessPartner> businessPartners;

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
                .setText(getContext().getString(R.string.availability,
                        mProduct.getDefaultProductPriceAvailability().getAvailability()));

        businessPartnersSpinner = (Spinner) view.findViewById(R.id.business_partners_spinner);
        buttonsContainer = view.findViewById(R.id.buttons_container);
        registerBusinessPartnerButton = view.findViewById(R.id.register_business_partner_button);

        Currency currency = (new CurrencyDB(getContext(), mUser)).getActiveCurrencyById(Parameter.getDefaultCurrencyId(getContext(), mUser));
        ((TextView) view.findViewById(R.id.product_price_label_textView)).setText(currency!=null
                ? getString(R.string.price_currency_label_detail, currency.getName())
                : getString(R.string.price_label));

        ProductTax productTax = (new ProductTaxDB(getContext(), mUser)).getActiveTaxById(Parameter.getDefaultTaxId(getContext(), mUser));
        ((EditText) view.findViewById(R.id.product_tax_editText))
                .setText(productTax !=null ? String.valueOf(productTax.getPercentage()) : "0.0");

        if(mUser!=null && mUser.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
            registerBusinessPartnerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogCreateBusinessPartner();
                }
            });
        }else if(mUser!=null && mUser.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
            registerBusinessPartnerButton.setVisibility(View.GONE);
        }

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
                            throw new Exception(getString(R.string.invalid_qty_requested));
                        }
                        double productPrice = 0;
                        try {
                            productPrice = Double
                                    .valueOf(((EditText) view.findViewById(R.id.product_price_editText)).getText().toString());
                        } catch (Exception e) {
                            //empty
                        }
                        double productTaxPercentage = 0;
                        try {
                            productTaxPercentage = Double
                                    .valueOf(((EditText) view.findViewById(R.id.product_tax_editText)).getText().toString());
                        } catch (Exception e) {
                            //empty
                        }

                        String result = (new SalesOrderLineDB(getContext(), mUser))
                                .addProductToShoppingSale(mProduct.getId(), qtyRequested, productPrice,
                                        productTaxPercentage, PreferenceManager.getDefaultSharedPreferences(getContext())
                                                .getInt(BusinessPartner.CURRENT_APP_BP_ID_SHARED_PREFS_KEY, 0));
                        if(result == null){
                            Toast.makeText(getContext(), R.string.product_moved_to_shopping_sale,
                                    Toast.LENGTH_LONG).show();
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), getString(R.string.invalid_qty_requested), Toast.LENGTH_LONG).show();
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
                        mProduct.getProductCommercialPackage().getUnits(), mProduct.getProductCommercialPackage().getUnitDescription()));
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
        DialogRegisterUserBusinessPartner dialogRegisterUserBusinessPartner =
                DialogRegisterUserBusinessPartner.newInstance(mUser);
        dialogRegisterUserBusinessPartner.setTargetFragment(this, 0);
        dialogRegisterUserBusinessPartner.show(fm, DialogRegisterUserBusinessPartner.class.getSimpleName());
    }

    public void initViews(){
        int appCurrentBusinessPartnerId = 0;
        try{
            appCurrentBusinessPartnerId = PreferenceManager.getDefaultSharedPreferences(getContext())
                    .getInt(BusinessPartner.CURRENT_APP_BP_ID_SHARED_PREFS_KEY, 0);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (mUser!=null && mUser.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID) {
            businessPartners = (new UserBusinessPartnerDB(getContext(), mUser)).getActiveUserBusinessPartners();
        } else if (mUser!=null && mUser.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID) {
            BusinessPartner businessPartner = (new BusinessPartnerDB(getContext(), mUser))
                    .getActiveBusinessPartnerById(appCurrentBusinessPartnerId);
            if (businessPartner!=null) {
                businessPartners = new ArrayList<>();
                businessPartners.add(businessPartner);
            }
        }

        if (businessPartners!=null && !businessPartners.isEmpty()) {
            int index = 0;
            int selectedIndex = 0;
            List<String> spinnerArray =  new ArrayList<>();

            for (BusinessPartner businessPartner : businessPartners) {
                if(businessPartner.getId() == appCurrentBusinessPartnerId){
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
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                    editor.putInt(BusinessPartner.CURRENT_APP_BP_ID_SHARED_PREFS_KEY, businessPartners.get(position).getId());
                    editor.apply();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });
            businessPartnersSpinner.setVisibility(View.VISIBLE);
            buttonsContainer.setVisibility(View.VISIBLE);
            registerBusinessPartnerButton.setVisibility(View.GONE);
        } else {
            if(appCurrentBusinessPartnerId!=0){
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                editor.putInt(BusinessPartner.CURRENT_APP_BP_ID_SHARED_PREFS_KEY, 0);
                editor.apply();
            }

            if (mUser!=null && mUser.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID) {
                businessPartnersSpinner.setVisibility(View.GONE);
                registerBusinessPartnerButton.setVisibility(View.VISIBLE);
            }
            buttonsContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mUser);
        outState.putParcelable(STATE_CURRENT_PRODUCT, mProduct);
        super.onSaveInstanceState(outState);
    }
}
