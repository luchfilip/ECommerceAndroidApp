package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.OrderLineAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Order;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class OrderDetailFragment extends Fragment {

    private User mCurrentUser;
    private Order mOrder;

    public OrderDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order_detail, container, false);

        if(getArguments()!=null){
            if(getArguments().containsKey(OrderDetailActivity.KEY_ORDER)){
                mOrder = getArguments().getParcelable(OrderDetailActivity.KEY_ORDER);
            }
            if(getArguments().containsKey(OrderDetailActivity.KEY_CURRENT_USER)){
                mCurrentUser = getArguments().getParcelable(OrderDetailActivity.KEY_CURRENT_USER);
            }
        }else if(getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null){
            if(getActivity().getIntent().getExtras().containsKey(OrderDetailActivity.KEY_CURRENT_USER)){
                mCurrentUser = getActivity().getIntent().getExtras().getParcelable(OrderDetailActivity.KEY_CURRENT_USER);
            }
            if(getActivity().getIntent().getExtras().containsKey(OrderDetailActivity.KEY_ORDER)){
                mOrder = getActivity().getIntent().getExtras().getParcelable(OrderDetailActivity.KEY_ORDER);
            }
        }

        ArrayList<OrderLine> orderLines = new OrderLineDB(getContext(), mCurrentUser)
                .getActiveFinalizedOrderLinesByOrderId(mOrder.getId());

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.order_lines);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new OrderLineAdapter(orderLines, mCurrentUser));

        ((TextView) rootView.findViewById(R.id.order_lines_number_tv))
                .setText(getContext().getString(R.string.order_lines_numer, String.valueOf(orderLines.size())));

        ((TextView) rootView.findViewById(R.id.order_number_tv))
                .setText(getContext().getString(R.string.order_number, mOrder.getOrderNumber()));

        ((TextView) rootView.findViewById(R.id.order_date_tv))
                .setText(getContext().getString(R.string.order_date, mOrder.getCreatedStringFormat()));

        return rootView;
    }
}
