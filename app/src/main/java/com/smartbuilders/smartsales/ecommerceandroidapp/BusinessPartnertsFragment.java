package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.BusinessPartnertsListAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.BusinessPartner;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class BusinessPartnertsFragment extends Fragment {

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
    private BusinessPartnertsListAdapter businessPartnertsListAdapter;

    public BusinessPartnertsFragment() {
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

        View rootView = inflater.inflate(R.layout.fragment_business_partners, container, false);

        mListView = (ListView) rootView.findViewById(R.id.business_partnerts_list);
        businessPartnertsListAdapter = new BusinessPartnertsListAdapter(getContext(), new ArrayList<BusinessPartner>());
        mListView.setAdapter(businessPartnertsListAdapter);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final BusinessPartner businessPartner = (BusinessPartner) parent.getItemAtPosition(position);
                if (businessPartner != null) {
                    new AlertDialog.Builder(getContext())
                            .setMessage(getString(R.string.delete_business_partner, businessPartner.getCommercialName()))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String result = businessPartnerDB.deactivateBusinessPartner(businessPartner);
                                    if (result==null) {
                                        businessPartnertsListAdapter.setData(businessPartnerDB.getActiveBusinessPartners());
                                    } else {
                                        Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                }
                return true;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final BusinessPartner businessPartner = (BusinessPartner) parent.getItemAtPosition(position);
                if (businessPartner != null) {
                    startActivity(new Intent(getContext(), RegisterBusinessPartnerActivity.class)
                    .putExtra(RegisterBusinessPartnerActivity.KEY_CURRENT_USER, mCurrentUser)
                    .putExtra(RegisterBusinessPartnerActivity.KEY_BUSINESS_PARTNER, businessPartner));
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        businessPartnertsListAdapter.setData(businessPartnerDB.getActiveBusinessPartners());
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
