package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.ShoppingCartAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

public class ShoppingCartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String KEY_CURRENT_USER = "KEY_CURRENT_USER";
    public static final String STATE_CURRENT_USER = "state_current_user";

    private User mCurrentUser;
//    private ShareActionProvider mShareActionProvider;
    private ArrayList<OrderLine> mOrderLines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoping_cart);

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
                    .setText(getString(R.string.welcome_user, mCurrentUser.getUserName()));
        }catch(Exception e){
            e.printStackTrace();
        }

        mOrderLines = (new OrderLineDB(this, mCurrentUser)).getShoppingCart();

        if(findViewById(R.id.shoppingCart_items_list) != null) {
            ((ListView) findViewById(R.id.shoppingCart_items_list))
                    .setAdapter(new ShoppingCartAdapter(this, this, mOrderLines, mCurrentUser));
        }

        if(findViewById(R.id.proceed_to_checkout_button) != null) {
            findViewById(R.id.proceed_to_checkout_button)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AlertDialog.Builder(ShoppingCartActivity.this)
                                    .setMessage(R.string.proceed_to_checkout_question)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            OrderDB orderDB = new OrderDB(ShoppingCartActivity.this, mCurrentUser);
                                            String result = orderDB.createOrderFromShoppingCart();
                                            if(result == null){
                                                Intent intent = new Intent(ShoppingCartActivity.this, OrderDetailActivity.class);
                                                intent.putExtra(OrderDetailActivity.KEY_CURRENT_USER, mCurrentUser);
                                                intent.putExtra(OrderDetailActivity.KEY_ORDER, orderDB.getLastFinalizedOrder());
                                                startActivity(intent);
                                                finish();
                                            }else{
                                                new AlertDialog.Builder(ShoppingCartActivity.this)
                                                        .setMessage(result)
                                                        .setNeutralButton(android.R.string.ok, null)
                                                        .show();
                                            }
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null)
                                    .show();
                        }
                    });
        }

        if ((mOrderLines==null || mOrderLines.size()==0)
                && findViewById(R.id.company_logo_name)!=null
                && findViewById(R.id.shoppingCart_items_list)!=null
                && findViewById(R.id.shoppingCart_data_linearLayout)!=null) {
            findViewById(R.id.company_logo_name).setVisibility(View.VISIBLE);
            findViewById(R.id.shoppingCart_items_list).setVisibility(View.GONE);
            findViewById(R.id.shoppingCart_data_linearLayout).setVisibility(View.GONE);
        } else {
            ((TextView) findViewById(R.id.total_lines))
                    .setText(getString(R.string.order_lines_number, String.valueOf(mOrderLines.size())));
        }

        //if(findViewById(R.id.search_bar_linear_layout)!=null){
        //    findViewById(R.id.search_by_button).setOnClickListener(new View.OnClickListener() {
        //        @Override
        //        public void onClick(View v) {
        //            startActivity(new Intent(ShoppingCartActivity.this, FilterOptionsActivity.class)
        //                    .putExtra(FilterOptionsActivity.KEY_CURRENT_USER, mCurrentUser));
        //        }
        //    });
        //
        //    findViewById(R.id.search_product_editText).setOnClickListener(new View.OnClickListener() {
        //        @Override
        //        public void onClick(View v) {
        //            startActivity(new Intent(ShoppingCartActivity.this, SearchResultsActivity.class)
        //                    .putExtra(FilterOptionsActivity.KEY_CURRENT_USER, mCurrentUser));
        //        }
        //    });
        //
        //    findViewById(R.id.image_search_bar_layout).setOnClickListener(new View.OnClickListener() {
        //        @Override
        //        public void onClick(View v) {
        //            startActivity(new Intent(ShoppingCartActivity.this, SearchResultsActivity.class)
        //                    .putExtra(FilterOptionsActivity.KEY_CURRENT_USER, mCurrentUser));
        //        }
        //    });
        //}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping_cart, menu);

        //// Get the provider and hold onto it to set/change the share intent.
        //mShareActionProvider =  (ShareActionProvider) MenuItemCompat
        //        .getActionProvider(menu.findItem(R.id.action_share));

        // Attach an intent to this ShareActionProvider. You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        //if (mOrderLines != null && mOrderLines.size() > 0) {
        //    new CreateShareIntentThread().start();
        //}
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //case R.id.action_share:
            //    if (mOrderLines != null && mOrderLines.size() > 0) {
            //        mShareActionProvider.setShareIntent(createShareShoppingCartIntent());
            //    }
            //    return true;
            case R.id.search:
                startActivity(new Intent(this, SearchResultsActivity.class)
                        .putExtra(FilterOptionsActivity.KEY_CURRENT_USER, mCurrentUser));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //private Intent createShareShoppingCartIntent(){
    //    String fileName = "OrdenDePedido";
    //    String subject = "";
    //    String message = "";
    //
    //    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    //    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    //    // need this to prompts email client only
    //    shareIntent.setType("message/rfc822");
    //    shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
    //    shareIntent.putExtra(Intent.EXTRA_TEXT, message);
    //
    //    try{
    //        new OrderDetailPDFCreator().generatePDF(mOrderLines, fileName+".pdf", this, mCurrentUser);
    //    }catch(Exception e){
    //        e.printStackTrace();
    //    }
    //
    //    //Add the attachment by specifying a reference to our custom ContentProvider
    //    //and the specific file of interest
    //    //shareIntent.putExtra(Intent.EXTRA_STREAM,  Uri.parse("content://"
    //    //        + CachedFileProvider.AUTHORITY + "/" + fileName + ".pdf"));
    //    return shareIntent;
    //}

    //class CreateShareIntentThread extends Thread {
    //    public void run() {
    //        ShoppingCartActivity.this.runOnUiThread(new Runnable() {
    //            @Override
    //            public void run() {
    //                mShareActionProvider.setShareIntent(createShareShoppingCartIntent());
    //            }
    //        });
    //    }
    //}

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Utils.navigationItemSelectedBehave(item.getItemId(), this, mCurrentUser);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
        super.onSaveInstanceState(outState);
    }

}
