package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.WishListAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ProductDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.providers.CachedFileProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.WishListPDFCreator;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Alberto on 22/3/2016.
 */
public class WishListFragment extends Fragment implements WishListAdapter.Callback {

    private static final String STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION = "STATE_LISTVIEW_CURRENT_FIRST_POSITION";
    private static final String fileName = "ListaDeDeseos";

    private boolean mIsInitialLoad;
    private User mUser;
    private ShareActionProvider mShareActionProvider;
    private Intent mShareIntent;
    private WishListAdapter mWishListAdapter;
    private OrderLineDB mOrderLineDB;
    private LinearLayoutManager mLinearLayoutManager;
    private int mRecyclerViewCurrentFirstPosition;
    private View mBlankScreenView;
    private View mainLayout;

    public WishListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_wish_list, container, false);
        mIsInitialLoad = true;

        final ArrayList<OrderLine> wishListLines = new ArrayList<>();

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState != null) {
                        if(savedInstanceState.containsKey(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION)){
                            mRecyclerViewCurrentFirstPosition = savedInstanceState.getInt(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION);
                        }
                    }
                    mUser = Utils.getCurrentUser(getContext());
                    mOrderLineDB = new OrderLineDB(getContext(), mUser);

                    wishListLines.addAll(mOrderLineDB.getWishList());

                    mWishListAdapter = new WishListAdapter(getContext(), WishListFragment.this, wishListLines, mUser);
                    mShareIntent = createSharedIntent(wishListLines);
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

                                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.wish_list);
                                // use this setting to improve performance if you know that changes
                                // in content do not change the layout size of the RecyclerView
                                recyclerView.setHasFixedSize(true);
                                if (useGridView()) {
                                    mLinearLayoutManager = new GridLayoutManager(getContext(), getSpanCount());
                                }else{
                                    mLinearLayoutManager = new LinearLayoutManager(getContext());
                                }
                                recyclerView.setLayoutManager(mLinearLayoutManager);
                                recyclerView.setAdapter(mWishListAdapter);

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
                                if (wishListLines.isEmpty()) {
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
    public void onStart() {
        if(mIsInitialLoad){
            mIsInitialLoad = false;
        }else{
            reloadWishList();
        }
        super.onStart();
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

    public void reloadWishList(){
        if (mOrderLineDB!=null) {
            reloadWishList(mOrderLineDB.getWishList());
        }
    }

    @Override
    public void reloadWishList(ArrayList<OrderLine> wishListLines){
        mWishListAdapter.setData(wishListLines);
        //Se debe recargar el documento pdf que se tiene para compartir
        new ReloadShareIntentThread(wishListLines).start();
        if (wishListLines==null || wishListLines.size()==0) {
            mBlankScreenView.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_wishlist_fragment, menu);

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
            case R.id.clear_wish_list:
                clearWishList();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearWishList () {
        new AlertDialog.Builder(getContext())
                .setMessage(getContext().getString(R.string.clear_wish_list_question))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String result = mOrderLineDB.clearWishList();
                            if(result == null){
                                reloadWishList();
                            } else {
                                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    class ReloadShareIntentThread extends Thread {

        private ArrayList<OrderLine> mWishListLines;

        public ReloadShareIntentThread(ArrayList<OrderLine> wishListLines) {
            this.mWishListLines = wishListLines;
        }

        public void run() {
            mShareIntent = createSharedIntent(mWishListLines);
            mShareActionProvider.setShareIntent(mShareIntent);
        }
    }

    private Intent createSharedIntent(ArrayList<OrderLine> wishListLines) {
        try {
            if (wishListLines!=null && !wishListLines.isEmpty()) {
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
                    new WishListPDFCreator().generatePDF(wishListLines, fileName + ".pdf", getContext());
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