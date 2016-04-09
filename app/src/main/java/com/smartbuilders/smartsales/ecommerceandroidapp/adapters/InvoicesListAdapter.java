package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.smartbuilders.smartsales.ecommerceandroidapp.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Invoice;

import java.util.ArrayList;

/**
 * Created by Alberto on 7/4/2016.
 */
public class InvoicesListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Invoice> mDataset;

    public InvoicesListAdapter(Context context, ArrayList<Invoice> data) {
        mContext = context;
        mDataset = data;
    }

    @Override
    public int getCount() {
        return mDataset.size();
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.invoice_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);


        view.setTag(viewHolder);
        return view;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        // each data item is just a string in this case


        public ViewHolder(View v) {

        }
    }
}
