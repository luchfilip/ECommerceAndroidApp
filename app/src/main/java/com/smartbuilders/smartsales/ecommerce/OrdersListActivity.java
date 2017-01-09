package com.smartbuilders.smartsales.ecommerce;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.adapters.OrdersListAdapter;
import com.smartbuilders.smartsales.ecommerce.businessRules.OrderBR;
import com.smartbuilders.smartsales.ecommerce.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.data.OrderDB;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerce.services.SyncDataRealTimeWithServerService;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.model.Order;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.UserProfile;
import com.smartbuilders.synchronizer.ids.syncadapter.SyncAdapter;

/**
 * Jesus Sarco
 */
public class OrdersListActivity extends AppCompatActivity
        implements OrdersListFragment.Callback, OrderDetailFragment.Callback,
        NavigationView.OnNavigationItemSelectedListener {

    public static final String ORDER_DETAIL_FRAGMENT_TAG = "ORDER_DETAIL_FRAGMENT_TAG";
    public static final String KEY_ORDER_ID = "KEY_ORDER_ID";

    private ListView mListView;
    private OrderDB mOrderDB;
    private int mCurrentSelectedIndex;
    private User mUser;
    private int mOrderId;

    private BroadcastReceiver syncDataFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(mListView!=null && mOrderDB!=null){
                int oldListSize;
                if (mListView.getAdapter()==null) {
                    oldListSize = 0;
                    mListView.setAdapter(new OrdersListAdapter(OrdersListActivity.this, mOrderDB.getActiveOrders()));
                } else {
                    oldListSize = mListView.getAdapter().getCount();
                    ((OrdersListAdapter) mListView.getAdapter()).setData(mOrderDB.getActiveOrders());
                }
                if(mListView.getAdapter().getCount()>0){
                    if(oldListSize==0){
                        onListIsLoaded();
                    }else{
                        setSelectedIndex(mCurrentSelectedIndex);
                    }
                }else{
                    onListIsLoaded();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);

        mUser = Utils.getCurrentUser(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, true);
        setSupportActionBar(toolbar);

        Utils.inflateNavigationView(this, this, toolbar, mUser);

        mListView = (ListView) findViewById(R.id.orders_list);
        mOrderDB = new OrderDB(this, mUser);

        if (getIntent()!=null && getIntent().getExtras()!=null) {
            if (getIntent().getExtras().containsKey(KEY_ORDER_ID)) {
                mOrderId = getIntent().getExtras().getInt(KEY_ORDER_ID);
            }
        }
    }

    @Override
    protected void onPostResume() {
        Utils.manageNotificationOnDrawerLayout(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer!=null && drawer.isDrawerOpen(GravityCompat.START)) {
            Utils.loadNavigationViewBadge(getApplicationContext(), Utils.getCurrentUser(this),
                    (NavigationView) findViewById(R.id.nav_view));
        }
        super.onPostResume();
    }

    @Override
    protected void onStart() {
        try {
            IntentFilter intentFilter = new IntentFilter(SyncAdapter.FULL_SYNCHRONIZATION_FINISHED);
            intentFilter.addAction(SyncDataRealTimeWithServerService.SYNCHRONIZATION_FINISHED);
            registerReceiver(syncDataFinishedReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(/*findViewById(R.id.order_detail_container)==null
                && */(mUser!=null && (BuildConfig.IS_SALES_FORCE_SYSTEM
                || mUser.getUserProfileId()==UserProfile.SALES_MAN_PROFILE_ID))){
            try {
                BusinessPartner businessPartner = (new BusinessPartnerDB(this, mUser))
                        .getBusinessPartnerById(Utils.getAppCurrentBusinessPartnerId(this, mUser));
                if(businessPartner!=null){
                    ((TextView) findViewById(R.id.business_partner_name)).setText(businessPartner.getName());
                    findViewById(R.id.business_partner_name_container).setVisibility(View.VISIBLE);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try{
            unregisterReceiver(syncDataFinishedReceiver);
        }catch(Exception e){
            //do nothing
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_orders_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.search) {
            startActivity(new Intent(this, SearchResultsActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListIsLoaded() {
        if (findViewById(R.id.order_detail_container) != null) {
            if (mListView != null && mListView.getAdapter()!=null && !mListView.getAdapter().isEmpty()) {
                int position = 0;
                if (mOrderId!=0 && ((OrdersListAdapter) mListView.getAdapter()).getData()!=null) {
                    for (int i = 0; i < mListView.getAdapter().getCount(); i++) {
                        if (((OrdersListAdapter) mListView.getAdapter()).getData().get(i)!=null
                                && ((OrdersListAdapter) mListView.getAdapter()).getData().get(i).getId()==mOrderId) {
                            position = i;
                            break;
                        }
                    }
                }
                mListView.performItemClick(mListView.getAdapter().getView(position, null, null), position, 0);
                if (position>0) {
                    mListView.smoothScrollToPosition(position);
                }
            }
        }
        showOrHideEmptyLayoutWallpaper();
    }

    private void showOrHideEmptyLayoutWallpaper() {
        if (mListView != null && mListView.getAdapter()!=null && !mListView.getAdapter().isEmpty()) {
            if (findViewById(R.id.main_layout) != null) {
                findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
            }
            if (findViewById(R.id.order_detail_container) != null) {
                findViewById(R.id.order_detail_container).setVisibility(View.VISIBLE);
            }
            if (findViewById(R.id.empty_layout_wallpaper) != null) {
                findViewById(R.id.empty_layout_wallpaper).setVisibility(View.GONE);
            }
        } else {
            if (findViewById(R.id.empty_layout_wallpaper) != null) {
                findViewById(R.id.empty_layout_wallpaper).setVisibility(View.VISIBLE);
            }
            if (findViewById(R.id.order_detail_container) != null) {
                findViewById(R.id.order_detail_container).setVisibility(View.GONE);
            }
            if (findViewById(R.id.main_layout) != null) {
                findViewById(R.id.main_layout).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void setSelectedIndex(int selectedIndex) {
        if (findViewById(R.id.order_detail_container) != null) {
            mCurrentSelectedIndex = selectedIndex;
            if (mListView!=null && mListView.getAdapter()!=null
                    && mListView.getAdapter().getCount()>selectedIndex) {
                mListView.setSelection(selectedIndex);
                mListView.setItemChecked(selectedIndex, true);
            }
        }
    }

    @Override
    public void onItemSelected(Order order, int selectedIndex) {
        if(findViewById(R.id.order_detail_container) != null){
            mCurrentSelectedIndex = selectedIndex;
            Bundle args = new Bundle();
            args.putInt(OrderDetailActivity.KEY_ORDER_ID, order.getId());

            OrderDetailFragment fragment = new OrderDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.order_detail_container, fragment, ORDER_DETAIL_FRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(this, OrderDetailActivity.class);
            intent.putExtra(OrderDetailActivity.KEY_ORDER_ID, order.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onItemLongSelected(final Order order) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.delete_order_question, order.getOrderNumber()))
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String result = OrderBR.deactivateOrderById(OrdersListActivity.this,
                                Utils.getCurrentUser(OrdersListActivity.this), order.getId());
                        if (result==null) {
                            reloadOrdersList();
                        } else {
                            Toast.makeText(OrdersListActivity.this, result, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    public void reloadOrdersList() {
        if (mListView != null && mOrderDB != null) {
            if (mListView.getAdapter() != null) {
                ((OrdersListAdapter) mListView.getAdapter()).setData(mOrderDB.getActiveOrders());
            } else {
                mListView.setAdapter(new OrdersListAdapter(this, mOrderDB.getActiveOrders()));
            }
        }
        onListIsLoaded();
    }

    @Override
    public void orderDetailLoaded() {
        //do nothing
    }

    @Override
    public boolean isFragmentMenuVisible() {
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer!=null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Utils.navigationItemSelectedBehave(item.getItemId(), this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer!=null){
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }
}