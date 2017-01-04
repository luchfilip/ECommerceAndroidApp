package com.smartbuilders.smartsales.ecommerce;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerce.adapters.OrderTrackingAdapter;
import com.smartbuilders.smartsales.ecommerce.data.OrderDB;
import com.smartbuilders.smartsales.ecommerce.data.OrderTrackingDB;
import com.smartbuilders.smartsales.ecommerce.model.Order;
import com.smartbuilders.smartsales.ecommerce.model.OrderTracking;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class OrderTrackingDetailFragment extends Fragment {

    private static final String STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION =
            "STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION";
    private static final String STATE_ORDER_ID = "STATE_ORDER_ID";

    private boolean mIsInitialLoad;
    private int mOrderId;
    private int mRecyclerViewCurrentFirstPosition;
    private RecyclerView mRecyclerView;
    private ArrayList<OrderTracking> mOrderTrackings;
    private Order mOrder;

    public interface Callback {
        void goToOrderDetail(int orderId);
    }

    public OrderTrackingDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_order_tracking_detail, container, false);
        mIsInitialLoad = true;

        new Thread() {
            @Override
            public void run() {
                try {
                    if (savedInstanceState != null) {
                        if (savedInstanceState.containsKey(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION)) {
                            mRecyclerViewCurrentFirstPosition = savedInstanceState.getInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION);
                        }
                        if (savedInstanceState.containsKey(STATE_ORDER_ID)) {
                            mOrderId = savedInstanceState.getInt(STATE_ORDER_ID);
                        }
                    }

                    if (getArguments() != null) {
                        if (getArguments().containsKey(OrderTrackingDetailActivity.KEY_ORDER_ID)) {
                            mOrderId = getArguments().getInt(OrderTrackingDetailActivity.KEY_ORDER_ID);
                        }
                    } else if (getActivity()!=null && getActivity().getIntent() != null &&
                            getActivity().getIntent().getExtras() != null) {
                        if (getActivity().getIntent().getExtras().containsKey(OrderTrackingDetailActivity.KEY_ORDER_ID)) {
                            mOrderId = getActivity().getIntent().getExtras().getInt(OrderTrackingDetailActivity.KEY_ORDER_ID);
                        }
                    }
                    final User user = Utils.getCurrentUser(getContext());

                    OrderTrackingDB orderTrackingDB = new OrderTrackingDB(getContext(), user);
                    mOrderTrackings = orderTrackingDB.getOrderTrackings(mOrderId);
                    mOrder = (new OrderDB(getContext(), user)).getActiveOrderById(mOrderId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mRecyclerView = (RecyclerView) view.findViewById(R.id.order_tracking);

                                if (view.findViewById(R.id.go_to_order_fab) != null) {
                                    view.findViewById(R.id.go_to_order_fab).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ((Callback) getActivity()).goToOrderDetail(mOrderId);
                                        }
                                    });
                                }

                                if (mOrder!=null) {
                                    ((TextView) view.findViewById(R.id.order_number_tv))
                                            .setText(getContext().getString(R.string.order_number, mOrder.getOrderNumber()));

                                    ((TextView) view.findViewById(R.id.order_date_tv))
                                            .setText(getContext().getString(R.string.order_date, mOrder.getCreatedStringFormat()));
                                }

                                loadViews(/*mProgressText, mProgressPercentage, */mOrderTrackings);
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

    @Override
    public void onStart() {
        if (mIsInitialLoad) {
            mIsInitialLoad = false;
        } else {
            OrderTrackingDB orderTrackingDB = new OrderTrackingDB(getContext(), Utils.getCurrentUser(getContext()));
            loadViews(orderTrackingDB.getOrderTrackings(mOrderId));
        }
        super.onStart();
    }

    private void loadViews(ArrayList<OrderTracking> orderTrackings) {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new OrderTrackingAdapter(getActivity(), orderTrackings));

        if (mRecyclerViewCurrentFirstPosition!=0) {
            mRecyclerView.scrollToPosition(mRecyclerViewCurrentFirstPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        try {
            outState.putInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION,
                    ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition());
        } catch (Exception e) {
            outState.putInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION, mRecyclerViewCurrentFirstPosition);
        }
        super.onSaveInstanceState(outState);
    }
}
