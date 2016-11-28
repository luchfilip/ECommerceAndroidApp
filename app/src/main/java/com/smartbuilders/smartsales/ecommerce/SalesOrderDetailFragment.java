package com.smartbuilders.smartsales.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.adapters.SalesOrderLineAdapter;
import com.smartbuilders.smartsales.ecommerce.data.CurrencyDB;
import com.smartbuilders.smartsales.ecommerce.data.SalesOrderDB;
import com.smartbuilders.smartsales.ecommerce.data.SalesOrderLineDB;
import com.smartbuilders.smartsales.ecommerce.model.Currency;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrder;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;
import com.smartbuilders.smartsales.ecommerce.providers.CachedFileProvider;
import com.smartbuilders.smartsales.ecommerce.utils.SalesOrderDetailPDFCreator;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class SalesOrderDetailFragment extends Fragment {

    private static final String STATE_SALES_ORDER_ID = "STATE_SALES_ORDER_ID";
    private static final String STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION = "STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION";
    private static final String fileName = "Cotizacion";

    private User mUser;
    private int mSalesOrderId;
    private SalesOrder mSalesOrder;
    private ArrayList<SalesOrderLine> mSalesOrderLines;
    private LinearLayoutManager mLinearLayoutManager;
    private int mRecyclerViewCurrentFirstPosition;
    private Intent mShareIntent;
    private ProgressDialog waitPlease;
    private boolean mIsInitialLoad;

    public SalesOrderDetailFragment() {
    }

    public interface Callback{
        void salesOrderDetailLoaded();
        boolean isFragmentMenuVisible();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sales_order_detail, container, false);
        setMenuVisibility(((Callback) getActivity()).isFragmentMenuVisible());

        mIsInitialLoad = true;

        new Thread() {
            @Override
            public void run() {
                try {
                    if (savedInstanceState!=null) {
                        if (savedInstanceState.containsKey(STATE_SALES_ORDER_ID)) {
                            mSalesOrderId = savedInstanceState.getInt(STATE_SALES_ORDER_ID);
                        }
                        if (savedInstanceState.containsKey(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION)) {
                            mRecyclerViewCurrentFirstPosition = savedInstanceState.getInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION);
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

                    mUser = Utils.getCurrentUser(getContext());

                    if (mSalesOrderId>0) {
                        mSalesOrder = (new SalesOrderDB(getContext(), mUser)).getSalesOrderById(mSalesOrderId);
                    }

                    if (mSalesOrder != null) {
                        mSalesOrderLines = (new SalesOrderLineDB(getContext(), mUser))
                                .getSalesOrderLinesList(mSalesOrder.getId(), mSalesOrder.getBusinessPartnerId());
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
                                    if(mSalesOrder.getBusinessPartner()!=null){
                                        if (getActivity().findViewById(R.id.business_partner_name_container)!=null
                                                && getActivity().findViewById(R.id.business_partner_name)!=null) {
                                            ((TextView) getActivity().findViewById(R.id.business_partner_name))
                                                    .setText(mSalesOrder.getBusinessPartner().getName());
                                            getActivity().findViewById(R.id.business_partner_name_container)
                                                    .setVisibility(View.VISIBLE);
                                        }
                                    }

                                    Currency currency = (new CurrencyDB(getContext(), mUser))
                                            .getActiveCurrencyById(Parameter.getDefaultCurrencyId(getContext(), mUser));

                                    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.sales_order_lines);
                                    // use this setting to improve performance if you know that changes
                                    // in content do not change the layout size of the RecyclerView
                                    recyclerView.setHasFixedSize(true);
                                    mLinearLayoutManager = new LinearLayoutManager(getContext());
                                    recyclerView.setLayoutManager(mLinearLayoutManager);
                                    recyclerView.setAdapter(new SalesOrderLineAdapter(getContext(), mSalesOrderLines, mUser));

                                    if (mRecyclerViewCurrentFirstPosition!=0) {
                                        recyclerView.scrollToPosition(mRecyclerViewCurrentFirstPosition);
                                    }

                                    ((TextView) view.findViewById(R.id.sales_order_lines_number_tv))
                                            .setText(getContext().getString(R.string.order_lines_number, String.valueOf(mSalesOrder.getLinesNumber())));

                                    ((TextView) view.findViewById(R.id.sales_order_number_tv))
                                            .setText(getContext().getString(R.string.sales_order_number, mSalesOrder.getSalesOrderNumber()));

                                    ((TextView) view.findViewById(R.id.sales_order_date_tv))
                                            .setText(getContext().getString(R.string.sales_order_date, mSalesOrder.getCreatedStringFormat()));

                                    ((TextView) view.findViewById(R.id.sales_order_sub_total_tv))
                                            .setText(getContext().getString(R.string.sales_order_sub_total_amount,
                                                    currency!=null ? currency.getName() : "",
                                                    mSalesOrder.getSubTotalAmountStringFormat()));

                                    ((TextView) view.findViewById(R.id.sales_order_tax_tv))
                                            .setText(getContext().getString(R.string.sales_order_tax_amount,
                                                    currency!=null ? currency.getName() : "",
                                                    mSalesOrder.getTaxAmountStringFormat()));

                                    ((TextView) view.findViewById(R.id.sales_order_total_tv))
                                            .setText(getContext().getString(R.string.sales_order_total_amount,
                                                    currency!=null ? currency.getName() : "",
                                                    mSalesOrder.getTotalAmountStringFormat()));

                                    if(mSalesOrder.getValidTo()!=null){
                                        ((TextView) view.findViewById(R.id.sales_order_valid_to_tv))
                                                .setText(getContext().getString(R.string.sales_order_valid_to, mSalesOrder.getValidToStringFormat()));
                                    }else{
                                        ((TextView) view.findViewById(R.id.sales_order_valid_to_tv))
                                                .setText(getContext().getString(R.string.sales_order_valid_to, getString(R.string.undefined)));
                                    }

                                    view.findViewById(R.id.create_order_button)
                                            .setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(getContext(), ShoppingCartActivity.class);
                                                    intent.putExtra(ShoppingCartActivity.KEY_SALES_ORDER_ID, mSalesOrder.getId());
                                                    intent.putExtra(ShoppingCartActivity.KEY_BUSINESS_PARTNER_ID, mSalesOrder.getBusinessPartnerId());
                                                    startActivity(intent);
                                                }
                                            });

                                    //view.findViewById(R.id.share_button)
                                    //        .setOnClickListener(new View.OnClickListener() {
                                    //            @Override
                                    //            public void onClick(View v) {
                                    //                new CreateShareAndDownloadIntentThread(0).start();
                                    //            }
                                    //        });
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
                                if(getActivity()!=null){
                                    ((Callback) getActivity()).salesOrderDetailLoaded();
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
    public void onStart() {
        if (mIsInitialLoad) {
            mIsInitialLoad = false;
        } else {
            mShareIntent = null;
        }
        super.onStart();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_sales_order_detail_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_download) {
            new CreateShareAndDownloadIntentThread(1).start();
        } else if (i == R.id.action_share) {
            new CreateShareAndDownloadIntentThread(0).start();
        }
        return super.onOptionsItemSelected(item);
    }

    class CreateShareAndDownloadIntentThread extends Thread {

        private int mMode;
        private String mErrorMessage;

        CreateShareAndDownloadIntentThread(int mode) {
            mMode = mode;
        }

        public void run() {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Se bloquea la rotacion de la pantalla para evitar que se mate a la aplicacion
                        Utils.lockScreenOrientation(getActivity());
                        if (waitPlease==null || !waitPlease.isShowing()){
                            waitPlease = ProgressDialog.show(getContext(), null,
                                    getString(R.string.creating_sales_order_wait_please), true, false);
                        }
                    }
                });
            }

            try {
                if (mShareIntent == null) {
                    createShareAndDownloadIntent();
                }
            } catch (Exception e) {
                e.printStackTrace();
                mErrorMessage = e.getMessage();
            }

            if(getActivity()!=null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (TextUtils.isEmpty(mErrorMessage)) {
                            if (mShareIntent != null) {
                                switch (mMode) {
                                    case 0:
                                        startActivity(mShareIntent);
                                        break;
                                    case 1:
                                        Utils.createPdfFileInDownloadFolder(getContext(),
                                                getContext().getCacheDir() + File.separator + (fileName + ".pdf"),
                                                fileName + ".pdf");
                                        break;
                                }
                            }
                        } else {
                            Toast.makeText(getContext(), mErrorMessage, Toast.LENGTH_SHORT).show();
                        }
                        if (waitPlease!=null && waitPlease.isShowing()) {
                            waitPlease.dismiss();
                            waitPlease = null;
                        }
                        Utils.unlockScreenOrientation(getActivity());
                    }
                });
            }
        }

        private void createShareAndDownloadIntent() throws Exception {
            try {
                if (mUser!=null && mSalesOrder!=null && mSalesOrderLines!=null && !mSalesOrderLines.isEmpty()) {
                    new SalesOrderDetailPDFCreator().generatePDF(mSalesOrder, mSalesOrderLines, fileName+".pdf",
                            getActivity(), getContext(), mUser);

                    mShareIntent = new Intent(Intent.ACTION_SEND);
                    mShareIntent.setType("application/pdf");
                    mShareIntent.putExtra(Intent.EXTRA_STREAM,
                            Uri.parse("content://"+CachedFileProvider.AUTHORITY+File.separator+fileName+".pdf"));
                } else {
                    mShareIntent = null;
                }
            } catch (Exception e) {
                mShareIntent = null;
                throw e;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SALES_ORDER_ID, mSalesOrderId);
        try {
            if (mLinearLayoutManager instanceof GridLayoutManager) {
                outState.putInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION,
                        mLinearLayoutManager.findFirstVisibleItemPosition());
            } else {
                outState.putInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION,
                        mLinearLayoutManager.findFirstCompletelyVisibleItemPosition());
            }
        } catch (Exception e) {
            outState.putInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION, mRecyclerViewCurrentFirstPosition);
        }
        super.onSaveInstanceState(outState);
    }
}
