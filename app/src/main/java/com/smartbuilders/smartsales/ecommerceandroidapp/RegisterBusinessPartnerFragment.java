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
import com.jasgcorp.ids.model.UserProfile;
import com.smartbuilders.smartsales.ecommerceandroidapp.businessRules.UserBusinessPartnerBR;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.UserBusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

/**
 * Created by stein on 4/6/2016.
 */
public class RegisterBusinessPartnerFragment extends Fragment {

    private static final String STATE_BUSINESS_PARTNER_ID = "state_business_partner_id";
    private static final String STATE_BUSINESS_PARTNER = "state_business_partner";
    private static final String STATE_ORIGINAL_TAX_ID = "state_original_tax_id";

    private int mBusinessPartnerId;
    private BusinessPartner mBusinessPartner;
    private String mOriginalTaxId;

    public interface Callback {
        void onBusinessPartnerRegistered();
        void onBusinessPartnerUpdated();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView  = inflater.inflate(R.layout.fragment_register_business_partner, container, false);

        final User user = Utils.getCurrentUser(getContext());

        new Thread() {
            @Override
            public void run() {
                try {
                    if (getArguments()!=null) {
                        if (getArguments().containsKey(RegisterBusinessPartnerActivity.KEY_BUSINESS_PARTNER_ID)) {
                            mBusinessPartnerId = getArguments().getInt(RegisterBusinessPartnerActivity.KEY_BUSINESS_PARTNER_ID);
                        }
                    } else if (getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null) {
                        if(getActivity().getIntent().getExtras().containsKey(RegisterBusinessPartnerActivity.KEY_BUSINESS_PARTNER_ID)) {
                            mBusinessPartnerId = getActivity().getIntent().getExtras().getInt(RegisterBusinessPartnerActivity.KEY_BUSINESS_PARTNER_ID);
                        }
                    }

                    if(savedInstanceState!=null){
                        if(savedInstanceState.containsKey(STATE_BUSINESS_PARTNER_ID)){
                            mBusinessPartnerId = savedInstanceState.getInt(STATE_BUSINESS_PARTNER_ID);
                        }
                        if(savedInstanceState.containsKey(STATE_BUSINESS_PARTNER)){
                            mBusinessPartner = savedInstanceState.getParcelable(STATE_BUSINESS_PARTNER);
                        }
                        if(savedInstanceState.containsKey(STATE_ORIGINAL_TAX_ID)){
                            mOriginalTaxId = savedInstanceState.getString(STATE_ORIGINAL_TAX_ID);
                        }
                    }

                    if(mBusinessPartnerId>0 && mBusinessPartner ==null){
                        if(user!=null && user.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                            mBusinessPartner = (new UserBusinessPartnerDB(getContext(), user))
                                    .getActiveUserBusinessPartnerById(mBusinessPartnerId);
                            mOriginalTaxId = mBusinessPartner.getTaxId();
                        }else if(user!=null && user.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
                            mBusinessPartner = (new BusinessPartnerDB(getContext(), user))
                                    .getActiveBusinessPartnerById(mBusinessPartnerId);
                        }
                    }
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

                                if (mBusinessPartner !=null){
                                    businessPartnerName.setText(mBusinessPartner.getName());
                                    businessPartnerCommercialName.setText(mBusinessPartner.getCommercialName());
                                    businessPartnerTaxId.setText(mBusinessPartner.getTaxId());
                                    businessPartnerAddress.setText(mBusinessPartner.getAddress());
                                    businessPartnerContactPerson.setText(mBusinessPartner.getContactPerson());
                                    businessPartnerEmailAddress.setText(mBusinessPartner.getEmailAddress());
                                    businessPartnerPhoneNumber.setText(mBusinessPartner.getPhoneNumber());

                                    if(user!=null && user.getUserProfileId()==UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                                        businessPartnerName.addTextChangedListener(new TextWatcher(){
                                            public void afterTextChanged(Editable s) {
                                                mBusinessPartner.setName(s.toString());
                                            }
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                            public void onTextChanged(CharSequence s, int start, int before, int count){}
                                        });
                                        businessPartnerCommercialName.addTextChangedListener(new TextWatcher(){
                                            public void afterTextChanged(Editable s) {
                                                mBusinessPartner.setCommercialName(s.toString());
                                            }
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                            public void onTextChanged(CharSequence s, int start, int before, int count){}
                                        });
                                        businessPartnerTaxId.addTextChangedListener(new TextWatcher(){
                                            public void afterTextChanged(Editable s) {
                                                mBusinessPartner.setTaxId(s.toString());
                                            }
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                            public void onTextChanged(CharSequence s, int start, int before, int count){}
                                        });
                                        businessPartnerAddress.addTextChangedListener(new TextWatcher(){
                                            public void afterTextChanged(Editable s) {
                                                mBusinessPartner.setAddress(s.toString());
                                            }
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                            public void onTextChanged(CharSequence s, int start, int before, int count){}
                                        });
                                        businessPartnerContactPerson.addTextChangedListener(new TextWatcher(){
                                            public void afterTextChanged(Editable s) {
                                                mBusinessPartner.setContactPerson(s.toString());
                                            }
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                            public void onTextChanged(CharSequence s, int start, int before, int count){}
                                        });
                                        businessPartnerEmailAddress.addTextChangedListener(new TextWatcher(){
                                            public void afterTextChanged(Editable s) {
                                                mBusinessPartner.setEmailAddress(s.toString());
                                            }
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                            public void onTextChanged(CharSequence s, int start, int before, int count){}
                                        });
                                        businessPartnerPhoneNumber.addTextChangedListener(new TextWatcher(){
                                            public void afterTextChanged(Editable s) {
                                                mBusinessPartner.setPhoneNumber(s.toString());
                                            }
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                            public void onTextChanged(CharSequence s, int start, int before, int count){}
                                        });

                                        saveButton.setText(getString(R.string.update));
                                    }else{
                                        if(rootView.findViewById(R.id.business_partner_internal_code_tableRow)!=null){
                                            rootView.findViewById(R.id.business_partner_internal_code_tableRow).setVisibility(View.VISIBLE);
                                        }
                                        if(rootView.findViewById(R.id.business_partner_internal_code_textView)!=null){
                                            rootView.findViewById(R.id.business_partner_internal_code_textView).setVisibility(View.VISIBLE);
                                        }
                                        if(rootView.findViewById(R.id.business_partner_internal_code_editText)!=null){
                                            rootView.findViewById(R.id.business_partner_internal_code_editText).setVisibility(View.VISIBLE);
                                            ((EditText) rootView.findViewById(R.id.business_partner_internal_code_editText))
                                                    .setText(mBusinessPartner.getInternalCode());
                                        }
                                        businessPartnerName.setFocusable(false);
                                        businessPartnerCommercialName.setFocusable(false);
                                        businessPartnerTaxId.setFocusable(false);
                                        businessPartnerAddress.setFocusable(false);
                                        rootView.findViewById(R.id.business_partner_contact_person_name_textView).setVisibility(View.GONE);
                                        businessPartnerContactPerson.setVisibility(View.GONE);
                                        businessPartnerEmailAddress.setFocusable(false);
                                        businessPartnerPhoneNumber.setFocusable(false);
                                        saveButton.setVisibility(View.GONE);
                                    }
                                }

                                if (saveButton!=null && user!=null
                                        && user.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID) {
                                    saveButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            UserBusinessPartnerDB userBusinessPartnerDB = new UserBusinessPartnerDB(getContext(), user);
                                            if (mBusinessPartner !=null) {
                                                if(!mBusinessPartner.getTaxId().equals(mOriginalTaxId)){
                                                    String result = UserBusinessPartnerBR.validateBusinessPartner(mBusinessPartner,
                                                            getContext(), user);
                                                    if(result==null){
                                                        result = userBusinessPartnerDB.updateUserBusinessPartner(mBusinessPartner);
                                                        if (result==null){
                                                            mOriginalTaxId = mBusinessPartner.getTaxId();
                                                            ((Callback) getActivity()).onBusinessPartnerUpdated();
                                                            Toast.makeText(getContext(), getString(R.string.business_partner_updated_successfully), Toast.LENGTH_LONG).show();
                                                        } else {
                                                            Toast.makeText(getContext(), String.valueOf(result), Toast.LENGTH_LONG).show();
                                                        }
                                                    }else{
                                                        new AlertDialog.Builder(getContext())
                                                                .setMessage(result)
                                                                .setNeutralButton(R.string.accept, null)
                                                                .show();
                                                    }
                                                }else{
                                                    String result = userBusinessPartnerDB.updateUserBusinessPartner(mBusinessPartner);
                                                    if (result==null){
                                                        ((Callback) getActivity()).onBusinessPartnerUpdated();
                                                        Toast.makeText(getContext(), getString(R.string.business_partner_updated_successfully), Toast.LENGTH_LONG).show();
                                                    } else {
                                                        Toast.makeText(getContext(), String.valueOf(result), Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            } else {
                                                BusinessPartner userBusinessPartner = new BusinessPartner();
                                                userBusinessPartner.setName(businessPartnerName.getText().toString());
                                                userBusinessPartner.setCommercialName(businessPartnerCommercialName.getText().toString());
                                                userBusinessPartner.setTaxId(businessPartnerTaxId.getText().toString());
                                                userBusinessPartner.setAddress(businessPartnerAddress.getText().toString());
                                                userBusinessPartner.setContactPerson(businessPartnerContactPerson.getText().toString());
                                                userBusinessPartner.setEmailAddress(businessPartnerEmailAddress.getText().toString());
                                                userBusinessPartner.setPhoneNumber(businessPartnerPhoneNumber.getText().toString());
                                                String result = UserBusinessPartnerBR.validateBusinessPartner(userBusinessPartner,
                                                        getContext(), user);
                                                if (result==null) {
                                                    result = userBusinessPartnerDB.registerUserBusinessPartner(userBusinessPartner);
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
        outState.putInt(STATE_BUSINESS_PARTNER_ID, mBusinessPartnerId);
        outState.putParcelable(STATE_BUSINESS_PARTNER, mBusinessPartner);
        outState.putString(STATE_ORIGINAL_TAX_ID, mOriginalTaxId);
        super.onSaveInstanceState(outState);
    }
}
