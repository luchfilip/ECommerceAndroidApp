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

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.BrandAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ProductBrandDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductBrand;

/**
 * A placeholder fragment containing a simple view.
 */
public class BrandsListFragment extends Fragment {

    private BrandAdapter mBrandAdapter;
    private User mCurrentUser;

    public BrandsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_brands_list, container, false);
        setHasOptionsMenu(true);

        if(getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null){
            if(getActivity().getIntent().getExtras().containsKey(BrandsListActivity.KEY_CURRENT_USER)){
                mCurrentUser = getActivity().getIntent().getExtras().getParcelable(BrandsListActivity.KEY_CURRENT_USER);
            }
        }

        mBrandAdapter = new BrandAdapter(getContext(),
                new ProductBrandDB(getContext(), mCurrentUser).getActiveProductBrands());

        ListView lv = (ListView) rootView.findViewById(R.id.brands_list);
        lv.setAdapter(mBrandAdapter);
        lv.setFastScrollEnabled(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                ProductBrand productBrand = (ProductBrand) adapterView.getItemAtPosition(position);
                if (productBrand != null) {
                    Intent intent = new Intent(getActivity(), ProductsListActivity.class);
                    intent.putExtra(ProductsListActivity.KEY_CURRENT_USER, mCurrentUser);
                    intent.putExtra(ProductsListActivity.KEY_PRODUCT_BRAND_ID, productBrand.getId());
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_brands_list, menu);

        SearchManager searchManager =
                (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchItem = menu.findItem(R.id.filter_brand);
        final SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // Some code here
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // Some code here
                mBrandAdapter.filter(s);
                return false;
            }
        });
    }
}
