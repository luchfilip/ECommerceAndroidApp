package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;

/**
 * Created by stein on 26/5/2016.
 */
public class DialogSortProductListOptions extends DialogFragment {

    private static final String STATE_CURRENT_USER = "STATE_CURRENT_USER";
    private static final String STATE_CURRENT_SORT_OPTION = "STATE_CURRENT_SORT_OPTION";

    public static final int SORT_BY_PRODUCT_NAME_ASC = 0;
    public static final int SORT_BY_PRODUCT_NAME_DESC = 1;
    public static final int SORT_BY_PRODUCT_INTERNAL_CODE_ASC = 2;
    public static final int SORT_BY_PRODUCT_INTERNAL_CODE_DESC = 3;

    private User mUser;
    private int mCurrentSortOption;

    public static DialogSortProductListOptions newInstance(User user, int currentSortOption){
        DialogSortProductListOptions dialogSortProductListOptions = new DialogSortProductListOptions();
        dialogSortProductListOptions.mUser = user;
        dialogSortProductListOptions.mCurrentSortOption = currentSortOption;
        return dialogSortProductListOptions;
    }

    public interface Callback {
        void sortProductsList(int sortOption);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        final View view = inflater.inflate(R.layout.dialog_sort_product_list_options, container);

        switch (mCurrentSortOption){
            case SORT_BY_PRODUCT_NAME_ASC:
                ((RadioButton) view.findViewById(R.id.sort_by_product_name_asc)).setChecked(true);
                break;
            case SORT_BY_PRODUCT_NAME_DESC:
                ((RadioButton) view.findViewById(R.id.sort_by_product_name_desc)).setChecked(true);
                break;
            case SORT_BY_PRODUCT_INTERNAL_CODE_ASC:
                ((RadioButton) view.findViewById(R.id.sort_by_product_internal_code_asc)).setChecked(true);
                break;
            case SORT_BY_PRODUCT_INTERNAL_CODE_DESC:
                ((RadioButton) view.findViewById(R.id.sort_by_product_internal_code_desc)).setChecked(true);
                break;
        }

        view.findViewById(R.id.sort_by_product_name_asc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Callback) getActivity()).sortProductsList(SORT_BY_PRODUCT_NAME_ASC);
                dismiss();
            }
        });

        view.findViewById(R.id.sort_by_product_name_desc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Callback) getActivity()).sortProductsList(SORT_BY_PRODUCT_NAME_DESC);
                dismiss();
            }
        });

        view.findViewById(R.id.sort_by_product_internal_code_asc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Callback) getActivity()).sortProductsList(SORT_BY_PRODUCT_INTERNAL_CODE_ASC);
                dismiss();
            }
        });

        view.findViewById(R.id.sort_by_product_internal_code_desc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Callback) getActivity()).sortProductsList(SORT_BY_PRODUCT_INTERNAL_CODE_DESC);
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mUser);
        outState.putInt(STATE_CURRENT_SORT_OPTION, mCurrentSortOption);
        super.onSaveInstanceState(outState);
    }
}
