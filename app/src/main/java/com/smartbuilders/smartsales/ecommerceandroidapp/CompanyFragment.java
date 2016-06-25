package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.UserCompanyDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Company;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.io.IOException;

/**
 * A placeholder fragment containing a simple view.
 */
public class CompanyFragment extends Fragment {

    private static final String STATE_COMPANY = "STATE_COMPANY";
    private static final int PICK_IMAGE = 100;

    private User mCurrentUser;
    private ImageView companyLogoImageView;
    private Company mCompany;

    public CompanyFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_company, container, false);

        mCurrentUser = Utils.getCurrentUser(getContext());
        final UserCompanyDB userCompanyDB = new UserCompanyDB(getContext(), mCurrentUser);

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
                        mCompany = userCompanyDB.getUserCompany();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final EditText companyName = (EditText) rootView.findViewById(R.id.business_partner_name_editText);
                                final EditText companyCommercialName = (EditText) rootView.findViewById(R.id.business_partner_commercial_name_editText);
                                final EditText companyTaxId = (EditText) rootView.findViewById(R.id.business_partner_tax_id_editText);
                                final EditText companyAddress = (EditText) rootView.findViewById(R.id.business_partner_address_editText);
                                final EditText companyContactPerson = (EditText) rootView.findViewById(R.id.business_partner_contact_person_name_editText);
                                final EditText companyEmailAddress = (EditText) rootView.findViewById(R.id.business_partner_email_address_editText);
                                final EditText companyPhoneNumber = (EditText) rootView.findViewById(R.id.business_partner_phone_number_editText);
                                final Button saveButton = (Button) rootView.findViewById(R.id.save_button);

                                companyLogoImageView = (ImageView) rootView.findViewById(R.id.company_logo_imageView);
                                companyLogoImageView.setImageBitmap(Utils.getImageFromUserCompanyDir(getContext(), mCurrentUser));
                                companyLogoImageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        pickImage();
                                    }
                                });

                                if (mCompany!=null){
                                    companyName.setText(mCompany.getName());
                                    companyName.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mCompany.setName(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    companyCommercialName.setText(mCompany.getCommercialName());
                                    companyCommercialName.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mCompany.setCommercialName(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    companyTaxId.setText(mCompany.getTaxId());
                                    companyTaxId.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mCompany.setTaxId(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    companyAddress.setText(mCompany.getAddress());
                                    companyAddress.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mCompany.setAddress(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    companyContactPerson.setText(mCompany.getContactPerson());
                                    companyContactPerson.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mCompany.setContactPerson(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    companyEmailAddress.setText(mCompany.getEmailAddress());
                                    companyEmailAddress.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mCompany.setEmailAddress(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    companyPhoneNumber.setText(mCompany.getPhoneNumber());
                                    companyPhoneNumber.addTextChangedListener(new TextWatcher(){
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
                                                String result = userCompanyDB.insertUpdateUserCompany(mCompany);
                                                if (result==null){
                                                    saveButton.setText(getString(R.string.update));
                                                    Toast.makeText(getContext(), getString(R.string.company_updated_successfully), Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(getContext(), String.valueOf(result), Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                Company company = new Company();
                                                company.setName(companyName.getText().toString());
                                                company.setCommercialName(companyCommercialName.getText().toString());
                                                company.setTaxId(companyTaxId.getText().toString());
                                                company.setAddress(companyAddress.getText().toString());
                                                company.setContactPerson(companyContactPerson.getText().toString());
                                                company.setEmailAddress(companyEmailAddress.getText().toString());
                                                company.setPhoneNumber(companyPhoneNumber.getText().toString());

                                                String result = userCompanyDB.insertUpdateUserCompany(company);
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

    private void pickImage(){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                try {
                    Utils.createImageInUserCompanyDir(getContext(), mCurrentUser,
                            getContext().getContentResolver().openInputStream(data.getData()));
                    companyLogoImageView.setImageBitmap(Utils.getImageFromUserCompanyDir(getContext(), mCurrentUser));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_COMPANY, mCompany);
        super.onSaveInstanceState(outState);
    }
}
