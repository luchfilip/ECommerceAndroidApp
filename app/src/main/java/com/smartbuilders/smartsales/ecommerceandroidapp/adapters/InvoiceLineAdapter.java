package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.InvoiceLine;

import java.util.ArrayList;

/**
 * Created by Alberto on 8/4/2016.
 */
public class InvoiceLineAdapter extends RecyclerView.Adapter<InvoiceLineAdapter.ViewHolder> {

    private ArrayList<InvoiceLine> mDataset;
    private Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView productName;
        public ImageView productImage;

        public ViewHolder(View v) {
            super(v);
            productName = (TextView) v.findViewById(R.id.product_name);
            productImage = (ImageView) v.findViewById(R.id.product_image);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public InvoiceLineAdapter(ArrayList<InvoiceLine> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public InvoiceLineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        mContext = parent.getContext();
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.invoice_line_item, parent, false);
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
        holder.productImage.setImageResource(R.drawable.no_image_available);
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