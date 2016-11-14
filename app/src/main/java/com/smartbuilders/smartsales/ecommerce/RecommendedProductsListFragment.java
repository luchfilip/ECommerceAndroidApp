package com.smartbuilders.smartsales.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;
import com.smartbuilders.smartsales.salesforcesystem.DialogAddToShoppingSale2;
import com.smartbuilders.smartsales.salesforcesystem.DialogUpdateShoppingSaleQtyOrdered;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.adapters.RecommendedProductsListAdapter;
import com.smartbuilders.smartsales.ecommerce.data.ProductDB;
import com.smartbuilders.smartsales.ecommerce.data.RecommendedProductDB;
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
    private Intent mShareIntent;
    private RecommendedProductsListAdapter mRecommendedProductsListAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private int mRecyclerViewCurrentFirstPosition;
    private View mEmptyLayoutWallPaper;
    private View mainLayout;
    private User mUser;
    private ArrayList<Product> mRecommendedProducts;
    private ProgressDialog waitPlease;

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
                                mEmptyLayoutWallPaper = view.findViewById(R.id.empty_layout_wallpaper);
                                mainLayout = view.findViewById(R.id.main_layout);

                                if (view.findViewById(R.id.search_fab) != null) {
                                    view.findViewById(R.id.search_fab).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            startActivity(new Intent(getActivity(), SearchResultsActivity.class));
                                        }
                                    });
                                }

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
                                        new CreateShareAndDownloadIntentThread(0).start();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                if (mRecommendedProducts.isEmpty()) {
                                    mEmptyLayoutWallPaper.setVisibility(View.VISIBLE);
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
    public void onStart() {
        if (mIsInitialLoad) {
            mIsInitialLoad = false;
        } else {
            reloadRecommendedProductsList();
        }
        super.onStart();
    }

    private void reloadRecommendedProductsList() {
        try {
            mRecommendedProducts = (new RecommendedProductDB(getActivity(), mUser))
                    .getRecommendedProductsByBusinessPartnerId(Utils.getAppCurrentBusinessPartnerId(getContext(), mUser));
            mRecommendedProductsListAdapter.setData(mRecommendedProducts);
            mShareIntent = null;
            if (mRecommendedProducts == null || mRecommendedProducts.size() == 0) {
                mEmptyLayoutWallPaper.setVisibility(View.VISIBLE);
                mainLayout.setVisibility(View.GONE);
            }else{
                mainLayout.setVisibility(View.VISIBLE);
                mEmptyLayoutWallPaper.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addToShoppingCart(int productId, User user) {
        Product product = (new ProductDB(getContext(), user))
                .getProductById(productId);
        if (product!=null) {
            DialogAddToShoppingCart dialogAddToShoppingCart =
                    DialogAddToShoppingCart.newInstance(product, user);
            dialogAddToShoppingCart.show(getActivity().getSupportFragmentManager(),
                    DialogAddToShoppingCart.class.getSimpleName());
        } else {
            //TODO: mostrar mensaje de error
        }
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
        if (product!=null) {
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
        } else {
            //TODO: mostrar mensaje de error
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_download) {
            new CreateShareAndDownloadIntentThread(1).start();
        }
        return super.onOptionsItemSelected(item);
    }

    class CreateShareAndDownloadIntentThread extends Thread {
        private int mMode;
        private String mErrorMessage;

        CreateShareAndDownloadIntentThread(int mode) {
            this.mMode = mode;
        }

        public void run() {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Se bloquea la rotacion de la pantalla para evitar que se mate a la aplicacion
                        Utils.lockScreenOrientation(getActivity());
                        if (waitPlease==null || !waitPlease.isShowing()){
                            waitPlease = ProgressDialog.show(getContext(), null,
                                    getString(R.string.creating_recommended_products_list_wait_please), true, false);
                        }
                    }
                });
            }

            try {
                if (mShareIntent == null) {
                    createShareAndDownloadIntent();
                }
            } catch (Exception e) {
                e.printStackTrace();
                mErrorMessage = e.getMessage();
            }

            if(getActivity()!=null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (TextUtils.isEmpty(mErrorMessage)) {
                            if (mShareIntent != null) {
                                switch (mMode) {
                                    case 0:
                                        startActivity(mShareIntent);
                                        break;
                                    case 1:
                                        Utils.createPdfFileInDownloadFolder(getContext(),
                                                getContext().getCacheDir() + File.separator + (fileName + ".pdf"),
                                                fileName + ".pdf");
                                        break;
                                }
                            }
                        } else {
                            Toast.makeText(getContext(), mErrorMessage, Toast.LENGTH_SHORT).show();
                        }
                        if (waitPlease!=null && waitPlease.isShowing()) {
                            waitPlease.dismiss();
                            waitPlease = null;
                        }
                        Utils.unlockScreenOrientation(getActivity());
                    }
                });
            }
        }

        private void createShareAndDownloadIntent() throws Exception {
            try {
                if (mRecommendedProducts != null && !mRecommendedProducts.isEmpty()) {
                    new RecommendedProductsPDFCreator().generatePDF(mRecommendedProducts, fileName + ".pdf",
                            getActivity(), getContext(), mUser);

                    mShareIntent = new Intent(Intent.ACTION_SEND);
                    mShareIntent.setType("application/pdf");
                    mShareIntent.putExtra(Intent.EXTRA_STREAM,
                            Uri.parse("content://"+CachedFileProvider.AUTHORITY+File.separator+fileName+".pdf"));
                } else {
                    mShareIntent = null;
                }
            } catch (Exception e) {
                mShareIntent = null;
                throw e;
            }
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
