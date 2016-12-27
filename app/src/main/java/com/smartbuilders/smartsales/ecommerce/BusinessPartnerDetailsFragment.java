package com.smartbuilders.smartsales.ecommerce;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public class BusinessPartnerDetailsFragment extends Fragment {

    private static final String STATE_BUSINESS_PARTNER_ID = "state_business_partner_id";

    private int mBusinessPartnerId;
    private BusinessPartner mBusinessPartner;

    public interface Callback {
        void onBusinessPartnerSelected(int businessPartnerId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_business_partner_details, container, false);

        new Thread() {
            @Override
            public void run() {
                try {
                    if (getArguments()!=null) {
                        if (getArguments().containsKey(BusinessPartnerDetailsActivity.KEY_BUSINESS_PARTNER_ID)) {
                            mBusinessPartnerId = getArguments().getInt(BusinessPartnerDetailsActivity.KEY_BUSINESS_PARTNER_ID);
                        }
                    } else if (getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null) {
                        if(getActivity().getIntent().getExtras().containsKey(BusinessPartnerDetailsActivity.KEY_BUSINESS_PARTNER_ID)) {
                            mBusinessPartnerId = getActivity().getIntent().getExtras().getInt(BusinessPartnerDetailsActivity.KEY_BUSINESS_PARTNER_ID);
                        }
                    }

                    if(savedInstanceState!=null){
                        if(savedInstanceState.containsKey(STATE_BUSINESS_PARTNER_ID)){
                            mBusinessPartnerId = savedInstanceState.getInt(STATE_BUSINESS_PARTNER_ID);
                        }
                    }

                    mBusinessPartner = (new BusinessPartnerDB(getContext(), Utils.getCurrentUser(getContext())))
                            .getBusinessPartnerById(mBusinessPartnerId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        try {
                            if (mBusinessPartner !=null){
                                ((TextView) rootView.findViewById(R.id.business_partner_internal_code_textView))
                                        .setText(mBusinessPartner.getInternalCode());
                                ((TextView) rootView.findViewById(R.id.business_partner_name_textView))
                                        .setText(mBusinessPartner.getName());
                                ((TextView) rootView.findViewById(R.id.business_partner_commercial_name_textView))
                                        .setText(mBusinessPartner.getCommercialName());
                                ((TextView) rootView.findViewById(R.id.business_partner_tax_id_textView))
                                        .setText(mBusinessPartner.getTaxId());
                                ((TextView) rootView.findViewById(R.id.business_partner_address_textView))
                                        .setText(mBusinessPartner.getAddress());
                                ((TextView) rootView.findViewById(R.id.business_partner_email_address_textView))
                                        .setText(mBusinessPartner.getEmailAddress());
                                ((TextView) rootView.findViewById(R.id.business_partner_phone_number_textView))
                                        .setText(mBusinessPartner.getPhoneNumber());
                                rootView.findViewById(R.id.init_session_button).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        new AlertDialog.Builder(getContext())
                                                .setMessage(getString(R.string.init_session_business_partner_question, mBusinessPartner.getName()))
                                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Utils.setAppCurrentBusinessPartnerId(getContext(), mBusinessPartner.getId());
                                                        Toast.makeText(getContext(), getString(R.string.session_loaded_detail,
                                                                mBusinessPartner.getName()), Toast.LENGTH_SHORT).show();
                                                        ((Callback) getActivity()).onBusinessPartnerSelected(mBusinessPartner.getId());
                                                    }
                                                })
                                                .setNegativeButton(R.string.no, null)
                                                .show();
                                    }
                                });
                            }
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_BUSINESS_PARTNER_ID, mBusinessPartnerId);
        super.onSaveInstanceState(outState);
    }
}
