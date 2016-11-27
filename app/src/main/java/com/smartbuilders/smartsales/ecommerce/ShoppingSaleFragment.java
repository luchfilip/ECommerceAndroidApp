package com.smartbuilders.smartsales.ecommerce;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.data.ProductDB;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.salesforcesystem.DialogUpdateShoppingSaleQtyOrdered;
import com.smartbuilders.smartsales.salesforcesystem.ShoppingSaleFinalizeOptionsActivity;
import com.smartbuilders.smartsales.salesforcesystem.adapters.ShoppingSaleAdapter2;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.adapters.ShoppingSaleAdapter;
import com.smartbuilders.smartsales.ecommerce.businessRules.SalesOrderBR;
import com.smartbuilders.smartsales.ecommerce.data.CurrencyDB;
import com.smartbuilders.smartsales.ecommerce.data.SalesOrderLineDB;
import com.smartbuilders.smartsales.ecommerce.model.Currency;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.smartsales.ecommerce.view.DatePickerFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShoppingSaleFragment extends Fragment implements ShoppingSaleAdapter.Callback,
        ShoppingSaleAdapter2.Callback, DatePickerFragment.Callback, DialogUpdateShoppingSaleQtyOrdered.Callback {

    private static final String STATE_USER_BUSINESS_PARTNER_ID = "STATE_USER_BUSINESS_PARTNER_ID";
    private static final String STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION = "STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION";
    private static final String STATE_VALID_TO = "STATE_VALID_TO";

    private User mUser;
    private boolean mIsInitialLoad;
    private ShoppingSaleAdapter mShoppingSaleAdapter;
    private ShoppingSaleAdapter2 mShoppingSaleAdapter2;
    private View mBlankScreenView;
    private View mainLayout;
    private TextView mTotalLines;
    private TextView mSubTotalAmount;
    private TextView mTaxAmount;
    private TextView mTotalAmount;
    private int mRecyclerViewCurrentFirstPosition;
    private LinearLayoutManager mLinearLayoutManager;
    private int mUserBusinessPartnerId;
    private ProgressDialog waitPlease;
    private EditText mValidToEditText;
    private String mValidToText;
    private SalesOrderLineDB mSalesOrderLineDB;

    public ShoppingSaleFragment() {
    }

    public interface Callback {
        void reloadShoppingSalesList(User user);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_shopping_sale, container, false);
        mIsInitialLoad = true;

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState!=null) {
                        if(savedInstanceState.containsKey(STATE_USER_BUSINESS_PARTNER_ID)) {
                            mUserBusinessPartnerId = savedInstanceState.getInt(STATE_USER_BUSINESS_PARTNER_ID);
                        }
                        if(savedInstanceState.containsKey(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION)) {
                            mRecyclerViewCurrentFirstPosition = savedInstanceState.getInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION);
                        }
                        if(savedInstanceState.containsKey(STATE_VALID_TO)) {
                            mValidToText = savedInstanceState.getString(STATE_VALID_TO);
                        }
                    }

                    if(getArguments()!=null){
                        if(getArguments().containsKey(ShoppingSaleActivity.KEY_USER_BUSINESS_PARTNER_ID)){
                            mUserBusinessPartnerId = getArguments().getInt(ShoppingSaleActivity.KEY_USER_BUSINESS_PARTNER_ID);
                        }
                    }else if(getActivity()!=null && getActivity().getIntent()!=null
                            && getActivity().getIntent().getExtras()!=null){
                        if(getActivity().getIntent().getExtras().containsKey(ShoppingSaleActivity.KEY_USER_BUSINESS_PARTNER_ID)) {
                            mUserBusinessPartnerId = getActivity().getIntent().getExtras()
                                    .getInt(ShoppingSaleActivity.KEY_USER_BUSINESS_PARTNER_ID);
                        }
                    }

                    mUser = Utils.getCurrentUser(getContext());
                    mSalesOrderLineDB = new SalesOrderLineDB(getContext(), mUser);

                    if (BuildConfig.IS_SALES_FORCE_SYSTEM) {
                        mShoppingSaleAdapter2 = new ShoppingSaleAdapter2(getContext(), ShoppingSaleFragment.this,
                                (mUserBusinessPartnerId !=0 ? mSalesOrderLineDB.getShoppingSaleByBusinessPartnerId(mUserBusinessPartnerId)
                                        : mSalesOrderLineDB.getShoppingSale()), mUser);
                    } else {
                        mShoppingSaleAdapter = new ShoppingSaleAdapter(getContext(), ShoppingSaleFragment.this,
                                (mUserBusinessPartnerId !=0 ? mSalesOrderLineDB.getShoppingSaleByBusinessPartnerId(mUserBusinessPartnerId)
                                        : mSalesOrderLineDB.getShoppingSale()), mUser);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mBlankScreenView = view.findViewById(R.id.company_logo_name);
                                mainLayout = view.findViewById(R.id.main_layout);

                                mTotalLines = (TextView) view.findViewById(R.id.total_lines);
                                mSubTotalAmount = (TextView) view.findViewById(R.id.subTotalAmount_tv);
                                mTaxAmount = (TextView) view.findViewById(R.id.taxesAmount_tv);
                                mTotalAmount = (TextView) view.findViewById(R.id.totalAmount_tv);

                                if (BuildConfig.IS_SALES_FORCE_SYSTEM) {
                                    mShoppingSaleAdapter2.setParentLayout(mShoppingSaleAdapter2.getItemCount()>0 ? mainLayout : mBlankScreenView);
                                } else {
                                    mShoppingSaleAdapter.setParentLayout(mShoppingSaleAdapter.getItemCount()>0 ? mainLayout : mBlankScreenView);
                                }

                                if (view.findViewById(R.id.empty_shopping_sale_imageView) != null) {
                                    ((ImageView) view.findViewById(R.id.empty_shopping_sale_imageView))
                                            .setColorFilter(Utils.getColor(getContext(), R.color.golden_medium));
                                }
                                if (view.findViewById(R.id.search_fab) != null) {
                                    view.findViewById(R.id.search_fab).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            startActivity(new Intent(getActivity(), SearchResultsActivity.class));
                                        }
                                    });
                                }

                                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.shoppingSale_items_list);
                                // use this setting to improve performance if you know that changes
                                // in content do not change the layout size of the RecyclerView
                                recyclerView.setHasFixedSize(true);
                                mLinearLayoutManager = new LinearLayoutManager(getContext());
                                recyclerView.setLayoutManager(mLinearLayoutManager);
                                recyclerView.setAdapter(BuildConfig.IS_SALES_FORCE_SYSTEM ? mShoppingSaleAdapter2 : mShoppingSaleAdapter);

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
                                                final SalesOrderLine salesOrderLine = (BuildConfig.IS_SALES_FORCE_SYSTEM
                                                        ? mShoppingSaleAdapter2.getItem(itemPosition)
                                                        : mShoppingSaleAdapter.getItem(itemPosition));

                                                new AlertDialog.Builder(getContext())
                                                        .setMessage(getString(R.string.delete_from_shopping_sale_question,
                                                                salesOrderLine.getProduct().getName()))
                                                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                String result = mSalesOrderLineDB.deactivateSalesOrderLine(salesOrderLine.getId());
                                                                if(result == null){
                                                                    if (BuildConfig.IS_SALES_FORCE_SYSTEM) {
                                                                        mShoppingSaleAdapter2.removeItem(itemPosition);
                                                                    } else {
                                                                        mShoppingSaleAdapter.removeItem(itemPosition);
                                                                    }
                                                                    Snackbar.make((BuildConfig.IS_SALES_FORCE_SYSTEM
                                                                            ? mShoppingSaleAdapter2.getItemCount()>0
                                                                            : mShoppingSaleAdapter.getItemCount()>0)
                                                                            ? mainLayout : mBlankScreenView, R.string.product_removed, Snackbar.LENGTH_LONG)
                                                                            .setAction(R.string.undo, new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View view) {
                                                                                    String result = mSalesOrderLineDB.restoreSalesOrderLine(salesOrderLine.getId());
                                                                                    if(result == null){
                                                                                        if (BuildConfig.IS_SALES_FORCE_SYSTEM) {
                                                                                            mShoppingSaleAdapter2.addItem(itemPosition, salesOrderLine);
                                                                                        } else {
                                                                                            mShoppingSaleAdapter.addItem(itemPosition, salesOrderLine);
                                                                                        }
                                                                                    } else {
                                                                                        Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            }).show();
                                                                } else {
                                                                    if (BuildConfig.IS_SALES_FORCE_SYSTEM) {
                                                                        mShoppingSaleAdapter2.notifyDataSetChanged();
                                                                    } else {
                                                                        mShoppingSaleAdapter.notifyDataSetChanged();
                                                                    }
                                                                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        })
                                                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                if (BuildConfig.IS_SALES_FORCE_SYSTEM) {
                                                                    mShoppingSaleAdapter2.notifyDataSetChanged();
                                                                } else {
                                                                    mShoppingSaleAdapter.notifyDataSetChanged();
                                                                }
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
                                                    p.setColor(Utils.getColor(getContext(), R.color.golden_medium));

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
                                    view.findViewById(R.id.valid_to_layout_container).setVisibility(View.GONE);
                                    view.findViewById(R.id.proceed_to_checkout_shopping_sale_button).setVisibility(View.GONE);
                                    view.findViewById(R.id.go_to_finalize_options_button)
                                            .setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    lockScreen();
                                                    new Thread() {
                                                        @Override
                                                        public void run() {
                                                            String result = null;
                                                            try {
                                                                result = SalesOrderBR.isValidQuantityOrderedInSalesOrderLines(getContext(), mUser,
                                                                        BuildConfig.IS_SALES_FORCE_SYSTEM ? mShoppingSaleAdapter.getData() : mShoppingSaleAdapter.getData());
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
                                    mValidToEditText = (EditText) view.findViewById(R.id.valid_to_editText);
                                    mValidToEditText.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Date validTo = null;
                                            try {
                                                validTo = DateFormat.getDateInstance(DateFormat.MEDIUM,
                                                        new Locale("es","VE")).parse(mValidToEditText.getText().toString());
                                            } catch (ParseException e){
                                                // do nothing
                                            }
                                            catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            DialogFragment dialogFragment = DatePickerFragment.getInstance(validTo);
                                            dialogFragment.setTargetFragment(ShoppingSaleFragment.this, 1);
                                            dialogFragment.show(getFragmentManager(), DatePickerFragment.class.getSimpleName());
                                        }
                                    });
                                    mValidToEditText.setText(mValidToText);

                                    view.findViewById(R.id.go_to_finalize_options_button).setVisibility(View.GONE);
                                    view.findViewById(R.id.proceed_to_checkout_shopping_sale_button)
                                            .setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    new AlertDialog.Builder(getContext())
                                                            .setMessage(R.string.proceed_to_checkout_quoatation_question)
                                                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Date validTo = null;
                                                                    try {
                                                                        validTo = DateFormat.getDateInstance(DateFormat.MEDIUM,
                                                                                new Locale("es", "VE")).parse(mValidToEditText.getText().toString());
                                                                    } catch (ParseException e) {
                                                                        // do nothing
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    closeSalesOrder(validTo);
                                                                }
                                                            })
                                                            .setNegativeButton(R.string.no, null)
                                                            .show();
                                                }
                                            });
                                }

                                fillFields();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                if (BuildConfig.IS_SALES_FORCE_SYSTEM
                                        ? (mShoppingSaleAdapter2==null || mShoppingSaleAdapter2.getItemCount()==0)
                                        : (mShoppingSaleAdapter==null || mShoppingSaleAdapter.getItemCount()==0)) {
                                    mBlankScreenView.setVisibility(View.VISIBLE);
                                } else {
                                    mainLayout.setVisibility(View.VISIBLE);
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
            reloadShoppingSale(mUserBusinessPartnerId != 0
                    ? (new SalesOrderLineDB(getContext(), mUser)).getShoppingSaleByBusinessPartnerId(mUserBusinessPartnerId)
                    : (new SalesOrderLineDB(getContext(), mUser)).getShoppingSale(), true);
            reloadShoppingSalesList(mUser);
        }
        super.onStart();
    }

    @Override
    public void setDate(String date) {
        mValidToText = date;
        mValidToEditText.setText(date);
    }

    private void closeSalesOrder(final Date validTo){
        lockScreen();
        new Thread() {
            @Override
            public void run() {
                String result = null;
                try {
                    if (mUserBusinessPartnerId != 0) {
                        result = SalesOrderBR.createSalesOrderFromShoppingSale(getContext(), mUser,
                                mUserBusinessPartnerId, validTo, 0);
                    } else {
                        result = SalesOrderBR.createSalesOrderFromShoppingSale(getContext(), mUser, validTo, 0);
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
                        getString(R.string.closing_sales_order_wait_please), true, false);
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
                            startActivity(new Intent(getContext(), ShoppingSaleFinalizeOptionsActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                        } else {
                            startActivity(new Intent(getContext(), SalesOrdersListActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                        }
                        if (waitPlease!=null && waitPlease.isShowing()) {
                            waitPlease.cancel();
                            waitPlease = null;
                        }
                        if (getActivity()!=null) {
                            getActivity().finish();
                        }
                    }
                }
            });
        }
    }

    public void fillFields(){
        if (BuildConfig.IS_SALES_FORCE_SYSTEM
                ? (mShoppingSaleAdapter2!=null && mShoppingSaleAdapter2.getItemCount()>0)
                : (mShoppingSaleAdapter!=null && mShoppingSaleAdapter.getItemCount()>0)) {
            mTotalLines.setText(getString(R.string.order_lines_number,
                    String.valueOf(BuildConfig.IS_SALES_FORCE_SYSTEM ? mShoppingSaleAdapter2.getItemCount() : mShoppingSaleAdapter.getItemCount())));
            Currency currency = (new CurrencyDB(getContext(), mUser))
                    .getActiveCurrencyById(Parameter.getDefaultCurrencyId(getContext(), mUser));
            mSubTotalAmount.setText(getString(R.string.sales_order_sub_total_amount,
                    currency!=null ? currency.getName() : "",
                    SalesOrderBR.getSubTotalAmountStringFormat(BuildConfig.IS_SALES_FORCE_SYSTEM ? mShoppingSaleAdapter2.getData() : mShoppingSaleAdapter.getData())));
            mTaxAmount.setText(getString(R.string.sales_order_tax_amount,
                    currency!=null ? currency.getName() : "",
                    SalesOrderBR.getTaxAmountStringFormat(BuildConfig.IS_SALES_FORCE_SYSTEM ? mShoppingSaleAdapter2.getData() : mShoppingSaleAdapter.getData())));
            mTotalAmount.setText(getString(R.string.sales_order_total_amount,
                    currency!=null ? currency.getName() : "",
                    SalesOrderBR.getTotalAmountStringFormat(BuildConfig.IS_SALES_FORCE_SYSTEM ? mShoppingSaleAdapter2.getData() : mShoppingSaleAdapter.getData())));
        }
    }

    @Override
    public void updateSalesOrderLine(SalesOrderLine salesOrderLine, int focus) {
        DialogUpdateSalesOrderLine dialogUpdateSalesOrderLine =
                DialogUpdateSalesOrderLine.newInstance(salesOrderLine, mUser, focus);
        dialogUpdateSalesOrderLine.setTargetFragment(this, 0);
        dialogUpdateSalesOrderLine.show(getActivity().getSupportFragmentManager(),
                DialogUpdateSalesOrderLine.class.getSimpleName());
    }

    @Override
    public void reloadShoppingSalesList(User user) {
        //manda a refrescar la lista de la izquierda cuando se esta en pantalla dividida
        ((Callback) getActivity()).reloadShoppingSalesList(user);
    }

    @Override
    public void updateQtyOrdered(SalesOrderLine salesOrderLine) {
        Product product = (new ProductDB(getContext(), mUser)).getProductById(salesOrderLine.getProductId());
        if (product!=null) {
            DialogUpdateShoppingSaleQtyOrdered dialogUpdateShoppingSaleQtyOrdered =
                    DialogUpdateShoppingSaleQtyOrdered.newInstance(product, salesOrderLine, mUser);
            dialogUpdateShoppingSaleQtyOrdered.setTargetFragment(this, 0);
            dialogUpdateShoppingSaleQtyOrdered.show(getActivity().getSupportFragmentManager(),
                    DialogUpdateShoppingSaleQtyOrdered.class.getSimpleName());
        } else {
            //TODO: mostrar mensaje de error
        }
    }

    @Override
    public void reloadShoppingSale(){
        if (BuildConfig.IS_SALES_FORCE_SYSTEM) {
            mShoppingSaleAdapter2.notifyDataSetChanged();
        } else {
            mShoppingSaleAdapter.notifyDataSetChanged();
        }
        reloadShoppingSale(null, false);
    }

    @Override
    public void reloadShoppingSale(ArrayList<SalesOrderLine> salesOrderLines, boolean setData){
        if (setData) {
            if (BuildConfig.IS_SALES_FORCE_SYSTEM) {
                mShoppingSaleAdapter2.setData(salesOrderLines);
            } else {
                mShoppingSaleAdapter.setData(salesOrderLines);
            }
        }

        if (BuildConfig.IS_SALES_FORCE_SYSTEM) {
            mShoppingSaleAdapter2.setParentLayout(mShoppingSaleAdapter2.getItemCount()>0 ? mainLayout : mBlankScreenView);
        } else {
            mShoppingSaleAdapter.setParentLayout(mShoppingSaleAdapter.getItemCount()>0 ? mainLayout : mBlankScreenView);
        }

        if (BuildConfig.IS_SALES_FORCE_SYSTEM
                ? (mShoppingSaleAdapter2==null || mShoppingSaleAdapter2.getItemCount()==0)
                : (mShoppingSaleAdapter==null || mShoppingSaleAdapter.getItemCount()==0)) {
            mBlankScreenView.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.GONE);
        }else{
            mainLayout.setVisibility(View.VISIBLE);
            mBlankScreenView.setVisibility(View.GONE);
            fillFields();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_USER_BUSINESS_PARTNER_ID, mUserBusinessPartnerId);
        outState.putString(STATE_VALID_TO, mValidToText);
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
