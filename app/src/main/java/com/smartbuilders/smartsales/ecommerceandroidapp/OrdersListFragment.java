package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.OrdersListAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Order;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class OrdersListFragment extends Fragment {

    private static final String STATE_CURRENT_SELECTED_INDEX = "STATE_CURRENT_SELECTED_INDEX";
    private static final String STATE_LIST_VIEW_INDEX = "STATE_LIST_VIEW_INDEX";
    private static final String STATE_LIST_VIEW_TOP = "STATE_LIST_VIEW_TOP";

    private boolean mIsInitialLoad;
    private int mOrdersListSize;
    private ListView mListView;
    private OrdersListAdapter mOrdersListAdapter;
    private OrderDB mOrderDB;
    private int mListViewIndex;
    private int mListViewTop;
    private int mCurrentSelectedIndex;

    public interface Callback {
        void onItemSelected(Order order);
        void onListIsLoaded();
        void setSelectedIndex(int selectedIndex);
    }

    public OrdersListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_orders_list, container, false);
        mIsInitialLoad = true;

        final ArrayList<Order> activeOrders = new ArrayList<>();

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

                    mOrderDB = new OrderDB(getContext(), Utils.getCurrentUser(getContext()));
                    activeOrders.addAll(mOrderDB.getActiveOrders());
                    mOrdersListSize = activeOrders.size();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mOrdersListAdapter = new OrdersListAdapter(getActivity(), activeOrders);

                                mListView = (ListView) view.findViewById(R.id.orders_list);
                                mListView.setAdapter(mOrdersListAdapter);
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
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                view.findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                if (getActivity()!=null) {
                                    if (savedInstanceState==null) {
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
            if(mListView!=null && mOrdersListAdapter!=null && mOrderDB!=null){
                mOrdersListAdapter.setData(mOrderDB.getActiveOrders());
                if(mOrdersListAdapter.getCount()>0 && getActivity()!=null){
                    if(mOrdersListAdapter.getCount()!=mOrdersListSize){
                        ((Callback) getActivity()).onListIsLoaded();
                    }else{
                        ((Callback) getActivity()).setSelectedIndex(mCurrentSelectedIndex);
                    }
                }
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
