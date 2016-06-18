package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
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
import com.smartbuilders.smartsales.ecommerceandroidapp.data.SalesOrderDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.SalesOrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.SalesOrder;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.SalesOrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.providers.CachedFileProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.SalesOrderDetailPDFCreator;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class SalesOrderDetailFragment extends Fragment {

    private static final String STATE_SALES_ORDER_ID = "STATE_SALES_ORDER_ID";
    private static final String STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION = "STATE_LISTVIEW_CURRENT_FIRST_POSITION";

    private User mCurrentUser;
    private int mSalesOrderId;
    private SalesOrder mSalesOrder;
    private LinearLayoutManager mLinearLayoutManager;
    private int mRecyclerViewCurrentFirstPosition;
    private ShareActionProvider mShareActionProvider;
    private Intent mShareIntent;

    public SalesOrderDetailFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sales_order_detail, container, false);

        final ArrayList<SalesOrderLine> orderLines = new ArrayList<>();

        new Thread() {
            @Override
            public void run() {
                try {
                    if (savedInstanceState!=null) {
                        if (savedInstanceState.containsKey(STATE_SALES_ORDER_ID)) {
                            mSalesOrderId = savedInstanceState.getInt(STATE_SALES_ORDER_ID);
                        }
                        if (savedInstanceState.containsKey(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION)) {
                            mRecyclerViewCurrentFirstPosition = savedInstanceState.getInt(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION);
                        }
                    }

                    if (getArguments() != null) {
                        if (getArguments().containsKey(SalesOrderDetailActivity.KEY_SALES_ORDER_ID)) {
                            mSalesOrderId = getArguments().getInt(SalesOrderDetailActivity.KEY_SALES_ORDER_ID);
                        }
                    } else if (getActivity().getIntent() != null && getActivity().getIntent().getExtras() != null) {
                        if (getActivity().getIntent().getExtras().containsKey(SalesOrderDetailActivity.KEY_SALES_ORDER_ID)) {
                            mSalesOrderId = getActivity().getIntent().getExtras().getInt(SalesOrderDetailActivity.KEY_SALES_ORDER_ID);
                        }
                    }

                    mCurrentUser = Utils.getCurrentUser(getContext());

                    if (mSalesOrderId>0) {
                        mSalesOrder = (new SalesOrderDB(getContext(), mCurrentUser)).getActiveSalesOrderById(mSalesOrderId);
                    }

                    if (mSalesOrder != null) {
                        orderLines.addAll((new SalesOrderLineDB(getContext(), mCurrentUser))
                                .getActiveFinalizedSalesOrderLinesByOrderId(mSalesOrder.getId()));
                        mShareIntent = createShareSalesOrderIntent(mSalesOrder, orderLines);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (mSalesOrder != null) {
                                    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.sales_order_lines);
                                    // use this setting to improve performance if you know that changes
                                    // in content do not change the layout size of the RecyclerView
                                    recyclerView.setHasFixedSize(true);
                                    mLinearLayoutManager = new LinearLayoutManager(getActivity());
                                    recyclerView.setLayoutManager(mLinearLayoutManager);
                                    recyclerView.setAdapter(new SalesOrderLineAdapter(getContext(), orderLines, mCurrentUser));

                                    if (mRecyclerViewCurrentFirstPosition!=0) {
                                        recyclerView.scrollToPosition(mRecyclerViewCurrentFirstPosition);
                                    }

                                    ((TextView) view.findViewById(R.id.sales_order_lines_number_tv))
                                            .setText(getContext().getString(R.string.order_lines_number, String.valueOf(mSalesOrder.getLinesNumber())));

                                    ((TextView) view.findViewById(R.id.sales_order_number_tv))
                                            .setText(getContext().getString(R.string.sales_order_number, mSalesOrder.getSalesOrderNumber()));

                                    ((TextView) view.findViewById(R.id.sales_order_date_tv))
                                            .setText(getContext().getString(R.string.order_date, mSalesOrder.getCreatedStringFormat()));

                                    if(view.findViewById(R.id.sales_order_sub_total_tv) != null) {
                                        ((TextView) view.findViewById(R.id.sales_order_sub_total_tv))
                                                .setText(getContext().getString(R.string.order_sub_total_amount, String.valueOf(mSalesOrder.getSubTotalAmount())));
                                    }
                                    if(view.findViewById(R.id.sales_order_total_tv) != null) {
                                        ((TextView) view.findViewById(R.id.sales_order_total_tv))
                                                .setText(getContext().getString(R.string.order_total_amount, String.valueOf(mSalesOrder.getTruncatedTotalAmount())));
                                    }
                                    if(view.findViewById(R.id.sales_order_tax_tv) != null) {
                                        ((TextView) view.findViewById(R.id.sales_order_tax_tv))
                                                .setText(getContext().getString(R.string.order_tax_amount, String.valueOf(mSalesOrder.getTruncatedTaxAmount())));
                                    }
                                    if(view.findViewById(R.id.create_sales_order_button)!=null){
                                        view.findViewById(R.id.create_sales_order_button)
                                                .setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(getContext(), ShoppingCartActivity.class);
                                                        intent.putExtra(ShoppingCartActivity.KEY_SALES_ORDER_ID, mSalesOrder.getId());
                                                        intent.putExtra(ShoppingCartActivity.KEY_BUSINESS_PARTNER_ID, mSalesOrder.getBusinessPartnerId());
                                                        startActivity(intent);
                                                    }
                                                });
                                    }
                                } else {
                                    //TODO: mostrar mensaje que no hay detalles para mostrar.
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                if (mSalesOrder!=null) {
                                    view.findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
                                    view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                } else {
                                    view.findViewById(R.id.main_layout).setVisibility(View.GONE);
                                    view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                }
            }
        }.start();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_sales_order_detail_fragment, menu);

        // Retrieve the share menu item
        MenuItem item = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        // Attach an intent to this ShareActionProvider. You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        mShareActionProvider.setShareIntent(mShareIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            mShareActionProvider.setShareIntent(mShareIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent createShareSalesOrderIntent(SalesOrder salesOrder, ArrayList<SalesOrderLine> salesOrderLines){
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
            new SalesOrderDetailPDFCreator().generatePDF(salesOrder, salesOrderLines, fileName+".pdf",
                    getContext(), mCurrentUser);
        }catch(Exception e){
            e.printStackTrace();
        }

        //Add the attachment by specifying a reference to our custom ContentProvider
        //and the specific file of interest
        shareIntent.putExtra(Intent.EXTRA_STREAM,  Uri.parse("content://"
                + CachedFileProvider.AUTHORITY + "/" + fileName + ".pdf"));
        return shareIntent;
    }

    //class ReloadShareIntentThread extends Thread {
    //
    //    private SalesOrder mSalesOrder;
    //    private ArrayList<SalesOrderLine> mSalesOrderLines;
    //
    //    ReloadShareIntentThread(SalesOrder salesOrder, ArrayList<SalesOrderLine> salesOrderLines) {
    //        this.mSalesOrder = salesOrder;
    //        this.mSalesOrderLines = salesOrderLines;
    //    }
    //
    //    public void run() {
    //        mShareIntent = createShareSalesOrderIntent(this.mSalesOrder, this.mSalesOrderLines);
    //        try {
    //            getActivity().runOnUiThread(new Runnable() {
    //                @Override
    //                public void run() {
    //                    mShareActionProvider.setShareIntent(mShareIntent);
    //                }
    //            });
    //        } catch (Exception e){
    //            e.printStackTrace();
    //        }
    //    }
    //}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SALES_ORDER_ID, mSalesOrderId);
        try {
            if (mLinearLayoutManager instanceof GridLayoutManager) {
                outState.putInt(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION,
                        mLinearLayoutManager.findFirstVisibleItemPosition());
            } else {
                outState.putInt(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION,
                        mLinearLayoutManager.findFirstCompletelyVisibleItemPosition());
            }
        } catch (Exception e) {
            outState.putInt(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION, mRecyclerViewCurrentFirstPosition);
        }
        super.onSaveInstanceState(outState);
    }
}
