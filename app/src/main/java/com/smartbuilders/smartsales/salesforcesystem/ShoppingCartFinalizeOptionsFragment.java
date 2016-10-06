package com.smartbuilders.smartsales.salesforcesystem;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerce.OrdersListActivity;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.SalesOrdersListActivity;
import com.smartbuilders.smartsales.ecommerce.businessRules.OrderBR;
import com.smartbuilders.smartsales.ecommerce.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.data.CurrencyDB;
import com.smartbuilders.smartsales.ecommerce.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerce.data.SalesOrderDB;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartnerAddress;
import com.smartbuilders.smartsales.ecommerce.model.Currency;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrder;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShoppingCartFinalizeOptionsFragment extends Fragment {

    private static final String STATE_SALES_ORDER_ID = "state_sales_order_id";
    private static final String STATE_BUSINESS_PARTNER_ID = "state_business_partner_id";
    private static final String STATE_ORDER_LINES = "state_order_lines";
    private static final String STATE_SELECTED_BUSINESS_PARTNER_ADDRESS_ID = "state_selected_business_partner_address_id";

    private User mUser;
    private int mSalesOrderId;
    private int mBusinessPartnerId;
    private ArrayList<OrderLine> mOrderLines;
    private ProgressDialog waitPlease;
    private boolean mIsShoppingCart = true;
    private Spinner mBusinessPartnersAddressesSpinner;
    private int mSelectedBusinessPartnerAddressId;

    public ShoppingCartFinalizeOptionsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_shopping_cart_finalize_options, container, false);

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState != null) {
                        if(savedInstanceState.containsKey(STATE_SALES_ORDER_ID)
                                && savedInstanceState.containsKey(STATE_BUSINESS_PARTNER_ID)
                                && savedInstanceState.containsKey(STATE_ORDER_LINES)){
                            mIsShoppingCart = false;
                            mSalesOrderId = savedInstanceState.getInt(STATE_SALES_ORDER_ID);
                            mBusinessPartnerId = savedInstanceState.getInt(STATE_BUSINESS_PARTNER_ID);
                            mOrderLines = savedInstanceState.getParcelableArrayList(STATE_ORDER_LINES);
                        }
                        if(savedInstanceState.containsKey(STATE_SELECTED_BUSINESS_PARTNER_ADDRESS_ID)){
                            mSelectedBusinessPartnerAddressId = savedInstanceState.getInt(STATE_SELECTED_BUSINESS_PARTNER_ADDRESS_ID);
                        }
                    } else  if(getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null) {
                        if(getActivity().getIntent().getExtras().containsKey(ShoppingCartFinalizeOptionsActivity.KEY_SALES_ORDER_ID)
                                && getActivity().getIntent().getExtras().containsKey(ShoppingCartFinalizeOptionsActivity.KEY_BUSINESS_PARTNER_ID)){
                            mIsShoppingCart = false;
                            mSalesOrderId = getActivity().getIntent().getExtras()
                                    .getInt(ShoppingCartFinalizeOptionsActivity.KEY_SALES_ORDER_ID);
                            mBusinessPartnerId = getActivity().getIntent().getExtras()
                                    .getInt(ShoppingCartFinalizeOptionsActivity.KEY_BUSINESS_PARTNER_ID);

                            if(getActivity().getIntent().getExtras().containsKey(ShoppingCartFinalizeOptionsActivity.KEY_ORDER_LINES)){
                                mOrderLines = getActivity().getIntent().getExtras()
                                        .getParcelableArrayList(ShoppingCartFinalizeOptionsActivity.KEY_ORDER_LINES);
                            }
                        }
                    }
                    mUser = Utils.getCurrentUser(getContext());

                    if(mOrderLines==null){
                        mOrderLines = (new OrderLineDB(getContext(), mUser)).getActiveOrderLinesFromShoppingCart();
                    }
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
                                            .getActiveBusinessPartnerById(Utils.getAppCurrentBusinessPartnerId(getContext(), mUser));
                                    if (businessPartner!=null) {
                                        ((TextView) rootView.findViewById(R.id.business_partner_name_textView))
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

                                if (mSalesOrderId!=0) {
                                    SalesOrder salesOrder = (new SalesOrderDB(getContext(), mUser)).getActiveSalesOrderById(mSalesOrderId);
                                    if (salesOrder!=null && salesOrder.getBusinessPartner()!=null) {
                                        ((TextView) rootView.findViewById(R.id.sales_order_number_textView))
                                                .setText(getString(R.string.sales_order_number, salesOrder.getSalesOrderNumber()));
                                        rootView.findViewById(R.id.sales_order_number_textView).setVisibility(View.VISIBLE);
                                    }
                                }

                                ((TextView) rootView.findViewById(R.id.total_lines))
                                        .setText(getString(R.string.order_lines_number, String.valueOf(mOrderLines.size())));

                                Currency currency = (new CurrencyDB(getContext(), mUser))
                                        .getActiveCurrencyById(Parameter.getDefaultCurrencyId(getContext(), mUser));

                                ((TextView) rootView.findViewById(R.id.subTotalAmount_tv))
                                        .setText(getString(R.string.order_sub_total_amount,
                                                currency!=null ? currency.getName() : "",
                                                OrderBR.getSubTotalAmountStringFormat(mOrderLines)));
                                ((TextView) rootView.findViewById(R.id.taxesAmount_tv))
                                        .setText(getString(R.string.order_tax_amount,
                                                currency!=null ? currency.getName() : "",
                                                OrderBR.getTaxAmountStringFormat(mOrderLines)));
                                ((TextView) rootView.findViewById(R.id.totalAmount_tv))
                                        .setText(getString(R.string.order_total_amount,
                                                currency!=null ? currency.getName() : "",
                                                OrderBR.getTotalAmountStringFormat(mOrderLines)));

                                rootView.findViewById(R.id.proceed_to_checkout_button)
                                        .setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                new AlertDialog.Builder(getContext())
                                                        .setMessage(R.string.proceed_to_checkout_question)
                                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                closeOrder();
                                                            }
                                                        })
                                                        .setNegativeButton(R.string.no, null)
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

    private void closeOrder(){
        lockScreen();
        new Thread() {
            @Override
            public void run() {
                String result = null;
                try {
                    if (mSalesOrderId > 0) {
                        result = OrderBR.createOrderFromOrderLines(getContext(), mUser, mSalesOrderId, mSelectedBusinessPartnerAddressId, mOrderLines);
                    } else {
                        result = OrderBR.createOrderFromShoppingCart(getContext(), mUser, mSelectedBusinessPartnerAddressId);
                    }
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
                        getString(R.string.closing_order_wait_please), true, false);
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
                        if(mIsShoppingCart){
                            startActivity(new Intent(getContext(), OrdersListActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT ));
                        }else{
                            startActivity(new Intent(getContext(), SalesOrdersListActivity.class)
                                    .putExtra(SalesOrdersListActivity.KEY_CURRENT_TAB_SELECTED, 1)
                                    .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT ));
                        }
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
        if (!mIsShoppingCart) {
            outState.putInt(STATE_SALES_ORDER_ID, mSalesOrderId);
            outState.putInt(STATE_BUSINESS_PARTNER_ID, mBusinessPartnerId);
            outState.putParcelableArrayList(STATE_ORDER_LINES, mOrderLines);
        }
        outState.putInt(STATE_SELECTED_BUSINESS_PARTNER_ADDRESS_ID, mSelectedBusinessPartnerAddressId);
        super.onSaveInstanceState(outState);
    }
}
