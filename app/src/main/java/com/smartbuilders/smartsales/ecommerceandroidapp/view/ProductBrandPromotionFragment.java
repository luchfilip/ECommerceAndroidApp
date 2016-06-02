package com.smartbuilders.smartsales.ecommerceandroidapp.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductBrand;

/**
 * Created by stein on 2/6/2016.
 */
public class ProductBrandPromotionFragment extends Fragment {

    private static final String STATE_PRODUCT_BRAND = "STATE_PRODUCT_BRAND";

    private ProductBrand mProductBrand;

    public static ProductBrandPromotionFragment getInstance (ProductBrand productBrand){
        ProductBrandPromotionFragment productBrandPromotionFragment
                = new ProductBrandPromotionFragment();
        productBrandPromotionFragment.mProductBrand = productBrand;
        return productBrandPromotionFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (savedInstanceState!=null && savedInstanceState.containsKey(STATE_PRODUCT_BRAND)) {
           mProductBrand = savedInstanceState.getParcelable(STATE_PRODUCT_BRAND);
        }

        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.brand_promotion_layout, container, false);

        ((TextView) rootView.findViewById(R.id.product_brand_name))
                .setText(mProductBrand.getDescription());
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_PRODUCT_BRAND, mProductBrand);
        super.onSaveInstanceState(outState);
    }
}
