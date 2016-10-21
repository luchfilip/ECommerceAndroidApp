package com.smartbuilders.smartsales.ecommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.data.OrderTrackingDB;
import com.smartbuilders.smartsales.ecommerce.model.Order;
import com.smartbuilders.smartsales.ecommerce.model.OrderTracking;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;

import java.util.ArrayList;

/**
 * Created by Alberto on 7/4/2016.
 */
public class OrdersTrackingListAdapter extends BaseAdapter {

    private Context mContext;
    private User mUser;
    private ArrayList<Order> mDataset;

    public OrdersTrackingListAdapter(Context context, User user, ArrayList<Order> data) {
        mContext = context;
        mUser = user;
        mDataset = data;
    }

    @Override
    public int getCount() {
        if (mDataset!=null) {
            return mDataset.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        try {
            return mDataset.get(position);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        try {
            return mDataset.get(position).getId();
        } catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;
        if(view==null){//si la vista es null la crea sino la reutiliza
            view = LayoutInflater.from(mContext).inflate(R.layout.orders_tracking_list_item, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        if(mDataset.get(position).getSalesOrderId()>0
                && mDataset.get(position).getBusinessPartner()!=null){
            viewHolder.salesOrderNumber.setText(mContext.getString(R.string.sales_order_number,
                    mDataset.get(position).getSalesOrderNumber()));
            viewHolder.salesOrderNumber.setVisibility(View.VISIBLE);
        }else{
            viewHolder.salesOrderNumber.setVisibility(View.GONE);
        }

        viewHolder.orderNumber.setText(mContext.getString(R.string.order_number, mDataset.get(position).getOrderNumber()));

        OrderTracking orderTracking = (new OrderTrackingDB(mContext, mUser))
                .getMaxOrderTracking(mDataset.get(position).getBusinessPartnerId(), mDataset.get(position).getId());

        viewHolder.titleTextView.setText(orderTracking.getTitle());
        viewHolder.dateTextView.setText(orderTracking.getDateStringFormat());

        if (orderTracking.isLastState()) {
            viewHolder.iconImageView.setImageResource(R.drawable.ic_check_circle_white_48dp);
            viewHolder.iconImageView.setColorFilter(Utils.getColor(mContext, R.color.successDarkColor));
        } else {
            viewHolder.iconImageView.setImageResource(orderTracking.getImageResId());
            viewHolder.iconImageView.setColorFilter(Utils.getColor(mContext, R.color.colorPrimary));
        }
        return view;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        // each data item is just a string in this case
        public TextView salesOrderNumber;
        public TextView orderNumber;
        public ImageView iconImageView;
        public TextView titleTextView;
        public TextView dateTextView;

        public ViewHolder(View v) {
            salesOrderNumber = (TextView) v.findViewById(R.id.sales_order_number_tv);
            orderNumber = (TextView) v.findViewById(R.id.order_number_tv);
            iconImageView = (ImageView) v.findViewById(R.id.icon_imageView);
            titleTextView = (TextView) v.findViewById(R.id.title_textView);
            dateTextView = (TextView) v.findViewById(R.id.date_textView);
        }
    }

    public void setData (ArrayList<Order> orders){
        mDataset = orders;
        notifyDataSetChanged();
    }
}