package com.smartbuilders.smartsales.ecommerce;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.smartbuilders.ids.model.User;
import com.smartbuilders.ids.model.UserProfile;
import com.smartbuilders.smartsales.ecommerce.adapters.BusinessPartnersListAdapter;
import com.smartbuilders.smartsales.ecommerce.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.data.UserBusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public class BusinessPartnersListFragment extends Fragment {

    private static final String STATE_CURRENT_SELECTED_INDEX = "STATE_CURRENT_SELECTED_INDEX";
    private static final String STATE_LIST_VIEW_INDEX = "STATE_LIST_VIEW_INDEX";
    private static final String STATE_LIST_VIEW_TOP = "STATE_LIST_VIEW_TOP";

    private boolean mIsInitialLoad;
    private ListView mListView;
    private int mListViewIndex;
    private int mListViewTop;
    private int mCurrentSelectedIndex;
    private BusinessPartnerDB mBusinessPartnerDB;
    private UserBusinessPartnerDB mUserBusinessPartnerDB;
    private BusinessPartnersListAdapter mBusinessPartnersListAdapter;

    public interface Callback {
        void onItemSelected(int businessPartnerId);
        void onItemLongSelected(int businessPartnerId, String businessPartnerName, User user);
        void onListIsLoaded();
        void setSelectedIndex(int selectedIndex);
    }

    public BusinessPartnersListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_business_partners_list, container, false);
        mIsInitialLoad = true;

        final User user = Utils.getCurrentUser(getContext());

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
                    if(user!=null){
                        if(user.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                            mUserBusinessPartnerDB = new UserBusinessPartnerDB(getContext(), user);
                            mBusinessPartnersListAdapter = new BusinessPartnersListAdapter(getContext(),
                                    mUserBusinessPartnerDB.getActiveUserBusinessPartners(), 0);
                        }else if(user.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
                            mBusinessPartnerDB = new BusinessPartnerDB(getContext(), user);
                            mBusinessPartnersListAdapter = new BusinessPartnersListAdapter(getContext(),
                                    mBusinessPartnerDB.getActiveBusinessPartners(),
                                    Utils.getAppCurrentBusinessPartnerId(getContext(), user));
                        }
                    }
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
                                            ((Callback) getActivity()).onItemLongSelected(businessPartner.getId(), businessPartner.getName(), user);
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
            if(mListView!=null && mBusinessPartnersListAdapter!=null){
                if(mUserBusinessPartnerDB!=null) {
                    mBusinessPartnersListAdapter.setData(mUserBusinessPartnerDB.getActiveUserBusinessPartners());
                }else if(mBusinessPartnerDB!=null) {
                    mBusinessPartnersListAdapter.setData(mBusinessPartnerDB.getActiveBusinessPartners());
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
