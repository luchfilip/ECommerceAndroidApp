package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.BrandsListActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.CategoriesListActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductsListActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.RecentSearchDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductSubCategory;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.RecentSearch;

import java.util.ArrayList;

/**
 * Created by Alberto on 27/3/2016.
 */
public class SearchResultAdapter extends BaseAdapter {

    private Context mContext;
    private String mSearchPattern;
    private ArrayList mDataset;
    private User mCurrentUser;
    private RecentSearchDB recentSearchDB;

    public SearchResultAdapter(Context context, ArrayList data, User user) {
        mContext = context;
        recentSearchDB = new RecentSearchDB(context, user);
        mSearchPattern = new String();
        if(data == null){
            data = recentSearchDB.getRecentSearches(30);
        }else if(data.isEmpty()){
            data.add(context.getString(R.string.no_results_founds));
            data.add(context.getString(R.string.search_by_category));
            data.add(context.getString(R.string.search_by_brand));
        }
        mDataset = data;
        mCurrentUser = user;
    }

    public void setData(String searchPattern, ArrayList data, Context context){
        mSearchPattern = searchPattern;
        if(data == null){
            data = recentSearchDB.getRecentSearches(30);
        }else if(data.isEmpty()){
            data.add(context.getString(R.string.no_results_founds));
            data.add(context.getString(R.string.search_by_category));
            data.add(context.getString(R.string.search_by_brand));
        }
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
        if(mDataset.get(position) instanceof Product){
            return ((Product) mDataset.get(position)).getId();
        }
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //dejar esta vista asi porque se cambia mucho entre los diferentes tipos de busquedas y se hace
        //complicado el mantenimiento
        View view = LayoutInflater.from(mContext).inflate(R.layout.search_result_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        if(mDataset.get(position) instanceof Product){
            if(!TextUtils.isEmpty(((Product) mDataset.get(position)).getName())){
                viewHolder.title.setText(((Product) mDataset.get(position)).getName());
            }else{
                viewHolder.title.setVisibility(TextView.GONE);
            }
            if(((Product) mDataset.get(position)).getProductSubCategory()!=null &&
                    !TextUtils.isEmpty(((Product) mDataset.get(position)).getProductSubCategory().getDescription())){
                viewHolder.subTitle.setText(((Product) mDataset.get(position)).getProductSubCategory().getDescription());
            }else{
                viewHolder.subTitle.setVisibility(TextView.GONE);
            }
            view.findViewById(R.id.linearLayout_container).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recentSearchDB.insertRecentSearch(((Product) mDataset.get(position)).getName(),
                            ((Product) mDataset.get(position)).getId(),
                            ((Product) mDataset.get(position)).getProductSubCategory().getId());
                    goToProductList(((Product) mDataset.get(position)).getName(), (Product) mDataset.get(position));
                }
            });
        }else if(mDataset.get(position) instanceof String){
            viewHolder.title.setPadding(0, 12, 0, 12);
            viewHolder.title.setText((String) mDataset.get(position));
            viewHolder.subTitle.setVisibility(TextView.GONE);

            if(mDataset.get(position).equals(mContext.getString(R.string.no_results_founds))){
                viewHolder.title.setTextSize(17);
                viewHolder.goToSearchImage.setVisibility(View.INVISIBLE);
            }else if(mDataset.get(position).equals(mContext.getString(R.string.search_by_category))) {
                viewHolder.title.setTextSize(18);
                viewHolder.title.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                view.findViewById(R.id.linearLayout_container).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(new Intent(mContext, CategoriesListActivity.class));
                    }
                });
            } else if (mDataset.get(position).equals(mContext.getString(R.string.search_by_brand))) {
                viewHolder.title.setTextSize(18);
                viewHolder.title.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                view.findViewById(R.id.linearLayout_container).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(new Intent(mContext, BrandsListActivity.class));
                    }
                });
            }
        }else if(mDataset.get(position) instanceof RecentSearch){
            viewHolder.title.setPadding(0, 11, 0, 11);
            viewHolder.title.setTextSize(14);
            viewHolder.title.setText(((RecentSearch) mDataset.get(position)).getTextToSearch());

            if(((RecentSearch) mDataset.get(position)).getProductSubCategory()!=null &&
                    !TextUtils.isEmpty(((RecentSearch) mDataset.get(position)).getProductSubCategory().getDescription())){
                viewHolder.subTitle.setText(((RecentSearch) mDataset.get(position)).getProductSubCategory().getDescription());
            }else{
                viewHolder.subTitle.setVisibility(TextView.GONE);
            }

            viewHolder.goToSearchImage.setImageResource(android.R.drawable.ic_menu_recent_history);

            viewHolder.deleteRecentSearchImage.setVisibility(View.VISIBLE);
            viewHolder.deleteRecentSearchImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(mContext)
                        .setMessage(mContext.getString(R.string.delete_recent_search,
                                ((RecentSearch) mDataset.get(position)).getTextToSearch()))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                recentSearchDB.deleteRecentSearchById(((RecentSearch) mDataset.get(position)).getId());
                                mDataset.remove(position);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                }
            });

            view.findViewById(R.id.linearLayout_container).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(((RecentSearch) mDataset.get(position)).getProductId()>0 &&
                            ((RecentSearch) mDataset.get(position)).getSubcategoryId()>0) {
                        Product product = new Product();
                        product.setId(((RecentSearch) mDataset.get(position)).getProductId());
                        ProductSubCategory productSubCategory = new ProductSubCategory();
                        productSubCategory.setId(((RecentSearch) mDataset.get(position)).getSubcategoryId());
                        product.setProductSubCategory(productSubCategory);
                        goToProductList(((RecentSearch) mDataset.get(position)).getTextToSearch(), product);
                    }else{
                        Intent intent = new Intent(mContext, ProductsListActivity.class);
                        intent.putExtra(ProductsListActivity.KEY_PRODUCT_NAME, ((RecentSearch) mDataset.get(position)).getTextToSearch());
                        mContext.startActivity(intent);
                    }
                }
            });
        }

        return view;
    }

    private void goToProductList(String searchPattern, Product product){
        Intent intent = new Intent(mContext, ProductsListActivity.class);
        intent.putExtra(ProductsListActivity.KEY_PRODUCT_SUBCATEGORY_ID, product.getProductSubCategory().getId());
        intent.putExtra(ProductsListActivity.KEY_SEARCH_PATTERN, searchPattern);
        mContext.startActivity(intent);
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView subTitle;
        public ImageView goToSearchImage;
        public ImageView deleteRecentSearchImage;

        public ViewHolder(View v) {
            title = (TextView) v.findViewById(R.id.title_textView);
            subTitle = (TextView) v.findViewById(R.id.subTitle_textView);
            goToSearchImage = (ImageView) v.findViewById(R.id.go_to_search_result_img);
            deleteRecentSearchImage = (ImageView) v.findViewById(R.id.delete_recent_search_img);
        }
    }
}
