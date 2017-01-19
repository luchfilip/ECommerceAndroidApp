package com.smartbuilders.smartsales.ecommerce;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.adapters.OrdersListAdapter;
import com.smartbuilders.smartsales.ecommerce.data.OrderDB;
import com.smartbuilders.smartsales.ecommerce.model.Order;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public class OrdersListFragment extends Fragment {

    private static final String STATE_CURRENT_SELECTED_INDEX = "STATE_CURRENT_SELECTED_INDEX";
    private static final String STATE_LIST_VIEW_INDEX = "STATE_LIST_VIEW_INDEX";
    private static final String STATE_LIST_VIEW_TOP = "STATE_LIST_VIEW_TOP";

    private boolean mIsInitialLoad;
    private ListView mListView;
    private OrdersListAdapter mOrdersListAdapter;
    private OrderDB mOrderDB;
    private int mListViewIndex;
    private int mListViewTop;
    private int mCurrentSelectedIndex;
    private User mUser;

    public interface Callback {
        void onItemSelected(Order order, int selectedIndex);
        void onItemLongSelected(Order order);
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
                    mOrdersListAdapter = new OrdersListAdapter(getActivity(), mOrderDB.getActiveOrders());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (view.findViewById(R.id.empty_orders_list_imageView) != null) {
                                    ((ImageView) view.findViewById(R.id.empty_orders_list_imageView))
                                            .setColorFilter(Utils.getColor(getActivity(), R.color.colorPrimary));
                                }

                                if (view.findViewById(R.id.search_fab) != null) {
                                    view.findViewById(R.id.search_fab).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            startActivity(new Intent(getActivity(), SearchResultsActivity.class));
                                        }
                                    });
                                }

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
                                            ((Callback) getActivity()).onItemSelected(order, position);
                                        }
                                    }
                                });
                                if (Parameter.isDeactiveOrderAvailable(getContext(), mUser)) {
                                    mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                        @Override
                                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                            // CursorAdapter returns a cursor at the correct position for getItem(), or null
                                            // if it cannot seek to that position.
                                            Order order = (Order) parent.getItemAtPosition(position);
                                            if (order != null) {
                                                ((Callback) getActivity()).onItemLongSelected(order);
                                            }
                                            return true;
                                        }
                                    });
                                }
                                mListView.setSelectionFromTop(mListViewIndex, mListViewTop);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                view.findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                if (getActivity()!=null) {
                                    if (savedInstanceState==null || mOrdersListAdapter==null || mOrdersListAdapter.isEmpty()) {
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
                int oldListSize = mOrdersListAdapter.getCount();
                mOrdersListAdapter.setData(mOrderDB.getActiveOrders());
                if(mOrdersListAdapter.getCount()>0 && getActivity()!=null){
                    if(mOrdersListAdapter.getCount()!=oldListSize){
                        ((Callback) getActivity()).onListIsLoaded();
                    }else{
                        ((Callback) getActivity()).setSelectedIndex(mCurrentSelectedIndex);
                    }
                }else{
                    ((Callback) getActivity()).onListIsLoaded();
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
