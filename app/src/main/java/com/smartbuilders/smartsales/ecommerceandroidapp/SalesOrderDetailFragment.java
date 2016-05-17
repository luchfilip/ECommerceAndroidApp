package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.SalesOrderLineAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Order;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.providers.CachedFileProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.ShoppingCartPDFCreator;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.ShoppingSalePDFCreator;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class SalesOrderDetailFragment extends Fragment {

    private User mCurrentUser;
    private Order mOrder;
    private ShareActionProvider mShareActionProvider;
    private ArrayList<OrderLine> mOrderLines;

    public SalesOrderDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sales_order_detail, container, false);

        setHasOptionsMenu(true);

        if (getArguments() != null) {
            if (getArguments().containsKey(SalesOrderDetailActivity.KEY_SALES_ORDER)) {
                mOrder = getArguments().getParcelable(SalesOrderDetailActivity.KEY_SALES_ORDER);
            }
            if (getArguments().containsKey(SalesOrderDetailActivity.KEY_CURRENT_USER)) {
                mCurrentUser = getArguments().getParcelable(SalesOrderDetailActivity.KEY_CURRENT_USER);
            }
        } else if (getActivity().getIntent() != null && getActivity().getIntent().getExtras() != null) {
            if (getActivity().getIntent().getExtras().containsKey(SalesOrderDetailActivity.KEY_CURRENT_USER)) {
                mCurrentUser = getActivity().getIntent().getExtras().getParcelable(SalesOrderDetailActivity.KEY_CURRENT_USER);
            }
            if (getActivity().getIntent().getExtras().containsKey(SalesOrderDetailActivity.KEY_SALES_ORDER)) {
                mOrder = getActivity().getIntent().getExtras().getParcelable(SalesOrderDetailActivity.KEY_SALES_ORDER);
            }
        }

        if (mOrder == null) {
            mOrder = (new OrderDB(getContext(), mCurrentUser)).getLastFinalizedSalesOrder();
        }
        if (mOrder != null) {
            mOrderLines = new OrderLineDB(getContext(), mCurrentUser)
                    .getActiveFinalizedSalesOrderLinesByOrderId(mOrder.getId());

            RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.sales_order_lines);
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setAdapter(new SalesOrderLineAdapter(mOrderLines, mCurrentUser));

            ((TextView) rootView.findViewById(R.id.sales_order_lines_number_tv))
                    .setText(getContext().getString(R.string.order_lines_number, String.valueOf(mOrder.getOrderLinesNumber())));

            ((TextView) rootView.findViewById(R.id.sales_order_number_tv))
                    .setText(getContext().getString(R.string.sales_order_number, mOrder.getOrderNumber()));

            ((TextView) rootView.findViewById(R.id.sales_order_date_tv))
                    .setText(getContext().getString(R.string.order_date, mOrder.getCreatedStringFormat()));

            if(rootView.findViewById(R.id.sales_order_sub_total_tv) != null) {
                ((TextView) rootView.findViewById(R.id.sales_order_sub_total_tv))
                        .setText(getContext().getString(R.string.order_sub_total_amount, String.valueOf(mOrder.getSubTotalAmount())));
            }
            if(rootView.findViewById(R.id.sales_order_total_tv) != null) {
                ((TextView) rootView.findViewById(R.id.sales_order_total_tv))
                        .setText(getContext().getString(R.string.order_total_amount, String.valueOf(mOrder.getTotalAmount())));
            }
            if(rootView.findViewById(R.id.sales_order_tax_tv) != null) {
                ((TextView) rootView.findViewById(R.id.sales_order_tax_tv))
                        .setText(getContext().getString(R.string.order_tax_amount, String.valueOf(mOrder.getTaxAmount())));
            }
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_sales_order_detail, menu);

        // Retrieve the share menu item
        MenuItem item = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        // Attach an intent to this ShareActionProvider. You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mOrderLines != null && mOrderLines.size() > 0) {
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
            if (mOrderLines != null && mOrderLines.size() > 0) {
                //mShareActionProvider.setShareHistoryFileName(null);
                mShareActionProvider.setShareIntent(createShareProductIntent());
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent createShareProductIntent(){
        String fileName = "Cotizacion";
        String subject = "";
        String message = "";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        // need this to prompts email client only
        shareIntent.setType("message/rfc822");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);

        try{
            new ShoppingSalePDFCreator().generatePDF(mOrder, mOrderLines, fileName+".pdf", getContext(), mCurrentUser);
        }catch(Exception e){
            e.printStackTrace();
        }

        //Add the attachment by specifying a reference to our custom ContentProvider
        //and the specific file of interest
        //shareIntent.putExtra(Intent.EXTRA_STREAM,  Uri.parse("content://"
        //        + CachedFileProvider.AUTHORITY + "/" + fileName + ".pdf"));
        return shareIntent;
    }

    class CreateShareIntentThread extends Thread {
        public void run() {
            final Intent shareIntent = createShareProductIntent();
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mShareActionProvider.setShareIntent(shareIntent);
                    }
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
