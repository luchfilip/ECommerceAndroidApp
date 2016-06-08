package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.businessRules.BusinessPartnerBR;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.BusinessPartner;

/**
 * Created by stein on 5/1/2016.
 */
public class DialogRegisterBusinessPartner extends DialogFragment {

    private static final String STATE_CURRENT_USER = "STATE_CURRENT_USER";

    private User mCurrentUser;
    private DialogAddToShoppingSale mDialogAddToShoppingSale;

    public DialogRegisterBusinessPartner() {
        // Empty constructor required for DialogFragment
    }

    public static DialogRegisterBusinessPartner newInstance(User user, DialogAddToShoppingSale dialogAddToShoppingSale){
        DialogRegisterBusinessPartner editQtyRequestedDialogFragment = new DialogRegisterBusinessPartner();
        editQtyRequestedDialogFragment.mCurrentUser = user;
        editQtyRequestedDialogFragment.mDialogAddToShoppingSale = dialogAddToShoppingSale;
        return editQtyRequestedDialogFragment;
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
                    BusinessPartnerDB businessPartnerDB = new BusinessPartnerDB(getContext(), mCurrentUser);
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
                            mDialogAddToShoppingSale.initViews();
                            dismiss();
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
            });
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        try {
            if(getActivity()!=null && getActivity().getWindow()!=null){
                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                ((InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDismiss(dialog);
    }
}
