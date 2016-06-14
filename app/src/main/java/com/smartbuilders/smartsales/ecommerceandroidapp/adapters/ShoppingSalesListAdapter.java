package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.SalesOrder;

import java.util.ArrayList;

/**
 * Created by stein on 5/6/2016.
 */
public class ShoppingSalesListAdapter extends BaseAdapter {

    private ArrayList<SalesOrder> mDataset;

    public ShoppingSalesListAdapter(ArrayList<SalesOrder> data) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_sales_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.businessPartnerCommercialName.setText(mDataset.get(position).getBusinessPartner().getCommercialName());
        viewHolder.shoppingSaleLinesNumber.setText(parent.getContext().getString(R.string.order_lines_number, mDataset.get(position).getLinesNumber()));

        view.setTag(viewHolder);
        return view;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        // each data item is just a string in this case
        public TextView businessPartnerCommercialName;
        public TextView shoppingSaleLinesNumber;

        public ViewHolder(View v) {
            businessPartnerCommercialName = (TextView) v.findViewById(R.id.business_partner_commercial_name_textView);
            shoppingSaleLinesNumber = (TextView) v.findViewById(R.id.shopping_sale_lines_number_textView);
        }
    }
}