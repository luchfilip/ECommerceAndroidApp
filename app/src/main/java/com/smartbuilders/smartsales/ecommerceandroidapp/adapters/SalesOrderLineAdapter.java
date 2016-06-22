package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductDetailActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.SalesOrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.CallbackPicassoDownloadImage;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Alberto on 8/4/2016.
 */
public class SalesOrderLineAdapter extends RecyclerView.Adapter<SalesOrderLineAdapter.ViewHolder> {

    private ArrayList<SalesOrderLine> mDataset;
    private Context mContext;
    private User mCurrentUser;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView productName;
        public TextView productBrand;
        public ImageView productImage;
        public TextView qtyOrdered;
        public TextView productPrice;
        public TextView productTax;
        public TextView totalLineAmount;

        public ViewHolder(View v) {
            super(v);
            productName = (TextView) v.findViewById(R.id.product_name);
            productBrand = (TextView) v.findViewById(R.id.product_brand);
            productImage = (ImageView) v.findViewById(R.id.product_image);
            qtyOrdered = (TextView) v.findViewById(R.id.qty_requested_textView);
            productPrice = (TextView) v.findViewById(R.id.product_price_textView);
            productTax = (TextView) v.findViewById(R.id.product_tax_percentage_textView);
            totalLineAmount = (TextView) v.findViewById(R.id.total_line_amount_textView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SalesOrderLineAdapter(Context context, ArrayList<SalesOrderLine> myDataset, User user) {
        mDataset = myDataset;
        mCurrentUser = user;
        mContext = context;
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
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.productName.setText(mDataset.get(position).getProduct().getName());
        holder.productName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProductDetails(mDataset.get(holder.getAdapterPosition()).getProduct());
            }
        });

        if(holder.productBrand!=null){
            if(mDataset.get(position).getProduct().getProductBrand()!=null
                    && !TextUtils.isEmpty(mDataset.get(position).getProduct().getProductBrand().getDescription())){
                holder.productBrand.setText(mContext.getString(R.string.brand_detail,
                        mDataset.get(position).getProduct().getProductBrand().getDescription()));
            }else{
                holder.productBrand.setVisibility(TextView.GONE);
            }
        }

        if(!TextUtils.isEmpty(mDataset.get(position).getProduct().getImageFileName())){
            File img = Utils.getFileThumbByFileName(mContext, mCurrentUser,
                    mDataset.get(position).getProduct().getImageFileName());
            if(img!=null){
                Picasso.with(mContext)
                        .load(img).error(R.drawable.no_image_available).into(holder.productImage);
            }else{
                Picasso.with(mContext)
                        .load(mCurrentUser.getServerAddress() + "/IntelligentDataSynchronizer/GetThumbImage?fileName="
                                + mDataset.get(position).getProduct().getImageFileName())
                        .error(R.drawable.no_image_available)
                        //.into(holder.productImage, new Callback() {
                        //    @Override
                        //    public void onSuccess() {
                        //        Utils.createFileInThumbDir(mDataset.get(holder.getAdapterPosition()).getProduct().getImageFileName(),
                        //                ((BitmapDrawable)holder.productImage.getDrawable()).getBitmap(),
                        //                mCurrentUser, mContext);
                        //    }
                        //
                        //    @Override
                        //    public void onError() {
                        //    }
                        //});
                        .into(holder.productImage,
                                new CallbackPicassoDownloadImage(mDataset.get(position).getProduct().getImageFileName(),
                                        true, mCurrentUser, mContext));
            }
        }else{
            holder.productImage.setImageResource(R.drawable.no_image_available);
        }

        holder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProductDetails(mDataset.get(holder.getAdapterPosition()).getProduct());
            }
        });

        holder.qtyOrdered.setText(mContext.getString(R.string.qty_ordered,
                String.valueOf(mDataset.get(position).getQuantityOrdered())));
        holder.productPrice.setText(mContext.getString(R.string.order_price_label,
                mDataset.get(position).getPriceStringFormat()));
        holder.productTax.setText(mContext.getString(R.string.order_tax_amount,
                mDataset.get(position).getTaxPercentageStringFormat()));
        holder.totalLineAmount.setText(mContext.getString(R.string.order_sub_total_line_amount,
                mDataset.get(position).getTotalLineAmountStringFormat()));

    }

    private void goToProductDetails(Product product) {
        Intent intent = new Intent(mContext, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.KEY_PRODUCT_ID, product.getId());
        mContext.startActivity(intent);
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