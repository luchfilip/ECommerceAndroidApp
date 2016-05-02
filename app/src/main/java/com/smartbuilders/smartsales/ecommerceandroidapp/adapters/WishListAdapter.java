package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductDetailActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductDetailFragment;
import com.smartbuilders.smartsales.ecommerceandroidapp.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
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
    private OrderLineDB orderLineDB;

    public WishListAdapter(Context context, ArrayList<OrderLine> data, User user) {
        mContext = context;
        mDataset = data;
        mCurrentUser = user;
        orderLineDB = new OrderLineDB(context, user);
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
            Bitmap img = Utils.getThumbByFileName(mContext, mCurrentUser, mDataset.get(position).getProduct().getImageFileName());
            if(img!=null){
                viewHolder.productImage.setImageBitmap(img);
            }else{
                viewHolder.productImage.setImageResource(mDataset.get(position).getProduct().getImageId());
            }
        }else{
            viewHolder.productImage.setImageResource(mDataset.get(position).getProduct().getImageId());
        }

        viewHolder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.KEY_CURRENT_USER, mCurrentUser);
                intent.putExtra(ProductDetailFragment.KEY_PRODUCT, mDataset.get(position).getProduct());
                mContext.startActivity(intent);
            }
        });

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

        viewHolder.moveToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = orderLineDB.moveOrderLineToShoppingCart(mDataset.get(position));
                if(result == null){
                    mDataset.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(mContext, "Producto movido al Carrito de compras exitosamente.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
                }
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
        public Button moveToCart;

        public ViewHolder(View v) {
            productImage = (ImageView) v.findViewById(R.id.product_image);
            productName = (TextView) v.findViewById(R.id.product_name);
            productCommercialPackage = (TextView) v.findViewById(R.id.product_commercial_package);
            deleteItem = (ImageView) v.findViewById(R.id.delete_item_button_img);
            moveToCart = (Button) v.findViewById(R.id.move_to_cart_button);
        }
    }
}