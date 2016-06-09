package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductDetailActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.ShoppingCartFragment;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Alberto on 7/4/2016.
 */
public class ShoppingCartAdapter extends BaseAdapter {

    private Context mContext;
    private ShoppingCartFragment mShoppingCartFragment;
    private ArrayList<OrderLine> mDataset;
    private User mCurrentUser;
    private OrderLineDB orderLineDB;
    private boolean mIsShoppingCart;

    public interface Callback {
        void updateQtyOrdered(OrderLine orderLine);
    }

    public ShoppingCartAdapter(Context context, ShoppingCartFragment shoppingCartFragment,
                               ArrayList<OrderLine> data, boolean isShoppingCart, User user) {
        mContext = context;
        mShoppingCartFragment = shoppingCartFragment;
        mDataset = data;
        mIsShoppingCart = isShoppingCart;
        mCurrentUser = user;
        orderLineDB = new OrderLineDB(context, user);
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
        return  -1;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.shopping_cart_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        if(mDataset.get(position).getProduct().getImageFileName()!=null){
            File img = Utils.getFileThumbByFileName(mContext, mCurrentUser,
                    mDataset.get(position).getProduct().getImageFileName());
            if(img!=null){
                Picasso.with(mContext)
                        .load(img).error(R.drawable.no_image_available).into(viewHolder.productImage);
            }else{
                Picasso.with(mContext)
                        .load(mCurrentUser.getServerAddress() + "/IntelligentDataSynchronizer/GetThumbImage?fileName="
                                + mDataset.get(position).getProduct().getImageFileName())
                        .error(R.drawable.no_image_available)
                        .into(viewHolder.productImage, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                Utils.createFileInThumbDir(mDataset.get(position).getProduct().getImageFileName(),
                                        ((BitmapDrawable) viewHolder.productImage.getDrawable()).getBitmap(),
                                        mCurrentUser, mContext);
                            }

                            @Override
                            public void onError() {
                            }
                        });
            }
        }else{
            viewHolder.productImage.setImageResource(R.drawable.no_image_available);
        }

        viewHolder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.KEY_PRODUCT_ID, mDataset.get(position).getProduct().getId());
                mContext.startActivity(intent);
            }
        });

        viewHolder.productName.setText(mDataset.get(position).getProduct().getName());

        viewHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setMessage(mContext.getString(R.string.delete_from_shopping_cart_question,
                                mDataset.get(position).getProduct().getName()))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String result = null;
                                if(mIsShoppingCart){
                                    result = orderLineDB.deleteOrderLine(mDataset.get(position));
                                }
                                if(result == null){
                                    mDataset.remove(position);
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        viewHolder.productCommercialPackage.setText(mContext.getString(R.string.commercial_package,
                mDataset.get(position).getProduct().getProductCommercialPackage().getUnits() + " " +
                mDataset.get(position).getProduct().getProductCommercialPackage().getUnitDescription()));

        viewHolder.qtyOrdered.setText(String.valueOf(mDataset.get(position).getQuantityOrdered()));

        viewHolder.qtyOrdered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShoppingCartFragment.updateQtyOrdered(mDataset.get(position));
            }
        });

        view.setTag(viewHolder);
        return view;
    }

    public void setData(ArrayList<OrderLine> orderLines) {
        mDataset = orderLines;
        notifyDataSetChanged();
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
        public TextView productPrice;
        public TextView totalLine;
        public EditText qtyOrdered;

        public ViewHolder(View v) {
            productImage = (ImageView) v.findViewById(R.id.product_image);
            productName = (TextView) v.findViewById(R.id.product_name);
            totalLine = (TextView) v.findViewById(R.id.total_line);
            productCommercialPackage = (TextView) v.findViewById(R.id.product_commercial_package);
            productPrice = (TextView) v.findViewById(R.id.product_price);
            deleteItem = (ImageView) v.findViewById(R.id.delete_item_button_img);
            qtyOrdered = (EditText) v.findViewById(R.id.qty_ordered);
        }
    }
}
