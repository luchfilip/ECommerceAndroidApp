package com.smartbuilders.smartsales.ecommerce;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.smartbuilders.smartsales.ecommerce.data.ProductDB;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.salesforcesystem.DialogUpdateShoppingSaleQtyOrdered;
import com.smartbuilders.smartsales.salesforcesystem.adapters.ShoppingSaleAdapter2;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.adapters.ShoppingSaleAdapter;
import com.smartbuilders.smartsales.ecommerce.adapters.ShoppingSalesListAdapter;
import com.smartbuilders.smartsales.ecommerce.data.SalesOrderDB;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrder;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShoppingSalesListFragment extends Fragment implements ShoppingSaleAdapter.Callback,
        ShoppingSaleAdapter2.Callback {

    private static final String STATE_CURRENT_SELECTED_INDEX = "STATE_CURRENT_SELECTED_INDEX";
    private static final String STATE_LIST_VIEW_INDEX = "STATE_LIST_VIEW_INDEX";
    private static final String STATE_LIST_VIEW_TOP = "STATE_LIST_VIEW_TOP";

    private User mUser;
    private boolean mIsInitialLoad;
    private ListView mListView;
    private int mListViewIndex;
    private int mListViewTop;
    private int mCurrentSelectedIndex;
    private SalesOrderDB mSalesOrderDB;
    private ShoppingSalesListAdapter mShoppingSalesListAdapter;

    public interface Callback {
        void onItemSelected(SalesOrder salesOrder);
        void onItemLongSelected(SalesOrder salesOrder, User user);
        void onListIsLoaded();
        void setSelectedIndex(int selectedIndex);
    }

    public ShoppingSalesListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_shopping_sales_list, container, false);
        mIsInitialLoad = true;

        mUser = Utils.getCurrentUser(getContext());
        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState != null) {
                        if(savedInstanceState.containsKey(STATE_CURRENT_SELECTED_INDEX)){
                            mCurrentSelectedIndex = savedInstanceState.getInt(STATE_CURRENT_SELECTED_INDEX);
                        }
                        if(savedInstanceState.containsKey(STATE_LIST_VIEW_INDEX)){
                            mListViewIndex = savedInstanceState.getInt(STATE_LIST_VIEW_INDEX);
                        }
                        if(savedInstanceState.containsKey(STATE_LIST_VIEW_TOP)){
                            mListViewTop = savedInstanceState.getInt(STATE_LIST_VIEW_TOP);
                        }
                    }
                    mSalesOrderDB = new SalesOrderDB(getContext(), mUser);
                    mShoppingSalesListAdapter = new ShoppingSalesListAdapter(getContext(), mSalesOrderDB.getShoppingSalesList());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (view.findViewById(R.id.empty_shopping_sale_list_imageView) != null) {
                                    ((ImageView) view.findViewById(R.id.empty_shopping_sale_list_imageView))
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

                                mListView = (ListView) view.findViewById(R.id.shopping_sales_orders_list);

                                mListView.setAdapter(mShoppingSalesListAdapter);

                                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        mCurrentSelectedIndex = position;
                                        SalesOrder salesOrder = (SalesOrder) parent.getItemAtPosition(position);
                                        if (salesOrder != null) {
                                            ((Callback) getActivity()).onItemSelected(salesOrder);
                                        }
                                    }
                                });

                                mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                    @Override
                                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                        SalesOrder salesOrder = (SalesOrder) parent.getItemAtPosition(position);
                                        if (salesOrder != null) {
                                            ((Callback) getActivity()).onItemLongSelected(salesOrder, mUser);
                                        }
                                        return true;
                                    }
                                });

                                mListView.setSelectionFromTop(mListViewIndex, mListViewTop);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                view.findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                if (getActivity()!=null) {
                                    if (savedInstanceState==null || mShoppingSalesListAdapter.isEmpty()) {
                                        ((Callback) getActivity()).onListIsLoaded();
                                    } else {
                                        ((Callback) getActivity()).setSelectedIndex(mCurrentSelectedIndex);
                                    }
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
            if(mListView!=null && mShoppingSalesListAdapter!=null && mSalesOrderDB!=null){
                int oldListSize = mShoppingSalesListAdapter.getCount();
                mShoppingSalesListAdapter.setData(mSalesOrderDB.getShoppingSalesList());
                if(mShoppingSalesListAdapter.getCount()!=oldListSize){
                    ((Callback) getActivity()).onListIsLoaded();
                }else{
                    ((Callback) getActivity()).setSelectedIndex(mCurrentSelectedIndex);
                }
            }
        }
        super.onStart();
    }

    @Override
    public void updateSalesOrderLine(SalesOrderLine orderLine, int focus) {
        //do nothing
    }

    @Override
    public void reloadShoppingSalesList(User user) {
        mShoppingSalesListAdapter.setData(mSalesOrderDB.getShoppingSalesList());
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
    public void reloadShoppingSale() {
        //do nothing
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        try {
            outState.putInt(STATE_LIST_VIEW_INDEX, mListView.getFirstVisiblePosition());
        } catch (Exception e) {
            outState.putInt(STATE_LIST_VIEW_INDEX, mListViewIndex);
        }
        try {
            outState.putInt(STATE_LIST_VIEW_TOP, (mListView.getChildAt(0) == null) ? 0 :
                    (mListView.getChildAt(0).getTop() - mListView.getPaddingTop()));
        } catch (Exception e) {
            outState.putInt(STATE_LIST_VIEW_TOP, mListViewTop);
        }
        outState.putInt(STATE_CURRENT_SELECTED_INDEX, mCurrentSelectedIndex);
        super.onSaveInstanceState(outState);
    }
}
