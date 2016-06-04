package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.BusinessPartner;

import java.util.ArrayList;

/**
 * Created by Alberto on 7/4/2016.
 */
public class BusinessPartnertsListAdapter extends BaseAdapter {

    private Context mContext;
    private User mCurrentUser;
    private ArrayList<BusinessPartner> mDataset;

    public BusinessPartnertsListAdapter(Context context, ArrayList<BusinessPartner> data, User user) {
        mContext = context;
        mDataset = data;
        mCurrentUser = user;
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.busineess_partner_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.businessPartnerName.setText(mDataset.get(position).getName());
        viewHolder.businessPartnerTaxId.setText(mContext.getString(R.string.tax_id, mDataset.get(position).getTaxId()));

        view.setTag(viewHolder);
        return view;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        // each data item is just a string in this case
        public TextView businessPartnerName;
        public TextView businessPartnerTaxId;

        public ViewHolder(View v) {
            businessPartnerName = (TextView) v.findViewById(R.id.business_partner_name);
            businessPartnerTaxId = (TextView) v.findViewById(R.id.business_partner_tax_id);
        }
    }
}