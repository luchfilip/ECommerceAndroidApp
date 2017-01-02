package com.smartbuilders.smartsales.ecommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerce.SalesOrdersListActivity;
import com.smartbuilders.smartsales.ecommerce.SalesOrdersListFragment;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.model.UserProfile;
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
        viewHolder.salesOrderDate.setText(mContext.getString(R.string.sales_order_date, mDataset.get(position).getCreatedStringFormat()));
        viewHolder.salesOrderLinesNumber.setText(mContext.getString(R.string.order_lines_number,
                String.valueOf(mDataset.get(position).getLinesNumber())));
        if(mUser!=null && mUser.getUserProfileId()== UserProfile.BUSINESS_PARTNER_PROFILE_ID){
            viewHolder.businessPartnerName.setText(mContext.getString(R.string.business_partner_name_detail,
                    mDataset.get(position).getBusinessPartner().getName()));
            viewHolder.businessPartnerName.setVisibility(View.VISIBLE);
        }else{
            viewHolder.businessPartnerName.setVisibility(View.GONE);
        }

        viewHolder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof SalesOrdersListActivity) {
                    ((SalesOrdersListFragment.Callback) mContext).onItemLongSelected(mDataset.get(position),
                            (ListView) ((SalesOrdersListActivity) mContext).findViewById(R.id.sales_orders_list), mUser);
                }
            }
        });

        return view;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        // each data item is just a string in this case
        public TextView businessPartnerName;
        public TextView salesOrderNumber;
        public TextView salesOrderDate;
        public TextView salesOrderLinesNumber;
        public View deleteImageView;

        public ViewHolder(View v) {
            businessPartnerName = (TextView) v.findViewById(R.id.business_partner_commercial_name_textView);
            salesOrderNumber = (TextView) v.findViewById(R.id.sales_order_number_tv);
            salesOrderDate = (TextView) v.findViewById(R.id.sales_order_date_tv);
            salesOrderLinesNumber = (TextView) v.findViewById(R.id.sales_order_lines_number_tv);
            deleteImageView = v.findViewById(R.id.delete_imageView);
        }
    }

    public void setData(ArrayList<SalesOrder> salesOrders) {
        mDataset = salesOrders;
        notifyDataSetChanged();
    }
}