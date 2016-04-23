package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.InvoiceLineAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.ProductRecyclerViewAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.InvoiceLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * A placeholder fragment containing a simple view.
 */
public class InvoiceDetailFragment extends Fragment {

    public InvoiceDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_invoice_detail, container, false);

        ArrayList<InvoiceLine> invoiceLines = new ArrayList<InvoiceLine>();
        InvoiceLine invLine = new InvoiceLine();
        Product p = new Product();
        p.setName("Bomba 1/2 hp periferica pedrollo");
        p.setImageId(R.drawable.product1);
        invLine.setProduct(p);
        invoiceLines.add(invLine);

        invLine = new InvoiceLine();
        p = new Product();
        p.setName("Capacitador con terminal p/bomba 1/2hp");
        p.setImageId(R.drawable.product2);
        invLine.setProduct(p);
        invoiceLines.add(invLine);

        invLine = new InvoiceLine();
        p = new Product();
        p.setName("Capacitor 25uf semilic");
        p.setImageId(R.drawable.product3);
        invLine.setProduct(p);
        invoiceLines.add(invLine);

        invLine = new InvoiceLine();
        p = new Product();
        p.setName("Cargador de aire 100gl tm");
        p.setImageId(R.drawable.product4);
        invLine.setProduct(p);
        invoiceLines.add(invLine);

        invLine = new InvoiceLine();
        p = new Product();
        p.setName("Manometro 0-90psi semilic");
        p.setImageId(R.drawable.product5);
        invLine.setProduct(p);
        invoiceLines.add(invLine);

        invLine = new InvoiceLine();
        p = new Product();
        p.setName("Mini presostato 20-40 semilic");
        p.setImageId(R.drawable.product6);
        invLine.setProduct(p);
        invoiceLines.add(invLine);

        invLine = new InvoiceLine();
        p = new Product();
        p.setName("Presostato 20-40 semilic");
        p.setImageId(R.drawable.product7);
        invLine.setProduct(p);
        invoiceLines.add(invLine);

        invLine = new InvoiceLine();
        p = new Product();
        p.setName("Rolinera para bomba 1/2hp");
        p.setImageId(R.drawable.product8);
        invLine.setProduct(p);
        invoiceLines.add(invLine);

        invLine = new InvoiceLine();
        p = new Product();
        p.setName("Aspersor pico blanco 3/16\" agroinplast");
        p.setImageId(R.drawable.product9);
        invLine.setProduct(p);
        invoiceLines.add(invLine);

        invLine = new InvoiceLine();
        p = new Product();
        p.setName("Aspersor oscilante bv");
        p.setImageId(R.drawable.product10);
        invLine.setProduct(p);
        invoiceLines.add(invLine);

        invLine = new InvoiceLine();
        p = new Product();
        p.setName("Aspersor plastic triple bv");
        p.setImageId(R.drawable.product11);
        invLine.setProduct(p);
        invoiceLines.add(invLine);

        invLine = new InvoiceLine();
        p = new Product();
        p.setName("Aspersor plastico triple chesterwood");
        p.setImageId(R.drawable.product12);
        invLine.setProduct(p);
        invoiceLines.add(invLine);

        Collections.shuffle(invoiceLines, new Random(System.nanoTime()));

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.invoce_lines);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new InvoiceLineAdapter(invoiceLines));

        return rootView;
    }
}
