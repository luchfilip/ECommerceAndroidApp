package com.smartbuilders.smartsales.ecommerce.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.BuildConfig;
import com.smartbuilders.smartsales.ecommerce.data.ProductDB;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.ProductDetailActivity;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.data.CurrencyDB;
import com.smartbuilders.smartsales.ecommerce.model.Currency;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Alberto on 8/4/2016.
 */
public class SalesOrderLineAdapter extends RecyclerView.Adapter<SalesOrderLineAdapter.ViewHolder> {

    private ArrayList<SalesOrderLine> mDataset;
    private Context mContext;
    private User mUser;
    private String mCurrencyName;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView productName;
        public TextView productInternalCode;
        public ImageView productImage;
        public TextView qtyOrdered;
        public TextView productPrice;
        public TextView productTax;
        public TextView totalLineAmount;
        public View containerLayout;

        public ViewHolder(View v) {
            super(v);
            productName = (TextView) v.findViewById(R.id.product_name);
            productInternalCode = (TextView) v.findViewById(R.id.product_internal_code);
            productImage = (ImageView) v.findViewById(R.id.product_image);
            qtyOrdered = (TextView) v.findViewById(R.id.qty_requested_textView);
            productPrice = (TextView) v.findViewById(R.id.product_price_textView);
            productTax = (TextView) v.findViewById(R.id.product_tax_percentage_textView);
            totalLineAmount = (TextView) v.findViewById(R.id.total_line_amount_textView);
            containerLayout = v.findViewById(R.id.container_layout);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SalesOrderLineAdapter(Context context, ArrayList<SalesOrderLine> myDataset, User user) {
        mDataset = myDataset;
        mUser = user;
        mContext = context;
        Currency currency = (new CurrencyDB(context, user))
                .getActiveCurrencyById(Parameter.getDefaultCurrencyId(context, user));
        mCurrencyName = currency!=null ? currency.getName() : "";
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SalesOrderLineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sales_order_line_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
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
        }

        holder.qtyOrdered.setText(mContext.getString(R.string.qty_ordered_label_detail,
                String.valueOf(mDataset.get(position).getQuantityOrdered())));
        holder.productPrice.setText(mContext.getString(R.string.sales_order_product_price,
                mCurrencyName, mDataset.get(position).getPriceStringFormat()));
        holder.productTax.setText(mContext.getString(R.string.product_tax_percentage_detail,
                mDataset.get(position).getProductTaxPercentageStringFormat()));
        holder.totalLineAmount.setText(mContext.getString(R.string.sales_order_sub_total_line_amount,
                mCurrencyName, mDataset.get(position).getTotalLineAmountStringFormat()));

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

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mDataset!=null) {
            return mDataset.size();
        }
        return 0;
    }
}