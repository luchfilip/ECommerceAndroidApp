package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.MainPageProductDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.MainPageProduct;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.MainPageSection;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Alberto on 25/3/2016.
 */
public class MainActivityRecyclerViewAdapter extends BaseAdapter {

    private static final int VIEW_TYPE_VIEWFLIPPER = 0;
    private static final int VIEW_TYPE_RECYCLERVIEW = 1;

    private FragmentActivity mFragmentActivity;
    private ArrayList<MainPageSection> mDataset;
    private User mCurrentUser;
    private HashMap<Integer, ArrayList<Product>> productsByPosition;

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_VIEWFLIPPER : VIEW_TYPE_RECYCLERVIEW;
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
    public MainActivityRecyclerViewAdapter(FragmentActivity fragmentActivity, ArrayList<MainPageSection> myDataset,
                                           User user) {
        mFragmentActivity = fragmentActivity;
        mDataset = myDataset;
        mCurrentUser = user;
        productsByPosition = new HashMap<>();
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

        ViewHolder viewHolder = new ViewHolder(view);
        switch (getItemViewType(position)) {
            case VIEW_TYPE_VIEWFLIPPER: {
                //for(int i=0; i<5; i++) {
                //    setFlipperImage(holder.mViewFlipper, R.drawable.febeca);
                //}
                setFlipperImage(parent.getContext(), viewHolder.mViewFlipper, R.drawable.banner);
                setFlipperImage(parent.getContext(), viewHolder.mViewFlipper, R.drawable.banner1);
                setFlipperImage(parent.getContext(), viewHolder.mViewFlipper, R.drawable.banner2);
                setFlipperImage(parent.getContext(), viewHolder.mViewFlipper, R.drawable.banner3);
                setFlipperImage(parent.getContext(), viewHolder.mViewFlipper, R.drawable.banner4);
                /** Start Flipping */
                viewHolder.mViewFlipper.startFlipping();
                break;
            }
            case VIEW_TYPE_RECYCLERVIEW: {
                MainPageProductDB mainPageProductDB = new MainPageProductDB(parent.getContext(), mCurrentUser);
                ArrayList<MainPageProduct> mainPageProducts = mainPageProductDB
                        .getActiveMainPageProductsByMainPageSectionId(mDataset.get(position).getId());
                if(mainPageProducts != null && !mainPageProducts.isEmpty()){
                    // - get element from your dataset at this position
                    // - replace the contents of the view with that element
                    viewHolder.categoryName.setText(mDataset.get(position).getName());

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

                    if (!productsByPosition.containsKey(position)) {
                        productsByPosition.put(position, new ArrayList<Product>());
                        for(MainPageProduct mainPageProduct : mainPageProducts){
                            productsByPosition.get(position).add(mainPageProduct.getProduct());
                        }
                    }
                    viewHolder.mRecyclerView.setAdapter(new ProductRecyclerViewAdapter(mFragmentActivity,
                            productsByPosition.get(position), false, ProductRecyclerViewAdapter.REDIRECT_PRODUCT_DETAILS, mCurrentUser));
                }
                break;
            }
        }

        view.setTag(viewHolder);
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
        return mDataset.get(position).getId();
    }

}
