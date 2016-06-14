package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
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

    private User mCurrentUser;
    private boolean mLoadOrdersFromSalesOrders;

    public interface Callback {
        public void onItemSelected(SalesOrder salesOrder, int selectedItemPosition);
        public void onItemSelected(Order order, int selectedItemPosition);
    }

    public SalesOrdersListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(STATE_LOAD_ORDERS_FROM_SALES_ORDERS)){
                mLoadOrdersFromSalesOrders = savedInstanceState.getBoolean(STATE_LOAD_ORDERS_FROM_SALES_ORDERS);
            }
        }

        if(getArguments()!=null){
            if(getArguments().containsKey(KEY_LOAD_ORDERS_FROM_SALES_ORDERS)){
                mLoadOrdersFromSalesOrders = getArguments().getBoolean(KEY_LOAD_ORDERS_FROM_SALES_ORDERS);
            }
        }

        View rootView = inflater.inflate(R.layout.fragment_sales_orders_list, container, false);

        mCurrentUser = Utils.getCurrentUser(getContext());

        if (mLoadOrdersFromSalesOrders) {
            ArrayList<Order> activeOrdersFromSalesOrders = (new OrderDB(getContext(), mCurrentUser)).getActiveOrdersFromSalesOrders();
            if (activeOrdersFromSalesOrders!=null && !activeOrdersFromSalesOrders.isEmpty()) {
                ListView listView = (ListView) rootView.findViewById(R.id.sales_orders_list);
                listView.setAdapter(new OrdersListAdapter(getActivity(), activeOrdersFromSalesOrders));

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                        // CursorAdapter returns a cursor at the correct position for getItem(), or null
                        // if it cannot seek to that position.
                        Order order = (Order) adapterView.getItemAtPosition(position);
                        if (order != null) {
                            ((Callback) getActivity()).onItemSelected(order, position);
                        }
                    }
                });
            }
        } else {
            ArrayList<SalesOrder> activeSalesOrders = (new SalesOrderDB(getContext(), mCurrentUser)).getActiveSalesOrders();
            if (activeSalesOrders!=null && !activeSalesOrders.isEmpty()) {
                ListView listView = (ListView) rootView.findViewById(R.id.sales_orders_list);
                listView.setAdapter(new SalesOrdersListAdapter(getActivity(), activeSalesOrders));

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                        // CursorAdapter returns a cursor at the correct position for getItem(), or null
                        // if it cannot seek to that position.
                        SalesOrder salesOrder = (SalesOrder) adapterView.getItemAtPosition(position);
                        if (salesOrder != null) {
                            ((Callback) getActivity()).onItemSelected(salesOrder, position);
                        }
                    }
                });
            }
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_LOAD_ORDERS_FROM_SALES_ORDERS, mLoadOrdersFromSalesOrders);
        super.onSaveInstanceState(outState);
    }
}
