package com.smartbuilders.smartsales.salesforcesystem;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.SalesOrdersListActivity;
import com.smartbuilders.smartsales.ecommerce.businessRules.SalesOrderBR;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.smartsales.ecommerce.view.DatePickerFragment;
import com.smartbuilders.synchronizer.ids.model.User;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShoppingSaleFinalizeOptionsFragment extends Fragment implements DatePickerFragment.Callback {

    private static final String STATE_VALID_TO = "STATE_VALID_TO";

    private User mUser;
    private EditText mValidToEditText;
    private String mValidToText;
    private ProgressDialog waitPlease;

    public ShoppingSaleFinalizeOptionsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_shopping_sale_finalize_options, container, false);

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState != null) {
                        if(savedInstanceState.containsKey(STATE_VALID_TO)) {
                            mValidToText = savedInstanceState.getString(STATE_VALID_TO);
                        }
                    }

                    mUser = Utils.getCurrentUser(getContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mValidToEditText = (EditText) rootView.findViewById(R.id.valid_to_editText);
                                mValidToEditText.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Date validTo = null;
                                        try {
                                            validTo = DateFormat.getDateInstance(DateFormat.MEDIUM,
                                                    new Locale("es","VE")).parse(mValidToEditText.getText().toString());
                                        } catch (ParseException e){
                                            // do nothing
                                        }
                                        catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        DialogFragment dialogFragment = DatePickerFragment.getInstance(validTo);
                                        dialogFragment.setTargetFragment(ShoppingSaleFinalizeOptionsFragment.this, 1);
                                        dialogFragment.show(getFragmentManager(), DatePickerFragment.class.getSimpleName());
                                    }
                                });
                                mValidToEditText.setText(mValidToText);

                                rootView.findViewById(R.id.proceed_to_checkout_shopping_sale_button)
                                        .setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                new AlertDialog.Builder(getContext())
                                                        .setMessage(R.string.proceed_to_checkout_quoatation_question)
                                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Date validTo = null;
                                                                try {
                                                                    validTo = DateFormat.getDateInstance(DateFormat.MEDIUM,
                                                                            new Locale("es", "VE")).parse(mValidToEditText.getText().toString());
                                                                } catch (ParseException e) {
                                                                    // do nothing
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                                closeSalesOrder(validTo);
                                                            }
                                                        })
                                                        .setNegativeButton(R.string.no, null)
                                                        .show();
                                            }
                                        });

                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                rootView.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                rootView.findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        }.start();
        return rootView;
    }

    @Override
    public void setDate(String date) {
        mValidToText = date;
        mValidToEditText.setText(date);
    }

    private void closeSalesOrder(final Date validTo){
        lockScreen();
        new Thread() {
            @Override
            public void run() {
                String result = null;
                try {
                    result = SalesOrderBR.createSalesOrderFromShoppingSale(getContext(), mUser, validTo);
                } catch (Exception e) {
                    e.printStackTrace();
                    result = e.getMessage();
                } finally {
                    unlockScreen(result);
                }
            }
        }.start();
    }

    private void lockScreen() {
        if (getActivity()!=null) {
            //Se bloquea la rotacion de la pantalla para evitar que se mate a la aplicacion
            Utils.lockScreenOrientation(getActivity());
            if (waitPlease==null || !waitPlease.isShowing()){
                waitPlease = ProgressDialog.show(getContext(), null,
                        getString(R.string.closing_sales_order_wait_please), true, false);
            }
        }
    }

    private void unlockScreen(final String message){
        if(getActivity()!=null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(message!=null){
                        new AlertDialog.Builder(getContext())
                                .setMessage(message)
                                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Utils.unlockScreenOrientation(getActivity());
                                    }
                                })
                                .setCancelable(false)
                                .show();
                        if (waitPlease!=null && waitPlease.isShowing()) {
                            waitPlease.cancel();
                            waitPlease = null;
                        }
                    } else {
                        startActivity(new Intent(getContext(), SalesOrdersListActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP));

                        if (waitPlease!=null && waitPlease.isShowing()) {
                            waitPlease.cancel();
                            waitPlease = null;
                        }
                        if (getActivity()!=null) {
                            getActivity().finish();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_VALID_TO, mValidToText);
        super.onSaveInstanceState(outState);
    }
}
