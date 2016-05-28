package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.util.Log;
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
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.ProductRecyclerViewAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ProductDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.GetFileFromServlet;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProductDetailFragment extends Fragment {

    private static final String TAG = ProductDetailFragment.class.getSimpleName();
    private static final String STATE_CURRENT_USER = "state_current_user";

    private ProgressDialog waitPlease;
    private Product mProduct;
    private ShareActionProvider mShareActionProvider;
    public static final String KEY_PRODUCT = "key_product";
    private User mCurrentUser;
    private ArrayList<Product> relatedProductsByShopping;
    private ArrayList<Product> relatedProductsByBrandId;
    private ArrayList<Product> relatedProductsBySubCategoryId;

    public ProductDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        if(getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null) {
            if(getActivity().getIntent().getExtras().containsKey(ProductDetailActivity.KEY_CURRENT_USER)){
                mCurrentUser = getActivity().getIntent().getExtras().getParcelable(ProductDetailActivity.KEY_CURRENT_USER);
            }
            if(getActivity().getIntent().getExtras().containsKey(KEY_PRODUCT)){
                mProduct = getActivity().getIntent().getExtras().getParcelable(KEY_PRODUCT);
            }
        }

        waitPlease = ProgressDialog.show(getActivity(), getString(R.string.loading), getString(R.string.wait_please), true, false);
        new Thread() {
            @Override
            public void run() {
                ProductDB productDB = new ProductDB(getContext(), mCurrentUser);
                try {
                    mProduct = productDB.getProductById(mProduct.getId(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    relatedProductsByShopping = productDB.getRelatedShoppingProductsByProductId(mProduct.getId(), 20);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if(mProduct.getProductBrand()!=null) {
                        relatedProductsByBrandId = productDB
                                .getRelatedProductsByBrandId(mProduct.getProductBrand().getId(), mProduct.getId(), 50);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if(mProduct.getProductSubCategory()!=null) {
                        relatedProductsBySubCategoryId = productDB
                                .getRelatedProductsBySubCategoryId(mProduct.getProductSubCategory().getId(), mProduct.getId(), 30);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
                                File img = Utils.getFileImageByFileName(getContext(), mCurrentUser, mProduct.getImageFileName());
                                if(img!=null){
                                    Picasso.with(getContext())
                                            .load(img).error(R.drawable.no_image_available)
                                            .into((ImageView) view.findViewById(R.id.product_image));
                                }else{
                                    Picasso.with(getContext())
                                        .load(mCurrentUser.getServerAddress() + "/IntelligentDataSynchronizer/GetOriginalImage?fileName="
                                                + mProduct.getImageFileName())
                                        .error(R.drawable.no_image_available)
                                        .into((ImageView) view.findViewById(R.id.product_image), new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                Utils.createFileInOriginalDir(mProduct.getImageFileName(),
                                                        ((BitmapDrawable)((ImageView) view.findViewById(R.id.product_image)).getDrawable()).getBitmap(),
                                                        mCurrentUser, getContext());
                                            }

                                            @Override
                                            public void onError() {
                                                File img = Utils.getFileThumbByFileName(getContext(), mCurrentUser, mProduct.getImageFileName());
                                                if(img!=null){
                                                    Picasso.with(getContext())
                                                            .load(img).error(R.drawable.no_image_available)
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
                                         .putExtra(ZoomImageActivity.KEY_CURRENT_USER, mCurrentUser)
                                         .putExtra(ZoomImageActivity.KEY_IMAGE_FILE_NAME, mProduct.getImageFileName())));
                                 }
                             });

                            if (relatedProductsByShopping != null && !relatedProductsByShopping.isEmpty()) {
                                RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.related_shopping_products_recycler_view);
                                // use this setting to improve performance if you know that changes
                                // in content do not change the layout size of the RecyclerView
                                mRecyclerView.setHasFixedSize(true);
                                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                                mRecyclerView.setAdapter(new ProductRecyclerViewAdapter(getActivity(), relatedProductsByShopping, false,
                                        ProductRecyclerViewAdapter.REDIRECT_PRODUCT_DETAILS, mCurrentUser));
                            } else {
                                view.findViewById(R.id.related_shopping_products_card_view).setVisibility(View.GONE);
                            }

                            if (relatedProductsByBrandId != null && !relatedProductsByBrandId.isEmpty()) {
                                RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.related_products_by_brand_recycler_view);
                                // use this setting to improve performance if you know that changes
                                // in content do not change the layout size of the RecyclerView
                                mRecyclerView.setHasFixedSize(true);
                                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                                mRecyclerView.setAdapter(new ProductRecyclerViewAdapter(getActivity(), relatedProductsByBrandId, false,
                                        ProductRecyclerViewAdapter.REDIRECT_PRODUCT_DETAILS, mCurrentUser));

                                ((TextView) view.findViewById(R.id.related_products_by_brand_tv))
                                        .setText(getString(R.string.related_products_by_brand_title,
                                                !TextUtils.isEmpty(mProduct.getProductBrand().getDescription())
                                                        ? mProduct.getProductBrand().getDescription()
                                                        : mProduct.getProductBrand().getName()));
                            } else {
                                view.findViewById(R.id.related_products_by_brand_card_view).setVisibility(View.GONE);
                            }

                            if (relatedProductsBySubCategoryId != null && !relatedProductsBySubCategoryId.isEmpty()) {
                                RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.relatedproducts_recycler_view);
                                // use this setting to improve performance if you know that changes
                                // in content do not change the layout size of the RecyclerView
                                mRecyclerView.setHasFixedSize(true);
                                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                                mRecyclerView.setAdapter(new ProductRecyclerViewAdapter(getActivity(), relatedProductsBySubCategoryId, false,
                                        ProductRecyclerViewAdapter.REDIRECT_PRODUCT_DETAILS, mCurrentUser));
                            } else {
                                view.findViewById(R.id.relatedproducts_card_view).setVisibility(View.GONE);
                            }

                            if (mProduct.getProductCommercialPackage() != null) {
                                ((TextView) view.findViewById(R.id.product_comercial_package)).setText(getContext().getString(R.string.commercial_package,
                                        mProduct.getProductCommercialPackage().getUnits() + " " +
                                                mProduct.getProductCommercialPackage().getUnitDescription()));
                            } else {
                                view.findViewById(R.id.product_comercial_package).setVisibility(View.GONE);
                            }

                            if (view.findViewById(R.id.product_addtoshoppingsales_button) != null) {
                                view.findViewById(R.id.product_addtoshoppingsales_button).setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            addToShoppingSale();
                                        }
                                    }
                                );
                            }

                            if (view.findViewById(R.id.product_addtoshoppingcart_button) != null) {
                                view.findViewById(R.id.product_addtoshoppingcart_button).setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            addToShoppingCart();
                                        }
                                    }
                                );
                            }

                            if (view.findViewById(R.id.product_addtowishlist_button) != null) {
                                view.findViewById(R.id.product_addtowishlist_button).setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String result = (new OrderLineDB(getContext(), mCurrentUser)).addProductToWishList(mProduct);
                                            if (result == null) {
                                                Toast.makeText(getContext(), R.string.product_put_in_wishlist, Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                                            }
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
                            try {
                                waitPlease.dismiss();
                                waitPlease.cancel();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }.start();

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
        if (mProduct != null) {
            //mShareActionProvider.setShareHistoryFileName(null);
            mShareActionProvider.setShareIntent(createShareIntent());
        } else {
            Log.d(TAG, "Share Action Provider is null?");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            mShareActionProvider.setShareIntent(createShareIntent());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent createShareIntent(){
        String fileName = "tmpImg.jpg";
        if(mProduct.getImageFileName()!=null){
            Bitmap productImage = Utils.getImageByFileName(getContext(), mCurrentUser, mProduct.getImageFileName());
            if(productImage==null){
                //Si el archivo no existe entonces se descarga
                GetFileFromServlet getFileFromServlet =
                        new GetFileFromServlet(mProduct.getImageFileName(), false, mCurrentUser, getContext());
                try {
                    productImage = getFileFromServlet.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            if(productImage!=null){
                Utils.createFileInCacheDir(fileName, productImage, getContext());
            }else{
                Utils.createFileInCacheDir(fileName, R.drawable.no_image_available, getContext());
            }
        }else{
            Utils.createFileInCacheDir(fileName, R.drawable.no_image_available, getContext());
        }
        return Utils.createShareProductIntent(getContext(), mProduct, fileName);
    }

    private void addToShoppingCart() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        DialogAddToShoppingCart addToShoppingCartFragment =
                DialogAddToShoppingCart.newInstance(mProduct, mCurrentUser);
        addToShoppingCartFragment.show(fm, "fragment_edit_name");
    }

    private void addToShoppingSale() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        DialogAddToShoppingSale addToShoppingSaleFragment =
                DialogAddToShoppingSale.newInstance(mProduct, mCurrentUser);
        addToShoppingSaleFragment.show(fm, "fragment_sale_edit_name");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
        super.onSaveInstanceState(outState);
    }
}