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
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Order;

/**
 * A placeholder fragment containing a simple view.
 */
public class SalesOrdersListFragment extends Fragment {

    private ListView mListView;
    private User mCurrentUser;

    public interface Callback {
        public void onItemSelected(Order order, int selectedItemPosition);
    }

    public SalesOrdersListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sales_orders_list, container, false);

        if(getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null){
            if(getActivity().getIntent().getExtras().containsKey(SalesOrdersListActivity.KEY_CURRENT_USER)){
                mCurrentUser = getActivity().getIntent().getExtras().getParcelable(SalesOrdersListActivity.KEY_CURRENT_USER);
            }
        }

        mListView = (ListView) rootView.findViewById(R.id.sales_orders_list);
        mListView.setAdapter(new SalesOrdersListAdapter(getActivity(),
                (new OrderDB(getContext(), mCurrentUser)).getActiveSalesOrders(), mCurrentUser));

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
        return rootView;
    }
}
