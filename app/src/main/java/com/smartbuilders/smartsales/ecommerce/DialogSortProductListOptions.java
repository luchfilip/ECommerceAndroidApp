package com.smartbuilders.smartsales.ecommerce;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.jasgcorp.ids.model.User;

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
    public static final int SORT_BY_PRODUCT_AVAILABILITY_ASC = 4;
    public static final int SORT_BY_PRODUCT_AVAILABILITY_DESC = 5;

    private User mUser;
    private int mCurrentSortOption;

    public static DialogSortProductListOptions newInstance(User user, int currentSortOption){
        DialogSortProductListOptions dialogSortProductListOptions = new DialogSortProductListOptions();
        dialogSortProductListOptions.mUser = user;
        dialogSortProductListOptions.mCurrentSortOption = currentSortOption;
        return dialogSortProductListOptions;
    }

    public interface Callback {
        void sortProductsList(int sortOption, User user);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
            if(savedInstanceState.containsKey(STATE_CURRENT_SORT_OPTION)){
                mCurrentSortOption = savedInstanceState.getInt(STATE_CURRENT_SORT_OPTION);
            }
        }

        final View view = inflater.inflate(R.layout.dialog_sort_product_list_options, container);

        switch (mCurrentSortOption){
            case SORT_BY_PRODUCT_NAME_ASC:
                ((RadioGroup) view.findViewById(R.id.sort_products_list_option_radio_group))
                        .check(R.id.sort_by_product_name_asc);
                break;
            case SORT_BY_PRODUCT_NAME_DESC:
                ((RadioGroup) view.findViewById(R.id.sort_products_list_option_radio_group))
                        .check(R.id.sort_by_product_name_desc);
                break;
            case SORT_BY_PRODUCT_INTERNAL_CODE_ASC:
                ((RadioGroup) view.findViewById(R.id.sort_products_list_option_radio_group))
                        .check(R.id.sort_by_product_internal_code_asc);
                break;
            case SORT_BY_PRODUCT_INTERNAL_CODE_DESC:
                ((RadioGroup) view.findViewById(R.id.sort_products_list_option_radio_group))
                        .check(R.id.sort_by_product_internal_code_desc);
                break;
            case SORT_BY_PRODUCT_AVAILABILITY_ASC:
                ((RadioGroup) view.findViewById(R.id.sort_products_list_option_radio_group))
                        .check(R.id.sort_by_product_availability_asc);
                break;
            case SORT_BY_PRODUCT_AVAILABILITY_DESC:
                ((RadioGroup) view.findViewById(R.id.sort_products_list_option_radio_group))
                        .check(R.id.sort_by_product_availability_desc);
                break;
        }

        ((RadioGroup) view.findViewById(R.id.sort_products_list_option_radio_group))
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId){
                            case R.id.sort_by_product_name_asc:
                                ((Callback) getActivity()).sortProductsList(SORT_BY_PRODUCT_NAME_ASC, mUser);
                                break;
                            case R.id.sort_by_product_name_desc:
                                ((Callback) getActivity()).sortProductsList(SORT_BY_PRODUCT_NAME_DESC, mUser);
                                break;
                            case R.id.sort_by_product_internal_code_asc:
                                ((Callback) getActivity()).sortProductsList(SORT_BY_PRODUCT_INTERNAL_CODE_ASC, mUser);
                                break;
                            case R.id.sort_by_product_internal_code_desc:
                                ((Callback) getActivity()).sortProductsList(SORT_BY_PRODUCT_INTERNAL_CODE_DESC, mUser);
                                break;
                            case R.id.sort_by_product_availability_asc:
                                ((Callback) getActivity()).sortProductsList(SORT_BY_PRODUCT_AVAILABILITY_ASC, mUser);
                                break;
                            case R.id.sort_by_product_availability_desc:
                                ((Callback) getActivity()).sortProductsList(SORT_BY_PRODUCT_AVAILABILITY_DESC, mUser);
                                break;
                        }
                        dismiss();
                    }
                });

        getDialog().setTitle(R.string.sort_by);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mUser);
        outState.putInt(STATE_CURRENT_SORT_OPTION, mCurrentSortOption);
        super.onSaveInstanceState(outState);
    }
}
