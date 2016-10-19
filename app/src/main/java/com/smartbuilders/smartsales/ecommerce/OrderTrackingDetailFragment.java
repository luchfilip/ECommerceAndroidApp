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
    private static final String STATE_BUSINESS_PARTNER_ID = "STATE_BUSINESS_PARTNER_ID";
    private static final String STATE_ORDER_ID = "STATE_ORDER_ID";

    private boolean mIsInitialLoad;
    private int mBusinessPartnerId;
    private int mOrderId;
    private int mRecyclerViewCurrentFirstPosition;
    private TextView mProgressTextView;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;

    public OrderTrackingDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_order_tracking_detail, container, false);
        mIsInitialLoad = true;

        final ArrayList<OrderTracking> orderTrackings = new ArrayList<>();

        new Thread() {
            @Override
            public void run() {
                try {
                    if (savedInstanceState != null) {
                        if (savedInstanceState.containsKey(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION)) {
                            mRecyclerViewCurrentFirstPosition = savedInstanceState.getInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION);
                        }
                        if (savedInstanceState.containsKey(STATE_BUSINESS_PARTNER_ID)) {
                            mBusinessPartnerId = savedInstanceState.getInt(STATE_BUSINESS_PARTNER_ID);
                        }
                        if (savedInstanceState.containsKey(STATE_ORDER_ID)) {
                            mOrderId = savedInstanceState.getInt(STATE_ORDER_ID);
                        }
                    }
                    if (getActivity()!=null && getActivity().getIntent() != null &&
                            getActivity().getIntent().getExtras() != null) {
                        if (getActivity().getIntent().getExtras().containsKey(OrderTrackingDetailActivity.KEY_BUSINESS_PARTNER_ID)) {
                            mBusinessPartnerId = getActivity().getIntent().getExtras().getInt(OrderTrackingDetailActivity.KEY_BUSINESS_PARTNER_ID);
                        }
                        if (getActivity().getIntent().getExtras().containsKey(OrderTrackingDetailActivity.KEY_ORDER_ID)) {
                            mOrderId = getActivity().getIntent().getExtras().getInt(OrderTrackingDetailActivity.KEY_ORDER_ID);
                        }
                    }

                    orderTrackings.addAll((new OrderTrackingDB(getContext(), Utils.getCurrentUser(getContext())))
                            .getOrderTracking(mBusinessPartnerId, mOrderId));
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

                                loadViews("Progreso: 1/5", 20, orderTrackings);
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
            loadViews("Progreso: 1/5", 20, (new OrderTrackingDB(getContext(), Utils.getCurrentUser(getContext())))
                    .getOrderTracking(mBusinessPartnerId, mOrderId));
        }
        super.onStart();
    }

    private void loadViews(String progressText, int progressPercentage, ArrayList<OrderTracking> orderTrackings) {
        mProgressTextView.setText(progressText);

        //if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
        //    mProgressBar.setProgressDrawable(getContext().getResources()
        //            .getDrawable(R.drawable.order_tracking_progress_bar));
        //} else {
        //    mProgressBar.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.order_tracking_progress_bar));
        //}
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
