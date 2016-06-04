package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
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
 * Jesus Sarco 03.06.2016
 */
public class RegisterBusinessPartnerActivity extends AppCompatActivity {

    public static final String KEY_CURRENT_USER = "KEY_CURRENT_USER";
    private static final String STATE_CURRENT_USER = "state_current_user";

    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_business_partner);

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        if(getIntent()!=null && getIntent().getExtras()!=null){
            if(getIntent().getExtras().containsKey(KEY_CURRENT_USER)){
                mCurrentUser = getIntent().getExtras().getParcelable(KEY_CURRENT_USER);
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, mCurrentUser, false);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText businessPartnerName = (EditText) findViewById(R.id.business_partner_name_editText);
        final EditText businessPartnerCommercialName = (EditText) findViewById(R.id.business_partner_commercial_name_editText);
        final EditText businessPartnerTaxId = (EditText) findViewById(R.id.business_partner_tax_id_editText);
        final EditText businessPartnerAddress = (EditText) findViewById(R.id.business_partner_address_editText);
        final EditText businessPartnerContactName = (EditText) findViewById(R.id.business_partner_contact_person_name_editText);
        final EditText businessPartnerEmailAddress = (EditText) findViewById(R.id.business_partner_email_address_editText);
        final EditText businessPartnerPhoneNumber = (EditText) findViewById(R.id.business_partner_phone_number_editText);

        Button saveButton = (Button) findViewById(R.id.save_button);
        if (saveButton!=null) {
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BusinessPartner businessPartner = new BusinessPartner();
                    businessPartner.setName(businessPartnerName.getText().toString());
                    businessPartner.setCommercialName(businessPartnerCommercialName.getText().toString());
                    businessPartner.setTaxId(businessPartnerTaxId.getText().toString());
                    businessPartner.setAddress(businessPartnerAddress.getText().toString());
                    businessPartner.setContactPerson(businessPartnerContactName.getText().toString());
                    businessPartner.setEmailAddress(businessPartnerEmailAddress.getText().toString());
                    businessPartner.setPhoneNumber(businessPartnerPhoneNumber.getText().toString());

                    String result = BusinessPartnerBR.validateBusinessPartner(businessPartner,
                            RegisterBusinessPartnerActivity.this, mCurrentUser);
                    if (result==null) {
                        result = (new BusinessPartnerDB(RegisterBusinessPartnerActivity.this, mCurrentUser))
                                .registerBusinessPartner(businessPartner);
                        if (result==null){
                            startActivity(new Intent(RegisterBusinessPartnerActivity.this, BusinessPartnersActivity.class)
                                    .putExtra(BusinessPartnersActivity.KEY_CURRENT_USER, mCurrentUser));
                            finish();
                        } else {
                            Toast.makeText(RegisterBusinessPartnerActivity.this, String.valueOf(result), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        new AlertDialog.Builder(RegisterBusinessPartnerActivity.this)
                                .setMessage(result)
                                .setNeutralButton(R.string.accept, null)
                                .show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
        super.onSaveInstanceState(outState);
    }
}
