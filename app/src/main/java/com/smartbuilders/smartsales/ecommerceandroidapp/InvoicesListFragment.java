package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.InvoicesListAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Invoice;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class InvoicesListFragment extends Fragment {

    private ListView mListView;
    private InvoicesListAdapter mInvoicesListAdapter;

    public interface Callback {
        public void onItemSelected(Invoice invoice);
    }

    public InvoicesListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_invoices_list, container, false);

        ArrayList<Invoice> invoices = new ArrayList<Invoice>();
        Invoice invoice = new Invoice();
        invoice.setId(1);
        invoices.add(invoice);

        invoice = new Invoice();
        invoice.setId(2);
        invoices.add(invoice);

        invoice = new Invoice();
        invoice.setId(3);
        invoices.add(invoice);

        invoice = new Invoice();
        invoice.setId(4);
        invoices.add(invoice);

        invoice = new Invoice();
        invoice.setId(5);
        invoices.add(invoice);

        invoice = new Invoice();
        invoice.setId(6);
        invoices.add(invoice);

        invoice = new Invoice();
        invoice.setId(7);
        invoices.add(invoice);

        mInvoicesListAdapter = new InvoicesListAdapter(getActivity(), invoices);

        mListView = (ListView) rootView.findViewById(R.id.invoices_list);
        mListView.setAdapter(mInvoicesListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Invoice invoice = (Invoice) adapterView.getItemAtPosition(position);
                if (invoice != null) {
                    ((Callback) getActivity()).onItemSelected(invoice);
                }
            }
        });

        return rootView;
    }
}
