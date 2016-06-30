package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.BusinessPartnersListAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.UserBusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public class BusinessPartnersListFragment extends Fragment {

    private static final String STATE_CURRENT_SELECTED_INDEX = "STATE_CURRENT_SELECTED_INDEX";
    private static final String STATE_LISTVIEW_INDEX = "STATE_LISTVIEW_INDEX";
    private static final String STATE_LISTVIEW_TOP = "STATE_LISTVIEW_TOP";

    private boolean mIsInitialLoad;
    private ListView mListView;
    private int mListViewIndex;
    private int mListViewTop;
    private int mCurrentSelectedIndex;
    private UserBusinessPartnerDB mUserBusinessPartnerDB;
    private BusinessPartnersListAdapter mBusinessPartnersListAdapter;

    public interface Callback {
        void onItemSelected(int businessPartnerId);
        void onItemLongSelected(int businessPartnerId, String businessPartnerCommercialName);
        void onListIsLoaded();
        void setSelectedIndex(int selectedIndex);
        void reloadActivity();
    }

    public BusinessPartnersListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_business_partners_list, container, false);

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState != null) {
                        if(savedInstanceState.containsKey(STATE_CURRENT_SELECTED_INDEX)){
                            mCurrentSelectedIndex = savedInstanceState.getInt(STATE_CURRENT_SELECTED_INDEX);
                        }
                        if(savedInstanceState.containsKey(STATE_LISTVIEW_INDEX)){
                            mListViewIndex = savedInstanceState.getInt(STATE_LISTVIEW_INDEX);
                        }
                        if(savedInstanceState.containsKey(STATE_LISTVIEW_TOP)){
                            mListViewTop = savedInstanceState.getInt(STATE_LISTVIEW_TOP);
                        }
                    }
                    mUserBusinessPartnerDB = new UserBusinessPartnerDB(getContext(), Utils.getCurrentUser(getContext()));
                    mBusinessPartnersListAdapter = new BusinessPartnersListAdapter(getContext(),
                            mUserBusinessPartnerDB.getActiveUserBusinessPartners());
                    mIsInitialLoad = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mListView = (ListView) view.findViewById(R.id.business_partners_list);

                                mListView.setAdapter(mBusinessPartnersListAdapter);

                                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        mCurrentSelectedIndex = position;
                                        final BusinessPartner businessPartner = (BusinessPartner) parent.getItemAtPosition(position);
                                        if (businessPartner != null) {
                                            ((Callback) getActivity()).onItemSelected(businessPartner.getId());
                                        }
                                    }
                                });

                                mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                    @Override
                                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                        final BusinessPartner businessPartner = (BusinessPartner) parent.getItemAtPosition(position);
                                        if (businessPartner != null) {
                                            ((Callback) getActivity()).onItemLongSelected(businessPartner.getId(), businessPartner.getCommercialName());
                                        }
                                        return true;
                                    }
                                });

                                mListView.setSelectionFromTop(mListViewIndex, mListViewTop);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                view.findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
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
            if(mListView!=null && mBusinessPartnersListAdapter!=null) {
                mBusinessPartnersListAdapter.setData(mUserBusinessPartnerDB.getActiveUserBusinessPartners());
            }
        }
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        try {
            outState.putInt(STATE_LISTVIEW_INDEX, mListView.getFirstVisiblePosition());
        } catch (Exception e) {
            outState.putInt(STATE_LISTVIEW_INDEX, mListViewIndex);
        }
        try {
            outState.putInt(STATE_LISTVIEW_TOP, (mListView.getChildAt(0) == null) ? 0 :
                    (mListView.getChildAt(0).getTop() - mListView.getPaddingTop()));
        } catch (Exception e) {
            outState.putInt(STATE_LISTVIEW_TOP, mListViewTop);
        }
        outState.putInt(STATE_CURRENT_SELECTED_INDEX, mCurrentSelectedIndex);
        super.onSaveInstanceState(outState);
    }
}
