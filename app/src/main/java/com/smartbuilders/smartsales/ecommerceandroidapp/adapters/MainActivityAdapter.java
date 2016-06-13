package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductDetailActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductsListActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Banner;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.BannerSection;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.MainPageProductSection;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductBrandPromotionalSection;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.ViewIdGenerator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Alberto on 25/3/2016.
 */
public class MainActivityAdapter extends BaseAdapter {

    private static final int VIEW_TYPE_VIEWFLIPPER = 0;
    private static final int VIEW_TYPE_RECYCLERVIEW = 1;
    private static final int VIEW_TYPE_VIEWPAGER = 2;

    private FragmentActivity mFragmentActivity;
    private ArrayList<Object> mDataset;
    private User mCurrentUser;
    private DisplayMetrics metrics;

    @Override
    public int getItemViewType(int position) {
        if (mDataset.get(position) instanceof BannerSection) {
            return VIEW_TYPE_VIEWFLIPPER;
        } else if (mDataset.get(position) instanceof MainPageProductSection) {
            return VIEW_TYPE_RECYCLERVIEW;
        } else if (mDataset.get(position) instanceof ProductBrandPromotionalSection) {
            return VIEW_TYPE_VIEWPAGER;
        }
        return -1;
    }

    public static class ViewHolder {
        public TextView categoryName;
        public RecyclerView mRecyclerView;
        public ViewFlipper mViewFlipper;
        public ViewPager mViewPager;

        public ViewHolder(View v) {
            categoryName = (TextView) v.findViewById(R.id.category_name);
            mRecyclerView = (RecyclerView) v.findViewById(R.id.product_list);
            mViewFlipper = (ViewFlipper) v.findViewById(R.id.banner_flipper);
            mViewPager = (ViewPager) v.findViewById(R.id.view_pager);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MainActivityAdapter(FragmentActivity fragmentActivity, ArrayList<Object> myDataset,
                               User user) {
        mFragmentActivity = fragmentActivity;
        mDataset = myDataset;
        mCurrentUser = user;
        metrics = new DisplayMetrics();
        fragmentActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // create a new view
        View view = null;
        switch (getItemViewType(position)) {
            case VIEW_TYPE_VIEWFLIPPER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.banner, parent, false);
                break;
            case VIEW_TYPE_RECYCLERVIEW:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.category_product_list, parent, false);
                break;
            case VIEW_TYPE_VIEWPAGER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_pager_layout, parent, false);
                break;
        }

        if (view != null) {
            ViewHolder viewHolder = new ViewHolder(view);
            switch (getItemViewType(position)) {
                case VIEW_TYPE_VIEWFLIPPER: {
                    if (mDataset!=null && mDataset.get(position)!=null
                            && mDataset.get(position) instanceof BannerSection
                            &&  ((BannerSection) mDataset.get(position)).getBanners()!=null) {
                        for (Banner banner : ((BannerSection) mDataset.get(position)).getBanners()) {
                            setFlipperImage(parent.getContext(), viewHolder.mViewFlipper, banner);
                        }
                        /** Start Flipping */
                        viewHolder.mViewFlipper.startFlipping();
                    }
                    break;
                }
                case VIEW_TYPE_RECYCLERVIEW: {
                    MainPageProductSection mainPageProductSection = (MainPageProductSection) mDataset.get(position);

                    if(mainPageProductSection !=null && mainPageProductSection.getProducts()!=null
                            && !mainPageProductSection.getProducts().isEmpty()){
                        // - get element from your dataset at this position
                        // - replace the contents of the view with that element
                        viewHolder.categoryName.setText(mainPageProductSection.getName());

                        // use this setting to improve performance if you know that changes
                        // in content do not change the layout size of the RecyclerView
                        viewHolder.mRecyclerView.setHasFixedSize(true);
                        //int spanCount = 2;
                        //try {
                        //    int measuredWidth;
                        //    WindowManager w = mFragmentActivity.getWindowManager();
                        //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                        //        Point size = new Point();
                        //        w.getDefaultDisplay().getSize(size);
                        //        measuredWidth = size.x;
                        //    } else {
                        //        measuredWidth = w.getDefaultDisplay().getWidth();
                        //    }
                        //    spanCount = (int) (measuredWidth / mFragmentActivity.getResources().getDimension(R.dimen.productMinInfo_cardView_Width));
                        //} catch (Exception e){
                        //    e.printStackTrace();
                        //}

                        viewHolder.mRecyclerView.setLayoutManager(new LinearLayoutManager(parent.getContext(),
                                LinearLayoutManager.HORIZONTAL, false));
                        //viewHolder.mRecyclerView.setLayoutManager(new GridLayoutManager(parent.getContext(), spanCount));

                        viewHolder.mRecyclerView.setAdapter(new ProductsListAdapter(mFragmentActivity,
                                mainPageProductSection.getProducts(), false, ProductsListAdapter.REDIRECT_PRODUCT_DETAILS, mCurrentUser));
                    }
                    break;
                }
                case VIEW_TYPE_VIEWPAGER:
                    ProductBrandPromotionalSection productBrandPromotionalSection =
                            (ProductBrandPromotionalSection) mDataset.get(position);
                    if(productBrandPromotionalSection !=null
                            && productBrandPromotionalSection.getProductBrandPromotionalCards()!=null
                            && !productBrandPromotionalSection.getProductBrandPromotionalCards().isEmpty()) {
                        viewHolder.mViewPager.setId(ViewIdGenerator.generateViewId());
                        viewHolder.mViewPager.setClipToPadding(false);
                        int height;
                        if(metrics.widthPixels < metrics.heightPixels){
                            height = (int) (metrics.heightPixels / 3.5);
                        } else {
                            height = (int) (metrics.widthPixels / 3.5);
                        }
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(metrics.widthPixels, height);
                        viewHolder.mViewPager.setLayoutParams(lp);
                        viewHolder.mViewPager.setPageMargin(12);
                        ProductBrandPromotionalAdapter productBrandPromotionalAdapter =
                                new ProductBrandPromotionalAdapter(mFragmentActivity.getSupportFragmentManager());
                        productBrandPromotionalAdapter.setData(productBrandPromotionalSection.getProductBrandPromotionalCards());
                        viewHolder.mViewPager.setAdapter(productBrandPromotionalAdapter);
                    }
                    break;
            }

            view.setTag(viewHolder);
        }
        return view;
    }

    private void setFlipperImage(final Context context, ViewFlipper viewFlipper, final Banner banner) {
        final ImageView image = new ImageView(context);

        File img = Utils.getFileInBannerDirByFileName(context, mCurrentUser, banner.getImageFileName());
        if(img!=null){
            Picasso.with(context).load(img).into(image);
        }else{
            Picasso.with(context)
                    .load(mCurrentUser.getServerAddress()
                            + "/IntelligentDataSynchronizer/GetBannerImage?fileName="
                            + banner.getImageFileName())
                    .into(image, new Callback() {
                        @Override
                        public void onSuccess() {
                            Utils.createFileInBannerDir(banner.getImageFileName(),
                                    ((BitmapDrawable)(image).getDrawable()).getBitmap(),
                                    mCurrentUser, context);
                        }

                        @Override
                        public void onError() { }
                    });
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(banner.getProductId()>0){
                    context.startActivity(new Intent(context, ProductDetailActivity.class)
                            .putExtra(ProductDetailActivity.KEY_PRODUCT_ID, banner.getProductId()));
                } else if (banner.getProductBrandId()>0) {
                    context.startActivity(new Intent(context, ProductsListActivity.class)
                            .putExtra(ProductsListActivity.KEY_PRODUCT_BRAND_ID, banner.getProductBrandId()));
                } else if (banner.getProductSubCategoryId()>0) {
                    context.startActivity(new Intent(context, ProductsListActivity.class)
                            .putExtra(ProductsListActivity.KEY_PRODUCT_SUBCATEGORY_ID, banner.getProductSubCategoryId()));
                } else if (banner.getProductCategoryId()>0) {
                    context.startActivity(new Intent(context, ProductsListActivity.class)
                            .putExtra(ProductsListActivity.KEY_PRODUCT_CATEGORY_ID, banner.getProductCategoryId()));
                }
            }
        });

        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setAdjustViewBounds(true);
        viewFlipper.addView(image);
    }

    @Override
    public int getCount() {
        if (mDataset!=null) {
            return mDataset.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public long getItemId(int position) {
        return -1;
    }

}
