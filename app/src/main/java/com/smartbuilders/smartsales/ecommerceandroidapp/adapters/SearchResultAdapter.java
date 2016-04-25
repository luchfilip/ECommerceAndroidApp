package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerceandroidapp.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;

import java.util.ArrayList;

/**
 * Created by Alberto on 27/3/2016.
 */
public class SearchResultAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Product> mDataset;

    public SearchResultAdapter(Context context, ArrayList<Product> data) {
        mContext = context;
        mDataset = data;
    }

    public void setData(ArrayList<Product> data){
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.search_result_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        if(!TextUtils.isEmpty(mDataset.get(position).getName())){
            viewHolder.productName.setText(mDataset.get(position).getName());
        }else{
            viewHolder.productName.setVisibility(TextView.GONE);
        }
        if(mDataset.get(position).getProductSubCategory()!=null &&
                !TextUtils.isEmpty(mDataset.get(position).getProductSubCategory().getDescription())){
            viewHolder.productGroup.setText(mDataset.get(position).getProductSubCategory().getDescription());
        }else{
            viewHolder.productGroup.setVisibility(TextView.GONE);
        }

        view.setTag(viewHolder);
        return view;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        // each data item is just a string in this case
        public TextView productName;
        public TextView productGroup;

        public ViewHolder(View v) {
            productName = (TextView) v.findViewById(R.id.product_name_textView);
            productGroup = (TextView) v.findViewById(R.id.product_group_textView);
        }
    }
}
