package com.smartbuilders.smartsales.ecommerce;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartbuilders.smartsales.ecommerce.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class QueriesFragment extends Fragment {

    public QueriesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_queries, container, false);
        view.findViewById(R.id.shopping_carts_by_business_partners_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), ShoppingCartsListQueryActivity.class));
                    }
                });

        view.findViewById(R.id.orders_by_business_partners_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), OrdersListQueryActivity.class));
                    }
                });

        view.findViewById(R.id.shopping_sales_by_business_partners_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), ShoppingSalesListQueryActivity.class));
                    }
                });

        view.findViewById(R.id.sales_orders_by_business_partners_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), SalesOrdersListQueryActivity.class));
                    }
                });
        return view;
    }
}
