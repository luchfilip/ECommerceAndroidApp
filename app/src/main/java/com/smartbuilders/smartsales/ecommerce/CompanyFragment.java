package com.smartbuilders.smartsales.ecommerce;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.data.UserCompanyDB;
import com.smartbuilders.smartsales.ecommerce.model.Company;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public class CompanyFragment extends Fragment implements DialogEditCompanyLogo.Callback {

    private static final String STATE_COMPANY = "STATE_COMPANY";

    private User mUser;
    private ImageView companyLogoImageView;
    private View noImageLoadedTextView;
    private Company mCompany;

    public CompanyFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_company, container, false);

        mUser = Utils.getCurrentUser(getContext());
        final UserCompanyDB userCompanyDB = new UserCompanyDB(getContext(), mUser);

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

                                noImageLoadedTextView = rootView.findViewById(R.id.no_image_loaded_textView);

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
                                                String result = userCompanyDB.updateUserCompany(mCompany);
                                                if (result==null){
                                                    saveButton.setText(getString(R.string.update));
                                                    Toast.makeText(getContext(), getString(R.string.company_updated_successfully), Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getContext(), String.valueOf(result), Toast.LENGTH_SHORT).show();
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

                                                String result;
                                                if (saveButton.getText().equals(getString(R.string.update))) {
                                                    result = userCompanyDB.updateUserCompany(company);
                                                } else {
                                                    result = userCompanyDB.insertUserCompany(company);
                                                }
                                                if (result==null){
                                                    saveButton.setText(getString(R.string.update));
                                                    Toast.makeText(getContext(), getString(R.string.company_updated_successfully), Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getContext(), String.valueOf(result), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    });
                                }

                                companyLogoImageView = (ImageView) rootView.findViewById(R.id.company_logo_imageView);
                                Bitmap image = Utils.getUserCompanyImage(getContext(), mUser);
                                if (image != null) {
                                    companyLogoImageView.setImageBitmap(image);
                                    companyLogoImageView.setVisibility(View.VISIBLE);
                                    noImageLoadedTextView.setVisibility(View.GONE);
                                } else {
                                    noImageLoadedTextView.setVisibility(View.VISIBLE);
                                    companyLogoImageView.setVisibility(View.GONE);
                                }

                                rootView.findViewById(R.id.choose_company_logo_image_button).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        pickImage();
                                    }
                                });

                                rootView.findViewById(R.id.discard_image_button).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        new AlertDialog.Builder(getContext())
                                                .setMessage(getString(R.string.discard_image_question))
                                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if (Utils.deleteUserCompanyImage(getContext(), mUser)) {
                                                            noImageLoadedTextView.setVisibility(View.VISIBLE);
                                                            companyLogoImageView.setVisibility(View.GONE);
                                                        }
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

    private void pickImage(){
        DialogEditCompanyLogo dialogEditCompanyLogo =
                DialogEditCompanyLogo.newInstance(mUser);
        dialogEditCompanyLogo.setTargetFragment(this, 0);
        dialogEditCompanyLogo.show(getActivity().getSupportFragmentManager(),
                DialogEditCompanyLogo.class.getSimpleName());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_COMPANY, mCompany);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void reloadCompanyLogoImageView() {
        if (companyLogoImageView != null) {
            Bitmap image = Utils.getUserCompanyImage(getContext(), mUser);
            if (image != null) {
                companyLogoImageView.setImageBitmap(image);
                companyLogoImageView.setVisibility(View.VISIBLE);
                noImageLoadedTextView.setVisibility(View.GONE);
            } else {
                noImageLoadedTextView.setVisibility(View.VISIBLE);
                companyLogoImageView.setVisibility(View.GONE);
            }
        }
    }
}
