package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private ArrayList<MainPageSection> mDataset;
    private User mCurrentUser;
    private Context mContext;
    private boolean mUseDetailLayout;
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
    public MainActivityRecyclerViewAdapter(ArrayList<MainPageSection> myDataset,
                                           boolean useDetailLayout, User user) {
        mDataset = myDataset;
        mUseDetailLayout = useDetailLayout;
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
                if(mUseDetailLayout){
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.category_product_list, parent, false);
                }else{
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.category_product_list, parent, false);
                }
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
                    holder.mRecyclerView.setLayoutManager(
                            new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

                    //holder.mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 8));

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
        image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL));
        image.setBackgroundResource(res);
        viewFlipper.addView(image);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
