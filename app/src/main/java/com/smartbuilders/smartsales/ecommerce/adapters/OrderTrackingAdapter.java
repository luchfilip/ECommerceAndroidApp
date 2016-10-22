package com.smartbuilders.smartsales.ecommerce.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.model.OrderTracking;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.util.ArrayList;

/**
 * Created by AlbertoSarco on 18/10/2016.
 */

public class OrderTrackingAdapter extends RecyclerView.Adapter<OrderTrackingAdapter.ViewHolder> {

    private ArrayList<OrderTracking> mOrderTrackings;
    private Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public ImageView iconImageView;
        public ImageView checkImageView;
        public TextView titleTextView;
        public TextView detailsTextView;
        public TextView dateTextView;
        public LinearLayout containerLinearLayout;

        public ViewHolder(View v) {
            super(v);
            iconImageView = (ImageView) v.findViewById(R.id.icon_imageView);
            checkImageView = (ImageView) v.findViewById(R.id.check_imageView);
            titleTextView = (TextView) v.findViewById(R.id.title_textView);
            detailsTextView = (TextView) v.findViewById(R.id.details_textView);
            dateTextView = (TextView) v.findViewById(R.id.date_textView);
            containerLinearLayout = (LinearLayout) v.findViewById(R.id.container_linearLayout);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public OrderTrackingAdapter(Context context, ArrayList<OrderTracking> orderTrackings) {
        mContext = context;
        mOrderTrackings = orderTrackings;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_tracking_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.titleTextView.setText(mOrderTrackings.get(position).getOrderTrackingState().getTitle());

        if (mOrderTrackings.get(position).getCreated() != null) {
            holder.detailsTextView.setText(mOrderTrackings.get(position).getDetails());

            holder.dateTextView.setText(mOrderTrackings.get(position).getCreatedStringFormat());

            holder.titleTextView.setTextColor(Utils.getColor(mContext, R.color.black));
            holder.detailsTextView.setTextColor(Utils.getColor(mContext, R.color.black));
            holder.dateTextView.setTextColor(Utils.getColor(mContext, R.color.black));

            holder.checkImageView.setVisibility(View.VISIBLE);
        }

//        if (mOrderTrackingStates.get(position).getIconResName() != 0) {
//            holder.iconImageView.setImageResource(mOrderTrackingStates.get(position).getIconResName());
//            if (mOrderTrackingStates.get(position).getDate() != null) {
//                if (mOrderTrackingStates.get(position).isLastState()) {
//                    holder.iconImageView.setColorFilter(Utils.getColor(mContext, R.color.successDarkColor));
//                    holder.checkImageView.setColorFilter(Utils.getColor(mContext, R.color.successDarkColor));
//                } else {
//                    holder.iconImageView.setColorFilter(Utils.getColor(mContext, R.color.colorPrimary));
//                    holder.checkImageView.setColorFilter(Utils.getColor(mContext, R.color.colorPrimary));
//                }
//            } else {
//                holder.iconImageView.setColorFilter(Utils.getColor(mContext, R.color.dark_grey));
//            }
//        }

    }

    @Override
    public int getItemCount() {
        try {
            return mOrderTrackings.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        try {
            return mOrderTrackings.get(position).getOrderTrackingStateId();
        } catch (Exception e) {
            return 0;
        }
    }
}
