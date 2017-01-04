package com.smartbuilders.smartsales.ecommerce;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.smartbuilders.smartsales.ecommerce.adapters.CategoryAdapter;
import com.smartbuilders.smartsales.ecommerce.data.ProductCategoryDB;
import com.smartbuilders.smartsales.ecommerce.model.ProductCategory;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Alberto on 26/3/2016.
 */
public class CategoriesListFragment extends Fragment {

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
        void onItemLongSelected(int productCategoryId);
        void onCategoriesListIsLoaded();
        void setSelectedIndex(int selectedIndex);
    }

    public CategoriesListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_categories_list, container, false);

        final ArrayList<ProductCategory> productCategories = new ArrayList<>();

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
                    productCategories.addAll(new ProductCategoryDB(getContext(), Utils.getCurrentUser(getContext()))
                            .getProductCategories()) ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mListView = (ListView) rootView.findViewById(R.id.categories_list);
                                mListView.setAdapter(new CategoryAdapter(getActivity(), productCategories));

                                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                                        mCurrentSelectedIndex = position;
                                        // CursorAdapter returns a cursor at the correct position for getItem(), or null
                                        // if it cannot seek to that position.
                                        ProductCategory productCategory = (ProductCategory) adapterView.getItemAtPosition(position);
                                        if (productCategory != null) {
                                            ((Callback) getActivity()).onItemSelected(productCategory.getId());
                                        }
                                    }
                                });

                                mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                    @Override
                                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                        ProductCategory productCategory = (ProductCategory) parent.getItemAtPosition(position);
                                        if (productCategory != null) {
                                            ((Callback) getActivity()).onItemLongSelected(productCategory.getId());
                                        }
                                        return true;
                                    }
                                });

                                mListView.setSelectionFromTop(mListViewIndex, mListViewTop);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                rootView.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                rootView.findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
                                if (savedInstanceState==null && getActivity()!=null) {
                                    ((Callback) getActivity()).onCategoriesListIsLoaded();
                                } else {
                                    ((Callback) getActivity()).setSelectedIndex(mCurrentSelectedIndex);
                                }
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
        outState.putInt(STATE_CURRENT_SELECTED_INDEX, mCurrentSelectedIndex);
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
