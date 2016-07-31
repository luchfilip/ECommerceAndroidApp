package com.smartbuilders.smartsales.ecommerce.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.DialogAddToShoppingCart;
import com.smartbuilders.smartsales.ecommerce.DialogAddToShoppingSale;
import com.smartbuilders.smartsales.ecommerce.DialogSortProductListOptions;
import com.smartbuilders.smartsales.ecommerce.DialogUpdateShoppingCartQtyOrdered;
import com.smartbuilders.smartsales.ecommerce.ProductDetailActivity;
import com.smartbuilders.smartsales.ecommerce.ProductsListActivity;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerce.data.ProductDB;
import com.smartbuilders.smartsales.ecommerce.model.Banner;
import com.smartbuilders.smartsales.ecommerce.model.BannerSection;
import com.smartbuilders.smartsales.ecommerce.model.MainPageProductSection;
import com.smartbuilders.smartsales.ecommerce.model.MainPageTitleSection;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.ecommerce.model.ProductBrandPromotionalSection;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.smartsales.ecommerce.utils.ViewIdGenerator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Alberto on 25/3/2016.
 */
public class MainActivityAdapter extends BaseAdapter {

    private static final int VIEW_TYPE_EMPTY_LAYOUT     = -1;
    private static final int VIEW_TYPE_VIEW_FLIPPER     = 0;
    private static final int VIEW_TYPE_RECYCLER_VIEW    = 1;
    private static final int VIEW_TYPE_VIEWPAGER        = 2;
    private static final int VIEW_TYPE_STRING           = 3;
    private static final int VIEW_TYPE_TITLE            = 4;
    private static final int VIEW_TYPE_PRODUCT          = 5;

    private Context mContext;
    private FragmentActivity mFragmentActivity;
    private ArrayList<Object> mDataset;
    private User mUser;
    private DisplayMetrics metrics;
    private String mUrlScreenParameters;
    private boolean mIsLandscape;
    private boolean mIsManagePriceInOrder;

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
        }
        return VIEW_TYPE_EMPTY_LAYOUT;
    }

    public static class ViewHolder {
        public TextView categoryName;
        public RecyclerView mRecyclerView;
        public ViewFlipper mViewFlipper;
        public ViewPager mViewPager;
        public TextView mTextView;
        public TextView mTitleTextView;

        /*********************************************/
        public TextView productName;
        public ImageView productImage;
        public TextView productPrice;
        public TextView productAvailability;
        public View goToProductDetails;
        public ImageView shareImageView;
        public ImageView favoriteImageView;
        public ImageView addToShoppingCartImage;
        public ImageView addToShoppingSaleImage;
        /*********************************************/

        public ViewHolder(View v) {
            categoryName = (TextView) v.findViewById(R.id.category_name);
            mRecyclerView = (RecyclerView) v.findViewById(R.id.product_list);
            mViewFlipper = (ViewFlipper) v.findViewById(R.id.banner_flipper);
            mViewPager = (ViewPager) v.findViewById(R.id.view_pager);
            mTextView = (TextView) v.findViewById(R.id.textView);
            mTitleTextView = (TextView) v.findViewById(R.id.title_textView);
            /*********************************************/
            productName = (TextView) v.findViewById(R.id.product_name);
            productImage = (ImageView) v.findViewById(R.id.product_image);
            goToProductDetails = v.findViewById(R.id.go_to_product_details);
            productPrice = (TextView) v.findViewById(R.id.product_price);
            productAvailability = (TextView) v.findViewById(R.id.product_availability);
            shareImageView = (ImageView) v.findViewById(R.id.share_imageView);
            favoriteImageView = (ImageView) v.findViewById(R.id.favorite_imageView);
            addToShoppingCartImage = (ImageView) v.findViewById(R.id.addToShoppingCart_imageView);
            addToShoppingSaleImage = (ImageView) v.findViewById(R.id.addToShoppingSale_imageView);
            /***************************************************/
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MainActivityAdapter(Context context, FragmentActivity fragmentActivity, ArrayList<Object> myDataset,
                               User user) {
        mContext = context;
        mFragmentActivity = fragmentActivity;
        mDataset = myDataset;
        mUser = user;
        metrics = new DisplayMetrics();
        fragmentActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        mIsLandscape = mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        mUrlScreenParameters = Utils.getUrlScreenParameters(mIsLandscape, context);
        mIsManagePriceInOrder = Parameter.isManagePriceInOrder(context, mUser);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // create a new view
        View view;
        switch (getItemViewType(position)) {
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
                        .inflate(R.layout.product_main_activity, parent, false);
                break;
            case VIEW_TYPE_EMPTY_LAYOUT:
            default:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.empty_layout, parent, false);
        }

        if (view != null) {
            final ViewHolder viewHolder = new ViewHolder(view);
            switch (getItemViewType(position)) {
                case VIEW_TYPE_EMPTY_LAYOUT:
                    return view;
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
                    MainPageProductSection mainPageProductSection = (MainPageProductSection) mDataset.get(position);

                    if(mainPageProductSection !=null && mainPageProductSection.getProducts()!=null
                            && !mainPageProductSection.getProducts().isEmpty()){
                        // - get element from your dataset at this position
                        // - replace the contents of the view with that element
                        viewHolder.categoryName.setText(mainPageProductSection.getName());

                        // use this setting to improve performance if you know that changes
                        // in content do not change the layout size of the RecyclerView
                        viewHolder.mRecyclerView.setHasFixedSize(true);
                        //int spanCount = 2;
                        //try {
                        //    int measuredWidth;
                        //    WindowManager w = mFragmentActivity.getWindowManager();
                        //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                        //        Point size = new Point();
                        //        w.getDefaultDisplay().getSize(size);
                        //        measuredWidth = size.x;
                        //    } else {
                        //        measuredWidth = w.getDefaultDisplay().getWidth();
                        //    }
                        //    spanCount = (int) (measuredWidth / mFragmentActivity.getResources().getDimension(R.dimen.productMinInfo_cardView_Width));
                        //} catch (Exception e){
                        //    e.printStackTrace();
                        //}

                        viewHolder.mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext,
                                LinearLayoutManager.HORIZONTAL, false));
                        //viewHolder.mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, spanCount));

                        viewHolder.mRecyclerView.setAdapter(new ProductsListAdapter(mContext, mFragmentActivity,
                                mainPageProductSection.getProducts(), ProductsListAdapter.MASK_PRODUCT_MIN_INFO,
                                DialogSortProductListOptions.SORT_BY_PRODUCT_NAME_ASC, mUser));
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
                            height = (metrics.heightPixels / 5);
                        } else {
                            height = (metrics.widthPixels / 6);
                        }
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(metrics.widthPixels, height);
                        viewHolder.mViewPager.setLayoutParams(lp);
                        viewHolder.mViewPager.setPageMargin(12);
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
                        final Product product = (Product) mDataset.get(position);
                        Utils.loadThumbImageByFileName(mContext, mUser,
                                product.getImageFileName(), viewHolder.productImage);

                        viewHolder.productImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                goToProductDetails(product.getId());
                            }
                        });

                        viewHolder.productName.setText(product.getName());

                        if (mIsManagePriceInOrder) {
                            viewHolder.productPrice.setText(mContext.getString(R.string.price_detail,
                                    product.getDefaultProductPriceAvailability().getCurrency().getName(),
                                    product.getDefaultProductPriceAvailability().getPrice()));
                            viewHolder.productPrice.setVisibility(View.VISIBLE);
                        } else {
                            viewHolder.productPrice.setVisibility(View.GONE);
                        }

                        viewHolder.productAvailability.setText(mContext.getString(R.string.availability,
                                product.getDefaultProductPriceAvailability().getAvailability()));

                        viewHolder.shareImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewHolder.shareImageView.setEnabled(false);
                                new CreateShareIntentThread(mFragmentActivity, product, viewHolder.shareImageView).start();
                            }
                        });

                        if(product.isFavorite()){
                            viewHolder.favoriteImageView.setImageResource(R.drawable.ic_favorite_black_24dp);
                            viewHolder.favoriteImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String result = removeFromWishList(product.getId());
                                    if (result == null) {
                                        ((Product) mDataset.get(position)).setFavorite(false);
                                        notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }else{
                            viewHolder.favoriteImageView.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            viewHolder.favoriteImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String result = addToWishList(product);
                                    if (result == null) {
                                        ((Product) mDataset.get(position)).setFavorite(true);
                                        notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }

                        viewHolder.addToShoppingCartImage.setColorFilter(Utils.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        viewHolder.addToShoppingCartImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                OrderLine orderLine = (new OrderLineDB(mContext, mUser))
                                        .getOrderLineFromShoppingCartByProductId(product.getId());
                                if(orderLine!=null){
                                    updateQtyOrderedInShoppingCart(orderLine);
                                }else{
                                    addToShoppingCart(product);
                                }
                            }
                        });

                        viewHolder.addToShoppingSaleImage.setColorFilter(Utils.getColor(mContext, R.color.golden), PorterDuff.Mode.SRC_ATOP);
                        viewHolder.addToShoppingSaleImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addToShoppingSale(product);
                            }
                        });

                        viewHolder.goToProductDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                goToProductDetails(product.getId());
                            }
                        });
                    }
                    break;
            }
            view.setTag(viewHolder);
        }
        return view;
    }

    private void setFlipperImage(ViewFlipper viewFlipper, final Banner banner) {
        final ImageView image = new ImageView(mContext);
        File img = Utils.getFileInBannerDirByFileName(mContext, mIsLandscape, banner.getImageFileName());

        if(img!=null){
            Picasso.with(mContext).load(img).into(image);
        }else{
            Picasso.with(mContext)
                    .load(mUser.getServerAddress() + "/IntelligentDataSynchronizer/GetBannerImage?fileName=" +
                            banner.getImageFileName() + mUrlScreenParameters)
                    .into(image, new Callback() {
                        @Override
                        public void onSuccess() {
                            Utils.createFileInBannerDir(mContext, mIsLandscape, banner.getImageFileName(),
                                    ((BitmapDrawable)(image).getDrawable()).getBitmap());
                        }

                        @Override
                        public void onError() { }
                    });
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(banner.getProductId()>0){
                    mContext.startActivity(new Intent(mContext, ProductDetailActivity.class)
                            .putExtra(ProductDetailActivity.KEY_PRODUCT_ID, banner.getProductId()));
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

    private String addToWishList(Product product) {
        return (new OrderLineDB(mContext, mUser)).addProductToWishList(product);
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

    public void setData(ArrayList<Object> data){
        mDataset = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mDataset!=null) {
            return mDataset.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public long getItemId(int position) {
        return -1;
    }

}
