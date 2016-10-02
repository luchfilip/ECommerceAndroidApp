package com.smartbuilders.smartsales.ecommerce;

import android.content.Intent;
import android.database.Cursor;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.bluetoothchat.BluetoothChatService;
import com.smartbuilders.smartsales.ecommerce.providers.BluetoothConnectionProvider;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.adapters.ProductsListAdapter;
import com.smartbuilders.smartsales.ecommerce.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerce.data.ProductDB;
import com.smartbuilders.smartsales.ecommerce.data.ProductRecentlySeenDB;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProductDetailFragment extends Fragment {

    private static final String STATE_PRODUCT_ID = "STATE_PRODUCT_ID";

    private int mProductId;
    private Product mProduct;
    private User mUser;
    private ShareActionProvider mShareActionProvider;
    private Intent mShareIntent;

    public ProductDetailFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
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

                    mUser = Utils.getCurrentUser(getContext());
                    ProductDB productDB = new ProductDB(getContext(), mUser);

                    mProduct = productDB.getProductById(mProductId);
                    if (mProduct!=null) {
                        relatedProductsByShopping.addAll(productDB.getRelatedShoppingProductsByProductId(mProduct.getId(), 12));
                        if (mProduct.getProductBrand()!=null) {
                            relatedProductsByBrandId.addAll(productDB
                                    .getRelatedProductsByBrandId(mProduct.getProductBrand().getId(), mProduct.getId(), 12));
                        }
                        if (mProduct.getProductSubCategory()!=null) {
                            relatedProductsBySubCategoryId.addAll(productDB
                                    .getRelatedProductsBySubCategoryId(mProduct.getProductSubCategory().getId(), mProduct.getId(), 12));
                        }
                        //Se agrega el producto a la lista de productos recientemente vistos
                        (new ProductRecentlySeenDB(getContext(), mUser)).addProduct(mProductId,
                                Utils.getAppCurrentBusinessPartnerId(getContext(), mUser));

                        mShareIntent = Utils.createShareProductIntentFromView(getActivity(), getContext(), mUser, mProduct);

                        // Se envia el id del producto por bluetooth
                        Cursor cursor = null;
                        try {
                            cursor = getContext().getContentResolver().query(BluetoothConnectionProvider.GET_CHAT_SERVICE_STATE_URI, null, null, null, null);
                            // Check that we're actually connected before trying anything
                            if(cursor!=null && cursor.moveToNext() && cursor.getInt(0)==BluetoothChatService.STATE_CONNECTED){
                                cursor.close();
                                // Get the message and tell the BluetoothChatService to write
                                cursor = getContext().getContentResolver().query(BluetoothConnectionProvider.SEND_MESSAGE_URI.buildUpon()
                                        .appendQueryParameter(BluetoothConnectionProvider.KEY_MESSAGE, String.valueOf(mProductId)).build(), null, null, null, null);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (cursor!=null) {
                                cursor.close();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (mProduct!=null) {
                                    ((TextView) view.findViewById(R.id.product_name)).setText(mProduct.getName());

                                    if (BuildConfig.USE_PRODUCT_IMAGE) {
                                        Utils.loadOriginalImageByFileName(getContext(), mUser, mProduct.getImageFileName(),
                                                (ImageView) view.findViewById(R.id.product_image));

                                        view.findViewById(R.id.product_image).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (TextUtils.isEmpty(mProduct.getImageFileName())) {
                                                    Toast.makeText(getContext(), R.string.no_image_available, Toast.LENGTH_LONG).show();
                                                } else {
                                                    startActivity((new Intent(getContext(), ZoomImageActivity.class)
                                                            .putExtra(ZoomImageActivity.KEY_IMAGE_FILE_NAME, mProduct.getImageFileName())));
                                                }
                                            }
                                        });

                                        if (TextUtils.isEmpty(mProduct.getImageFileName())) {
                                            view.findViewById(R.id.zoom_imageView).setVisibility(View.GONE);
                                        }
                                    } else {
                                        view.findViewById(R.id.product_image).setVisibility(View.GONE);
                                        view.findViewById(R.id.zoom_imageView).setVisibility(View.GONE);
                                    }

                                    if (!TextUtils.isEmpty(mProduct.getInternalCode())) {
                                        ((TextView) view.findViewById(R.id.product_internal_code))
                                                .setText(getContext().getString(R.string.product_internalCode, mProduct.getInternalCode()));
                                    } else {
                                        view.findViewById(R.id.product_internal_code).setVisibility(View.GONE);
                                    }

                                    if (Parameter.showProductRatingBar(getContext(), mUser)) {
                                        ((TextView) view.findViewById(R.id.product_ratingBar_label_textView))
                                                .setText(getString(R.string.product_ratingBar_label_text_detail, Parameter.getProductRatingBarLabelText(getContext(), mUser)));
                                        if (mProduct.getRating() >= 0) {
                                            ((RatingBar) view.findViewById(R.id.product_ratingBar)).setRating(mProduct.getRating());
                                        }
                                    } else {
                                        view.findViewById(R.id.product_ratingBar_container).setVisibility(View.GONE);
                                    }

                                    if (!TextUtils.isEmpty(mProduct.getDescription())) {
                                        ((TextView) view.findViewById(R.id.product_description)).setText(mProduct.getDescription());
                                    } else {
                                        view.findViewById(R.id.product_description).setVisibility(View.GONE);
                                    }

                                    if (!TextUtils.isEmpty(mProduct.getDescription())) {
                                        ((TextView) view.findViewById(R.id.product_description)).setText(getString(R.string.product_description_detail,
                                                mProduct.getDescription()));
                                    } else {
                                        view.findViewById(R.id.product_description).setVisibility(View.GONE);
                                    }

                                    if (!TextUtils.isEmpty(mProduct.getPurpose())) {
                                        ((TextView) view.findViewById(R.id.product_purpose)).setText(getString(R.string.product_purpose_detail,
                                                mProduct.getPurpose()));
                                    } else {
                                        view.findViewById(R.id.product_purpose).setVisibility(View.GONE);
                                    }

                                    if (mProduct.getProductBrand() != null && !TextUtils.isEmpty(mProduct.getProductBrand().getName())) {
                                        ((TextView) view.findViewById(R.id.product_brand)).setText(getString(R.string.brand_detail,
                                                mProduct.getProductBrand().getName()));
                                    } else {
                                        view.findViewById(R.id.product_brand).setVisibility(View.GONE);
                                    }

                                    final ImageView favoriteImageView = (ImageView) view.findViewById(R.id.favorite_imageView);

                                    favoriteImageView.setImageResource(mProduct.isFavorite()
                                            ? R.drawable.ic_favorite_black_24dp : R.drawable.ic_favorite_border_black_24dp);
                                    favoriteImageView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (mProduct.isFavorite()) {
                                                String result = (new OrderLineDB(getContext(), mUser)).removeProductFromWishList(mProduct.getId());
                                                if (result == null) {
                                                    mProduct.setFavorite(false);
                                                    favoriteImageView.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                                                } else {
                                                    Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                String result = (new OrderLineDB(getContext(), mUser)).addProductToWishList(mProduct);
                                                if (result == null) {
                                                    mProduct.setFavorite(true);
                                                    favoriteImageView.setImageResource(R.drawable.ic_favorite_black_24dp);
                                                } else {
                                                    Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                    });

                                    if (!relatedProductsByShopping.isEmpty()) {
                                        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.related_shopping_products_recycler_view);
                                        // use this setting to improve performance if you know that changes
                                        // in content do not change the layout size of the RecyclerView
                                        mRecyclerView.setHasFixedSize(true);
                                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                                        mRecyclerView.setAdapter(new ProductsListAdapter(getContext(), getActivity(),
                                                relatedProductsByShopping, ProductsListAdapter.MASK_PRODUCT_MIN_INFO,
                                                DialogSortProductListOptions.SORT_BY_PRODUCT_NAME_ASC, mUser));

                                        view.findViewById(R.id.related_shopping_products_see_all)
                                                .setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getContext(), ProductsListActivity.class);
                                                        intent.putExtra(ProductsListActivity.KEY_PRODUCT_ID_SHOW_RELATED_SHOPPING_PRODUCTS, mProductId);
                                                        startActivity(intent);
                                                    }
                                                });
                                    } else {
                                        view.findViewById(R.id.related_shopping_products_card_view).setVisibility(View.GONE);
                                    }

                                    if (!relatedProductsByBrandId.isEmpty()) {
                                        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.related_products_by_brand_recycler_view);
                                        // use this setting to improve performance if you know that changes
                                        // in content do not change the layout size of the RecyclerView
                                        mRecyclerView.setHasFixedSize(true);
                                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                                        mRecyclerView.setAdapter(new ProductsListAdapter(getContext(), getActivity(),
                                                relatedProductsByBrandId, ProductsListAdapter.MASK_PRODUCT_MIN_INFO,
                                                DialogSortProductListOptions.SORT_BY_PRODUCT_NAME_ASC, mUser));

                                        ((TextView) view.findViewById(R.id.related_products_by_brand_tv))
                                                .setText(getString(R.string.related_products_by_brand_title,
                                                        !TextUtils.isEmpty(mProduct.getProductBrand().getDescription())
                                                                ? mProduct.getProductBrand().getDescription()
                                                                : mProduct.getProductBrand().getName()));

                                        view.findViewById(R.id.related_products_by_brand_see_all)
                                                .setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getContext(), ProductsListActivity.class);
                                                        intent.putExtra(ProductsListActivity.KEY_PRODUCT_BRAND_ID, mProduct.getProductBrandId());
                                                        startActivity(intent);
                                                    }
                                                });
                                    } else {
                                        view.findViewById(R.id.related_products_by_brand_card_view).setVisibility(View.GONE);
                                    }

                                    if (!relatedProductsBySubCategoryId.isEmpty()) {
                                        ((TextView) view.findViewById(R.id.related_products))
                                                .setText(getString(R.string.related_products_by_sub_category_details,
                                                        mProduct.getProductSubCategory().getName()));
                                        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.relatedproducts_recycler_view);
                                        // use this setting to improve performance if you know that changes
                                        // in content do not change the layout size of the RecyclerView
                                        mRecyclerView.setHasFixedSize(true);
                                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                                        mRecyclerView.setAdapter(new ProductsListAdapter(getContext(), getActivity(),
                                                relatedProductsBySubCategoryId, ProductsListAdapter.MASK_PRODUCT_MIN_INFO,
                                                DialogSortProductListOptions.SORT_BY_PRODUCT_NAME_ASC, mUser));

                                        view.findViewById(R.id.related_products_see_all)
                                                .setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getContext(), ProductsListActivity.class);
                                                        intent.putExtra(ProductsListActivity.KEY_PRODUCT_SUBCATEGORY_ID, mProduct.getProductSubCategoryId());
                                                        startActivity(intent);
                                                    }
                                                });
                                    } else {
                                        view.findViewById(R.id.relatedproducts_card_view).setVisibility(View.GONE);
                                    }

                                    if (mProduct.getProductCommercialPackage()!=null
                                            && !TextUtils.isEmpty(mProduct.getProductCommercialPackage().getUnitDescription())
                                            && mProduct.getProductCommercialPackage().getUnits()>0) {
                                        ((TextView) view.findViewById(R.id.product_commercial_package))
                                                .setText(getContext().getString(R.string.commercial_package_label_detail,
                                                        mProduct.getProductCommercialPackage().getUnitDescription(),
                                                        mProduct.getProductCommercialPackage().getUnits()));
                                    } else {
                                        view.findViewById(R.id.product_commercial_package).setVisibility(View.GONE);
                                    }

                                    view.findViewById(R.id.product_addtoshoppingsales_button).setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                addToShoppingSale(mUser, mProduct);
                                            }
                                        }
                                    );

                                    view.findViewById(R.id.product_addtoshoppingcart_button).setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                OrderLine orderLine = (new OrderLineDB(getContext(), mUser))
                                                        .getOrderLineFromShoppingCartByProductId(mProductId);
                                                if (orderLine != null) {
                                                    updateQtyOrderedInShoppingCart(orderLine, mUser);
                                                } else {
                                                    addToShoppingCart(mUser, mProduct);
                                                }
                                            }
                                        }
                                    );

                                    if (Parameter.isManagePriceInOrder(getContext(), mUser)) {
                                        ((TextView) view.findViewById(R.id.product_price))
                                                .setText(getString(R.string.price_detail,
                                                        mProduct.getDefaultProductPriceAvailability().getCurrency().getName(),
                                                        mProduct.getDefaultProductPriceAvailability().getPrice()));
                                    } else {
                                        view.findViewById(R.id.product_price).setVisibility(View.GONE);
                                    }

                                    ((TextView) view.findViewById(R.id.product_availability))
                                            .setText(getString(R.string.availability,
                                                    mProduct.getDefaultProductPriceAvailability().getAvailability()));

                                    if (mShareActionProvider!=null) {
                                        mShareActionProvider.setShareIntent(mShareIntent);
                                    }
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
        inflater.inflate(R.menu.menu_product_detail, menu);

        // Retrieve the share menu item
        MenuItem item = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        // Attach an intent to this ShareActionProvider. You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        mShareActionProvider.setShareIntent(mShareIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_share) {
            if (mShareActionProvider!=null) {
                mShareActionProvider.setShareIntent(mShareIntent);
            }
        } else if (i == R.id.search) {
            startActivity(new Intent(getContext(), SearchResultsActivity.class));
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

    public void updateQtyOrderedInShoppingCart(OrderLine orderLine, User user) {
        DialogUpdateShoppingCartQtyOrdered dialogUpdateShoppingCartQtyOrdered =
                DialogUpdateShoppingCartQtyOrdered.newInstance(orderLine, true, user);
        dialogUpdateShoppingCartQtyOrdered.show(getActivity().getSupportFragmentManager(),
                DialogUpdateShoppingCartQtyOrdered.class.getSimpleName());
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