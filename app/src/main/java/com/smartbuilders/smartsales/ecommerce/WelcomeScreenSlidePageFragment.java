package com.smartbuilders.smartsales.ecommerce;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by AlbertoSarco on 24/10/2016.
 */

public class WelcomeScreenSlidePageFragment extends Fragment {
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static WelcomeScreenSlidePageFragment create(int pageNumber) {
        WelcomeScreenSlidePageFragment fragment = new WelcomeScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public WelcomeScreenSlidePageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView;
        switch (mPageNumber) {
            case 0:
                // Inflate the layout.
                rootView = (ViewGroup) inflater
                        .inflate(R.layout.welcome_layout_welcome, container, false);
                break;
            case 1:
                // Inflate the layout.
                rootView = (ViewGroup) inflater
                        .inflate(R.layout.welcome_layout_business_partners, container, false);
                break;
            case 2:
                // Inflate the layout.
                rootView = (ViewGroup) inflater
                        .inflate(R.layout.welcome_layout_sales_order, container, false);
                break;
            case 3:
                // Inflate the layout.
                rootView = (ViewGroup) inflater
                        .inflate(R.layout.welcome_layout_wish_list, container, false);
                break;
            case 4:
                // Inflate the layout.
                rootView = (ViewGroup) inflater
                        .inflate(R.layout.welcome_layout_recommended_products, container, false);
                break;
            case 5:
                // Inflate the layout.
                rootView = (ViewGroup) inflater
                        .inflate(R.layout.welcome_layout_orders, container, false);
                break;
            case 6:
                // Inflate the layout.
                rootView = (ViewGroup) inflater
                        .inflate(R.layout.welcome_layout_order_tracking, container, false);
                break;
            case 7:
                // Inflate the layout.
                rootView = (ViewGroup) inflater
                        .inflate(R.layout.welcome_layout_share, container, false);

                if (rootView.findViewById(R.id.go_to_app_textView) != null) {
                    rootView.findViewById(R.id.go_to_app_textView).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getActivity().finish();
                        }
                    });
                }
                break;

            default:
                // Inflate the layout.
                rootView = (ViewGroup) inflater
                        .inflate(R.layout.empty_layout, container, false);
        }
        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}