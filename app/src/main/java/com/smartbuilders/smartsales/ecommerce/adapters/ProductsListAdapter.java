package com.smartbuilders.smartsales.ecommerce.adapters;

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

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.regex.Pattern;

import com.smartbuilders.smartsales.ecommerce.BuildConfig;
import com.smartbuilders.smartsales.ecommerce.data.SalesOrderLineDB;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;
import com.smartbuilders.smartsales.salesforcesystem.DialogAddToShoppingSale2;
import com.smartbuilders.smartsales.ecommerce.utils.CreateShareIntentThread;
import com.smartbuilders.smartsales.salesforcesystem.DialogUpdateShoppingSaleQtyOrdered;
import com.smartbuilders.synchronizer.ids.model.User;
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

    // Regular expression in Java to check if String is number or not
    private static final Pattern patternIsNotNumeric = Pattern.compile(".*[^0-9].*");

    private FragmentActivity mFragmentActivity;
    private ArrayList<Product> mDataset;
    private Context mContext;
    private int mSortOption;
    private User mUser;
    private int mMask;
    private ArrayList<Product> filterAux;
    private boolean mIsManagePriceInOrder;
    private boolean mShowProductPrice;
    private boolean mShowProductTotalPrice;
    private String mCurrentFilterText;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView productName;
        public TextView productInternalCode;
        public TextView productReference;
        public ImageView productImage;
        public TextView productBrand;
        public TextView productDescription;
        public TextView productPurpose;
        public TextView productPrice;
        public TextView productAvailability;
        public ImageView shareImageView;
        public View shareImageViewContainer;
        public ImageView favoriteImageView;
        public View favoriteImageViewViewContainer;
        public ImageView addToShoppingCartImageView;
        public View addToShoppingCartImageViewContainer;
        public ImageView addToShoppingSaleImageView;
        public View addToShoppingSaleImageViewContainer;
        public View productRatingBarContainer;
        public TextView productRatingBarLabelTextView;
        public RatingBar productRatingBar;
        public View productDetailsInfoContainer;
        public View containerLayout;

        public ViewHolder(View v) {
            super(v);
            productName = (TextView) v.findViewById(R.id.product_name);
            productInternalCode = (TextView) v.findViewById(R.id.product_internal_code);
            productReference = (TextView) v.findViewById(R.id.product_reference);
            productImage = (ImageView) v.findViewById(R.id.product_image);
            productBrand = (TextView) v.findViewById(R.id.product_brand);
            productDescription = (TextView) v.findViewById(R.id.product_description);
            productPurpose = (TextView) v.findViewById(R.id.product_purpose);
            productPrice = (TextView) v.findViewById(R.id.product_price);
            productAvailability = (TextView) v.findViewById(R.id.product_availability);
            shareImageView = (ImageView) v.findViewById(R.id.share_imageView);
            shareImageViewContainer = v.findViewById(R.id.share_imageView_container);
            favoriteImageView = (ImageView) v.findViewById(R.id.favorite_imageView);
            favoriteImageViewViewContainer = v.findViewById(R.id.favorite_imageView_container);
            addToShoppingCartImageView = (ImageView) v.findViewById(R.id.addToShoppingCart_imageView);
            addToShoppingCartImageViewContainer = v.findViewById(R.id.addToShoppingCart_imageView_container);
            addToShoppingSaleImageView = (ImageView) v.findViewById(R.id.addToShoppingSale_imageView);
            addToShoppingSaleImageViewContainer = v.findViewById(R.id.addToShoppingSale_imageView_container);
            productRatingBarContainer = v.findViewById(R.id.product_ratingBar_container);
            productRatingBarLabelTextView = (TextView) v.findViewById(R.id.product_ratingBar_label_textView);
            productRatingBar = (RatingBar) v.findViewById(R.id.product_ratingBar);
            productDetailsInfoContainer = v.findViewById(R.id.product_details_info_container);
            containerLayout = v.findViewById(R.id.container_layout);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ProductsListAdapter(Context context, FragmentActivity fragmentActivity,
                               ArrayList<Product> products, int mask, int sortOption, User user) {
        mContext = context;
        mFragmentActivity = fragmentActivity;
        mDataset = products;
        filterAux = new ArrayList<>();
        filterAux.addAll(mDataset);
        mSortOption = sortOption;
        mUser = user;
        mMask = mask;
        mIsManagePriceInOrder = Parameter.isManagePriceInOrder(context, user);
        mShowProductPrice = Parameter.showProductPrice(context, user);
        mShowProductTotalPrice = Parameter.showProductTotalPrice(context, user);
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
        sortProductList();

        if(mMask!=MASK_PRODUCT_MIN_INFO && mMask!=MASK_PRODUCT_DETAILS
                && mMask!=MASK_PRODUCT_LARGE_DETAILS){
            return;
        }

        if (holder.containerLayout != null) {
            holder.containerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDataset.get(holder.getAdapterPosition())!=null) {
                        mContext.startActivity(new Intent(mContext, ProductDetailActivity.class)
                                .putExtra(ProductDetailActivity.KEY_PRODUCT, mDataset.get(holder.getAdapterPosition())));
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.no_product_details), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (BuildConfig.USE_PRODUCT_IMAGE) {
            if (mMask == MASK_PRODUCT_LARGE_DETAILS) {
                Utils.loadOriginalImageByFileName(mContext, mUser,
                        mDataset.get(position).getImageFileName(), holder.productImage);
            } else {
                Utils.loadThumbImageByFileName(mContext, mUser,
                        mDataset.get(position).getImageFileName(), holder.productImage);
            }
        } else {
            holder.productImage.setVisibility(View.GONE);
            if (holder.productDetailsInfoContainer!=null) {
                holder.productDetailsInfoContainer.setMinimumHeight(0);
            }
        }

        holder.productName.setText(mDataset.get(position).getName());

        if (mIsManagePriceInOrder) {
            //se toma solo uno de los dos precios, teniendo como prioridad el precio total
            if (mShowProductTotalPrice) {
                holder.productPrice.setText(mContext.getString(R.string.product_total_price_detail,
                        mDataset.get(position).getProductPriceAvailability().getCurrency().getName(),
                        mDataset.get(position).getProductPriceAvailability().getTotalPriceStringFormat()));
                holder.productPrice.setVisibility(View.VISIBLE);
            } else if (mShowProductPrice) {
                holder.productPrice.setText(mContext.getString(R.string.product_price_detail,
                        mDataset.get(position).getProductPriceAvailability().getCurrency().getName(),
                        mDataset.get(position).getProductPriceAvailability().getPriceStringFormat()));
                holder.productPrice.setVisibility(View.VISIBLE);
            } else {
                holder.productPrice.setVisibility(View.GONE);
            }
        } else {
            holder.productPrice.setVisibility(View.GONE);
        }

        holder.productAvailability.setText(mContext.getString(R.string.availability,
                mDataset.get(position).getProductPriceAvailability().getAvailability()));

        holder.shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.shareImageView.setEnabled(false);
                new CreateShareIntentThread(mFragmentActivity, mContext, mUser, mDataset.get(holder.getAdapterPosition()),
                        holder.shareImageView).start();
            }
        });

        if (BuildConfig.IS_SALES_FORCE_SYSTEM) {
            holder.favoriteImageView.setVisibility(View.GONE);
            if (holder.favoriteImageViewViewContainer!=null) {
                holder.favoriteImageViewViewContainer.setVisibility(View.GONE);
            }
        } else {
            holder.favoriteImageView.setImageResource(mDataset.get(holder.getAdapterPosition()).isFavorite()
                    ? R.drawable.ic_favorite_black_24dp : R.drawable.ic_favorite_border_black_24dp);
            holder.favoriteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDataset.get(holder.getAdapterPosition()).isFavorite()) {
                        String result = removeFromWishList(mDataset.get(holder.getAdapterPosition()).getId());
                        if (result == null) {
                            mDataset.get(holder.getAdapterPosition()).setFavorite(false);
                            holder.favoriteImageView.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        } else {
                            Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String result = addToWishList(mDataset.get(holder.getAdapterPosition()));
                        if (result == null) {
                            mDataset.get(holder.getAdapterPosition()).setFavorite(true);
                            holder.favoriteImageView.setImageResource(R.drawable.ic_favorite_black_24dp);
                        } else {
                            Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
        holder.addToShoppingCartImageView.setColorFilter(Utils.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        holder.addToShoppingCartImageView.setOnClickListener(new View.OnClickListener() {
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

        holder.addToShoppingSaleImageView.setColorFilter(Utils.getColor(mContext, R.color.golden), PorterDuff.Mode.SRC_ATOP);
        holder.addToShoppingSaleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BuildConfig.IS_SALES_FORCE_SYSTEM) {
                    try {
                        SalesOrderLine salesOrderLine = (new SalesOrderLineDB(mContext, mUser))
                                .getSalesOrderLineFromShoppingSales(mDataset.get(holder.getAdapterPosition()).getId(),
                                        Utils.getAppCurrentBusinessPartnerId(mContext, mUser));
                        if (salesOrderLine != null) {
                            updateQtyOrderedInShoppingSales(salesOrderLine);
                        } else {
                            addToShoppingSale(mDataset.get(holder.getAdapterPosition()));
                        }
                    } catch (Exception e) {
                        //do nothing
                    }
                } else {
                    addToShoppingSale(mDataset.get(holder.getAdapterPosition()));
                }
            }
        });

        if(mMask==MASK_PRODUCT_DETAILS || mMask==MASK_PRODUCT_LARGE_DETAILS){
            holder.productInternalCode.setText(mDataset.get(position).getInternalCodeMayoreoFormat());
            holder.productReference.setText(mDataset.get(position).getReference());

            if(mDataset.get(position).getProductBrand()!=null
                    && !TextUtils.isEmpty(mDataset.get(position).getProductBrand().getName())){
                holder.productBrand.setText(mContext.getString(R.string.brand_detail,
                    mDataset.get(position).getProductBrand().getName()));
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
                holder.productRatingBarContainer.setVisibility(View.VISIBLE);
            }else{
                holder.productRatingBarContainer.setVisibility(View.GONE);
            }

            if(holder.productDescription!=null){
                if(!TextUtils.isEmpty(mDataset.get(position).getDescription())){
                    holder.productDescription.setText(mContext.getString(R.string.product_description_detail,
                            mDataset.get(position).getDescription()));
                    holder.productDescription.setVisibility(View.VISIBLE);
                }else{
                    holder.productDescription.setVisibility(View.GONE);
                }
            }

            if(holder.productPurpose!=null){
                if(!TextUtils.isEmpty(mDataset.get(position).getPurpose())){
                    holder.productPurpose.setText(mContext.getString(R.string.product_purpose_detail,
                            mDataset.get(position).getPurpose()));
                    holder.productPurpose.setVisibility(View.VISIBLE);
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
        if (product!=null) {
            DialogAddToShoppingCart dialogAddToShoppingCart =
                    DialogAddToShoppingCart.newInstance(product, mUser);
            dialogAddToShoppingCart.show(mFragmentActivity.getSupportFragmentManager(),
                    DialogAddToShoppingCart.class.getSimpleName());
        } else {
            //TODO: mostrar mensaje de error
        }
    }

    private void updateQtyOrderedInShoppingCart(OrderLine orderLine) {
        DialogUpdateShoppingCartQtyOrdered dialogUpdateShoppingCartQtyOrdered =
                DialogUpdateShoppingCartQtyOrdered.newInstance(orderLine, true, mUser);
        dialogUpdateShoppingCartQtyOrdered.show(mFragmentActivity.getSupportFragmentManager(),
                DialogUpdateShoppingCartQtyOrdered.class.getSimpleName());
    }

    private void addToShoppingSale(Product product) {
        product = (new ProductDB(mContext, mUser)).getProductById(product.getId());
        if (product!=null) {
            if (BuildConfig.IS_SALES_FORCE_SYSTEM) {
                DialogAddToShoppingSale2 dialogAddToShoppingSale2 =
                        DialogAddToShoppingSale2.newInstance(product, mUser);
                dialogAddToShoppingSale2.show(mFragmentActivity.getSupportFragmentManager(),
                        DialogAddToShoppingSale2.class.getSimpleName());
            } else {
                DialogAddToShoppingSale dialogAddToShoppingSale =
                        DialogAddToShoppingSale.newInstance(product, mUser);
                dialogAddToShoppingSale.show(mFragmentActivity.getSupportFragmentManager(),
                        DialogAddToShoppingSale.class.getSimpleName());
            }
        } else {
            //TODO: mostrar mensaje de error
        }
    }

    private void updateQtyOrderedInShoppingSales(SalesOrderLine salesOrderLine) {
        Product product = (new ProductDB(mContext, mUser)).getProductById(salesOrderLine.getProductId());
        if (product!=null) {
            DialogUpdateShoppingSaleQtyOrdered dialogUpdateShoppingSaleQtyOrdered =
                    DialogUpdateShoppingSaleQtyOrdered.newInstance(product, salesOrderLine, mUser);
            dialogUpdateShoppingSaleQtyOrdered.show(mFragmentActivity.getSupportFragmentManager(),
                    DialogUpdateShoppingSaleQtyOrdered.class.getSimpleName());
        } else {
            //TODO: mostrar mensaje de error
        }
    }

    private String addToWishList(Product product) {
        return (new OrderLineDB(mContext, mUser)).addProductToWishList(product);
    }

    private String removeFromWishList(int productId) {
        return (new OrderLineDB(mContext, mUser)).removeProductFromWishList(productId);
    }

    private void sortProductList(){
        if (mSortOption!=DialogSortProductListOptions.SORT_BY_PRODUCT_NAME_ASC
                && mSortOption!=DialogSortProductListOptions.SORT_BY_PRODUCT_NAME_DESC
                && mSortOption!=DialogSortProductListOptions.SORT_BY_PRODUCT_INTERNAL_CODE_ASC
                && mSortOption!=DialogSortProductListOptions.SORT_BY_PRODUCT_INTERNAL_CODE_DESC
                && mSortOption!=DialogSortProductListOptions.SORT_BY_PRODUCT_AVAILABILITY_ASC
                && mSortOption!=DialogSortProductListOptions.SORT_BY_PRODUCT_AVAILABILITY_DESC){
            return;
        }
        Collections.sort(mDataset, new Comparator<Product>() {
            @Override
            public int compare(Product lhs, Product rhs) {
                try{
                    if (lhs!=null && rhs!=null) {
                        switch (mSortOption){
                            case DialogSortProductListOptions.SORT_BY_PRODUCT_NAME_ASC:
                                if (lhs.getName()!=null && rhs.getName()!=null) {
                                    return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
                                }
                            case DialogSortProductListOptions.SORT_BY_PRODUCT_NAME_DESC:
                                if (lhs.getName()!=null && rhs.getName()!=null) {
                                    return rhs.getName().toLowerCase().compareTo(lhs.getName().toLowerCase());
                                }
                            case DialogSortProductListOptions.SORT_BY_PRODUCT_INTERNAL_CODE_ASC:
                                if (lhs.getInternalCode()!=null && rhs.getInternalCode()!=null) {
                                    return lhs.getInternalCode().compareTo(rhs.getInternalCode());
                                }
                            case DialogSortProductListOptions.SORT_BY_PRODUCT_INTERNAL_CODE_DESC:
                                if (lhs.getInternalCode()!=null && rhs.getInternalCode()!=null) {
                                    return rhs.getInternalCode().compareTo(lhs.getInternalCode());
                                }
                            case DialogSortProductListOptions.SORT_BY_PRODUCT_AVAILABILITY_ASC:
                                if (lhs.getProductPriceAvailability()!=null
                                        && rhs.getProductPriceAvailability()!=null) {
                                    return Integer.valueOf(lhs.getProductPriceAvailability().getAvailability())
                                            .compareTo(rhs.getProductPriceAvailability().getAvailability());
                                }
                            case DialogSortProductListOptions.SORT_BY_PRODUCT_AVAILABILITY_DESC:
                                if (lhs.getProductPriceAvailability()!=null
                                        && rhs.getProductPriceAvailability()!=null) {
                                    return Integer.valueOf(rhs.getProductPriceAvailability().getAvailability())
                                            .compareTo(lhs.getProductPriceAvailability().getAvailability());
                                }
                            default:
                                return 0;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    public void filter(String charText) {
        try {
            charText = Normalizer.normalize(charText.toLowerCase(Locale.getDefault()),Normalizer.Form.NFD).replaceAll("[^a-zA-Z0-9 ]","");
            if (TextUtils.isEmpty(charText)) {
                mDataset.clear();
                mDataset.addAll(filterAux);
                return;
            }
            ArrayList<Product> currentData = new ArrayList<>();
            //si a la cadena de busqueda lo que se hace es agregar caracteres entonces se usa la actual
            //lista de productos para filtrar, en caso contrario se toma la lista de productos originalmente cargada.
            //esto es para que a medida que se vayan agregando caracteres la busqueda sea mas rapida
            if (mCurrentFilterText!=null && charText.length()>mCurrentFilterText.length()) {
                currentData.addAll(mDataset);
            }

            mCurrentFilterText = charText;
            mDataset.clear();
            for (Product product : currentData.isEmpty() ? filterAux : currentData) {
                try {
                    String normalized;
                    if (!TextUtils.isEmpty(product.getName())) {
                        normalized = Normalizer.normalize(product.getName().toLowerCase(Locale.getDefault()),Normalizer.Form.NFD).replaceAll("[^a-zA-Z0-9 ]","");
                        try {
                            for (String aux : (charText+" ").split("\\s+")) {
                                if (!normalized.contains(aux)) {
                                    throw new Exception("No se encontro la palabra");
                                }
                            }
                            mDataset.add(product);
                            //si se agrega el producto entonces no se sigue probando las otras opciones
                            continue;
                        } catch (Exception e) {
                            //do nothing
                        }
                    }

                    if (!patternIsNotNumeric.matcher(charText).matches() && !TextUtils.isEmpty(product.getInternalCode()) &&
                            product.getInternalCode().toLowerCase(Locale.getDefault()).startsWith(charText)) {
                        mDataset.add(product);
                        continue;
                    }

                    if (!TextUtils.isEmpty(product.getPurpose()) &&
                            Normalizer.normalize(product.getPurpose().toLowerCase(Locale.getDefault()), Normalizer.Form.NFD).replaceAll("[^a-zA-Z0-9 ]","").contains(charText)) {
                        mDataset.add(product);
                        continue;
                    }

                    if (!TextUtils.isEmpty(product.getReference()) &&
                            Normalizer.normalize(product.getReference().toLowerCase(Locale.getDefault()), Normalizer.Form.NFD).replaceAll("[^a-zA-Z0-9 ]","").contains(charText)) {
                        mDataset.add(product);
                        continue;
                    }

                    if (product.getProductBrand() != null &&
                            !TextUtils.isEmpty(product.getProductBrand().getName()) &&
                            Normalizer.normalize(product.getProductBrand().getName().toLowerCase(Locale.getDefault()), Normalizer.Form.NFD).replaceAll("[^a-zA-Z0-9 ]","").contains(charText)) {
                        mDataset.add(product);
                        continue;
                    }

                    if (!TextUtils.isEmpty(product.getDescription()) &&
                            Normalizer.normalize(product.getDescription().toLowerCase(Locale.getDefault()), Normalizer.Form.NFD).replaceAll("[^a-zA-Z0-9 ]","").contains(charText)) {
                        mDataset.add(product);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } finally {
            notifyDataSetChanged();
        }
    }
}
