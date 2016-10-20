package com.smartbuilders.smartsales.ecommerce.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by Alberto on 7/4/2016.
 */
public class BusinessPartnersListAdapter extends BaseAdapter {

    // Regular expression in Java to check if String is number or not
    private static final Pattern patternIsNotNumeric = Pattern.compile(".*[^0-9].*");

    private Context mContext;
    private ArrayList<BusinessPartner> mDataset;
    private ArrayList<BusinessPartner> filterAux;
    private int mAppCurrentBusinessPartnerId;

    public BusinessPartnersListAdapter(Context context, ArrayList<BusinessPartner> data,
                                       int appCurrentBusinessPartnerId) {
        mContext = context;
        mDataset = data;
        filterAux = new ArrayList<>();
        filterAux.addAll(mDataset);
        mAppCurrentBusinessPartnerId = appCurrentBusinessPartnerId;
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
        View view = convertView;
        ViewHolder viewHolder;
        if(view==null){//si la vista es null la crea sino la reutiliza
            view = LayoutInflater.from(mContext).inflate(R.layout.business_partner_list_item, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        if(!TextUtils.isEmpty(mDataset.get(position).getInternalCode())){
            viewHolder.businessPartnerInternalCode.setText(mContext.
                    getString(R.string.business_partner_internal_code_detail, mDataset.get(position).getInternalCode()));
            viewHolder.businessPartnerInternalCode.setVisibility(View.VISIBLE);
        }else{
            viewHolder.businessPartnerInternalCode.setVisibility(View.GONE);
        }

        viewHolder.businessPartnerCommercialName.setText(mDataset.get(position).getCommercialName());
        viewHolder.businessPartnerName.setText(mDataset.get(position).getName());
        viewHolder.businessPartnerTaxId.setText(mContext.getString(R.string.tax_id, mDataset.get(position).getTaxId()));

        if(mDataset.get(position).getId() == mAppCurrentBusinessPartnerId){
            viewHolder.appCurrentBusinessPartnerIndicator.setColorFilter(Utils.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            viewHolder.appCurrentBusinessPartnerIndicator.setVisibility(View.VISIBLE);
        }else{
            viewHolder.appCurrentBusinessPartnerIndicator.setVisibility(View.GONE);
        }

        return view;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        // each data item is just a string in this case
        public TextView businessPartnerCommercialName;
        public TextView businessPartnerName;
        public TextView businessPartnerTaxId;
        public TextView businessPartnerInternalCode;
        public ImageView appCurrentBusinessPartnerIndicator;

        public ViewHolder(View v) {
            businessPartnerCommercialName = (TextView) v.findViewById(R.id.business_partner_commercial_name_textView);
            businessPartnerName = (TextView) v.findViewById(R.id.business_partner_name_textView);
            businessPartnerTaxId = (TextView) v.findViewById(R.id.business_partner_tax_id_textView);
            businessPartnerInternalCode = (TextView) v.findViewById(R.id.business_partner_internal_code_textView);
            appCurrentBusinessPartnerIndicator = (ImageView) v.findViewById(R.id.app_current_business_partner_indicator_imageView);
        }
    }

    public void setData(ArrayList<BusinessPartner> businessPartners) {
        mDataset = businessPartners;
        filterAux = new ArrayList<>();
        filterAux.addAll(mDataset);
        notifyDataSetChanged();
    }

    public void setAppCurrentBusinessPartnerId(int appCurrentBusinessPartnerId){
        mAppCurrentBusinessPartnerId = appCurrentBusinessPartnerId;
    }

    /**
     *
     * @param currentBusinessPartnerId, se usa cuando se esta en twoPanel para no borrar el
     *                                  actual businessPartner que se muestra en el detalle
     * @param charText
     * @param filterBy
     */
    public void filter(Integer currentBusinessPartnerId, String charText, String filterBy) {
        if(charText == null || filterBy == null){
            return;
        }
        charText = charText.toLowerCase(Locale.getDefault());
        mDataset.clear();
        if (charText.length() == 0) {
            mDataset.addAll(filterAux);
        } else {
            if(filterBy.equals(mContext.getString(R.string.filter_by_business_partner_code))){
                if(charText.length()<8 && !patternIsNotNumeric.matcher(charText).matches()){
                    for (BusinessPartner businessPartner : filterAux) {
                        if ((!TextUtils.isEmpty(businessPartner.getInternalCode()) &&
                                businessPartner.getInternalCode().toLowerCase(Locale.getDefault()).startsWith(charText))
                                || (currentBusinessPartnerId!=null && businessPartner.getId()==currentBusinessPartnerId)) {
                            mDataset.add(businessPartner);
                        }
                    }
                }else{
                    if (currentBusinessPartnerId!=null) {
                        for (BusinessPartner businessPartner : filterAux) {
                            if (businessPartner.getId()==currentBusinessPartnerId) {
                                mDataset.add(businessPartner);
                                break;
                            }
                        }
                    } else {
                        mDataset.clear();
                    }
                }
            }else if(filterBy.equals(mContext.getString(R.string.filter_by_business_partner_name))){
                for (BusinessPartner businessPartner : filterAux) {
                    if ((!TextUtils.isEmpty(businessPartner.getName()) &&
                            businessPartner.getName().toLowerCase(Locale.getDefault()).contains(charText))
                            || (currentBusinessPartnerId!=null && businessPartner.getId()==currentBusinessPartnerId)) {
                        mDataset.add(businessPartner);
                    }
                }
            }else if(filterBy.equals(mContext.getString(R.string.filter_by_business_partner_commercial_name))){
                for (BusinessPartner businessPartner : filterAux) {
                    if ((!TextUtils.isEmpty(businessPartner.getCommercialName()) &&
                            businessPartner.getCommercialName().toLowerCase(Locale.getDefault()).contains(charText))
                            || (currentBusinessPartnerId!=null && businessPartner.getId()==currentBusinessPartnerId)) {
                        mDataset.add(businessPartner);
                    }
                }
            }else if(filterBy.equals(mContext.getString(R.string.filter_by_business_partner_tax_id))){
                for (BusinessPartner businessPartner : filterAux) {
                    if ((!TextUtils.isEmpty(businessPartner.getTaxId()) &&
                            businessPartner.getTaxId().toLowerCase(Locale.getDefault()).contains(charText))
                            || (currentBusinessPartnerId!=null && businessPartner.getId()==currentBusinessPartnerId)) {
                        mDataset.add(businessPartner);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
}