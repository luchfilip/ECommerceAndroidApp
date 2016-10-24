package com.smartbuilders.smartsales.ecommerce.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
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
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.ProductDetailActivity;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.data.CurrencyDB;
import com.smartbuilders.smartsales.ecommerce.data.SalesOrderLineDB;
import com.smartbuilders.smartsales.ecommerce.model.Currency;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Alberto on 7/4/2016.
 */
public class ShoppingSaleAdapter extends RecyclerView.Adapter<ShoppingSaleAdapter.ViewHolder> {

    public static final int FOCUS_PRICE = 1;
    public static final int FOCUS_TAX_PERCENTAGE = 2;
    public static final int FOCUS_QTY_ORDERED = 3;

    private Context mContext;
    private Fragment mFragment;
    private ArrayList<SalesOrderLine> mDataset;
    private User mUser;
    private SalesOrderLineDB mSalesOrderLineDB;
    private String productPriceLabelText;

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView productImage;
        public ImageView deleteItem;
        public TextView productName;
        public TextView productInternalCode;
        public EditText qtyOrdered;
        public EditText productPrice;
        public EditText productTaxPercentage;
        public EditText totalLineAmount;
        public TextView productPriceLabel;

        public ViewHolder(View v) {
            super(v);
            productImage = (ImageView) v.findViewById(R.id.product_image);
            productName = (TextView) v.findViewById(R.id.product_name);
            productInternalCode = (TextView) v.findViewById(R.id.product_internal_code);
            productPrice = (EditText) v.findViewById(R.id.product_price);
            productTaxPercentage = (EditText) v.findViewById(R.id.product_tax_percentage);
            totalLineAmount = (EditText) v.findViewById(R.id.total_line_amount);
            deleteItem = (ImageView) v.findViewById(R.id.delete_item_button_img);
            qtyOrdered = (EditText) v.findViewById(R.id.qty_ordered);
            productPriceLabel = (TextView) v.findViewById(R.id.product_price_label_textView);
        }
    }

    public interface Callback {
        void updateSalesOrderLine(SalesOrderLine orderLine, int focus);
        void reloadShoppingSalesList(User user);
        void reloadShoppingSale();
    }

    public ShoppingSaleAdapter(Context context, Fragment fragment, ArrayList<SalesOrderLine> data,
                               User user) {
        mContext = context;
        mFragment = fragment;
        mDataset = data;
        mUser = user;
        mSalesOrderLineDB = new SalesOrderLineDB(context, user);
        Currency currency = (new CurrencyDB(context, user)).getActiveCurrencyById(Parameter.getDefaultCurrencyId(context, user));
        productPriceLabelText = currency!=null
                ? mContext.getString(R.string.price_currency_label_detail, currency.getName())
                : mContext.getString(R.string.price_label);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ShoppingSaleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shopping_sale_item, parent, false);

        return new ViewHolder(v);
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
            return -1;
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (BuildConfig.USE_PRODUCT_IMAGE) {
            Utils.loadThumbImageByFileName(mContext, mUser,
                    mDataset.get(position).getProduct().getImageFileName(), holder.productImage);

            holder.productImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToProductDetails(mDataset.get(holder.getAdapterPosition()).getProduct());
                }
            });
        } else {
            holder.productImage.setVisibility(View.GONE);
        }

        holder.productName.setText(mDataset.get(position).getProduct().getName());
        holder.productName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProductDetails(mDataset.get(holder.getAdapterPosition()).getProduct());
            }
        });

        if(mDataset.get(position).getProduct().getInternalCode()!=null){
            holder.productInternalCode.setText(mContext.getString(R.string.product_internalCode,
                    mDataset.get(position).getProduct().getInternalCode()));
        }

        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setMessage(mContext.getString(R.string.delete_from_shopping_sale_question,
                                mDataset.get(holder.getAdapterPosition()).getProduct().getName()))
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String result = mSalesOrderLineDB.deactiveSalesOrderLine(mDataset.get(holder.getAdapterPosition()));
                                if(result == null){
                                    mDataset.remove(holder.getAdapterPosition());
                                    notifyDataSetChanged();
                                    ((Callback) mFragment).reloadShoppingSale();
                                    ((Callback) mFragment).reloadShoppingSalesList(mUser);
                                } else {
                                    Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }
        });

        holder.productPriceLabel.setText(productPriceLabelText);

        holder.productPrice.setText(mDataset.get(position).getPriceStringFormat());
        holder.productPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Callback) mFragment).updateSalesOrderLine(mDataset.get(holder.getAdapterPosition()), FOCUS_PRICE);
            }
        });

        holder.productTaxPercentage.setText(mDataset.get(position).getProductTaxPercentageStringFormat());
        holder.productTaxPercentage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Callback) mFragment).updateSalesOrderLine(mDataset.get(holder.getAdapterPosition()), FOCUS_TAX_PERCENTAGE);
            }
        });

        holder.qtyOrdered.setText(String.valueOf(mDataset.get(position).getQuantityOrdered()));
        holder.qtyOrdered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Callback) mFragment).updateSalesOrderLine(mDataset.get(holder.getAdapterPosition()), FOCUS_QTY_ORDERED);
            }
        });

        holder.totalLineAmount.setText(mDataset.get(position).getTotalLineAmountStringFormat());
    }

    private void goToProductDetails(Product product) {
        Intent intent = new Intent(mContext, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.KEY_PRODUCT_ID, product.getId());
        mContext.startActivity(intent);
    }

    public void setData(ArrayList<SalesOrderLine> salesOrderLines) {
        mDataset = salesOrderLines;
        notifyDataSetChanged();
    }
}
