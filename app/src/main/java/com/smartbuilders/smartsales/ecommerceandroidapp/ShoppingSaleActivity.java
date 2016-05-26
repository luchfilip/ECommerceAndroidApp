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
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.ShoppingSaleAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Order;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * Jesus Sarco 12.05.2016
 */
public class ShoppingSaleActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String KEY_CURRENT_USER = "KEY_CURRENT_USER";
    public static final String STATE_CURRENT_USER = "state_current_user";

    private User mCurrentUser;
    private ArrayList<OrderLine> mOrderLines;
    private Order mOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_sale);

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

        mOrderLines = (new OrderLineDB(this, mCurrentUser)).getShoppingSale();
        mOrder = new Order();

        if(findViewById(R.id.shoppingSale_items_list) != null) {
            ((ListView) findViewById(R.id.shoppingSale_items_list))
                    .setAdapter(new ShoppingSaleAdapter(this, this, mOrderLines, mCurrentUser));
        }

        if(findViewById(R.id.proceed_to_checkout_shopping_sale_button) != null) {
            findViewById(R.id.proceed_to_checkout_shopping_sale_button)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AlertDialog.Builder(ShoppingSaleActivity.this)
                                    .setMessage(R.string.proceed_to_checkout_quoatation_question)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            OrderDB orderDB = new OrderDB(ShoppingSaleActivity.this, mCurrentUser);
                                            String result = orderDB.createOrderFromShoppingSale();
                                            if(result == null){
                                                Intent intent = new Intent(ShoppingSaleActivity.this, SalesOrderDetailActivity.class);
                                                intent.putExtra(SalesOrderDetailActivity.KEY_CURRENT_USER, mCurrentUser);
                                                intent.putExtra(SalesOrderDetailActivity.KEY_SALES_ORDER, orderDB.getLastFinalizedSalesOrder());
                                                startActivity(intent);
                                                finish();
                                            }else{
                                                new AlertDialog.Builder(ShoppingSaleActivity.this)
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
    }

    @Override
    protected void onResume() {
        if ((mOrderLines==null || mOrderLines.size()==0)
                && findViewById(R.id.company_logo_name)!=null
                && findViewById(R.id.shoppingSale_items_list)!=null
                && findViewById(R.id.shoppingSale_data_linearLayout)!=null) {
            findViewById(R.id.company_logo_name).setVisibility(View.VISIBLE);
            findViewById(R.id.shoppingSale_items_list).setVisibility(View.GONE);
            findViewById(R.id.shoppingSale_data_linearLayout).setVisibility(View.GONE);
        } else {
            fillFields();
        }
        super.onResume();
    }

    public void fillFields(){
        if(findViewById(R.id.total_lines)!=null) {
            ((TextView) findViewById(R.id.total_lines))
                    .setText(getString(R.string.order_lines_number, String.valueOf(mOrderLines.size())));
        }
        double subTotal=0, tax=0, total=0;
        for(OrderLine orderLine : mOrderLines){
            subTotal += orderLine.getQuantityOrdered() * orderLine.getPrice();
            tax += orderLine.getQuantityOrdered() * orderLine.getPrice() * (orderLine.getTaxPercentage()/100);
            total += subTotal + tax;
        }
        if(findViewById(R.id.subTotalAmount_tv)!=null){
            ((TextView) findViewById(R.id.subTotalAmount_tv))
                    .setText(getString(R.string.order_sub_total_amount, String.valueOf(subTotal)));
        }

        if(findViewById(R.id.taxesAmount_tv)!=null){
            ((TextView) findViewById(R.id.taxesAmount_tv))
                    .setText(getString(R.string.order_tax_amount, String.valueOf(tax)));
        }

        if(findViewById(R.id.totalAmount_tv)!=null){
            ((TextView) findViewById(R.id.totalAmount_tv))
                    .setText(getString(R.string.order_total_amount, String.valueOf(total)));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping_sale, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                startActivity(new Intent(this, SearchResultsActivity.class)
                        .putExtra(SearchResultsActivity.KEY_CURRENT_USER, mCurrentUser));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
        super.onSaveInstanceState(outState);
    }
}
