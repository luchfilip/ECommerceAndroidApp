package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.ShoppingSaleAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.businessRules.SalesOrderBR;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.SalesOrderDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.SalesOrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.SalesOrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShoppingSaleFragment extends Fragment implements ShoppingSaleAdapter.Callback {

    private static final String STATE_BUSINESS_PARTNER_ID = "STATE_BUSINESS_PARTNER_ID";
    private static final String STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION = "STATE_LISTVIEW_CURRENT_FIRST_POSITION";

    private User mCurrentUser;
    private ArrayList<SalesOrderLine> mSalesOrderLines;
    private ShoppingSaleAdapter mShoppingSaleAdapter;
    private View mBlankScreenView;
    private View mShoppingSaleItemsListView;
    private View mShoppingSaleDataView;
    private TextView mTotalLines;
    private TextView mSubTotalAmount;
    private TextView mTaxAmount;
    private TextView mTotalAmount;
    private int mRecyclerViewCurrentFirstPosition;
    private int mCurrentBusinessPartnerId;
    private ProgressDialog waitPlease;

    public ShoppingSaleFragment() {
    }

    public interface Callback {
        void reloadShoppingSalesList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_shopping_sale, container, false);

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState!=null) {
                        if(savedInstanceState.containsKey(STATE_BUSINESS_PARTNER_ID)) {
                            mCurrentBusinessPartnerId = savedInstanceState.getInt(STATE_BUSINESS_PARTNER_ID);
                        }
                        if(savedInstanceState.containsKey(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION)) {
                            mRecyclerViewCurrentFirstPosition = savedInstanceState.getInt(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION);
                        }
                    }

                    if(getArguments()!=null){
                        if(getArguments().containsKey(ShoppingSaleActivity.KEY_BUSINESS_PARTNER_ID)){
                            mCurrentBusinessPartnerId = getArguments().getInt(ShoppingSaleActivity.KEY_BUSINESS_PARTNER_ID);
                        }
                    }else if(getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null){
                        if(getActivity().getIntent().getExtras().containsKey(ShoppingSaleActivity.KEY_BUSINESS_PARTNER_ID)) {
                            mCurrentBusinessPartnerId = getActivity().getIntent().getExtras()
                                    .getInt(ShoppingSaleActivity.KEY_BUSINESS_PARTNER_ID);
                        }
                    }
                    mCurrentUser = Utils.getCurrentUser(getContext());

                    mSalesOrderLines = (new SalesOrderLineDB(getContext(), mCurrentUser)).getShoppingSale(mCurrentBusinessPartnerId);
                    mShoppingSaleAdapter = new ShoppingSaleAdapter(getContext(), ShoppingSaleFragment.this, mSalesOrderLines, mCurrentUser);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.shoppingSale_items_list);
                                // use this setting to improve performance if you know that changes
                                // in content do not change the layout size of the RecyclerView
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerView.setAdapter(mShoppingSaleAdapter);

                                if (mRecyclerViewCurrentFirstPosition!=0) {
                                    recyclerView.scrollToPosition(mRecyclerViewCurrentFirstPosition);
                                }

                                view.findViewById(R.id.proceed_to_checkout_shopping_sale_button)
                                        .setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                new AlertDialog.Builder(getContext())
                                                        .setMessage(R.string.proceed_to_checkout_quoatation_question)
                                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                closeSalesOrder();
                                                            }
                                                        })
                                                        .setNegativeButton(android.R.string.no, null)
                                                        .show();
                                            }
                                        });

                                mTotalLines = (TextView) view.findViewById(R.id.total_lines);
                                mSubTotalAmount = (TextView) view.findViewById(R.id.subTotalAmount_tv);
                                mTaxAmount = (TextView) view.findViewById(R.id.taxesAmount_tv);
                                mTotalAmount = (TextView) view.findViewById(R.id.totalAmount_tv);

                                mBlankScreenView = view.findViewById(R.id.company_logo_name);
                                mShoppingSaleItemsListView = view.findViewById(R.id.shoppingSale_items_list);
                                mShoppingSaleDataView = view.findViewById(R.id.shoppingSale_data_linearLayout);

                                fillFields();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                view.findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        }.start();
        return view;
    }

    private void closeSalesOrder(){
        lockScreen();
        new Thread() {
            @Override
            public void run() {
                String result = null;
                try {
                    result = new SalesOrderDB(getContext(), mCurrentUser)
                            .createSalesOrderFromShoppingSale(mCurrentBusinessPartnerId);
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
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            } else {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }
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
                                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                                    }
                                })
                                .setCancelable(false)
                                .show();
                        if (waitPlease!=null && waitPlease.isShowing()) {
                            waitPlease.cancel();
                            waitPlease = null;
                        }
                    } else {
                        if (waitPlease!=null && waitPlease.isShowing()) {
                            waitPlease.cancel();
                            waitPlease = null;
                        }
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                        Intent intent = new Intent(getContext(), SalesOrderDetailActivity.class);
                        intent.putExtra(SalesOrderDetailActivity.KEY_SALES_ORDER, new SalesOrderDB(getContext(), mCurrentUser)
                                .getLastFinalizedSalesOrder());
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            });
        }
    }

    public void fillFields(){
        if (mSalesOrderLines==null || mSalesOrderLines.size()==0) {
            mBlankScreenView.setVisibility(View.VISIBLE);
            mShoppingSaleItemsListView.setVisibility(View.GONE);
            mShoppingSaleDataView.setVisibility(View.GONE);
        } else {
            mTotalLines.setText(getString(R.string.order_lines_number,
                    String.valueOf(mSalesOrderLines.size())));
            mSubTotalAmount.setText(getString(R.string.order_sub_total_amount,
                    String.valueOf(SalesOrderBR.getSubTotalAmount(mSalesOrderLines))));
            mTaxAmount.setText(getString(R.string.order_tax_amount,
                    String.valueOf(SalesOrderBR.getTaxAmount(mSalesOrderLines))));
            mTotalAmount.setText(getString(R.string.order_total_amount,
                    String.valueOf(SalesOrderBR.getTotalAmount(mSalesOrderLines))));
        }
    }

    @Override
    public void updateSalesOrderLine(SalesOrderLine orderLine, int focus) {
        DialogUpdateSalesOrderLine dialogUpdateSalesOrderLine =
                DialogUpdateSalesOrderLine.newInstance(orderLine, mCurrentUser, focus);
        dialogUpdateSalesOrderLine.setTargetFragment(this, 0);
        dialogUpdateSalesOrderLine.show(getActivity().getSupportFragmentManager(),
                DialogUpdateSalesOrderLine.class.getSimpleName());
    }

    @Override
    public void reloadShoppingSalesList() {
        //manda a refrescar la lista de la izquierda cuando se esta en pantalla dividida
        ((Callback) getActivity()).reloadShoppingSalesList();
    }

    public void reloadShoppingSale(){
        mSalesOrderLines = (new SalesOrderLineDB(getActivity(), mCurrentUser)).getShoppingSale(mCurrentBusinessPartnerId);
        mShoppingSaleAdapter.setData(mSalesOrderLines);
        fillFields();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_BUSINESS_PARTNER_ID, mCurrentBusinessPartnerId);
        outState.putInt(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION, mRecyclerViewCurrentFirstPosition);
        super.onSaveInstanceState(outState);
    }
}
