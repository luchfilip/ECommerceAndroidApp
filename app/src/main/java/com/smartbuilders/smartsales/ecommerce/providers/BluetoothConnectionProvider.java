package com.smartbuilders.smartsales.ecommerce.providers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.BuildConfig;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.bluetoothchat.BluetoothChatService;
import com.smartbuilders.smartsales.ecommerce.bluetoothchat.Constants;

/**
 * Created by Jesus Sarco on 12/9/2016.
 */
public class BluetoothConnectionProvider extends ContentProvider {

    private static final String TAG = BluetoothConnectionProvider.class.getSimpleName();

    // The authority is the symbolic name for the provider class
    public static final String AUTHORITY = BuildConfig.BluetoothConnectionProvider_AUTHORITY;

    private static final Uri CONTENT_URI 					    = Uri.parse("content://"+AUTHORITY);

    public static final Uri CONNECT_DEVICE_URI				    = Uri.withAppendedPath(CONTENT_URI, "connectDevice");
    public static final Uri DISCONNECT_DEVICE_URI 			    = Uri.withAppendedPath(CONTENT_URI, "disconnectDevice");
    public static final Uri SEND_MESSAGE_URI 			        = Uri.withAppendedPath(CONTENT_URI, "sendMessage");
    public static final Uri INIT_BLUETOOTH_ADAPTER_URI 	        = Uri.withAppendedPath(CONTENT_URI, "initBluetoothAdapter");
    public static final Uri IS_BLUETOOTH_ENABLED_URI 	        = Uri.withAppendedPath(CONTENT_URI, "isBluetoothEnabled");
    public static final Uri IS_CHAT_SERVICE_NULL_URI 	        = Uri.withAppendedPath(CONTENT_URI, "isChatServiceNull");
    public static final Uri INIT_BLUETOOTH_CHAT_SERVICE_URI     = Uri.withAppendedPath(CONTENT_URI, "initBluetoothChatService");
    public static final Uri GET_BLUETOOTH_ADAPTER_SCAN_MODE_URI = Uri.withAppendedPath(CONTENT_URI, "getBluetoothAdapterScanMode");
    public static final Uri GET_CHAT_SERVICE_STATE_URI          = Uri.withAppendedPath(CONTENT_URI, "getChatServiceState");
    public static final Uri START_CHAT_SERVICE_URI              = Uri.withAppendedPath(CONTENT_URI, "startChatService");

    public static final String KEY_DEVICE_MAC_ADDRESS           = "KEY_DEVICE_MAC_ADDRESS";
    public static final String KEY_USE_SECURE_CONNECTION        = "KEY_USE_SECURE_CONNECTION";
    public static final String KEY_MESSAGE                      = "KEY_MESSAGE";

    public static final String BLUETOOTH_STATE_CHANGE_BROADCAST = "BLUETOOTH_STATE_CHANGE_BROADCAST";
    public static final String MESSAGE_WRITE_BROADCAST          = "MESSAGE_WRITE_BROADCAST";
    public static final String MESSAGE_READ_BROADCAST           = "MESSAGE_READ_BROADCAST";

    private static final int CONNECT 						    = 1;
    private static final int DISCONNECT						    = 2;
    private static final int SEND_MESSAGE					    = 3;
    private static final int INIT_BLUETOOTH_ADAPTER			    = 4;
    private static final int IS_BLUETOOTH_ENABLED   		    = 5;
    private static final int IS_CHAT_SERVICE_NULL   		    = 6;
    private static final int INIT_BLUETOOTH_CHAT_SERVICE	    = 7;
    private static final int GET_BLUETOOTH_ADAPTER_SCAN_MODE    = 8;
    private static final int GET_CHAT_SERVICE_STATE             = 9;
    private static final int START_CHAT_SERVICE                 = 10;

    private static final UriMatcher uriMatcher;

    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "connectDevice", CONNECT);
        uriMatcher.addURI(AUTHORITY, "disconnectDevice", DISCONNECT);
        uriMatcher.addURI(AUTHORITY, "sendMessage", SEND_MESSAGE);
        uriMatcher.addURI(AUTHORITY, "initBluetoothAdapter", INIT_BLUETOOTH_ADAPTER);
        uriMatcher.addURI(AUTHORITY, "isBluetoothEnabled", IS_BLUETOOTH_ENABLED);
        uriMatcher.addURI(AUTHORITY, "isChatServiceNull", IS_CHAT_SERVICE_NULL);
        uriMatcher.addURI(AUTHORITY, "initBluetoothChatService", INIT_BLUETOOTH_CHAT_SERVICE);
        uriMatcher.addURI(AUTHORITY, "getBluetoothAdapterScanMode", GET_BLUETOOTH_ADAPTER_SCAN_MODE);
        uriMatcher.addURI(AUTHORITY, "getChatServiceState", GET_CHAT_SERVICE_STATE);
        uriMatcher.addURI(AUTHORITY, "startChatService", START_CHAT_SERVICE);
    }

    /**
     * Member object for the chat services
     */
    private BluetoothChatService mChatService;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter;

    private String mConnectedDeviceName;

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //FragmentActivity activity = getActivity();
            Intent intent = null;
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    intent = new Intent(BLUETOOTH_STATE_CHANGE_BROADCAST);
                    intent.putExtra("state", msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            if (getContext()!=null) {
                                intent.putExtra("status", getContext().getString(R.string.title_connected_to, mConnectedDeviceName));
                            }
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            if (getContext()!=null) {
                                intent.putExtra("status", getContext().getString(R.string.title_connecting));
                            }
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            if (getContext()!=null) {
                                intent.putExtra("status", getContext().getString(R.string.title_not_connected));
                            }
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Log.d(TAG, "Constants.MESSAGE_WRITE, writeMessage: "+writeMessage);
                    intent = new Intent(MESSAGE_WRITE_BROADCAST);
                    intent.putExtra("writeMessage", "Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d(TAG, "Constants.MESSAGE_READ, readMessage: "+readMessage);
                    intent = new Intent(MESSAGE_READ_BROADCAST);
                    intent.putExtra("readMessage", mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    //Name of the connected device
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (getContext()!=null) {
                        Toast.makeText(getContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (getContext()!=null) {
                        Toast.makeText(getContext(), msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            if (intent!=null && getContext()!=null) {
                getContext().sendBroadcast(intent);
            }
        }
    };

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        Cursor response;
        switch (uriMatcher.match(uri)) {
            case CONNECT:
                response = connectDevice(uri);
                break;
            case DISCONNECT:
                response = disconnect();
                break;
            case SEND_MESSAGE:
                response = sendMessage(uri);
                break;
            case INIT_BLUETOOTH_ADAPTER:
                response = initBluetoothAdapter();
                break;
            case IS_BLUETOOTH_ENABLED:
                response = isBluetoothEnabled();
                break;
            case IS_CHAT_SERVICE_NULL:
                response = isChatServiceNull();
                break;
            case INIT_BLUETOOTH_CHAT_SERVICE:
                response = initBluetoothChatService();
                break;
            case GET_BLUETOOTH_ADAPTER_SCAN_MODE:
                response = getBluetoothAdapterScanMode();
                break;
            case GET_CHAT_SERVICE_STATE:
                response = getChatServiceState();
                break;
            case START_CHAT_SERVICE:
                response = startChatService();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return response;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    private Cursor disconnect() {
        Log.d(TAG, "disconnect()");
        MatrixCursor cursor = new MatrixCursor(new String[]{"result", "exception_message"});
        try {
            if (mChatService != null) {
                mChatService.stop();
            }
            cursor.addRow(new Object[]{String.valueOf(Boolean.TRUE), null});
        } catch (Exception e) {
            e.printStackTrace();
            cursor.addRow(new Object[]{String.valueOf(Boolean.FALSE), e.getMessage()});
        }
        return cursor;
    }

    private Cursor sendMessage(Uri uri) {
        Log.d(TAG, "sendMessage()");
        MatrixCursor cursor = new MatrixCursor(new String[]{"result", "exception_message"});
        try {
            if(uri.getQueryParameter(KEY_MESSAGE)!=null){
                // Get the message to send
                String message = uri.getQueryParameter(KEY_MESSAGE);
                // Get the message bytes and tell the BluetoothChatService to write
                byte[] send = message.getBytes();
                mChatService.write(send);
                cursor.addRow(new Object[]{String.valueOf(Boolean.TRUE), null});
            }else{
                throw new Exception("No parameters found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            cursor.addRow(new Object[]{String.valueOf(Boolean.FALSE), e.getMessage()});
        }
        return cursor;
    }

    private Cursor initBluetoothAdapter() {
        Log.d(TAG, "initBluetoothAdapter()");
        MatrixCursor cursor = new MatrixCursor(new String[]{"result", "exception_message"});
        try {
            // Get local Bluetooth adapter
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            // If the adapter is null, then Bluetooth is not supported
            cursor.addRow(new Object[]{String.valueOf(mBluetoothAdapter != null), null});
        } catch (Exception e) {
            e.printStackTrace();
            cursor.addRow(new Object[]{String.valueOf(Boolean.FALSE), e.getMessage()});
        }
        return cursor;
    }

    private Cursor isBluetoothEnabled() {
        Log.d(TAG, "isBluetoothEnabled()");
        MatrixCursor cursor = new MatrixCursor(new String[]{"result", "exception_message"});
        try {
            cursor.addRow(new Object[]{String.valueOf(mBluetoothAdapter.isEnabled()), null});
        } catch (Exception e) {
            e.printStackTrace();
            cursor.addRow(new Object[]{String.valueOf(Boolean.FALSE), e.getMessage()});
        }
        return cursor;
    }

    private Cursor isChatServiceNull() {
        Log.d(TAG, "isChatServiceNull()");
        MatrixCursor cursor = new MatrixCursor(new String[]{"result"});
        cursor.addRow(new Object[]{String.valueOf(mChatService==null)});
        return cursor;
    }

    private Cursor initBluetoothChatService() {
        Log.d(TAG, "initBluetoothChatService()");
        MatrixCursor cursor = new MatrixCursor(new String[]{"result", "exception_message"});
        try {
            mChatService = new BluetoothChatService(getContext(), mHandler);
            cursor.addRow(new Object[]{String.valueOf(Boolean.TRUE), null});
        } catch (Exception e) {
            e.printStackTrace();
            cursor.addRow(new Object[]{String.valueOf(Boolean.FALSE), e.getMessage()});
        }
        return cursor;
    }

    private Cursor getBluetoothAdapterScanMode() {
        Log.d(TAG, "initBluetoothChatService()");
        MatrixCursor cursor = new MatrixCursor(new String[]{"scanMode", "exception_message"});
        try {
            cursor.addRow(new Object[]{mBluetoothAdapter.getScanMode(), null});
        } catch (Exception e) {
            e.printStackTrace();
            cursor.addRow(new Object[]{-1, e.getMessage()});
        }
        return cursor;
    }

    private Cursor getChatServiceState() {
        Log.d(TAG, "getChatServiceState()");
        MatrixCursor cursor = new MatrixCursor(new String[]{"state", "exception_message"});
        try {
            cursor.addRow(new Object[]{mChatService.getState(), null});
        } catch (Exception e) {
            e.printStackTrace();
            cursor.addRow(new Object[]{-1, e.getMessage()});
        }
        return cursor;
    }

    private Cursor startChatService() {
        Log.d(TAG, "startChatService()");
        MatrixCursor cursor = new MatrixCursor(new String[]{"result", "exception_message"});
        try {
            mChatService.start();
            cursor.addRow(new Object[]{String.valueOf(Boolean.TRUE), null});
        } catch (Exception e) {
            e.printStackTrace();
            cursor.addRow(new Object[]{String.valueOf(Boolean.FALSE), e.getMessage()});
        }
        return cursor;
    }

    /**
     * Establish connection with other divice
     */
    private Cursor connectDevice(Uri uri) {
        Log.d(TAG, "connectDevice(...)");
        MatrixCursor cursor = new MatrixCursor(new String[]{"result", "exception_message"});
        try {
            if(uri.getQueryParameter(KEY_DEVICE_MAC_ADDRESS)!=null
                    && uri.getQueryParameter(KEY_USE_SECURE_CONNECTION)!=null){
                // Get the device MAC address
                // Get the BluetoothDevice object
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(uri.getQueryParameter(KEY_DEVICE_MAC_ADDRESS));
                Log.v(TAG, "KEY_USE_SECURE_CONNECTION: "+uri.getQueryParameter(KEY_USE_SECURE_CONNECTION));
                // Attempt to connect to the device
                mChatService.connect(device, Boolean.valueOf(uri.getQueryParameter(KEY_USE_SECURE_CONNECTION)));
                cursor.addRow(new Object[]{String.valueOf(Boolean.TRUE), null});
            }else{
                throw new Exception("No parameters found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            cursor.addRow(new Object[]{String.valueOf(Boolean.FALSE), e.getMessage()});
        }
        return cursor;
    }
}
