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
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.ShoppingCartAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShoppingCartFragment extends Fragment implements ShoppingCartAdapter.Callback {

    private static final String STATE_CURRENT_USER = "state_current_user";
    private User mCurrentUser;
    private ShoppingCartAdapter mShoppingCartAdapter;
    private ArrayList<OrderLine> mOrderLines;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);
        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        if(getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null) {
            if(getActivity().getIntent().getExtras().containsKey(ShoppingCartActivity.KEY_CURRENT_USER)){
                mCurrentUser = getActivity().getIntent().getExtras()
                        .getParcelable(ShoppingCartActivity.KEY_CURRENT_USER);
            }
        }

        mOrderLines = (new OrderLineDB(getContext(), mCurrentUser)).getShoppingCart();
        mShoppingCartAdapter = new ShoppingCartAdapter(getContext(), this, mOrderLines, mCurrentUser);

        ((ListView) view.findViewById(R.id.shoppingCart_items_list)).setAdapter(mShoppingCartAdapter);

        view.findViewById(R.id.proceed_to_checkout_button)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext())
                        .setMessage(R.string.proceed_to_checkout_question)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                OrderDB orderDB = new OrderDB(getContext(), mCurrentUser);
                                String result = orderDB.createOrderFromShoppingCart();
                                if(result == null){
                                    Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                                    intent.putExtra(OrderDetailActivity.KEY_CURRENT_USER, mCurrentUser);
                                    intent.putExtra(OrderDetailActivity.KEY_ORDER, orderDB.getLastFinalizedOrder());
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

        if ((mOrderLines==null || mOrderLines.size()==0)
                && view.findViewById(R.id.company_logo_name)!=null
                && view.findViewById(R.id.shoppingCart_items_list)!=null
                && view.findViewById(R.id.shoppingCart_data_linearLayout)!=null) {
            view.findViewById(R.id.company_logo_name).setVisibility(View.VISIBLE);
            view.findViewById(R.id.shoppingCart_items_list).setVisibility(View.GONE);
            view.findViewById(R.id.shoppingCart_data_linearLayout).setVisibility(View.GONE);
        } else {
            ((TextView) view.findViewById(R.id.total_lines))
                    .setText(getString(R.string.order_lines_number, String.valueOf(mOrderLines.size())));
        }
        return view;
    }

    @Override
    public void updateQtyOrdered(OrderLine orderLine) {
        DialogUpdateQuantityOrdered dialogUpdateQuantityOrdered =
                DialogUpdateQuantityOrdered.newInstance(orderLine, mCurrentUser);
        dialogUpdateQuantityOrdered.setTargetFragment(this, 0);
        dialogUpdateQuantityOrdered.show(getActivity().getSupportFragmentManager(),
                DialogUpdateQuantityOrdered.class.getSimpleName());
    }

    public void reloadShoppingCart(){
        mOrderLines = (new OrderLineDB(getActivity(), mCurrentUser)).getShoppingCart();
        mShoppingCartAdapter.setData(mOrderLines);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
        super.onSaveInstanceState(outState);
    }
}
