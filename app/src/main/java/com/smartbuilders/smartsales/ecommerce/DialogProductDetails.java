package com.smartbuilders.smartsales.ecommerce;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerce.data.ProductDB;
import com.smartbuilders.smartsales.ecommerce.data.SalesOrderLineDB;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.utils.CreateShareIntentThread;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.smartsales.salesforcesystem.DialogAddToShoppingSale2;
import com.smartbuilders.smartsales.salesforcesystem.DialogUpdateShoppingSaleQtyOrdered;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.model.UserProfile;

/**
 * Created by Jesus Sarco on 16/9/2016.
 */
public class DialogProductDetails extends DialogFragment {

    private static final String STATE_CURRENT_PRODUCT = "STATE_CURRENT_PRODUCT";
    private static final String STATE_CURRENT_USER = "STATE_CURRENT_USER";

    private Product mProduct;
    private User mUser;

    public DialogProductDetails() {
        // Empty constructor required for DialogFragment
    }

    public static DialogProductDetails newInstance(Product product, User user){
        DialogProductDetails dialogProductDetails = new DialogProductDetails();
        dialogProductDetails.mProduct = product;
        dialogProductDetails.mUser = user;
        return dialogProductDetails;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(STATE_CURRENT_PRODUCT)){
                mProduct = savedInstanceState.getParcelable(STATE_CURRENT_PRODUCT);
            }
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        final View view = inflater.inflate(R.layout.dialog_product_details, container);

        final boolean managePriceInOrder = Parameter.isManagePriceInOrder(getContext(), mUser);

        view.findViewById(R.id.container_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ProductDetailActivity.class)
                        .putExtra(ProductDetailActivity.KEY_PRODUCT, mProduct));
            }
        });

        ((TextView) view.findViewById(R.id.product_name)).setText(mProduct.getName());

        if (BuildConfig.USE_PRODUCT_IMAGE) {
            Utils.loadOriginalImageByFileName(getContext(), mUser,
                    mProduct.getImageFileName(), ((ImageView) view.findViewById(R.id.product_image)));
        } else {
            view.findViewById(R.id.product_image).setVisibility(View.GONE);
        }

        if (managePriceInOrder
                && mProduct.getProductPriceAvailability().getAvailability()>0
                && mProduct.getProductPriceAvailability().getPrice()>0) {
            if (Parameter.showProductTotalPrice(getContext(), mUser)) {
                ((TextView) view.findViewById(R.id.product_total_price)).setText(getString(R.string.product_total_price_detail,
                        mProduct.getProductPriceAvailability().getCurrency().getName(),
                        mProduct.getProductPriceAvailability().getTotalPriceStringFormat()));
                view.findViewById(R.id.product_total_price).setVisibility(View.VISIBLE);
            } else if (Parameter.showProductPrice(getContext(), mUser)) {
                ((TextView) view.findViewById(R.id.product_total_price)).setText(getString(R.string.product_price_detail,
                        mProduct.getProductPriceAvailability().getCurrency().getName(),
                        mProduct.getProductPriceAvailability().getPriceStringFormat()));
                view.findViewById(R.id.product_total_price).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.product_total_price).setVisibility(View.GONE);
            }
        } else {
            view.findViewById(R.id.product_total_price).setVisibility(View.GONE);
        }

        ((TextView) view.findViewById(R.id.product_availability)).setText(getString(R.string.availability,
                mProduct.getProductPriceAvailability().getAvailability()));

        view.findViewById(R.id.share_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.findViewById(R.id.share_imageView).setEnabled(false);
                new CreateShareIntentThread(getActivity(), getContext(), mUser,
                        mProduct, ((ImageView) view.findViewById(R.id.share_imageView))).start();
            }
        });

        if (BuildConfig.IS_SALES_FORCE_SYSTEM) {
            view.findViewById(R.id.favorite_imageView).setVisibility(View.GONE);
        } else {
            ((ImageView) view.findViewById(R.id.favorite_imageView))
                    .setImageResource(mProduct.isFavorite() ? R.drawable.ic_favorite_black_24dp : R.drawable.ic_favorite_border_black_24dp);
            view.findViewById(R.id.favorite_imageView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mProduct.isFavorite()) {
                        String result = removeFromWishList(mProduct.getId());
                        if (result == null) {
                            mProduct.setFavorite(false);
                            ((ImageView) view.findViewById(R.id.favorite_imageView))
                                    .setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        } else {
                            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String result = addToWishList(mProduct);
                        if (result == null) {
                            mProduct.setFavorite(true);
                            ((ImageView) view.findViewById(R.id.favorite_imageView))
                                    .setImageResource(R.drawable.ic_favorite_black_24dp);
                        } else {
                            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        ((ImageView) view.findViewById(R.id.addToShoppingCart_imageView))
                .setColorFilter(Utils.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        view.findViewById(R.id.addToShoppingCart_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderLine orderLine = (new OrderLineDB(getContext(), mUser))
                        .getOrderLineFromShoppingCartByProductId(mProduct.getId());
                if(orderLine!=null){
                    updateQtyOrderedInShoppingCart(orderLine);
                }else{
                    addToShoppingCart(mProduct);
                }
            }
        });

        ((ImageView) view.findViewById(R.id.addToShoppingSale_imageView))
                .setColorFilter(Utils.getColor(getContext(), R.color.golden), PorterDuff.Mode.SRC_ATOP);
        view.findViewById(R.id.addToShoppingSale_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BuildConfig.IS_SALES_FORCE_SYSTEM
                        || (mUser.getUserProfileId()== UserProfile.SALES_MAN_PROFILE_ID && managePriceInOrder)) {
                    try {
                        SalesOrderLine salesOrderLine = (new SalesOrderLineDB(getContext(), mUser))
                                .getSalesOrderLineFromShoppingSales(mProduct.getId(), Utils.getAppCurrentBusinessPartnerId(getContext(), mUser));
                        if (salesOrderLine != null) {
                            updateQtyOrderedInShoppingSales(salesOrderLine);
                        } else {
                            addToShoppingSale(mProduct, managePriceInOrder);
                        }
                    } catch (Exception e) {
                        //do nothing
                    }
                } else {
                    addToShoppingSale(mProduct, managePriceInOrder);
                }
            }
        });

        ((TextView) view.findViewById(R.id.product_internal_code)).setText(mProduct.getInternalCodeMayoreoFormat());

        ((TextView) view.findViewById(R.id.product_reference)).setText(mProduct.getReference());

        if(mProduct.getProductBrand()!=null
                && !TextUtils.isEmpty(mProduct.getProductBrand().getName())){
            ((TextView) view.findViewById(R.id.product_brand)).setText(getString(R.string.brand_detail,
                    mProduct.getProductBrand().getName()));
            view.findViewById(R.id.product_brand).setVisibility(TextView.VISIBLE);
        }else{
            view.findViewById(R.id.product_brand).setVisibility(TextView.GONE);
        }

        if(Parameter.showProductRatingBar(getContext(), mUser)){
            ((TextView) view.findViewById(R.id.product_ratingBar_label_textView)).setText(getString(R.string.product_ratingBar_label_text_detail,
                    Parameter.getProductRatingBarLabelText(getContext(), mUser)));
            if(mProduct.getRating()>=0){
                ((RatingBar) view.findViewById(R.id.product_ratingBar)).setRating(mProduct.getRating());
            }
            view.findViewById(R.id.product_ratingBar_container).setVisibility(View.VISIBLE);
        }else{
            view.findViewById(R.id.product_ratingBar_container).setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(mProduct.getDescription())){
            ((TextView) view.findViewById(R.id.product_description)).setText(getString(R.string.product_description_detail,
                    mProduct.getDescription()));
            view.findViewById(R.id.product_description).setVisibility(View.VISIBLE);
        }else{
            view.findViewById(R.id.product_description).setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(mProduct.getPurpose())){
            ((TextView) view.findViewById(R.id.product_purpose)).setText(getString(R.string.product_purpose_detail,
                    mProduct.getPurpose()));
            view.findViewById(R.id.product_purpose).setVisibility(View.VISIBLE);
        }else{
            view.findViewById(R.id.product_purpose).setVisibility(View.GONE);
        }

        return view;
    }

    private String addToWishList(Product product) {
        return (new OrderLineDB(getContext(), mUser)).addProductToWishList(product);
    }

    private String removeFromWishList(int productId) {
        return (new OrderLineDB(getContext(), mUser)).removeProductFromWishList(productId);
    }

    private void addToShoppingCart(Product product) {
        product = (new ProductDB(getContext(), mUser)).getProductById(product.getId());
        if (product!=null) {
            DialogAddToShoppingCart dialogAddToShoppingCart =
                    DialogAddToShoppingCart.newInstance(product, mUser);
            dialogAddToShoppingCart.show(getActivity().getSupportFragmentManager(),
                    DialogAddToShoppingCart.class.getSimpleName());
        } else {
            //TODO: mostrar error
        }
    }

    public void updateQtyOrderedInShoppingCart(OrderLine orderLine) {
        DialogUpdateShoppingCartQtyOrdered dialogUpdateShoppingCartQtyOrdered =
                DialogUpdateShoppingCartQtyOrdered.newInstance(orderLine, true, mUser);
        dialogUpdateShoppingCartQtyOrdered.show(getActivity().getSupportFragmentManager(),
                DialogUpdateShoppingCartQtyOrdered.class.getSimpleName());
    }

    private void addToShoppingSale(Product product, boolean managePriceInOrder) {
        product = (new ProductDB(getContext(), mUser)).getProductById(product.getId());
        if (product!=null) {
            if (BuildConfig.IS_SALES_FORCE_SYSTEM
                    || (mUser.getUserProfileId()==UserProfile.SALES_MAN_PROFILE_ID && managePriceInOrder)) {
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
        } else {
            //TODO: mostrar mensaje de error
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mUser);
        outState.putParcelable(STATE_CURRENT_PRODUCT, mProduct);
        super.onSaveInstanceState(outState);
    }
}
