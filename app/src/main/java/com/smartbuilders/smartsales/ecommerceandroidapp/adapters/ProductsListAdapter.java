package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.DialogAddToShoppingCart;
import com.smartbuilders.smartsales.ecommerceandroidapp.DialogAddToShoppingSale;
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductDetailActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductsListActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ProductDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;

/**
 * Created by Alberto on 22/3/2016.
 */
public class ProductsListAdapter extends RecyclerView.Adapter<ProductsListAdapter.ViewHolder> {

    public static final int REDIRECT_PRODUCT_LIST = 0;
    public static final int REDIRECT_PRODUCT_DETAILS = 1;

    private FragmentActivity mFragmentActivity;
    private ArrayList<Product> mDataset;
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
        public ImageView productImage;
        public TextView productBrand;
        public TextView commercialPackage;
        public TextView productAvailability;
        public LinearLayout linearLayoutContent;
        public ImageView shareImageView;
        public ImageView favoriteImageView;
        public Button addToShoppingCartButton;
        public Button addToShoppingSaleButton;
        public ImageView addToShoppingCartImage;
        public ImageView addToShoppingSaleImage;

        public ViewHolder(View v) {
            super(v);
            productName = (TextView) v.findViewById(R.id.product_name);
            productImage = (ImageView) v.findViewById(R.id.product_image);
            linearLayoutContent = (LinearLayout) v.findViewById(R.id.linear_layout_content);
            productBrand = (TextView) v.findViewById(R.id.product_brand);
            commercialPackage = (TextView) v.findViewById(R.id.product_commercial_package);
            productAvailability = (TextView) v.findViewById(R.id.product_availability);
            shareImageView = (ImageView) v.findViewById(R.id.share_imageView);
            favoriteImageView = (ImageView) v.findViewById(R.id.favorite_imageView);
            addToShoppingCartButton = (Button) v.findViewById(R.id.product_addtoshoppingcart_button);
            addToShoppingSaleButton = (Button) v.findViewById(R.id.product_addtoshoppingsales_button);
            addToShoppingCartImage = (ImageView) v.findViewById(R.id.addToShoppingCart_imageView);
            addToShoppingSaleImage = (ImageView) v.findViewById(R.id.addToShoppingSale_imageView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ProductsListAdapter(FragmentActivity fragmentActivity, ArrayList<Product> myDataset,
                               boolean useDetailLayout, int redirectOption, User user) {
        mFragmentActivity = fragmentActivity;
        mDataset = myDataset;
        mCurrentUser = user;
        mUseDetailLayout = useDetailLayout;
        mRedirectOption = redirectOption;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ProductsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
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

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(mDataset==null || mDataset.get(position) == null){
            return;
        }
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.productName.setText(mDataset.get(position).getName());

        if(holder.linearLayoutContent != null){
            switch (mRedirectOption){
                case REDIRECT_PRODUCT_DETAILS:
                    holder.linearLayoutContent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, ProductDetailActivity.class);
                            intent.putExtra(ProductDetailActivity.KEY_PRODUCT_ID, mDataset.get(position).getId());
                            mContext.startActivity(intent);
                        }
                    });
                break;
                case  REDIRECT_PRODUCT_LIST:
                    holder.linearLayoutContent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, ProductsListActivity.class);
                            intent.putExtra(ProductsListActivity.KEY_PRODUCT_SUBCATEGORY_ID,
                                    mDataset.get(position).getProductSubCategory().getId());
                            mContext.startActivity(intent);
                        }
                    });
                break;
            }
        }

        if(holder.productBrand!=null){
            if(mDataset.get(position).getProductBrand()!=null
                    && !TextUtils.isEmpty(mDataset.get(position).getProductBrand().getDescription())){
                holder.productBrand.setText(mContext.getString(R.string.brand_detail,
                        mDataset.get(position).getProductBrand().getDescription()));
            }else{
                holder.productBrand.setVisibility(TextView.GONE);
            }
        }

        if(holder.commercialPackage!=null){
            if(mDataset.get(position).getProductCommercialPackage()!=null
                    && !TextUtils.isEmpty(mDataset.get(position).getProductCommercialPackage().getUnitDescription())){
                holder.commercialPackage.setText(mContext.getString(R.string.commercial_package,
                                mDataset.get(position).getProductCommercialPackage().getUnits(), mDataset.get(position).getProductCommercialPackage().getUnitDescription()));
            }else{
                holder.commercialPackage.setVisibility(TextView.GONE);
            }
        }

        if(!TextUtils.isEmpty(mDataset.get(position).getImageFileName())){
            File img = Utils.getFileThumbByFileName(mContext, mCurrentUser, mDataset.get(position).getImageFileName());
            if(img!=null){
                Picasso.with(mContext).load(img).error(R.drawable.no_image_available).into(holder.productImage);
            }else{
                Picasso.with(mContext)
                        .load(mCurrentUser.getServerAddress() + "/IntelligentDataSynchronizer/GetThumbImage?fileName="
                                + mDataset.get(position).getImageFileName())
                        .error(R.drawable.no_image_available)
                        .into(holder.productImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                Utils.createFileInThumbDir(mDataset.get(position).getImageFileName(),
                                        ((BitmapDrawable)holder.productImage.getDrawable()).getBitmap(),
                                        mCurrentUser, mContext);
                            }

                            @Override
                            public void onError() {
                            }
                        });
            }
        }else{
            holder.productImage.setImageResource(R.drawable.no_image_available);
        }

        if(holder.productAvailability !=null){
            holder.productAvailability.setText(mContext.getString(R.string.availability,
                    mDataset.get(position).getAvailability()));
        }else{
            holder.productAvailability.setVisibility(View.VISIBLE);
        }

        if(holder.shareImageView!=null) {
            //holder.shareImageView.setColorFilter(mContext.getResources().getColor(R.color.black),
            //        PorterDuff.Mode.SRC_ATOP);
            holder.shareImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.shareImageView.setEnabled(false);
                    new CreateShareIntentThread(mFragmentActivity, mDataset.get(position), holder.shareImageView).start();
                }
            });
        }

        if(holder.favoriteImageView!=null) {
            //holder.favoriteImageView.setColorFilter(mContext.getResources().getColor(R.color.heart_color),
            //    PorterDuff.Mode.SRC_ATOP);
            if(mDataset.get(position).isFavorite()){
                holder.favoriteImageView.setImageResource(R.drawable.ic_favorite_black_24dp);
                holder.favoriteImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String result = removeFromWishList(mDataset.get(position));
                        if (result == null) {
                            mDataset.get(position).setFavorite(false);
                            notifyItemChanged(position);
                        } else {
                            Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }else{
                holder.favoriteImageView.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                holder.favoriteImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String result = addToWishList(mDataset.get(position));
                        if (result == null) {
                            mDataset.get(position).setFavorite(true);
                            notifyItemChanged(position);
                        } else {
                            Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }

        if(holder.addToShoppingCartButton!=null) {
            holder.addToShoppingCartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addToShoppingCart(mDataset.get(position));
                }
            });
        }

        if(holder.addToShoppingSaleButton!=null) {
            holder.addToShoppingSaleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addToShoppingSale(mDataset.get(position));
                }
            });
        }

        if(holder.addToShoppingCartImage!=null){
            holder.addToShoppingCartImage.setColorFilter(mContext.getResources().getColor(R.color.colorPrimary),
                    PorterDuff.Mode.SRC_ATOP);
            holder.addToShoppingCartImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addToShoppingCart(mDataset.get(position));
                }
            });
        }

        if(holder.addToShoppingSaleImage!=null) {
            holder.addToShoppingSaleImage.setColorFilter(mContext.getResources().getColor(R.color.golden),
                    PorterDuff.Mode.SRC_ATOP);
            holder.addToShoppingSaleImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addToShoppingSale(mDataset.get(position));
                }
            });
        }
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

    private void addToShoppingCart(Product product) {
        product = (new ProductDB(mContext, mCurrentUser)).getProductById(product.getId(), false);
        DialogAddToShoppingCart dialogAddToShoppingCart =
                DialogAddToShoppingCart.newInstance(product, mCurrentUser);
        dialogAddToShoppingCart.show(mFragmentActivity.getSupportFragmentManager(),
                DialogAddToShoppingCart.class.getSimpleName());
    }

    private void addToShoppingSale(Product product) {
        product = (new ProductDB(mContext, mCurrentUser)).getProductById(product.getId(), false);
        DialogAddToShoppingSale dialogAddToShoppingSale =
                DialogAddToShoppingSale.newInstance(product, mCurrentUser);
        dialogAddToShoppingSale.show(mFragmentActivity.getSupportFragmentManager(),
                DialogAddToShoppingCart.class.getSimpleName());
    }

    private String addToWishList(Product product) {
        product = (new ProductDB(mContext, mCurrentUser)).getProductById(product.getId(), false);
        return (new OrderLineDB(mContext, mCurrentUser)).addProductToWishList(product);
    }

    private String removeFromWishList(Product product) {
        product = (new ProductDB(mContext, mCurrentUser)).getProductById(product.getId(), false);
        return (new OrderLineDB(mContext, mCurrentUser)).removeProductFromWishList(product);
    }

    class CreateShareIntentThread extends Thread {
        private Activity mActivity;
        private Product mProduct;
        private ImageView mShareProductImageView;

        CreateShareIntentThread(Activity activity, Product product, ImageView shareProductImageView) {
            mActivity = activity;
            mProduct = product;
            mShareProductImageView = shareProductImageView;
        }

        public void run() {
            final Intent shareIntent = Intent.createChooser(Utils.createShareProductIntent(mProduct,
                    mContext, mCurrentUser), mContext.getString(R.string.share_image));
            if(mActivity!=null){
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mContext.startActivity(shareIntent);
                        mShareProductImageView.setEnabled(true);
                    }
                });
            }
        }
    }
}