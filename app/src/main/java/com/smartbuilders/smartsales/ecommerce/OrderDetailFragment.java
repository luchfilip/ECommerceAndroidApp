package com.smartbuilders.smartsales.ecommerce;

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

import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.model.UserProfile;
import com.smartbuilders.smartsales.ecommerce.adapters.OrderLineAdapter;
import com.smartbuilders.smartsales.ecommerce.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.data.CurrencyDB;
import com.smartbuilders.smartsales.ecommerce.data.OrderDB;
import com.smartbuilders.smartsales.ecommerce.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerce.model.Currency;
import com.smartbuilders.smartsales.ecommerce.model.Order;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.providers.CachedFileProvider;
import com.smartbuilders.smartsales.ecommerce.utils.OrderDetailPDFCreator;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.io.File;
import java.util.ArrayList;

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
    private int mRecyclerViewCurrentFirstPosition;
    private ShareActionProvider mShareActionProvider;
    private Order mOrder;
    private Intent mShareIntent;

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

        final ArrayList<OrderLine> mOrderLines = new ArrayList<>();

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

                    mOrderLines.addAll(new OrderLineDB(getContext(), mUser)
                            .getActiveFinalizedOrderLinesByOrderId(mOrderId));

                    mOrder = (new OrderDB(getContext(), mUser)).getActiveOrderById(mOrderId);

                    mShareIntent = createShareOrderIntent(mOrder, mOrderLines);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(mUser!=null && mUser.getUserProfileId()==UserProfile.SALES_MAN_PROFILE_ID){
                                    BusinessPartner businessPartner = (new BusinessPartnerDB(getContext(), mUser))
                                            .getActiveBusinessPartnerById(Utils.getAppCurrentBusinessPartnerId(getContext(), mUser));
                                    if(businessPartner!=null){
                                        ((TextView) view.findViewById(R.id.business_partner_name_textView))
                                                .setText(getString(R.string.business_partner_detail, businessPartner.getName()));
                                        view.findViewById(R.id.business_partner_name_textView).setVisibility(View.VISIBLE);
                                    }
                                }

                                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.order_lines);
                                // use this setting to improve performance if you know that changes
                                // in content do not change the layout size of the RecyclerView
                                recyclerView.setHasFixedSize(true);
                                mLinearLayoutManager = new LinearLayoutManager(getActivity());
                                recyclerView.setLayoutManager(mLinearLayoutManager);
                                recyclerView.setAdapter(new OrderLineAdapter(getContext(), mOrderLines, mUser));

                                if (mRecyclerViewCurrentFirstPosition!=0) {
                                    recyclerView.scrollToPosition(mRecyclerViewCurrentFirstPosition);
                                }

                                ((TextView) view.findViewById(R.id.order_lines_number_tv))
                                        .setText(getContext().getString(R.string.order_lines_number, String.valueOf(mOrderLines.size())));

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
                                view.findViewById(R.id.share_button).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(mShareIntent);
                                    }
                                });
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
        int i = item.getItemId();
        if (i == R.id.action_share) {
            mShareActionProvider.setShareIntent(mShareIntent);

        } else if (i == R.id.action_download) {
            if (mShareIntent != null) {
                Utils.createPdfFileInDownloadFolder(getContext(),
                        getContext().getCacheDir() + File.separator + (fileName + ".pdf"),
                        fileName + ".pdf");
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private Intent createShareOrderIntent(Order order, ArrayList<OrderLine> orderLines){
        String subject = "";
        String message = "";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        // need this to prompts email client only
        shareIntent.setType("message/rfc822");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);

        try{
            new OrderDetailPDFCreator().generatePDF(order, orderLines, fileName+".pdf", getContext(), mUser);
        }catch(Exception e){
            e.printStackTrace();
        }

        //Add the attachment by specifying a reference to our custom ContentProvider
        //and the specific file of interest
        shareIntent.putExtra(Intent.EXTRA_STREAM,  Uri.parse("content://"
                + CachedFileProvider.AUTHORITY + File.separator + fileName + ".pdf"));
        return shareIntent;
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
