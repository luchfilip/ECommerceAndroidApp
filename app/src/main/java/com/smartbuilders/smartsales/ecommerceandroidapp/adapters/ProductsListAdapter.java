package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.Toast;

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
        public TextView productInternalCode;
        public ImageView productImage;
        public TextView productBrand;
        public TextView commercialPackage;
        public TextView productAvailability;
        public LinearLayout linearLayoutContent;
        public ImageView shareImageView;
        public ImageView favoriteImageView;
        public ImageView addToShoppingCartImage;
        public ImageView addToShoppingSaleImage;
        public RatingBar productRatingBar;

        public ViewHolder(View v) {
            super(v);
            productName = (TextView) v.findViewById(R.id.product_name);
            productInternalCode = (TextView) v.findViewById(R.id.product_internal_code);
            productImage = (ImageView) v.findViewById(R.id.product_image);
            linearLayoutContent = (LinearLayout) v.findViewById(R.id.linear_layout_content);
            productBrand = (TextView) v.findViewById(R.id.product_brand);
            commercialPackage = (TextView) v.findViewById(R.id.product_commercial_package);
            productAvailability = (TextView) v.findViewById(R.id.product_availability);
            shareImageView = (ImageView) v.findViewById(R.id.share_imageView);
            favoriteImageView = (ImageView) v.findViewById(R.id.favorite_imageView);
            addToShoppingCartImage = (ImageView) v.findViewById(R.id.addToShoppingCart_imageView);
            addToShoppingSaleImage = (ImageView) v.findViewById(R.id.addToShoppingSale_imageView);
            productRatingBar = (RatingBar) v.findViewById(R.id.product_ratingbar);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ProductsListAdapter(Context context, FragmentActivity fragmentActivity, ArrayList<Product> myDataset,
                               boolean useDetailLayout, int redirectOption, User user) {
        mContext = context;
        mFragmentActivity = fragmentActivity;
        mDataset = myDataset;
        mCurrentUser = user;
        mUseDetailLayout = useDetailLayout;
        mRedirectOption = redirectOption;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ProductsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(mDataset==null || mDataset.get(position) == null){
            return;
        }

        Utils.loadThumbImageByFileName(mContext, mCurrentUser,
                mDataset.get(position).getImageFileName(), holder.productImage);

        holder.productName.setText(mDataset.get(position).getName());

        if(holder.productInternalCode!=null){
            if(mDataset.get(position).getInternalCode()!=null){
                holder.productInternalCode.setText(mContext.getString(R.string.product_internalCode,
                        mDataset.get(position).getInternalCode()));
            }
        }

        if(holder.linearLayoutContent != null){
            switch (mRedirectOption){
                case REDIRECT_PRODUCT_DETAILS:
                    holder.linearLayoutContent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, ProductDetailActivity.class);
                            intent.putExtra(ProductDetailActivity.KEY_PRODUCT_ID, mDataset.get(holder.getAdapterPosition()).getId());
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
                                    mDataset.get(holder.getAdapterPosition()).getProductSubCategory().getId());
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
                holder.productBrand.setVisibility(TextView.VISIBLE);
            }else{
                holder.productBrand.setVisibility(TextView.GONE);
            }
        }

        if(holder.productRatingBar!=null){
            if(mDataset.get(position).getRating()>=0){
                ((RatingBar) holder.productRatingBar.findViewById(R.id.product_ratingbar))
                        .setRating(mDataset.get(position).getRating());
            }
        }

        if(holder.commercialPackage!=null){
            if(mDataset.get(position).getProductCommercialPackage()!=null
                    && !TextUtils.isEmpty(mDataset.get(position).getProductCommercialPackage().getUnitDescription())){
                holder.commercialPackage.setText(mContext.getString(R.string.commercial_package,
                                mDataset.get(position).getProductCommercialPackage().getUnits(), mDataset.get(position).getProductCommercialPackage().getUnitDescription()));
                holder.commercialPackage.setVisibility(TextView.VISIBLE);
            }else{
                holder.commercialPackage.setVisibility(TextView.GONE);
            }
        }

        holder.productAvailability.setText(mContext.getString(R.string.availability,
                    mDataset.get(position).getAvailability()));

        if(holder.shareImageView!=null) {
            holder.shareImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.shareImageView.setEnabled(false);
                    new CreateShareIntentThread(mFragmentActivity, mDataset.get(holder.getAdapterPosition()),
                            holder.shareImageView).start();
                }
            });
        }

        if(holder.favoriteImageView!=null) {
            if(mDataset.get(position).isFavorite()){
                holder.favoriteImageView.setImageResource(R.drawable.ic_favorite_black_24dp);
                holder.favoriteImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String result = removeFromWishList(mDataset.get(holder.getAdapterPosition()).getId());
                        if (result == null) {
                            mDataset.get(holder.getAdapterPosition()).setFavorite(false);
                            notifyItemChanged(holder.getAdapterPosition());
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
                        String result = addToWishList(mDataset.get(holder.getAdapterPosition()).getId());
                        if (result == null) {
                            mDataset.get(holder.getAdapterPosition()).setFavorite(true);
                            notifyItemChanged(holder.getAdapterPosition());
                        } else {
                            Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }

        holder.addToShoppingCartImage.setColorFilter(Utils.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        holder.addToShoppingCartImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToShoppingCart(mDataset.get(holder.getAdapterPosition()));
            }
        });

        holder.addToShoppingSaleImage.setColorFilter(Utils.getColor(mContext, R.color.golden), PorterDuff.Mode.SRC_ATOP);
        holder.addToShoppingSaleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToShoppingSale(mDataset.get(holder.getAdapterPosition()));
            }
        });
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
        product = (new ProductDB(mContext, mCurrentUser)).getProductById(product.getId());
        DialogAddToShoppingCart dialogAddToShoppingCart =
                DialogAddToShoppingCart.newInstance(product, mCurrentUser);
        dialogAddToShoppingCart.show(mFragmentActivity.getSupportFragmentManager(),
                DialogAddToShoppingCart.class.getSimpleName());
    }

    private void addToShoppingSale(Product product) {
        product = (new ProductDB(mContext, mCurrentUser)).getProductById(product.getId());
        DialogAddToShoppingSale dialogAddToShoppingSale =
                DialogAddToShoppingSale.newInstance(product, mCurrentUser);
        dialogAddToShoppingSale.show(mFragmentActivity.getSupportFragmentManager(),
                DialogAddToShoppingCart.class.getSimpleName());
    }

    private String addToWishList(int productId) {
        return (new OrderLineDB(mContext, mCurrentUser)).addProductToWishList(productId);
    }

    private String removeFromWishList(int productId) {
        return (new OrderLineDB(mContext, mCurrentUser)).removeProductFromWishList(productId);
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