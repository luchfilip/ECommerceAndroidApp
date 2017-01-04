package com.smartbuilders.smartsales.ecommerce.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.BusinessPartnersListActivity;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.data.UserBusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerce.model.UserBusinessPartner;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by Alberto on 7/4/2016.
 */
public class BusinessPartnersListAdapter extends BaseAdapter {

    // Regular expression in Java to check if String is number or not
    private static final Pattern patternIsNotNumeric = Pattern.compile(".*[^0-9].*");

    final private Context mContext;
    final private User mUser;
    private ArrayList<BusinessPartner> mDataset;
    private ArrayList<BusinessPartner> filterAux;
    private int mAppCurrentBusinessPartnerId;

    public BusinessPartnersListAdapter(Context context, User user, ArrayList<? extends BusinessPartner> data,
                                       int appCurrentBusinessPartnerId) {
        mContext = context;
        mUser = user;
        mDataset = new ArrayList<>();
        if (data!=null) {
            mDataset.addAll(data);
        }
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

        if (mDataset.get(position) instanceof UserBusinessPartner) {
            viewHolder.imageView.setImageResource(R.drawable.ic_highlight_off_black);
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(mContext)
                            .setMessage(mContext.getString(R.string.delete_business_partner, mDataset.get(position).getName()))
                            .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String result = (new UserBusinessPartnerDB(mContext, mUser)).deactivateUserBusinessPartner(mDataset.get(position).getId());
                                    if (result==null) {
                                        ((BusinessPartnersListActivity) mContext).reloadBusinessPartnersList();
                                    } else {
                                        Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .show();
                }
            });
        } else {
            if (mDataset.get(position).getId() == mAppCurrentBusinessPartnerId) {
                viewHolder.imageView.setColorFilter(Utils.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, mContext.getString(R.string.session_loaded_detail,
                                mDataset.get(position).getName()), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                viewHolder.imageView.setColorFilter(Utils.getColor(mContext, R.color.dark_grey), PorterDuff.Mode.SRC_ATOP);
                viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(mContext)
                                .setMessage(mContext.getString(R.string.init_session_business_partner_question, mDataset.get(position).getName()))
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        mAppCurrentBusinessPartnerId = mDataset.get(position).getId();
                                        Utils.setAppCurrentBusinessPartnerId(mContext, mDataset.get(position).getId());
                                        Toast.makeText(mContext, mContext.getString(R.string.session_loaded_detail,
                                                mDataset.get(position).getName()), Toast.LENGTH_SHORT).show();
                                        notifyDataSetChanged();
                                    }
                                })
                                .setNegativeButton(R.string.no, null)
                                .show();
                    }
                });
            }
        }

        return view;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        // each data item is just a string in this case
        final TextView businessPartnerCommercialName;
        final TextView businessPartnerName;
        final TextView businessPartnerTaxId;
        final TextView businessPartnerInternalCode;
        final ImageView imageView;

        public ViewHolder(View v) {
            businessPartnerCommercialName = (TextView) v.findViewById(R.id.business_partner_commercial_name_textView);
            businessPartnerName = (TextView) v.findViewById(R.id.business_partner_name_textView);
            businessPartnerTaxId = (TextView) v.findViewById(R.id.business_partner_tax_id_textView);
            businessPartnerInternalCode = (TextView) v.findViewById(R.id.business_partner_internal_code_textView);
            imageView = (ImageView) v.findViewById(R.id.imageView);
        }
    }

    public void setData(ArrayList<? extends BusinessPartner> businessPartners) {
        if (businessPartners==null) {
            mDataset = null;
        } else {
            mDataset = new ArrayList<>();
            mDataset.addAll(businessPartners);
        }
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