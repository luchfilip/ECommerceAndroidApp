package com.smartbuilders.smartsales.ecommerce;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
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
import com.smartbuilders.smartsales.ecommerce.model.UserBusinessPartner;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.model.UserProfile;
import com.smartbuilders.smartsales.ecommerce.data.SalesOrderLineDB;
import com.smartbuilders.smartsales.ecommerce.data.UserBusinessPartnerDB;
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
    private View mBusinessPartnersTableRow;
    private View mBusinessPartnersSpinnerContainer;
    private Spinner mUseBusinessPartnersSpinner;
    private ArrayList<UserBusinessPartner> mUserBusinessPartners;
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
                        mProduct.getProductPriceAvailability().getAvailability()));

        mBusinessPartnersTableRow = view.findViewById(R.id.business_partners_tableRow);
        mBusinessPartnersSpinnerContainer = view.findViewById(R.id.business_partners_spinner_container);
        mUseBusinessPartnersSpinner = (Spinner) view.findViewById(R.id.business_partners_spinner);
        buttonsContainer = view.findViewById(R.id.buttons_container);
        registerBusinessPartnerButton = view.findViewById(R.id.register_business_partner_button);
        addToShoppingSaleButton = view.findViewById(R.id.add_to_shopping_sale_button);
        updateShoppingSaleButton = view.findViewById(R.id.update_shopping_sale_button);
        productPriceEditText = (EditText) view.findViewById(R.id.product_price_editText);
        productTaxPercentageEditText = (EditText) view.findViewById(R.id.product_tax_editText);
        qtyRequestedEditText = (EditText) view.findViewById(R.id.qty_requested_editText);

        if (mUser!=null) {
            if (mUser.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID) {
                registerBusinessPartnerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialogCreateBusinessPartner();
                    }
                });
            } else if (mUser.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID) {
                registerBusinessPartnerButton.setVisibility(View.GONE);
            }
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
                ((TextView) view.findViewById(R.id.product_commercial_package)).setText(Html.fromHtml(getString(R.string.commercial_package_label_detail_html,
                        mProduct.getProductCommercialPackage().getUnitDescription(), mProduct.getProductCommercialPackage().getUnits())));
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
        if (mUser!=null) {
            if (mUser.getUserProfileId()==UserProfile.BUSINESS_PARTNER_PROFILE_ID) {
                int appCurrentUserBusinessPartnerId = 0;
                try{
                    appCurrentUserBusinessPartnerId = PreferenceManager.getDefaultSharedPreferences(getContext())
                            .getInt(UserBusinessPartner.CURRENT_APP_UBP_ID_SHARED_PREFS_KEY, 0);
                }catch (Exception e){
                    e.printStackTrace();
                }

                mUserBusinessPartners = (new UserBusinessPartnerDB(getContext(), mUser)).getUserBusinessPartners();
                if (mUserBusinessPartners != null && !mUserBusinessPartners.isEmpty()) {
                    int index = 0;
                    int selectedIndex = 0;
                    List<String> spinnerArray = new ArrayList<>();

                    for (UserBusinessPartner businessPartner : mUserBusinessPartners) {
                        if (businessPartner.getId() == appCurrentUserBusinessPartnerId) {
                            selectedIndex = index;
                        }
                        spinnerArray.add(businessPartner.getName() + " - " +
                                getString(R.string.tax_id, businessPartner.getTaxId()));
                        index++;
                    }
                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mUseBusinessPartnersSpinner.setAdapter(adapter);
                    mUseBusinessPartnersSpinner.setSelection(selectedIndex);

                    mUseBusinessPartnersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                            editor.putInt(UserBusinessPartner.CURRENT_APP_UBP_ID_SHARED_PREFS_KEY, mUserBusinessPartners.get(position).getId());
                            editor.apply();
                            loadFields(mUserBusinessPartners.get(position).getId());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    mUseBusinessPartnersSpinner.setVisibility(View.VISIBLE);
                    mBusinessPartnersSpinnerContainer.setVisibility(View.VISIBLE);
                    buttonsContainer.setVisibility(View.VISIBLE);
                    registerBusinessPartnerButton.setVisibility(View.GONE);
                } else {
                    if (appCurrentUserBusinessPartnerId != 0) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                        editor.putInt(UserBusinessPartner.CURRENT_APP_UBP_ID_SHARED_PREFS_KEY, 0);
                        editor.apply();
                    }

                    mBusinessPartnersSpinnerContainer.setVisibility(View.GONE);
                    mUseBusinessPartnersSpinner.setVisibility(View.GONE);
                    registerBusinessPartnerButton.setVisibility(View.VISIBLE);
                    buttonsContainer.setVisibility(View.GONE);
                }
            } else if (mUser.getUserProfileId()==UserProfile.SALES_MAN_PROFILE_ID) {
                mBusinessPartnersTableRow.setVisibility(View.GONE);
                buttonsContainer.setVisibility(View.VISIBLE);
                try {
                    loadFields(Utils.getAppCurrentBusinessPartnerId(getContext(), mUser));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
            product.getProductPriceAvailability().setPrice(Float.valueOf(productPriceEditText.getText().toString()));
        } catch (NumberFormatException e) {
            throw new Exception(getString(R.string.invalid_product_price));
        }

        try {
            product.getProductTax().setPercentage(Float.valueOf(productTaxPercentageEditText.getText().toString()));
            product.getProductPriceAvailability().setTax(product.getProductPriceAvailability().getPrice()
                    * (product.getProductTax().getPercentage() / 100));
        } catch (NumberFormatException e) {
            throw new Exception(getString(R.string.invalid_product_tax_percentage));
        }

        product.getProductPriceAvailability().setTotalPrice(product.getProductPriceAvailability().getPrice()
                + product.getProductPriceAvailability().getTax());
        /**********************************************************/

        SalesOrderLine salesOrderLine = new SalesOrderLine();
        salesOrderLine.setId(mSalesOrderLineId);
        SalesOrderLineBR.fillSalesOrderLine(qtyRequested, product, salesOrderLine);
        if (mUser!=null) {
            if (mUser.getUserProfileId()==UserProfile.BUSINESS_PARTNER_PROFILE_ID) {
                salesOrderLine.setBusinessPartnerId(mUserBusinessPartners.get(mUseBusinessPartnersSpinner.getSelectedItemPosition()).getId());
            } else if (mUser.getUserProfileId()==UserProfile.SALES_MAN_PROFILE_ID) {
                salesOrderLine.setBusinessPartnerId(Utils.getAppCurrentBusinessPartnerId(getContext(), mUser));
            }
        }
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
