package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.WishListAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.WishListLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.providers.CachedFileProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.WishListPDFCreator;

import java.util.ArrayList;

public class WishListActivity extends AppCompatActivity {

    private static final String TAG = WishListActivity.class.getSimpleName();
    public static final String KEY_CURRENT_USER = "KEY_CURRENT_USER";
    public static final String STATE_CURRENT_USER = "state_current_user";
    private User mCurrentUser;
    private ListView mListView;
    private WishListAdapter mWishListAdapter;
    ArrayList<WishListLine> wishListLines;

    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);



        wishListLines = new ArrayList<WishListLine>();
        WishListLine wl = new WishListLine();
        wl.setId(1);
        Product p = new Product();
        p.setName("Bomba 1/2 hp periferica pedrollo");
        p.setImageId(R.drawable.product1);
        wl.setProduct(p);
        wishListLines.add(wl);

        wl = new WishListLine();
        wl.setId(2);
        p = new Product();
        p.setName("Capacitador con terminal p/bomba 1/2hp");
        p.setImageId(R.drawable.product2);
        wl.setProduct(p);
        wishListLines.add(wl);

        wl = new WishListLine();
        wl.setId(3);
        p = new Product();
        p.setName("Capacitor 25uf semilic");
        p.setImageId(R.drawable.product3);
        wl.setProduct(p);
        wishListLines.add(wl);

        wl = new WishListLine();
        wl.setId(4);
        p = new Product();
        p.setName("Cargador de aire 100gl tm");
        p.setImageId(R.drawable.product4);
        wl.setProduct(p);
        wishListLines.add(wl);

        wl = new WishListLine();
        wl.setId(5);
        p = new Product();
        p.setName("Manometro 0-90psi semilic");
        p.setImageId(R.drawable.product5);
        wl.setProduct(p);
        wishListLines.add(wl);

        wl = new WishListLine();
        wl.setId(6);
        p = new Product();
        p.setName("Mini presostato 20-40 semilic");
        p.setImageId(R.drawable.product6);
        wl.setProduct(p);
        wishListLines.add(wl);

        wl = new WishListLine();
        wl.setId(7);
        p = new Product();
        p.setName("Presostato 20-40 semilic");
        p.setImageId(R.drawable.product7);
        wl.setProduct(p);
        wishListLines.add(wl);

        mWishListAdapter = new WishListAdapter(this, wishListLines);

        mListView = (ListView) findViewById(R.id.wish_list);
        mListView.setAdapter(mWishListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wishlist, menu);

        // Retrieve the share menu item
        MenuItem item =(MenuItem) menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        // Attach an intent to this ShareActionProvider. You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (wishListLines != null) {
            new CreateShareIntentThread().start();
            //mShareActionProvider.setShareIntent(createShareProductIntent());
        } else {
            Log.d(TAG, "Share Action Provider is null?");
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected(...)");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            if (wishListLines != null) {
                mShareActionProvider.setShareIntent(createShareProductIntent());
            } else {
                Log.d(TAG, "Share Action Provider is null?");
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
            new WishListPDFCreator().generatePDF(wishListLines, fileName + ".pdf", this);
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
}
