package com.smartbuilders.smartsales.salesforcesystem;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.model.PricesList;
import com.smartbuilders.smartsales.salesforcesystem.adapters.PricesListAdapter;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PricesListFragment extends Fragment {

    private static final String STATE_CURRENT_SELECTED_INDEX = "STATE_CURRENT_SELECTED_INDEX";
    private static final String STATE_LIST_VIEW_INDEX = "STATE_LIST_VIEW_INDEX";
    private static final String STATE_LIST_VIEW_TOP = "STATE_LIST_VIEW_TOP";

    // save index and top position
    int mListViewIndex;
    int mListViewTop;

    private ListView mListView;
    private int mCurrentSelectedIndex;

    public interface Callback {
        void onItemSelected(int productCategoryId);
        void onPricesListIsLoaded();
        void setSelectedIndex(int selectedIndex);
    }

    public PricesListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_prices_list, container, false);

        final ArrayList<PricesList> pricesLists = new ArrayList<>();

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState != null) {
                        if (savedInstanceState.containsKey(STATE_CURRENT_SELECTED_INDEX)) {
                            mCurrentSelectedIndex = savedInstanceState.getInt(STATE_CURRENT_SELECTED_INDEX) ;
                        }
                        if(savedInstanceState.containsKey(STATE_LIST_VIEW_INDEX)){
                            mListViewIndex = savedInstanceState.getInt(STATE_LIST_VIEW_INDEX);
                        }
                        if(savedInstanceState.containsKey(STATE_LIST_VIEW_TOP)){
                            mListViewTop = savedInstanceState.getInt(STATE_LIST_VIEW_TOP);
                        }
                    }
                    PricesList pricesList = new PricesList();
                    pricesList.setId(2);
                    pricesList.setName("Detal");
                    pricesLists.add(pricesList);

                    pricesList = new PricesList();
                    pricesList.setId(1);
                    pricesList.setName("Ferreteros");
                    pricesLists.add(pricesList);

                    pricesList = new PricesList();
                    pricesList.setId(0);
                    pricesList.setName("Constructoras");

                    pricesLists.add(pricesList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mListView = (ListView) rootView.findViewById(R.id.prices_list_listView);
                                mListView.setAdapter(new PricesListAdapter(getActivity(), pricesLists));

                                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                                        mCurrentSelectedIndex = position;
                                        // CursorAdapter returns a cursor at the correct position for getItem(), or null
                                        // if it cannot seek to that position.
                                        PricesList pricesList = (PricesList) adapterView.getItemAtPosition(position);
                                        if (pricesList != null) {
                                            ((PricesListFragment.Callback) getActivity()).onItemSelected(pricesList.getId());
                                        }
                                    }
                                });

                                mListView.setSelectionFromTop(mListViewIndex, mListViewTop);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                rootView.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                rootView.findViewById(R.id.prices_list_listView).setVisibility(View.VISIBLE);
                                if (savedInstanceState==null && getActivity()!=null) {
                                    ((PricesListFragment.Callback) getActivity()).onPricesListIsLoaded();
                                } else {
                                    ((PricesListFragment.Callback) getActivity()).setSelectedIndex(mCurrentSelectedIndex);
                                }
                            }
                        }
                    });
                }
            }
        }.start();
        return rootView;
    }
}
