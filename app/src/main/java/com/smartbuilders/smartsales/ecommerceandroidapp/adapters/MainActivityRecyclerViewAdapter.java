package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.MainPageProductDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.MainPageProduct;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.MainPageSection;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;

import java.util.ArrayList;

/**
 * Created by Alberto on 25/3/2016.
 */
public class MainActivityRecyclerViewAdapter extends RecyclerView.Adapter<MainActivityRecyclerViewAdapter.ViewHolder> {

    private static final int VIEW_TYPE_VIEWFLIPPER = 0;
    private static final int VIEW_TYPE_RECYCLERVIEW = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    private Activity mActivity;
    private ArrayList<MainPageSection> mDataset;
    private User mCurrentUser;
    private Context mContext;
    private MainPageProductDB mainPageProductDB;

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_VIEWFLIPPER : VIEW_TYPE_RECYCLERVIEW;
    }

    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView categoryName;
        public RecyclerView mRecyclerView;
        public ViewFlipper mViewFlipper;

        public ViewHolder(View v) {
            super(v);
            categoryName = (TextView) v.findViewById(R.id.category_name);
            mRecyclerView = (RecyclerView) v.findViewById(R.id.product_list);
            mViewFlipper = (ViewFlipper) v.findViewById(R.id.banner_flipper);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MainActivityRecyclerViewAdapter(Activity activity, ArrayList<MainPageSection> myDataset,
                                           User user) {
        mActivity = activity;
        mDataset = myDataset;
        mCurrentUser = user;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MainActivityRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        mContext = parent.getContext();

        mainPageProductDB = new MainPageProductDB(mContext, mCurrentUser);

        // create a new view
        View v = null;

        switch (viewType) {
            case VIEW_TYPE_VIEWFLIPPER: {
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.banner, parent, false);
                break;
            }
            case VIEW_TYPE_RECYCLERVIEW: {
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.category_product_list, parent, false);
                break;
            }
        }

        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_VIEWFLIPPER: {
                //for(int i=0; i<5; i++) {
                //    setFlipperImage(holder.mViewFlipper, R.drawable.febeca);
                //}
                setFlipperImage(holder.mViewFlipper, R.drawable.banner);
                setFlipperImage(holder.mViewFlipper, R.drawable.banner1);
                setFlipperImage(holder.mViewFlipper, R.drawable.banner2);
                setFlipperImage(holder.mViewFlipper, R.drawable.banner3);
                setFlipperImage(holder.mViewFlipper, R.drawable.banner4);
                /** Start Flipping */
                holder.mViewFlipper.startFlipping();
                break;
            }
            case VIEW_TYPE_RECYCLERVIEW: {
                ArrayList<MainPageProduct> mainPageProducts = mainPageProductDB
                        .getActiveMainPageProductsByMainPageSectionId(mDataset.get(position).getId());
                if(mainPageProducts != null && !mainPageProducts.isEmpty()){
                    // - get element from your dataset at this position
                    // - replace the contents of the view with that element
                    holder.categoryName.setText(mDataset.get(position).getName());

                    // use this setting to improve performance if you know that changes
                    // in content do not change the layout size of the RecyclerView
                    holder.mRecyclerView.setHasFixedSize(true);
                    //holder.mRecyclerView.setLayoutManager(
                    //       new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

                    int spanCount = 2;
                    try {
                        int measuredWidth;
                        WindowManager w = mActivity.getWindowManager();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            Point size = new Point();
                            w.getDefaultDisplay().getSize(size);
                            measuredWidth = size.x;
                        } else {
                            measuredWidth = w.getDefaultDisplay().getWidth();
                        }
                        spanCount = (int) (measuredWidth / mActivity.getResources().getDimension(R.dimen.productMinInfo_cardView_Width));
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    holder.mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, spanCount));

                    ArrayList<Product> products = new ArrayList<>();
                    for(MainPageProduct mainPageProduct : mainPageProducts){
                        products.add(mainPageProduct.getProduct());
                    }
                    holder.mRecyclerView.setAdapter(new ProductRecyclerViewAdapter(
                            products, false, ProductRecyclerViewAdapter.REDIRECT_PRODUCT_DETAILS, mCurrentUser));
                }else{

                }
                break;
            }
        }
    }

    private void setFlipperImage(ViewFlipper viewFlipper, int res) {
        ImageView image = new ImageView(mContext);
        image.setImageResource(res);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setAdjustViewBounds(true);
        //image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
        //        LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL));
        viewFlipper.addView(image);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
