package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.ShoppingSaleAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShoppingSaleFragment extends Fragment implements ShoppingSaleAdapter.Callback {

    private static final String STATE_CURRENT_USER = "state_current_user";

    private User mCurrentUser;
    private ArrayList<OrderLine> mOrderLines;
    private ShoppingSaleAdapter mShoppingSaleAdapter;
    private TextView totalLines;
    private TextView subTotalAmount;
    private TextView taxesAmount;
    private TextView totalAmount;

    public ShoppingSaleFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_shopping_sale, container, false);

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        if(getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null) {
            if(getActivity().getIntent().getExtras().containsKey(ShoppingSaleActivity.KEY_CURRENT_USER)){
                mCurrentUser = getActivity().getIntent().getExtras().getParcelable(ShoppingSaleActivity.KEY_CURRENT_USER);
            }
        }

        mOrderLines = (new OrderLineDB(getContext(), mCurrentUser)).getShoppingSale();
        mShoppingSaleAdapter = new ShoppingSaleAdapter(getContext(), this, mOrderLines, mCurrentUser);

        ((ListView) view.findViewById(R.id.shoppingSale_items_list)).setAdapter(mShoppingSaleAdapter);

        view.findViewById(R.id.proceed_to_checkout_shopping_sale_button)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext())
                        .setMessage(R.string.proceed_to_checkout_quoatation_question)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                OrderDB orderDB = new OrderDB(getContext(), mCurrentUser);
                                String result = orderDB.createOrderFromShoppingSale();
                                if(result == null){
                                    Intent intent = new Intent(getContext(), SalesOrderDetailActivity.class);
                                    intent.putExtra(SalesOrderDetailActivity.KEY_CURRENT_USER, mCurrentUser);
                                    intent.putExtra(SalesOrderDetailActivity.KEY_SALES_ORDER, orderDB.getLastFinalizedSalesOrder());
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

        if ((mOrderLines==null || mOrderLines.size()==0)
                && view.findViewById(R.id.company_logo_name)!=null
                && view.findViewById(R.id.shoppingSale_items_list)!=null
                && view.findViewById(R.id.shoppingSale_data_linearLayout)!=null) {
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
        for(OrderLine orderLine : mOrderLines){
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
    public void updateSalesOrderLine(OrderLine orderLine, int focus) {
        DialogUpdateSalesOrderLine dialogUpdateSalesOrderLine =
                DialogUpdateSalesOrderLine.newInstance(orderLine, mCurrentUser, focus);
        dialogUpdateSalesOrderLine.setTargetFragment(this, 0);
        dialogUpdateSalesOrderLine.show(getActivity().getSupportFragmentManager(),
                DialogUpdateSalesOrderLine.class.getSimpleName());
    }

    public void reloadShoppingSale(){
        mOrderLines = (new OrderLineDB(getActivity(), mCurrentUser)).getShoppingSale();
        mShoppingSaleAdapter.setData(mOrderLines);
        fillFields();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
        super.onSaveInstanceState(outState);
    }
}
