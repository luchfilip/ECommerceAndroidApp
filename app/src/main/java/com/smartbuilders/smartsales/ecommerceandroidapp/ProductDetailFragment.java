package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.CustomPagerAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.ProductRecyclerViewAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProductDetailFragment extends Fragment {

    private static final String TAG = ProductDetailFragment.class.getSimpleName();

    private Product mProduct;
    private ShareActionProvider mShareActionProvider;
    public static final String KEY_PRODUCT = "key_product";


    public ProductDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        if(getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null
                && getActivity().getIntent().getExtras().containsKey(KEY_PRODUCT)){
            mProduct = getActivity().getIntent().getExtras().getParcelable(KEY_PRODUCT);
        }

        ((TextView) view.findViewById(R.id.product_name)).setText(mProduct.getName());

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.relatedproducts_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(new ProductRecyclerViewAdapter(Utils.getGenericProductsList(1), false,
                ProductRecyclerViewAdapter.REDIRECT_PRODUCT_DETAILS));

        ArrayList<Integer> imagesIds = new ArrayList<Integer>();
        imagesIds.add(mProduct.getImageId());
        imagesIds.add(mProduct.getImageId());
        imagesIds.add(mProduct.getImageId());
        imagesIds.add(mProduct.getImageId());
        imagesIds.add(mProduct.getImageId());
        mProduct.setImagesIds(imagesIds);

        CustomPagerAdapter mCustomPagerAdapter = new CustomPagerAdapter(getContext(), mProduct.getImagesIds());

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);

        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radiogroup);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        radioGroup.check(R.id.radioButton);
                        break;
                    case 1:
                        radioGroup.check(R.id.radioButton2);
                        break;
                    case 2:
                        radioGroup.check(R.id.radioButton3);
                        break;
                    case 3:
                        radioGroup.check(R.id.radioButton4);
                        break;
                    case 4:
                        radioGroup.check(R.id.radioButton5);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(mCustomPagerAdapter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.productdetailfragment, menu);

        // Retrieve the share menu item
        MenuItem item =(MenuItem) menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        // Attach an intent to this ShareActionProvider. You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mProduct != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        } else {
            Log.d(TAG, "Share Action Provider is null?");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            mShareActionProvider.setShareIntent(createShareIntent());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent createShareIntent(){
        String fileName = "tmpImg.jgg";
        Utils.createFileInCacheDir(fileName, mProduct.getImageId(), getContext());
        return Utils.createShareProductIntent(getContext(), mProduct, fileName);
    }

}

