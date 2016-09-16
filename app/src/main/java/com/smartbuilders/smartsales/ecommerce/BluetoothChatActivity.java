/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.smartbuilders.smartsales.ecommerce;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.bluetoothchat.BluetoothChatService;
import com.smartbuilders.smartsales.ecommerce.providers.BluetoothConnectionProvider;

public class BluetoothChatActivity extends AppCompatActivity {

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;

    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    public void setStatus(CharSequence subTitle) {
        final ActionBar actionBar = getSupportActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    private BroadcastReceiver bluetoothConnectionProviderReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null && intent.getAction()!=null){
                Bundle extras = intent.getExtras();
                if(extras!=null){
                    if (intent.getAction().equals(BluetoothConnectionProvider.BLUETOOTH_STATE_CHANGE_BROADCAST)) {
                        switch (extras.getInt("state")) {
                            case BluetoothChatService.STATE_CONNECTED:
                                setStatus(extras.getString("status"));
                                mConversationArrayAdapter.clear();
                                break;
                            case BluetoothChatService.STATE_CONNECTING:
                                setStatus(extras.getString("status"));
                                break;
                            case BluetoothChatService.STATE_LISTEN:
                            case BluetoothChatService.STATE_NONE:
                                setStatus(extras.getString("status"));
                                break;
                        }
                        mConversationArrayAdapter.clear();
                    } else if(intent.getAction().equals(BluetoothConnectionProvider.MESSAGE_READ_BROADCAST)) {
                        mConversationArrayAdapter.add(extras.getString("readMessage"));
                    } else if(intent.getAction().equals(BluetoothConnectionProvider.MESSAGE_WRITE_BROADCAST)) {
                        mConversationArrayAdapter.add(extras.getString("writeMessage"));
                    }
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(BluetoothConnectionProvider.INIT_BLUETOOTH_ADAPTER_URI, null, null, null, null);
            if(cursor==null || !cursor.moveToNext() || cursor.getString(0).equals(String.valueOf(Boolean.FALSE))){
                Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
                finish();
            }
        } finally {
            if (cursor!=null) {
                try {
                    cursor.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        mConversationView = (ListView) findViewById(R.id.in);
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        mSendButton = (Button) findViewById(R.id.button_send);
    }


    @Override
    public void onStart() {
        super.onStart();
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(BluetoothConnectionProvider.IS_BLUETOOTH_ENABLED_URI, null, null, null, null);
            // If BT is not on, request that it be enabled.
            // setupChat() will then be called during onActivityResult
            if(cursor==null || !cursor.moveToNext() || cursor.getString(0).equals(String.valueOf(Boolean.FALSE))){
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                // Otherwise, setup the chat session
            }else{
                //try {
                //    cursor.close();
                //} catch (SQLException e) {
                //    //do nothing
                //}
                //cursor = getContentResolver().query(BluetoothConnectionProvider.IS_CHAT_SERVICE_NULL_URI, null, null, null, null);
                //if (cursor!=null && cursor.moveToNext() && cursor.getString(0).equals(String.valueOf(Boolean.TRUE))) {
                    getContentResolver().query(BluetoothConnectionProvider.INIT_BLUETOOTH_CHAT_SERVICE_URI, null, null, null, null);
                //}
                setupChat();
            }
        } finally {
            if (cursor!=null) {
                try {
                    cursor.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            IntentFilter intentFilter = new IntentFilter(BluetoothConnectionProvider.BLUETOOTH_STATE_CHANGE_BROADCAST);
            intentFilter.addAction(BluetoothConnectionProvider.MESSAGE_READ_BROADCAST);
            intentFilter.addAction(BluetoothConnectionProvider.MESSAGE_WRITE_BROADCAST);
            registerReceiver(bluetoothConnectionProviderReceiver, intentFilter);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(BluetoothConnectionProvider.IS_CHAT_SERVICE_NULL_URI, null, null, null, null);
            // Performing this check in onResume() covers the case in which BT was
            // not enabled during onStart(), so we were paused to enable it...
            // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
            if (cursor!=null && cursor.moveToNext() && cursor.getString(0).equals(String.valueOf(Boolean.FALSE))) {
                //try {
                //    cursor.close();
                //} catch (SQLException e) {
                //    e.printStackTrace();
                //}
                //cursor = getContentResolver()
                //        .query(BluetoothConnectionProvider.GET_CHAT_SERVICE_STATE_URI, null, null, null, null);
                //Only if the state is STATE_NONE, do we know that we haven't started already
                //if (cursor!=null && cursor.moveToNext() && cursor.getInt(0)==BluetoothChatService.STATE_NONE) {
                //    // Start the Bluetooth chat services
                    getContentResolver()
                            .query(BluetoothConnectionProvider.START_CHAT_SERVICE_URI, null, null, null, null);
                //}
            }else{
                getContentResolver().query(BluetoothConnectionProvider.INIT_BLUETOOTH_CHAT_SERVICE_URI, null, null, null, null);
                getContentResolver().query(BluetoothConnectionProvider.START_CHAT_SERVICE_URI, null, null, null, null);
            }
        } finally {
            if (cursor!=null) {
                try {
                    cursor.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try{
            unregisterReceiver(bluetoothConnectionProviderReceiver);
        }catch(Exception e){
            //do nothing
        }
    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<>(this, R.layout.message);

        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                TextView textView = (TextView) findViewById(R.id.edit_text_out);
                String message = textView.getText().toString();
                sendMessage(message);
            }
        });

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * Makes this device discoverable.
     */
    private void ensureDiscoverable() {
        Cursor cursor = null;
        try {
            cursor = getContentResolver()
                    .query(BluetoothConnectionProvider.GET_BLUETOOTH_ADAPTER_SCAN_MODE_URI, null, null, null, null);
            if(cursor!=null && cursor.moveToNext() && cursor.getInt(0) != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);
            }
        } finally {
            if (cursor!=null) {
                try {
                    cursor.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        Cursor cursor = null;
        try {
            cursor = getContentResolver()
                    .query(BluetoothConnectionProvider.GET_CHAT_SERVICE_STATE_URI, null, null, null, null);
            // Check that we're actually connected before trying anything
            if(cursor!=null && cursor.moveToNext() && cursor.getInt(0)!=BluetoothChatService.STATE_CONNECTED){
                    Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
                return;
            }
        } finally {
            if (cursor!=null) {
                try {
                    cursor.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message and tell the BluetoothChatService to write
            getContentResolver().query(BluetoothConnectionProvider.SEND_MESSAGE_URI.buildUpon()
                    .appendQueryParameter(BluetoothConnectionProvider.KEY_MESSAGE, message).build(), null, null, null, null);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }

    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When BluetoothDeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When BluetoothDeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    //Cursor cursor = null;
                    //try {
                        //cursor = getContentResolver()
                        //        .query(BluetoothConnectionProvider.IS_CHAT_SERVICE_NULL_URI, null, null, null, null);
                        //if (cursor!=null && cursor.moveToNext() && cursor.getString(0).equals(String.valueOf(Boolean.TRUE))) {
                            getContentResolver()
                                    .query(BluetoothConnectionProvider.INIT_BLUETOOTH_CHAT_SERVICE_URI, null, null, null, null);
                        //}
                        setupChat();
                    //} finally {
                    //    if (cursor!=null) {
                    //        try {
                    //            cursor.close();
                    //        } catch (SQLException e) {
                    //            e.printStackTrace();
                    //        }
                    //    }
                    //}
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    /**
     * Establish connection with other device
     *
     * @param data   An {@link Intent} with {@link BluetoothDeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        if (data!=null && data.getExtras()!=null
                && data.getExtras().containsKey(BluetoothDeviceListActivity.EXTRA_DEVICE_ADDRESS)
                && data.getExtras().getString(BluetoothDeviceListActivity.EXTRA_DEVICE_ADDRESS)!=null) {
            getContentResolver().query(BluetoothConnectionProvider.CONNECT_DEVICE_URI.buildUpon()
                    .appendQueryParameter(BluetoothConnectionProvider.KEY_DEVICE_MAC_ADDRESS,
                            data.getExtras().getString(BluetoothDeviceListActivity.EXTRA_DEVICE_ADDRESS))
                    .appendQueryParameter(BluetoothConnectionProvider.KEY_USE_SECURE_CONNECTION, Boolean.valueOf(secure).toString())
                    .build(), null, null, null, null);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bluetooth_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.secure_connect_scan) {
            Intent serverIntent = new Intent(this, BluetoothDeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            return true;
        } else if (i == R.id.insecure_connect_scan) {
            Intent serverIntent = new Intent(this, BluetoothDeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
            return true;
        } else if (i == R.id.discoverable) {
            ensureDiscoverable();
            return true;
        }
        return false;
    }
}
