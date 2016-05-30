package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.InvoiceLineAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.InvoiceLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;

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
