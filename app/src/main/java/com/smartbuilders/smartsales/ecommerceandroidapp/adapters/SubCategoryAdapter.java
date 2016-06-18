package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductSubCategory;

import java.util.ArrayList;

/**
 * Created by Alberto on 22/4/2016.
 */
public class SubCategoryAdapter  extends BaseAdapter {

    private Context mContext;
    private ArrayList<ProductSubCategory> mDataset;

    public SubCategoryAdapter(Context context, ArrayList<ProductSubCategory> data) {
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
        View view = convertView;
        ViewHolder viewHolder;
        if(view==null){//si la vista es null la crea sino la reutiliza
            view = LayoutInflater.from(mContext).inflate(R.layout.subcategory_list_item, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        if(!TextUtils.isEmpty(mDataset.get(position).getName())){
            viewHolder.categoryName.setText(mDataset.get(position).getName());
        }else{
            viewHolder.categoryName.setVisibility(TextView.GONE);
        }
        if(!TextUtils.isEmpty(mDataset.get(position).getDescription())){
            viewHolder.categoryDescription.setText(mDataset.get(position).getDescription());
        }else{
            viewHolder.categoryDescription.setVisibility(TextView.GONE);
        }
        if(mDataset.get(position).getImageId()>0){
            viewHolder.categoryImage.setImageResource(mDataset.get(position).getImageId());
        }else{
            viewHolder.categoryImage.setVisibility(ImageView.GONE);
        }

        viewHolder.productsActiveQty.setText(mContext.getString(R.string.products_availables,
                String.valueOf(mDataset.get(position).getProductsActiveQty())));

        return view;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        // each data item is just a string in this case
        public TextView categoryName;
        public TextView categoryDescription;
        public ImageView categoryImage;
        public TextView productsActiveQty;

        public ViewHolder(View v) {
            categoryName = (TextView) v.findViewById(R.id.category_name_textView);
            categoryDescription = (TextView) v.findViewById(R.id.category_description_textView);
            categoryImage = (ImageView) v.findViewById(R.id.category_imageView);
            productsActiveQty = (TextView) v.findViewById(R.id.products_active_qty);
        }
    }
}
