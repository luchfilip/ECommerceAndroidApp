package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductDetailActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductDetailFragment;
import com.smartbuilders.smartsales.ecommerceandroidapp.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Alberto on 8/4/2016.
 */
public class SalesOrderLineAdapter extends RecyclerView.Adapter<SalesOrderLineAdapter.ViewHolder> {

    private ArrayList<OrderLine> mDataset;
    private Context mContext;
    private User mCurrentUser;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView productName;
        public ImageView productImage;
        public TextView qtyOrdered;

        public ViewHolder(View v) {
            super(v);
            productName = (TextView) v.findViewById(R.id.product_name);
            productImage = (ImageView) v.findViewById(R.id.product_image);
            qtyOrdered = (TextView) v.findViewById(R.id.qty_requested_textView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SalesOrderLineAdapter(ArrayList<OrderLine> myDataset, User user) {
        mDataset = myDataset;
        mCurrentUser = user;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SalesOrderLineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        mContext = parent.getContext();
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sales_order_line_item, parent, false);
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
        if(mDataset.get(position).getProduct().getImageFileName()!=null){
            Bitmap img = Utils.getThumbByFileName(mContext, mCurrentUser, mDataset.get(position).getProduct().getImageFileName());
            if(img!=null){
                holder.productImage.setImageBitmap(img);
            }else{
                holder.productImage.setImageResource(mDataset.get(position).getProduct().getImageId());
            }
        }else{
            holder.productImage.setImageResource(mDataset.get(position).getProduct().getImageId());
        }

        holder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.KEY_CURRENT_USER, mCurrentUser);
                intent.putExtra(ProductDetailFragment.KEY_PRODUCT, mDataset.get(position).getProduct());
                mContext.startActivity(intent);
            }
        });

        holder.qtyOrdered.setText(mContext.getString(R.string.qty_ordered, String.valueOf(mDataset.get(position).getQuantityOrdered())));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}