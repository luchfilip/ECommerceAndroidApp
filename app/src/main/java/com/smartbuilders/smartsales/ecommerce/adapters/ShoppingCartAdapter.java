package com.smartbuilders.smartsales.ecommerce.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.BuildConfig;
import com.smartbuilders.smartsales.ecommerce.data.ProductDB;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.ProductDetailActivity;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.businessRules.OrderBR;
import com.smartbuilders.smartsales.ecommerce.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Alberto on 7/4/2016.
 */
public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> {

    private Context mContext;
    private Fragment mFragment;
    private ArrayList<OrderLine> mDataset;
    private User mUser;
    private OrderLineDB orderLineDB;
    private boolean mIsShoppingCart;
    private boolean mIsManagePriceInOrder;
    private boolean mShowProductPrice;
    private boolean mShowProductTax;
    private boolean mShowProductTotalPrice;
    private boolean mShowSubTotalLineAmount;
    private boolean mShowTotalLineAmount;

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView productImage;
        public TextView productInternalCode;
        public ImageView deleteItem;
        public TextView productName;
        public TextView productPrice;
        public TextView productTax;
        public TextView productTotalPrice;
        public TextView subTotalLine;
        public TextView totalLine;
        public EditText qtyOrdered;
        public View containerLayout;

        public ViewHolder(View v) {
            super(v);
            productImage = (ImageView) v.findViewById(R.id.product_image);
            productInternalCode = (TextView) v.findViewById(R.id.product_internal_code);
            productName = (TextView) v.findViewById(R.id.product_name);
            productPrice = (TextView) v.findViewById(R.id.product_price);
            productTax = (TextView) v.findViewById(R.id.product_tax);
            productTotalPrice = (TextView) v.findViewById(R.id.product_total_price);
            subTotalLine = (TextView) v.findViewById(R.id.sub_total_line_textView);
            totalLine = (TextView) v.findViewById(R.id.total_line_textView);
            deleteItem = (ImageView) v.findViewById(R.id.delete_item_button_img);
            qtyOrdered = (EditText) v.findViewById(R.id.qty_ordered);
            containerLayout = v.findViewById(R.id.container_layout);
        }
    }

    public interface Callback {
        void updateQtyOrdered(OrderLine orderLine);
        void reloadShoppingCart();
        void reloadShoppingCart(ArrayList<OrderLine> orderLines);
    }

    public ShoppingCartAdapter(Context context, Fragment shoppingCartFragment,
                               ArrayList<OrderLine> data, boolean isShoppingCart, User user) {
        mContext = context;
        mFragment = shoppingCartFragment;
        mDataset = data;
        mIsShoppingCart = isShoppingCart;
        mUser = user;
        OrderBR.validateQuantityOrderedInOrderLines(context, user, data);
        orderLineDB = new OrderLineDB(context, user);
        mIsManagePriceInOrder = Parameter.isManagePriceInOrder(context, user);
        mShowProductPrice = Parameter.showProductPrice(context, user);
        mShowProductTax = Parameter.showProductTax(context, user);
        mShowProductTotalPrice = Parameter.showProductTotalPrice(context, user);
        mShowSubTotalLineAmount = Parameter.showSubTotalLineAmountInOrderLine(context, user);
        mShowTotalLineAmount = Parameter.showTotalLineAmountInOrderLine(context, user);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ShoppingCartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shopping_cart_item, parent, false);

        return new ViewHolder(v);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        try {
            return mDataset.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public long getItemId(int position) {
        try {
            return mDataset.get(position).getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (BuildConfig.USE_PRODUCT_IMAGE) {
            Utils.loadThumbImageByFileName(mContext, mUser,
                    mDataset.get(position).getProduct().getImageFileName(), holder.productImage);
        } else {
            holder.productImage.setVisibility(View.GONE);
        }

        holder.productName.setText(mDataset.get(position).getProduct().getName());

        if(mDataset.get(position).getProduct().getInternalCode()!=null){
            holder.productInternalCode.setText(mContext.getString(R.string.product_internalCode,
                    mDataset.get(position).getProduct().getInternalCodeMayoreoFormat()));
            holder.productInternalCode.setVisibility(View.VISIBLE);
        } else {
            holder.productInternalCode.setVisibility(View.GONE);
        }

        if (mIsManagePriceInOrder) {
            if (mShowProductPrice) {
                //holder.productPrice.setText(mContext.getString(R.string.price_detail,
                //        mDataset.get(position).getProduct().getProductPriceAvailability().getCurrency().getName(),
                //        mDataset.get(position).getProduct().getProductPriceAvailability().getProductPriceStringFormat()));
                holder.productPrice.setText(mContext.getString(R.string.product_price,
                        mDataset.get(position).getProduct().getProductPriceAvailability().getPriceStringFormat()));
                holder.productPrice.setVisibility(View.VISIBLE);
            } else {
                holder.productPrice.setVisibility(View.GONE);
            }

            if (mShowProductTax) {
                //holder.productTax.setText(mContext.getString(R.string.product_tax_detail,
                //        mDataset.get(position).getProduct().getProductPriceAvailability().getCurrency().getName(),
                //        mDataset.get(position).getProduct().getProductPriceAvailability().getTaxStringFormat()));
                holder.productTax.setText(mContext.getString(R.string.product_tax,
                        mDataset.get(position).getProduct().getProductPriceAvailability().getTaxStringFormat()));
                holder.productTax.setVisibility(View.VISIBLE);
            } else {
                holder.productTax.setVisibility(View.GONE);
            }

            if (mShowProductTotalPrice) {
                holder.productTotalPrice.setText(mContext.getString(R.string.product_total_price_detail,
                        mDataset.get(position).getProduct().getProductPriceAvailability().getCurrency().getName(),
                        mDataset.get(position).getProduct().getProductPriceAvailability().getTotalPriceStringFormat()));
                holder.productTotalPrice.setVisibility(View.VISIBLE);
            } else {
                holder.productTotalPrice.setVisibility(View.GONE);
            }

            if (mShowSubTotalLineAmount) {
                holder.subTotalLine.setText(mContext.getString(R.string.order_sub_total_line_amount,
                        mDataset.get(position).getProduct().getProductPriceAvailability().getCurrency().getName(),
                        mDataset.get(position).getSubTotalLineAmountStringFormat()));
                holder.subTotalLine.setVisibility(View.VISIBLE);
            } else {
                holder.subTotalLine.setVisibility(View.GONE);
            }

            if (mShowTotalLineAmount) {
                holder.totalLine.setText(mContext.getString(R.string.order_total_line_amount,
                        mDataset.get(position).getProduct().getProductPriceAvailability().getCurrency().getName(),
                        mDataset.get(position).getTotalLineAmountStringFormat()));
                holder.totalLine.setVisibility(View.VISIBLE);
            } else {
                holder.totalLine.setVisibility(View.GONE);
            }
        } else {
            holder.productPrice.setVisibility(View.GONE);
            holder.productTax.setVisibility(View.GONE);
            holder.productTotalPrice.setVisibility(View.GONE);
            holder.totalLine.setVisibility(View.GONE);
        }

        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                    .setMessage(mContext.getString(R.string.delete_from_shopping_cart_question,
                            mDataset.get(holder.getAdapterPosition()).getProduct().getName()))
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String result = null;
                            if(mIsShoppingCart){
                                result = orderLineDB.deleteOrderLine(mDataset.get(holder.getAdapterPosition()));
                            }
                            if(result == null){
                                if(mIsShoppingCart){
                                    ((Callback) mFragment).reloadShoppingCart();
                                }else{
                                    mDataset.remove(holder.getAdapterPosition());
                                    ((Callback) mFragment).reloadShoppingCart(mDataset);
                                }
                            } else {
                                Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
            }
        });

        if (mDataset.get(position).isQuantityOrderedInvalid()) {
            holder.qtyOrdered.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorQtyOrderedError));
        } else {
            holder.qtyOrdered.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimaryLight));
        }

        holder.qtyOrdered.setText(String.valueOf(mDataset.get(position).getQuantityOrdered()));

        holder.qtyOrdered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Callback) mFragment).updateQtyOrdered(mDataset.get(holder.getAdapterPosition()));
            }
        });

        holder.containerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product product = (new ProductDB(mContext, mUser))
                        .getProductById(mDataset.get(holder.getAdapterPosition()).getProductId());
                if (product!=null) {
                    mContext.startActivity(new Intent(mContext, ProductDetailActivity.class)
                            .putExtra(ProductDetailActivity.KEY_PRODUCT, product));
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.no_product_details), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setData(ArrayList<OrderLine> orderLines) {
        mDataset = orderLines;
        OrderBR.validateQuantityOrderedInOrderLines(mContext, mUser, mDataset);
        notifyDataSetChanged();
    }
}
