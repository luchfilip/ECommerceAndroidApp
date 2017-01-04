package com.smartbuilders.smartsales.ecommerce.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.smartbuilders.smartsales.ecommerce.BuildConfig;
import com.smartbuilders.smartsales.ecommerce.DialogAddToShoppingCart;
import com.smartbuilders.smartsales.ecommerce.DialogAddToShoppingSale;
import com.smartbuilders.smartsales.ecommerce.DialogSortProductListOptions;
import com.smartbuilders.smartsales.ecommerce.DialogUpdateShoppingCartQtyOrdered;
import com.smartbuilders.smartsales.ecommerce.ProductDetailActivity;
import com.smartbuilders.smartsales.ecommerce.ProductsListActivity;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerce.data.ProductDB;
import com.smartbuilders.smartsales.ecommerce.data.SalesOrderLineDB;
import com.smartbuilders.smartsales.ecommerce.model.Banner;
import com.smartbuilders.smartsales.ecommerce.model.BannerSection;
import com.smartbuilders.smartsales.ecommerce.model.MainPageProductSection;
import com.smartbuilders.smartsales.ecommerce.model.MainPageTitleSection;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.ecommerce.model.ProductBrandPromotionalSection;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.utils.CreateShareIntentThread;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.smartsales.ecommerce.utils.ViewIdGenerator;
import com.smartbuilders.smartsales.salesforcesystem.DialogAddToShoppingSale2;
import com.smartbuilders.smartsales.salesforcesystem.DialogUpdateShoppingSaleQtyOrdered;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.model.UserProfile;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Alberto on 25/3/2016.
 */
public class MainActivityLayoutAdapter extends RecyclerView.Adapter<MainActivityLayoutAdapter.ViewHolder> {

    private static final int VIEW_TYPE_EMPTY_LAYOUT     = -1;
    private static final int VIEW_TYPE_VIEW_FLIPPER     = 0;
    private static final int VIEW_TYPE_RECYCLER_VIEW    = 1;
    private static final int VIEW_TYPE_VIEWPAGER        = 2;
    private static final int VIEW_TYPE_STRING           = 3;
    private static final int VIEW_TYPE_TITLE            = 4;
    private static final int VIEW_TYPE_PRODUCT          = 5;
    private static final int VIEW_TYPE_INTENT_SEE_ALL   = 6;

    private Context mContext;
    private FragmentActivity mFragmentActivity;
    private ArrayList<Object> mDataset;
    private User mUser;
    private DisplayMetrics metrics;
    private String mUrlScreenParameters;
    private boolean mIsLandscape;
    private boolean mIsManagePriceInOrder;
    private boolean mShowProductPrice;
    private boolean mShowProductTotalPrice;
    private Typeface mTypefaceMedium;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryName;
        public RecyclerView mRecyclerView;
        public ViewFlipper mViewFlipper;
        public ViewPager mViewPager;
        public TextView mTextView;
        public TextView mTitleTextView;
        public View mSeeAllProductsButton;

        /*********************************************/
        public TextView productName;
        public ImageView productImage;
        public View productPriceContainer;
        public TextView productPriceCurrencyName;
        public TextView productPrice;
        public TextView productAvailability;
        public ImageView shareImageView;
        public ImageView favoriteImageView;
        public ImageView addToShoppingCartImage;
        public ImageView addToShoppingSaleImage;
        public View containerLayout;
        /*********************************************/

        public ViewHolder(View v) {
            super(v);
            categoryName = (TextView) v.findViewById(R.id.category_name);
            mRecyclerView = (RecyclerView) v.findViewById(R.id.product_list);
            mViewFlipper = (ViewFlipper) v.findViewById(R.id.banner_flipper);
            mViewPager = (ViewPager) v.findViewById(R.id.view_pager);
            mTextView = (TextView) v.findViewById(R.id.textView);
            mTitleTextView = (TextView) v.findViewById(R.id.title_textView);
            mSeeAllProductsButton = v.findViewById(R.id.see_all_products_button);
            /*********************************************/
            productName = (TextView) v.findViewById(R.id.product_name);
            productImage = (ImageView) v.findViewById(R.id.product_image);
            productPriceContainer = v.findViewById(R.id.product_price_container);
            productPriceCurrencyName = (TextView) v.findViewById(R.id.product_price_currency_name);
            productPrice = (TextView) v.findViewById(R.id.product_price);
            productAvailability = (TextView) v.findViewById(R.id.product_availability);
            shareImageView = (ImageView) v.findViewById(R.id.share_imageView);
            favoriteImageView = (ImageView) v.findViewById(R.id.favorite_imageView);
            addToShoppingCartImage = (ImageView) v.findViewById(R.id.addToShoppingCart_imageView);
            addToShoppingSaleImage = (ImageView) v.findViewById(R.id.addToShoppingSale_imageView);
            containerLayout = v.findViewById(R.id.container_layout);
            /***************************************************/
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataset.get(position) instanceof BannerSection) {
            return VIEW_TYPE_VIEW_FLIPPER;
        } else if (mDataset.get(position) instanceof MainPageProductSection) {
            return VIEW_TYPE_RECYCLER_VIEW;
        } else if (mDataset.get(position) instanceof ProductBrandPromotionalSection) {
            return VIEW_TYPE_VIEWPAGER;
        } else if (mDataset.get(position) instanceof String) {
            return VIEW_TYPE_STRING;
        } else if (mDataset.get(position) instanceof MainPageTitleSection) {
            return VIEW_TYPE_TITLE;
        } else if (mDataset.get(position) instanceof Product) {
            return VIEW_TYPE_PRODUCT;
        } else if (mDataset.get(position) instanceof Intent) {
            return VIEW_TYPE_INTENT_SEE_ALL;
        }
        return VIEW_TYPE_EMPTY_LAYOUT;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MainActivityLayoutAdapter(Context context, FragmentActivity fragmentActivity,
                                     ArrayList<Object> myDataset, User user) {
        mContext = context;
        mFragmentActivity = fragmentActivity;
        mDataset = myDataset;
        mUser = user;
        metrics = new DisplayMetrics();
        fragmentActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        mIsLandscape = mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        mUrlScreenParameters = Utils.getUrlScreenParameters(mIsLandscape, context);
        mIsManagePriceInOrder = Parameter.isManagePriceInOrder(context, user);
        mShowProductPrice = Parameter.showProductPrice(context, user);
        mShowProductTotalPrice = Parameter.showProductTotalPrice(context, user);
        mTypefaceMedium = Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Medium.ttf");
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MainActivityLayoutAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view;
        switch (viewType) {
            case VIEW_TYPE_VIEW_FLIPPER:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.banner, parent, false);
                break;
            case VIEW_TYPE_RECYCLER_VIEW:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.category_product_list, parent, false);
                break;
            case VIEW_TYPE_VIEWPAGER:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.view_pager_layout, parent, false);
                break;
            case VIEW_TYPE_STRING:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.welcome_user_layout, parent, false);
                break;
            case VIEW_TYPE_TITLE:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.main_page_title_section_layout, parent, false);
                break;
            case VIEW_TYPE_PRODUCT:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.product_min_info_dynamic_height, parent, false);
                break;
            case VIEW_TYPE_INTENT_SEE_ALL:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.main_page_section_see_all_button, parent, false);
                break;
            case VIEW_TYPE_EMPTY_LAYOUT:
            default:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.empty_layout, parent, false);
        }
        return new MainActivityLayoutAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        StaggeredGridLayoutManager.LayoutParams layoutParams =
                (StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
        layoutParams.setFullSpan(getItemViewType(position)!=VIEW_TYPE_PRODUCT);

        switch (getItemViewType(position)) {
            case VIEW_TYPE_EMPTY_LAYOUT:
                break;
            case VIEW_TYPE_VIEW_FLIPPER:
                if (mDataset!=null && mDataset.get(position)!=null
                        && mDataset.get(position) instanceof BannerSection
                        &&  ((BannerSection) mDataset.get(position)).getBanners()!=null) {
                    for (Banner banner : ((BannerSection) mDataset.get(position)).getBanners()) {
                        setFlipperImage(viewHolder.mViewFlipper, banner);
                    }
                    int height;
                    if(metrics.widthPixels < metrics.heightPixels){
                        height = (metrics.heightPixels / 4);
                    } else {
                        height = (metrics.widthPixels / 5);
                    }
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(metrics.widthPixels, height);
                    viewHolder.mViewFlipper.setLayoutParams(lp);
                    /** Start Flipping */
                    viewHolder.mViewFlipper.startFlipping();
                }
                break;
            case VIEW_TYPE_RECYCLER_VIEW:
                final MainPageProductSection mainPageProductSection = (MainPageProductSection) mDataset.get(position);

                if(mainPageProductSection !=null && mainPageProductSection.getProducts()!=null
                        && !mainPageProductSection.getProducts().isEmpty()){
                    // - get element from your dataset at this position
                    // - replace the contents of the view with that element
                    viewHolder.categoryName.setText(mainPageProductSection.getName());

                    // use this setting to improve performance if you know that changes
                    // in content do not change the layout size of the RecyclerView
                    viewHolder.mRecyclerView.setHasFixedSize(true);

                    viewHolder.mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext,
                            LinearLayoutManager.HORIZONTAL, false));

                    viewHolder.mRecyclerView.setAdapter(new ProductsListAdapter(mContext, mFragmentActivity,
                            mainPageProductSection.getProducts(), ProductsListAdapter.MASK_PRODUCT_MIN_INFO,
                            DialogSortProductListOptions.SORT_BY_NO_SORT, mUser));

                    if (mainPageProductSection.getSeeAllIntent()!=null) {
                        viewHolder.mSeeAllProductsButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mContext.startActivity(mainPageProductSection.getSeeAllIntent());
                            }
                        });
                        viewHolder.mSeeAllProductsButton.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.mSeeAllProductsButton.setVisibility(View.GONE);
                    }
                }
                break;
            case VIEW_TYPE_VIEWPAGER:
                ProductBrandPromotionalSection productBrandPromotionalSection =
                        (ProductBrandPromotionalSection) mDataset.get(position);
                if(productBrandPromotionalSection !=null
                        && productBrandPromotionalSection.getProductBrandPromotionalCards()!=null
                        && !productBrandPromotionalSection.getProductBrandPromotionalCards().isEmpty()) {
                    viewHolder.mViewPager.setId(ViewIdGenerator.generateViewId());
                    viewHolder.mViewPager.setClipToPadding(false);
                    int height;
                    if(metrics.widthPixels < metrics.heightPixels){
                        height = (metrics.heightPixels / 4);
                    } else {
                        height = (metrics.widthPixels / 5);
                    }
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(metrics.widthPixels, height);
                    viewHolder.mViewPager.setLayoutParams(lp);

                    ProductBrandPromotionalAdapter productBrandPromotionalAdapter =
                            new ProductBrandPromotionalAdapter(mFragmentActivity.getSupportFragmentManager(), metrics);
                    productBrandPromotionalAdapter.setData(productBrandPromotionalSection.getProductBrandPromotionalCards());
                    viewHolder.mViewPager.setAdapter(productBrandPromotionalAdapter);
                }
                break;
            case VIEW_TYPE_STRING:
                if (mDataset!=null && mDataset.get(position) instanceof String && viewHolder.mTextView!=null){
                    viewHolder.mTextView.setText(mContext
                            .getString(R.string.welcome_user_detail, ((String) mDataset.get(position))));
                }
                break;
            case VIEW_TYPE_TITLE:
                if (mDataset!=null && mDataset.get(position) instanceof MainPageTitleSection && viewHolder.mTitleTextView!=null){
                    viewHolder.mTitleTextView.setText(((MainPageTitleSection) mDataset.get(position)).getTitle());
                }
                break;
            case VIEW_TYPE_PRODUCT:
                if (mDataset!=null && mDataset.get(position) instanceof Product){
                    if (viewHolder.containerLayout != null) {
                        viewHolder.containerLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mContext.startActivity(new Intent(mContext, ProductDetailActivity.class)
                                        .putExtra(ProductDetailActivity.KEY_PRODUCT, (Product) mDataset.get(viewHolder.getAdapterPosition())));
                            }
                        });
                    }
                    if (BuildConfig.USE_PRODUCT_IMAGE) {
                        Utils.loadThumbImageByFileName(mContext, mUser,
                                ((Product) mDataset.get(position)).getImageFileName(), viewHolder.productImage);
                    } else {
                        viewHolder.productImage.setVisibility(View.GONE);
                    }

                    viewHolder.productName.setText(((Product) mDataset.get(position)).getName());
                    viewHolder.productName.setTypeface(mTypefaceMedium);

                    viewHolder.shareImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewHolder.shareImageView.setEnabled(false);
                            new CreateShareIntentThread(mFragmentActivity, mContext, mUser,
                                    ((Product) mDataset.get(viewHolder.getAdapterPosition())), viewHolder.shareImageView).start();
                        }
                    });

                    if (mIsManagePriceInOrder) {
                        if (((Product) mDataset.get(viewHolder.getAdapterPosition())).getProductPriceAvailability().getAvailability() > 0
                                && ((Product) mDataset.get(viewHolder.getAdapterPosition())).getProductPriceAvailability().getPrice() > 0) {
                            //se toma solo uno de los dos precios, teniendo como prioridad el precio total
                            if (mShowProductTotalPrice) {
                                viewHolder.productPriceCurrencyName.setText(((Product) mDataset.get(viewHolder.getAdapterPosition()))
                                        .getProductPriceAvailability().getCurrency().getName());
                                viewHolder.productPrice.setText(((Product) mDataset.get(viewHolder.getAdapterPosition()))
                                        .getProductPriceAvailability().getTotalPriceStringFormat());
                                viewHolder.productPriceContainer.setVisibility(View.VISIBLE);
                            } else if (mShowProductPrice) {
                                viewHolder.productPriceCurrencyName.setText(((Product) mDataset.get(viewHolder.getAdapterPosition()))
                                        .getProductPriceAvailability().getCurrency().getName());
                                viewHolder.productPrice.setText(((Product) mDataset.get(viewHolder.getAdapterPosition()))
                                        .getProductPriceAvailability().getPriceStringFormat());
                                viewHolder.productPriceContainer.setVisibility(View.VISIBLE);
                            } else {
                                viewHolder.productPriceContainer.setVisibility(View.GONE);
                            }
                        } else {
                            viewHolder.productPriceContainer.setVisibility(View.GONE);
                        }
                    }

                    if (!mIsManagePriceInOrder) {
                        viewHolder.productAvailability.setText(mContext.getString(R.string.availability,
                                ((Product) mDataset.get(viewHolder.getAdapterPosition())).getProductPriceAvailability().getAvailability()));
                        viewHolder.productAvailability.setVisibility(View.VISIBLE);
                    }

                    if (BuildConfig.IS_SALES_FORCE_SYSTEM) {
                        viewHolder.favoriteImageView.setVisibility(View.GONE);
                    } else {
                        viewHolder.favoriteImageView.setImageResource(((Product) mDataset.get(position)).isFavorite()
                                ? R.drawable.ic_favorite_black_24dp : R.drawable.ic_favorite_border_black_24dp);
                        viewHolder.favoriteImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (((Product) mDataset.get(viewHolder.getAdapterPosition())).isFavorite()) {
                                    String result = removeFromWishList(((Product) mDataset.get(viewHolder.getAdapterPosition())).getId());
                                    if (result == null) {
                                        ((Product) mDataset.get(viewHolder.getAdapterPosition())).setFavorite(false);
                                        viewHolder.favoriteImageView.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                                    } else {
                                        Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    String result = addToWishList(((Product) mDataset.get(viewHolder.getAdapterPosition())));
                                    if (result == null) {
                                        ((Product) mDataset.get(viewHolder.getAdapterPosition())).setFavorite(true);
                                        viewHolder.favoriteImageView.setImageResource(R.drawable.ic_favorite_black_24dp);
                                    } else {
                                        Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }

                    viewHolder.addToShoppingCartImage.setColorFilter(Utils.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                    viewHolder.addToShoppingCartImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            OrderLine orderLine = (new OrderLineDB(mContext, mUser))
                                    .getOrderLineFromShoppingCartByProductId(((Product) mDataset.get(viewHolder.getAdapterPosition())).getId());
                            if(orderLine!=null){
                                updateQtyOrderedInShoppingCart(orderLine);
                            }else{
                                addToShoppingCart(((Product) mDataset.get(viewHolder.getAdapterPosition())));
                            }
                        }
                    });

                    viewHolder.addToShoppingSaleImage.setColorFilter(Utils.getColor(mContext, R.color.golden), PorterDuff.Mode.SRC_ATOP);
                    viewHolder.addToShoppingSaleImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (BuildConfig.IS_SALES_FORCE_SYSTEM
                                    || (mUser.getUserProfileId()== UserProfile.SALES_MAN_PROFILE_ID && mIsManagePriceInOrder)) {
                                try {
                                    SalesOrderLine salesOrderLine = (new SalesOrderLineDB(mContext, mUser))
                                            .getSalesOrderLineFromShoppingSales(((Product) mDataset.get(viewHolder.getAdapterPosition())).getId(),
                                                    Utils.getAppCurrentBusinessPartnerId(mContext, mUser));
                                    if (salesOrderLine != null) {
                                        updateQtyOrderedInShoppingSales(salesOrderLine);
                                    } else {
                                        addToShoppingSale(((Product) mDataset.get(viewHolder.getAdapterPosition())), mIsManagePriceInOrder);
                                    }
                                } catch (Exception e) {
                                    //do nothing
                                }
                            } else {
                                addToShoppingSale(((Product) mDataset.get(viewHolder.getAdapterPosition())), mIsManagePriceInOrder);
                            }
                        }
                    });
                }
                break;
            case VIEW_TYPE_INTENT_SEE_ALL:
                if (mDataset!=null && mDataset.get(position) instanceof Intent){
                    viewHolder.mSeeAllProductsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mContext.startActivity((Intent) mDataset.get(viewHolder.getAdapterPosition()));
                        }
                    });
                }
                break;
        }
    }

    private void setFlipperImage(ViewFlipper viewFlipper, final Banner banner) {
        final ImageView image = new ImageView(mContext);
        File img = Utils.getFileInBannerDirByFileName(mContext, mIsLandscape, banner.getImageFileName());

        if(img!=null){
            Picasso.with(mContext).load(img).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(image);
            Picasso.with(mContext).invalidate(img);
        }else{
            Picasso.with(mContext)
                    .load(mUser.getServerAddress() + "/IntelligentDataSynchronizer/GetBannerImage?fileName=" +
                            banner.getImageFileName() + mUrlScreenParameters)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(image, new Callback() {
                        @Override
                        public void onSuccess() {
                            Utils.createFileInBannerDir(mContext, mIsLandscape, banner.getImageFileName(),
                                    ((BitmapDrawable)(image).getDrawable()).getBitmap());
                            Picasso.with(mContext)
                                    .invalidate(mUser.getServerAddress() + "/IntelligentDataSynchronizer/GetBannerImage?fileName=" +
                                            banner.getImageFileName() + mUrlScreenParameters);
                        }

                        @Override
                        public void onError() { }
                    });
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(banner.getProductId()>0){
                    Product product = (new ProductDB(mContext, mUser)).getProductById(banner.getProductId());
                    if (product!=null) {
                        mContext.startActivity(new Intent(mContext, ProductDetailActivity.class)
                                .putExtra(ProductDetailActivity.KEY_PRODUCT, product));
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.no_product_details), Toast.LENGTH_SHORT).show();
                    }
                } else if (banner.getProductBrandId()>0) {
                    mContext.startActivity(new Intent(mContext, ProductsListActivity.class)
                            .putExtra(ProductsListActivity.KEY_PRODUCT_BRAND_ID, banner.getProductBrandId()));
                } else if (banner.getProductSubCategoryId()>0) {
                    mContext.startActivity(new Intent(mContext, ProductsListActivity.class)
                            .putExtra(ProductsListActivity.KEY_PRODUCT_SUBCATEGORY_ID, banner.getProductSubCategoryId()));
                } else if (banner.getProductCategoryId()>0) {
                    mContext.startActivity(new Intent(mContext, ProductsListActivity.class)
                            .putExtra(ProductsListActivity.KEY_PRODUCT_CATEGORY_ID, banner.getProductCategoryId()));
                }
            }
        });

        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setAdjustViewBounds(true);
        viewFlipper.addView(image);
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

    private void addToShoppingSale(Product product, boolean managePriceInOrder) {
        product = (new ProductDB(mContext, mUser)).getProductById(product.getId());
        if (product!=null) {
            if (BuildConfig.IS_SALES_FORCE_SYSTEM
                    || (mUser.getUserProfileId()== UserProfile.SALES_MAN_PROFILE_ID && managePriceInOrder)) {
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

    public void setData(ArrayList<Object> data){
        mDataset = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mDataset!=null) {
            return mDataset.size();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return -1;
    }

}
