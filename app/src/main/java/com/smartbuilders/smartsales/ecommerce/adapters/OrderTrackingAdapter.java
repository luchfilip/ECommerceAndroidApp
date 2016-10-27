package com.smartbuilders.smartsales.ecommerce.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
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

        if (mOrderTrackings.get(position).getOrderTrackingState().getIconResName() != null) {
            try {
                holder.iconImageView.setImageResource(mContext.getResources()
                        .getIdentifier(mOrderTrackings.get(position).getOrderTrackingState().getIconResName(),
                                "drawable", mContext.getPackageName()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mOrderTrackings.get(position).getCreated() != null) {
            //Cambia el color al titulo
            if (mOrderTrackings.get(position).getOrderTrackingState().getTitle_R_Color() >= 0
                    && mOrderTrackings.get(position).getOrderTrackingState().getTitle_G_Color() >= 0
                    && mOrderTrackings.get(position).getOrderTrackingState().getTitle_B_Color() >= 0) {
                holder.titleTextView.setTextColor(Color.rgb(mOrderTrackings.get(position).getOrderTrackingState().getTitle_R_Color(),
                        mOrderTrackings.get(position).getOrderTrackingState().getTitle_G_Color(),
                        mOrderTrackings.get(position).getOrderTrackingState().getTitle_B_Color()));
            } else {
                holder.titleTextView.setTextColor(Utils.getColor(mContext, R.color.black));
            }

            //Muestra la imagen de check en la cartica
            holder.checkImageView.setVisibility(View.VISIBLE);

            //Cambia el color a la imagen
            if (mOrderTrackings.get(position).getOrderTrackingState().getIcon_R_Color() >= 0
                    && mOrderTrackings.get(position).getOrderTrackingState().getIcon_G_Color() >= 0
                    && mOrderTrackings.get(position).getOrderTrackingState().getIcon_B_Color() >= 0) {
                holder.iconImageView.setColorFilter(Color.rgb(mOrderTrackings.get(position).getOrderTrackingState().getIcon_R_Color(),
                        mOrderTrackings.get(position).getOrderTrackingState().getIcon_G_Color(),
                        mOrderTrackings.get(position).getOrderTrackingState().getIcon_B_Color()));
            } else {
                holder.iconImageView.setColorFilter(Utils.getColor(mContext, R.color.colorPrimary));
            }

            //Muestra el detalle del tracking
            holder.detailsTextView.setText(mOrderTrackings.get(position).getDetails());

            //muestra la fecha de creacion del tracking
            holder.dateTextView.setText(mOrderTrackings.get(position).getCreatedStringFormat());

            /********************************************************************************/
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setCornerRadius(Utils.convertDpToPixel(4, mContext));
            //cambia el color del fondo de la cartica
            if (mOrderTrackings.get(position).getOrderTrackingState().getBackground_R_Color() >= 0
                    && mOrderTrackings.get(position).getOrderTrackingState().getBackground_G_Color() >= 0
                    && mOrderTrackings.get(position).getOrderTrackingState().getBackground_B_Color() >= 0) {
                shape.setColor(Color.rgb(mOrderTrackings.get(position).getOrderTrackingState().getBackground_R_Color(),
                        mOrderTrackings.get(position).getOrderTrackingState().getBackground_G_Color(),
                        mOrderTrackings.get(position).getOrderTrackingState().getBackground_B_Color()));
            } else {
                shape.setColor(Utils.getColor(mContext, android.R.color.white));
            }

            //cambia el color del borde de la cartica
            if (mOrderTrackings.get(position).getOrderTrackingState().getBorder_R_Color() >= 0
                    && mOrderTrackings.get(position).getOrderTrackingState().getBorder_G_Color() >= 0
                    && mOrderTrackings.get(position).getOrderTrackingState().getBorder_B_Color() >= 0) {
                shape.setStroke(Utils.convertDpToPixel(1, mContext),
                        Color.rgb(mOrderTrackings.get(position).getOrderTrackingState().getBorder_R_Color(),
                        mOrderTrackings.get(position).getOrderTrackingState().getBorder_G_Color(),
                        mOrderTrackings.get(position).getOrderTrackingState().getBorder_B_Color()));
            } else {
                shape.setStroke(Utils.convertDpToPixel(1, mContext), Utils.getColor(mContext, R.color.colorPrimary));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.containerLinearLayout.setBackground(shape);
            } else {
                holder.containerLinearLayout.setBackgroundDrawable(shape);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.containerLinearLayout.setElevation(5);
            }
            /********************************************************************************/
        }
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
