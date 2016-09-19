package com.smartbuilders.smartsales.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.smartbuilders.smartsales.ecommerce.adapters.SubCategoryAdapter;
import com.smartbuilders.smartsales.ecommerce.data.ProductSubCategoryDB;
import com.smartbuilders.smartsales.ecommerce.model.ProductSubCategory;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.util.ArrayList;

/**
 * Jesus Sarco, 26/3/2016.
 */
public class SubCategoriesListFragment extends Fragment {

    private static final String STATE_CATEGORY_ID = "STATE_CATEGORY_ID";
    private static final String STATE_LIST_VIEW_INDEX = "STATE_LIST_VIEW_INDEX";
    private static final String STATE_LIST_VIEW_TOP = "STATE_LIST_VIEW_TOP";

    // save index and top position
    private int mListViewIndex;
    private int mListViewTop;
    private ListView mListView;
    private int mCategoryId;

    public SubCategoriesListFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_sub_categories_list, container, false);

        final ArrayList<ProductSubCategory> productSubCategories = new ArrayList<>();

        new Thread() {
            @Override
            public void run() {
                try {
                    if (savedInstanceState!=null) {
                        if (savedInstanceState.containsKey(STATE_CATEGORY_ID)) {
                            mCategoryId = savedInstanceState.getInt(STATE_CATEGORY_ID);
                        }
                        if(savedInstanceState.containsKey(STATE_LIST_VIEW_INDEX)){
                            mListViewIndex = savedInstanceState.getInt(STATE_LIST_VIEW_INDEX);
                        }
                        if(savedInstanceState.containsKey(STATE_LIST_VIEW_TOP)){
                            mListViewTop = savedInstanceState.getInt(STATE_LIST_VIEW_TOP);
                        }
                    }

                    if(getArguments()!=null){
                        if(getArguments().containsKey(SubCategoriesListActivity.KEY_CATEGORY_ID)){
                            mCategoryId = getArguments().getInt(SubCategoriesListActivity.KEY_CATEGORY_ID);
                        }
                    }else if(getActivity()!=null && getActivity().getIntent()!=null
                            && getActivity().getIntent().getExtras()!=null) {
                        if(getActivity().getIntent().getExtras().containsKey(SubCategoriesListActivity.KEY_CATEGORY_ID)) {
                            mCategoryId = getActivity().getIntent().getExtras().getInt(SubCategoriesListActivity.KEY_CATEGORY_ID);
                        }
                    }

                    productSubCategories.addAll((new ProductSubCategoryDB(getContext(), Utils.getCurrentUser(getContext())))
                            .getActiveProductSubCategoriesByCategoryId(mCategoryId));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mListView = (ListView) rootView.findViewById(R.id.sub_categories_list);
                                mListView.setAdapter(new SubCategoryAdapter(getContext(), productSubCategories));

                                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                                        // CursorAdapter returns a cursor at the correct position for getItem(), or null
                                        // if it cannot seek to that position.
                                        ProductSubCategory productSubCategory = (ProductSubCategory) adapterView.getItemAtPosition(position);
                                        if (productSubCategory != null) {
                                            Intent intent = new Intent(getContext(), ProductsListActivity.class);
                                            intent.putExtra(ProductsListActivity.KEY_PRODUCT_SUBCATEGORY_ID, productSubCategory.getId());
                                            startActivity(intent);
                                        }
                                    }
                                });

                                mListView.setSelectionFromTop(mListViewIndex, mListViewTop);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                rootView.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                rootView.findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
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
        outState.putInt(STATE_CATEGORY_ID, mCategoryId);
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