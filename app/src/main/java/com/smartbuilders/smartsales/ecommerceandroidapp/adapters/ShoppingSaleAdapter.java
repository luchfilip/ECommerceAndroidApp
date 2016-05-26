package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductDetailFragment;
import com.smartbuilders.smartsales.ecommerceandroidapp.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.ShoppingSaleFragment;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.GetFileFromServlet;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Alberto on 7/4/2016.
 */
public class ShoppingSaleAdapter extends BaseAdapter {

    public static final int FOCUS_PRICE = 1;
    public static final int FOCUS_TAX_PERCENTAGE = 2;
    public static final int FOCUS_QTY_ORDERED = 3;

    private Context mContext;
    private ShoppingSaleFragment mShoppingSaleFragment;
    private ArrayList<OrderLine> mDataset;
    private User mCurrentUser;
    private OrderLineDB orderLineDB;

    public interface Callback {
        public void updateSalesOrderLine(OrderLine orderLine, int focus);
    }

    public ShoppingSaleAdapter(Context context, ShoppingSaleFragment shoppingSaleFragment, ArrayList<OrderLine> data, User user) {
        mContext = context;
        mShoppingSaleFragment = shoppingSaleFragment;
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.shopping_sale_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        if(mDataset.get(position).getProduct().getImageFileName()!=null){
            //Picasso.with(mContext).load(mCurrentUser.getServerAddress() + "/IntelligentDataSynchronizer/GetThumbImage?fileName=" +
            //        mDataset.get(position).getProduct().getImageFileName()).error(R.drawable.ic_error_black_48dp).into(viewHolder.productImage);
            Bitmap img = Utils.getImageByFileName(mContext, mCurrentUser, mDataset.get(position).getProduct().getImageFileName());
            if(img!=null){
                viewHolder.productImage.setImageBitmap(img);
            }else{
                GetFileFromServlet getFileFromServlet =
                        new GetFileFromServlet(mDataset.get(position).getProduct().getImageFileName(),
                                true, viewHolder.productImage, mCurrentUser, mContext);
                getFileFromServlet.execute();
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

        viewHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setMessage(mContext.getString(R.string.delete_from_shopping_sale_question,
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

        viewHolder.productPrice.setText(String.valueOf(mDataset.get(position).getPrice()));
        viewHolder.productPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Callback) mShoppingSaleFragment).updateSalesOrderLine(mDataset.get(position), FOCUS_PRICE);
            }
        });

        viewHolder.productTaxPercentage.setText(String.valueOf(mDataset.get(position).getTaxPercentage()));
        viewHolder.productTaxPercentage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Callback) mShoppingSaleFragment).updateSalesOrderLine(mDataset.get(position), FOCUS_TAX_PERCENTAGE);
            }
        });

        viewHolder.qtyOrdered.setText(String.valueOf(mDataset.get(position).getQuantityOrdered()));
        viewHolder.qtyOrdered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Callback) mShoppingSaleFragment).updateSalesOrderLine(mDataset.get(position), FOCUS_QTY_ORDERED);
            }
        });

        viewHolder.totalLineAmount.setText(String.valueOf(mDataset.get(position).getTotalLineAmount()));

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
        public TextView totalLine;
        public EditText qtyOrdered;
        public EditText productPrice;
        public EditText productTaxPercentage;
        public EditText totalLineAmount;

        public ViewHolder(View v) {
            productImage = (ImageView) v.findViewById(R.id.product_image);
            productName = (TextView) v.findViewById(R.id.product_name);
            totalLine = (TextView) v.findViewById(R.id.total_line);
            productPrice = (EditText) v.findViewById(R.id.product_price);
            productTaxPercentage = (EditText) v.findViewById(R.id.product_tax_percentage);
            totalLineAmount = (EditText) v.findViewById(R.id.total_line_amount);
            deleteItem = (ImageView) v.findViewById(R.id.delete_item_button_img);
            qtyOrdered = (EditText) v.findViewById(R.id.qty_ordered);
        }
    }

    public void setData(ArrayList<OrderLine> salesOrderLines) {
        this.mDataset = salesOrderLines;
        notifyDataSetChanged();
    }
}
