package com.smartbuilders.smartsales.ecommerce;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.smartbuilders.smartsales.ecommerce.adapters.OrdersTrackingListAdapter;
import com.smartbuilders.smartsales.ecommerce.data.OrderDB;
import com.smartbuilders.smartsales.ecommerce.model.Order;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;

/**
 * A placeholder fragment containing a simple view.
 */
public class OrdersTrackingListFragment extends Fragment {

    private static final String STATE_CURRENT_SELECTED_INDEX = "STATE_CURRENT_SELECTED_INDEX";
    private static final String STATE_LIST_VIEW_INDEX = "STATE_LIST_VIEW_INDEX";
    private static final String STATE_LIST_VIEW_TOP = "STATE_LIST_VIEW_TOP";
    private static final String TAG = OrdersTrackingListFragment.class.getSimpleName();

    private boolean mIsInitialLoad;
    private ListView mListView;
    private OrdersTrackingListAdapter mOrdersTrackingListAdapter;
    private OrderDB mOrderDB;
    private int mListViewIndex;
    private int mListViewTop;
    private int mCurrentSelectedIndex;
    private User mUser;

    public interface Callback {
        void onItemSelected(Order order);
        void onListIsLoaded();
        void setSelectedIndex(int selectedIndex);
    }

    public OrdersTrackingListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_orders_tracking_list, container, false);
        mIsInitialLoad = true;

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState != null) {
                        if(savedInstanceState.containsKey(STATE_CURRENT_SELECTED_INDEX)){
                            mCurrentSelectedIndex = savedInstanceState.getInt(STATE_CURRENT_SELECTED_INDEX);
                        }
                        if(savedInstanceState.containsKey(STATE_LIST_VIEW_INDEX)){
                            mListViewIndex = savedInstanceState.getInt(STATE_LIST_VIEW_INDEX);
                        }
                        if(savedInstanceState.containsKey(STATE_LIST_VIEW_TOP)){
                            mListViewTop = savedInstanceState.getInt(STATE_LIST_VIEW_TOP);
                        }
                    }

                    mUser = Utils.getCurrentUser(getContext());

                    mOrderDB = new OrderDB(getContext(), mUser);
                    mOrdersTrackingListAdapter = new OrdersTrackingListAdapter(getActivity(),
                            mOrderDB.getActiveOrdersWithTracking());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mListView = (ListView) view.findViewById(R.id.orders_tracking_list);
                                mListView.setAdapter(mOrdersTrackingListAdapter);
                                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                                        mCurrentSelectedIndex = position;
                                        // CursorAdapter returns a cursor at the correct position for getItem(), or null
                                        // if it cannot seek to that position.
                                        Order order = (Order) adapterView.getItemAtPosition(position);
                                        if (order != null) {
                                            ((Callback) getActivity()).onItemSelected(order);
                                        }
                                    }
                                });
                                mListView.setSelectionFromTop(mListViewIndex, mListViewTop);

                                if (view.findViewById(R.id.search_fab) != null) {
                                    view.findViewById(R.id.search_fab).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            startActivity(new Intent(getActivity(), SearchResultsActivity.class));
                                        }
                                    });
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                view.findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                if (getActivity()!=null) {
                                    //entra en esta condicion si esta vacio para que coloque el wallpaper
                                    if (savedInstanceState==null || mOrdersTrackingListAdapter.isEmpty()) {
                                        ((Callback) getActivity()).onListIsLoaded();
                                    } else {
                                        ((Callback) getActivity()).setSelectedIndex(mCurrentSelectedIndex);
                                    }
                                }
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
        if(mIsInitialLoad){
            mIsInitialLoad = false;
        }else{
            if(mListView!=null && mOrdersTrackingListAdapter!=null && mOrderDB!=null){
                int oldListSize = mOrdersTrackingListAdapter.getCount();
                mOrdersTrackingListAdapter.setData(mOrderDB.getActiveOrdersWithTracking());
                if(mOrdersTrackingListAdapter.getCount()>0 && getActivity()!=null){
                    if(mOrdersTrackingListAdapter.getCount()!=oldListSize){
                        ((Callback) getActivity()).onListIsLoaded();
                    }else{
                        ((Callback) getActivity()).setSelectedIndex(mCurrentSelectedIndex);
                    }
                }else{
                    ((Callback) getActivity()).onListIsLoaded();
                }
            } else {
                ((Callback) getActivity()).onListIsLoaded();
            }
        }
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        try {
            outState.putInt(STATE_LIST_VIEW_INDEX, mListView.getFirstVisiblePosition());
        } catch (Exception e) {
            outState.putInt(STATE_LIST_VIEW_INDEX, mListViewIndex);
        }
        try {
            outState.putInt(STATE_LIST_VIEW_TOP, (mListView.getChildAt(0) == null) ? 0 :
                    (mListView.getChildAt(0).getTop() - mListView.getPaddingTop()));
        } catch (Exception e) {
            outState.putInt(STATE_LIST_VIEW_TOP, mListViewTop);
        }
        outState.putInt(STATE_CURRENT_SELECTED_INDEX, mCurrentSelectedIndex);
        super.onSaveInstanceState(outState);
    }
}
