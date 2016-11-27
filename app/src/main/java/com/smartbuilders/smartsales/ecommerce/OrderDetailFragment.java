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

import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.adapters.OrderLineAdapter;
import com.smartbuilders.smartsales.ecommerce.data.CurrencyDB;
import com.smartbuilders.smartsales.ecommerce.data.OrderDB;
import com.smartbuilders.smartsales.ecommerce.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerce.model.Currency;
import com.smartbuilders.smartsales.ecommerce.model.Order;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.providers.CachedFileProvider;
import com.smartbuilders.smartsales.ecommerce.utils.OrderDetailPDFCreator;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.io.File;

/**
 * A placeholder fragment containing a simple view.
 */
public class OrderDetailFragment extends Fragment {

    private static final String STATE_ORDER_ID = "STATE_ORDER_ID";
    private static final String STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION =
            "STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION";
    private static final String fileName = "Pedido";

    private User mUser;
    private int mOrderId;
    private LinearLayoutManager mLinearLayoutManager;
    private OrderLineAdapter mOrderLineAdapter;
    private int mRecyclerViewCurrentFirstPosition;
    private Order mOrder;
    private Intent mShareIntent;
    private ProgressDialog waitPlease;

    public OrderDetailFragment() {
    }

    public interface Callback {
        void orderDetailLoaded();
        boolean isFragmentMenuVisible();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_order_detail, container, false);
        setMenuVisibility(((Callback) getActivity()).isFragmentMenuVisible());

        new Thread() {
            @Override
            public void run() {
                try {
                    if (savedInstanceState!=null) {
                        if (savedInstanceState.containsKey(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION)) {
                            mRecyclerViewCurrentFirstPosition = savedInstanceState.getInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION);
                        }
                        if (savedInstanceState.containsKey(STATE_ORDER_ID)) {
                            mOrderId = savedInstanceState.getInt(STATE_ORDER_ID);
                        }
                    }

                    if (getArguments() != null) {
                        if (getArguments().containsKey(OrderDetailActivity.KEY_ORDER_ID)) {
                            mOrderId = getArguments().getInt(OrderDetailActivity.KEY_ORDER_ID);
                        }
                    } else if (getActivity()!=null && getActivity().getIntent() != null &&
                            getActivity().getIntent().getExtras() != null) {
                        if (getActivity().getIntent().getExtras().containsKey(OrderDetailActivity.KEY_ORDER_ID)) {
                            mOrderId = getActivity().getIntent().getExtras().getInt(OrderDetailActivity.KEY_ORDER_ID);
                        }
                    }

                    mUser = Utils.getCurrentUser(getContext());
                    mOrderLineAdapter = new OrderLineAdapter(getContext(), new OrderLineDB(getContext(), mUser).getActiveFinalizedOrderLinesByOrderId(mOrderId), mUser);
                    mOrder = (new OrderDB(getContext(), mUser)).getActiveOrderById(mOrderId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.order_lines);
                                // use this setting to improve performance if you know that changes
                                // in content do not change the layout size of the RecyclerView
                                recyclerView.setHasFixedSize(true);
                                mLinearLayoutManager = new LinearLayoutManager(getActivity());
                                recyclerView.setLayoutManager(mLinearLayoutManager);
                                recyclerView.setAdapter(mOrderLineAdapter);

                                if (mRecyclerViewCurrentFirstPosition!=0) {
                                    recyclerView.scrollToPosition(mRecyclerViewCurrentFirstPosition);
                                }

                                ((TextView) view.findViewById(R.id.order_lines_number_tv))
                                        .setText(getContext().getString(R.string.order_lines_number, String.valueOf(mOrder.getLinesNumber())));

                                if (mOrder!=null) {
                                    ((TextView) view.findViewById(R.id.order_number_tv))
                                            .setText(getContext().getString(R.string.order_number, mOrder.getOrderNumber()));

                                    ((TextView) view.findViewById(R.id.order_date_tv))
                                            .setText(getContext().getString(R.string.order_date, mOrder.getCreatedStringFormat()));

                                    if (Parameter.isManagePriceInOrder(getContext(), mUser)) {
                                        Currency currency = (new CurrencyDB(getContext(), mUser))
                                                .getActiveCurrencyById(Parameter.getDefaultCurrencyId(getContext(), mUser));

                                        ((TextView) view.findViewById(R.id.order_sub_total_tv))
                                                .setText(getContext().getString(R.string.order_sub_total_amount,
                                                        currency!=null ? currency.getName() : "",
                                                        mOrder.getSubTotalAmountStringFormat()));
                                        view.findViewById(R.id.order_sub_total_tv).setVisibility(View.VISIBLE);

                                        ((TextView) view.findViewById(R.id.order_tax_tv))
                                                .setText(getContext().getString(R.string.order_tax_amount,
                                                        currency!=null ? currency.getName() : "",
                                                        mOrder.getTaxAmountStringFormat()));
                                        view.findViewById(R.id.order_tax_tv).setVisibility(View.VISIBLE);

                                        ((TextView) view.findViewById(R.id.order_total_tv))
                                                .setText(getContext().getString(R.string.order_total_amount,
                                                        currency!=null ? currency.getName() : "",
                                                        mOrder.getTotalAmountStringFormat()));
                                        view.findViewById(R.id.order_total_tv).setVisibility(View.VISIBLE);
                                    } else {
                                        view.findViewById(R.id.order_sub_total_tv).setVisibility(View.GONE);
                                        view.findViewById(R.id.order_tax_tv).setVisibility(View.GONE);
                                        view.findViewById(R.id.order_total_tv).setVisibility(View.GONE);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                view.findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                if(getActivity()!=null){
                                    ((Callback) getActivity()).orderDetailLoaded();
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
        inflater.inflate(R.menu.menu_order_detail_fragment, menu);
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
                                    getString(R.string.creating_order_wait_please), true, false);
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
                if (mUser!=null && mOrder != null && mOrderLineAdapter!=null && mOrderLineAdapter.getItemCount()>0) {
                    new OrderDetailPDFCreator().generatePDF(mOrder, mOrderLineAdapter.getData(), fileName + ".pdf",
                            getContext(), mUser);

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
        outState.putInt(STATE_ORDER_ID, mOrderId);
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
