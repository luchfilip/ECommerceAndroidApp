package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Invoice;

public class InvoicesListActivity extends AppCompatActivity implements InvoicesListFragment.Callback {

    private static final String INVOICEDETAIL_FRAGMENT_TAG = "INVOICEDETAIL_FRAGMENT_TAG";
    public static final String KEY_CURRENT_USER = "KEY_CURRENT_USER";
    public static final String STATE_CURRENT_USER = "state_current_user";
    private User mCurrentUser;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices_list);

        if(findViewById(R.id.invoice_detail_container) != null){
            // If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.invoice_detail_container, new InvoiceDetailFragment(),
                                INVOICEDETAIL_FRAGMENT_TAG)
                        .commit();
            }
        }else{
            mTwoPane = false;
        }

        getSupportActionBar().setElevation(0);
    }

    @Override
    public void onItemSelected(Invoice invoice) {
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putInt(InvoiceDetailActivity.KEY_INVOICE_ID, invoice.getId());

            InvoiceDetailFragment fragment = new InvoiceDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.invoice_detail_container, fragment, INVOICEDETAIL_FRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(InvoicesListActivity.this, InvoiceDetailActivity.class);
            intent.putExtra(InvoiceDetailActivity.KEY_INVOICE_ID, invoice.getId());
            startActivity(intent);
            finish();
        }
    }

}
