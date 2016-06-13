package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.ShoppingSaleAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.SalesOrderDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.SalesOrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.SalesOrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShoppingSaleFragment extends Fragment implements ShoppingSaleAdapter.Callback {

    private static final String STATE_BUSINESS_PARTNER_ID = "STATE_BUSINESS_PARTNER_ID";
    private static final String STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION = "STATE_LISTVIEW_CURRENT_FIRST_POSITION";

    private User mCurrentUser;
    private ArrayList<SalesOrderLine> mOrderLines;
    private ShoppingSaleAdapter mShoppingSaleAdapter;
    private TextView totalLines;
    private TextView subTotalAmount;
    private TextView taxesAmount;
    private TextView totalAmount;
    private int mRecyclerViewCurrentFirstPosition;
    private int mCurrentBusinessPartnerId;

    public ShoppingSaleFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState!=null) {
            if(savedInstanceState.containsKey(STATE_BUSINESS_PARTNER_ID)) {
                mCurrentBusinessPartnerId = savedInstanceState.getInt(STATE_BUSINESS_PARTNER_ID);
            }
            if(savedInstanceState.containsKey(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION)) {
                mRecyclerViewCurrentFirstPosition = savedInstanceState.getInt(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION);
            }
        }

        if(getArguments()!=null){
            if(getArguments().containsKey(ShoppingSaleActivity.KEY_BUSINESS_PARTNER_ID)){
                mCurrentBusinessPartnerId = getArguments().getInt(ShoppingSaleActivity.KEY_BUSINESS_PARTNER_ID);
            }
        }else if(getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null){
            if(getActivity().getIntent().getExtras().containsKey(ShoppingSaleActivity.KEY_BUSINESS_PARTNER_ID)) {
                mCurrentBusinessPartnerId = getActivity().getIntent().getExtras()
                        .getInt(ShoppingSaleActivity.KEY_BUSINESS_PARTNER_ID);
            }
        }

        final View view = inflater.inflate(R.layout.fragment_shopping_sale, container, false);

        mCurrentUser = Utils.getCurrentUser(getContext());

        mOrderLines = (new SalesOrderLineDB(getContext(), mCurrentUser)).getShoppingSale(mCurrentBusinessPartnerId);
        mShoppingSaleAdapter = new ShoppingSaleAdapter(getContext(), this, mOrderLines, mCurrentUser);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.shoppingSale_items_list);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mShoppingSaleAdapter);

        if (mRecyclerViewCurrentFirstPosition!=0) {
            recyclerView.scrollToPosition(mRecyclerViewCurrentFirstPosition);
        }

        view.findViewById(R.id.proceed_to_checkout_shopping_sale_button)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext())
                        .setMessage(R.string.proceed_to_checkout_quoatation_question)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SalesOrderDB salesOrderDB = new SalesOrderDB(getContext(), mCurrentUser);
                                String result = salesOrderDB.createSalesOrderFromShoppingSale(mCurrentBusinessPartnerId);
                                if(result == null){
                                    Intent intent = new Intent(getContext(), SalesOrderDetailActivity.class);
                                    intent.putExtra(SalesOrderDetailActivity.KEY_SALES_ORDER, salesOrderDB.getLastFinalizedSalesOrder());
                                    startActivity(intent);
                                    getActivity().finish();
                                }else{
                                    new AlertDialog.Builder(getContext())
                                            .setMessage(result)
                                            .setNeutralButton(android.R.string.ok, null)
                                            .show();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                }
            });

        totalLines = (TextView) view.findViewById(R.id.total_lines);
        subTotalAmount = (TextView) view.findViewById(R.id.subTotalAmount_tv);
        taxesAmount = (TextView) view.findViewById(R.id.taxesAmount_tv);
        totalAmount = (TextView) view.findViewById(R.id.totalAmount_tv);

        if (mOrderLines==null || mOrderLines.size()==0) {
            view.findViewById(R.id.company_logo_name).setVisibility(View.VISIBLE);
            view.findViewById(R.id.shoppingSale_items_list).setVisibility(View.GONE);
            view.findViewById(R.id.shoppingSale_data_linearLayout).setVisibility(View.GONE);
        } else {
            fillFields();
        }
        return view;
    }

    public void fillFields(){
        double subTotal=0, tax=0, total=0;
        for(SalesOrderLine orderLine : mOrderLines){
            subTotal += orderLine.getQuantityOrdered() * orderLine.getPrice();
            tax += orderLine.getQuantityOrdered() * orderLine.getPrice() * (orderLine.getTaxPercentage()/100);
            total += subTotal + tax;
        }
        totalLines.setText(getString(R.string.order_lines_number, String.valueOf(mOrderLines.size())));
        subTotalAmount.setText(getString(R.string.order_sub_total_amount, String.valueOf(subTotal)));
        taxesAmount.setText(getString(R.string.order_tax_amount, String.valueOf(tax)));
        totalAmount.setText(getString(R.string.order_total_amount, String.valueOf(total)));
    }

    @Override
    public void updateSalesOrderLine(SalesOrderLine orderLine, int focus) {
        DialogUpdateSalesOrderLine dialogUpdateSalesOrderLine =
                DialogUpdateSalesOrderLine.newInstance(orderLine, mCurrentUser, focus);
        dialogUpdateSalesOrderLine.setTargetFragment(this, 0);
        dialogUpdateSalesOrderLine.show(getActivity().getSupportFragmentManager(),
                DialogUpdateSalesOrderLine.class.getSimpleName());
    }

    public void reloadShoppingSale(){
        mOrderLines = (new SalesOrderLineDB(getActivity(), mCurrentUser)).getShoppingSale(mCurrentBusinessPartnerId);
        mShoppingSaleAdapter.setData(mOrderLines);
        fillFields();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_BUSINESS_PARTNER_ID, mCurrentBusinessPartnerId);
        outState.putInt(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION, mRecyclerViewCurrentFirstPosition);
        super.onSaveInstanceState(outState);
    }
}
