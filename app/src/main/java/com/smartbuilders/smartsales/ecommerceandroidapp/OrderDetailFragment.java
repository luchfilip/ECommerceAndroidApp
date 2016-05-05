package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.OrderLineAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;

/**
 * A placeholder fragment containing a simple view.
 */
public class OrderDetailFragment extends Fragment {

    private User mCurrentUser;
    private int orderId;

    public OrderDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order_detail, container, false);

        if(getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null){
            if(getActivity().getIntent().getExtras().containsKey(OrderDetailActivity.KEY_CURRENT_USER)){
                mCurrentUser = getActivity().getIntent().getExtras().getParcelable(OrderDetailActivity.KEY_CURRENT_USER);
            }

            if(getActivity().getIntent().getExtras().containsKey(OrderDetailActivity.KEY_ORDER_ID)){
                orderId = getActivity().getIntent().getExtras().getInt(OrderDetailActivity.KEY_ORDER_ID);
            }
        }

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.order_lines);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new OrderLineAdapter((new OrderLineDB(getContext(), mCurrentUser))
                .getActiveFinalizedOrderLinesByOrderId(orderId), mCurrentUser));

        return rootView;
    }
}
