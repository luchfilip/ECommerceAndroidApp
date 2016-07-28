package com.smartbuilders.smartsales.ecommerce;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.model.UserProfile;
import com.smartbuilders.smartsales.ecommerce.adapters.OrdersListAdapter;
import com.smartbuilders.smartsales.ecommerce.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.data.OrderDB;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerce.model.Order;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.util.ArrayList;

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
    private TextView mBusinessPartnerCommercialName;
    private View mBusinessPartnerInfoSeparator;

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

                    mUser = Utils.getCurrentUser(getContext());

                    mOrderDB = new OrderDB(getContext(), mUser);
                    activeOrders.addAll(mOrderDB.getActiveOrders());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mBusinessPartnerCommercialName = (TextView) view.findViewById(R.id.business_partner_commercial_name_textView);
                                mBusinessPartnerInfoSeparator = view.findViewById(R.id.business_partner_info_separator);
                                setHeader();

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
            setHeader();
            if(mListView!=null && mOrdersListAdapter!=null && mOrderDB!=null){
                int oldListSize = mOrdersListAdapter.getCount();
                mOrdersListAdapter.setData(mOrderDB.getActiveOrders());
                if(mOrdersListAdapter.getCount()>0 && getActivity()!=null){
                    if(mOrdersListAdapter.getCount()!=oldListSize){
                        ((Callback) getActivity()).onListIsLoaded();
                    }else{
                        ((Callback) getActivity()).setSelectedIndex(mCurrentSelectedIndex);
                    }
                }
            }
        }
        super.onStart();
    }

    private void setHeader(){
        if(mUser!=null && mUser.getUserProfileId()==UserProfile.SALES_MAN_PROFILE_ID){
            try {
                BusinessPartner businessPartner = (new BusinessPartnerDB(getContext(), mUser))
                        .getActiveBusinessPartnerById(Utils.getAppCurrentBusinessPartnerId(getContext(), mUser));
                if(businessPartner!=null){
                    mBusinessPartnerCommercialName.setText(getString(R.string.business_partner_detail, businessPartner.getCommercialName()));
                    mBusinessPartnerCommercialName.setVisibility(View.VISIBLE);
                    mBusinessPartnerInfoSeparator.setVisibility(View.VISIBLE);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
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