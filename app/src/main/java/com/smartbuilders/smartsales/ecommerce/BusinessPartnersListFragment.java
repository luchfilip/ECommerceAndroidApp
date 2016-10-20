package com.smartbuilders.smartsales.ecommerce;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.model.UserProfile;
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
    private static final String STATE_CURRENT_FILTER_TEXT = "STATE_CURRENT_FILTER_TEXT";
    private static final String STATE_CURRENT_FILTER_OPTION = "STATE_CURRENT_FILTER_OPTION";
    private static final String STATE_SPINNER_SELECTED_ITEM_POSITION = "STATE_SPINNER_SELECTED_ITEM_POSITION";

    private boolean mIsInitialLoad;
    private ListView mListView;
    private int mListViewIndex;
    private int mListViewTop;
    private int mCurrentSelectedIndex;
    private String mCurrentFilterText;
    private int mCurrentFilterOption;
    private int mSpinnerSelectedItemPosition;
    private BusinessPartnerDB mBusinessPartnerDB;
    private UserBusinessPartnerDB mUserBusinessPartnerDB;
    private BusinessPartnersListAdapter mBusinessPartnersListAdapter;
    private Spinner mFilterByOptionsSpinner;

    public interface Callback {
        void onItemSelected(int businessPartnerId);
        void onItemLongSelected(int businessPartnerId, String businessPartnerName, User user);
        void onListIsLoaded();
        void setSelectedIndex(int selectedIndex);
        Integer getBusinessPartnerIdInDetailFragment();
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
                        if(savedInstanceState.containsKey(STATE_CURRENT_FILTER_TEXT)){
                            mCurrentFilterText = savedInstanceState.getString(STATE_CURRENT_FILTER_TEXT);
                        }
                        if(savedInstanceState.containsKey(STATE_CURRENT_FILTER_OPTION)){
                            mCurrentFilterOption = savedInstanceState.getInt(STATE_CURRENT_FILTER_OPTION);
                        }
                        if(savedInstanceState.containsKey(STATE_SPINNER_SELECTED_ITEM_POSITION)){
                            mSpinnerSelectedItemPosition = savedInstanceState.getInt(STATE_SPINNER_SELECTED_ITEM_POSITION);
                        }
                    }
                    if(user!=null){
                        if(BuildConfig.IS_SALES_FORCE_SYSTEM || user.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
                            mBusinessPartnerDB = new BusinessPartnerDB(getContext(), user);
                            mBusinessPartnersListAdapter = new BusinessPartnersListAdapter(getContext(),
                                    mBusinessPartnerDB.getBusinessPartners(),
                                    Utils.getAppCurrentBusinessPartnerId(getContext(), user));
                        }else if(user.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                            mUserBusinessPartnerDB = new UserBusinessPartnerDB(getContext(), user);
                            mBusinessPartnersListAdapter = new BusinessPartnersListAdapter(getContext(),
                                    mUserBusinessPartnerDB.getUserBusinessPartners(), 0);
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

                                /*******************************************************************************/
                                if (getActivity() != null) {
                                    mFilterByOptionsSpinner = (Spinner) getActivity().findViewById(R.id.filter_by_options_spinner);

                                    if (mBusinessPartnersListAdapter != null) {
                                        mFilterByOptionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(final AdapterView<?> parent, View view, int position, long id) {
                                                mSpinnerSelectedItemPosition = position;
                                                if (parent.getItemAtPosition(position) != null) {
                                                    if (!TextUtils.isEmpty(mCurrentFilterText)) {
                                                        mBusinessPartnersListAdapter.filter(((Callback) getActivity()).getBusinessPartnerIdInDetailFragment(),
                                                                mCurrentFilterText, (String) parent.getItemAtPosition(position));
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {
                                            }
                                        });
                                        mFilterByOptionsSpinner.setSelection(mSpinnerSelectedItemPosition);
                                    }

                                    final EditText filterBusinessPartnerTextView = (EditText) getActivity().findViewById(R.id.filter_businessPartner_editText);
                                    final ImageView filterImageView = (ImageView) getActivity().findViewById(R.id.filter_imageView);
                                    if (filterBusinessPartnerTextView != null && filterImageView != null) {
                                        filterBusinessPartnerTextView.setFocusableInTouchMode(true);

                                        final View.OnClickListener filterImageViewOnClickListener =
                                                new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        filterBusinessPartnerTextView.setText(null);
                                                    }
                                                };
                                        filterBusinessPartnerTextView.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                            }

                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                if (s.length() > 0) {
                                                    filterImageView.setImageResource(R.drawable.ic_close_black_24dp);
                                                    filterImageView.setOnClickListener(filterImageViewOnClickListener);
                                                } else {
                                                    filterImageView.setImageResource(R.drawable.ic_filter_list_black_24dp);
                                                    filterImageView.setOnClickListener(null);
                                                }
                                                mCurrentFilterText = s.toString();
                                                mBusinessPartnersListAdapter.filter(((Callback) getActivity()).getBusinessPartnerIdInDetailFragment(),
                                                        mCurrentFilterText, (String) mFilterByOptionsSpinner.getItemAtPosition(mSpinnerSelectedItemPosition));
                                            }

                                            @Override
                                            public void afterTextChanged(Editable s) {
                                            }
                                        });
                                        filterBusinessPartnerTextView.setText(mCurrentFilterText);
                                        filterBusinessPartnerTextView.setSelection(filterBusinessPartnerTextView.length());
                                    }
                                }
                                /*******************************************************************************/
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
                    mBusinessPartnersListAdapter.setData(mUserBusinessPartnerDB.getUserBusinessPartners());
                }else if(mBusinessPartnerDB!=null) {
                    mBusinessPartnersListAdapter.setData(mBusinessPartnerDB.getBusinessPartners());
                }
                mBusinessPartnersListAdapter.filter(((Callback) getActivity()).getBusinessPartnerIdInDetailFragment(),
                        mCurrentFilterText, (String) mFilterByOptionsSpinner.getItemAtPosition(mSpinnerSelectedItemPosition));
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
        outState.putString(STATE_CURRENT_FILTER_TEXT, mCurrentFilterText);
        outState.putInt(STATE_CURRENT_FILTER_OPTION, mCurrentFilterOption);
        outState.putInt(STATE_SPINNER_SELECTED_ITEM_POSITION, mSpinnerSelectedItemPosition);
        super.onSaveInstanceState(outState);
    }
}
