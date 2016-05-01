package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Alberto on 7/4/2016.
 */
public class WishListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<OrderLine> mDataset;
    private User mCurrentUser;

    public WishListAdapter(Context context, ArrayList<OrderLine> data, User user) {
        mContext = context;
        mDataset = data;
        mCurrentUser = user;
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

        if(mDataset.get(position).getProduct().getImageFileName()!=null){
            Bitmap img = Utils.getImageByFileName(mContext, mCurrentUser, mDataset.get(position).getProduct().getImageFileName());
            if(img!=null){
                viewHolder.productImage.setImageBitmap(img);
            }else{
                viewHolder.productImage.setImageResource(mDataset.get(position).getProduct().getImageId());
            }
        }else{
            viewHolder.productImage.setImageResource(mDataset.get(position).getProduct().getImageId());
        }

        viewHolder.productName.setText(mDataset.get(position).getProduct().getName());

        viewHolder.productCommercialPackage.setText(mContext.getString(R.string.commercial_package,
                mDataset.get(position).getProduct().getProductCommercialPackage().getUnits() + " " +
                mDataset.get(position).getProduct().getProductCommercialPackage().getUnitDescription()));

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
        public TextView productCommercialPackage;

        public ViewHolder(View v) {
            productImage = (ImageView) v.findViewById(R.id.product_image);
            productName = (TextView) v.findViewById(R.id.product_name);
            productCommercialPackage = (TextView) v.findViewById(R.id.product_commercial_package);
            deleteItem = (ImageView) v.findViewById(R.id.delete_item_button_img);
        }
    }
}