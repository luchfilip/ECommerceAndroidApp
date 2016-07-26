package com.smartbuilders.smartsales.ecommerce.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.model.ProductBrand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

/**
 * Created by Alberto on 9/4/2016.
 */
public class BrandsListAdapter extends BaseAdapter implements SectionIndexer {

    private HashMap<String, Integer> alphaIndexer;
    private String[] sections;
    private Context mContext;
    private ArrayList<ProductBrand> mDataset;
    private ArrayList<ProductBrand> arraylist;

    public BrandsListAdapter(Context context, ArrayList<ProductBrand> data) {
        mContext = context;
        mDataset = data;

        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(mDataset);

        alphaIndexer = new HashMap<>();
        int size = mDataset.size();

        for (int x = 0; x < size; x++) {
            String s = mDataset.get(x).getName(); //items.get(x);
            // get the first letter of the store
            String ch = s.substring(0, 1);
            // convert to uppercase otherwise lowercase a -z will be sorted
            // after upper A-Z
            ch = ch.toUpperCase();
            // put only if the key does not exist
            if (!alphaIndexer.containsKey(ch))
                alphaIndexer.put(ch, x);
        }

        Set<String> sectionLetters = alphaIndexer.keySet();
        // create a list from the set to sort
        ArrayList<String> sectionList = new ArrayList<>(
                sectionLetters);
        Collections.sort(sectionList);
        sections = new String[sectionList.size()];
        sections = sectionList.toArray(sections);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;
        if(view==null){//si la vista es null la crea sino la reutiliza
            view = LayoutInflater.from(mContext).inflate(R.layout.brand_list_item, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        if(!TextUtils.isEmpty(mDataset.get(position).getName())){
            viewHolder.brandName.setText(mDataset.get(position).getName());
            viewHolder.brandName.setVisibility(TextView.VISIBLE);
        }else{
            viewHolder.brandName.setVisibility(TextView.GONE);
        }

        if(!TextUtils.isEmpty(mDataset.get(position).getDescription())){
            viewHolder.brandDescription.setText(mDataset.get(position).getDescription());
            viewHolder.brandDescription.setVisibility(TextView.VISIBLE);
        }else{
            viewHolder.brandDescription.setVisibility(TextView.GONE);
        }

        if(mDataset.get(position).getImageId()>0){
            viewHolder.brandImage.setImageResource(mDataset.get(position).getImageId());
            viewHolder.brandImage.setVisibility(ImageView.VISIBLE);
        }else{
            viewHolder.brandImage.setVisibility(ImageView.GONE);
        }

        viewHolder.productsActiveQty.setText(mContext.getString(R.string.products_availables,
                String.valueOf(mDataset.get(position).getProductsActiveQty())));
        return view;
    }

    @Override
    public int getPositionForSection(int section) {
        return alphaIndexer.get(sections[section]);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        return sections;
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

    public void filter(String charText) {
        if(charText == null){
            return;
        }
        charText = charText.toLowerCase(Locale.getDefault());
        mDataset.clear();
        if (charText.length() == 0) {
            mDataset.addAll(arraylist);
        } else {
            for (ProductBrand brand : arraylist) {
                if (brand.getName().toLowerCase(Locale.getDefault()).contains(charText)
                        || brand.getDescription().toLowerCase(Locale.getDefault()).contains(charText)) {
                    mDataset.add(brand);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        public TextView brandName;
        public TextView brandDescription;
        public ImageView brandImage;
        public TextView productsActiveQty;

        public ViewHolder(View v) {
            brandName = (TextView) v.findViewById(R.id.brand_name_tv);
            brandDescription = (TextView) v.findViewById(R.id.brand_description_tv);
            brandImage = (ImageView) v.findViewById(R.id.brand_imageView);
            productsActiveQty = (TextView) v.findViewById(R.id.products_active_qty);
        }
    }
}