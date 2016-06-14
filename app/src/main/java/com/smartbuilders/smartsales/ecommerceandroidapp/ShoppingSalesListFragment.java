package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.ShoppingSalesListAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.SalesOrderDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.SalesOrder;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShoppingSalesListFragment extends Fragment {

    private User mCurrentUser;

    public interface Callback {
        public void onItemSelected(SalesOrder salesOrder, int selectedItemPosition);
        public void onItemLongSelected(SalesOrder salesOrder, int selectedItemPosition);
    }

    public ShoppingSalesListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mCurrentUser = Utils.getCurrentUser(getContext());

        View rootView = inflater.inflate(R.layout.fragment_shopping_sales_list, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.shopping_sales_orders_list);

        listView.setAdapter(new ShoppingSalesListAdapter((new SalesOrderDB(getContext(), mCurrentUser)).getActiveShoppingSalesOrders()));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SalesOrder salesOrder = (SalesOrder) parent.getItemAtPosition(position);
                if (salesOrder != null) {
                    ((Callback) getActivity()).onItemSelected(salesOrder, position);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SalesOrder salesOrder = (SalesOrder) parent.getItemAtPosition(position);
                if (salesOrder != null) {
                    ((Callback) getActivity()).onItemLongSelected(salesOrder, position);
                }
                return false;
            }
        });

        return rootView;
    }
}
