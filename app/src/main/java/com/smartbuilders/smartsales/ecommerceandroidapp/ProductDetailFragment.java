package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.ProductsListAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ProductDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.CallbackPicassoDownloadImage;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProductDetailFragment extends Fragment {

    private static final String STATE_PRODUCT_ID = "STATE_PRODUCT_ID";

    private int mProductId;
    private Product mProduct;
    private ShareActionProvider mShareActionProvider;
    private Intent mShareIntent;
    private User mCurrentUser;

    public ProductDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        final ArrayList<Product> relatedProductsByShopping = new ArrayList<>();
        final ArrayList<Product> relatedProductsByBrandId = new ArrayList<>();
        final ArrayList<Product> relatedProductsBySubCategoryId = new ArrayList<>();

        new Thread() {
            @Override
            public void run() {
                try {
                    if(getActivity()!=null && getActivity().getIntent()!=null
                            && getActivity().getIntent().getExtras()!=null) {
                        if(getActivity().getIntent().getExtras().containsKey(ProductDetailActivity.KEY_PRODUCT_ID)){
                            mProductId = getActivity().getIntent().getExtras().getInt(ProductDetailActivity.KEY_PRODUCT_ID);
                        }
                    }

                    if(savedInstanceState!=null){
                        if(savedInstanceState.containsKey(STATE_PRODUCT_ID)){
                            mProductId = savedInstanceState.getInt(STATE_PRODUCT_ID);
                        }
                    }

                    mCurrentUser = Utils.getCurrentUser(getContext());
                    ProductDB productDB = new ProductDB(getContext(), mCurrentUser);

                    try {
                        mProduct = productDB.getProductById(mProductId, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        relatedProductsByShopping.addAll(productDB.getRelatedShoppingProductsByProductId(mProduct.getId(), 20));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (mProduct.getProductBrand() != null) {
                            relatedProductsByBrandId.addAll(productDB
                                    .getRelatedProductsByBrandId(mProduct.getProductBrand().getId(), mProduct.getId(), 50));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (mProduct.getProductSubCategory() != null) {
                            relatedProductsBySubCategoryId.addAll(productDB
                                    .getRelatedProductsBySubCategoryId(mProduct.getProductSubCategory().getId(), mProduct.getId(), 30));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mProduct!=null) {
                        mShareIntent = Utils.createShareProductIntent(mProduct, getContext(), mCurrentUser);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ((TextView) view.findViewById(R.id.product_name)).setText(mProduct.getName());

                                if (mProduct.getDescription() != null) {
                                    ((TextView) view.findViewById(R.id.product_detail_description)).setText(mProduct.getDescription());
                                }

                                if (mProduct.getProductBrand() != null && mProduct.getProductBrand().getDescription() != null) {
                                    ((TextView) view.findViewById(R.id.product_brand)).setText(getString(R.string.brand_detail,
                                            mProduct.getProductBrand().getDescription()));
                                }

                                if (!TextUtils.isEmpty(mProduct.getImageFileName())) {
                                    final File thumb = Utils.getFileThumbByFileName(getContext(), mCurrentUser, mProduct.getImageFileName());
                                    if (thumb!=null) {
                                        Picasso.with(getContext())
                                                .load(thumb).error(R.drawable.no_image_available)
                                                .into((ImageView) view.findViewById(R.id.product_image));
                                    } else {
                                        ((ImageView) view.findViewById(R.id.product_image))
                                                .setImageResource(R.drawable.no_image_available);
                                    }

                                    final ImageView favoriteImageView = (ImageView) view.findViewById(R.id.favorite_imageView);
                                    favoriteImageView.setColorFilter(Utils.getColor(getContext(), R.color.dark_grey), PorterDuff.Mode.SRC_ATOP);

                                    if(mProduct.isFavorite()){
                                        favoriteImageView.setImageResource(R.drawable.ic_favorite_black_36dp);
                                    }else{
                                        favoriteImageView.setImageResource(R.drawable.ic_favorite_border_black_36dp);
                                    }

                                    favoriteImageView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(mProduct.isFavorite()) {
                                                String result = (new OrderLineDB(getContext(), mCurrentUser)).removeProductFromWishList(mProduct);
                                                if (result == null) {
                                                    mProduct.setFavorite(false);
                                                    favoriteImageView.setImageResource(R.drawable.ic_favorite_border_black_36dp);
                                                } else {
                                                    Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                String result = (new OrderLineDB(getContext(), mCurrentUser)).addProductToWishList(mProduct);
                                                if (result == null) {
                                                    mProduct.setFavorite(true);
                                                    favoriteImageView.setImageResource(R.drawable.ic_favorite_black_36dp);
                                                } else {
                                                    Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                    });

                                    File img = Utils.getFileInOriginalDirByFileName(getContext(), mCurrentUser, mProduct.getImageFileName());
                                    if(img!=null){
                                        Picasso.with(getContext())
                                                .load(img).into((ImageView) view.findViewById(R.id.product_image));
                                    }else{
                                        Picasso.with(getContext())
                                            .load(mCurrentUser.getServerAddress() + "/IntelligentDataSynchronizer/GetOriginalImage?fileName="
                                                    + mProduct.getImageFileName())
                                            //.into((ImageView) view.findViewById(R.id.product_image), new Callback() {
                                            //    @Override
                                            //    public void onSuccess() {
                                            //        Utils.createFileInOriginalDir(mProduct.getImageFileName(),
                                            //                ((BitmapDrawable)((ImageView) view.findViewById(R.id.product_image)).getDrawable()).getBitmap(),
                                            //                mCurrentUser, getContext());
                                            //    }
                                            //
                                            //    @Override
                                            //    public void onError() {
                                            //        if(thumb!=null){
                                            //            Picasso.with(getContext())
                                            //                    .load(thumb).error(R.drawable.no_image_available)
                                            //                    .into((ImageView) view.findViewById(R.id.product_image));
                                            //        }else{
                                            //            ((ImageView) view.findViewById(R.id.product_image))
                                            //                    .setImageResource(R.drawable.no_image_available);
                                            //        }
                                            //    }
                                            //});
                                            .into((ImageView) view.findViewById(R.id.product_image),
                                                    new CallbackPicassoDownloadImage(mProduct.getImageFileName(),
                                                            false, mCurrentUser, getContext()){
                                                        @Override
                                                        public void onSuccess() {
                                                            super.onSuccess();
                                                        }

                                                        @Override
                                                        public void onError() {
                                                            if(thumb!=null){
                                                                Picasso.with(getContext())
                                                                        .load(thumb).error(R.drawable.no_image_available)
                                                                        .into((ImageView) view.findViewById(R.id.product_image));
                                                            }else{
                                                                ((ImageView) view.findViewById(R.id.product_image))
                                                                        .setImageResource(R.drawable.no_image_available);
                                                            }
                                                        }
                                                    });
                                    }
                                } else {
                                    ((ImageView) view.findViewById(R.id.product_image))
                                            .setImageResource(R.drawable.no_image_available);
                                }

                                 view.findViewById(R.id.product_image).setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         startActivity((new Intent(getContext(), ZoomImageActivity.class)
                                             .putExtra(ZoomImageActivity.KEY_IMAGE_FILE_NAME, mProduct.getImageFileName())));
                                     }
                                 });

                                if (!relatedProductsByShopping.isEmpty()) {
                                    RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.related_shopping_products_recycler_view);
                                    // use this setting to improve performance if you know that changes
                                    // in content do not change the layout size of the RecyclerView
                                    mRecyclerView.setHasFixedSize(true);
                                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                                    mRecyclerView.setAdapter(new ProductsListAdapter(getContext(), getActivity(), relatedProductsByShopping, false,
                                            ProductsListAdapter.REDIRECT_PRODUCT_DETAILS, mCurrentUser));
                                } else {
                                    view.findViewById(R.id.related_shopping_products_card_view).setVisibility(View.GONE);
                                }

                                if (!relatedProductsByBrandId.isEmpty()) {
                                    RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.related_products_by_brand_recycler_view);
                                    // use this setting to improve performance if you know that changes
                                    // in content do not change the layout size of the RecyclerView
                                    mRecyclerView.setHasFixedSize(true);
                                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                                    mRecyclerView.setAdapter(new ProductsListAdapter(getContext(), getActivity(), relatedProductsByBrandId, false,
                                            ProductsListAdapter.REDIRECT_PRODUCT_DETAILS, mCurrentUser));

                                    ((TextView) view.findViewById(R.id.related_products_by_brand_tv))
                                            .setText(getString(R.string.related_products_by_brand_title,
                                                    !TextUtils.isEmpty(mProduct.getProductBrand().getDescription())
                                                            ? mProduct.getProductBrand().getDescription()
                                                            : mProduct.getProductBrand().getName()));
                                } else {
                                    view.findViewById(R.id.related_products_by_brand_card_view).setVisibility(View.GONE);
                                }

                                if (!relatedProductsBySubCategoryId.isEmpty()) {
                                    RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.relatedproducts_recycler_view);
                                    // use this setting to improve performance if you know that changes
                                    // in content do not change the layout size of the RecyclerView
                                    mRecyclerView.setHasFixedSize(true);
                                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                                    mRecyclerView.setAdapter(new ProductsListAdapter(getContext(), getActivity(), relatedProductsBySubCategoryId, false,
                                            ProductsListAdapter.REDIRECT_PRODUCT_DETAILS, mCurrentUser));
                                } else {
                                    view.findViewById(R.id.relatedproducts_card_view).setVisibility(View.GONE);
                                }

                                if (mProduct.getProductCommercialPackage() != null) {
                                    ((TextView) view.findViewById(R.id.product_commercial_package)).setText(getContext().getString(R.string.commercial_package,
                                            mProduct.getProductCommercialPackage().getUnits(), mProduct.getProductCommercialPackage().getUnitDescription()));
                                } else {
                                    view.findViewById(R.id.product_commercial_package).setVisibility(View.GONE);
                                }

                                if (view.findViewById(R.id.product_addtoshoppingsales_button) != null) {
                                    view.findViewById(R.id.product_addtoshoppingsales_button).setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                addToShoppingSale(mCurrentUser, mProduct);
                                            }
                                        }
                                    );
                                }

                                if (view.findViewById(R.id.product_addtoshoppingcart_button) != null) {
                                    view.findViewById(R.id.product_addtoshoppingcart_button).setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                addToShoppingCart(mCurrentUser, mProduct);
                                            }
                                        }
                                    );
                                }

                                if (view.findViewById(R.id.product_availability) != null) {
                                    ((TextView) view.findViewById(R.id.product_availability))
                                            .setText(getString(R.string.availability, mProduct.getAvailability()));
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                            } finally {
                                view.findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        }.start();

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.productdetailfragment, menu);

        // Retrieve the share menu item
        MenuItem item = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        // Attach an intent to this ShareActionProvider. You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        mShareActionProvider.setShareIntent(mShareIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            mShareActionProvider.setShareIntent(mShareIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addToShoppingCart(User user, Product product) {
        DialogAddToShoppingCart addToShoppingCartFragment =
                DialogAddToShoppingCart.newInstance(product, user);
        addToShoppingCartFragment.show(getActivity().getSupportFragmentManager(),
                DialogAddToShoppingCart.class.getSimpleName());
    }

    private void addToShoppingSale(User user, Product product) {
        DialogAddToShoppingSale addToShoppingSaleFragment =
                DialogAddToShoppingSale.newInstance(product, user);
        addToShoppingSaleFragment.show(getActivity().getSupportFragmentManager(),
                DialogAddToShoppingSale.class.getSimpleName());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_PRODUCT_ID, mProductId);
        super.onSaveInstanceState(outState);
    }
}