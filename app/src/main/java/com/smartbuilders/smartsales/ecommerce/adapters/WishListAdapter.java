package com.smartbuilders.smartsales.ecommerce.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
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
        public TextView productBrand;
        public TextView commercialPackage;
        public ImageView shareImageView;
        public ImageView addToShoppingCartImage;
        public ImageView addToShoppingSaleImage;
        public RatingBar productRatingBar;
        public View goToProductDetails;


        public ViewHolder(View v) {
            super(v);
            productImage = (ImageView) v.findViewById(R.id.product_image);
            productName = (TextView) v.findViewById(R.id.product_name);
            productPrice = (TextView) v.findViewById(R.id.product_price);
            productAvailability = (TextView) v.findViewById(R.id.product_availability);
            productBrand = (TextView) v.findViewById(R.id.product_brand);
            commercialPackage = (TextView) v.findViewById(R.id.product_commercial_package);
            shareImageView = (ImageView) v.findViewById(R.id.share_imageView);
            deleteItem = (ImageView) v.findViewById(R.id.delete_item_button_img);
            addToShoppingCartImage = (ImageView) v.findViewById(R.id.addToShoppingCart_imageView);
            addToShoppingSaleImage = (ImageView) v.findViewById(R.id.addToShoppingSale_imageView);
            productRatingBar = (RatingBar) v.findViewById(R.id.product_ratingbar);
            goToProductDetails = v.findViewById(R.id.go_to_product_details);
        }
    }

    public interface Callback{
        void updateQtyOrderedInShoppingCart(OrderLine orderLine, User user);
        void addToShoppingCart(int productId, User user);
        void addToShoppingSale(int productId, User user);
        void reloadWishList(ArrayList<OrderLine> wishListLines);
    }

    public WishListAdapter(Context context, Fragment fragment, ArrayList<OrderLine> data, User user) {
        mContext = context;
        mFragment = fragment;
        mDataset = data;
        mUser = user;
        orderLineDB = new OrderLineDB(context, user);
        mIsManagePriceInOrder = Parameter.isManagePriceInOrder(context, mUser);
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
        Utils.loadThumbImageByFileName(mContext, mUser,
                mDataset.get(position).getProduct().getImageFileName(), holder.productImage);

        holder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProductDetails(mDataset.get(holder.getAdapterPosition()).getProduct());
            }
        });

        holder.productName.setText(mDataset.get(position).getProduct().getName());
        holder.productName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProductDetails(mDataset.get(holder.getAdapterPosition()).getProduct());
            }
        });

        if(mDataset.get(holder.getAdapterPosition()).getProduct().getRating()>=0){
            ((RatingBar) holder.productRatingBar.findViewById(R.id.product_ratingbar))
                    .setRating(mDataset.get(holder.getAdapterPosition()).getProduct().getRating());
        }

        if (mIsManagePriceInOrder) {
            holder.productPrice.setText(mContext.getString(R.string.price_detail,
                    mDataset.get(position).getProduct().getDefaultProductPriceAvailability().getCurrency().getName(),
                    mDataset.get(position).getProduct().getDefaultProductPriceAvailability().getPrice()));
            holder.productPrice.setVisibility(View.VISIBLE);
        } else {
            holder.productPrice.setVisibility(View.GONE);
        }

        holder.productAvailability.setText(mContext.getString(R.string.availability,
                mDataset.get(position).getProduct().getDefaultProductPriceAvailability().getAvailability()));

        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setMessage(mContext.getString(R.string.delete_from_wish_list_question,
                                mDataset.get(holder.getAdapterPosition()).getProduct().getName()))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String result = orderLineDB.deleteOrderLine(mDataset.get(holder.getAdapterPosition()));
                                if(result == null){
                                    mDataset.remove(holder.getAdapterPosition());
                                    ((Callback) mFragment).reloadWishList(mDataset);
                                } else {
                                    Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        holder.shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(Intent.createChooser(Utils.createShareProductIntent(
                        mDataset.get(holder.getAdapterPosition()).getProduct(), mContext, mUser), mContext.getString(R.string.share_image)));
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
                    ((Callback) mFragment).addToShoppingCart(mDataset.get(holder.getAdapterPosition()).getProductId(), mUser);
                }
            }
        });

        holder.addToShoppingSaleImage.setColorFilter(Utils.getColor(mContext, R.color.golden), PorterDuff.Mode.SRC_ATOP);
        holder.addToShoppingSaleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Callback) mFragment).addToShoppingSale(mDataset.get(holder.getAdapterPosition()).getProductId(), mUser);
            }
        });

        if (mDataset.get(position).getProduct().getProductBrand() != null
                && mDataset.get(position).getProduct().getProductBrand().getDescription() != null) {
            holder.productBrand.setText(mContext.getString(R.string.brand_detail,
                    mDataset.get(position).getProduct().getProductBrand().getDescription()));
        } else {
            holder.productBrand.setVisibility(View.INVISIBLE);
        }

        if(mDataset.get(position).getProduct().getProductCommercialPackage()!=null
                && !TextUtils.isEmpty(mDataset.get(position).getProduct().getProductCommercialPackage().getUnitDescription())){
            holder.commercialPackage.setText(mContext.getString(R.string.commercial_package,
                    mDataset.get(position).getProduct().getProductCommercialPackage().getUnits(), mDataset.get(position).getProduct().getProductCommercialPackage().getUnitDescription()));
        }else{
            holder.commercialPackage.setVisibility(TextView.GONE);
        }

        holder.goToProductDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProductDetails(mDataset.get(holder.getAdapterPosition()).getProduct());
            }
        });
    }

    private void goToProductDetails(Product product) {
        Intent intent = new Intent(mContext, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.KEY_PRODUCT_ID, product.getId());
        mContext.startActivity(intent);
    }

    public void setData(ArrayList<OrderLine> wishListLines) {
        mDataset = wishListLines;
        notifyDataSetChanged();
    }
}