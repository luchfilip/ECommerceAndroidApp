package com.smartbuilders.smartsales.salesforcesystem;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.SalesOrdersListActivity;
import com.smartbuilders.smartsales.ecommerce.businessRules.SalesOrderSalesManBR;
import com.smartbuilders.smartsales.ecommerce.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.data.CurrencyDB;
import com.smartbuilders.smartsales.ecommerce.data.SalesOrderLineDB;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartnerAddress;
import com.smartbuilders.smartsales.ecommerce.model.Currency;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.smartsales.ecommerce.view.DatePickerFragment;
import com.smartbuilders.synchronizer.ids.model.User;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShoppingSaleFinalizeOptionsFragment extends Fragment implements DatePickerFragment.Callback {

    private static final String STATE_VALID_TO = "STATE_VALID_TO";
    private static final String STATE_SELECTED_BUSINESS_PARTNER_ADDRESS_ID = "state_selected_business_partner_address_id";

    private User mUser;
    private ArrayList<SalesOrderLine> mSalesOrderLines;
    private EditText mValidToEditText;
    private String mValidToText;
    private ProgressDialog waitPlease;
    private Spinner mBusinessPartnersAddressesSpinner;
    private int mSelectedBusinessPartnerAddressId;

    public ShoppingSaleFinalizeOptionsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_shopping_sale_finalize_options, container, false);

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState != null) {
                        if(savedInstanceState.containsKey(STATE_VALID_TO)) {
                            mValidToText = savedInstanceState.getString(STATE_VALID_TO);
                        }
                        if(savedInstanceState.containsKey(STATE_SELECTED_BUSINESS_PARTNER_ADDRESS_ID)){
                            mSelectedBusinessPartnerAddressId = savedInstanceState.getInt(STATE_SELECTED_BUSINESS_PARTNER_ADDRESS_ID);
                        }
                    }

                    mUser = Utils.getCurrentUser(getContext());

                    mSalesOrderLines = (new SalesOrderLineDB(getContext(), mUser)).getShoppingSale();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mBusinessPartnersAddressesSpinner = (Spinner) rootView
                                        .findViewById(R.id.business_partner_delivery_addresses_spinner);

                                try {
                                    final BusinessPartner businessPartner = (new BusinessPartnerDB(getContext(), mUser))
                                            .getBusinessPartnerById(Utils.getAppCurrentBusinessPartnerId(getContext(), mUser));
                                    if (businessPartner!=null) {
                                        ((TextView) rootView.findViewById(R.id.business_partner_name_tv))
                                                .setText(getString(R.string.business_partner_name_detail, businessPartner.getName()));

                                        /**************************/
                                        List<String> spinnerArray =  new ArrayList<>();
                                        int index = 0;
                                        int selectedIndex = 0;
                                        for (BusinessPartnerAddress businessPartnerAddress : businessPartner.getAddresses()) {
                                            if(businessPartnerAddress.getId() == mSelectedBusinessPartnerAddressId){
                                                selectedIndex = index;
                                            }
                                            spinnerArray.add(businessPartnerAddress.getAddress());
                                            index++;
                                        }

                                        ArrayAdapter<String> adapter =
                                                new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);

                                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        mBusinessPartnersAddressesSpinner.setAdapter(adapter);
                                        mBusinessPartnersAddressesSpinner.setSelection(selectedIndex);

                                        mBusinessPartnersAddressesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                mSelectedBusinessPartnerAddressId = businessPartner.getAddresses().get(position).getId();
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) { }
                                        });
                                        /**************************/
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                mValidToEditText = (EditText) rootView.findViewById(R.id.valid_to_editText);
                                mValidToEditText.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Date validTo = null;
                                        try {
                                            validTo = DateFormat.getDateInstance(DateFormat.MEDIUM,
                                                    new Locale("es","VE")).parse(mValidToEditText.getText().toString());
                                        } catch (ParseException e){
                                            // do nothing
                                        }
                                        catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        DialogFragment dialogFragment = DatePickerFragment.getInstance(validTo);
                                        dialogFragment.setTargetFragment(ShoppingSaleFinalizeOptionsFragment.this, 1);
                                        dialogFragment.show(getFragmentManager(), DatePickerFragment.class.getSimpleName());
                                    }
                                });
                                mValidToEditText.setText(mValidToText);

                                ((TextView) rootView.findViewById(R.id.total_lines))
                                        .setText(getString(R.string.order_lines_number, String.valueOf(mSalesOrderLines.size())));

                                Currency currency = (new CurrencyDB(getContext(), mUser))
                                        .getActiveCurrencyById(Parameter.getDefaultCurrencyId(getContext(), mUser));

                                ((TextView) rootView.findViewById(R.id.subTotalAmount_tv))
                                        .setText(getString(R.string.sales_order_sub_total_amount,
                                                currency!=null ? currency.getName() : "",
                                                SalesOrderSalesManBR.getSubTotalAmountStringFormat(mSalesOrderLines)));
                                ((TextView) rootView.findViewById(R.id.taxesAmount_tv))
                                        .setText(getString(R.string.sales_order_tax_amount,
                                                currency!=null ? currency.getName() : "",
                                                SalesOrderSalesManBR.getTaxAmountStringFormat(mSalesOrderLines)));
                                ((TextView) rootView.findViewById(R.id.totalAmount_tv))
                                        .setText(getString(R.string.sales_order_total_amount,
                                                currency!=null ? currency.getName() : "",
                                                SalesOrderSalesManBR.getTotalAmountStringFormat(mSalesOrderLines)));

                                rootView.findViewById(R.id.proceed_to_checkout_shopping_sale_button)
                                        .setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                new AlertDialog.Builder(getContext())
                                                        .setMessage(R.string.proceed_to_checkout_quotation_question)
                                                        .setPositiveButton(R.string.proceed_to_checkout, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Date validTo = null;
                                                                try {
                                                                    validTo = DateFormat.getDateInstance(DateFormat.MEDIUM,
                                                                            new Locale("es", "VE")).parse(mValidToEditText.getText().toString());
                                                                } catch (ParseException e) {
                                                                    // do nothing
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                                closeSalesOrder(validTo);
                                                            }
                                                        })
                                                        .setNegativeButton(R.string.cancel, null)
                                                        .show();
                                            }
                                        });
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                rootView.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                rootView.findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        }.start();
        return rootView;
    }

    @Override
    public void setDate(String date) {
        mValidToText = date;
        mValidToEditText.setText(date);
    }

    private void closeSalesOrder(final Date validTo){
        lockScreen();
        new Thread() {
            @Override
            public void run() {
                String result = null;
                try {
                    result = SalesOrderSalesManBR.createSalesOrderFromShoppingSale(getContext(), mUser,
                            validTo, mSelectedBusinessPartnerAddressId, mSalesOrderLines);
                } catch (Exception e) {
                    e.printStackTrace();
                    result = e.getMessage();
                } finally {
                    unlockScreen(result);
                }
            }
        }.start();
    }

    private void lockScreen() {
        if (getActivity()!=null) {
            //Se bloquea la rotacion de la pantalla para evitar que se mate a la aplicacion
            Utils.lockScreenOrientation(getActivity());
            if (waitPlease==null || !waitPlease.isShowing()){
                waitPlease = ProgressDialog.show(getContext(), null,
                        getString(R.string.closing_sales_order_wait_please), true, false);
            }
        }
    }

    private void unlockScreen(final String message){
        if(getActivity()!=null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(message!=null){
                        new AlertDialog.Builder(getContext())
                                .setMessage(message)
                                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Utils.unlockScreenOrientation(getActivity());
                                    }
                                })
                                .setCancelable(false)
                                .show();
                        if (waitPlease!=null && waitPlease.isShowing()) {
                            waitPlease.cancel();
                            waitPlease = null;
                        }
                    } else {
                        startActivity(new Intent(getContext(), SalesOrdersListActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP));

                        if (waitPlease!=null && waitPlease.isShowing()) {
                            waitPlease.cancel();
                            waitPlease = null;
                        }
                        if (getActivity()!=null) {
                            getActivity().finish();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_VALID_TO, mValidToText);
        outState.putInt(STATE_SELECTED_BUSINESS_PARTNER_ADDRESS_ID, mSelectedBusinessPartnerAddressId);
        super.onSaveInstanceState(outState);
    }
}
