package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductDetailActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductDetailFragment;
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductsListActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

/**
 * Created by Alberto on 22/3/2016.
 */
public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = ProductRecyclerViewAdapter.class.getSimpleName();
    public static final int REDIRECT_PRODUCT_LIST = 0;
    public static final int REDIRECT_PRODUCT_DETAILS = 1;

    private ArrayList<Product> mDataset;
    private Product[] array;
    private Context mContext;
    private boolean mUseDetailLayout;
    private int mRedirectOption;
    private User mCurrentUser;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView productName;
        public TextView productShareLabel;
        public ImageView productImage;
        public TextView productSubCategory;
        public TextView productBrand;
        public TextView commercialPackage;
        public TextView productAvaliability;
        public LinearLayout linearLayoutContent;

        public ViewHolder(View v) {
            super(v);
            productName = (TextView) v.findViewById(R.id.product_name);
            productShareLabel = (TextView) v.findViewById(R.id.product_share);
            productImage = (ImageView) v.findViewById(R.id.product_image);
            linearLayoutContent = (LinearLayout) v.findViewById(R.id.linear_layout_content);
            productSubCategory = (TextView) v.findViewById(R.id.product_subcategory);
            productBrand = (TextView) v.findViewById(R.id.product_brand);
            commercialPackage = (TextView) v.findViewById(R.id.product_comercial_package);
            productAvaliability = (TextView) v.findViewById(R.id.product_availability);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ProductRecyclerViewAdapter(ArrayList<Product> myDataset, boolean useDetailLayout,
                                      int redirectOption, User user) {
        mDataset = myDataset;
        mCurrentUser = user;
        this.array = myDataset.toArray(new Product[0]);
        mUseDetailLayout = useDetailLayout;
        mRedirectOption = redirectOption;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ProductRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        mContext = parent.getContext();
        // create a new view
        View v;
        if(mUseDetailLayout){
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.product_details, parent, false);
        }else{
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.product_min_info, parent, false);
        }
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.productName.setText(mDataset.get(position).getName());
        if(holder.productShareLabel!=null){
            holder.productShareLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(createShareIntent(mDataset.get(position)));
                }
            });
        }
        if(holder.linearLayoutContent != null){
            switch (mRedirectOption){
                case REDIRECT_PRODUCT_DETAILS:
                    holder.linearLayoutContent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, ProductDetailActivity.class);
                            intent.putExtra(ProductDetailActivity.KEY_CURRENT_USER, mCurrentUser);
                            intent.putExtra(ProductDetailFragment.KEY_PRODUCT, mDataset.get(position));
                            mContext.startActivity(intent);
                        }
                    });
                break;
                case  REDIRECT_PRODUCT_LIST:
                    holder.linearLayoutContent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, ProductsListActivity.class);
                            intent.putExtra(ProductsListActivity.KEY_CURRENT_USER, mCurrentUser);
                            intent.putExtra(ProductsListActivity.KEY_PRODUCT_SUBCATEGORY_ID,
                                    mDataset.get(position).getProductSubCategory().getId());
                            mContext.startActivity(intent);
                        }
                    });
                break;
            }
        }
        if(holder.productSubCategory!=null){
            if(mDataset.get(position).getProductSubCategory()!=null
                    && !TextUtils.isEmpty(mDataset.get(position).getProductSubCategory().getDescription())){
                holder.productSubCategory.setText(mDataset.get(position).getProductSubCategory().getDescription());
            }else{
                holder.productSubCategory.setVisibility(TextView.GONE);
            }
        }
        if(holder.productBrand!=null){
            if(mDataset.get(position).getProductBrand()!=null
                    && !TextUtils.isEmpty(mDataset.get(position).getProductBrand().getDescription())){
                holder.productBrand.setText(mDataset.get(position).getProductBrand().getDescription());
            }else{
                holder.productBrand.setVisibility(TextView.GONE);
            }
        }

        if(holder.commercialPackage!=null){
            if(mDataset.get(position).getProductCommercialPackage()!=null
                    && !TextUtils.isEmpty(mDataset.get(position).getProductCommercialPackage().getUnitDescription())){
                holder.commercialPackage.setText(mContext.getString(R.string.commercial_package,
                                mDataset.get(position).getProductCommercialPackage().getUnits() + " " +
                                mDataset.get(position).getProductCommercialPackage().getUnitDescription()));
            }else{
                holder.commercialPackage.setVisibility(TextView.GONE);
            }
        }

        if(mDataset.get(position).getImageFileName()!=null){
            Bitmap img = Utils.getImageByFileName(mContext, mCurrentUser, mDataset.get(position).getImageFileName());
            if(img!=null){
                holder.productImage.setImageBitmap(img);
            }else{
                holder.productImage.setImageResource(mDataset.get(position).getImageId());
            }
        }else{
            holder.productImage.setImageResource(mDataset.get(position).getImageId());
        }

        if(holder.productAvaliability!=null){
            holder.productAvaliability.setText(mContext.getString(R.string.availability,
                    mDataset.get(position).getAvailability()));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private Intent createShareIntent(Product product){
        String fileName = "tmpImg.jgg";
        if(product.getImageFileName()!=null){
            Bitmap productImage = Utils.getImageByFileName(mContext, mCurrentUser,
                    product.getImageFileName());
            if(productImage!=null){
                Utils.createFileInCacheDir(fileName, productImage, mContext);
            }else{
                Utils.createFileInCacheDir(fileName, product.getImageId(), mContext);
            }
        }else{
            Utils.createFileInCacheDir(fileName, product.getImageId(), mContext);
        }
        return Utils.createShareProductIntent(mContext, product, fileName);
    }

    public void filter (String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        mDataset.clear();
        if (charText.length() == 0) {
            mDataset.addAll(Arrays.asList(array));
        } else {
            for (Product product : array) {
                if (product.getName().toLowerCase(Locale.getDefault()).contains(charText)
                        || (product.getDescription()!=null && product.getDescription()
                        .toLowerCase(Locale.getDefault()).contains(charText))) {
                    mDataset.add(product);
                }
            }
        }
        notifyDataSetChanged();
    }
}