package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.businessRules.BusinessPartnerBR;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

/**
 * Created by stein on 4/6/2016.
 */
public class RegisterBusinessPartnerFragment extends Fragment {

    private static final String STATE_BUSINESS_PARTNER = "state_business_partner";

    private User mCurrentUser;
    private BusinessPartner mBusinessPartner;

    public interface Callback {
        void onBusinessPartnerRegistered();
        void onBusinessPartnerUpdated();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView  = inflater.inflate(R.layout.fragment_register_business_partner, container, false);

        new Thread() {
            @Override
            public void run() {
                try {
                    if (getArguments()!=null) {
                        if (getArguments().containsKey(RegisterBusinessPartnerActivity.KEY_BUSINESS_PARTNER)) {
                            mBusinessPartner = getArguments().getParcelable(RegisterBusinessPartnerActivity.KEY_BUSINESS_PARTNER);
                        }
                    } else if (getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null) {
                        if(getActivity().getIntent().getExtras().containsKey(RegisterBusinessPartnerActivity.KEY_BUSINESS_PARTNER)) {
                            mBusinessPartner = getActivity().getIntent().getExtras().getParcelable(RegisterBusinessPartnerActivity.KEY_BUSINESS_PARTNER);
                        }
                    }

                    if(savedInstanceState!=null){
                        if(savedInstanceState.containsKey(STATE_BUSINESS_PARTNER)){
                            mBusinessPartner = savedInstanceState.getParcelable(STATE_BUSINESS_PARTNER);
                        }
                    }

                    mCurrentUser = Utils.getCurrentUser(getContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final EditText businessPartnerName = (EditText) rootView.findViewById(R.id.business_partner_name_editText);
                                final EditText businessPartnerCommercialName = (EditText) rootView.findViewById(R.id.business_partner_commercial_name_editText);
                                final EditText businessPartnerTaxId = (EditText) rootView.findViewById(R.id.business_partner_tax_id_editText);
                                final EditText businessPartnerAddress = (EditText) rootView.findViewById(R.id.business_partner_address_editText);
                                final EditText businessPartnerContactPerson = (EditText) rootView.findViewById(R.id.business_partner_contact_person_name_editText);
                                final EditText businessPartnerEmailAddress = (EditText) rootView.findViewById(R.id.business_partner_email_address_editText);
                                final EditText businessPartnerPhoneNumber = (EditText) rootView.findViewById(R.id.business_partner_phone_number_editText);

                                Button saveButton = (Button) rootView.findViewById(R.id.save_button);

                                if (mBusinessPartner!=null){
                                    businessPartnerName.setText(mBusinessPartner.getName());
                                    businessPartnerName.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mBusinessPartner.setName(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    businessPartnerCommercialName.setText(mBusinessPartner.getCommercialName());
                                    businessPartnerCommercialName.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mBusinessPartner.setCommercialName(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    businessPartnerTaxId.setText(mBusinessPartner.getTaxId());
                                    businessPartnerTaxId.setEnabled(false);

                                    businessPartnerAddress.setText(mBusinessPartner.getAddress());
                                    businessPartnerAddress.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mBusinessPartner.setAddress(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    businessPartnerContactPerson.setText(mBusinessPartner.getContactPerson());
                                    businessPartnerContactPerson.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mBusinessPartner.setContactPerson(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    businessPartnerEmailAddress.setText(mBusinessPartner.getEmailAddress());
                                    businessPartnerEmailAddress.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mBusinessPartner.setEmailAddress(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    businessPartnerPhoneNumber.setText(mBusinessPartner.getPhoneNumber());
                                    businessPartnerPhoneNumber.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mBusinessPartner.setPhoneNumber(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    saveButton.setText(getString(R.string.update));
                                }

                                if (saveButton!=null) {
                                    saveButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            BusinessPartnerDB businessPartnerDB = new BusinessPartnerDB(getContext(), mCurrentUser);
                                            if (mBusinessPartner!=null) {
                                                String result = businessPartnerDB.updateBusinessPartner(mBusinessPartner);
                                                if (result==null){
                                                    ((Callback) getActivity()).onBusinessPartnerUpdated();
                                                    Toast.makeText(getContext(), getString(R.string.business_partner_updated_successfully), Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(getContext(), String.valueOf(result), Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                BusinessPartner businessPartner = new BusinessPartner();
                                                businessPartner.setName(businessPartnerName.getText().toString());
                                                businessPartner.setCommercialName(businessPartnerCommercialName.getText().toString());
                                                businessPartner.setTaxId(businessPartnerTaxId.getText().toString());
                                                businessPartner.setAddress(businessPartnerAddress.getText().toString());
                                                businessPartner.setContactPerson(businessPartnerContactPerson.getText().toString());
                                                businessPartner.setEmailAddress(businessPartnerEmailAddress.getText().toString());
                                                businessPartner.setPhoneNumber(businessPartnerPhoneNumber.getText().toString());
                                                String result = BusinessPartnerBR.validateBusinessPartner(businessPartner,
                                                        getContext(), mCurrentUser);
                                                if (result==null) {
                                                    result = businessPartnerDB.registerBusinessPartner(businessPartner);
                                                    if (result==null){
                                                        ((Callback) getActivity()).onBusinessPartnerRegistered();
                                                    } else {
                                                        Toast.makeText(getContext(), String.valueOf(result), Toast.LENGTH_LONG).show();
                                                    }
                                                } else {
                                                    new AlertDialog.Builder(getContext())
                                                            .setMessage(result)
                                                            .setNeutralButton(R.string.accept, null)
                                                            .show();
                                                }
                                            }
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
        outState.putParcelable(STATE_BUSINESS_PARTNER, mBusinessPartner);
        super.onSaveInstanceState(outState);
    }
}
