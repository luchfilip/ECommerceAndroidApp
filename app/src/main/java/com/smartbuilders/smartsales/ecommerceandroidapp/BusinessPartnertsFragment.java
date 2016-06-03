package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class BusinessPartnertsFragment extends Fragment {

    public BusinessPartnertsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_business_partners, container, false);
    }
}
