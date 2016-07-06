package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.RecommendedProductsListAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ProductDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.RecommendedProductDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.providers.CachedFileProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.RecommendedProductsPDFCreator;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class RecommendedProductsListFragment extends Fragment implements RecommendedProductsListAdapter.Callback {

    private static final String STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION = "STATE_LISTVIEW_CURRENT_FIRST_POSITION";
    private static final String fileName = "ProductosRecomendados";

    private ShareActionProvider mShareActionProvider;
    private Intent mShareIntent;
    private RecommendedProductsListAdapter mRecommendedProductsListAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private int mRecyclerViewCurrentFirstPosition;
    private View mBlankScreenView;
    private View mainLayout;

    public RecommendedProductsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recommended_products_list, container, false);

        final ArrayList<Product> recommendedProducts = new ArrayList<>();

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState != null) {
                        if(savedInstanceState.containsKey(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION)){
                            mRecyclerViewCurrentFirstPosition = savedInstanceState.getInt(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION);
                        }
                    }
                    User user = Utils.getCurrentUser(getContext());
                    if (getActivity()!=null && user!=null) {
                        recommendedProducts.addAll((new RecommendedProductDB(getActivity(), user))
                                .getRecommendedProductsByBusinessPartnerId(Utils.getAppCurrentBusinessPartnerId(getContext(), user)));
                    }
                    if (getContext()!=null) {
                        mRecommendedProductsListAdapter = new RecommendedProductsListAdapter(getContext(),
                                RecommendedProductsListFragment.this, recommendedProducts, user);
                    }
                    mShareIntent = createSharedIntent(recommendedProducts);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mBlankScreenView = view.findViewById(R.id.company_logo_name);
                                mainLayout = view.findViewById(R.id.main_layout);

                                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recommended_products_list);
                                // use this setting to improve performance if you know that changes
                                // in content do not change the layout size of the RecyclerView
                                recyclerView.setHasFixedSize(true);
                                if (useGridView()) {
                                    mLinearLayoutManager = new GridLayoutManager(getContext(), getSpanCount());
                                }else{
                                    mLinearLayoutManager = new LinearLayoutManager(getContext());
                                }
                                recyclerView.setLayoutManager(mLinearLayoutManager);
                                recyclerView.setAdapter(mRecommendedProductsListAdapter);

                                if (mRecyclerViewCurrentFirstPosition!=0) {
                                    recyclerView.scrollToPosition(mRecyclerViewCurrentFirstPosition);
                                }

                                view.findViewById(R.id.share_fab).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(mShareIntent);
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                if (recommendedProducts.isEmpty()) {
                                    mBlankScreenView.setVisibility(View.VISIBLE);
                                } else {
                                    mainLayout.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
                }
            }
        }.start();
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void addToShoppingCart(int productId, User user) {
        Product product = (new ProductDB(getContext(), user))
                .getProductById(productId);
        DialogAddToShoppingCart dialogAddToShoppingCart =
                DialogAddToShoppingCart.newInstance(product, user);
        dialogAddToShoppingCart.show(getActivity().getSupportFragmentManager(),
                DialogAddToShoppingCart.class.getSimpleName());
    }

    @Override
    public void updateQtyOrderedInShoppingCart(OrderLine orderLine, User user) {
        DialogUpdateShoppingCartQtyOrdered dialogUpdateShoppingCartQtyOrdered =
                DialogUpdateShoppingCartQtyOrdered.newInstance(orderLine, true, user);
        dialogUpdateShoppingCartQtyOrdered.show(getActivity().getSupportFragmentManager(),
                DialogUpdateShoppingCartQtyOrdered.class.getSimpleName());
    }

    @Override
    public void addToShoppingSale(int productId, User user) {
        Product product = (new ProductDB(getContext(), user))
                .getProductById(productId);
        DialogAddToShoppingSale dialogAddToShoppingSale =
                DialogAddToShoppingSale.newInstance(product, user);
        dialogAddToShoppingSale.show(getActivity().getSupportFragmentManager(),
                DialogAddToShoppingSale.class.getSimpleName());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_recommended_products_list_fragment, menu);

        // Retrieve the share menu item
        MenuItem item = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        // Attach an intent to this ShareActionProvider. You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        mShareActionProvider.setShareIntent(mShareIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_share:
                mShareActionProvider.setShareIntent(mShareIntent);
                break;
            case R.id.action_download:
                if(mShareIntent!=null){
                    Utils.createPdfFileInDownloadFolder(getContext(),
                            getContext().getCacheDir() + File.separator + (fileName+".pdf"),
                            fileName+".pdf");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent createSharedIntent(ArrayList<Product> products) {
        try {
            if (products!=null && !products.isEmpty()) {
                String subject = "";
                String message = "";

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                if (Build.VERSION.SDK_INT >= 21) {
                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                } else {
                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                }
                // need this to prompts email client only
                shareIntent.setType("message/rfc822");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                shareIntent.putExtra(Intent.EXTRA_TEXT, message);

                try{
                    new RecommendedProductsPDFCreator().generatePDF(products, fileName + ".pdf", getContext());
                }catch(Exception e) {
                    e.printStackTrace();
                }

                //Add the attachment by specifying a reference to our custom ContentProvider
                //and the specific file of interest
                shareIntent.putExtra(Intent.EXTRA_STREAM,  Uri.parse("content://"
                        + CachedFileProvider.AUTHORITY + File.separator + fileName + ".pdf"));
                return shareIntent;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean useGridView(){
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            return true;
        }else {
            switch(getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) {
                case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                    switch(getResources().getDisplayMetrics().densityDpi) {
                        case DisplayMetrics.DENSITY_LOW:
                            break;
                        case DisplayMetrics.DENSITY_MEDIUM:
                        case DisplayMetrics.DENSITY_HIGH:
                        case DisplayMetrics.DENSITY_XHIGH:
                            return  true;
                    }
                    break;
                //case Configuration.SCREENLAYOUT_SIZE_LARGE:
                //    break;
                //case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                //    break;
                //case Configuration.SCREENLAYOUT_SIZE_SMALL:
                //    break;
            }
        }
        return false;
    }

    private int getSpanCount() {
        switch(getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) {
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                switch(getResources().getDisplayMetrics().densityDpi) {
                    case DisplayMetrics.DENSITY_LOW:
                        break;
                    case DisplayMetrics.DENSITY_MEDIUM:
                    case DisplayMetrics.DENSITY_HIGH:
                    case DisplayMetrics.DENSITY_XHIGH:
                        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                            return 3;
                        }
                        break;
                }
                break;
            //case Configuration.SCREENLAYOUT_SIZE_LARGE:
            //    break;
            //case Configuration.SCREENLAYOUT_SIZE_NORMAL:
            //    break;
            //case Configuration.SCREENLAYOUT_SIZE_SMALL:
            //    break;
        }
        return 2;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        try {
            if (mLinearLayoutManager instanceof GridLayoutManager) {
                outState.putInt(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION,
                        mLinearLayoutManager.findFirstVisibleItemPosition());
            } else {
                outState.putInt(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION,
                        mLinearLayoutManager.findFirstCompletelyVisibleItemPosition());
            }
        } catch (Exception e) {
            outState.putInt(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION, mRecyclerViewCurrentFirstPosition);
        }
        super.onSaveInstanceState(outState);
    }
}
