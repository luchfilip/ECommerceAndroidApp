package com.smartbuilders.smartsales.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.smartbuilders.smartsales.ecommerce.data.SalesOrderLineDB;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;
import com.smartbuilders.smartsales.ecommerce.providers.BluetoothConnectionProvider;
import com.smartbuilders.smartsales.salesforcesystem.DialogAddToShoppingSale2;
import com.smartbuilders.smartsales.salesforcesystem.DialogUpdateShoppingSaleQtyOrdered;
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

    private static final String STATE_PRODUCT = "STATE_PRODUCT";

    private Product mProduct;
    private User mUser;
    private Intent mShareIntent;
    private ProgressDialog waitPlease;

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
                        if(getActivity().getIntent().getExtras().containsKey(ProductDetailActivity.KEY_PRODUCT)){
                            mProduct = getActivity().getIntent().getExtras().getParcelable(ProductDetailActivity.KEY_PRODUCT);
                        }
                    }

                    if(savedInstanceState!=null){
                        if(savedInstanceState.containsKey(STATE_PRODUCT)){
                            mProduct = savedInstanceState.getParcelable(STATE_PRODUCT);
                        }
                    }

                    mUser = Utils.getCurrentUser(getContext());
                    ProductDB productDB = new ProductDB(getContext(), mUser);

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
                        (new ProductRecentlySeenDB(getContext(), mUser)).addProduct(mProduct.getId());

                        // Se envia el id del producto por bluetooth
                        Cursor cursor = null;
                        try {
                            cursor = getContext().getContentResolver().query(BluetoothConnectionProvider.GET_CHAT_SERVICE_STATE_URI, null, null, null, null);
                            // Check that we're actually connected before trying anything
                            if(cursor!=null && cursor.moveToNext() && cursor.getInt(0)==BluetoothChatService.STATE_CONNECTED){
                                cursor.close();
                                // Get the message and tell the BluetoothChatService to write
                                cursor = getContext().getContentResolver().query(BluetoothConnectionProvider.SEND_MESSAGE_URI.buildUpon()
                                        .appendQueryParameter(BluetoothConnectionProvider.KEY_MESSAGE, String.valueOf(mProduct.getId())).build(), null, null, null, null);
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
                                                    Toast.makeText(getContext(), R.string.no_image_available, Toast.LENGTH_SHORT).show();
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
                                        view.findViewById(R.id.product_image_container).setVisibility(View.GONE);
                                        view.findViewById(R.id.product_image).setVisibility(View.GONE);
                                        view.findViewById(R.id.zoom_imageView).setVisibility(View.GONE);
                                        if (view.findViewById(R.id.product_details_info_container)!=null) {
                                            view.findViewById(R.id.product_details_info_container).setMinimumHeight(0);
                                        }
                                    }

                                    if (!TextUtils.isEmpty(mProduct.getInternalCode())) {
                                        ((TextView) view.findViewById(R.id.product_internal_code))
                                                .setText(mProduct.getInternalCodeMayoreoFormat());
                                    } else {
                                        view.findViewById(R.id.product_internal_code).setVisibility(View.GONE);
                                    }

                                    if (!TextUtils.isEmpty(mProduct.getReference())) {
                                        ((TextView) view.findViewById(R.id.product_reference))
                                                .setText(mProduct.getReference());
                                    } else {
                                        view.findViewById(R.id.product_reference).setVisibility(View.GONE);
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

                                    if (BuildConfig.IS_SALES_FORCE_SYSTEM) {
                                        view.findViewById(R.id.favorite_imageView).setVisibility(View.GONE);
                                    } else {
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
                                                        Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    String result = (new OrderLineDB(getContext(), mUser)).addProductToWishList(mProduct);
                                                    if (result == null) {
                                                        mProduct.setFavorite(true);
                                                        favoriteImageView.setImageResource(R.drawable.ic_favorite_black_24dp);
                                                    } else {
                                                        Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        });
                                    }

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
                                                        intent.putExtra(ProductsListActivity.KEY_PRODUCT_ID_SHOW_RELATED_SHOPPING_PRODUCTS, mProduct.getId());
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
                                                if (BuildConfig.IS_SALES_FORCE_SYSTEM) {
                                                    try {
                                                        SalesOrderLine salesOrderLine = (new SalesOrderLineDB(getContext(), mUser))
                                                                .getSalesOrderLineFromShoppingSales(mProduct.getId(), Utils.getAppCurrentBusinessPartnerId(getContext(), mUser));
                                                        if (salesOrderLine != null) {
                                                            updateQtyOrderedInShoppingSales(salesOrderLine);
                                                        } else {
                                                            addToShoppingSale(mProduct);
                                                        }
                                                    } catch (Exception e) {
                                                        //do nothing
                                                    }
                                                } else {
                                                    addToShoppingSale(mProduct);
                                                }
                                            }
                                        }
                                    );

                                    view.findViewById(R.id.product_addtoshoppingcart_button).setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                OrderLine orderLine = (new OrderLineDB(getContext(), mUser))
                                                        .getOrderLineFromShoppingCartByProductId(mProduct.getId());
                                                if (orderLine != null) {
                                                    updateQtyOrderedInShoppingCart(orderLine, mUser);
                                                } else {
                                                    addToShoppingCart(mProduct);
                                                }
                                            }
                                        }
                                    );

                                    if (Parameter.isManagePriceInOrder(getContext(), mUser)) {
                                        if (Parameter.showProductTotalPrice(getContext(), mUser)) {
                                            ((TextView) view.findViewById(R.id.product_price))
                                                    .setText(getString(R.string.product_total_price_detail,
                                                            mProduct.getDefaultProductPriceAvailability().getCurrency().getName(),
                                                            mProduct.getDefaultProductPriceAvailability().getTotalPriceStringFormat()));
                                            view.findViewById(R.id.product_price).setVisibility(View.VISIBLE);
                                        } else if (Parameter.showProductPrice(getContext(), mUser)) {
                                            ((TextView) view.findViewById(R.id.product_price))
                                                    .setText(getString(R.string.product_price_detail,
                                                            mProduct.getDefaultProductPriceAvailability().getCurrency().getName(),
                                                            mProduct.getDefaultProductPriceAvailability().getPriceStringFormat()));
                                            view.findViewById(R.id.product_price).setVisibility(View.VISIBLE);
                                        } else {
                                            view.findViewById(R.id.product_price).setVisibility(View.GONE);
                                        }
                                    } else {
                                        view.findViewById(R.id.product_price).setVisibility(View.GONE);
                                    }

                                    ((TextView) view.findViewById(R.id.product_availability))
                                            .setText(getString(R.string.availability,
                                                    mProduct.getDefaultProductPriceAvailability().getAvailability()));
                                } else {
                                    //TODO: mostrar mesaje de error cuando el objeto product es null
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_share) {
            new CreateShareIntentThread().start();
        } else if (i == R.id.search) {
            startActivity(new Intent(getContext(), SearchResultsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addToShoppingCart(Product product) {
        DialogAddToShoppingCart addToShoppingCartFragment =
                DialogAddToShoppingCart.newInstance(product, mUser);
        addToShoppingCartFragment.show(getActivity().getSupportFragmentManager(),
                DialogAddToShoppingCart.class.getSimpleName());
    }

    public void updateQtyOrderedInShoppingCart(OrderLine orderLine, User user) {
        DialogUpdateShoppingCartQtyOrdered dialogUpdateShoppingCartQtyOrdered =
                DialogUpdateShoppingCartQtyOrdered.newInstance(orderLine, true, user);
        dialogUpdateShoppingCartQtyOrdered.show(getActivity().getSupportFragmentManager(),
                DialogUpdateShoppingCartQtyOrdered.class.getSimpleName());
    }

    private void addToShoppingSale(Product product) {
        if (BuildConfig.IS_SALES_FORCE_SYSTEM) {
            DialogAddToShoppingSale2 dialogAddToShoppingSale2 =
                    DialogAddToShoppingSale2.newInstance(product, mUser);
            dialogAddToShoppingSale2.show(getActivity().getSupportFragmentManager(),
                    DialogAddToShoppingSale2.class.getSimpleName());
        } else {
            DialogAddToShoppingSale dialogAddToShoppingSale =
                    DialogAddToShoppingSale.newInstance(product, mUser);
            dialogAddToShoppingSale.show(getActivity().getSupportFragmentManager(),
                    DialogAddToShoppingSale.class.getSimpleName());
        }
    }

    public void updateQtyOrderedInShoppingSales(SalesOrderLine salesOrderLine) {
        Product product = (new ProductDB(getContext(), mUser)).getProductById(salesOrderLine.getProductId());
        if (product!=null) {
            DialogUpdateShoppingSaleQtyOrdered dialogUpdateShoppingSaleQtyOrdered =
                    DialogUpdateShoppingSaleQtyOrdered.newInstance(product, salesOrderLine, mUser);
            dialogUpdateShoppingSaleQtyOrdered.show(getActivity().getSupportFragmentManager(),
                    DialogUpdateShoppingSaleQtyOrdered.class.getSimpleName());
        } else {
            //TODO: mostrar mensaje de error
        }
    }

    class CreateShareIntentThread extends Thread {

        private String mErrorMessage;

        CreateShareIntentThread() {
        }

        public void run() {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Se bloquea la rotacion de la pantalla para evitar que se mate a la aplicacion
                        Utils.lockScreenOrientation(getActivity());
                        if (waitPlease==null || !waitPlease.isShowing()){
                            waitPlease = ProgressDialog.show(getContext(), null,
                                    getString(R.string.creating_product_share_card_wait_please), true, false);
                        }
                    }
                });
            }

            try {
                if (mShareIntent == null) {
                    createShareAndDownloadIntent();
                }
            } catch (Exception e) {
                e.printStackTrace();
                mErrorMessage = e.getMessage();
            }

            if(getActivity()!=null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (TextUtils.isEmpty(mErrorMessage)) {
                            if (mShareIntent != null) {
                                startActivity(mShareIntent);
                            }
                        } else {
                            Toast.makeText(getContext(), mErrorMessage, Toast.LENGTH_SHORT).show();
                        }
                        if (waitPlease!=null && waitPlease.isShowing()) {
                            waitPlease.dismiss();
                            waitPlease = null;
                        }
                        Utils.unlockScreenOrientation(getActivity());
                    }
                });
            }
        }

        private void createShareAndDownloadIntent() throws Exception {
            try {
                if (getActivity()!=null && getContext()!=null && mUser!=null && mProduct==null) {
                    mShareIntent = Utils.createShareProductIntentFromView(getActivity(), getContext(), mUser, mProduct);
                } else {
                    mShareIntent = null;
                }
            } catch (Exception e) {
                mShareIntent = null;
                throw e;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_PRODUCT, mProduct);
        super.onSaveInstanceState(outState);
    }
}