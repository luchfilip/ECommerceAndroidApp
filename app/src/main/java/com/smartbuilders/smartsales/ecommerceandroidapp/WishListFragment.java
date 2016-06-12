package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.WishListAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ProductDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.providers.CachedFileProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.WishListPDFCreator;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;

import java.util.ArrayList;

/**
 * Created by Alberto on 22/3/2016.
 */
public class WishListFragment extends Fragment {

    private static final String STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION = "STATE_LISTVIEW_CURRENT_FIRST_POSITION";

    private User mCurrentUser;
    private ShareActionProvider mShareActionProvider;

    private WishListAdapter mWishListAdapter;
    private ArrayList<OrderLine> wishListLines;
    private LinearLayoutManager linearLayoutManager;
    private int mRecyclerViewCurrentFirstPosition;
    private Intent shareIntent;

    public WishListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        final View view = inflater.inflate(R.layout.fragment_wish_list, container, false);

        wishListLines = new ArrayList<>();

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState != null) {
                        if(savedInstanceState.containsKey(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION)){
                            mRecyclerViewCurrentFirstPosition = savedInstanceState.getInt(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION);
                        }
                    }
                    mCurrentUser = Utils.getCurrentUser(getContext());
                    if (getActivity()!=null) {
                        wishListLines.addAll((new OrderLineDB(getActivity(), mCurrentUser)).getWishList());
                    }
                    if (getContext()!=null) {
                        mWishListAdapter = new WishListAdapter(getContext(), WishListFragment.this, wishListLines, mCurrentUser);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (wishListLines==null || wishListLines.size()==0) {
                                    view.findViewById(R.id.company_logo_name).setVisibility(View.VISIBLE);
                                    view.findViewById(R.id.wish_list).setVisibility(View.GONE);
                                } else {
                                    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.wish_list);
                                    // use this setting to improve performance if you know that changes
                                    // in content do not change the layout size of the RecyclerView
                                    recyclerView.setHasFixedSize(true);
                                    if (useGridView()) {
                                        linearLayoutManager = new GridLayoutManager(getContext(), getSpanCount());
                                    }else{
                                        linearLayoutManager = new LinearLayoutManager(getContext());
                                    }
                                    recyclerView.setLayoutManager(linearLayoutManager);
                                    recyclerView.setAdapter(mWishListAdapter);

                                    if (mRecyclerViewCurrentFirstPosition!=0) {
                                        recyclerView.scrollToPosition(mRecyclerViewCurrentFirstPosition);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                view.findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        }.start();
        return view;
    }

    public void addToShoppingCart(OrderLine orderLine, User user) {
        Product product = (new ProductDB(getContext(), user))
                .getProductById(orderLine.getProduct().getId(), false);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        DialogAddToShoppingCart addToShoppingCartFragment =
                DialogAddToShoppingCart.newInstance(product, user);
        addToShoppingCartFragment.show(fm, DialogAddToShoppingCart.class.getSimpleName());
    }

    public void addToShoppingSale(OrderLine orderLine, User user) {
        Product product = (new ProductDB(getContext(), user))
                .getProductById(orderLine.getProduct().getId(), false);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        DialogAddToShoppingSale addToShoppingSaleFragment =
                DialogAddToShoppingSale.newInstance(product, user);
        addToShoppingSaleFragment.show(fm, DialogAddToShoppingSale.class.getSimpleName());
    }

    public void reloadWishList(User user){
        if (getActivity()!=null) {
            wishListLines = (new OrderLineDB(getActivity(), user)).getWishList();
            mWishListAdapter.setData(wishListLines);
            //Se debe recargar el documento pdf que se tiene para compartir
            new CreateShareIntentThread().start();
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
        if (wishListLines != null && wishListLines.size() > 0) {
            new CreateShareIntentThread().start();
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
            if (wishListLines != null && wishListLines.size() > 0) {
                mShareActionProvider.setShareIntent(shareIntent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class CreateShareIntentThread extends Thread {
        public void run() {
            String fileName = "ListaDeDeseos";
            String subject = "";
            String message = "";

            shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            // need this to prompts email client only
            shareIntent.setType("message/rfc822");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);

            try{
                new WishListPDFCreator().generatePDF(wishListLines, fileName + ".pdf", getContext(), mCurrentUser);
            }catch(Exception e) {
                e.printStackTrace();
            }

            //Add the attachment by specifying a reference to our custom ContentProvider
            //and the specific file of interest
            shareIntent.putExtra(Intent.EXTRA_STREAM,  Uri.parse("content://"
                    + CachedFileProvider.AUTHORITY + "/" + fileName + ".pdf"));

            if (getActivity()!=null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mShareActionProvider.setShareIntent(shareIntent);
                    }
                });
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
        if(linearLayoutManager!=null) {
            if (linearLayoutManager instanceof GridLayoutManager) {
                outState.putInt(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION,
                        linearLayoutManager.findFirstVisibleItemPosition());
            } else {
                outState.putInt(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION,
                        linearLayoutManager.findFirstCompletelyVisibleItemPosition());
            }
        } else {
            outState.putInt(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION, mRecyclerViewCurrentFirstPosition);
        }
        super.onSaveInstanceState(outState);
    }
}