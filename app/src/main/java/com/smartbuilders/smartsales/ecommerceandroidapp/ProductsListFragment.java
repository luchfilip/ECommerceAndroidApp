package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.ProductRecyclerViewAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProductsListFragment extends Fragment {

    public static final String KEY_PRODUCT = "key_product";

    public ProductsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_products_list, container, false);


        TextView categorySubcategoryResultsTextView = (TextView) view
                .findViewById(R.id.category_subcategory_results);
        Spannable word = new SpannableString("Categoria del Producto >> ");

        word.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.product_category)), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        categorySubcategoryResultsTextView.setText(word);
        Spannable wordTwo = new SpannableString(" SubCategoria del producto ");

        wordTwo.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.product_subcategory)),
                0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        categorySubcategoryResultsTextView.append(wordTwo);

        Spannable wordThree = new SpannableString(" (250 Resultados) ");
        categorySubcategoryResultsTextView.append(wordThree);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.product_list_result);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new ProductRecyclerViewAdapter(Utils.getGenericProductsList(20),
                true, ProductRecyclerViewAdapter.REDIRECT_PRODUCT_DETAILS));

        return view;
    }
}
