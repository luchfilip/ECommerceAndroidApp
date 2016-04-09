package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.OrdersListAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Order;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class OrdersListFragment extends Fragment {

    private ListView mListView;
    private OrdersListAdapter mOrdersListAdapter;

    public interface Callback {
        public void onItemSelected(Order order);
    }

    public OrdersListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_orders_list, container, false);

        ArrayList<Order> orders = new ArrayList<Order>();
        Order order = new Order();
        order.setId(1);
        orders.add(order);

        order = new Order();
        order.setId(2);
        orders.add(order);

        order = new Order();
        order.setId(3);
        orders.add(order);

        order = new Order();
        order.setId(4);
        orders.add(order);

        order = new Order();
        order.setId(5);
        orders.add(order);

        order = new Order();
        order.setId(6);
        orders.add(order);

        order = new Order();
        order.setId(7);
        orders.add(order);

        mOrdersListAdapter = new OrdersListAdapter(getActivity(), orders);

        mListView = (ListView) rootView.findViewById(R.id.orders_list);
        mListView.setAdapter(mOrdersListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Order order = (Order) adapterView.getItemAtPosition(position);
                if (order != null) {
                    ((Callback) getActivity()).onItemSelected(order);
                }
            }
        });

        return rootView;
    }
}
