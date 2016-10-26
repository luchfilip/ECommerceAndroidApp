package com.smartbuilders.smartsales.ecommerce;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerce.adapters.OrderTrackingAdapter;
import com.smartbuilders.smartsales.ecommerce.data.OrderTrackingDB;
import com.smartbuilders.smartsales.ecommerce.model.OrderTracking;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

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
    private TextView mProgressTextView;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private String mProgressText;
    private int mProgressPercentage;
    private ArrayList<OrderTracking> mOrderTrackings;

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
                    OrderTrackingDB orderTrackingDB = new OrderTrackingDB(getContext(), Utils.getCurrentUser(getContext()));
                    mProgressText = orderTrackingDB.getProgressText(mOrderId);
                    mProgressPercentage = orderTrackingDB.getProgressPercentage(mOrderId);
                    mOrderTrackings = orderTrackingDB.getOrderTrackings(mOrderId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mProgressBar = ((ProgressBar) view.findViewById(R.id.orderTracking_progressBar));
                                mProgressTextView = (TextView) view.findViewById(R.id.progress_textView);
                                mRecyclerView = (RecyclerView) view.findViewById(R.id.order_tracking);

                                loadViews(mProgressText, mProgressPercentage, mOrderTrackings);
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
            loadViews(orderTrackingDB.getProgressText(mOrderId),
                    orderTrackingDB.getProgressPercentage(mOrderId),
                    orderTrackingDB.getOrderTrackings(mOrderId));
        }
        super.onStart();
    }

    private void loadViews(String progressText, int progressPercentage, ArrayList<OrderTracking> orderTrackings) {
        mProgressTextView.setText(progressText);
        mProgressBar.setProgress(progressPercentage);

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
