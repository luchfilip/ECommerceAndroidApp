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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductDetailActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductDetailFragment;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.WishListFragment;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Alberto on 7/4/2016.
 */
public class WishListAdapter extends BaseAdapter {

    private Context mContext;
    private WishListFragment mWishListFragment;
    private ArrayList<OrderLine> mDataset;
    private User mCurrentUser;
    private OrderLineDB orderLineDB;

    public interface Callback {
        public void addToShoppingCart(OrderLine orderLine);
        public void addToShoppingSale(OrderLine orderLine);
    }

    public WishListAdapter(Context context, WishListFragment wishListFragment, ArrayList<OrderLine> data, User user) {
        mContext = context;
        mWishListFragment = wishListFragment;
        mDataset = data;
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
        return mDataset.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDataset.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.wish_list_item, parent, false);
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
                intent.putExtra(ProductDetailActivity.KEY_CURRENT_USER, mCurrentUser);
                intent.putExtra(ProductDetailFragment.KEY_PRODUCT, mDataset.get(position).getProduct());
                mContext.startActivity(intent);
            }
        });

        viewHolder.productName.setText(mDataset.get(position).getProduct().getName());

        viewHolder.productAvailability.setText(mContext.getString(R.string.availability,
                mDataset.get(position).getProduct().getAvailability()));

        viewHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setMessage(mContext.getString(R.string.delete_from_wish_list_question, mDataset.get(position).getProduct().getName()))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String result = orderLineDB.deleteOrderLine(mDataset.get(position));
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

        viewHolder.addToShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDataset.get(position) != null) {
                    mWishListFragment.addToShoppingCart(mDataset.get(position));
                }
            }
        });

        viewHolder.addToShoppingSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDataset.get(position) != null) {
                    mWishListFragment.addToShoppingSale(mDataset.get(position));
                }
            }
        });

        if (mDataset.get(position).getProduct().getProductBrand() != null
                && mDataset.get(position).getProduct().getProductBrand().getDescription() != null) {
            viewHolder.productBrand.setText(mContext.getString(R.string.brand_detail,
                    mDataset.get(position).getProduct().getProductBrand().getDescription()));
        } else {
            viewHolder.productBrand.setVisibility(View.INVISIBLE);
        }

        view.setTag(viewHolder);
        return view;
    }

    public void setData(ArrayList<OrderLine> wishListLines) {
        mDataset = wishListLines;
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
        public TextView productAvailability;
        public TextView productBrand;
        public Button addToShoppingCart;
        public Button addToShoppingSale;

        public ViewHolder(View v) {
            productImage = (ImageView) v.findViewById(R.id.product_image);
            productName = (TextView) v.findViewById(R.id.product_name);
            productAvailability = (TextView) v.findViewById(R.id.product_availability);
            productBrand = (TextView) v.findViewById(R.id.product_brand);
            deleteItem = (ImageView) v.findViewById(R.id.delete_item_button_img);
            addToShoppingCart = (Button) v.findViewById(R.id.product_addtoshoppingcart_button);
            addToShoppingSale = (Button) v.findViewById(R.id.product_addtoshoppingsales_button);
        }
    }
}