package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.BusinessPartnersListAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.BusinessPartnerDB;
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

    private ListView mListView;
    private int mListViewIndex;
    private int mListViewTop;
    private int mCurrentSelectedIndex;
    private BusinessPartnerDB businessPartnerDB;
    private BusinessPartnersListAdapter businessPartnersListAdapter;

    public interface Callback {
        public void onItemSelected(BusinessPartner businessPartner);
        public void onItemLongSelected(BusinessPartner businessPartner);
        public void onListIsLoaded();
        public void setSelectedIndex(int selectedIndex);
    }

    public BusinessPartnersListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_business_partners_list, container, false);

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
                    businessPartnerDB = new BusinessPartnerDB(getContext(), Utils.getCurrentUser(getContext()));
                    businessPartnersListAdapter = new BusinessPartnersListAdapter(getContext(),
                            businessPartnerDB.getActiveBusinessPartners());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mListView = (ListView) rootView.findViewById(R.id.business_partners_list);

                                mListView.setAdapter(businessPartnersListAdapter);

                                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        mCurrentSelectedIndex = position;
                                        final BusinessPartner businessPartner = (BusinessPartner) parent.getItemAtPosition(position);
                                        if (businessPartner != null) {
                                            ((Callback) getActivity()).onItemSelected(businessPartner);
                                        }
                                    }
                                });

                                mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                    @Override
                                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                        final BusinessPartner businessPartner = (BusinessPartner) parent.getItemAtPosition(position);
                                        if (businessPartner != null) {
                                            ((Callback) getActivity()).onItemLongSelected(businessPartner);
                                        }
                                        return true;
                                    }
                                });

                                mListView.setSelectionFromTop(mListViewIndex, mListViewTop);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                rootView.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                rootView.findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
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

        return rootView;
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
