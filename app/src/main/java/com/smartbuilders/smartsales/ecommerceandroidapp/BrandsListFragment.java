package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.BrandAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductBrand;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class BrandsListFragment extends Fragment {

    public BrandsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_brands_list, container, false);

        ArrayList<ProductBrand> productBrands = new ArrayList<ProductBrand>();
        productBrands.addAll(Utils.generateProductBrandsListByLetter("A", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("B", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("C", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("D", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("E", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("F", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("G", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("H", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("I", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("J", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("K", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("L", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("M", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("N", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("Ñ", 5));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("O", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("P", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("Q", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("R", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("S", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("T", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("U", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("V", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("W", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("X", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("Y", 25));
        productBrands.addAll(Utils.generateProductBrandsListByLetter("Z", 25));

        ListView lv = (ListView) rootView.findViewById(R.id.brands_list);
        lv.setAdapter(new BrandAdapter(getContext(), productBrands));
        lv.setFastScrollEnabled(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                ProductBrand productBrand = (ProductBrand) adapterView.getItemAtPosition(position);
                if (productBrand != null) {
                    startActivity(new Intent(getActivity(), ProductsListActivity.class));
                    getActivity().finish();
                }
            }
        });

        return rootView;
    }
}
