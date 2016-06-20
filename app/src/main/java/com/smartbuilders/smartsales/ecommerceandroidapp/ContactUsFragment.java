package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;

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

        String companyName = getString(R.string.company_name);
        String companyNameTaxId = getString(R.string.company_tax_id);
        String companyContactCenterPhone = "0800-Febeca-0";
        String companyMasterPhone = "+58-241-856.7200";
        String companyFax = "+58-241-856.7120";
        String companyEmailAddress = "atencionalcliente@febeca.com";
        String companyWebPage = "atencionalcliente@febeca.com";
        String companyAddress = "Av. Fundo La Unión, Parcela L13, L19, Zona industrial Castillito, San Diego, Edo. Carabobo. República Bolivariana de Venezuela.";

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

        if(!TextUtils.isEmpty(companyName)){
            companyNameTextView.setText(companyName);
        }else{
            companyNameTextView.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(companyNameTaxId)){
            companyTaxIdTextView.setText(companyNameTaxId);
        }else{
            companyTaxIdTextView.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(companyContactCenterPhone)){
            contactCenterPhoneTextView.setText(companyContactCenterPhone);
        }else{
            contactCenterLabelTextView.setVisibility(View.GONE);
            contactCenterPhoneTextView.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(companyMasterPhone)){
            masterPhoneTextView.setText(companyMasterPhone);
        }else{
            masterPhoneTableRow.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(companyFax)){
            faxTextView.setText(companyFax);
        }else{
            faxTableRow.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(companyEmailAddress)){
            emailAddressTextView.setText(companyEmailAddress);
        }else{
            emailAddressTableRow.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(companyWebPage)){
            webPageTextView.setText(companyWebPage);
        }else{
            webPageTextView.setVisibility(View.GONE);
        }


        if(!TextUtils.isEmpty(companyAddress)){
            addressTextView.setText(companyAddress);
        }else{
            addressLabelTextView.setVisibility(View.GONE);
            addressTextView.setVisibility(View.GONE);
        }

        return view;
    }
}
