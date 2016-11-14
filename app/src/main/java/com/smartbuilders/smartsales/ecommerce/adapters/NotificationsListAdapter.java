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

import com.smartbuilders.smartsales.ecommerce.OrderTrackingDetailActivity;
import com.smartbuilders.smartsales.ecommerce.ProductDetailActivity;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.data.OrderTrackingDB;
import com.smartbuilders.smartsales.ecommerce.data.ProductDB;
import com.smartbuilders.smartsales.ecommerce.model.NotificationHistory;
import com.smartbuilders.smartsales.ecommerce.model.OrderTracking;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;

import java.util.ArrayList;

/**
 * Created by Alberto on 7/4/2016.
 */
public class NotificationsListAdapter extends RecyclerView.Adapter<NotificationsListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<NotificationHistory> mDataset;
    private User mUser;


    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View dividerGroupLine;
        public TextView notificationGroupCreateDate;
        public ImageView notificationImage;
        public TextView notificationTitle;
        public TextView notificationMessage;
        public TextView notificationCreateTime;
        public View containerLayout;

        public ViewHolder(View v) {
            super(v);
            dividerGroupLine = v.findViewById(R.id.divider_line);
            notificationGroupCreateDate = (TextView) v.findViewById(R.id.notification_group_created_date);
            notificationImage = (ImageView) v.findViewById(R.id.notification_image);
            notificationTitle = (TextView) v.findViewById(R.id.notification_title);
            notificationMessage = (TextView) v.findViewById(R.id.notification_message);
            notificationCreateTime = (TextView) v.findViewById(R.id.notification_create_time);
            containerLayout = v.findViewById(R.id.container_layout);
        }
    }

    public NotificationsListAdapter(Context context, ArrayList<NotificationHistory> data, User user) {
        mContext = context;
        mDataset = data;
        mUser = user;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NotificationsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_list_item, parent, false);
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
        if (position==0 || (position>0 && !mDataset.get(position).getCreatedDateStringFormat()
                .equals(mDataset.get(position-1).getCreatedDateStringFormat()))) {
            holder.dividerGroupLine.setVisibility(position==0 ? View.GONE : View.VISIBLE);
            holder.notificationGroupCreateDate.setText(mDataset.get(position).getCreatedDateStringFormat());
            holder.notificationGroupCreateDate.setVisibility(View.VISIBLE);
        } else {
            holder.dividerGroupLine.setVisibility(View.GONE);
            holder.notificationGroupCreateDate.setVisibility(View.GONE);
        }

        holder.notificationTitle.setText(mDataset.get(position).getTitle());
        holder.notificationMessage.setText(mDataset.get(position).getMessage());
        holder.notificationCreateTime.setText(mDataset.get(position).getCreatedTimeStringFormat());

        if (mDataset.get(position).getRelatedId()!=0) {
            if (mDataset.get(position).getType()==1) {
                final Product product = (new ProductDB(mContext, mUser))
                        .getProductById(mDataset.get(position).getRelatedId());
                if (product != null) {
                    Utils.loadThumbImageByFileName(mContext, mUser, product.getImageFileName(), holder.notificationImage);
                    holder.containerLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mContext.startActivity(new Intent(mContext, ProductDetailActivity.class)
                                    .putExtra(ProductDetailActivity.KEY_PRODUCT, product));
                        }
                    });
                } else {
                    holder.notificationImage.setImageDrawable(Utils.getNoImageAvailableDrawable(mContext));
                    holder.containerLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(mContext, mContext.getString(R.string.no_product_details), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else if (mDataset.get(position).getType()==2) {
                final OrderTracking orderTracking = (new OrderTrackingDB(mContext, mUser))
                        .getOrderTracking(mDataset.get(position).getRelatedId());
                if (orderTracking!=null) {
                    holder.notificationImage.setImageResource(mContext.getResources()
                            .getIdentifier(orderTracking.getOrderTrackingState().getIconResName(),
                                    "drawable", mContext.getPackageName()));
                    holder.containerLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mContext.startActivity(new Intent(mContext, OrderTrackingDetailActivity.class)
                                    .putExtra(OrderTrackingDetailActivity.KEY_ORDER_ID, orderTracking.getOrderId()));
                        }
                    });
                } else {
                    holder.notificationImage.setImageDrawable(Utils.getNoImageAvailableDrawable(mContext));
                    holder.containerLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(mContext, mContext.getString(R.string.no_order_tracking_details), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        } else {
            holder.notificationImage.setImageDrawable(Utils.getNoImageAvailableDrawable(mContext));
        }
    }

    public void setData(ArrayList<NotificationHistory> notificationHistories) {
        mDataset = notificationHistories;
        notifyDataSetChanged();
    }
}