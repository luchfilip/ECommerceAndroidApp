package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductDetailActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductDetailFragment;
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductsListActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by stein on 4/25/2016.
 */
public class ProductGridViewAdapter  extends BaseAdapter {

    public static final int REDIRECT_PRODUCT_LIST = 0;
    public static final int REDIRECT_PRODUCT_DETAILS = 1;

    private ArrayList<Product> mDataset;
    private Product[] array;
    private Context mContext;
    private User mCurrentUser;
    private int mRedirectOption;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ProductGridViewAdapter(Context context, ArrayList<Product> myDataset, int redirectOption, User user) {
        this.mContext = context;
        mDataset = myDataset;
        mCurrentUser = user;
        mRedirectOption = redirectOption;
        this.array = myDataset.toArray(new Product[0]);
    }

    @Override
    public int getCount() {
        if(mDataset!=null){
            return mDataset.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(mDataset!=null){
            return mDataset.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if(mDataset!=null){
            return mDataset.get(position).getId();
        }
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.product_details, parent, false);
        ViewHolder holder = new ViewHolder(view);

        holder.productName.setText(mDataset.get(position).getName());
        if(holder.productShareLabel!=null){
            holder.productShareLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(createShareIntent(mDataset.get(position)));
                }
            });
        }
        if(holder.linearLayoutContent != null){
            switch (mRedirectOption){
                case REDIRECT_PRODUCT_DETAILS:
                    holder.linearLayoutContent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, ProductDetailActivity.class);
                            intent.putExtra(ProductDetailActivity.KEY_CURRENT_USER, mCurrentUser);
                            intent.putExtra(ProductDetailFragment.KEY_PRODUCT, mDataset.get(position));
                            mContext.startActivity(intent);
                        }
                    });
                    break;
                case  REDIRECT_PRODUCT_LIST:
                    holder.linearLayoutContent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, ProductsListActivity.class);
                            intent.putExtra(ProductsListActivity.KEY_CURRENT_USER, mCurrentUser);
                            intent.putExtra(ProductsListActivity.KEY_PRODUCT_SUBCATEGORY_ID,
                                    mDataset.get(position).getProductSubCategory().getId());
                            mContext.startActivity(intent);
                        }
                    });
                    break;
            }
        }
        if(holder.productSubCategory!=null){
            if(mDataset.get(position).getProductSubCategory()!=null
                    && !TextUtils.isEmpty(mDataset.get(position).getProductSubCategory().getDescription())){
                holder.productSubCategory.setText(mDataset.get(position).getProductSubCategory().getDescription());
            }else{
                holder.productSubCategory.setVisibility(TextView.GONE);
            }
        }
        if(holder.productBrand!=null){
            if(mDataset.get(position).getProductBrand()!=null
                    && !TextUtils.isEmpty(mDataset.get(position).getProductBrand().getDescription())){
                holder.productBrand.setText(mDataset.get(position).getProductBrand().getDescription());
            }else{
                holder.productBrand.setVisibility(TextView.GONE);
            }
        }

        if(holder.commercialPackage!=null){
            if(mDataset.get(position).getProductCommercialPackage()!=null
                    && !TextUtils.isEmpty(mDataset.get(position).getProductCommercialPackage().getUnitDescription())){
                holder.commercialPackage.setText(mContext.getString(R.string.commercial_package,
                        mDataset.get(position).getProductCommercialPackage().getUnits() + " " +
                                mDataset.get(position).getProductCommercialPackage().getUnitDescription()));
            }else{
                holder.commercialPackage.setVisibility(TextView.GONE);
            }
        }

        if(mDataset.get(position).getImageFileName()!=null){
            Bitmap img = Utils.getImageByFileName(mContext, mCurrentUser, mDataset.get(position).getImageFileName());
            if(img!=null){
                holder.productImage.setImageBitmap(img);
            }else{
                holder.productImage.setImageResource(mDataset.get(position).getImageId());
            }
        }else{
            holder.productImage.setImageResource(mDataset.get(position).getImageId());
        }

        view.setTag(holder);
        return view;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        // each data item is just a string in this case
        public TextView productName;
        public TextView productShareLabel;
        public ImageView productImage;
        public TextView productSubCategory;
        public TextView productBrand;
        public TextView commercialPackage;
        public LinearLayout linearLayoutContent;

        public ViewHolder(View v) {
            productName = (TextView) v.findViewById(R.id.product_name);
            productShareLabel = (TextView) v.findViewById(R.id.product_share);
            productImage = (ImageView) v.findViewById(R.id.product_image);
            linearLayoutContent = (LinearLayout) v.findViewById(R.id.linear_layout_content);
            productSubCategory = (TextView) v.findViewById(R.id.product_subcategory);
            productBrand = (TextView) v.findViewById(R.id.product_brand);
            commercialPackage = (TextView) v.findViewById(R.id.product_comercial_package);
        }
    }

    private Intent createShareIntent(Product product){
        String fileName = "tmpImg.jgg";
        if(product.getImageFileName()!=null){
            Bitmap productImage = Utils.getImageByFileName(mContext, mCurrentUser,
                    product.getImageFileName());
            if(productImage!=null){
                Utils.createFileInCacheDir(fileName, productImage, mContext);
            }else{
                Utils.createFileInCacheDir(fileName, product.getImageId(), mContext);
            }
        }else{
            Utils.createFileInCacheDir(fileName, product.getImageId(), mContext);
        }
        return Utils.createShareProductIntent(mContext, product, fileName);
    }

    public void filter (String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        mDataset.clear();
        if (charText.length() == 0) {
            mDataset.addAll(Arrays.asList(array));
        } else {
            for (Product product : array) {
                if (product.getName().toLowerCase(Locale.getDefault()).contains(charText)
                        || (product.getDescription()!=null && product.getDescription()
                        .toLowerCase(Locale.getDefault()).contains(charText))) {
                    mDataset.add(product);
                }
            }
        }
        notifyDataSetChanged();
    }
}
