package com.smartbuilders.smartsales.ecommerceandroidapp.view;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
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
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductBrandPromotionalCard;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by stein on 2/6/2016.
 */
public class ProductBrandPromotionFragment extends Fragment {

    private static final String STATE_PRODUCT_BRAND_PROMOTIONAL_CARD = "STATE_PRODUCT_BRAND_PROMOTIONAL_CARD";

    private ProductBrandPromotionalCard mProductBrandPromotionalCard;

    public static ProductBrandPromotionFragment getInstance (ProductBrandPromotionalCard productBrandPromotionalCard){
        ProductBrandPromotionFragment productBrandPromotionFragment
                = new ProductBrandPromotionFragment();
        productBrandPromotionFragment.mProductBrandPromotionalCard = productBrandPromotionalCard;
        return productBrandPromotionFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final User user = Utils.getCurrentUser(getContext());

        if (savedInstanceState!=null){
            if(savedInstanceState.containsKey(STATE_PRODUCT_BRAND_PROMOTIONAL_CARD)) {
                mProductBrandPromotionalCard = savedInstanceState.getParcelable(STATE_PRODUCT_BRAND_PROMOTIONAL_CARD);
            }
        }

        final ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.brand_promotion_layout, container, false);

        ((TextView) rootView.findViewById(R.id.promotional_text))
                .setText(mProductBrandPromotionalCard.getPromotionalText());

        File img = Utils.getFileInProductBrandPromotionalDirByFileName(getContext(), user, mProductBrandPromotionalCard.getImageFileName());
        if (img!=null) {
            Picasso.with(getContext())
                    .load(img).into((ImageView) rootView.findViewById(R.id.product_brand_promotion_imageView));
        } else {
            Picasso.with(getContext())
                    .load(user.getServerAddress() + "/IntelligentDataSynchronizer/GetProductBrandPromotionalImage?fileName="
                            + mProductBrandPromotionalCard.getImageFileName())
                    .into((ImageView) rootView.findViewById(R.id.product_brand_promotion_imageView), new Callback() {
                        @Override
                        public void onSuccess() {
                            Utils.createFileInProductBrandPromotionalDir(mProductBrandPromotionalCard.getImageFileName(),
                                    ((BitmapDrawable)((ImageView) rootView.findViewById(R.id.product_brand_promotion_imageView)).getDrawable()).getBitmap(),
                                    user, getContext());
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProductsListActivity.class);
                intent.putExtra(ProductsListActivity.KEY_PRODUCT_BRAND_ID, mProductBrandPromotionalCard.getProductBrandId());
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_PRODUCT_BRAND_PROMOTIONAL_CARD, mProductBrandPromotionalCard);
        super.onSaveInstanceState(outState);
    }
}
