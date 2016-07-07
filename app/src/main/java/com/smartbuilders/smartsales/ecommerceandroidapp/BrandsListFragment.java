package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.BrandsListAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ProductBrandDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductBrand;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class BrandsListFragment extends Fragment {

    private static final String STATE_LIST_VIEW_INDEX = "STATE_LIST_VIEW_INDEX";
    private static final String STATE_LIST_VIEW_TOP = "STATE_LIST_VIEW_TOP";

    // save index and top position
    int mListViewIndex;
    int mListViewTop;

    private ListView mListView;
    private BrandsListAdapter mBrandsListAdapter;

    public BrandsListFragment() {
    }

    public interface Callback{
        void onListLoaded();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_brands_list, container, false);
        setHasOptionsMenu(true);

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState != null) {
                        if(savedInstanceState.containsKey(STATE_LIST_VIEW_INDEX)){
                            mListViewIndex = savedInstanceState.getInt(STATE_LIST_VIEW_INDEX);
                        }
                        if(savedInstanceState.containsKey(STATE_LIST_VIEW_TOP)){
                            mListViewTop = savedInstanceState.getInt(STATE_LIST_VIEW_TOP);
                        }
                    }
                    mBrandsListAdapter = new BrandsListAdapter(getContext(), new ProductBrandDB(getContext())
                            .getActiveProductBrands());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mListView = (ListView) rootView.findViewById(R.id.brands_list);
                                mListView.setAdapter(mBrandsListAdapter);
                                mListView.setFastScrollEnabled(true);

                                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                                        // CursorAdapter returns a cursor at the correct position for getItem(), or null
                                        // if it cannot seek to that position.
                                        ProductBrand productBrand = (ProductBrand) adapterView.getItemAtPosition(position);
                                        if (productBrand != null) {
                                            Intent intent = new Intent(getActivity(), ProductsListActivity.class);
                                            intent.putExtra(ProductsListActivity.KEY_PRODUCT_BRAND_ID, productBrand.getId());
                                            startActivity(intent);
                                        }
                                    }
                                });

                                mListView.setSelectionFromTop(mListViewIndex, mListViewTop);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                ((Callback) getActivity()).onListLoaded();
                                rootView.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                rootView.findViewById(R.id.brands_list).setVisibility(View.VISIBLE);
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
        super.onSaveInstanceState(outState);
    }
}
