package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.OrdersListAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.SalesOrdersListAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.SalesOrderDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Order;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.SalesOrder;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class SalesOrdersListFragment extends Fragment {

    public static final String KEY_LOAD_ORDERS_FROM_SALES_ORDERS = "KEY_LOAD_ORDERS_FROM_SALES_ORDERS";
    private static final String STATE_LOAD_ORDERS_FROM_SALES_ORDERS = "STATE_LOAD_ORDERS_FROM_SALES_ORDERS";
    private static final String STATE_CURRENT_SELECTED_INDEX = "STATE_CURRENT_SELECTED_INDEX";
    private static final String STATE_LISTVIEW_INDEX = "STATE_LISTVIEW_INDEX";
    private static final String STATE_LISTVIEW_TOP = "STATE_LISTVIEW_TOP";

    private User mCurrentUser;
    private boolean mLoadOrdersFromSalesOrders;
    private ListView mListView;
    private int mListViewIndex;
    private int mListViewTop;
    private int mCurrentSelectedIndex;

    public interface Callback {
        void onItemSelected(SalesOrder salesOrder);
        void onItemLongSelected(SalesOrder salesOrder, ListView listView);
        void onItemSelected(Order order);
        void onListIsLoaded(ListView listView);
        void setSelectedIndex(int selectedIndex, ListView listView);
        void reloadActivity();
    }

    public SalesOrdersListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sales_orders_list, container, false);

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
                        if(savedInstanceState.containsKey(STATE_LISTVIEW_INDEX)){
                            mListViewIndex = savedInstanceState.getInt(STATE_LISTVIEW_INDEX);
                        }
                        if(savedInstanceState.containsKey(STATE_LISTVIEW_TOP)){
                            mListViewTop = savedInstanceState.getInt(STATE_LISTVIEW_TOP);
                        }
                    }

                    if(getArguments()!=null){
                        if(getArguments().containsKey(KEY_LOAD_ORDERS_FROM_SALES_ORDERS)){
                            mLoadOrdersFromSalesOrders = getArguments().getBoolean(KEY_LOAD_ORDERS_FROM_SALES_ORDERS);
                        }
                    }

                    mCurrentUser = Utils.getCurrentUser(getContext());

                    if (mLoadOrdersFromSalesOrders) {
                        activeOrdersFromSalesOrders.addAll((new OrderDB(getContext(), mCurrentUser)).getActiveOrdersFromSalesOrders());
                    } else {
                        activeSalesOrders.addAll((new SalesOrderDB(getContext(), mCurrentUser)).getActiveSalesOrders());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
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
                                    mListView.setAdapter(new SalesOrdersListAdapter(getActivity(), activeSalesOrders));

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
                                                ((Callback) getActivity()).onItemLongSelected(salesOrder, mListView);
                                            }
                                            return true;
                                        }
                                    });
                                }

                                mListView.setSelectionFromTop(mListViewIndex, mListViewTop);

                                /*
                                 * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
                                 * performs a swipe-to-refresh gesture.
                                 */
                                ((SwipeRefreshLayout) view.findViewById(R.id.main_layout)).setOnRefreshListener(
                                    new SwipeRefreshLayout.OnRefreshListener() {
                                        @Override
                                        public void onRefresh() {
                                            ((Callback) getActivity()).reloadActivity();
                                        }
                                    }
                                );
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_LOAD_ORDERS_FROM_SALES_ORDERS, mLoadOrdersFromSalesOrders);
        try {
            outState.putInt(STATE_LISTVIEW_INDEX, mListView.getFirstVisiblePosition());
        } catch (Exception e) {
            outState.putInt(STATE_LISTVIEW_INDEX, mListViewIndex);
        }
        try {
            outState.putInt(STATE_LISTVIEW_TOP, (mListView.getChildAt(0) == null) ? 0 :
                    (mListView.getChildAt(0).getTop() - mListView.getPaddingTop()));
        } catch (Exception e) {
            outState.putInt(STATE_LISTVIEW_TOP, mListViewTop);
        }
        outState.putInt(STATE_CURRENT_SELECTED_INDEX, mCurrentSelectedIndex);
        super.onSaveInstanceState(outState);
    }
}
