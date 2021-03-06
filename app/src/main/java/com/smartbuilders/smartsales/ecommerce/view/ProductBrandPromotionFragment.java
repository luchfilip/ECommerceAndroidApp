package com.smartbuilders.smartsales.ecommerce.view;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.ProductsListActivity;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.model.ProductBrandPromotionalCard;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
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

        final boolean isLandscape = getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        File img = Utils.getFileInProductBrandPromotionalDirByFileName(getContext(), isLandscape, mProductBrandPromotionalCard.getImageFileName());
        if (img!=null) {
            Picasso.with(getContext()).load(img).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into((ImageView) rootView.findViewById(R.id.product_brand_promotion_imageView));
            Picasso.with(getContext()).invalidate(img);
        } else if (user!=null) {
            Picasso.with(getContext())
                    .load(user.getServerAddress() + "/IntelligentDataSynchronizer/GetProductBrandPromotionalImage?fileName="
                            + mProductBrandPromotionalCard.getImageFileName() + Utils.getUrlScreenParameters(isLandscape, getContext()))
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into((ImageView) rootView.findViewById(R.id.product_brand_promotion_imageView), new Callback() {
                        @Override
                        public void onSuccess() {
                            Utils.createFileInProductBrandPromotionalDir(getContext(), isLandscape,
                                    mProductBrandPromotionalCard.getImageFileName(),
                                    ((BitmapDrawable)((ImageView) rootView.findViewById(R.id.product_brand_promotion_imageView)).getDrawable()).getBitmap());
                            Picasso.with(getContext()).invalidate(user.getServerAddress() + "/IntelligentDataSynchronizer/GetProductBrandPromotionalImage?fileName="
                                    + mProductBrandPromotionalCard.getImageFileName() + Utils.getUrlScreenParameters(isLandscape, getContext()));
                        }

                        @Override
                        public void onError() { }
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

        if(mProductBrandPromotionalCard.getBackground_R_Color()>=0
                && mProductBrandPromotionalCard.getBackground_G_Color()>=0
                && mProductBrandPromotionalCard.getBackground_B_Color()>=0){

            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setCornerRadius(Utils.convertDpToPixel(5, getContext()));
            shape.setColor(Color.rgb(mProductBrandPromotionalCard.getBackground_R_Color(),
                    mProductBrandPromotionalCard.getBackground_G_Color(),
                    mProductBrandPromotionalCard.getBackground_B_Color()));
            shape.setStroke(Utils.convertDpToPixel(1, getContext()), Utils.getColor(getContext(), R.color.grey_medium));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                rootView.findViewById(R.id.main_layout).setBackground(shape);
            } else {
                rootView.findViewById(R.id.main_layout).setBackgroundDrawable(shape);
            }
        }

        if(mProductBrandPromotionalCard.getPromotionalText_R_Color()>=0
                && mProductBrandPromotionalCard.getPromotionalText_G_Color()>=0
                && mProductBrandPromotionalCard.getPromotionalText_B_Color()>=0){
            ((TextView) rootView.findViewById(R.id.promotional_text))
                    .setTextColor(Color.rgb(mProductBrandPromotionalCard.getPromotionalText_R_Color(),
                            mProductBrandPromotionalCard.getPromotionalText_G_Color(),
                            mProductBrandPromotionalCard.getPromotionalText_B_Color()));
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_PRODUCT_BRAND_PROMOTIONAL_CARD, mProductBrandPromotionalCard);
        super.onSaveInstanceState(outState);
    }
}
