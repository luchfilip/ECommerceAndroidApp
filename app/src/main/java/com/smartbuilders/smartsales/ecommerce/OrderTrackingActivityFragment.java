package com.smartbuilders.smartsales.ecommerce;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
public class OrderTrackingActivityFragment extends Fragment {

    private static final String STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION =
            "STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION";

    private boolean mIsInitialLoad;
    private LinearLayoutManager mLinearLayoutManager;
    private int mRecyclerViewCurrentFirstPosition;


    public OrderTrackingActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_order_tracking, container, false);
        mIsInitialLoad = true;

        final ArrayList<OrderTracking> orderTrackings = new ArrayList<>();

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState != null) {
                        if(savedInstanceState.containsKey(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION)){
                            mRecyclerViewCurrentFirstPosition = savedInstanceState.getInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION);
                        }
                    }

                    orderTrackings.addAll((new OrderTrackingDB(getContext(), Utils.getCurrentUser(getContext()))).getOrderTrackings());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ProgressBar progressBar = ((ProgressBar) view.findViewById(R.id.orderTracking_progressBar));
                                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                    progressBar.setProgressDrawable(getContext().getResources()
                                            .getDrawable(R.drawable.order_tracking_progress_bar));
                                } else {
                                    progressBar.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.order_tracking_progress_bar));
                                }

                                progressBar.setProgress(20);

                                ((TextView) view.findViewById(R.id.progress_textView)).setText("Progreso: 1/5");

                                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.order_tracking);
                                // use this setting to improve performance if you know that changes
                                // in content do not change the layout size of the RecyclerView
                                recyclerView.setHasFixedSize(true);
                                mLinearLayoutManager = new LinearLayoutManager(getActivity());
                                recyclerView.setLayoutManager(mLinearLayoutManager);
                                recyclerView.setAdapter(new OrderTrackingAdapter(getActivity(), orderTrackings));

                                if (mRecyclerViewCurrentFirstPosition!=0) {
                                    recyclerView.scrollToPosition(mRecyclerViewCurrentFirstPosition);
                                }
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
            //TODO: reload list
        }
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        try {
            outState.putInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION,
                    mLinearLayoutManager.findFirstCompletelyVisibleItemPosition());
        } catch (Exception e) {
            outState.putInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION, mRecyclerViewCurrentFirstPosition);
        }
        super.onSaveInstanceState(outState);
    }
}
