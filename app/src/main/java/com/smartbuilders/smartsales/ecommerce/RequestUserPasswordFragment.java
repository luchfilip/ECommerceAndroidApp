package com.smartbuilders.smartsales.ecommerce;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.smartbuilders.smartsales.ecommerce.services.RequestUserPasswordService;
import com.smartbuilders.smartsales.ecommerce.utils.EmailValidator;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class RequestUserPasswordFragment extends Fragment {

    public static final String ACTION_RESP = BuildConfig.APPLICATION_ID + "." + RequestUserPasswordFragment.class.getSimpleName() + ".ACTION_RESP";
    public static final String MESSAGE = BuildConfig.APPLICATION_ID + "." + RequestUserPasswordFragment.class.getSimpleName() + ".MESSAGE";

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

        if (TextUtils.isEmpty(BuildConfig.SERVER_ADDRESS)) {
            rootView.findViewById(R.id.serverAddress_editText).setVisibility(View.VISIBLE);
        }

        submit = (Button) rootView.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String serverAddress = !TextUtils.isEmpty(BuildConfig.SERVER_ADDRESS)
                        ? BuildConfig.SERVER_ADDRESS : ((EditText) rootView.findViewById(R.id.serverAddress_editText)).getText().toString();
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

        //if (rootView.findViewById(R.id.user_prefix_spinner) != null) {
        //    List<String> spinnerArray =  new ArrayList<>();
        //    spinnerArray.add("J");
        //    spinnerArray.add("V");
        //    spinnerArray.add("E");
        //    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray);
        //
        //    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //    ((Spinner) rootView.findViewById(R.id.user_prefix_spinner)).setAdapter(adapter);
        //}

        rootView.findViewById(R.id.go_back_textView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

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
                                .setNeutralButton(R.string.accept, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Utils.unlockScreenOrientation(getActivity());
                                    }
                                })
                                .setPositiveButton(R.string.go_back, new DialogInterface.OnClickListener() {
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

    class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction()!=null && intent.getAction().equals(ACTION_RESP)){
                unlockScreen(intent.getExtras().getString(MESSAGE));
            }
        }
    }
}
