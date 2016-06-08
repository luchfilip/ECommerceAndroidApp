package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.services.RequestUserPasswordService;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.EmailValidator;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * A placeholder fragment containing a simple view.
 */
public class RequestUserPasswordFragment extends Fragment {

    public static final String ACTION_RESP =
            "RequestUserPasswordFragment.ResponseReceiver.ACTION_RESP";
    public static final String MESSAGE =
            "RequestUserPasswordFragment.ResponseReceiver.MESSAGE";

    private ResponseReceiver receiver;
    private boolean mServiceRunning;
    private Button submit;
    private ProgressDialog waitPlease;

    public RequestUserPasswordFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_request_user_password, container, false);

        submit = (Button) rootView.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String serverAddress = ((EditText) rootView.findViewById(R.id.serverAddress_editText)).getText().toString();
                final String userName = ((EditText) rootView.findViewById(R.id.userName_editText)).getText().toString();
                final String userEmail = ((EditText) rootView.findViewById(R.id.userEmail_editText)).getText().toString();

                if (!mServiceRunning && validateInputFields(serverAddress, userName, userEmail)) {
                    lockScreen();

                    new AsyncTask<Void, Void, String>() {

                        @Override
                        protected String doInBackground(Void... params) {
                            try {
                                URL url = new URL(serverAddress);
                                URLConnection conn = url.openConnection();
                                conn.setConnectTimeout(1000*2);//2 seconds
                                conn.connect();

                                Intent msgIntent = new Intent(getContext(), RequestUserPasswordService.class);
                                msgIntent.putExtra(RequestUserPasswordService.SERVER_ADDRESS, serverAddress);
                                msgIntent.putExtra(RequestUserPasswordService.USER_NAME, userName);
                                msgIntent.putExtra(RequestUserPasswordService.USER_EMAIL, userEmail);
                                getContext().startService(msgIntent);
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
                    }.execute();
                }
            }
        });

        if(Utils.isServiceRunning(getContext(), RequestUserPasswordService.class)){
            lockScreen();
        }else{
            unlockScreen(null);
        }

        return rootView;
    }

    private boolean validateInputFields(String serverAddress, String userName, String userEmail) {
        EmailValidator emailValidator = new EmailValidator();
        if (!emailValidator.validate(userEmail)) {
            new AlertDialog.Builder(getContext())
                    .setMessage(R.string.invalid_email_format)
                    .setPositiveButton(R.string.accept, null)
                    .show();
            return false;
        }
        return true;
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
        if (waitPlease!=null && waitPlease.isShowing()) {
            waitPlease.cancel();
        }
        super.onStop();
    }

    private void lockScreen(){
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
        if (waitPlease==null || !waitPlease.isShowing()){
            waitPlease = ProgressDialog.show(getContext(), null,
                    getString(R.string.sending_request_wait_please), true, false);
        }
        mServiceRunning = true;
        submit.setEnabled(false);
    }

    private void unlockScreen(final String message){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(message!=null){
                    new AlertDialog.Builder(getContext())
                            .setMessage(message)
                            .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                                }
                            })
                            .setCancelable(false)
                            .show();
                } else {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                }
                submit.setEnabled(true);
                if (waitPlease!=null && waitPlease.isShowing()) {
                    waitPlease.cancel();
                    waitPlease = null;
                }
                mServiceRunning = false;

            }
        });
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
