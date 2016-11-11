package com.smartbuilders.smartsales.ecommerce.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.model.Notification;
import com.smartbuilders.synchronizer.ids.model.User;

import java.util.ArrayList;

/**
 * Created by Alberto on 7/4/2016.
 */
public class NotificationsListAdapter extends RecyclerView.Adapter<NotificationsListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Notification> mDataset;
    private User mUser;


    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView notificationImage;
        public TextView notificationTitle;
        public TextView notificationMessage;
        public TextView notificationCreateTime;
        public View containerLayout;

        public ViewHolder(View v) {
            super(v);
            notificationImage = (ImageView) v.findViewById(R.id.notification_image);
            notificationTitle = (TextView) v.findViewById(R.id.notification_title);
            notificationMessage = (TextView) v.findViewById(R.id.notification_message);
            notificationCreateTime = (TextView) v.findViewById(R.id.notification_create_time);
            containerLayout = v.findViewById(R.id.container_layout);
        }
    }

    public NotificationsListAdapter(Context context, ArrayList<Notification> data, User user) {
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
        holder.notificationTitle.setText(mDataset.get(position).getTitle());

        holder.notificationMessage.setText(mDataset.get(position).getMessage());

        holder.notificationCreateTime.setText(mDataset.get(position).getCreatedStringFormat());

        holder.containerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void setData(ArrayList<Notification> notifications) {
        mDataset = notifications;
        notifyDataSetChanged();
    }
}