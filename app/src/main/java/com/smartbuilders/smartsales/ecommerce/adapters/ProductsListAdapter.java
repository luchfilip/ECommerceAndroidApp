package com.smartbuilders.smartsales.ecommerce.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.regex.Pattern;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.DialogAddToShoppingCart;
import com.smartbuilders.smartsales.ecommerce.DialogAddToShoppingSale;
import com.smartbuilders.smartsales.ecommerce.DialogSortProductListOptions;
import com.smartbuilders.smartsales.ecommerce.DialogUpdateShoppingCartQtyOrdered;
import com.smartbuilders.smartsales.ecommerce.ProductDetailActivity;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerce.data.ProductDB;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 * Created by Alberto on 22/3/2016.
 */
public class ProductsListAdapter extends RecyclerView.Adapter<ProductsListAdapter.ViewHolder> {

    public static final int EMPTY_LAYOUT                = 0;
    public static final int MASK_PRODUCT_MIN_INFO       = 1;
    public static final int MASK_PRODUCT_DETAILS        = 2;
    public static final int MASK_PRODUCT_LARGE_DETAILS  = 3;

    public static final int FILTER_BY_PRODUCT_NAME              = 1;
    public static final int FILTER_BY_PRODUCT_INTERNAL_CODE     = 2;
    public static final int FILTER_BY_PRODUCT_BRAND_DESCRIPTION = 3;
    public static final int FILTER_BY_PRODUCT_DESCRIPTION       = 4;
    public static final int FILTER_BY_PRODUCT_PURPOSE           = 5;
    // Regular expression in Java to check if String is number or not
    private static final Pattern patternIsNotNumeric = Pattern.compile(".*[^0-9].*");

    private FragmentActivity mFragmentActivity;
    private ArrayList<Product> mDataset;
    private Context mContext;
    private User mUser;
    private int mMask;
    private ArrayList<Product> filterAux;
    private boolean mIsManagePriceInOrder;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView productName;
        public TextView productInternalCode;
        public ImageView productImage;
        public TextView productBrand;
        public TextView productDescription;
        public TextView productPurpose;
        public TextView commercialPackage;
        public TextView productPrice;
        public TextView productAvailability;
        public View goToProductDetails;
        public ImageView shareImageView;
        public ImageView favoriteImageView;
        public ImageView addToShoppingCartImage;
        public ImageView addToShoppingSaleImage;
        public View productRatingBarContainer;
        public TextView productRatingBarLabelTextView;
        public RatingBar productRatingBar;

        public ViewHolder(View v) {
            super(v);
            productName = (TextView) v.findViewById(R.id.product_name);
            productInternalCode = (TextView) v.findViewById(R.id.product_internal_code);
            productImage = (ImageView) v.findViewById(R.id.product_image);
            goToProductDetails = v.findViewById(R.id.go_to_product_details);
            productBrand = (TextView) v.findViewById(R.id.product_brand);
            productDescription = (TextView) v.findViewById(R.id.product_description);
            productPurpose = (TextView) v.findViewById(R.id.product_purpose);
            commercialPackage = (TextView) v.findViewById(R.id.product_commercial_package);
            productPrice = (TextView) v.findViewById(R.id.product_price);
            productAvailability = (TextView) v.findViewById(R.id.product_availability);
            shareImageView = (ImageView) v.findViewById(R.id.share_imageView);
            favoriteImageView = (ImageView) v.findViewById(R.id.favorite_imageView);
            addToShoppingCartImage = (ImageView) v.findViewById(R.id.addToShoppingCart_imageView);
            addToShoppingSaleImage = (ImageView) v.findViewById(R.id.addToShoppingSale_imageView);
            productRatingBarContainer = v.findViewById(R.id.product_ratingBar_container);
            productRatingBarLabelTextView = (TextView) v.findViewById(R.id.product_ratingBar_label_textView);
            productRatingBar = (RatingBar) v.findViewById(R.id.product_ratingBar);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ProductsListAdapter(Context context, FragmentActivity fragmentActivity,
                               ArrayList<Product> products, int mask, int sortOption, User user) {
        mContext = context;
        mFragmentActivity = fragmentActivity;
        mDataset = products;
        this.filterAux = new ArrayList<>();
        this.filterAux.addAll(mDataset);
        mUser = user;
        mMask = mask;
        mIsManagePriceInOrder = Parameter.isManagePriceInOrder(context, mUser);
        sortProductList(sortOption);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ProductsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v;
        switch (mMask){
            case MASK_PRODUCT_MIN_INFO:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_min_info, parent, false);
                break;
            case MASK_PRODUCT_DETAILS:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_details, parent, false);
                break;
            case MASK_PRODUCT_LARGE_DETAILS:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_large_details, parent, false);
                break;
            case EMPTY_LAYOUT:
            default:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.empty_layout, parent, false);
        }
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(mMask!=MASK_PRODUCT_MIN_INFO && mMask!=MASK_PRODUCT_DETAILS
                && mMask!=MASK_PRODUCT_LARGE_DETAILS){
            return;
        }

        if(mMask==MASK_PRODUCT_LARGE_DETAILS){
            Utils.loadOriginalImageByFileName(mContext, mUser,
                    mDataset.get(position).getImageFileName(), holder.productImage);
        }else{
            Utils.loadThumbImageByFileName(mContext, mUser,
                    mDataset.get(position).getImageFileName(), holder.productImage);
        }
        holder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProductDetails(mDataset.get(holder.getAdapterPosition()).getId());
            }
        });

        holder.productName.setText(mDataset.get(position).getName());

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
                new CreateShareIntentThread(mFragmentActivity, mDataset.get(holder.getAdapterPosition()),
                        holder.shareImageView).start();
            }
        });

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

        holder.addToShoppingCartImage.setColorFilter(Utils.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        holder.addToShoppingCartImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderLine orderLine = (new OrderLineDB(mContext, mUser))
                        .getOrderLineFromShoppingCartByProductId(mDataset.get(holder.getAdapterPosition()).getId());
                if(orderLine!=null){
                    updateQtyOrderedInShoppingCart(orderLine);
                }else{
                    addToShoppingCart(mDataset.get(holder.getAdapterPosition()));
                }
            }
        });

        holder.addToShoppingSaleImage.setColorFilter(Utils.getColor(mContext, R.color.golden), PorterDuff.Mode.SRC_ATOP);
        holder.addToShoppingSaleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToShoppingSale(mDataset.get(holder.getAdapterPosition()));
            }
        });

        holder.goToProductDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProductDetails(mDataset.get(holder.getAdapterPosition()).getId());
            }
        });

        if(mMask==MASK_PRODUCT_DETAILS || mMask==MASK_PRODUCT_LARGE_DETAILS){
            if(mDataset.get(position).getInternalCode()!=null){
                holder.productInternalCode.setText(mContext.getString(R.string.product_internalCode,
                        mDataset.get(position).getInternalCode()));
            }

            if(mDataset.get(position).getProductBrand()!=null
                    && !TextUtils.isEmpty(mDataset.get(position).getProductBrand().getDescription())){
                holder.productBrand.setText(mContext.getString(R.string.brand_detail,
                        mDataset.get(position).getProductBrand().getDescription()));
                holder.productBrand.setVisibility(TextView.VISIBLE);
            }else{
                holder.productBrand.setVisibility(TextView.GONE);
            }

            if(Parameter.showProductRatingBar(mContext, mUser)){
                holder.productRatingBarLabelTextView.setText(mContext.getString(R.string.product_ratingBar_label_text_detail,
                        Parameter.getProductRatingBarLabelText(mContext, mUser)));
                if(mDataset.get(position).getRating()>=0){
                    holder.productRatingBar.setRating(mDataset.get(position).getRating());
                }
            }else{
                holder.productRatingBarContainer.setVisibility(View.GONE);
            }

            if(mDataset.get(position).getProductCommercialPackage()!=null
                    && !TextUtils.isEmpty(mDataset.get(position).getProductCommercialPackage().getUnitDescription())){
                holder.commercialPackage.setText(mContext.getString(R.string.commercial_package,
                                mDataset.get(position).getProductCommercialPackage().getUnits(),
                        mDataset.get(position).getProductCommercialPackage().getUnitDescription()));
                holder.commercialPackage.setVisibility(TextView.VISIBLE);
            }else{
                holder.commercialPackage.setVisibility(TextView.GONE);
            }

            if(holder.productDescription!=null){
                if(!TextUtils.isEmpty(mDataset.get(position).getDescription())){
                    holder.productDescription.setText(mContext.getString(R.string.product_description_detail,
                            mDataset.get(position).getDescription()));
                }else{
                    holder.productDescription.setVisibility(View.GONE);
                }
            }

            if(holder.productPurpose!=null){
                if(!TextUtils.isEmpty(mDataset.get(position).getPurpose())){
                    holder.productPurpose.setText(mContext.getString(R.string.product_purpose_detail,
                            mDataset.get(position).getPurpose()));
                }else{
                    holder.productPurpose.setVisibility(View.GONE);
                }
            }
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
        product = (new ProductDB(mContext, mUser)).getProductById(product.getId());
        DialogAddToShoppingCart dialogAddToShoppingCart =
                DialogAddToShoppingCart.newInstance(product, mUser);
        dialogAddToShoppingCart.show(mFragmentActivity.getSupportFragmentManager(),
                DialogAddToShoppingCart.class.getSimpleName());
    }

    public void updateQtyOrderedInShoppingCart(OrderLine orderLine) {
        DialogUpdateShoppingCartQtyOrdered dialogUpdateShoppingCartQtyOrdered =
                DialogUpdateShoppingCartQtyOrdered.newInstance(orderLine, true, mUser);
        dialogUpdateShoppingCartQtyOrdered.show(mFragmentActivity.getSupportFragmentManager(),
                DialogUpdateShoppingCartQtyOrdered.class.getSimpleName());
    }

    private void addToShoppingSale(Product product) {
        product = (new ProductDB(mContext, mUser)).getProductById(product.getId());
        DialogAddToShoppingSale dialogAddToShoppingSale =
                DialogAddToShoppingSale.newInstance(product, mUser);
        dialogAddToShoppingSale.show(mFragmentActivity.getSupportFragmentManager(),
                DialogAddToShoppingSale.class.getSimpleName());
    }

    private String addToWishList(int productId) {
        return (new OrderLineDB(mContext, mUser)).addProductToWishList(productId);
    }

    private String removeFromWishList(int productId) {
        return (new OrderLineDB(mContext, mUser)).removeProductFromWishList(productId);
    }

    private void goToProductDetails(int productId){
        mContext.startActivity(new Intent(mContext, ProductDetailActivity.class)
                .putExtra(ProductDetailActivity.KEY_PRODUCT_ID, productId));
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
                    mContext, mUser), mContext.getString(R.string.share_image));
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

    private void sortProductList(final int sortOption){
        Collections.sort(mDataset, new Comparator<Product>() {
            @Override
            public int compare(Product lhs, Product rhs) {
                try{
                    switch (sortOption){
                        case DialogSortProductListOptions.SORT_BY_PRODUCT_NAME_ASC:
                            return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
                        case DialogSortProductListOptions.SORT_BY_PRODUCT_NAME_DESC:
                            return rhs.getName().toLowerCase().compareTo(lhs.getName().toLowerCase());
                        case DialogSortProductListOptions.SORT_BY_PRODUCT_INTERNAL_CODE_ASC:
                            return lhs.getInternalCode().compareTo(rhs.getInternalCode());
                        case DialogSortProductListOptions.SORT_BY_PRODUCT_INTERNAL_CODE_DESC:
                            return rhs.getInternalCode().compareTo(lhs.getInternalCode());
                        case DialogSortProductListOptions.SORT_BY_PRODUCT_AVAILABILITY_ASC:
                            return Integer.valueOf(lhs.getDefaultProductPriceAvailability().getAvailability())
                                    .compareTo(rhs.getDefaultProductPriceAvailability().getAvailability());
                        case DialogSortProductListOptions.SORT_BY_PRODUCT_AVAILABILITY_DESC:
                            return Integer.valueOf(rhs.getDefaultProductPriceAvailability().getAvailability())
                                    .compareTo(lhs.getDefaultProductPriceAvailability().getAvailability());
                        default:
                            return 0;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    public void filter(String charText, int filterBy) {
        if(charText == null){
            System.out.println("public void filter(String charText, int filterBy), charText == null");
            return;
        }
        charText = charText.toLowerCase(Locale.getDefault());
        mDataset.clear();
        if (charText.length() == 0) {
            mDataset.addAll(filterAux);
        } else {
            switch (filterBy){
                case FILTER_BY_PRODUCT_NAME:
                    for (Product product : filterAux) {
                        if (!TextUtils.isEmpty(product.getName()) &&
                                product.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                            mDataset.add(product);
                        }
                    }
                    break;
                case FILTER_BY_PRODUCT_INTERNAL_CODE:
                    if(charText.length()<8 && !patternIsNotNumeric.matcher(charText).matches()){
                        for (Product product : filterAux) {
                            if (!TextUtils.isEmpty(product.getInternalCode()) &&
                                    product.getInternalCode().toLowerCase(Locale.getDefault()).startsWith(charText)) {
                                mDataset.add(product);
                            }
                        }
                    }else{
                        mDataset.clear();
                    }
                    break;
                case FILTER_BY_PRODUCT_BRAND_DESCRIPTION:
                    for (Product product : filterAux) {
                        if (product.getProductBrand()!=null &&
                                !TextUtils.isEmpty(product.getProductBrand().getDescription()) &&
                                product.getProductBrand().getDescription().toLowerCase(Locale.getDefault()).contains(charText)) {
                            mDataset.add(product);
                        }
                    }
                    break;
                case FILTER_BY_PRODUCT_DESCRIPTION:
                    for (Product product : filterAux) {
                        if (!TextUtils.isEmpty(product.getDescription()) &&
                                product.getDescription().toLowerCase(Locale.getDefault()).contains(charText)) {
                            mDataset.add(product);
                        }
                    }
                    break;
                case FILTER_BY_PRODUCT_PURPOSE:
                    for (Product product : filterAux) {
                        if (!TextUtils.isEmpty(product.getPurpose()) &&
                                product.getPurpose().toLowerCase(Locale.getDefault()).contains(charText)) {
                            mDataset.add(product);
                        }
                    }
                    break;
            }
        }
        notifyDataSetChanged();
    }
}