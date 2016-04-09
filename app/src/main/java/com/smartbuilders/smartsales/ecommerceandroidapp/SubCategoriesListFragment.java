package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.CategoryAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductCategory;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

/**
 * Created by Alberto on 26/3/2016.
 */
public class SubCategoriesListFragment extends Fragment {

    private static final String TAG = SubCategoriesListFragment.class.getSimpleName();
    public static final String KEY_CATEGORY_ID = "key_category_id";

    private ListView mListView;
    private CategoryAdapter mCategoryAdapter;
    private int mCategoryId;

    public SubCategoriesListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categories_list, container, false);

        if(getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null
                && getActivity().getIntent().getExtras().containsKey(KEY_CATEGORY_ID)) {
            mCategoryId = getActivity().getIntent().getExtras().getInt(KEY_CATEGORY_ID);
        }else if(getArguments()!=null && getArguments().containsKey(KEY_CATEGORY_ID)){
            mCategoryId = getArguments().getInt(KEY_CATEGORY_ID);
        }

        mCategoryAdapter = new CategoryAdapter(getActivity(),
                Utils.getSubCategoriesByCategoryId(mCategoryId));

        mListView = (ListView) rootView.findViewById(R.id.categories_list);
        mListView.setAdapter(mCategoryAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                ProductCategory productCategory = (ProductCategory) adapterView.getItemAtPosition(position);
                if (productCategory != null) {
                    startActivity(new Intent(getActivity(), ProductsListActivity.class));
                    getActivity().finish();
                }
            }
        });
        return rootView;
    }

}
