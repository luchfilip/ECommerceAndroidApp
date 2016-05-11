package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.SearchResultAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.WishListAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ProductDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.providers.CachedFileProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.WishListPDFCreator;

import java.util.ArrayList;

public class WishListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = WishListActivity.class.getSimpleName();
    public static final String KEY_CURRENT_USER = "KEY_CURRENT_USER";
    public static final String STATE_CURRENT_USER = "state_current_user";
    private User mCurrentUser;
    private ListView mListView;
    private WishListAdapter mWishListAdapter;
    ArrayList<OrderLine> wishListLines;
    private ShareActionProvider mShareActionProvider;
    private ProductDB productDB;
    private ListView mListViewSearchResults;
    private SearchResultAdapter mSearchResultAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);

        if( savedInstanceState != null ) {
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        if(getIntent()!=null && getIntent().getExtras()!=null){
            if(getIntent().getExtras().containsKey(KEY_CURRENT_USER)){
                mCurrentUser = getIntent().getExtras().getParcelable(KEY_CURRENT_USER);
            }
        }

        if(findViewById(R.id.title_textView) != null){
            ((TextView) findViewById(R.id.title_textView))
                    .setTypeface(Typeface.createFromAsset(getAssets(), "MyriadPro-Bold.otf"));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        Utils.setCustomToolbarTitle(this, toolbar, mCurrentUser, true);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        try{
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name))
                    .setText(mCurrentUser.getUserName());
        }catch(Exception e){
            e.printStackTrace();
        }

        OrderLineDB orderLineDB = new OrderLineDB(this, mCurrentUser);
        wishListLines = orderLineDB.getWishList();

        mWishListAdapter = new WishListAdapter(this, wishListLines, mCurrentUser);

        mListView = (ListView) findViewById(R.id.wish_list);
        mListView.setAdapter(mWishListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                OrderLine orderLine = (OrderLine) adapterView.getItemAtPosition(position);
                if (orderLine != null) {
                    Intent intent = new Intent(WishListActivity.this, ProductDetailActivity.class);
                    intent.putExtra(ProductDetailActivity.KEY_CURRENT_USER, mCurrentUser);
                    intent.putExtra(ProductDetailFragment.KEY_PRODUCT, orderLine.getProduct());
                    startActivity(intent);
                }
            }
        });

        productDB = new ProductDB(this, mCurrentUser);

        mSearchResultAdapter = new SearchResultAdapter(this, new ArrayList<Product>(), mCurrentUser);

        mListViewSearchResults = (ListView) findViewById(R.id.search_result_list);
        mListViewSearchResults.setAdapter(mSearchResultAdapter);

        if ((wishListLines==null || wishListLines.size()==0)
                && findViewById(R.id.company_logo_name)!=null) {
            findViewById(R.id.company_logo_name).setVisibility(View.VISIBLE);
            mListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wishlist, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent intent = new Intent(WishListActivity.this, ProductsListActivity.class);
                intent.putExtra(ProductsListActivity.KEY_CURRENT_USER, mCurrentUser);
                intent.putExtra(ProductsListActivity.KEY_PRODUCT_NAME, s);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mSearchResultAdapter.setData(productDB.getLightProductsByName(s), WishListActivity.this);
                mSearchResultAdapter.notifyDataSetChanged();
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mListViewSearchResults.setVisibility(View.VISIBLE);
                findViewById(R.id.wish_list).setVisibility(View.GONE);
                if(findViewById(R.id.title_textView) != null) {
                    findViewById(R.id.title_textView).setVisibility(View.GONE);
                }
                if ((wishListLines==null || wishListLines.size()==0)
                        && findViewById(R.id.company_logo_name)!=null) {
                    findViewById(R.id.company_logo_name).setVisibility(View.GONE);
                }
                mSearchResultAdapter.setData(new ArrayList<Product>(), WishListActivity.this);
                mSearchResultAdapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mListViewSearchResults.setVisibility(View.GONE);
                findViewById(R.id.wish_list).setVisibility(View.VISIBLE);
                if(findViewById(R.id.title_textView) != null) {
                    findViewById(R.id.title_textView).setVisibility(View.VISIBLE);
                }
                if ((wishListLines==null || wishListLines.size()==0)
                        && findViewById(R.id.company_logo_name)!=null) {
                    findViewById(R.id.company_logo_name).setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

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
        return super.onCreateOptionsMenu(menu);
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
                mShareActionProvider.setShareIntent(createShareProductIntent());
            }
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
            new WishListPDFCreator().generatePDF(wishListLines, fileName + ".pdf", this, mCurrentUser);
        }catch(Exception e){
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mShareActionProvider.setShareIntent(shareIntent);
                }
            });
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Utils.navigationItemSelectedBehave(item.getItemId(), this, mCurrentUser);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
