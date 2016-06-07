package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.services.RequestResetUserPasswordService;

/**
 * A placeholder fragment containing a simple view.
 */
public class RequestResetUserPasswordFragment extends Fragment {

    public static final String ACTION_RESP =
            "RequestResetUserPasswordFragment.ResponseReceiver.ACTION_RESP";
    public static final String MESSAGE =
            "RequestResetUserPasswordFragment.ResponseReceiver.MESSAGE";

    private View progressContainer;
    private ResponseReceiver receiver;


    public RequestResetUserPasswordFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_request_reset_user_password, container, false);

        final EditText serverAddress = (EditText) rootView.findViewById(R.id.serverAddress_editText);
        final EditText userEmail = (EditText) rootView.findViewById(R.id.userEmail_editText);

        progressContainer = rootView.findViewById(R.id.progressContainer);

        rootView.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent msgIntent = new Intent(getContext(), RequestResetUserPasswordService.class);
                    validateInputFields(serverAddress.getText().toString(), userEmail.getText().toString());
                    msgIntent.putExtra(RequestResetUserPasswordService.SERVER_ADDRESS, serverAddress.getText().toString());
                    msgIntent.putExtra(RequestResetUserPasswordService.USER_EMAIL, userEmail.getText().toString());
                    getContext().startService(msgIntent);
                    progressContainer.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        IntentFilter filter = new IntentFilter(ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        getContext().registerReceiver(receiver, filter);
        super.onStart();
    }

    @Override
    public void onStop() {
        try {
            getContext().unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    private void validateInputFields(String serverAddress, String userEmail) {

    }

    class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction()!=null && intent.getAction().equals(ACTION_RESP)){
                progressContainer.setVisibility(View.GONE);
            }
        }
    }
}
