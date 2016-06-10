package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.CategoryAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ProductCategoryDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductCategory;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Alberto on 26/3/2016.
 */
public class CategoriesListFragment extends Fragment {

    public static final String STATE_CURRENT_SELECTED_INDEX = "STATE_CURRENT_SELECTED_INDEX";

    private int mCurrentSelectedIndex;

    public interface Callback {
        public void onItemSelected(ProductCategory productCategory);
        public void onItemLongSelected(ProductCategory productCategory);
        public void onCategoriesListIsLoaded(int selectedIndex);
    }

    public CategoriesListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState!=null) {
            if (savedInstanceState.containsKey(STATE_CURRENT_SELECTED_INDEX)) {
               mCurrentSelectedIndex = savedInstanceState.getInt(STATE_CURRENT_SELECTED_INDEX) ;
            }
        }
        final View rootView = inflater.inflate(R.layout.fragment_categories_list, container, false);

        final ArrayList<ProductCategory> productCategories = new ArrayList<>();

        new Thread() {
            @Override
            public void run() {
                try {
                    productCategories.addAll(new ProductCategoryDB(getContext(),
                            Utils.getCurrentUser(getContext())).getActiveProductCategories()) ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ListView listView = (ListView) rootView.findViewById(R.id.categories_list);
                                listView.setAdapter(new CategoryAdapter(getActivity(), productCategories));

                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                                        mCurrentSelectedIndex = position;
                                        // CursorAdapter returns a cursor at the correct position for getItem(), or null
                                        // if it cannot seek to that position.
                                        ProductCategory productCategory = (ProductCategory) adapterView.getItemAtPosition(position);
                                        if (productCategory != null) {
                                            ((Callback) getActivity()).onItemSelected(productCategory);
                                        }
                                    }
                                });

                                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                    @Override
                                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                        ProductCategory productCategory = (ProductCategory) parent.getItemAtPosition(position);
                                        if (productCategory != null) {
                                            ((Callback) getActivity()).onItemLongSelected(productCategory);
                                        }
                                        return true;
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                rootView.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                rootView.findViewById(R.id.categories_list).setVisibility(View.VISIBLE);
                                if (getActivity()!=null) {
                                    ((Callback) getActivity()).onCategoriesListIsLoaded(mCurrentSelectedIndex);
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
        super.onSaveInstanceState(outState);
    }
}
