package com.smartbuilders.smartsales.ecommerce;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.smartbuilders.ids.model.User;
import com.smartbuilders.ids.model.UserProfile;
import com.smartbuilders.smartsales.ecommerce.adapters.OrdersListAdapter;
import com.smartbuilders.smartsales.ecommerce.adapters.SalesOrdersListAdapter;
import com.smartbuilders.smartsales.ecommerce.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.data.OrderDB;
import com.smartbuilders.smartsales.ecommerce.data.SalesOrderDB;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerce.model.Order;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrder;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class SalesOrdersListFragment extends Fragment {

    public static final String KEY_LOAD_ORDERS_FROM_SALES_ORDERS = "KEY_LOAD_ORDERS_FROM_SALES_ORDERS";
    private static final String STATE_LOAD_ORDERS_FROM_SALES_ORDERS = "STATE_LOAD_ORDERS_FROM_SALES_ORDERS";
    private static final String STATE_CURRENT_SELECTED_INDEX = "STATE_CURRENT_SELECTED_INDEX";
    private static final String STATE_LIST_VIEW_INDEX = "STATE_LIST_VIEW_INDEX";
    private static final String STATE_LIST_VIEW_TOP = "STATE_LIST_VIEW_TOP";

    private boolean mIsInitialLoad;
    private User mUser;
    private boolean mLoadOrdersFromSalesOrders;
    private ListView mListView;
    private int mListViewIndex;
    private int mListViewTop;
    private int mCurrentSelectedIndex;
    private TextView mBusinessPartnerName;
    private View mBusinessPartnerInfoSeparator;

    public interface Callback {
        void onItemSelected(SalesOrder salesOrder);
        void onItemLongSelected(SalesOrder salesOrder, ListView listView, User user);
        void onItemSelected(Order order);
        void onListIsLoaded(ListView listView);
        void setSelectedIndex(int selectedIndex, ListView listView);
    }

    public SalesOrdersListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sales_orders_list, container, false);

        mIsInitialLoad = true;

        final ArrayList<Order> activeOrdersFromSalesOrders = new ArrayList<>();
        final ArrayList<SalesOrder> activeSalesOrders = new ArrayList<>();

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState!=null){
                        if(savedInstanceState.containsKey(STATE_LOAD_ORDERS_FROM_SALES_ORDERS)){
                            mLoadOrdersFromSalesOrders = savedInstanceState.getBoolean(STATE_LOAD_ORDERS_FROM_SALES_ORDERS);
                        }
                        if(savedInstanceState.containsKey(STATE_CURRENT_SELECTED_INDEX)){
                            mCurrentSelectedIndex = savedInstanceState.getInt(STATE_CURRENT_SELECTED_INDEX);
                        }
                        if(savedInstanceState.containsKey(STATE_LIST_VIEW_INDEX)){
                            mListViewIndex = savedInstanceState.getInt(STATE_LIST_VIEW_INDEX);
                        }
                        if(savedInstanceState.containsKey(STATE_LIST_VIEW_TOP)){
                            mListViewTop = savedInstanceState.getInt(STATE_LIST_VIEW_TOP);
                        }
                    }

                    if(getArguments()!=null){
                        if(getArguments().containsKey(KEY_LOAD_ORDERS_FROM_SALES_ORDERS)){
                            mLoadOrdersFromSalesOrders = getArguments().getBoolean(KEY_LOAD_ORDERS_FROM_SALES_ORDERS);
                        }
                    }

                    mUser = Utils.getCurrentUser(getContext());

                    if (mLoadOrdersFromSalesOrders) {
                        activeOrdersFromSalesOrders.addAll((new OrderDB(getContext(), mUser)).getActiveOrdersFromSalesOrders());
                    } else {
                        activeSalesOrders.addAll((new SalesOrderDB(getContext(), mUser)).getActiveSalesOrders());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mBusinessPartnerName = (TextView) view.findViewById(R.id.business_partner_commercial_name_textView);
                                mBusinessPartnerInfoSeparator = view.findViewById(R.id.business_partner_info_separator);
                                setHeader();

                                mListView = (ListView) view.findViewById(R.id.sales_orders_list);
                                if (mLoadOrdersFromSalesOrders) {
                                    mListView.setAdapter(new OrdersListAdapter(getActivity(), activeOrdersFromSalesOrders));

                                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                        @Override
                                        public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                                            mCurrentSelectedIndex = position;
                                            // CursorAdapter returns a cursor at the correct position for getItem(), or null
                                            // if it cannot seek to that position.
                                            Order order = (Order) adapterView.getItemAtPosition(position);
                                            if (order != null) {
                                                ((Callback) getActivity()).onItemSelected(order);
                                            }
                                        }
                                    });
                                } else {
                                    mListView.setAdapter(new SalesOrdersListAdapter(getContext(), mUser, activeSalesOrders));

                                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                        @Override
                                        public void onItemClick(AdapterView parent, View view, int position, long id) {
                                            mCurrentSelectedIndex = position;
                                            // CursorAdapter returns a cursor at the correct position for getItem(), or null
                                            // if it cannot seek to that position.
                                            SalesOrder salesOrder = (SalesOrder) parent.getItemAtPosition(position);
                                            if (salesOrder != null) {
                                                ((Callback) getActivity()).onItemSelected(salesOrder);
                                            }
                                        }
                                    });

                                    mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                        @Override
                                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                            // CursorAdapter returns a cursor at the correct position for getItem(), or null
                                            // if it cannot seek to that position.
                                            SalesOrder salesOrder = (SalesOrder) parent.getItemAtPosition(position);
                                            if (salesOrder != null) {
                                                ((Callback) getActivity()).onItemLongSelected(salesOrder, mListView, mUser);
                                            }
                                            return true;
                                        }
                                    });
                                }

                                mListView.setSelectionFromTop(mListViewIndex, mListViewTop);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                view.findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                if (getActivity()!=null) {
                                    if (savedInstanceState==null) {
                                        ((Callback) getActivity()).onListIsLoaded(mListView);
                                    } else {
                                        ((Callback) getActivity()).setSelectedIndex(mCurrentSelectedIndex, mListView);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }.start();
        return view;
    }

    private void setHeader(){
        if(mUser!=null && mUser.getUserProfileId()== UserProfile.SALES_MAN_PROFILE_ID){
            try {
                BusinessPartner businessPartner = (new BusinessPartnerDB(getContext(), mUser))
                        .getActiveBusinessPartnerById(Utils.getAppCurrentBusinessPartnerId(getContext(), mUser));
                if(businessPartner!=null){
                    mBusinessPartnerName.setText(getString(R.string.business_partner_detail, businessPartner.getName()));
                    mBusinessPartnerName.setVisibility(View.VISIBLE);
                    mBusinessPartnerInfoSeparator.setVisibility(View.VISIBLE);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart() {
        if(mIsInitialLoad){
            mIsInitialLoad = false;
        }else{
            if(mListView!=null && mListView.getAdapter()!=null && getContext()!=null
                    && getActivity()!=null){
                int oldListSize = mListView.getAdapter().getCount();
                if (mLoadOrdersFromSalesOrders) {
                    ((OrdersListAdapter) mListView.getAdapter()).setData((new OrderDB(getContext(),
                            mUser)).getActiveOrdersFromSalesOrders());
                }else{
                    ((SalesOrdersListAdapter) mListView.getAdapter()).setData((new SalesOrderDB(
                            getContext(), mUser)).getActiveSalesOrders());
                }
                if(mListView.getAdapter().getCount()!=oldListSize){
                    ((Callback) getActivity()).onListIsLoaded(mListView);
                }else{
                    ((Callback) getActivity()).setSelectedIndex(mCurrentSelectedIndex, mListView);
                }
            }
        }
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_LOAD_ORDERS_FROM_SALES_ORDERS, mLoadOrdersFromSalesOrders);
        try {
            outState.putInt(STATE_LIST_VIEW_INDEX, mListView.getFirstVisiblePosition());
        } catch (Exception e) {
            outState.putInt(STATE_LIST_VIEW_INDEX, mListViewIndex);
        }
        try {
            outState.putInt(STATE_LIST_VIEW_TOP, (mListView.getChildAt(0) == null) ? 0 :
                    (mListView.getChildAt(0).getTop() - mListView.getPaddingTop()));
        } catch (Exception e) {
            outState.putInt(STATE_LIST_VIEW_TOP, mListViewTop);
        }
        outState.putInt(STATE_CURRENT_SELECTED_INDEX, mCurrentSelectedIndex);
        super.onSaveInstanceState(outState);
    }
}
