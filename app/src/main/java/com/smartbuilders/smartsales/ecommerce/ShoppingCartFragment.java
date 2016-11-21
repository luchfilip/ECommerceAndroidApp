package com.smartbuilders.smartsales.ecommerce;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbuilders.smartsales.salesforcesystem.ShoppingCartFinalizeOptionsActivity;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.model.UserProfile;
import com.smartbuilders.smartsales.ecommerce.adapters.ShoppingCartAdapter;
import com.smartbuilders.smartsales.ecommerce.businessRules.OrderBR;
import com.smartbuilders.smartsales.ecommerce.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.data.CurrencyDB;
import com.smartbuilders.smartsales.ecommerce.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerce.data.SalesOrderDB;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerce.model.Currency;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrder;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShoppingCartFragment extends Fragment implements ShoppingCartAdapter.Callback,
        DialogUpdateShoppingCartQtyOrdered.Callback {

    private static final String STATE_SALES_ORDER_ID = "state_sales_order_id";
    private static final String STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION =
            "STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION";
    private static final String STATE_ORDER_LINES = "STATE_ORDER_LINES";

    private boolean mIsInitialLoad;
    private User mUser;
    private int mSalesOrderId;
    private ShoppingCartAdapter mShoppingCartAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private int mRecyclerViewCurrentFirstPosition;
    private TextView mTotalLines;
    private TextView mSubTotalAmount;
    private TextView mTaxAmount;
    private TextView mTotalAmount;
    private ProgressDialog waitPlease;
    private View mMainLayout;
    private View mBlankScreenView;
    private boolean mIsShoppingCart = true;
    private TextView mBusinessPartnerName;
    private TextView mSalesOrderNumber;
    private View mSalesOrderInfoSeparator;
    private OrderLineDB mOrderLineDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);
        mIsInitialLoad = true;
        final ArrayList<OrderLine> mOrderLines = new ArrayList<>();

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState != null) {
                        if(savedInstanceState.containsKey(STATE_SALES_ORDER_ID)){
                            mIsShoppingCart = false;
                            mSalesOrderId = savedInstanceState.getInt(STATE_SALES_ORDER_ID);
                        }
                        if(savedInstanceState.containsKey(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION)){
                            mRecyclerViewCurrentFirstPosition = savedInstanceState.getInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION);
                        }
                        if(savedInstanceState.containsKey(STATE_ORDER_LINES)){
                            ArrayList<OrderLine> orderLines = savedInstanceState.getParcelableArrayList(STATE_ORDER_LINES);
                            mOrderLines.addAll(orderLines);
                        }
                    } else if(getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null) {
                        if(getActivity().getIntent().getExtras().containsKey(ShoppingCartActivity.KEY_SALES_ORDER_ID)
                                && getActivity().getIntent().getExtras().containsKey(ShoppingCartActivity.KEY_BUSINESS_PARTNER_ID)){
                            mIsShoppingCart = false;
                            mSalesOrderId = getActivity().getIntent().getExtras()
                                    .getInt(ShoppingCartActivity.KEY_SALES_ORDER_ID);
                            //Si viene de ShoppingCartFinalizeOptionsActivity
                            if(getActivity().getIntent().getExtras().containsKey(ShoppingCartActivity.KEY_ORDER_LINES)){
                                ArrayList<OrderLine> orderLines = getActivity().getIntent().getExtras()
                                        .getParcelableArrayList(ShoppingCartActivity.KEY_ORDER_LINES);
                                mOrderLines.addAll(orderLines);
                            }
                        }
                    } else if (!mIsShoppingCart) {
                        mOrderLines.addAll(mOrderLineDB.getOrderLinesBySalesOrderId(mSalesOrderId));
                    }

                    mUser = Utils.getCurrentUser(getContext());
                    mOrderLineDB = new OrderLineDB(getContext(), mUser);

                    if (mIsShoppingCart) {
                        mOrderLines.addAll(mOrderLineDB.getActiveOrderLinesFromShoppingCart());
                    }
                    mShoppingCartAdapter = new ShoppingCartAdapter(getContext(),
                            ShoppingCartFragment.this, mOrderLines, mIsShoppingCart, mUser);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mBlankScreenView = view.findViewById(R.id.company_logo_name);
                                mMainLayout = view.findViewById(R.id.main_layout);
                                mShoppingCartAdapter.setParentLayout(mShoppingCartAdapter.getItemCount()>0 ? mMainLayout : mBlankScreenView);

                                mBusinessPartnerName = (TextView) view.findViewById(R.id.business_partner_commercial_name_textView);
                                mSalesOrderNumber = (TextView) view.findViewById(R.id.sales_order_number_textView);
                                mSalesOrderInfoSeparator = view.findViewById(R.id.sales_order_info_separator);

                                if (view.findViewById(R.id.empty_shopping_cart_imageView) != null) {
                                    ((ImageView) view.findViewById(R.id.empty_shopping_cart_imageView))
                                            .setColorFilter(Utils.getColor(getContext(), R.color.colorAccent));
                                }
                                if (view.findViewById(R.id.search_fab) != null) {
                                    view.findViewById(R.id.search_fab).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            startActivity(new Intent(getActivity(), SearchResultsActivity.class));
                                        }
                                    });
                                }

                                setHeader();

                                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.shoppingCart_items_list);
                                // use this setting to improve performance if you know that changes
                                // in content do not change the layout size of the RecyclerView
                                recyclerView.setHasFixedSize(true);
                                mLinearLayoutManager = new LinearLayoutManager(getContext());
                                recyclerView.setLayoutManager(mLinearLayoutManager);
                                recyclerView.setAdapter(mShoppingCartAdapter);

                                if (mRecyclerViewCurrentFirstPosition!=0) {
                                    recyclerView.scrollToPosition(mRecyclerViewCurrentFirstPosition);
                                }

                                ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                                        new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {

                                            @Override
                                            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                                                return false;
                                            }

                                            @Override
                                            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                                                //Remove swiped item from list and notify the RecyclerView
                                                final int itemPosition = viewHolder.getAdapterPosition();
                                                final OrderLine orderLine = mShoppingCartAdapter.getItem(itemPosition);

                                                new AlertDialog.Builder(getContext())
                                                        .setMessage(getString(R.string.delete_from_shopping_cart_question,
                                                                orderLine.getProduct().getName()))
                                                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                String result = null;
                                                                if(mIsShoppingCart){
                                                                    result = mOrderLineDB.deleteOrderLine(orderLine.getId());
                                                                }
                                                                if(result == null){
                                                                    mShoppingCartAdapter.removeItem(itemPosition);
                                                                    Snackbar.make(mShoppingCartAdapter.getItemCount()>0 ? mMainLayout : mBlankScreenView, R.string.product_removed, Snackbar.LENGTH_LONG)
                                                                            .setAction(R.string.undo, new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View view) {
                                                                                    String result = mOrderLineDB.restoreOrderLine(orderLine.getId());
                                                                                    if(result == null){
                                                                                        mShoppingCartAdapter.addItem(itemPosition, orderLine);
                                                                                        Snackbar.make(mShoppingCartAdapter.getItemCount()>0 ? mMainLayout : mBlankScreenView, R.string.product_restored, Snackbar.LENGTH_SHORT).show();
                                                                                    } else {
                                                                                        Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            }).show();
                                                                } else {
                                                                    mShoppingCartAdapter.notifyDataSetChanged();
                                                                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        })
                                                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                mShoppingCartAdapter.notifyDataSetChanged();
                                                            }
                                                        })
                                                        .show();
                                            }

                                            @Override
                                            public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                                                    RecyclerView.ViewHolder viewHolder, float dX,
                                                                    float dY, int actionState, boolean isCurrentlyActive) {
                                                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                                                    // Get RecyclerView item from the ViewHolder
                                                    View itemView = viewHolder.itemView;

                                                    Paint p = new Paint();
                                                    p.setColor(Utils.getColor(getContext(), R.color.on_swipe_bg_color));

                                                    Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_highlight_off_white_48dp);
                                                    if (dX > 0) {
                                                        // Draw Rect with varying right side, equal to displacement dX
                                                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                                                                (float) itemView.getBottom(), p);

                                                        // Set the image icon for Right swipe
                                                        c.drawBitmap(icon,
                                                                (float) itemView.getLeft() + Utils.convertDpToPixel(16, getContext()),
                                                                (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight())/2,
                                                                p);
                                                    } else {
                                                        // Draw Rect with varying left side, equal to the item's right side plus negative displacement dX
                                                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                                                (float) itemView.getRight(), (float) itemView.getBottom(), p);

                                                        //Set the image icon for Left swipe
                                                        c.drawBitmap(icon,
                                                                (float) itemView.getRight() - Utils.convertDpToPixel(16, getContext()) - icon.getWidth(),
                                                                (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight())/2,
                                                                p);
                                                    }
                                                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                                                }
                                            }

                                        };
                                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
                                itemTouchHelper.attachToRecyclerView(recyclerView);


                                if (BuildConfig.IS_SALES_FORCE_SYSTEM) {
                                    view.findViewById(R.id.proceed_to_checkout_button).setVisibility(View.GONE);
                                    view.findViewById(R.id.go_to_finalize_options_button)
                                            .setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    lockScreen();
                                                    new Thread() {
                                                        @Override
                                                        public void run() {
                                                            String result = null;
                                                            try {
                                                                result = OrderBR.isValidQuantityOrderedInOrderLines(getContext(), mUser, mOrderLines);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                                result = e.getMessage();
                                                            } finally {
                                                                unlockScreen(result);
                                                            }
                                                        }
                                                    }.start();
                                                }
                                            });
                                } else {
                                    view.findViewById(R.id.go_to_finalize_options_button).setVisibility(View.GONE);
                                    view.findViewById(R.id.proceed_to_checkout_button)
                                            .setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    new AlertDialog.Builder(getContext())
                                                            .setMessage(R.string.proceed_to_checkout_question)
                                                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    closeOrder();
                                                                }
                                                            })
                                                            .setNegativeButton(R.string.no, null)
                                                            .show();
                                                }
                                            });
                                }
                                mTotalLines = (TextView) view.findViewById(R.id.total_lines);
                                mSubTotalAmount = (TextView) view.findViewById(R.id.subTotalAmount_tv);
                                mTaxAmount = (TextView) view.findViewById(R.id.taxesAmount_tv);
                                mTotalAmount = (TextView) view.findViewById(R.id.totalAmount_tv);

                                fillFields();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                if (mShoppingCartAdapter==null || mShoppingCartAdapter.getItemCount()==0) {
                                    mBlankScreenView.setVisibility(View.VISIBLE);
                                } else {
                                    mMainLayout.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
                }
            }
        }.start();
        return view;
    }

    @Override
    public void onStart() {
        if(mIsInitialLoad){
            mIsInitialLoad = false;
        }else{
            if(mIsShoppingCart){
                try {
                    reloadShoppingCart((new OrderLineDB(getActivity(), mUser)).getActiveOrderLinesFromShoppingCart(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.error)
                            .setMessage(e.getMessage())
                            .setNeutralButton(R.string.accept, null)
                            .show();
                }
            }
        }
        super.onStart();
    }

    private void closeOrder(){
        lockScreen();
        new Thread() {
            @Override
            public void run() {
                String result = null;
                try {
                    result = OrderBR.isValidQuantityOrderedInOrderLines(getContext(), mUser, mShoppingCartAdapter.getData());
                    if (result==null) {
                        if (mSalesOrderId > 0) {
                            result = OrderBR.createOrderFromOrderLines(getContext(), mUser, mSalesOrderId, 0, mShoppingCartAdapter.getData());
                        } else {
                            result = OrderBR.createOrderFromShoppingCart(getContext(), mUser, 0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    result = e.getMessage();
                } finally {
                    unlockScreen(result);
                }
            }
        }.start();
    }

    private void lockScreen() {
        if (getActivity()!=null) {
            //Se bloquea la rotacion de la pantalla para evitar que se mate a la aplicacion
            Utils.lockScreenOrientation(getActivity());
            if (waitPlease==null || !waitPlease.isShowing()){
                waitPlease = ProgressDialog.show(getContext(), null,
                        getString(R.string.closing_order_wait_please), true, false);
            }
        }
    }

    private void unlockScreen(final String message){
        if(getActivity()!=null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(message!=null){
                        new AlertDialog.Builder(getContext())
                                .setMessage(message)
                                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Utils.unlockScreenOrientation(getActivity());
                                    }
                                })
                                .setCancelable(false)
                                .show();
                        if (waitPlease!=null && waitPlease.isShowing()) {
                            waitPlease.cancel();
                            waitPlease = null;
                        }
                    } else {
                        if (BuildConfig.IS_SALES_FORCE_SYSTEM) {
                            if (mIsShoppingCart) {
                                startActivity(new Intent(getContext(), ShoppingCartFinalizeOptionsActivity.class));
                            } else {
                                try {
                                    //TODO: arreglar aqui, me parece que no es necesario colocar el businessPartnerId
                                    Intent intent = new Intent(getContext(), ShoppingCartFinalizeOptionsActivity.class);
                                    intent.putExtra(ShoppingCartFinalizeOptionsActivity.KEY_BUSINESS_PARTNER_ID, Utils.getAppCurrentBusinessPartnerId(getContext(), mUser));
                                    intent.putExtra(ShoppingCartFinalizeOptionsActivity.KEY_SALES_ORDER_ID, mSalesOrderId);
                                    intent.putExtra(ShoppingCartFinalizeOptionsActivity.KEY_ORDER_LINES, mShoppingCartAdapter.getData());
                                    startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    new AlertDialog.Builder(getContext())
                                            .setMessage(e.getMessage())
                                            .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Utils.unlockScreenOrientation(getActivity());
                                                }
                                            })
                                            .setCancelable(false)
                                            .show();
                                }
                            }
                        } else {
                            if (mIsShoppingCart) {
                                startActivity(new Intent(getContext(), OrdersListActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                            } //else {
                                //startActivity(new Intent(getContext(), SalesOrdersListActivity.class)
                                //        .putExtra(SalesOrdersListActivity.KEY_CURRENT_TAB_SELECTED, 1)
                                //        .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                            //}
                        }
                        if (waitPlease != null && waitPlease.isShowing()) {
                            waitPlease.cancel();
                            waitPlease = null;
                        }
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void updateQtyOrdered(OrderLine orderLine) {
        DialogUpdateShoppingCartQtyOrdered dialogUpdateShoppingCartQtyOrdered =
                DialogUpdateShoppingCartQtyOrdered.newInstance(orderLine, mIsShoppingCart, mUser);
        dialogUpdateShoppingCartQtyOrdered.setTargetFragment(this, 0);
        dialogUpdateShoppingCartQtyOrdered.show(getActivity().getSupportFragmentManager(),
                DialogUpdateShoppingCartQtyOrdered.class.getSimpleName());
    }

    private void setHeader(){
        if(mUser!=null) {
            if (BuildConfig.IS_SALES_FORCE_SYSTEM || mUser.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID) {
                if (mIsShoppingCart) {
                    try {
                        BusinessPartner businessPartner = (new BusinessPartnerDB(getContext(), mUser))
                                .getBusinessPartnerById(Utils.getAppCurrentBusinessPartnerId(getContext(), mUser));
                        if (businessPartner != null) {
                            mBusinessPartnerName.setText(getString(R.string.business_partner_name_detail, businessPartner.getName()));
                            mBusinessPartnerName.setVisibility(View.VISIBLE);

                            mSalesOrderInfoSeparator.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    SalesOrder salesOrder = (new SalesOrderDB(getContext(), mUser)).getSalesOrderById(mSalesOrderId);
                    if (salesOrder != null && salesOrder.getBusinessPartner() != null) {
                        mBusinessPartnerName.setText(getString(R.string.business_partner_name_detail, salesOrder.getBusinessPartner().getName()));
                        mBusinessPartnerName.setVisibility(View.VISIBLE);

                        mSalesOrderNumber.setText(getString(R.string.sales_order_number, salesOrder.getSalesOrderNumber()));
                        mSalesOrderNumber.setVisibility(View.VISIBLE);

                        mSalesOrderInfoSeparator.setVisibility(View.VISIBLE);
                    }
                }
            } else if (mUser.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID) {
                //solamente muestra el nombre del cliente y el numero de cotizacion cuando se esta
                //pasando de cotizacion a pedido
                if (!mIsShoppingCart) {
                    SalesOrder salesOrder = (new SalesOrderDB(getContext(), mUser)).getSalesOrderById(mSalesOrderId);
                    if (salesOrder != null && salesOrder.getBusinessPartner()!=null) {
                        mBusinessPartnerName.setText(getString(R.string.business_partner_name_detail, salesOrder.getBusinessPartner().getName()));
                        mBusinessPartnerName.setVisibility(View.VISIBLE);

                        mSalesOrderNumber.setText(getString(R.string.sales_order_number, salesOrder.getSalesOrderNumber()));
                        mSalesOrderNumber.setVisibility(View.VISIBLE);

                        mSalesOrderInfoSeparator.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    public void fillFields(){
        if (mShoppingCartAdapter!=null && mShoppingCartAdapter.getItemCount()>0) {
            mTotalLines.setText(getString(R.string.order_lines_number,
                    String.valueOf(mShoppingCartAdapter.getItemCount())));
            if (Parameter.isManagePriceInOrder(getContext(), mUser)) {
                Currency currency = (new CurrencyDB(getContext(), mUser))
                        .getActiveCurrencyById(Parameter.getDefaultCurrencyId(getContext(), mUser));
                mSubTotalAmount.setText(getString(R.string.order_sub_total_amount,
                        currency!=null ? currency.getName() : "",
                        OrderBR.getSubTotalAmountStringFormat(mShoppingCartAdapter.getData())));
                mSubTotalAmount.setVisibility(View.VISIBLE);

                mTaxAmount.setText(getString(R.string.order_tax_amount,
                        currency!=null ? currency.getName() : "",
                        OrderBR.getTaxAmountStringFormat(mShoppingCartAdapter.getData())));
                mTaxAmount.setVisibility(View.VISIBLE);

                mTotalAmount.setText(getString(R.string.order_total_amount,
                        currency!=null ? currency.getName() : "",
                        OrderBR.getTotalAmountStringFormat(mShoppingCartAdapter.getData())));
                mTotalAmount.setVisibility(View.VISIBLE);
            } else {
                mSubTotalAmount.setVisibility(View.GONE);
                mTaxAmount.setVisibility(View.GONE);
                mTotalAmount.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void reloadShoppingCart(){
        mShoppingCartAdapter.notifyDataSetChanged();
        reloadShoppingCart(null, false);
    }

    @Override
    public void reloadShoppingCart(ArrayList<OrderLine> orderLines, boolean setData){
        if (setData) {
            mShoppingCartAdapter.setData(orderLines);
        }
        mShoppingCartAdapter.setParentLayout(mShoppingCartAdapter.getItemCount()>0 ? mMainLayout : mBlankScreenView);
        setHeader();
        if (mShoppingCartAdapter==null || mShoppingCartAdapter.getItemCount()==0) {
            mBlankScreenView.setVisibility(View.VISIBLE);
            mMainLayout.setVisibility(View.GONE);
        }else{
            mBlankScreenView.setVisibility(View.GONE);
            mMainLayout.setVisibility(View.VISIBLE);
            fillFields();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!mIsShoppingCart) {
            outState.putInt(STATE_SALES_ORDER_ID, mSalesOrderId);
            outState.putParcelableArrayList(STATE_ORDER_LINES, mShoppingCartAdapter.getData());
        }
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
