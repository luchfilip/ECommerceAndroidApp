package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.ProductsListAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ProductDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProductsListFragment extends Fragment {

    private static final String STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION = "STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION";

    private int productCategoryId;
    private int productSubCategoryId;
    private int productBrandId;
    private String productName;
    private String mSearchPattern;
    private ProductsListAdapter mProductsListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private int mRecyclerViewCurrentFirstPosition;
    private User mCurrentUser;

    public ProductsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view  = inflater.inflate(R.layout.fragment_products_list, container, false);

        final ArrayList<Product> products = new ArrayList<>();

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState != null) {
                        if(savedInstanceState.containsKey(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION)){
                            mRecyclerViewCurrentFirstPosition = savedInstanceState.getInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION);
                        }
                    }

                    if(getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null) {
                        if(getActivity().getIntent().getExtras().containsKey(ProductsListActivity.KEY_PRODUCT_CATEGORY_ID)){
                            productCategoryId = getActivity().getIntent().getExtras().getInt(ProductsListActivity.KEY_PRODUCT_CATEGORY_ID);
                        }
                        if(getActivity().getIntent().getExtras().containsKey(ProductsListActivity.KEY_PRODUCT_SUBCATEGORY_ID)){
                            productSubCategoryId = getActivity().getIntent().getExtras().getInt(ProductsListActivity.KEY_PRODUCT_SUBCATEGORY_ID);
                        }
                        if(getActivity().getIntent().getExtras().containsKey(ProductsListActivity.KEY_PRODUCT_BRAND_ID)){
                            productBrandId = getActivity().getIntent().getExtras().getInt(ProductsListActivity.KEY_PRODUCT_BRAND_ID);
                        }
                        if(getActivity().getIntent().getExtras().containsKey(ProductsListActivity.KEY_PRODUCT_NAME)){
                            productName = getActivity().getIntent().getExtras().getString(ProductsListActivity.KEY_PRODUCT_NAME);
                        }
                        if(getActivity().getIntent().getExtras().containsKey(ProductsListActivity.KEY_SEARCH_PATTERN)){
                            mSearchPattern = getActivity().getIntent().getExtras().getString(ProductsListActivity.KEY_SEARCH_PATTERN);
                        }
                    }

                    mCurrentUser = Utils.getCurrentUser(getContext());

                    if (productCategoryId != 0) {
                        products.addAll(new ProductDB(getContext(), mCurrentUser).getProductsByCategoryId(productCategoryId));
                    } else if (productSubCategoryId != 0) {
                        products.addAll(new ProductDB(getContext(), mCurrentUser).getProductsBySubCategoryId(productSubCategoryId, mSearchPattern));
                    } else if (productBrandId != 0) {
                        products.addAll(new ProductDB(getContext(), mCurrentUser).getProductsByBrandId(productBrandId));
                    } else if (productName != null) {
                        products.addAll(new ProductDB(getContext(), mCurrentUser).getProductsByName(productName));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(!products.isEmpty()) {
                                    if (productCategoryId != 0) {
                                        TextView categorySubcategoryResultsTextView = (TextView) view.findViewById(R.id.category_subcategory_results);
                                        Spannable word = new SpannableString(products.get(0).getProductCategory().getDescription() + " ");
                                        word.setSpan(new ForegroundColorSpan(Utils.getColor(getContext(), R.color.product_category)), 0,
                                                word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        categorySubcategoryResultsTextView.setText(word);
                                        categorySubcategoryResultsTextView.append(new SpannableString(" ("+products.size()+" Resultados) "));
                                    } else if (productSubCategoryId != 0) {
                                        TextView categorySubcategoryResultsTextView = (TextView) view.findViewById(R.id.category_subcategory_results);
                                        Spannable word = new SpannableString(products.get(0).getProductCategory().getDescription() + " >> ");
                                        word.setSpan(new ForegroundColorSpan(Utils.getColor(getContext(), R.color.product_category)), 0,
                                                word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        categorySubcategoryResultsTextView.setText(word);
                                        Spannable wordTwo = new SpannableString(" "+products.get(0).getProductSubCategory().getDescription()+" ");
                                        wordTwo.setSpan(new ForegroundColorSpan(Utils.getColor(getContext(), R.color.product_subcategory)),
                                                0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        categorySubcategoryResultsTextView.append(wordTwo);
                                        categorySubcategoryResultsTextView.append(new SpannableString(" ("+products.size()+" Resultados) "));
                                    } else if (productBrandId != 0) {
                                        TextView categorySubcategoryResultsTextView = (TextView) view.findViewById(R.id.category_subcategory_results);
                                        Spannable word = new SpannableString(products.get(0).getProductBrand().getDescription() + " ");
                                        word.setSpan(new ForegroundColorSpan(Utils.getColor(getContext(), R.color.product_category)), 0,
                                                word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        categorySubcategoryResultsTextView.setText(word);
                                        categorySubcategoryResultsTextView.append(new SpannableString("("+products.size()+" Resultados) "));
                                    } else if (productName != null) {
                                        TextView categorySubcategoryResultsTextView = (TextView) view.findViewById(R.id.category_subcategory_results);
                                        Spannable word = new SpannableString("Búsqueda: \""+productName+"\" ");
                                        word.setSpan(new ForegroundColorSpan(Utils.getColor(getContext(), R.color.product_category)), 0,
                                                word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        categorySubcategoryResultsTextView.setText(word);
                                        categorySubcategoryResultsTextView.append(new SpannableString("("+products.size()+" Resultados) "));
                                    }
                                } else {
                                    TextView categorySubcategoryResultsTextView = (TextView) view.findViewById(R.id.category_subcategory_results);
                                    Spannable word = new SpannableString(getString(R.string.no_products_to_show));
                                    word.setSpan(new ForegroundColorSpan(Utils.getColor(getContext(), R.color.black)), 0,
                                            word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    categorySubcategoryResultsTextView.append(word);
                                }

                                mProductsListAdapter = new ProductsListAdapter(getContext(), getActivity(), products, true, mCurrentUser);

                                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.product_list_result);
                                // use this setting to improve performance if you know that changes
                                // in content do not change the layout size of the RecyclerView
                                recyclerView.setHasFixedSize(true);
                                if (useGridView()) {
                                    linearLayoutManager = new GridLayoutManager(getContext(), getSpanCount());
                                }else{
                                    linearLayoutManager = new LinearLayoutManager(getContext());
                                }
                                recyclerView.setLayoutManager(linearLayoutManager);
                                recyclerView.setAdapter(mProductsListAdapter);

                                if (mRecyclerViewCurrentFirstPosition!=0) {
                                    recyclerView.scrollToPosition(mRecyclerViewCurrentFirstPosition);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                view.findViewById(R.id.category_subcategory_results).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.product_list_result).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        }.start();
        return view;
    }

    private boolean useGridView(){
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            return true;
        }else {
            switch(getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) {
                case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                    switch(getResources().getDisplayMetrics().densityDpi) {
                        case DisplayMetrics.DENSITY_LOW:
                            break;
                        case DisplayMetrics.DENSITY_MEDIUM:
                        case DisplayMetrics.DENSITY_HIGH:
                        case DisplayMetrics.DENSITY_XHIGH:
                            return  true;
                    }
                    break;
                //case Configuration.SCREENLAYOUT_SIZE_LARGE:
                //    break;
                //case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                //    break;
                //case Configuration.SCREENLAYOUT_SIZE_SMALL:
                //    break;
            }
        }
        return false;
    }

    private int getSpanCount() {
        switch(getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) {
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                switch(getResources().getDisplayMetrics().densityDpi) {
                    case DisplayMetrics.DENSITY_LOW:
                        break;
                    case DisplayMetrics.DENSITY_MEDIUM:
                    case DisplayMetrics.DENSITY_HIGH:
                    case DisplayMetrics.DENSITY_XHIGH:
                        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                            return 3;
                        }
                        break;
                }
                break;
            //case Configuration.SCREENLAYOUT_SIZE_LARGE:
            //    break;
            //case Configuration.SCREENLAYOUT_SIZE_NORMAL:
            //    break;
            //case Configuration.SCREENLAYOUT_SIZE_SMALL:
            //    break;
        }
        return 2;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        try {
            if (linearLayoutManager instanceof GridLayoutManager) {
                outState.putInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION,
                        linearLayoutManager.findFirstVisibleItemPosition());
            } else {
                outState.putInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION,
                        linearLayoutManager.findFirstCompletelyVisibleItemPosition());
            }
        } catch (Exception e) {
            outState.putInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION, mRecyclerViewCurrentFirstPosition);
        }
        super.onSaveInstanceState(outState);
    }
}
