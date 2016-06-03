package com.smartbuilders.smartsales.ecommerceandroidapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductsListActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductBrand;

/**
 * Created by stein on 2/6/2016.
 */
public class ProductBrandPromotionFragment extends Fragment {

    private static final String STATE_PRODUCT_BRAND = "STATE_PRODUCT_BRAND";
    private static final String STATE_CURRENT_USER = "STATE_CURRENT_USER";

    private ProductBrand mProductBrand;
    private User mCurrentUser;

    public static ProductBrandPromotionFragment getInstance (ProductBrand productBrand, User user){
        ProductBrandPromotionFragment productBrandPromotionFragment
                = new ProductBrandPromotionFragment();
        productBrandPromotionFragment.mProductBrand = productBrand;
        productBrandPromotionFragment.mCurrentUser = user;
        return productBrandPromotionFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (savedInstanceState!=null){
            if(savedInstanceState.containsKey(STATE_PRODUCT_BRAND)) {
                mProductBrand = savedInstanceState.getParcelable(STATE_PRODUCT_BRAND);
            }
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)) {
                mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.brand_promotion_layout, container, false);

        ((TextView) rootView.findViewById(R.id.product_brand_name))
                .setText(mProductBrand.getDescription());

        ((ImageView) rootView.findViewById(R.id.product_brand_promotion_imageView))
                .setImageResource(mProductBrand.getImageId());

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProductsListActivity.class);
                intent.putExtra(ProductsListActivity.KEY_CURRENT_USER, mCurrentUser);
                intent.putExtra(ProductsListActivity.KEY_PRODUCT_BRAND_ID, mProductBrand.getId());
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_PRODUCT_BRAND, mProductBrand);
        outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
        super.onSaveInstanceState(outState);
    }
}
