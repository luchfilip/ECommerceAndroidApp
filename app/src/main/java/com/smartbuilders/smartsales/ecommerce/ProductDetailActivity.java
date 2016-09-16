package com.smartbuilders.smartsales.ecommerce;

import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerce.bluetoothchat.BluetoothChatService;
import com.smartbuilders.smartsales.ecommerce.providers.BluetoothConnectionProvider;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.model.UserProfile;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 * Created by Alberto on 22/3/2016.
 */
public class ProductDetailActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String KEY_PRODUCT_ID = "key_product_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        final User user = Utils.getCurrentUser(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, true);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view); //must call super
                Utils.loadNavigationViewBadge(getApplicationContext(), user,
                        (NavigationView) findViewById(R.id.nav_view));
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(navigationView!=null && user!=null){
            if(user.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                navigationView.inflateMenu(R.menu.business_partner_drawer_menu);
            }else if(user.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
                navigationView.inflateMenu(R.menu.sales_man_drawer_menu);
            }
            navigationView.setNavigationItemSelectedListener(this);
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name))
                    .setText(getString(R.string.welcome_user, user.getUserName()));
        }

        // Se envia el id del producto por bluetooth
        if(getIntent()!=null && getIntent().getExtras()!=null) {
            if(getIntent().getExtras().containsKey(KEY_PRODUCT_ID)){
                String message = String.valueOf(getIntent().getExtras().getInt(KEY_PRODUCT_ID));
                // Check that there's actually something to send
                if (!TextUtils.isEmpty(message)){
                    Cursor cursor = null;
                    try {
                        cursor = getContentResolver().query(BluetoothConnectionProvider.GET_CHAT_SERVICE_STATE_URI, null, null, null, null);
                        // Check that we're actually connected before trying anything
                        if(cursor!=null && cursor.moveToNext() && cursor.getInt(0)==BluetoothChatService.STATE_CONNECTED){
                            // Get the message and tell the BluetoothChatService to write
                            getContentResolver().query(BluetoothConnectionProvider.SEND_MESSAGE_URI.buildUpon()
                                    .appendQueryParameter(BluetoothConnectionProvider.KEY_MESSAGE, message).build(), null, null, null, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor!=null) {
                            try {
                                cursor.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Utils.navigationItemSelectedBehave(item.getItemId(), this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}