package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
public class OrderLineAdapter extends BaseAdapter {

    private ArrayList<OrderLine> mDataset;
    private User mCurrentUser;

    // Provide a suitable constructor (depends on the kind of dataset)
    public OrderLineAdapter(ArrayList<OrderLine> myDataset, User user) {
        mDataset = myDataset;
        mCurrentUser = user;
    }

    @Override
    public int getCount() {
        try {
            return mDataset.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public Object getItem(int position) {
        try {
            return mDataset.get(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_line_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.productName.setText(mDataset.get(position).getProduct().getName());

        if(!TextUtils.isEmpty(mDataset.get(position).getProduct().getImageFileName())){
            File img = Utils.getFileThumbByFileName(parent.getContext(), mCurrentUser,
                    mDataset.get(position).getProduct().getImageFileName());
            if(img!=null){
                Picasso.with(parent.getContext())
                        .load(img).error(R.drawable.no_image_available).into(viewHolder.productImage);
            }else{
                Picasso.with(parent.getContext())
                        .load(mCurrentUser.getServerAddress() + "/IntelligentDataSynchronizer/GetThumbImage?fileName="
                                + mDataset.get(position).getProduct().getImageFileName())
                        .error(R.drawable.no_image_available)
                        .into(viewHolder.productImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                Utils.createFileInThumbDir(mDataset.get(position).getProduct().getImageFileName(),
                                        ((BitmapDrawable) viewHolder.productImage.getDrawable()).getBitmap(),
                                        mCurrentUser, parent.getContext());
                            }

                            @Override
                            public void onError() {
                            }
                        });
            }
        }else{
            viewHolder.productImage.setImageResource(R.drawable.no_image_available);
        }

        if (mDataset.get(position).getProduct().getProductBrand() != null
                && mDataset.get(position).getProduct().getProductBrand().getDescription() != null) {
            viewHolder.productBrand.setText(parent.getContext().getString(R.string.brand_detail,
                    mDataset.get(position).getProduct().getProductBrand().getDescription()));
        } else {
            viewHolder.productBrand.setVisibility(View.INVISIBLE);
        }

        viewHolder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent.getContext(), ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.KEY_PRODUCT_ID, mDataset.get(position).getProduct().getId());
                parent.getContext().startActivity(intent);
            }
        });

        viewHolder.qtyOrdered.setText(parent.getContext().getString(R.string.qty_ordered,
                String.valueOf(mDataset.get(position).getQuantityOrdered())));

        view.setTag(viewHolder);
        return view;
    }

    //// Create new views (invoked by the layout manager)
    //@Override
    //public OrderLineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
    //                                                        int viewType) {
    //    mContext = parent.getContext();
    //    // create a new view
    //    View v = LayoutInflater.from(parent.getContext())
    //            .inflate(R.layout.order_line_item, parent, false);
    //    // set the view's size, margins, paddings and layout parameters
    //
    //    ViewHolder vh = new ViewHolder(v);
    //    return vh;
    //}

    //// Replace the contents of a view (invoked by the layout manager)
    //@Override
    //public void onBindViewHolder(final ViewHolder holder, final int position) {
    //    // - get element from your dataset at this position
    //    // - replace the contents of the view with that element
    //    holder.productName.setText(mDataset.get(position).getProduct().getName());
    //
    //    if(!TextUtils.isEmpty(mDataset.get(position).getProduct().getImageFileName())){
    //        File img = Utils.getFileThumbByFileName(mContext, mCurrentUser,
    //                mDataset.get(position).getProduct().getImageFileName());
    //        if(img!=null){
    //            Picasso.with(mContext)
    //                    .load(img).error(R.drawable.no_image_available).into(holder.productImage);
    //        }else{
    //            Picasso.with(mContext)
    //                    .load(mCurrentUser.getServerAddress() + "/IntelligentDataSynchronizer/GetThumbImage?fileName="
    //                            + mDataset.get(position).getProduct().getImageFileName())
    //                    .error(R.drawable.no_image_available)
    //                    .into(holder.productImage, new Callback() {
    //                        @Override
    //                        public void onSuccess() {
    //                            Utils.createFileInThumbDir(mDataset.get(position).getProduct().getImageFileName(),
    //                                    ((BitmapDrawable)holder.productImage.getDrawable()).getBitmap(),
    //                                    mCurrentUser, mContext);
    //                        }
    //
    //                        @Override
    //                        public void onError() {
    //                        }
    //                    });
    //        }
    //    }else{
    //        holder.productImage.setImageResource(R.drawable.no_image_available);
    //    }
    //
    //    if (mDataset.get(position).getProduct().getProductBrand() != null
    //            && mDataset.get(position).getProduct().getProductBrand().getDescription() != null) {
    //        holder.productBrand.setText(mContext.getString(R.string.brand_detail,
    //                mDataset.get(position).getProduct().getProductBrand().getDescription()));
    //    } else {
    //        holder.productBrand.setVisibility(View.INVISIBLE);
    //    }
    //
    //    holder.productImage.setOnClickListener(new View.OnClickListener() {
    //        @Override
    //        public void onClick(View v) {
    //            Intent intent = new Intent(mContext, ProductDetailActivity.class);
    //            intent.putExtra(ProductDetailActivity.KEY_PRODUCT_ID, mDataset.get(position).getProduct().getId());
    //            mContext.startActivity(intent);
    //        }
    //    });
    //
    //    holder.qtyOrdered.setText(mContext.getString(R.string.qty_ordered,
    //            String.valueOf(mDataset.get(position).getQuantityOrdered())));
    //}

    //// Return the size of your dataset (invoked by the layout manager)
    //@Override
    //public int getItemCount() {
    //    if (mDataset!=null) {
    //        return mDataset.size();
    //    }
    //    return 0;
    //}

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder /*extends RecyclerView.ViewHolder*/ {
        // each data item is just a string in this case
        public TextView productName;
        public TextView productBrand;
        public ImageView productImage;
        public TextView qtyOrdered;

        public ViewHolder(View v) {
            //super(v);
            productName = (TextView) v.findViewById(R.id.product_name);
            productBrand = (TextView) v.findViewById(R.id.product_brand);
            productImage = (ImageView) v.findViewById(R.id.product_image);
            qtyOrdered = (TextView) v.findViewById(R.id.qty_requested_textView);
        }
    }
}