package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.providers.CachedFileProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.WishListPDFCreator;

import java.util.ArrayList;

/**
 * Created by Alberto on 22/3/2016.
 */
public class WishListFragment extends Fragment implements WishListAdapter.Callback {

    private static final String STATE_CURRENT_USER = "state_current_user";
    private User mCurrentUser;
    private ShareActionProvider mShareActionProvider;
    private ArrayList<OrderLine> wishListLines;
    private WishListAdapter mWishListAdapter;

    public WishListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        final View view = inflater.inflate(R.layout.fragment_wish_list, container, false);

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        if(getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null) {
            if(getActivity().getIntent().getExtras().containsKey(WishListActivity.KEY_CURRENT_USER)){
                mCurrentUser = getActivity().getIntent().getExtras().getParcelable(WishListActivity.KEY_CURRENT_USER);
            }
        }

        wishListLines = (new OrderLineDB(getActivity(), mCurrentUser)).getWishList();

        if ((wishListLines==null || wishListLines.size()==0)
                && view.findViewById(R.id.company_logo_name)!=null) {
            if (view.findViewById(R.id.company_logo_name)!=null) {
                view.findViewById(R.id.company_logo_name).setVisibility(View.VISIBLE);
            }
            if (view.findViewById(R.id.wish_list) != null) {
                view.findViewById(R.id.wish_list).setVisibility(View.VISIBLE);
            }
        } else {
            mWishListAdapter = new WishListAdapter(getContext(), this, wishListLines, mCurrentUser);

            ListView listView = (ListView) view.findViewById(R.id.wish_list);
            listView.setAdapter(mWishListAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                    // CursorAdapter returns a cursor at the correct position for getItem(), or null
                    // if it cannot seek to that position.
                    OrderLine orderLine = (OrderLine) adapterView.getItemAtPosition(position);
                    if (orderLine != null) {
                        Intent intent = new Intent(getContext(), ProductDetailActivity.class);
                        intent.putExtra(ProductDetailActivity.KEY_CURRENT_USER, mCurrentUser);
                        intent.putExtra(ProductDetailFragment.KEY_PRODUCT, orderLine.getProduct());
                        startActivity(intent);
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void moveToShoppingCart(OrderLine orderLine) {
        DialogMoveToShoppingCart dialogMoveToShoppingCart =
                DialogMoveToShoppingCart.newInstance(orderLine, mCurrentUser);
        dialogMoveToShoppingCart.setTargetFragment(this, 0);
        dialogMoveToShoppingCart.show(getActivity().getSupportFragmentManager(),
                DialogMoveToShoppingCart.class.getSimpleName());
    }

    public void reloadWishList(){
        wishListLines = (new OrderLineDB(getActivity(), mCurrentUser)).getWishList();
        mWishListAdapter.setData(wishListLines);
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
            /*if (wishListLines != null && wishListLines.size() > 0) {
                mShareActionProvider.setShareIntent(createShareProductIntent());
            }*/
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent createShareProductIntent(){
        String fileName = "ListaDeDeseos";
        String subject = "";
        String message = "";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
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
        return shareIntent;
    }

    class CreateShareIntentThread extends Thread {
        public void run() {
            final Intent shareIntent = createShareProductIntent();
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
        outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
        super.onSaveInstanceState(outState);
    }
}