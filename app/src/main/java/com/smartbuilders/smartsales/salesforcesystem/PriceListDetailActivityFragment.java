package com.smartbuilders.smartsales.salesforcesystem;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartbuilders.smartsales.ecommerce.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PriceListDetailActivityFragment extends Fragment {

    public PriceListDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_price_list_detail, container, false);
    }
}
