package com.smartbuilders.smartsales.ecommerce.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.BuildConfig;
import com.smartbuilders.smartsales.ecommerce.data.SalesOrderLineDB;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;
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
public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {

    private Context mContext;
    private Fragment mFragment;
    private ArrayList<OrderLine> mDataset;
    private User mUser;
    private OrderLineDB orderLineDB;
    private boolean mIsManagePriceInOrder;
    private boolean mShowProductPrice;
    private boolean mShowProductTotalPrice;

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView productImage;
        public ImageView deleteItem;
        public TextView productName;
        public TextView productPrice;
        public TextView productAvailability;
        public TextView productAvailabilityVariation;
        public TextView productBrand;
        public TextView productInternalCode;
        public TextView productReference;
        public ImageView shareImageView;
        public ImageView addToShoppingCartImage;
        public ImageView addToShoppingSaleImage;
        public View productRatingBarContainer;
        public TextView productRatingBarLabelTextView;
        public RatingBar productRatingBar;
        public View containerLayout;

        public ViewHolder(View v) {
            super(v);
            productImage = (ImageView) v.findViewById(R.id.product_image);
            productName = (TextView) v.findViewById(R.id.product_name);
            productPrice = (TextView) v.findViewById(R.id.product_price);
            productAvailability = (TextView) v.findViewById(R.id.product_availability);
            productAvailabilityVariation = (TextView) v.findViewById(R.id.product_availability_variation);
            productBrand = (TextView) v.findViewById(R.id.product_brand);
            productInternalCode = (TextView) v.findViewById(R.id.product_internal_code);
            productReference = (TextView) v.findViewById(R.id.product_reference);
            shareImageView = (ImageView) v.findViewById(R.id.share_imageView);
            deleteItem = (ImageView) v.findViewById(R.id.delete_item_button_img);
            addToShoppingCartImage = (ImageView) v.findViewById(R.id.addToShoppingCart_imageView);
            addToShoppingSaleImage = (ImageView) v.findViewById(R.id.addToShoppingSale_imageView);
            productRatingBarContainer = v.findViewById(R.id.product_ratingBar_container);
            productRatingBarLabelTextView = (TextView) v.findViewById(R.id.product_ratingBar_label_textView);
            productRatingBar = (RatingBar) v.findViewById(R.id.product_ratingBar);
            containerLayout = v.findViewById(R.id.container_layout);
        }
    }

    public interface Callback{
        void updateQtyOrderedInShoppingCart(OrderLine orderLine, User user);
        void addToShoppingCart(Product product, User user);
        void addToShoppingSale(Product product, User user);
        void reloadWishList(ArrayList<OrderLine> wishListLines);
        void updateQtyOrderedInShoppingSales(SalesOrderLine salesOrderLine, User user);
    }

    public WishListAdapter(Context context, Fragment fragment, ArrayList<OrderLine> data, User user) {
        mContext = context;
        mFragment = fragment;
        mDataset = data;
        mUser = user;
        orderLineDB = new OrderLineDB(context, user);
        mIsManagePriceInOrder = Parameter.isManagePriceInOrder(context, user);
        mShowProductPrice = Parameter.showProductPrice(context, user);
        mShowProductTotalPrice = Parameter.showProductTotalPrice(context, user);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WishListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wish_list_item, parent, false);
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
        holder.containerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, ProductDetailActivity.class)
                        .putExtra(ProductDetailActivity.KEY_PRODUCT_ID, mDataset.get(holder.getAdapterPosition()).getProductId()));
            }
        });

        if (BuildConfig.USE_PRODUCT_IMAGE) {
            Utils.loadThumbImageByFileName(mContext, mUser,
                    mDataset.get(position).getProduct().getImageFileName(), holder.productImage);
        } else {
            holder.productImage.setVisibility(View.GONE);
        }

        holder.productName.setText(mDataset.get(position).getProduct().getName());

        if(Parameter.showProductRatingBar(mContext, mUser)){
            holder.productRatingBarLabelTextView.setText(mContext.getString(R.string.product_ratingBar_label_text_detail,
                    Parameter.getProductRatingBarLabelText(mContext, mUser)));
            if(mDataset.get(holder.getAdapterPosition()).getProduct().getRating()>=0){
                holder.productRatingBar.setRating(mDataset.get(holder.getAdapterPosition()).getProduct().getRating());
            }
            holder.productRatingBarContainer.setVisibility(View.VISIBLE);
        }else{
            holder.productRatingBarContainer.setVisibility(View.GONE);
        }

        if (mIsManagePriceInOrder) {
            if (mShowProductTotalPrice) {
                holder.productPrice.setText(mContext.getString(R.string.product_total_price_detail,
                        mDataset.get(position).getProduct().getDefaultProductPriceAvailability().getCurrency().getName(),
                        mDataset.get(position).getProduct().getDefaultProductPriceAvailability().getTotalPriceStringFormat()));
                holder.productPrice.setVisibility(View.VISIBLE);
            } else if (mShowProductPrice) {
                holder.productPrice.setText(mContext.getString(R.string.product_price_detail,
                        mDataset.get(position).getProduct().getDefaultProductPriceAvailability().getCurrency().getName(),
                        mDataset.get(position).getProduct().getDefaultProductPriceAvailability().getPriceStringFormat()));
                holder.productPrice.setVisibility(View.VISIBLE);
            } else {
                holder.productPrice.setVisibility(View.GONE);
            }
        } else {
            holder.productPrice.setVisibility(View.GONE);
        }

        holder.productAvailability.setText(mContext.getString(R.string.availability,
                mDataset.get(position).getProduct().getDefaultProductPriceAvailability().getAvailability()));

        int productAvailabilityVariation = mDataset.get(position).getProduct().getDefaultProductPriceAvailability().getAvailability()
                - mDataset.get(position).getQuantityOrdered();
        if(productAvailabilityVariation!=0) {
            if(productAvailabilityVariation > 0){
                holder.productAvailabilityVariation.setTextColor(Utils.getColor(mContext, R.color.green_dark));
                holder.productAvailabilityVariation.setText(mContext.getString(R.string.availability_positive_variation,
                        String.valueOf(productAvailabilityVariation)));
            } else {
                holder.productAvailabilityVariation.setTextColor(Utils.getColor(mContext, R.color.black));
                holder.productAvailabilityVariation.setText(mContext.getString(R.string.availability_variation,
                        String.valueOf(productAvailabilityVariation)));
            }
        }else{
            holder.productAvailabilityVariation.setText(null);
        }

        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setMessage(mContext.getString(R.string.delete_from_wish_list_question,
                                mDataset.get(holder.getAdapterPosition()).getProduct().getName()))
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String result = orderLineDB.deleteOrderLine(mDataset.get(holder.getAdapterPosition()));
                                if(result == null){
                                    mDataset.remove(holder.getAdapterPosition());
                                    ((Callback) mFragment).reloadWishList(mDataset);
                                } else {
                                    Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }
        });

        holder.shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.shareImageView.setEnabled(false);
                new CreateShareIntentThread(mFragment.getActivity(), mContext, mUser,
                        mDataset.get(holder.getAdapterPosition()).getProduct(), holder.shareImageView).start();
            }
        });

        holder.addToShoppingCartImage.setColorFilter(Utils.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        holder.addToShoppingCartImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderLine orderLine = (new OrderLineDB(mContext, mUser))
                        .getOrderLineFromShoppingCartByProductId(mDataset.get(holder.getAdapterPosition()).getProductId());
                if(orderLine!=null){
                    ((Callback) mFragment).updateQtyOrderedInShoppingCart(orderLine, mUser);
                }else{
                    ((Callback) mFragment).addToShoppingCart(mDataset.get(holder.getAdapterPosition()).getProduct(), mUser);
                }
            }
        });

        holder.addToShoppingSaleImage.setColorFilter(Utils.getColor(mContext, R.color.golden), PorterDuff.Mode.SRC_ATOP);
        holder.addToShoppingSaleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BuildConfig.IS_SALES_FORCE_SYSTEM) {
                    try {
                        SalesOrderLine salesOrderLine = (new SalesOrderLineDB(mContext, mUser))
                                .getSalesOrderLineFromShoppingSales(mDataset.get(holder.getAdapterPosition()).getProductId(),
                                        Utils.getAppCurrentBusinessPartnerId(mContext, mUser));
                        if (salesOrderLine != null) {
                            ((Callback) mFragment).updateQtyOrderedInShoppingSales(salesOrderLine, mUser);
                        } else {
                            ((Callback) mFragment).addToShoppingSale(mDataset.get(holder.getAdapterPosition()).getProduct(), mUser);
                        }
                    } catch (Exception e) {
                        //do nothing
                    }
                } else {
                    ((Callback) mFragment).addToShoppingSale(mDataset.get(holder.getAdapterPosition()).getProduct(), mUser);
                }
            }
        });

        if (mDataset.get(position).getProduct().getProductBrand() != null
                && mDataset.get(position).getProduct().getProductBrand().getName() != null) {
            holder.productBrand.setText(mContext.getString(R.string.brand_detail,
                    mDataset.get(position).getProduct().getProductBrand().getName()));
            holder.productBrand.setVisibility(View.VISIBLE);
        } else {
            holder.productBrand.setVisibility(View.GONE);
        }

        holder.productInternalCode.setText(mDataset.get(position).getProduct().getInternalCodeMayoreoFormat());

        holder.productReference.setText(mDataset.get(position).getProduct().getReference());
    }

    public void setData(ArrayList<OrderLine> wishListLines) {
        mDataset = wishListLines;
        notifyDataSetChanged();
    }
}