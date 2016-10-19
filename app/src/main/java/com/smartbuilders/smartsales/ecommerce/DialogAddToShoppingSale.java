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

import com.smartbuilders.smartsales.ecommerce.businessRules.SalesOrderLineBR;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.model.UserProfile;
import com.smartbuilders.smartsales.ecommerce.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.data.CurrencyDB;
import com.smartbuilders.smartsales.ecommerce.data.SalesOrderLineDB;
import com.smartbuilders.smartsales.ecommerce.data.UserBusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerce.model.Currency;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.model.Product;

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
    private ArrayList<BusinessPartner> businessPartners;
    private int mSalesOrderLineId;
    private View buttonsContainer;
    private View registerBusinessPartnerButton;
    private View addToShoppingSaleButton;
    private View updateShoppingSaleButton;
    private EditText productPriceEditText;
    private EditText productTaxPercentageEditText;
    private EditText qtyRequestedEditText;

    public DialogAddToShoppingSale() {
        // Empty constructor required for DialogFragment
    }

    public static DialogAddToShoppingSale newInstance(Product product, User user){
        DialogAddToShoppingSale dialogAddToShoppingSale = new DialogAddToShoppingSale();
        dialogAddToShoppingSale.mProduct = product;
        dialogAddToShoppingSale.mUser = user;
        return dialogAddToShoppingSale;
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
                .setText(getString(R.string.availability,
                        mProduct.getDefaultProductPriceAvailability().getAvailability()));

        businessPartnersSpinner = (Spinner) view.findViewById(R.id.business_partners_spinner);
        buttonsContainer = view.findViewById(R.id.buttons_container);
        registerBusinessPartnerButton = view.findViewById(R.id.register_business_partner_button);
        addToShoppingSaleButton = view.findViewById(R.id.add_to_shopping_sale_button);
        updateShoppingSaleButton = view.findViewById(R.id.update_shopping_sale_button);
        productPriceEditText = (EditText) view.findViewById(R.id.product_price_editText);
        productTaxPercentageEditText = (EditText) view.findViewById(R.id.product_tax_editText);
        qtyRequestedEditText = (EditText) view.findViewById(R.id.qty_requested_editText);

        Currency currency = (new CurrencyDB(getContext(), mUser)).getActiveCurrencyById(Parameter.getDefaultCurrencyId(getContext(), mUser));
        ((TextView) view.findViewById(R.id.product_price_label_textView)).setText(currency!=null
                ? getString(R.string.price_currency_label_detail, currency.getName())
                : getString(R.string.price_label));

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

        addToShoppingSaleButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String result = (new SalesOrderLineDB(getContext(), mUser))
                                .addSalesOrderLinesToShoppingSale(getSalesOrderLine());
                        if(result == null){
                            Toast.makeText(getContext(), R.string.product_moved_to_shopping_sale,
                                    Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );

        updateShoppingSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String result = (new SalesOrderLineDB(getContext(), mUser))
                            .updateSalesOrderLine(getSalesOrderLine());
                    if(result == null){
                        Toast.makeText(getContext(), R.string.shopping_sale_updated_successfully,
                                Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(view.findViewById(R.id.product_commercial_package) != null){
            if(mProduct.getProductCommercialPackage()!=null
                    && !TextUtils.isEmpty(mProduct.getProductCommercialPackage().getUnitDescription())){
                ((TextView) view.findViewById(R.id.product_commercial_package)).setText(getString(R.string.commercial_package_label_detail,
                        mProduct.getProductCommercialPackage().getUnitDescription(), mProduct.getProductCommercialPackage().getUnits()));
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
            businessPartners = (new UserBusinessPartnerDB(getContext(), mUser)).getUserBusinessPartners();
        } else if (mUser!=null && mUser.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID) {
            BusinessPartner businessPartner = (new BusinessPartnerDB(getContext(), mUser))
                    .getBusinessPartnerById(appCurrentBusinessPartnerId);
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
                spinnerArray.add(businessPartner.getName() + " - " +
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
                    loadFields(businessPartners.get(position).getId());
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

    private SalesOrderLine getSalesOrderLine() throws Exception {
        int qtyRequested = 0;
        try {
            qtyRequested = Integer.valueOf(qtyRequestedEditText.getText().toString());
        } catch (NumberFormatException e) {
            //empty
        }
        if (qtyRequested<=0) {
            throw new Exception(getString(R.string.invalid_qty_requested));
        }

        /**********************************************************/
        Product product = new Product();
        product.setId(mProduct.getId());

        try {
            product.getDefaultProductPriceAvailability().setPrice(Float.valueOf(productPriceEditText.getText().toString()));
        } catch (NumberFormatException e) {
            throw new Exception(getString(R.string.invalid_product_price));
        }

        try {
            product.getProductTax().setPercentage(Float.valueOf(productTaxPercentageEditText.getText().toString()));
            product.getDefaultProductPriceAvailability().setTax(product.getDefaultProductPriceAvailability().getPrice()
                    * (product.getProductTax().getPercentage() / 100));
        } catch (NumberFormatException e) {
            throw new Exception(getString(R.string.invalid_product_tax_percentage));
        }

        product.getDefaultProductPriceAvailability().setTotalPrice(product.getDefaultProductPriceAvailability().getPrice()
                + product.getDefaultProductPriceAvailability().getTax());
        /**********************************************************/

        SalesOrderLine salesOrderLine = new SalesOrderLine();
        salesOrderLine.setId(mSalesOrderLineId);
        SalesOrderLineBR.fillSalesOrderLine(qtyRequested, product, salesOrderLine);
        salesOrderLine.setBusinessPartnerId(businessPartners.get(businessPartnersSpinner.getSelectedItemPosition()).getId());
        return salesOrderLine;
    }

    private void loadFields(int businessPartnerId) {
        SalesOrderLine salesOrderLine = (new SalesOrderLineDB(getContext(), mUser))
                .getSalesOrderLineFromShoppingSales(mProduct.getId(), businessPartnerId);
        if (salesOrderLine != null) {
            mSalesOrderLineId = salesOrderLine.getId();
            productPriceEditText.setText(String.valueOf(salesOrderLine.getProductPrice()));
            productTaxPercentageEditText.setText(String.valueOf(salesOrderLine.getProductTaxPercentage()));
            qtyRequestedEditText.setText(String.valueOf(salesOrderLine.getQuantityOrdered()));
            addToShoppingSaleButton.setVisibility(View.GONE);
            updateShoppingSaleButton.setVisibility(View.VISIBLE);
        } else {
            mSalesOrderLineId = 0;
            productPriceEditText.setText(null);
            productTaxPercentageEditText.setText(String.valueOf(mProduct.getProductTax().getPercentage()));
            qtyRequestedEditText.setText(null);
            updateShoppingSaleButton.setVisibility(View.GONE);
            addToShoppingSaleButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mUser);
        outState.putParcelable(STATE_CURRENT_PRODUCT, mProduct);
        super.onSaveInstanceState(outState);
    }
}
