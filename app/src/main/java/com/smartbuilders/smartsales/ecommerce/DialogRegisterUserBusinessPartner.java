package com.smartbuilders.smartsales.ecommerce;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.businessRules.UserBusinessPartnerBR;
import com.smartbuilders.smartsales.ecommerce.data.UserBusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;

/**
 * Created by stein on 5/1/2016.
 */
public class DialogRegisterUserBusinessPartner extends DialogFragment {

    private static final String STATE_CURRENT_USER = "STATE_CURRENT_USER";

    private User mCurrentUser;

    public DialogRegisterUserBusinessPartner() {
        // Empty constructor required for DialogFragment
    }

    public interface Callback {
        void reloadBusinessPartnersList();
    }

    public static DialogRegisterUserBusinessPartner newInstance(User user){
        DialogRegisterUserBusinessPartner dialogRegisterUserBusinessPartner = new DialogRegisterUserBusinessPartner();
        dialogRegisterUserBusinessPartner.setCancelable(false);
        dialogRegisterUserBusinessPartner.mCurrentUser = user;
        return dialogRegisterUserBusinessPartner;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        final View view = inflater.inflate(R.layout.dialog_register_business_partner, container);
        
        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        final EditText businessPartnerName = (EditText) view.findViewById(R.id.business_partner_name_editText);
        final EditText businessPartnerCommercialName = (EditText) view.findViewById(R.id.business_partner_commercial_name_editText);
        final EditText businessPartnerTaxId = (EditText) view.findViewById(R.id.business_partner_tax_id_editText);
        final EditText businessPartnerAddress = (EditText) view.findViewById(R.id.business_partner_address_editText);
        final EditText businessPartnerContactPerson = (EditText) view.findViewById(R.id.business_partner_contact_person_name_editText);
        final EditText businessPartnerEmailAddress = (EditText) view.findViewById(R.id.business_partner_email_address_editText);
        final EditText businessPartnerPhoneNumber = (EditText) view.findViewById(R.id.business_partner_phone_number_editText);

        Button saveButton = (Button) view.findViewById(R.id.save_button);

        if (saveButton!=null) {
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserBusinessPartnerDB userBusinessPartnerDB = new UserBusinessPartnerDB(getContext(), mCurrentUser);
                    BusinessPartner businessPartner = new BusinessPartner();
                    businessPartner.setName(businessPartnerName.getText().toString());
                    businessPartner.setCommercialName(businessPartnerCommercialName.getText().toString());
                    businessPartner.setTaxId(businessPartnerTaxId.getText().toString());
                    businessPartner.setAddress(businessPartnerAddress.getText().toString());
                    businessPartner.setContactPerson(businessPartnerContactPerson.getText().toString());
                    businessPartner.setEmailAddress(businessPartnerEmailAddress.getText().toString());
                    businessPartner.setPhoneNumber(businessPartnerPhoneNumber.getText().toString());
                    String result = UserBusinessPartnerBR.validateBusinessPartner(businessPartner,
                            getContext(), mCurrentUser);
                    if (result==null) {
                        result = userBusinessPartnerDB.registerUserBusinessPartner(businessPartner);
                        if (result==null){
                            try {
                                if (getActivity()!=null && getActivity() instanceof BusinessPartnersListActivity) {
                                    ((Callback) getActivity()).reloadBusinessPartnersList();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                if (getTargetFragment()!=null && getTargetFragment() instanceof DialogAddToShoppingSale) {
                                    ((DialogAddToShoppingSale) getTargetFragment()).initViews();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), String.valueOf(result), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        new AlertDialog.Builder(getContext())
                                .setMessage(result)
                                .setNeutralButton(R.string.accept, null)
                                .show();
                    }
                }
            });
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
        super.onSaveInstanceState(outState);
    }
}
