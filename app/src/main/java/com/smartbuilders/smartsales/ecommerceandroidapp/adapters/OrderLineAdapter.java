package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
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
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Alberto on 8/4/2016.
 */
public class OrderLineAdapter extends RecyclerView.Adapter<OrderLineAdapter.ViewHolder> {

    private ArrayList<OrderLine> mDataset;
    private Context mContext;
    private User mCurrentUser;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView productName;
        public TextView productBrand;
        public TextView productCommercialPackage;
        public ImageView productImage;
        public TextView qtyOrdered;

        public ViewHolder(View v) {
            super(v);
            productName = (TextView) v.findViewById(R.id.product_name);
            productBrand = (TextView) v.findViewById(R.id.product_brand);
            productCommercialPackage = (TextView) v.findViewById(R.id.product_commercial_package);
            productImage = (ImageView) v.findViewById(R.id.product_image);
            qtyOrdered = (TextView) v.findViewById(R.id.qty_requested_textView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public OrderLineAdapter(ArrayList<OrderLine> myDataset, User user) {
        mDataset = myDataset;
        mCurrentUser = user;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public OrderLineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        mContext = parent.getContext();
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_line_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.productName.setText(mDataset.get(position).getProduct().getName());

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
                        .into(holder.productImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                Utils.createFileInThumbDir(mDataset.get(position).getProduct().getImageFileName(),
                                        ((BitmapDrawable)holder.productImage.getDrawable()).getBitmap(),
                                        mCurrentUser, mContext);
                            }

                            @Override
                            public void onError() {
                            }
                        });
            }
        }else{
            holder.productImage.setImageResource(R.drawable.no_image_available);
        }

        if (mDataset.get(position).getProduct().getProductCommercialPackage() != null) {
            holder.productCommercialPackage.setText(mContext.getString(R.string.commercial_package,
                    (mDataset.get(position).getProduct().getProductCommercialPackage().getUnits() + " " +
                            (mDataset.get(position).getProduct().getProductCommercialPackage().getUnitDescription()))));
        } else {
            holder.productCommercialPackage.setVisibility(View.INVISIBLE);
        }

        if (mDataset.get(position).getProduct().getProductBrand() != null
                && mDataset.get(position).getProduct().getProductBrand().getDescription() != null) {
            holder.productBrand.setText(mContext.getString(R.string.brand_detail,
                    mDataset.get(position).getProduct().getProductBrand().getDescription()));
        } else {
            holder.productBrand.setVisibility(View.INVISIBLE);
        }

        holder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.KEY_PRODUCT_ID, mDataset.get(position).getProduct().getId());
                mContext.startActivity(intent);
            }
        });

        holder.qtyOrdered.setText(mContext.getString(R.string.qty_ordered,
                String.valueOf(mDataset.get(position).getQuantityOrdered())));
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