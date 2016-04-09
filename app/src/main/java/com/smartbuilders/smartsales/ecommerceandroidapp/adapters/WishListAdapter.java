package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerceandroidapp.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.WishListLine;

import java.util.ArrayList;

/**
 * Created by Alberto on 7/4/2016.
 */
public class WishListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<WishListLine> mDataset;

    public WishListAdapter(Context context, ArrayList<WishListLine> data) {
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.wish_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.productImage.setImageResource(mDataset.get(position).getProduct().getImageId());

        viewHolder.productName.setText(mDataset.get(position).getProduct().getName());

        viewHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "deleteItem", Toast.LENGTH_SHORT).show();
            }
        });

        view.setTag(viewHolder);
        return view;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        // each data item is just a string in this case
        public ImageView productImage;
        public ImageView deleteItem;
        public TextView productName;
        public TextView productMinPackage;

        public ViewHolder(View v) {
            productImage = (ImageView) v.findViewById(R.id.product_image);
            productName = (TextView) v.findViewById(R.id.product_name);
            productMinPackage = (TextView) v.findViewById(R.id.product_minPackageUnit);
            deleteItem = (ImageView) v.findViewById(R.id.delete_item_button_img);
        }
    }
}