package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.SubCategoryAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductSubCategory;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

/**
 * Created by Alberto on 26/3/2016.
 */
public class SubCategoriesListFragment extends Fragment {

    private static final String TAG = SubCategoriesListFragment.class.getSimpleName();
    public static final String KEY_CATEGORY_ID = "key_category_id";

    private ListView mListView;
    private SubCategoryAdapter mCategoryAdapter;
    private int mCategoryId;
    private User mCurrentUser;

    public SubCategoriesListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categories_list, container, false);

        if(getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null) {
            if(getActivity().getIntent().getExtras().containsKey(KEY_CATEGORY_ID)) {
                mCategoryId = getActivity().getIntent().getExtras().getInt(KEY_CATEGORY_ID);
            }
            if(getActivity().getIntent().getExtras().containsKey(SubCategoriesListActivity.KEY_CURRENT_USER)){
                mCurrentUser = getActivity().getIntent().getExtras().getParcelable(SubCategoriesListActivity.KEY_CURRENT_USER);
            }
        }else if(getArguments()!=null){
            if(getArguments().containsKey(KEY_CATEGORY_ID)){
                mCategoryId = getArguments().getInt(KEY_CATEGORY_ID);
            }
            if(getArguments().containsKey(SubCategoriesListActivity.KEY_CURRENT_USER)){
                mCurrentUser = getArguments().getParcelable(SubCategoriesListActivity.KEY_CURRENT_USER);
            }
        }

        mCategoryAdapter = new SubCategoryAdapter(getActivity(),
                Utils.getSubCategoriesByCategoryId(mCategoryId));

        mListView = (ListView) rootView.findViewById(R.id.categories_list);
        mListView.setAdapter(mCategoryAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                ProductSubCategory productSubCategory = (ProductSubCategory) adapterView.getItemAtPosition(position);
                if (productSubCategory != null) {
                    startActivity(new Intent(getActivity(), ProductsListActivity.class)
                            .putExtra(ProductsListActivity.KEY_PRODUCT_SUBCATEGORY_ID, productSubCategory.getId())
                    .putExtra(ProductsListActivity.KEY_CURRENT_USER, mCurrentUser));
                    getActivity().finish();
                }
            }
        });
        return rootView;
    }

}
