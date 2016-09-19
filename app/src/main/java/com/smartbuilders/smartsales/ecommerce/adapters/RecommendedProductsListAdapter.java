package com.smartbuilders.smartsales.ecommerce.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.utils.CreateShareIntentThread;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.ProductDetailActivity;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Alberto on 7/4/2016.
 */
public class RecommendedProductsListAdapter extends
        RecyclerView.Adapter<RecommendedProductsListAdapter.ViewHolder> {

    private Context mContext;
    private Fragment mFragment;
    private ArrayList<Product> mDataset;
    private User mUser;
    private boolean mIsManagePriceInOrder;

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView productImage;
        public TextView productName;
        public TextView productPrice;
        public TextView productAvailability;
        public TextView productBrand;
        public TextView productInternalCode;
        public ImageView shareImageView;
        public ImageView favoriteImageView;
        public ImageView addToShoppingCartImage;
        public ImageView addToShoppingSaleImage;
        public View productRatingBarContainer;
        public TextView productRatingBarLabelTextView;
        public RatingBar productRatingBar;
        public View goToProductDetails;

        public ViewHolder(View v) {
            super(v);
            productImage = (ImageView) v.findViewById(R.id.product_image);
            productName = (TextView) v.findViewById(R.id.product_name);
            productPrice = (TextView) v.findViewById(R.id.product_price);
            productAvailability = (TextView) v.findViewById(R.id.product_availability);
            productBrand = (TextView) v.findViewById(R.id.product_brand);
            productInternalCode = (TextView) v.findViewById(R.id.product_internal_code);
            shareImageView = (ImageView) v.findViewById(R.id.share_imageView);
            favoriteImageView = (ImageView) v.findViewById(R.id.favorite_imageView);
            addToShoppingCartImage = (ImageView) v.findViewById(R.id.addToShoppingCart_imageView);
            addToShoppingSaleImage = (ImageView) v.findViewById(R.id.addToShoppingSale_imageView);
            productRatingBarContainer = v.findViewById(R.id.product_ratingBar_container);
            productRatingBarLabelTextView = (TextView) v.findViewById(R.id.product_ratingBar_label_textView);
            productRatingBar = (RatingBar) v.findViewById(R.id.product_ratingBar);
            goToProductDetails = v.findViewById(R.id.go_to_product_details);
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
        mIsManagePriceInOrder = Parameter.isManagePriceInOrder(context, mUser);
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
        Utils.loadThumbImageByFileName(mContext, mUser,
                mDataset.get(position).getImageFileName(), holder.productImage);

        holder.productName.setText(mDataset.get(position).getName());

        if(Parameter.showProductRatingBar(mContext, mUser)){
            holder.productRatingBarLabelTextView.setText(mContext.getString(R.string.product_ratingBar_label_text_detail,
                    Parameter.getProductRatingBarLabelText(mContext, mUser)));
            if(mDataset.get(position).getRating()>=0){
                holder.productRatingBar.setRating(mDataset.get(position).getRating());
            }
            holder.productRatingBarContainer.setVisibility(View.VISIBLE);
        }else{
            holder.productRatingBarContainer.setVisibility(View.GONE);
        }

        if (mIsManagePriceInOrder) {
            holder.productPrice.setText(mContext.getString(R.string.price_detail,
                    mDataset.get(position).getDefaultProductPriceAvailability().getCurrency().getName(),
                    mDataset.get(position).getDefaultProductPriceAvailability().getPrice()));
            holder.productPrice.setVisibility(View.VISIBLE);
        } else {
            holder.productPrice.setVisibility(View.GONE);
        }

        holder.productAvailability.setText(mContext.getString(R.string.availability,
                mDataset.get(position).getDefaultProductPriceAvailability().getAvailability()));

        holder.shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.shareImageView.setEnabled(false);
                new CreateShareIntentThread(mFragment.getActivity(), mContext, mUser,
                        mDataset.get(holder.getAdapterPosition()), holder.shareImageView).start();
            }
        });

        holder.favoriteImageView.setImageResource(mDataset.get(holder.getAdapterPosition()).isFavorite()
                ? R.drawable.ic_favorite_black_24dp : R.drawable.ic_favorite_border_black_24dp);
        holder.favoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDataset.get(holder.getAdapterPosition()).isFavorite()){
                    String result = removeFromWishList(mDataset.get(holder.getAdapterPosition()).getId());
                    if (result == null) {
                        mDataset.get(holder.getAdapterPosition()).setFavorite(false);
                        holder.favoriteImageView.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    } else {
                        Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
                    }
                }else{
                    String result = addToWishList(mDataset.get(holder.getAdapterPosition()));
                    if (result == null) {
                        mDataset.get(holder.getAdapterPosition()).setFavorite(true);
                        holder.favoriteImageView.setImageResource(R.drawable.ic_favorite_black_24dp);
                    } else {
                        Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

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
                && mDataset.get(position).getProductBrand().getName() != null) {
            holder.productBrand.setText(mContext.getString(R.string.brand_detail,
                    mDataset.get(position).getProductBrand().getName()));
            holder.productBrand.setVisibility(View.VISIBLE);
        } else {
            holder.productBrand.setVisibility(View.GONE);
        }

        if(mDataset.get(position).getInternalCode()!=null){
            holder.productInternalCode.setText(mContext.getString(R.string.product_internalCode,
                    mDataset.get(position).getInternalCode()));
            holder.productInternalCode.setVisibility(View.VISIBLE);
        }else{
            holder.productInternalCode.setVisibility(View.GONE);
        }

        holder.goToProductDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProductDetails(mDataset.get(holder.getAdapterPosition()));
            }
        });
    }

    private String addToWishList(Product product) {
        return (new OrderLineDB(mContext, mUser)).addProductToWishList(product);
    }

    private String removeFromWishList(int productId) {
        return (new OrderLineDB(mContext, mUser)).removeProductFromWishList(productId);
    }

    private void goToProductDetails(Product product) {
        Intent intent = new Intent(mContext, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.KEY_PRODUCT_ID, product.getId());
        mContext.startActivity(intent);
    }

    public void setData(ArrayList<Product> products) {
        mDataset = products;
        notifyDataSetChanged();
    }
}