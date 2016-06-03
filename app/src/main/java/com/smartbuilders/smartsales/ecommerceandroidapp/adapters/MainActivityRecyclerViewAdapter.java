package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
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
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Banner;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.BannerSection;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.MainPageSection;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductBrandsPromotionSection;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.ViewIdGenerator;

import java.util.ArrayList;

/**
 * Created by Alberto on 25/3/2016.
 */
public class MainActivityRecyclerViewAdapter extends BaseAdapter {

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
        } else if (mDataset.get(position) instanceof MainPageSection) {
            return VIEW_TYPE_RECYCLERVIEW;
        } else if (mDataset.get(position) instanceof ProductBrandsPromotionSection) {
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
    public MainActivityRecyclerViewAdapter(FragmentActivity fragmentActivity, ArrayList<Object> myDataset,
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
                            setFlipperImage(parent.getContext(), viewHolder.mViewFlipper, banner.getImageResId());
                        }
                        /** Start Flipping */
                        viewHolder.mViewFlipper.startFlipping();
                    }
                    break;
                }
                case VIEW_TYPE_RECYCLERVIEW: {
                    MainPageSection mainPageSection = (MainPageSection) mDataset.get(position);

                    if(mainPageSection!=null && mainPageSection.getProducts()!=null && !mainPageSection.getProducts().isEmpty()){
                        // - get element from your dataset at this position
                        // - replace the contents of the view with that element
                        viewHolder.categoryName.setText(mainPageSection.getName());

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

                        viewHolder.mRecyclerView.setAdapter(new ProductRecyclerViewAdapter(mFragmentActivity,
                                mainPageSection.getProducts(), false, ProductRecyclerViewAdapter.REDIRECT_PRODUCT_DETAILS, mCurrentUser));
                    }
                    break;
                }
                case VIEW_TYPE_VIEWPAGER:
                    ProductBrandsPromotionSection productBrandsPromotionSection =
                            (ProductBrandsPromotionSection) mDataset.get(position);
                    if(productBrandsPromotionSection!=null && productBrandsPromotionSection.getProductBrands()!=null
                            && !productBrandsPromotionSection.getProductBrands().isEmpty()) {
                        viewHolder.mViewPager.setId(ViewIdGenerator.generateViewId());
                        viewHolder.mViewPager.setClipToPadding(false);
                        int height = 0;
                        if(metrics.widthPixels < metrics.heightPixels){
                            height = (int) (metrics.heightPixels / 3.5);
                        } else {
                            height = (int) (metrics.widthPixels / 3.5);
                        }
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(metrics.widthPixels, height);
                        viewHolder.mViewPager.setLayoutParams(lp);
                        viewHolder.mViewPager.setPageMargin(12);
                        ProductBrandsPromotionAdapter productBrandsPromotionAdapter =
                                new ProductBrandsPromotionAdapter(mFragmentActivity.getSupportFragmentManager());
                        productBrandsPromotionAdapter.setData(productBrandsPromotionSection.getProductBrands());
                        productBrandsPromotionAdapter.setUser(mCurrentUser);
                        viewHolder.mViewPager.setAdapter(productBrandsPromotionAdapter);
                    }
                    break;
            }

            view.setTag(viewHolder);
        }
        return view;
    }

    private void setFlipperImage(Context context, ViewFlipper viewFlipper, int res) {
        ImageView image = new ImageView(context);
        image.setImageResource(res);
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
