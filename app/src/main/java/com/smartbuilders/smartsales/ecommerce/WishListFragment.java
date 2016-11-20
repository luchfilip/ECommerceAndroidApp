package com.smartbuilders.smartsales.ecommerce;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.data.ProductDB;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;
import com.smartbuilders.smartsales.salesforcesystem.DialogAddToShoppingSale2;
import com.smartbuilders.smartsales.salesforcesystem.DialogUpdateShoppingSaleQtyOrdered;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.adapters.WishListAdapter;
import com.smartbuilders.smartsales.ecommerce.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.ecommerce.providers.CachedFileProvider;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.smartsales.ecommerce.utils.WishListPDFCreator;

import java.io.File;
import java.util.ArrayList;

/**
 * Jesus Sarco, 22/3/2016.
 */
public class WishListFragment extends Fragment implements WishListAdapter.Callback {

    private static final String STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION = "STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION";
    private static final String fileName = "ListaDeFavoritos";

    private boolean mIsInitialLoad;
    private User mUser;
    private WishListAdapter mWishListAdapter;
    private OrderLineDB mOrderLineDB;
    private LinearLayoutManager mLinearLayoutManager;
    private int mRecyclerViewCurrentFirstPosition;
    private View mBlankScreenView;
    private View mainLayout;
    private Intent mShareIntent;
    private ProgressDialog waitPlease;

    public WishListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_wish_list, container, false);
        mIsInitialLoad = true;

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState != null) {
                        if(savedInstanceState.containsKey(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION)){
                            mRecyclerViewCurrentFirstPosition = savedInstanceState.getInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION);
                        }
                    }
                    mUser = Utils.getCurrentUser(getContext());
                    mOrderLineDB = new OrderLineDB(getContext(), mUser);

                    mWishListAdapter = new WishListAdapter(getContext(), WishListFragment.this, mOrderLineDB.getWishList(), mUser);
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
                                mWishListAdapter.setParentLayout(mWishListAdapter.getItemCount()>0 ? mainLayout : mBlankScreenView);

                                if (view.findViewById(R.id.search_fab) != null) {
                                    view.findViewById(R.id.search_fab).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            startActivity(new Intent(getActivity(), SearchResultsActivity.class));
                                        }
                                    });
                                }

                                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.wish_list);
                                // use this setting to improve performance if you know that changes
                                // in content do not change the layout size of the RecyclerView
                                recyclerView.setHasFixedSize(true);
                                if (useGridView()) {
                                    mLinearLayoutManager = new GridLayoutManager(getContext(), getSpanCount());
                                }else{
                                    mLinearLayoutManager = new LinearLayoutManager(getContext());
                                }
                                recyclerView.setLayoutManager(mLinearLayoutManager);
                                recyclerView.setAdapter(mWishListAdapter);

                                if (mRecyclerViewCurrentFirstPosition!=0) {
                                    recyclerView.scrollToPosition(mRecyclerViewCurrentFirstPosition);
                                }

                                ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                                        new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT /*| ItemTouchHelper.DOWN | ItemTouchHelper.UP*/) {

                                            @Override
                                            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                                                return false;
                                            }

                                            @Override
                                            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                                                //Remove swiped item from list and notify the RecyclerView
                                                final int itemPosition = viewHolder.getAdapterPosition();
                                                final OrderLine orderLine = mWishListAdapter.getItem(itemPosition);

                                                String result = mOrderLineDB.deleteOrderLine(orderLine.getId());
                                                if(result == null){
                                                    //viewHolder.setIsRecyclable(false);
                                                    mWishListAdapter.removeItem(itemPosition);
                                                    Snackbar.make(mWishListAdapter.getItemCount()>0 ? mainLayout : mBlankScreenView, R.string.product_removed, Snackbar.LENGTH_LONG)
                                                            .setAction(R.string.undo, new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    String result = mOrderLineDB.restoreOrderLine(orderLine.getId());
                                                                    if(result == null){
                                                                        mWishListAdapter.addItem(itemPosition, orderLine);
                                                                        Snackbar.make(mWishListAdapter.getItemCount()>0 ? mainLayout : mBlankScreenView,
                                                                                R.string.product_restored, Snackbar.LENGTH_SHORT).show();
                                                                    } else {
                                                                        Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            }).show();
                                                } else {
                                                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                                                }
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

                                view.findViewById(R.id.share_fab).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        new CreateShareAndDownloadIntentThread(0).start();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                if (mWishListAdapter==null || mWishListAdapter.getItemCount()==0) {
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
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onStart() {
        if(mIsInitialLoad){
            mIsInitialLoad = false;
        }else{
            reloadWishList();
        }
        super.onStart();
    }

    @Override
    public void addToShoppingCart(Product product, User user) {
        DialogAddToShoppingCart dialogAddToShoppingCart =
                DialogAddToShoppingCart.newInstance(product, user);
        dialogAddToShoppingCart.show(getActivity().getSupportFragmentManager(),
                DialogAddToShoppingCart.class.getSimpleName());
    }

    @Override
    public void updateQtyOrderedInShoppingCart(OrderLine orderLine, User user) {
        DialogUpdateShoppingCartQtyOrdered dialogUpdateShoppingCartQtyOrdered =
                DialogUpdateShoppingCartQtyOrdered.newInstance(orderLine, true, user);
        dialogUpdateShoppingCartQtyOrdered.show(getActivity().getSupportFragmentManager(),
                DialogUpdateShoppingCartQtyOrdered.class.getSimpleName());
    }

    @Override
    public void addToShoppingSale(Product product, User user) {
        if (BuildConfig.IS_SALES_FORCE_SYSTEM) {
            DialogAddToShoppingSale2 dialogAddToShoppingSale2 =
                    DialogAddToShoppingSale2.newInstance(product, user);
            dialogAddToShoppingSale2.show(getActivity().getSupportFragmentManager(),
                    DialogAddToShoppingSale2.class.getSimpleName());
        } else {
            DialogAddToShoppingSale dialogAddToShoppingSale =
                    DialogAddToShoppingSale.newInstance(product, user);
            dialogAddToShoppingSale.show(getActivity().getSupportFragmentManager(),
                    DialogAddToShoppingSale.class.getSimpleName());
        }
    }

    @Override
    public void updateQtyOrderedInShoppingSales(SalesOrderLine salesOrderLine, User user) {
        Product product = (new ProductDB(getContext(), mUser)).getProductById(salesOrderLine.getProductId());
        if (product!=null) {
            DialogUpdateShoppingSaleQtyOrdered dialogUpdateShoppingSaleQtyOrdered =
                    DialogUpdateShoppingSaleQtyOrdered.newInstance(product, salesOrderLine, user);
            dialogUpdateShoppingSaleQtyOrdered.show(getActivity().getSupportFragmentManager(),
                    DialogUpdateShoppingSaleQtyOrdered.class.getSimpleName());
        } else {
            //TODO: mostrar mensaje de error
        }
    }

    public void reloadWishList(){
        if (mOrderLineDB!=null) {
            reloadWishList(mOrderLineDB.getWishList(), true);
        }
    }

    @Override
    public void reloadWishList(ArrayList<OrderLine> wishListLines, boolean setData){
        if (setData) {
            mWishListAdapter.setData(wishListLines);
        }
        mWishListAdapter.setParentLayout(mWishListAdapter.getItemCount()>0 ? mainLayout : mBlankScreenView);
        mShareIntent = null;
        if (wishListLines==null || wishListLines.size()==0) {
            mBlankScreenView.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.GONE);
        }else{
            mainLayout.setVisibility(View.VISIBLE);
            mBlankScreenView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_wishlist_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_download) {
            new CreateShareAndDownloadIntentThread(1).start();
        } else if (i == R.id.clear_wish_list) {
            clearWishList();
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearWishList () {
        new AlertDialog.Builder(getContext())
                .setMessage(getContext().getString(R.string.clear_wish_list_question))
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String result = mOrderLineDB.clearWishList();
                            if(result == null){
                                reloadWishList();
                            } else {
                                Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    class CreateShareAndDownloadIntentThread extends Thread {

        private int mMode;
        private String mErrorMessage;

        CreateShareAndDownloadIntentThread(int mode) {
            this.mMode = mode;
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
                                    getString(R.string.creating_wish_list_wait_please), true, false);
                        }
                    }
                });
            }

            try {
                if (mShareIntent==null) {
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
                if (mWishListAdapter!=null && mWishListAdapter.getItemCount()>0) {
                    new WishListPDFCreator().generatePDF(mWishListAdapter.getData(), fileName + ".pdf",
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
                throw  e;
            }
        }
    }

    private boolean useGridView(){
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            return true;
        }else {
            switch(getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) {
                case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                    switch(getResources().getDisplayMetrics().densityDpi) {
                        case DisplayMetrics.DENSITY_LOW:
                            break;
                        case DisplayMetrics.DENSITY_MEDIUM:
                        case DisplayMetrics.DENSITY_HIGH:
                        case DisplayMetrics.DENSITY_XHIGH:
                            return  true;
                    }
                    break;
            }
        }
        return false;
    }

    private int getSpanCount() {
        switch(getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) {
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                switch(getResources().getDisplayMetrics().densityDpi) {
                    case DisplayMetrics.DENSITY_LOW:
                        break;
                    case DisplayMetrics.DENSITY_MEDIUM:
                    case DisplayMetrics.DENSITY_HIGH:
                    case DisplayMetrics.DENSITY_XHIGH:
                        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                            return 3;
                        }
                        break;
                }
                break;
        }
        return 2;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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