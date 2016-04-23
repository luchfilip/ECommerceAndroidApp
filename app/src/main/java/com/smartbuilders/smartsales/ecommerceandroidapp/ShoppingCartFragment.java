package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.ShoppingCartAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.WishListAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.WishListLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.providers.CachedFileProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.ShoppingCartPDFCreator;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShoppingCartFragment extends Fragment {

    private static String TAG = ShoppingCartFragment.class.getSimpleName();
    private ShareActionProvider mShareActionProvider;
    private ArrayList<OrderLine> orderLines;

    private ListView mListView;
    private ShoppingCartAdapter mShoppingCartAdapter;

    public ShoppingCartFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_shoping_cart, container, false);

        orderLines = new ArrayList<OrderLine>();
        OrderLine orderLine = new OrderLine();
        Product p = new Product();
        p.setName("Bomba 1/2 hp periferica pedrollo");
        p.setImageId(R.drawable.product1);
        orderLine.setProduct(p);
        orderLines.add(orderLine);

        orderLine = new OrderLine();
        p = new Product();
        p.setName("Capacitador con terminal p/bomba 1/2hp");
        p.setImageId(R.drawable.product2);
        orderLine.setProduct(p);
        orderLines.add(orderLine);

        orderLine = new OrderLine();
        p = new Product();
        p.setName("Capacitor 25uf semilic");
        p.setImageId(R.drawable.product3);
        orderLine.setProduct(p);
        orderLines.add(orderLine);

        orderLine = new OrderLine();
        p = new Product();
        p.setName("Cargador de aire 100gl tm");
        p.setImageId(R.drawable.product4);
        orderLine.setProduct(p);
        orderLines.add(orderLine);

        orderLine = new OrderLine();
        p = new Product();
        p.setName("Manometro 0-90psi semilic");
        p.setImageId(R.drawable.product5);
        orderLine.setProduct(p);
        orderLines.add(orderLine);

        orderLine = new OrderLine();
        p = new Product();
        p.setName("Mini presostato 20-40 semilic");
        p.setImageId(R.drawable.product6);
        orderLine.setProduct(p);
        orderLines.add(orderLine);

        orderLine = new OrderLine();
        p = new Product();
        p.setName("Presostato 20-40 semilic");
        p.setImageId(R.drawable.product7);
        orderLine.setProduct(p);
        orderLines.add(orderLine);

        orderLine = new OrderLine();
        p = new Product();
        p.setName("Rolinera para bomba 1/2hp");
        p.setImageId(R.drawable.product8);
        orderLine.setProduct(p);
        orderLines.add(orderLine);

        orderLine = new OrderLine();
        p = new Product();
        p.setName("Aspersor pico blanco 3/16\" agroinplast");
        p.setImageId(R.drawable.product9);
        orderLine.setProduct(p);
        orderLines.add(orderLine);

        orderLine = new OrderLine();
        p = new Product();
        p.setName("Aspersor oscilante bv");
        p.setImageId(R.drawable.product10);
        orderLine.setProduct(p);
        orderLines.add(orderLine);

        orderLine = new OrderLine();
        p = new Product();
        p.setName("Aspersor plastic triple bv");
        p.setImageId(R.drawable.product11);
        orderLine.setProduct(p);
        orderLines.add(orderLine);

        orderLine = new OrderLine();
        p = new Product();
        p.setName("Aspersor plastico triple chesterwood");
        p.setImageId(R.drawable.product12);
        orderLine.setProduct(p);
        orderLines.add(orderLine);

        mShoppingCartAdapter = new ShoppingCartAdapter(getActivity(), orderLines);

        mListView = (ListView) view.findViewById(R.id.shoppingCart_items_list);
        mListView.setAdapter(mShoppingCartAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        ((Button) view.findViewById(R.id.proceed_to_checkout_button))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), CheckoutActivity.class));
                    }
                });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_shoping_cart, menu);

        // Retrieve the share menu item
        MenuItem item =(MenuItem) menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        // Attach an intent to this ShareActionProvider. You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (orderLines != null) {
            new CreateShareIntentThread().start();
            //mShareActionProvider.setShareIntent(createShareProductIntent());
        } else {
            Log.d(TAG, "Share Action Provider is null?");
        }
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
            if (orderLines != null) {
                mShareActionProvider.setShareIntent(createShareProductIntent());
            } else {
                Log.d(TAG, "Share Action Provider is null?");
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent createShareProductIntent(){
        String fileName = "OrdenDePedido";
        String subject = "";
        String message = "";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        // need this to prompts email client only
        shareIntent.setType("message/rfc822");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);

        try{
            new ShoppingCartPDFCreator().generatePDF(orderLines, fileName+".pdf", getContext());
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mShareActionProvider.setShareIntent(shareIntent);
                }
            });
        }
    }
}
