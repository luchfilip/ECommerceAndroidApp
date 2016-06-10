package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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

    private static final String STATE_LISTVIEW_INDEX = "STATE_LISTVIEW_INDEX";
    private static final String STATE_LISTVIEW_TOP = "STATE_LISTVIEW_TOP";

    // save index and top position
    int mListViewIndex;
    int mListViewTop;

    private ListView mListView;

    private User mCurrentUser;
    private ShareActionProvider mShareActionProvider;

    private WishListAdapter mWishListAdapter;
    private ArrayList<OrderLine> wishListLines;
    private Intent shareIntent;

    public WishListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        final View view = inflater.inflate(R.layout.fragment_wish_list, container, false);
        mCurrentUser = Utils.getCurrentUser(getContext());

        wishListLines = new ArrayList<>();
        mWishListAdapter = new WishListAdapter(getContext(), this, wishListLines, mCurrentUser);

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState != null) {
                        if(savedInstanceState.containsKey(STATE_LISTVIEW_INDEX)){
                            mListViewIndex = savedInstanceState.getInt(STATE_LISTVIEW_INDEX);
                        }
                        if(savedInstanceState.containsKey(STATE_LISTVIEW_TOP)){
                            mListViewTop = savedInstanceState.getInt(STATE_LISTVIEW_TOP);
                        }
                    }
                    wishListLines.addAll((new OrderLineDB(getActivity(), mCurrentUser)).getWishList());
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
                                    mListView = (ListView) view.findViewById(R.id.wish_list);
                                    mListView.setAdapter(mWishListAdapter);

                                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                        @Override
                                        public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                                            // CursorAdapter returns a cursor at the correct position for getItem(), or null
                                            // if it cannot seek to that position.
                                            OrderLine orderLine = (OrderLine) adapterView.getItemAtPosition(position);
                                            if (orderLine != null) {
                                                Intent intent = new Intent(getContext(), ProductDetailActivity.class);
                                                intent.putExtra(ProductDetailActivity.KEY_PRODUCT_ID, orderLine.getProduct().getId());
                                                startActivity(intent);
                                            }
                                        }
                                    });

                                    mListView.setSelectionFromTop(mListViewIndex, mListViewTop);
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
        wishListLines = (new OrderLineDB(getActivity(), user)).getWishList();
        mWishListAdapter.setData(wishListLines);
        //Se debe recargar el documento pdf que se tiene para compartir
        new CreateShareIntentThread().start();
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

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mShareActionProvider.setShareIntent(shareIntent);
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        try {
            outState.putInt(STATE_LISTVIEW_INDEX, mListView.getFirstVisiblePosition());
        } catch (Exception e) {
            outState.putInt(STATE_LISTVIEW_INDEX, mListViewIndex);
        }
        try {
            outState.putInt(STATE_LISTVIEW_TOP, (mListView.getChildAt(0) == null) ? 0 :
                    (mListView.getChildAt(0).getTop() - mListView.getPaddingTop()));
        } catch (Exception e) {
            outState.putInt(STATE_LISTVIEW_TOP, mListViewTop);
        }
        super.onSaveInstanceState(outState);
    }
}