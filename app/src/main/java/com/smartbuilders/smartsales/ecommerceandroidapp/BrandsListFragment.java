package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.BrandAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ProductBrandDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductBrand;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class BrandsListFragment extends Fragment {

    private static final String STATE_LIST_VIEW_INDEX = "STATE_LIST_VIEW_INDEX";
    private static final String STATE_LIST_VIEW_TOP = "STATE_LIST_VIEW_TOP";
    private static final String STATE_FILTER_TEXT = "STATE_FILTER_TEXT";

    // save index and top position
    int mListViewIndex;
    int mListViewTop;

    private ListView mListView;
    private BrandAdapter mBrandAdapter;
    private String mFilterText;

    public BrandsListFragment() {
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
                        if(savedInstanceState.containsKey(STATE_FILTER_TEXT)){
                            mFilterText = savedInstanceState.getString(STATE_FILTER_TEXT);
                        }
                        if(savedInstanceState.containsKey(STATE_LIST_VIEW_INDEX)){
                            mListViewIndex = savedInstanceState.getInt(STATE_LIST_VIEW_INDEX);
                        }
                        if(savedInstanceState.containsKey(STATE_LIST_VIEW_TOP)){
                            mListViewTop = savedInstanceState.getInt(STATE_LIST_VIEW_TOP);
                        }
                    }
                    mBrandAdapter = new BrandAdapter(getContext(), new ProductBrandDB(getContext())
                            .getActiveProductBrands());
                    mBrandAdapter.filter(mFilterText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mListView = (ListView) rootView.findViewById(R.id.brands_list);
                                mListView.setAdapter(mBrandAdapter);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_brands_list, menu);

        SearchManager searchManager =
                (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchItem = menu.findItem(R.id.filter_brand);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.filter_title));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.post(new Runnable() {
            @Override
            public void run() {
                if(mFilterText!=null){
                    MenuItemCompat.expandActionView(searchItem);
                    searchView.setQuery(mFilterText, false);
                    searchView.clearFocus();
                }

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        // Some code here
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        if (mBrandAdapter!=null) {
                            mFilterText = s;
                            mBrandAdapter.filter(s);
                        }
                        return false;
                    }
                });
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mFilterText = mFilterText==null ? "" : mFilterText;
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mFilterText = null;
                return true;
            }
        });
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
        outState.putString(STATE_FILTER_TEXT, mFilterText);
        super.onSaveInstanceState(outState);
    }
}
