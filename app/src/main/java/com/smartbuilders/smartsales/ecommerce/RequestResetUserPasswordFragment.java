package com.smartbuilders.smartsales.ecommerce;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.services.RequestResetUserCredentialsService;
import com.smartbuilders.smartsales.ecommerce.utils.EmailValidator;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * A placeholder fragment containing a simple view.
 */
public class RequestResetUserPasswordFragment extends Fragment {

    public static final String ACTION_RESP = BuildConfig.APPLICATION_ID + "." + RequestResetUserPasswordFragment.class.getSimpleName() + ".ACTION_RESP";
    public static final String MESSAGE = BuildConfig.APPLICATION_ID + "." + RequestResetUserPasswordFragment.class.getSimpleName() + ".MESSAGE";

    private ResponseReceiver receiver;
    private boolean mServiceRunning;
    private Button submit;
    private ProgressDialog waitPlease;

    public RequestResetUserPasswordFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_request_reset_user_password, container, false);

        final EditText userNameEditText = (EditText) rootView.findViewById(R.id.userName_editText);
        final EditText userEmailEditText = (EditText) rootView.findViewById(R.id.userEmail_editText);

        if (TextUtils.isEmpty(BuildConfig.SERVER_ADDRESS)) {
            rootView.findViewById(R.id.serverAddress_editText).setVisibility(View.VISIBLE);
        }

        if (getActivity()!=null && getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null) {
            if (getActivity().getIntent().getExtras().containsKey(RequestResetUserPasswordActivity.KEY_USER_NAME)) {
                if (userNameEditText!=null) {
                    userNameEditText.setText(getActivity().getIntent().getExtras().getString(RequestResetUserPasswordActivity.KEY_USER_NAME));
                    userNameEditText.setEnabled(false);
                }
            }
        }

        submit = (Button) rootView.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.warning)
                        .setMessage(R.string.warning_reset_user_credentials_message)
                        .setPositiveButton(R.string.reset_password, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String serverAddress = !TextUtils.isEmpty(BuildConfig.SERVER_ADDRESS)
                                        ? BuildConfig.SERVER_ADDRESS : ((EditText) rootView.findViewById(R.id.serverAddress_editText)).getText().toString();
                                final String userName = userNameEditText.getText().toString();
                                final String userEmail = userEmailEditText.getText().toString();
                                if (!mServiceRunning && !TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userEmail)) {
                                    lockScreen();

                                    new AsyncTask<Void, Void, String>() {

                                        @Override
                                        protected String doInBackground(Void... params) {
                                            try {
                                                URL url = new URL(serverAddress);
                                                URLConnection conn = url.openConnection();
                                                conn.setConnectTimeout(1000 * 2);//2 seconds
                                                conn.connect();

                                                Intent msgIntent = new Intent(getContext(), RequestResetUserCredentialsService.class);
                                                msgIntent.putExtra(RequestResetUserCredentialsService.SERVER_ADDRESS, serverAddress);
                                                msgIntent.putExtra(RequestResetUserCredentialsService.USER_NAME, userName);
                                                msgIntent.putExtra(RequestResetUserCredentialsService.USER_EMAIL, userEmail);
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
                                } else if (TextUtils.isEmpty(userName)) {
                                    Toast.makeText(getContext(), "Debe llenar el campo Nombre de usuario", Toast.LENGTH_LONG).show();
                                } else if (TextUtils.isEmpty(userEmail)) {
                                    Toast.makeText(getContext(), "Debe llenar el campo Correo electrónico", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .setCancelable(false)
                        .show();
            }
        });

        rootView.findViewById(R.id.go_back_textView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        if(Utils.isServiceRunning(getContext(), RequestResetUserCredentialsService.class)) {
            lockScreen();
        } else {
            unlockScreen(null);
        }

        return rootView;
    }

    private void lockScreen(){
        Utils.lockScreenOrientation(getActivity());
        if (waitPlease==null || !waitPlease.isShowing()){
            waitPlease = ProgressDialog.show(getContext(), null,
                    getString(R.string.sending_request_wait_please), true, false);
        }
        mServiceRunning = true;
        submit.setEnabled(false);
    }

    private void unlockScreen(final String message){
        if(getActivity()!=null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(message!=null){
                        new AlertDialog.Builder(getContext())
                                .setMessage(message)
                                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Utils.unlockScreenOrientation(getActivity());
                                    }
                                })
                                .setNeutralButton(R.string.go_back, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getActivity().finish();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    } else {
                        Utils.unlockScreenOrientation(getActivity());
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

    private boolean validateInputFields(String userEmail) {
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

    class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction()!=null && intent.getAction().equals(ACTION_RESP)){
                unlockScreen(intent.getExtras().getString(MESSAGE));
            }
        }
    }
}
