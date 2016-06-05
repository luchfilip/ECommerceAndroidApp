package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Order;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.SalesOrder;

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
        return mDataset.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDataset.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.shopping_sales_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);


        view.setTag(viewHolder);
        return view;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        // each data item is just a string in this case
        public TextView businessPartnerName;
        public TextView shoppingSaleLinesNumber;
        public TextView shoppingSaleSubTotal;

        public ViewHolder(View v) {
            businessPartnerName = (TextView) v.findViewById(R.id.business_partner_name_textView);
            shoppingSaleLinesNumber = (TextView) v.findViewById(R.id.shopping_sale_lines_number_textView);
            shoppingSaleSubTotal = (TextView) v.findViewById(R.id.shopping_sale_sub_total_textView);
        }
    }
}