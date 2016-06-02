package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Banner;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.BannerSection;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.MainPageSection;

import java.util.ArrayList;

/**
 * Created by Alberto on 25/3/2016.
 */
public class MainActivityRecyclerViewAdapter extends BaseAdapter {

    private static final int VIEW_TYPE_VIEWFLIPPER = 0;
    private static final int VIEW_TYPE_RECYCLERVIEW = 1;

    private FragmentActivity mFragmentActivity;
    private ArrayList<Object> mDataset;
    private User mCurrentUser;

    @Override
    public int getItemViewType(int position) {
        if (mDataset.get(position) instanceof BannerSection) {
            return VIEW_TYPE_VIEWFLIPPER;
        } else if (mDataset.get(position) instanceof MainPageSection) {
            return VIEW_TYPE_RECYCLERVIEW;
        }
        return -1;
    }

    public static class ViewHolder {
        public TextView categoryName;
        public RecyclerView mRecyclerView;
        public ViewFlipper mViewFlipper;

        public ViewHolder(View v) {
            categoryName = (TextView) v.findViewById(R.id.category_name);
            mRecyclerView = (RecyclerView) v.findViewById(R.id.product_list);
            mViewFlipper = (ViewFlipper) v.findViewById(R.id.banner_flipper);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MainActivityRecyclerViewAdapter(FragmentActivity fragmentActivity, ArrayList<Object> myDataset,
                                           User user) {
        mFragmentActivity = fragmentActivity;
        mDataset = myDataset;
        mCurrentUser = user;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // create a new view
        View view = null;
        switch (getItemViewType(position)) {
            case VIEW_TYPE_VIEWFLIPPER: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.banner, parent, false);
                break;
            }
            case VIEW_TYPE_RECYCLERVIEW: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.category_product_list, parent, false);
                break;
            }
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

                    if(mainPageSection.getProducts() != null && !mainPageSection.getProducts().isEmpty()){
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
