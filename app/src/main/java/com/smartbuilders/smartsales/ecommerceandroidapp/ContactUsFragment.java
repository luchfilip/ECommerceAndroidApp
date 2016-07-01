package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerceandroidapp.data.CompanyDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Company;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContactUsFragment extends Fragment {

    public ContactUsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_contact_us, container, false);

        Company company = (new CompanyDB(getContext())).getCompany();

        TextView companyNameTextView = (TextView) view.findViewById(R.id.company_name);
        TextView companyTaxIdTextView = (TextView) view.findViewById(R.id.company_tax_id);
        TextView contactCenterLabelTextView = (TextView) view.findViewById(R.id.contact_center_label);
        TextView contactCenterPhoneTextView = (TextView) view.findViewById(R.id.contact_center_phone);
        TextView masterPhoneTextView = (TextView) view.findViewById(R.id.master_phone);
        TableRow masterPhoneTableRow = (TableRow) view.findViewById(R.id.master_phone_tableRow);
        TextView faxTextView = (TextView) view.findViewById(R.id.fax_number);
        TableRow faxTableRow = (TableRow) view.findViewById(R.id.fax_tableRow);
        TextView emailAddressTextView = (TextView) view.findViewById(R.id.email_address);
        TableRow emailAddressTableRow = (TableRow) view.findViewById(R.id.email_address_tableRow);
        TextView webPageTextView = (TextView) view.findViewById(R.id.web_page);
        TextView addressLabelTextView = (TextView) view.findViewById(R.id.address_label);
        TextView addressTextView = (TextView) view.findViewById(R.id.address);

        if(!TextUtils.isEmpty(company.getCommercialName())){
            companyNameTextView.setText(company.getCommercialName());
        }else{
            companyNameTextView.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(company.getTaxId())){
            companyTaxIdTextView.setText(getString(R.string.tax_id, company.getTaxId()));
        }else{
            companyTaxIdTextView.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(company.getContactCenterPhoneNumber())){
            contactCenterPhoneTextView.setText(company.getContactCenterPhoneNumber());
        }else{
            contactCenterLabelTextView.setVisibility(View.GONE);
            contactCenterPhoneTextView.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(company.getPhoneNumber())){
            masterPhoneTextView.setText(company.getPhoneNumber());
        }else{
            masterPhoneTableRow.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(company.getFaxNumber())){
            faxTextView.setText(company.getFaxNumber());
        }else{
            faxTableRow.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(company.getEmailAddress())){
            emailAddressTextView.setText(company.getEmailAddress());
        }else{
            emailAddressTableRow.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(company.getWebPage())){
            webPageTextView.setText(company.getWebPage());
        }else{
            webPageTextView.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(company.getAddress())){
            addressTextView.setText(company.getAddress());
        }else{
            addressLabelTextView.setVisibility(View.GONE);
            addressTextView.setVisibility(View.GONE);
        }
        return view;
    }
}
