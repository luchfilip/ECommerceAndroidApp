package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.BusinessPartner;

import java.util.ArrayList;

/**
 * Created by Alberto on 7/4/2016.
 */
public class BusinessPartnersListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<BusinessPartner> mDataset;

    public BusinessPartnersListAdapter(Context context, ArrayList<BusinessPartner> data) {
        mContext = context;
        mDataset = data;
    }

    @Override
    public int getCount() {
        try {
            return mDataset.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.business_partner_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.businessPartnerCommercialName.setText(mDataset.get(position).getCommercialName());
        viewHolder.businessPartnerTaxId.setText(mContext.getString(R.string.tax_id, mDataset.get(position).getTaxId()));

        view.setTag(viewHolder);
        return view;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        // each data item is just a string in this case
        public TextView businessPartnerCommercialName;
        public TextView businessPartnerTaxId;

        public ViewHolder(View v) {
            businessPartnerCommercialName = (TextView) v.findViewById(R.id.business_partner_commercial_name_textView);
            businessPartnerTaxId = (TextView) v.findViewById(R.id.business_partner_tax_id_textView);
        }
    }

    public void setData(ArrayList<BusinessPartner> businessPartners) {
        mDataset = businessPartners;
        notifyDataSetChanged();
    }
}