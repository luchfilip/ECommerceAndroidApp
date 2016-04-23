package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartbuilders.smartsales.ecommerceandroidapp.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.InvoiceLineAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.OrderLineAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.InvoiceLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * A placeholder fragment containing a simple view.
 */
public class OrderDetailFragment extends Fragment {

    public OrderDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order_detail, container, false);

        ArrayList<OrderLine> invoiceLines = new ArrayList<OrderLine>();
        OrderLine orderLine;
        Product p;
        for(int i=0; i<15; i++) {
            orderLine = new OrderLine();
            p = new Product();
            p.setName("Bomba 1/2 hp periferica pedrollo");
            p.setImageId(R.drawable.product1);
            orderLine.setProduct(p);
            invoiceLines.add(orderLine);

            orderLine = new OrderLine();
            p = new Product();
            p.setName("Capacitador con terminal p/bomba 1/2hp");
            p.setImageId(R.drawable.product2);
            orderLine.setProduct(p);
            invoiceLines.add(orderLine);

            orderLine = new OrderLine();
            p = new Product();
            p.setName("Capacitor 25uf semilic");
            p.setImageId(R.drawable.product3);
            orderLine.setProduct(p);
            invoiceLines.add(orderLine);

            orderLine = new OrderLine();
            p = new Product();
            p.setName("Cargador de aire 100gl tm");
            p.setImageId(R.drawable.product4);
            orderLine.setProduct(p);
            invoiceLines.add(orderLine);

            orderLine = new OrderLine();
            p = new Product();
            p.setName("Manometro 0-90psi semilic");
            p.setImageId(R.drawable.product5);
            orderLine.setProduct(p);
            invoiceLines.add(orderLine);

            orderLine = new OrderLine();
            p = new Product();
            p.setName("Mini presostato 20-40 semilic");
            p.setImageId(R.drawable.product6);
            orderLine.setProduct(p);
            invoiceLines.add(orderLine);

            orderLine = new OrderLine();
            p = new Product();
            p.setName("Presostato 20-40 semilic");
            p.setImageId(R.drawable.product7);
            orderLine.setProduct(p);
            invoiceLines.add(orderLine);

            orderLine = new OrderLine();
            p = new Product();
            p.setName("Rolinera para bomba 1/2hp");
            p.setImageId(R.drawable.product8);
            orderLine.setProduct(p);
            invoiceLines.add(orderLine);

            orderLine = new OrderLine();
            p = new Product();
            p.setName("Aspersor pico blanco 3/16\" agroinplast");
            p.setImageId(R.drawable.product9);
            orderLine.setProduct(p);
            invoiceLines.add(orderLine);

            orderLine = new OrderLine();
            p = new Product();
            p.setName("Aspersor oscilante bv");
            p.setImageId(R.drawable.product10);
            orderLine.setProduct(p);
            invoiceLines.add(orderLine);

            orderLine = new OrderLine();
            p = new Product();
            p.setName("Aspersor plastic triple bv");
            p.setImageId(R.drawable.product11);
            orderLine.setProduct(p);
            invoiceLines.add(orderLine);

            orderLine = new OrderLine();
            p = new Product();
            p.setName("Aspersor plastico triple chesterwood");
            p.setImageId(R.drawable.product12);
            orderLine.setProduct(p);
            invoiceLines.add(orderLine);
        }
        //Collections.shuffle(invoiceLines, new Random(System.nanoTime()));

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.order_lines);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new OrderLineAdapter(invoiceLines));

        return rootView;
    }
}
