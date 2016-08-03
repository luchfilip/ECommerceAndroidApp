package com.smartbuilders.smartsales.ecommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smartbuilders.ids.model.User;
import com.smartbuilders.ids.model.UserProfile;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrder;

import java.util.ArrayList;

/**
 * Created by Alberto on 7/4/2016.
 */
public class SalesOrdersListAdapter extends BaseAdapter {

    private Context mContext;
    private User mUser;
    private ArrayList<SalesOrder> mDataset;

    public SalesOrdersListAdapter(Context context, User user, ArrayList<SalesOrder> data) {
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
        return mDataset.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDataset.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;
        if(view==null){//si la vista es null la crea sino la reutiliza
            view = LayoutInflater.from(mContext).inflate(R.layout.sales_orders_list_item, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.salesOrderNumber.setText(mContext.getString(R.string.sales_order_number, mDataset.get(position).getSalesOrderNumber()));
        viewHolder.salesOrderDate.setText(mContext.getString(R.string.order_date, mDataset.get(position).getCreatedStringFormat()));
        viewHolder.salesOrderLinesNumber.setText(mContext.getString(R.string.order_lines_number,
                String.valueOf(mDataset.get(position).getLinesNumber())));
        if(mUser!=null && mUser.getUserProfileId()== UserProfile.BUSINESS_PARTNER_PROFILE_ID){
            viewHolder.businessPartnerCommercialName.setText(mContext.getString(R.string.business_partner_detail,
                    mDataset.get(position).getBusinessPartner().getCommercialName()));
            viewHolder.businessPartnerCommercialName.setVisibility(View.VISIBLE);
        }else{
            viewHolder.businessPartnerCommercialName.setVisibility(View.GONE);
        }

        return view;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        // each data item is just a string in this case
        public TextView businessPartnerCommercialName;
        public TextView salesOrderNumber;
        public TextView salesOrderDate;
        public TextView salesOrderLinesNumber;

        public ViewHolder(View v) {
            businessPartnerCommercialName = (TextView) v.findViewById(R.id.business_partner_commercial_name_textView);
            salesOrderNumber = (TextView) v.findViewById(R.id.sales_order_number_tv);
            salesOrderDate = (TextView) v.findViewById(R.id.sales_order_date_tv);
            salesOrderLinesNumber = (TextView) v.findViewById(R.id.sales_order_lines_number_tv);
        }
    }

    public void setData(ArrayList<SalesOrder> salesOrders) {
        mDataset = salesOrders;
        notifyDataSetChanged();
    }
}