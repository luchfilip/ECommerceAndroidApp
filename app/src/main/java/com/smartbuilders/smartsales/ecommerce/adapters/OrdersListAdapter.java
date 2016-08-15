package com.smartbuilders.smartsales.ecommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.model.UserProfile;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.model.Order;

import java.util.ArrayList;

/**
 * Created by Alberto on 7/4/2016.
 */
public class OrdersListAdapter extends BaseAdapter {

    private Context mContext;
    private User mUser;
    private ArrayList<Order> mDataset;

    public OrdersListAdapter(Context context, User user, ArrayList<Order> data) {
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
            view = LayoutInflater.from(mContext).inflate(R.layout.orders_list_item, parent, false);
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
            viewHolder.dividerView.setVisibility(View.VISIBLE);
            if (mUser!=null && mUser.getUserProfileId()== UserProfile.BUSINESS_PARTNER_PROFILE_ID) {
                viewHolder.businessPartnerName.setText(mDataset.get(position).getBusinessPartner().getName());
                viewHolder.businessPartnerName.setVisibility(View.VISIBLE);
            }
        }else{
            viewHolder.salesOrderNumber.setVisibility(View.GONE);
            viewHolder.businessPartnerName.setVisibility(View.GONE);
            viewHolder.dividerView.setVisibility(View.GONE);
        }

        viewHolder.orderNumber.setText(mContext.getString(R.string.order_number, mDataset.get(position).getOrderNumber()));
        viewHolder.orderDate.setText(mContext.getString(R.string.order_date, mDataset.get(position).getCreatedStringFormat()));
        viewHolder.orderLinesNumber.setText(mContext.getString(R.string.order_lines_number,
                String.valueOf(mDataset.get(position).getLinesNumber())));

        return view;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        // each data item is just a string in this case
        public TextView salesOrderNumber;
        public TextView businessPartnerName;
        public View dividerView;
        public TextView orderNumber;
        public TextView orderDate;
        public TextView orderLinesNumber;

        public ViewHolder(View v) {
            salesOrderNumber = (TextView) v.findViewById(R.id.sales_order_number_tv);
            businessPartnerName = (TextView) v.findViewById(R.id.business_partner_commercial_name_tv);
            dividerView = v.findViewById(R.id.divider_view);
            orderNumber = (TextView) v.findViewById(R.id.order_number_tv);
            orderDate = (TextView) v.findViewById(R.id.order_date_tv);
            orderLinesNumber = (TextView) v.findViewById(R.id.order_lines_number_tv);
        }
    }

    public void setData (ArrayList<Order> orders){
        mDataset = orders;
        notifyDataSetChanged();
    }
}