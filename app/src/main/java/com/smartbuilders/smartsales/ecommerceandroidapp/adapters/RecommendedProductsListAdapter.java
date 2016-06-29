package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductDetailActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Alberto on 7/4/2016.
 */
public class RecommendedProductsListAdapter extends RecyclerView.Adapter<RecommendedProductsListAdapter.ViewHolder> {

    private Context mContext;
    private Fragment mFragment;
    private ArrayList<Product> mDataset;
    private User mUser;

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView productImage;
        public TextView productName;
        public TextView productAvailability;
        public TextView productBrand;
        public TextView commercialPackage;
        public ImageView shareImageView;
        public ImageView addToShoppingCartImage;
        public ImageView addToShoppingSaleImage;
        public RatingBar productRatingBar;

        public ViewHolder(View v) {
            super(v);
            productImage = (ImageView) v.findViewById(R.id.product_image);
            productName = (TextView) v.findViewById(R.id.product_name);
            productAvailability = (TextView) v.findViewById(R.id.product_availability);
            productBrand = (TextView) v.findViewById(R.id.product_brand);
            commercialPackage = (TextView) v.findViewById(R.id.product_commercial_package);
            shareImageView = (ImageView) v.findViewById(R.id.share_imageView);
            addToShoppingCartImage = (ImageView) v.findViewById(R.id.addToShoppingCart_imageView);
            addToShoppingSaleImage = (ImageView) v.findViewById(R.id.addToShoppingSale_imageView);
            productRatingBar = (RatingBar) v.findViewById(R.id.product_ratingbar);
        }
    }

    public interface Callback{
        void updateQtyOrderedInShoppingCart(OrderLine orderLine, User user);
        void addToShoppingCart(int productId, User user);
        void addToShoppingSale(int productId, User user);
    }

    public RecommendedProductsListAdapter(Context context, Fragment fragment,
                                          ArrayList<Product> data, User user) {
        mContext = context;
        mFragment = fragment;
        mDataset = data;
        mUser = user;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecommendedProductsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recommended_product_list_item, parent, false);
        return new ViewHolder(v);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(mDataset==null){
            return 0;
        }
        return mDataset.size();
    }

    @Override
    public long getItemId(int position) {
        try {
            return mDataset.get(position).getId();
        } catch (Exception e) {
            return 0;
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(mDataset==null || mDataset.get(position) == null){
            return;
        }

        Utils.loadThumbImageByFileName(mContext, mUser,
                mDataset.get(position).getImageFileName(), holder.productImage);

        holder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProductDetails(mDataset.get(holder.getAdapterPosition()));
            }
        });

        holder.productName.setText(mDataset.get(position).getName());
        holder.productName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProductDetails(mDataset.get(holder.getAdapterPosition()));
            }
        });

        if(mDataset.get(holder.getAdapterPosition()).getRating()>=0){
            ((RatingBar) holder.productRatingBar.findViewById(R.id.product_ratingbar))
                    .setRating(mDataset.get(holder.getAdapterPosition()).getRating());
        }

        holder.productAvailability.setText(mContext.getString(R.string.availability,
                mDataset.get(position).getAvailability()));

        if(holder.shareImageView!=null) {
            holder.shareImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(Intent.createChooser(Utils.createShareProductIntent(
                            mDataset.get(holder.getAdapterPosition()), mContext, mUser), mContext.getString(R.string.share_image)));
                }
            });
        }

        holder.addToShoppingCartImage.setColorFilter(Utils.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        holder.addToShoppingCartImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderLine orderLine = (new OrderLineDB(mContext, mUser))
                        .getOrderLineFromShoppingCartByProductId(mDataset.get(holder.getAdapterPosition()).getId());
                if(orderLine!=null){
                    ((Callback) mFragment).updateQtyOrderedInShoppingCart(orderLine, mUser);
                }else{
                    ((Callback) mFragment).addToShoppingCart(mDataset.get(holder.getAdapterPosition()).getId(), mUser);
                }
            }
        });

        holder.addToShoppingSaleImage.setColorFilter(Utils.getColor(mContext, R.color.golden), PorterDuff.Mode.SRC_ATOP);
        holder.addToShoppingSaleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Callback) mFragment).addToShoppingSale(mDataset.get(holder.getAdapterPosition()).getId(), mUser);
            }
        });

        if (mDataset.get(position).getProductBrand() != null
                && mDataset.get(position).getProductBrand().getDescription() != null) {
            holder.productBrand.setText(mContext.getString(R.string.brand_detail,
                    mDataset.get(position).getProductBrand().getDescription()));
        } else {
            holder.productBrand.setVisibility(View.INVISIBLE);
        }

        if(holder.commercialPackage!=null){
            if(mDataset.get(position).getProductCommercialPackage()!=null
                    && !TextUtils.isEmpty(mDataset.get(position).getProductCommercialPackage().getUnitDescription())){
                holder.commercialPackage.setText(mContext.getString(R.string.commercial_package,
                        mDataset.get(position).getProductCommercialPackage().getUnits(),
                        mDataset.get(position).getProductCommercialPackage().getUnitDescription()));
            }else{
                holder.commercialPackage.setVisibility(TextView.GONE);
            }
        }
    }

    private void goToProductDetails(Product product) {
        Intent intent = new Intent(mContext, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.KEY_PRODUCT_ID, product.getId());
        mContext.startActivity(intent);
    }

    public void setData(ArrayList<Product> wishListLines) {
        mDataset = wishListLines;
        notifyDataSetChanged();
    }
}