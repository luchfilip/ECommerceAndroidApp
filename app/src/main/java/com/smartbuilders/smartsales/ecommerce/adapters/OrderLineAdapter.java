package com.smartbuilders.smartsales.ecommerce.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerce.BuildConfig;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.ProductDetailActivity;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.data.CurrencyDB;
import com.smartbuilders.smartsales.ecommerce.model.Currency;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Alberto on 8/4/2016.
 */
public class OrderLineAdapter extends RecyclerView.Adapter<OrderLineAdapter.ViewHolder> {

    private ArrayList<OrderLine> mDataset;
    private Context mContext;
    private User mUser;
    private String mCurrencyName;
    private boolean mIsManagePriceInOrder;

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
        public View goToProductDetails;

        public ViewHolder(View v) {
            super(v);
            productName = (TextView) v.findViewById(R.id.product_name);
            productInternalCode = (TextView) v.findViewById(R.id.product_internal_code);
            productImage = (ImageView) v.findViewById(R.id.product_image);
            qtyOrdered = (TextView) v.findViewById(R.id.qty_requested_textView);
            productPrice = (TextView) v.findViewById(R.id.product_price_textView);
            productTax = (TextView) v.findViewById(R.id.product_tax_percentage_textView);
            totalLineAmount = (TextView) v.findViewById(R.id.total_line_amount_textView);
            goToProductDetails = v.findViewById(R.id.go_to_product_details);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public OrderLineAdapter(Context context, ArrayList<OrderLine> myDataset, User user) {
        mContext = context;
        mDataset = myDataset;
        mUser = user;
        Currency currency = (new CurrencyDB(context, user))
                .getActiveCurrencyById(Parameter.getDefaultCurrencyId(context, user));
        mCurrencyName = currency!=null ? currency.getName() : "";
        mIsManagePriceInOrder = Parameter.isManagePriceInOrder(context, user);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mDataset!=null) {
            return mDataset.size();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        try {
            return mDataset.get(position).getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public OrderLineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_line_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.productName.setText(mDataset.get(position).getProduct().getName());

        if(mDataset.get(position).getProduct().getInternalCode()!=null){
            holder.productInternalCode.setText(mContext.getString(R.string.product_internalCode,
                    mDataset.get(position).getProduct().getInternalCode()));
        }

        if (BuildConfig.USE_PRODUCT_IMAGE) {
            Utils.loadThumbImageByFileName(mContext, mUser,
                    mDataset.get(position).getProduct().getImageFileName(), holder.productImage);
        } else {
            holder.productImage.setVisibility(View.GONE);
        }

        holder.qtyOrdered.setText(mContext.getString(R.string.qty_ordered_label_detail,
                String.valueOf(mDataset.get(position).getQuantityOrdered())));
        if (mIsManagePriceInOrder) {
            holder.productPrice.setText(mContext.getString(R.string.order_product_price,
                    mCurrencyName, mDataset.get(position).getPriceStringFormat()));
            holder.productPrice.setVisibility(View.VISIBLE);

            holder.productTax.setText(mContext.getString(R.string.product_tax_percentage_detail,
                    mDataset.get(position).getTaxPercentageStringFormat()));
            holder.productTax.setVisibility(View.VISIBLE);

            holder.totalLineAmount.setText(mContext.getString(R.string.order_sub_total_line_amount,
                    mCurrencyName, mDataset.get(position).getTotalLineAmountStringFormat()));
            holder.totalLineAmount.setVisibility(View.VISIBLE);
        } else {
            holder.productPrice.setVisibility(View.GONE);
            holder.productTax.setVisibility(View.GONE);
            holder.totalLineAmount.setVisibility(View.GONE);
        }

        holder.goToProductDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProductDetails(mDataset.get(holder.getAdapterPosition()).getProduct());
            }
        });
    }

    private void goToProductDetails(Product product) {
        Intent intent = new Intent(mContext, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.KEY_PRODUCT_ID, product.getId());
        mContext.startActivity(intent);
    }
}