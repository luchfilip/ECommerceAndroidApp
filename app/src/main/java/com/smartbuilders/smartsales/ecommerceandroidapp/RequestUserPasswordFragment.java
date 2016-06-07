package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.services.RequestUserPasswordService;

/**
 * A placeholder fragment containing a simple view.
 */
public class RequestUserPasswordFragment extends Fragment {
    public static final String ACTION_RESP =
            "RequestUserPasswordFragment.ResponseReceiver.ACTION_RESP";
    public static final String MESSAGE =
            "RequestUserPasswordFragment.ResponseReceiver.MESSAGE";

    private View progressContainer;
    private ResponseReceiver receiver;

    public RequestUserPasswordFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_request_user_password, container, false);

        final EditText serverAddress = (EditText) rootView.findViewById(R.id.serverAddress_editText);
        final EditText userName = (EditText) rootView.findViewById(R.id.userName_editText);
        final EditText userEmail = (EditText) rootView.findViewById(R.id.userEmail_editText);

        progressContainer = rootView.findViewById(R.id.progressContainer);

        IntentFilter filter = new IntentFilter(ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        getContext().registerReceiver(receiver, filter);

        rootView.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent msgIntent = new Intent(getContext(), RequestUserPasswordService.class);
                    validateInputFields(serverAddress.getText().toString(), userName.getText().toString(),
                            userEmail.getText().toString());
                    msgIntent.putExtra(RequestUserPasswordService.SERVER_ADDRESS, serverAddress.getText().toString());
                    msgIntent.putExtra(RequestUserPasswordService.USER_NAME, userName.getText().toString());
                    msgIntent.putExtra(RequestUserPasswordService.USER_EMAIL, userEmail.getText().toString());
                    getContext().startService(msgIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        return rootView;
    }

    private void validateInputFields(String serverAddress, String userName, String userEmail) {

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
