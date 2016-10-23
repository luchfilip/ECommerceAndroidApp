package com.smartbuilders.smartsales.ecommerce;

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
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;
import com.smartbuilders.smartsales.salesforcesystem.DialogAddToShoppingSale2;
import com.smartbuilders.smartsales.salesforcesystem.DialogUpdateShoppingSaleQtyOrdered;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.model.UserProfile;
import com.smartbuilders.smartsales.ecommerce.adapters.RecommendedProductsListAdapter;
import com.smartbuilders.smartsales.ecommerce.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.data.ProductDB;
import com.smartbuilders.smartsales.ecommerce.data.RecommendedProductDB;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.ecommerce.providers.CachedFileProvider;
import com.smartbuilders.smartsales.ecommerce.utils.RecommendedProductsPDFCreator;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class RecommendedProductsListFragment extends Fragment implements RecommendedProductsListAdapter.Callback {

    private static final String STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION = "STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION";
    private static final String fileName = "ProductosRecomendados";

    private boolean mIsInitialLoad;
    private ShareActionProvider mShareActionProvider;
    private Intent mShareIntent;
    private RecommendedProductsListAdapter mRecommendedProductsListAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private int mRecyclerViewCurrentFirstPosition;
    private View mBlankScreenView;
    private View mainLayout;
    private User mUser;
    private TextView mBusinessPartnerName;
    private View mBusinessPartnerInfoSeparator;
    private ArrayList<Product> mRecommendedProducts;

    public RecommendedProductsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recommended_products_list, container, false);
        mIsInitialLoad = true;

        new Thread() {
            @Override
            public void run() {
                try {
                    if (savedInstanceState != null) {
                        if (savedInstanceState.containsKey(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION)) {
                            mRecyclerViewCurrentFirstPosition = savedInstanceState.getInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION);
                        }
                    }
                    mUser = Utils.getCurrentUser(getContext());
                    if (getContext() != null && mUser != null) {
                        mRecommendedProducts = (new RecommendedProductDB(getContext(), mUser))
                                .getRecommendedProductsByBusinessPartnerId(Utils.getAppCurrentBusinessPartnerId(getContext(), mUser));
                    } else {
                        mRecommendedProducts = new ArrayList<>();
                    }
                    if (getContext() != null) {
                        mRecommendedProductsListAdapter = new RecommendedProductsListAdapter(getContext(),
                                RecommendedProductsListFragment.this, mRecommendedProducts, mUser);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mBlankScreenView = view.findViewById(R.id.company_logo_name);
                                mainLayout = view.findViewById(R.id.main_layout);

                                mBusinessPartnerName = (TextView) view.findViewById(R.id.business_partner_commercial_name_textView);
                                mBusinessPartnerInfoSeparator = view.findViewById(R.id.business_partner_info_separator);

                                if (view.findViewById(R.id.search_fab) != null) {
                                    view.findViewById(R.id.search_fab).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            startActivity(new Intent(getActivity(), SearchResultsActivity.class));
                                        }
                                    });
                                }

                                setHeader();

                                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recommended_products_list);
                                // use this setting to improve performance if you know that changes
                                // in content do not change the layout size of the RecyclerView
                                recyclerView.setHasFixedSize(true);
                                if (useGridView()) {
                                    mLinearLayoutManager = new GridLayoutManager(getContext(), getSpanCount());
                                } else {
                                    mLinearLayoutManager = new LinearLayoutManager(getContext());
                                }
                                recyclerView.setLayoutManager(mLinearLayoutManager);
                                recyclerView.setAdapter(mRecommendedProductsListAdapter);

                                if (mRecyclerViewCurrentFirstPosition != 0) {
                                    recyclerView.scrollToPosition(mRecyclerViewCurrentFirstPosition);
                                }

                                view.findViewById(R.id.share_fab).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (mShareIntent!=null) {
                                            startActivity(mShareIntent);
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                if (mRecommendedProducts.isEmpty()) {
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

    private void setHeader() {
        if (mUser != null && (BuildConfig.IS_SALES_FORCE_SYSTEM
                || mUser.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID)) {
            try {
                BusinessPartner businessPartner = (new BusinessPartnerDB(getContext(), mUser))
                        .getBusinessPartnerById(Utils.getAppCurrentBusinessPartnerId(getContext(), mUser));
                if (businessPartner != null) {
                    mBusinessPartnerName.setText(getString(R.string.business_partner_name_detail, businessPartner.getName()));
                    mBusinessPartnerName.setVisibility(View.VISIBLE);
                    mBusinessPartnerInfoSeparator.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart() {
        if (mIsInitialLoad) {
            mIsInitialLoad = false;
        } else {
            setHeader();
            reloadRecommendedProductsList();
        }
        super.onStart();
    }

    private void reloadRecommendedProductsList() {
        setHeader();
        try {
            ArrayList<Product> recommendedProducts = (new RecommendedProductDB(getActivity(), mUser))
                    .getRecommendedProductsByBusinessPartnerId(Utils.getAppCurrentBusinessPartnerId(getContext(), mUser));
            mRecommendedProductsListAdapter.setData(recommendedProducts);
            //Se debe recargar el documento pdf que se tiene para compartir
            new ReloadShareIntentThread(recommendedProducts).start();
            if (recommendedProducts == null || recommendedProducts.size() == 0) {
                mBlankScreenView.setVisibility(View.VISIBLE);
                mainLayout.setVisibility(View.GONE);
            }else{
                mainLayout.setVisibility(View.VISIBLE);
                mBlankScreenView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        Product product = (new ProductDB(getContext(), user)).getProductById(productId);
        if (BuildConfig.IS_SALES_FORCE_SYSTEM) {
            DialogAddToShoppingSale2 dialogAddToShoppingSale2 =
                    DialogAddToShoppingSale2.newInstance(product, user);
            dialogAddToShoppingSale2.show(getActivity().getSupportFragmentManager(),
                    DialogAddToShoppingSale2.class.getSimpleName());
        } else {
            DialogAddToShoppingSale dialogAddToShoppingSale =
                    DialogAddToShoppingSale.newInstance(product, user);
            dialogAddToShoppingSale.show(getActivity().getSupportFragmentManager(),
                    DialogAddToShoppingSale.class.getSimpleName());
        }
    }

    @Override
    public void updateQtyOrderedInShoppingSales(SalesOrderLine salesOrderLine, User user) {
        Product product = (new ProductDB(getContext(), mUser)).getProductById(salesOrderLine.getProductId());
        if (product!=null) {
            DialogUpdateShoppingSaleQtyOrdered dialogUpdateShoppingSaleQtyOrdered =
                    DialogUpdateShoppingSaleQtyOrdered.newInstance(product, salesOrderLine, user);
            dialogUpdateShoppingSaleQtyOrdered.show(getActivity().getSupportFragmentManager(),
                    DialogUpdateShoppingSaleQtyOrdered.class.getSimpleName());
        } else {
            //TODO: mostrar mensaje de error
        }
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
        new ReloadShareIntentThread(mRecommendedProducts).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_share) {
            if (mShareActionProvider!=null) {
                mShareActionProvider.setShareIntent(mShareIntent);
            }
        } else if (i == R.id.action_download) {
            if (mShareIntent != null) {
                Utils.createPdfFileInDownloadFolder(getContext(),
                        getContext().getCacheDir() + File.separator + (fileName + ".pdf"),
                        fileName + ".pdf");
            }

        }
        return super.onOptionsItemSelected(item);
    }

    class ReloadShareIntentThread extends Thread {

        private ArrayList<Product> mProducts;

        ReloadShareIntentThread(ArrayList<Product> products) {
            this.mProducts = products;
        }

        public void run() {
            mShareIntent = createShareIntent(mProducts);
            if(getActivity()!=null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mShareActionProvider!=null) {
                            mShareActionProvider.setShareIntent(mShareIntent);
                        }
                    }
                });
            }
        }

        private Intent createShareIntent(ArrayList<Product> products) {
            try {
                if (products != null && !products.isEmpty()) {
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

                    try {
                        new RecommendedProductsPDFCreator().generatePDF(products, fileName + ".pdf",
                                getActivity(), getContext(), mUser);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //Add the attachment by specifying a reference to our custom ContentProvider
                    //and the specific file of interest
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://"
                            + CachedFileProvider.AUTHORITY + File.separator + fileName + ".pdf"));
                    return shareIntent;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
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
                outState.putInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION,
                        mLinearLayoutManager.findFirstVisibleItemPosition());
            } else {
                outState.putInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION,
                        mLinearLayoutManager.findFirstCompletelyVisibleItemPosition());
            }
        } catch (Exception e) {
            outState.putInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION, mRecyclerViewCurrentFirstPosition);
        }
        super.onSaveInstanceState(outState);
    }
}
