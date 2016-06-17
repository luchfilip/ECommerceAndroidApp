package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerceandroidapp.data.CompanyDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Company;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public class CompanyFragment extends Fragment {

    private static final String STATE_COMPANY = "STATE_COMPANY";
    private Company mCompany;

    public CompanyFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_company, container, false);
        final CompanyDB companyDB = new CompanyDB(getContext(), Utils.getCurrentUser(getContext()));

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState!=null){
                        if (savedInstanceState.containsKey(STATE_COMPANY)) {
                            mCompany = savedInstanceState.getParcelable(STATE_COMPANY);
                        }
                    }

                    if (mCompany==null) {
                        mCompany = companyDB.getCompany();
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
                                final Button saveButton = (Button) rootView.findViewById(R.id.save_button);

                                if (mCompany!=null){
                                    businessPartnerName.setText(mCompany.getName());
                                    businessPartnerName.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mCompany.setName(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    businessPartnerCommercialName.setText(mCompany.getCommercialName());
                                    businessPartnerCommercialName.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mCompany.setCommercialName(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    businessPartnerTaxId.setText(mCompany.getTaxId());
                                    businessPartnerTaxId.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mCompany.setTaxId(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    businessPartnerAddress.setText(mCompany.getAddress());
                                    businessPartnerAddress.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mCompany.setAddress(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    businessPartnerContactPerson.setText(mCompany.getContactPerson());
                                    businessPartnerContactPerson.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mCompany.setContactPerson(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    businessPartnerEmailAddress.setText(mCompany.getEmailAddress());
                                    businessPartnerEmailAddress.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mCompany.setEmailAddress(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    businessPartnerPhoneNumber.setText(mCompany.getPhoneNumber());
                                    businessPartnerPhoneNumber.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mCompany.setPhoneNumber(s.toString());
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
                                            if (mCompany!=null) {
                                                String result = companyDB.insertUpdateCompany(mCompany);
                                                if (result==null){
                                                    saveButton.setText(getString(R.string.update));
                                                    Toast.makeText(getContext(), getString(R.string.company_updated_successfully), Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(getContext(), String.valueOf(result), Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                Company company = new Company();
                                                company.setName(businessPartnerName.getText().toString());
                                                company.setCommercialName(businessPartnerCommercialName.getText().toString());
                                                company.setTaxId(businessPartnerTaxId.getText().toString());
                                                company.setAddress(businessPartnerAddress.getText().toString());
                                                company.setContactPerson(businessPartnerContactPerson.getText().toString());
                                                company.setEmailAddress(businessPartnerEmailAddress.getText().toString());
                                                company.setPhoneNumber(businessPartnerPhoneNumber.getText().toString());

                                                String result = companyDB.insertUpdateCompany(company);
                                                if (result==null){
                                                    saveButton.setText(getString(R.string.update));
                                                    Toast.makeText(getContext(), getString(R.string.company_updated_successfully), Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(getContext(), String.valueOf(result), Toast.LENGTH_LONG).show();
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
        outState.putParcelable(STATE_COMPANY, mCompany);
        super.onSaveInstanceState(outState);
    }
}
