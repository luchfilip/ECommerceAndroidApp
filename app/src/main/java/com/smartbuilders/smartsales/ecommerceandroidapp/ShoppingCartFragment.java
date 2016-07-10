package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.model.UserProfile;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.ShoppingCartAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.SalesOrderDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.UserBusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.SalesOrder;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShoppingCartFragment extends Fragment implements ShoppingCartAdapter.Callback,
        DialogUpdateShoppingCartQtyOrdered.Callback {

    private static final String STATE_SALES_ORDER_ID = "state_sales_order_id";
    private static final String STATE_BUSINESS_PARTNER_ID = "state_business_partner_id";
    private static final String STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION =
            "STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION";
    private static final String STATE_ORDER_LINES = "STATE_ORDER_LINES";

    private boolean mIsInitialLoad;
    private User mUser;
    private int mSalesOrderId;
    private int mBusinessPartnerId;
    private ShoppingCartAdapter mShoppingCartAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private int mRecyclerViewCurrentFirstPosition;
    private ArrayList<OrderLine> mOrderLines;
    private TextView totalLines;
    private ProgressDialog waitPlease;
    private View mainLayout;
    private View mBlankScreenView;
    private boolean mIsShoppingCart = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);
        mIsInitialLoad = true;

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState != null) {
                        if(savedInstanceState.containsKey(STATE_SALES_ORDER_ID)
                                && savedInstanceState.containsKey(STATE_BUSINESS_PARTNER_ID)){
                            mIsShoppingCart = false;
                            mSalesOrderId = savedInstanceState.getInt(STATE_SALES_ORDER_ID);
                            mBusinessPartnerId = savedInstanceState.getInt(STATE_BUSINESS_PARTNER_ID);
                        }
                        if(savedInstanceState.containsKey(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION)){
                            mRecyclerViewCurrentFirstPosition = savedInstanceState.getInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION);
                        }
                        if(savedInstanceState.containsKey(STATE_ORDER_LINES)){
                            mOrderLines = savedInstanceState.getParcelableArrayList(STATE_ORDER_LINES);
                        }
                    } else  if(getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null) {
                        if(getActivity().getIntent().getExtras().containsKey(ShoppingCartActivity.KEY_SALES_ORDER_ID)
                                && getActivity().getIntent().getExtras().containsKey(ShoppingCartActivity.KEY_BUSINESS_PARTNER_ID)){
                            mIsShoppingCart = false;
                            mSalesOrderId = getActivity().getIntent().getExtras()
                                    .getInt(ShoppingCartActivity.KEY_SALES_ORDER_ID);
                            mBusinessPartnerId = getActivity().getIntent().getExtras()
                                    .getInt(ShoppingCartActivity.KEY_BUSINESS_PARTNER_ID);
                        }
                    }

                    mUser = Utils.getCurrentUser(getContext());

                    if (mIsShoppingCart) {
                        mOrderLines = (new OrderLineDB(getContext(), mUser)).getShoppingCart();
                    }else {
                        if(mOrderLines==null){
                            mOrderLines = (new OrderLineDB(getContext(), mUser)).getOrderLinesBySalesOrderId(mSalesOrderId);
                        }
                    }
                    mShoppingCartAdapter = new ShoppingCartAdapter(getContext(),
                            ShoppingCartFragment.this, mOrderLines, mIsShoppingCart, mUser);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mBlankScreenView = view.findViewById(R.id.company_logo_name);
                                mainLayout = view.findViewById(R.id.main_layout);

                                if(mUser!=null
                                        && mUser.getUserProfileId()==UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                                    if(!mIsShoppingCart){
                                        SalesOrder salesOrder = (new SalesOrderDB(getContext(), mUser)).getActiveSalesOrderById(mSalesOrderId);
                                        if(salesOrder!=null){
                                            BusinessPartner businessPartner = (new UserBusinessPartnerDB(getContext(), mUser))
                                                    .getActiveUserBusinessPartnerById(salesOrder.getBusinessPartnerId());
                                            if(businessPartner!=null){
                                                ((TextView) view.findViewById(R.id.business_partner_commercial_name_textView))
                                                        .setText(getString(R.string.business_partner_detail, businessPartner.getCommercialName()));
                                                view.findViewById(R.id.business_partner_commercial_name_textView).setVisibility(View.VISIBLE);

                                                ((TextView) view.findViewById(R.id.sales_order_number_textView))
                                                        .setText(getString(R.string.sales_order_number, salesOrder.getSalesOrderNumber()));
                                                view.findViewById(R.id.sales_order_number_textView).setVisibility(View.VISIBLE);

                                                view.findViewById(R.id.sales_order_info_separator).setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                }else if(mUser!=null
                                        && mUser.getUserProfileId()==UserProfile.SALES_MAN_PROFILE_ID){
                                    BusinessPartner businessPartner = (new UserBusinessPartnerDB(getContext(), mUser))
                                            .getActiveUserBusinessPartnerById(Utils.getAppCurrentBusinessPartnerId(getContext(), mUser));
                                    if(businessPartner!=null){
                                        ((TextView) view.findViewById(R.id.business_partner_commercial_name_textView))
                                                .setText(getString(R.string.business_partner_detail, businessPartner.getCommercialName()));
                                        view.findViewById(R.id.business_partner_commercial_name_textView).setVisibility(View.VISIBLE);

                                        view.findViewById(R.id.sales_order_info_separator).setVisibility(View.VISIBLE);
                                    }
                                }

                                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.shoppingCart_items_list);
                                // use this setting to improve performance if you know that changes
                                // in content do not change the layout size of the RecyclerView
                                recyclerView.setHasFixedSize(true);
                                mLinearLayoutManager = new LinearLayoutManager(getContext());
                                recyclerView.setLayoutManager(mLinearLayoutManager);
                                recyclerView.setAdapter(mShoppingCartAdapter);

                                if (mRecyclerViewCurrentFirstPosition!=0) {
                                    recyclerView.scrollToPosition(mRecyclerViewCurrentFirstPosition);
                                }

                                view.findViewById(R.id.proceed_to_checkout_button)
                                        .setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                new AlertDialog.Builder(getContext())
                                                        .setMessage(R.string.proceed_to_checkout_question)
                                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                closeOrder();
                                                            }
                                                        })
                                                        .setNegativeButton(android.R.string.no, null)
                                                        .show();
                                            }
                                        });
                                totalLines = (TextView) view.findViewById(R.id.total_lines);
                                totalLines.setText(getString(R.string.order_lines_number, String.valueOf(mOrderLines.size())));
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                if (mOrderLines!=null && !mOrderLines.isEmpty()) {
                                    mainLayout.setVisibility(View.VISIBLE);
                                } else {
                                    mBlankScreenView.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
                }
            }
        }.start();
        return view;
    }

    @Override
    public void onStart() {
        if(mIsInitialLoad){
            mIsInitialLoad = false;
        }else{
            reloadShoppingCart();
        }
        super.onStart();
    }

    private void closeOrder(){
        lockScreen();
        new Thread() {
            @Override
            public void run() {
                String result = null;
                try {
                    OrderDB orderDB = new OrderDB(getContext(), mUser);
                    if (mSalesOrderId > 0) {
                        result = orderDB.createOrderFromOrderLines(mSalesOrderId, mBusinessPartnerId, mOrderLines);
                    } else {
                        result = orderDB.createOrderFromShoppingCart(Utils.getAppCurrentBusinessPartnerId(getContext(), mUser));
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
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            } else {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }
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
                        if(mIsShoppingCart){
                            startActivity(new Intent(getContext(), OrdersListActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT ));
                        }else{
                            startActivity(new Intent(getContext(), SalesOrdersListActivity.class)
                                    .putExtra(SalesOrdersListActivity.KEY_CURRENT_TAB_SELECTED, 1)
                                    .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT ));
                        }
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                        if (waitPlease!=null && waitPlease.isShowing()) {
                            waitPlease.cancel();
                            waitPlease = null;
                        }
                    }
                }
            });
        }
    }

    @Override
    public void updateQtyOrdered(OrderLine orderLine) {
        DialogUpdateShoppingCartQtyOrdered dialogUpdateShoppingCartQtyOrdered =
                DialogUpdateShoppingCartQtyOrdered.newInstance(orderLine, mIsShoppingCart, mUser);
        dialogUpdateShoppingCartQtyOrdered.setTargetFragment(this, 0);
        dialogUpdateShoppingCartQtyOrdered.show(getActivity().getSupportFragmentManager(),
                DialogUpdateShoppingCartQtyOrdered.class.getSimpleName());
    }

    @Override
    public void reloadShoppingCart(){
        if(mIsShoppingCart){
            reloadShoppingCart((new OrderLineDB(getActivity(), mUser)).getShoppingCart());
        }else{
            mShoppingCartAdapter.setData(mOrderLines);
        }
    }

    @Override
    public void reloadShoppingCart(ArrayList<OrderLine> orderLines){
        mOrderLines = orderLines;
        mShoppingCartAdapter.setData(mOrderLines);
        if (mOrderLines==null || mOrderLines.isEmpty()) {
            mBlankScreenView.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.GONE);
        }else{
            mBlankScreenView.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
            totalLines.setText(getString(R.string.order_lines_number, String.valueOf(mOrderLines.size())));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!mIsShoppingCart) {
            outState.putInt(STATE_SALES_ORDER_ID, mSalesOrderId);
            outState.putInt(STATE_BUSINESS_PARTNER_ID, mBusinessPartnerId);
            outState.putParcelableArrayList(STATE_ORDER_LINES, mOrderLines);
        }
        try {
            if (mLinearLayoutManager instanceof GridLayoutManager) {
                outState.putInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION,
                        mLinearLayoutManager.findFirstVisibleItemPosition());
            } else {
                outState.putInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION,
                        mLinearLayoutManager.findFirstCompletelyVisibleItemPosition());
            }
        } catch (Exception e) {
            outState.putInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION, mRecyclerViewCurrentFirstPosition);
        }
        super.onSaveInstanceState(outState);
    }
}
