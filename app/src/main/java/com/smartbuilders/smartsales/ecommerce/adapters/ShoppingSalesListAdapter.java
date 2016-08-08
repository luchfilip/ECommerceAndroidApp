package com.smartbuilders.smartsales.ecommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrder;

import java.util.ArrayList;

/**
 * Created by stein on 5/6/2016.
 */
public class ShoppingSalesListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<SalesOrder> mDataset;

    public ShoppingSalesListAdapter(Context context, ArrayList<SalesOrder> data) {
        mContext = context;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        try {
            return mDataset.get(position).getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;
        if(view==null){//si la vista es null la crea sino la reutiliza
            view = LayoutInflater.from(mContext).inflate(R.layout.shopping_sales_list_item, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.businessPartnerName.setText(mDataset.get(position).getBusinessPartner().getName());
        viewHolder.shoppingSaleLinesNumber.setText(mContext.getString(R.string.order_lines_number, mDataset.get(position).getLinesNumber()));

        return view;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        // each data item is just a string in this case
        public TextView businessPartnerName;
        public TextView shoppingSaleLinesNumber;

        public ViewHolder(View v) {
            businessPartnerName = (TextView) v.findViewById(R.id.business_partner_commercial_name_textView);
            shoppingSaleLinesNumber = (TextView) v.findViewById(R.id.shopping_sale_lines_number_textView);
        }
    }

    public void setData(ArrayList<SalesOrder> salesOrders) {
        mDataset = salesOrders;
        notifyDataSetChanged();
    }
}