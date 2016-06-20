package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductDetailActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.SalesOrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.ShoppingSaleFragment;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.SalesOrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.CallbackPicassoDownloadImage;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Alberto on 7/4/2016.
 */
public class ShoppingSaleAdapter extends RecyclerView.Adapter<ShoppingSaleAdapter.ViewHolder> {

    public static final int FOCUS_PRICE = 1;
    public static final int FOCUS_TAX_PERCENTAGE = 2;
    public static final int FOCUS_QTY_ORDERED = 3;

    private Context mContext;
    private ShoppingSaleFragment mShoppingSaleFragment;
    private ArrayList<SalesOrderLine> mDataset;
    private User mCurrentUser;
    private SalesOrderLineDB mSalesOrderLineDB;

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView productImage;
        public ImageView deleteItem;
        public TextView productName;
        public TextView productBrand;
        public EditText qtyOrdered;
        public EditText productPrice;
        public EditText productTaxPercentage;
        public EditText totalLineAmount;

        public ViewHolder(View v) {
            super(v);
            productImage = (ImageView) v.findViewById(R.id.product_image);
            productName = (TextView) v.findViewById(R.id.product_name);
            productBrand = (TextView) v.findViewById(R.id.product_brand);
            productPrice = (EditText) v.findViewById(R.id.product_price);
            productTaxPercentage = (EditText) v.findViewById(R.id.product_tax_percentage);
            totalLineAmount = (EditText) v.findViewById(R.id.total_line_amount);
            deleteItem = (ImageView) v.findViewById(R.id.delete_item_button_img);
            qtyOrdered = (EditText) v.findViewById(R.id.qty_ordered);
        }
    }

    public interface Callback {
        void updateSalesOrderLine(SalesOrderLine orderLine, int focus);
        void reloadShoppingSalesList();
    }

    public ShoppingSaleAdapter(Context context, ShoppingSaleFragment shoppingSaleFragment,
                               ArrayList<SalesOrderLine> data, User user) {
        mContext = context;
        mShoppingSaleFragment = shoppingSaleFragment;
        mDataset = data;
        mCurrentUser = user;
        mSalesOrderLineDB = new SalesOrderLineDB(context, user);
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
            return 0;
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(mDataset==null || mDataset.get(position) == null){
            return;
        }

        if(mDataset.get(position).getProduct().getImageFileName()!=null){
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
                        //.into(holder.productImage, new com.squareup.picasso.Callback() {
                        //    @Override
                        //    public void onSuccess() {
                        //        Utils.createFileInThumbDir(mDataset.get(holder.getAdapterPosition()).getProduct().getImageFileName(),
                        //                ((BitmapDrawable) holder.productImage.getDrawable()).getBitmap(),
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
                holder.productBrand.setVisibility(TextView.VISIBLE);
            }else{
                holder.productBrand.setVisibility(TextView.GONE);
            }
        }

        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setMessage(mContext.getString(R.string.delete_from_shopping_sale_question,
                                mDataset.get(holder.getAdapterPosition()).getProduct().getName()))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String result = mSalesOrderLineDB.deactiveSalesOrderLine(mDataset.get(holder.getAdapterPosition()));
                                if(result == null){
                                    mDataset.remove(holder.getAdapterPosition());
                                    notifyDataSetChanged();
                                    mShoppingSaleFragment.reloadShoppingSale();
                                    mShoppingSaleFragment.reloadShoppingSalesList();
                                } else {
                                    Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        holder.productPrice.setText(String.valueOf(mDataset.get(position).getPrice()));
        holder.productPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShoppingSaleFragment.updateSalesOrderLine(mDataset.get(holder.getAdapterPosition()), FOCUS_PRICE);
            }
        });

        holder.productTaxPercentage.setText(String.valueOf(mDataset.get(position).getTaxPercentage()));
        holder.productTaxPercentage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShoppingSaleFragment.updateSalesOrderLine(mDataset.get(holder.getAdapterPosition()), FOCUS_TAX_PERCENTAGE);
            }
        });

        holder.qtyOrdered.setText(String.valueOf(mDataset.get(position).getQuantityOrdered()));
        holder.qtyOrdered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShoppingSaleFragment.updateSalesOrderLine(mDataset.get(holder.getAdapterPosition()), FOCUS_QTY_ORDERED);
            }
        });

        holder.totalLineAmount.setText(String.valueOf(mDataset.get(position).getTotalLineAmount()));
    }

    private void goToProductDetails(Product product) {
        Intent intent = new Intent(mContext, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.KEY_PRODUCT_ID, product.getId());
        mContext.startActivity(intent);
    }

    public void setData(ArrayList<SalesOrderLine> salesOrderLines) {
        this.mDataset = salesOrderLines;
        notifyDataSetChanged();
    }
}
