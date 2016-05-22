package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Alberto on 7/4/2016.
 */
public class ShoppingCartAdapter extends BaseAdapter {

    private Context mContext;
    private Activity mActivity;
    private ArrayList<OrderLine> mDataset;
    private User mCurrentUser;
    private OrderLineDB orderLineDB;

    public ShoppingCartAdapter(Context context, Activity activity, ArrayList<OrderLine> data, User user) {
        mContext = context;
        mActivity = activity;
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.shopping_cart_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        if(mDataset.get(position).getProduct().getImageFileName()!=null){
            Picasso.with(mContext).load(mCurrentUser.getServerAddress() + "/IntelligentDataSynchronizer/GetThumbImage?fileName=" +
                    mDataset.get(position).getProduct().getImageFileName()).error(R.drawable.ic_error_black_48dp).into(viewHolder.productImage);
            //Bitmap img = Utils.getThumbByFileName(mContext, mCurrentUser, mDataset.get(position).getProduct().getImageFileName());
            //if(img!=null){
            //    viewHolder.productImage.setImageBitmap(img);
            //}else{
            //    viewHolder.productImage.setImageResource(mDataset.get(position).getProduct().getImageId());
            //}
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

        viewHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setMessage(mContext.getString(R.string.delete_from_shopping_cart_question,
                                mDataset.get(position).getProduct().getName()))
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

        viewHolder.productCommercialPackage.setText(mContext.getString(R.string.commercial_package,
                mDataset.get(position).getProduct().getProductCommercialPackage().getUnits() + " " +
                mDataset.get(position).getProduct().getProductCommercialPackage().getUnitDescription()));

        viewHolder.qtyOrdered.setText(String.valueOf(mDataset.get(position).getQuantityOrdered()));

        viewHolder.qtyOrdered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // custom dialog
                final Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.fragment_add_to_shopping_cart);

                ((TextView) dialog.findViewById(R.id.product_availability_dialog_edit_qty_requested_tv))
                        .setText(mContext.getString(R.string.availability, mDataset.get(position).getProduct().getAvailability()));

                dialog.findViewById(R.id.cancel_button).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        }
                );

                ((Button) dialog.findViewById(R.id.add_to_shopping_cart_button)).setText(R.string.accept);
                dialog.findViewById(R.id.add_to_shopping_cart_button).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int oldValue = mDataset.get(position).getQuantityOrdered();
                                try {
                                    mDataset.get(position).setQuantityOrdered(Integer.valueOf(((EditText) dialog.findViewById(R.id.qty_requested_editText)).getText().toString()));
                                    String result = orderLineDB.updateOrderLine(mDataset.get(position));
                                    if(result == null){
                                        notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
                                    }
                                    dialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    mDataset.get(position).setQuantityOrdered(oldValue);
                                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                );
                dialog.setTitle(mDataset.get(position).getProduct().getName());
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        try {
                            mActivity.getWindow().setSoftInputMode(
                                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                dialog.show();
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
