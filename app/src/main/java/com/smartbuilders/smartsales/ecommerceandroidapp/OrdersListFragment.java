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
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Order;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class OrdersListFragment extends Fragment {

    private User mCurrentUser;

    public interface Callback {
        void onItemSelected(Order order, int selectedItemPosition);
    }

    public OrdersListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_orders_list, container, false);

        mCurrentUser = Utils.getCurrentUser(getContext());

        ArrayList<Order> activeOrders = (new OrderDB(getContext(), mCurrentUser)).getActiveOrders();
        if (activeOrders!=null && !activeOrders.isEmpty()) {
            ListView listView = (ListView) view.findViewById(R.id.orders_list);
            listView.setAdapter(new OrdersListAdapter(getActivity(), activeOrders));

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
        new Thread() {
            @Override
            public void run() {
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                view.findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        }.start();
        return view;
    }
}
