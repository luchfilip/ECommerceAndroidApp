package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.services.RequestResetUserPasswordService;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.EmailValidator;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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
    private boolean mServiceRunning;
    private Button submit;

    public RequestResetUserPasswordFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_request_reset_user_password, container, false);

        progressContainer = rootView.findViewById(R.id.progressContainer);

        submit = (Button) rootView.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverAddress = ((EditText) rootView.findViewById(R.id.serverAddress_editText)).getText().toString();
                String userEmail = ((EditText) rootView.findViewById(R.id.userEmail_editText)).getText().toString();
                if (!mServiceRunning && validateInputFields(serverAddress, userEmail)) {
                    new AsyncTask<String, Void, String>() {

                        @Override
                        protected String doInBackground(String... params) {
                            try {
                                String serverAddress = params[0];
                                String userEmail = params[1];

                                URL url = new URL(params[0]);
                                URLConnection conn = url.openConnection();
                                conn.setConnectTimeout(1000 * 2);//2 seconds
                                conn.connect();

                                Intent msgIntent = new Intent(getContext(), RequestResetUserPasswordService.class);
                                msgIntent.putExtra(RequestResetUserPasswordService.SERVER_ADDRESS, serverAddress);
                                msgIntent.putExtra(RequestResetUserPasswordService.USER_EMAIL, userEmail);
                                getContext().startService(msgIntent);
                                lockScreen();
                            } catch (MalformedURLException e) {
                                // the URL is not in a valid form
                                e.printStackTrace();
                                unlockScreen(getString(R.string.error_server_address_malformedurlexception));
                            } catch (IOException e) {
                                // the connection couldn't be established
                                e.printStackTrace();
                                unlockScreen(getString(R.string.error_server_address_ioexception));
                            } catch (Exception e) {
                                e.printStackTrace();
                                unlockScreen(e.getMessage());
                            }
                            return null;
                        }
                    }.execute(serverAddress, userEmail);
                }
            }
        });

        if(Utils.isServiceRunning(getContext(), RequestResetUserPasswordService.class)) {
            lockScreen();
        } else {
            unlockScreen(null);
        }

        return rootView;
    }

    private void lockScreen(){
        mServiceRunning = true;
        submit.setEnabled(false);
        progressContainer.setVisibility(View.VISIBLE);
    }

    private void unlockScreen(String message){
        if(message!=null){
            new AlertDialog.Builder(getContext())
                    .setMessage(message)
                    .setNeutralButton(R.string.accept, null)
                    .show();
        }
        submit.setEnabled(true);
        progressContainer.setVisibility(View.GONE);
        mServiceRunning = false;
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

    private boolean validateInputFields(String serverAddress, String userEmail) {
        EmailValidator emailValidator = new EmailValidator();
        emailValidator.validate(userEmail);
        return true;
    }

    class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction()!=null && intent.getAction().equals(ACTION_RESP)){
                unlockScreen(intent.getExtras().getString(MESSAGE));
            }
        }
    }
}
