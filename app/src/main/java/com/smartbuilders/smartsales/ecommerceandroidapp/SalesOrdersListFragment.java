package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.SalesOrdersListAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.SalesOrderDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.SalesOrder;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class SalesOrdersListFragment extends Fragment {

    private User mCurrentUser;

    public interface Callback {
        public void onItemSelected(SalesOrder salesOrder, int selectedItemPosition);
    }

    public SalesOrdersListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sales_orders_list, container, false);

        mCurrentUser = Utils.getCurrentUser(getContext());

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
        return rootView;
    }
}
