package com.smartbuilders.smartsales.ecommerce.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.BuildConfig;
import com.smartbuilders.smartsales.ecommerce.data.ProductDB;
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
import com.smartbuilders.synchronizer.ids.model.UserProfile;

import java.util.ArrayList;

/**
 * Created by Alberto on 7/4/2016.
 */
public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {

    private Context mContext;
    private Fragment mFragment;
    private View mParentLayout;
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
        public View productPriceContainer;
        public TextView productPriceCurrencyName;
        public TextView productPrice;
        public TextView productAvailability;
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
            productPriceContainer = v.findViewById(R.id.product_price_container);
            productPriceCurrencyName = (TextView) v.findViewById(R.id.product_price_currency_name);
            productPrice = (TextView) v.findViewById(R.id.product_price);
            productAvailability = (TextView) v.findViewById(R.id.product_availability);
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
        void addToShoppingSale(Product product, User user, boolean managePriceInOrder);
        void reloadWishList(ArrayList<OrderLine> wishListLines, boolean setData);
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
                Product product = (new ProductDB(mContext, mUser))
                        .getProductById(mDataset.get(holder.getAdapterPosition()).getProductId());
                if (product!=null) {
                    mContext.startActivity(new Intent(mContext, ProductDetailActivity.class)
                            .putExtra(ProductDetailActivity.KEY_PRODUCT, product));
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.no_product_details), Toast.LENGTH_SHORT).show();
                }
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

        if (mIsManagePriceInOrder
                && mDataset.get(position).getProduct().getProductPriceAvailability().getAvailability()>0
                && mDataset.get(position).getProduct().getProductPriceAvailability().getPrice()>0) {
            if (mShowProductTotalPrice) {
                holder.productPriceCurrencyName.setText(mDataset.get(position).getProduct().getProductPriceAvailability().getCurrency().getName());
                holder.productPrice.setText(mDataset.get(position).getProduct().getProductPriceAvailability().getTotalPriceStringFormat());
                holder.productPriceContainer.setVisibility(View.VISIBLE);
            } else if (mShowProductPrice) {
                holder.productPriceCurrencyName.setText(mDataset.get(position).getProduct().getProductPriceAvailability().getCurrency().getName());
                holder.productPrice.setText(mDataset.get(position).getProduct().getProductPriceAvailability().getPriceStringFormat());
                holder.productPriceContainer.setVisibility(View.VISIBLE);
            } else {
                holder.productPriceContainer.setVisibility(View.INVISIBLE);
            }
        } else {
            holder.productPriceContainer.setVisibility(mIsManagePriceInOrder ? View.INVISIBLE : View.GONE);
        }

        //if (mDataset.get(position).getProduct().getProductPriceAvailability().getAvailability()>0) {
            //holder.productAvailability.setVisibility(View.VISIBLE);
            holder.productAvailability.setText(mContext.getString(R.string.availability,
                    mDataset.get(position).getProduct().getProductPriceAvailability().getAvailability()));
        //} else {
            //holder.productAvailability.setVisibility(mIsManagePriceInOrder ? View.INVISIBLE : View.GONE);
        //}

        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int itemPosition = holder.getAdapterPosition();
                final OrderLine orderLine = mDataset.get(itemPosition);

                new AlertDialog.Builder(mContext)
                        .setMessage(mContext.getString(R.string.delete_from_wish_list_question,
                                mDataset.get(holder.getAdapterPosition()).getProduct().getName()))
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String result = orderLineDB.deleteOrderLine(orderLine.getId());
                                if(result == null){
                                    removeItem(itemPosition);
                                    if (mParentLayout!=null) {
                                        Snackbar.make(mParentLayout, R.string.product_removed, Snackbar.LENGTH_LONG)
                                                .setAction(R.string.undo, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        String result = orderLineDB.restoreOrderLine(orderLine.getId());
                                                        if (result == null) {
                                                            addItem(itemPosition, orderLine);
                                                        } else {
                                                            Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }).show();
                                    }
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
                if (BuildConfig.IS_SALES_FORCE_SYSTEM
                        || (mUser.getUserProfileId()== UserProfile.SALES_MAN_PROFILE_ID && mIsManagePriceInOrder)) {
                    try {
                        SalesOrderLine salesOrderLine = (new SalesOrderLineDB(mContext, mUser))
                                .getSalesOrderLineFromShoppingSales(mDataset.get(holder.getAdapterPosition()).getProductId(),
                                        Utils.getAppCurrentBusinessPartnerId(mContext, mUser));
                        if (salesOrderLine != null) {
                            ((Callback) mFragment).updateQtyOrderedInShoppingSales(salesOrderLine, mUser);
                        } else {
                            ((Callback) mFragment).addToShoppingSale(mDataset.get(holder.getAdapterPosition()).getProduct(), mUser, mIsManagePriceInOrder);
                        }
                    } catch (Exception e) {
                        //do nothing
                    }
                } else {
                    ((Callback) mFragment).addToShoppingSale(mDataset.get(holder.getAdapterPosition()).getProduct(), mUser, mIsManagePriceInOrder);
                }
            }
        });

        if (mDataset.get(position).getProduct().getProductBrand() != null
                && mDataset.get(position).getProduct().getProductBrand().getName() != null) {
            holder.productBrand.setText(Html.fromHtml(mContext.getString(R.string.brand_detail_html,
                    mDataset.get(position).getProduct().getProductBrand().getName())));
            holder.productBrand.setVisibility(View.VISIBLE);
        } else {
            holder.productBrand.setVisibility(View.GONE);
        }

        if (mDataset.get(position).getProduct().getInternalCode()!=null) {
            holder.productInternalCode.setText(mDataset.get(position).getProduct().getInternalCodeMayoreoFormat());
            holder.productInternalCode.setVisibility(View.VISIBLE);
        } else {
            holder.productInternalCode.setVisibility(View.GONE);
        }

        holder.productReference.setText(mDataset.get(position).getProduct().getReference());
    }

    public OrderLine getItem(int position) {
        try {
            return mDataset.get(position);
        } catch (Exception e) {
            return null;
        }
    }

    public void removeItem(int position) {
        try {
            mDataset.remove(position);
            notifyItemRemoved(position);
            ((Callback) mFragment).reloadWishList(mDataset, false);
        } catch (Exception e) {
            //do nothing
        }
    }

    public void addItem(int position, OrderLine item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
        ((Callback) mFragment).reloadWishList(mDataset, false);
    }

    public ArrayList<OrderLine> getData() {
        return mDataset;
    }

    public void setData(ArrayList<OrderLine> wishListLines) {
        mDataset = wishListLines;
        notifyDataSetChanged();
    }

    public void setParentLayout(View parentLayout) {
        mParentLayout = parentLayout;
    }
}