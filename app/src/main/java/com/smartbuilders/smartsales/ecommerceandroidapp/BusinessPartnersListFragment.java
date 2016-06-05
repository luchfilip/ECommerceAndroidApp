package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.BusinessPartnersListAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.BusinessPartner;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class BusinessPartnersListFragment extends Fragment {

    public static final String KEY_CURRENT_USER = "KEY_CURRENT_USER";

    private static final String STATE_CURRENT_USER = "state_current_user";
    private static final String STATE_LISTVIEW_INDEX = "STATE_LISTVIEW_INDEX";
    private static final String STATE_LISTVIEW_TOP = "STATE_LISTVIEW_TOP";

    private ListView mListView;
    // save index and top position
    int mListViewIndex;
    int mListViewTop;
    private User mCurrentUser;
    private BusinessPartnerDB businessPartnerDB;
    private BusinessPartnersListAdapter businessPartnersListAdapter;

    public interface Callback {
        public void onItemSelected(BusinessPartner businessPartner);
        public void onItemLongSelected(BusinessPartner businessPartner);
    }

    public BusinessPartnersListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
            if(savedInstanceState.containsKey(STATE_LISTVIEW_INDEX)){
                mListViewIndex = savedInstanceState.getInt(STATE_LISTVIEW_INDEX);
            }
            if(savedInstanceState.containsKey(STATE_LISTVIEW_TOP)){
                mListViewTop = savedInstanceState.getInt(STATE_LISTVIEW_TOP);
            }
        }

        if(getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null){
            if(getActivity().getIntent().getExtras().containsKey(KEY_CURRENT_USER)){
                mCurrentUser = getActivity().getIntent().getExtras().getParcelable(KEY_CURRENT_USER);
            }
        }

        businessPartnerDB = new BusinessPartnerDB(getContext(), mCurrentUser);

        View rootView = inflater.inflate(R.layout.fragment_business_partners_list, container, false);

        mListView = (ListView) rootView.findViewById(R.id.business_partners_list);
        businessPartnersListAdapter = new BusinessPartnersListAdapter(getContext(), businessPartnerDB.getActiveBusinessPartners());
        mListView.setAdapter(businessPartnersListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

        return rootView;
    }

    @Override
    public void onResume() {
        businessPartnersListAdapter.setData(businessPartnerDB.getActiveBusinessPartners());
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
        try {
            outState.putInt(STATE_LISTVIEW_INDEX, mListView.getFirstVisiblePosition());
            outState.putInt(STATE_LISTVIEW_TOP,
                    (mListView.getChildAt(0) == null) ? 0 : (mListView.getChildAt(0).getTop() - mListView.getPaddingTop()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onSaveInstanceState(outState);
    }
}
