package com.smartbuilders.smartsales.salesforcesystem;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartbuilders.smartsales.ecommerce.OrdersListActivity;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.SalesOrdersListActivity;
import com.smartbuilders.smartsales.ecommerce.ShoppingCartActivity;
import com.smartbuilders.smartsales.ecommerce.ShoppingSaleActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class SalesForceSystemMainActivityFragment extends Fragment {

    private boolean mIsInitialLoad;

    public interface Callback {
        void reload();
    }

    public SalesForceSystemMainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales_force_system_main, container, false);

        mIsInitialLoad = true;

        view.findViewById(R.id.shopping_cart_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ShoppingCartActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
        });

        view.findViewById(R.id.orders_list_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), OrdersListActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
        });

        view.findViewById(R.id.shopping_sale_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ShoppingSaleActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
        });

        view.findViewById(R.id.sales_orders_list_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SalesOrdersListActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        if(mIsInitialLoad){
            mIsInitialLoad = false;
        }else{
            // reload
            ((Callback) getActivity()).reload();
        }
        super.onStart();
    }

}
