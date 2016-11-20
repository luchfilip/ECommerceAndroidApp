package com.smartbuilders.smartsales.ecommerce;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.smartbuilders.smartsales.ecommerce.services.LoadProductsOriginalImage;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.syncadapter.model.AccountGeneral;

import com.smartbuilders.synchronizer.ids.utils.ApplicationUtilities;
import com.smartbuilders.smartsales.ecommerce.services.LoadProductsThumbImage;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    private static User mCurrentUser;

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        try {
            // Trigger the listener immediately with the preference's
            // current value.
            if(preference instanceof SwitchPreference){
                boolean defaultValue = false;
                if(preference.getKey().equals("save_images_in_device")){
                    defaultValue = true;
                }
                sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                        PreferenceManager
                                .getDefaultSharedPreferences(preference.getContext())
                                .getBoolean(preference.getKey(), defaultValue));
            }else{
                String defaultValue = "";
                if(preference.getKey().equals("server_address")){
                    defaultValue = mCurrentUser.getServerAddress();
                }
                sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                        PreferenceManager
                                .getDefaultSharedPreferences(preference.getContext())
                                .getString(preference.getKey(), defaultValue));
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.setBackgroundColor(Color.WHITE);
        mCurrentUser = Utils.getCurrentUser(getApplicationContext());
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (BuildConfig.IS_SALES_FORCE_SYSTEM) {
            loadHeadersFromResource(R.xml.pref_headers_sales_force_system, target);
        } else {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName)
                || BluetoothConnectionPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_availabilities_wish_list_ringtone"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);

            setHasOptionsMenu(true);

            if (!TextUtils.isEmpty(BuildConfig.SERVER_ADDRESS)) {
                getPreferenceScreen().removePreference(findPreference("category_sync"));
            }

            try {
                getPreferenceManager().findPreference("server_address").setSummary(mCurrentUser.getServerAddress());
                ((EditTextPreference) getPreferenceManager().findPreference("server_address")).setText(mCurrentUser.getServerAddress());
            } catch (NullPointerException e) {
                //do nothing
            }


            try {
                // Bind the summaries of EditText/List/Dialog/Ringtone preferences
                // to their values. When their values change, their summaries are
                // updated to reflect the new value, per the Android Design
                // guidelines.
                bindPreferenceSummaryToValue(findPreference("sync_periodicity"));
                bindPreferenceSummaryToValue(findPreference("server_address"));
            } catch (NullPointerException e) {
                //do nothing
            }


            try {
                findPreference("sync_periodicity").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        Account account = ApplicationUtilities.getAccountByIdFromAccountManager(preference.getContext(), mCurrentUser.getUserId());
                        ContentResolver.removePeriodicSync(account, BuildConfig.SYNC_ADAPTER_CONTENT_AUTHORITY, new Bundle());
                        if (Long.valueOf(newValue.toString()) <= 0) {
                            //Turn off periodic syncing
                            ContentResolver.setSyncAutomatically(account, BuildConfig.SYNC_ADAPTER_CONTENT_AUTHORITY, false);
                        } else {
                            //Turn on periodic syncing
                            ContentResolver.setIsSyncable(account, BuildConfig.SYNC_ADAPTER_CONTENT_AUTHORITY, 1);
                            ContentResolver.setSyncAutomatically(account, BuildConfig.SYNC_ADAPTER_CONTENT_AUTHORITY, true);

                            ContentResolver.addPeriodicSync(
                                    account,
                                    BuildConfig.SYNC_ADAPTER_CONTENT_AUTHORITY,
                                    Bundle.EMPTY,
                                    Utils.getSyncPeriodicityFromPreferences(getActivity()));
                        }

                        int index = ((ListPreference) preference).findIndexOfValue(newValue.toString());
                        // Set the summary to reflect the new value.
                        preference.setSummary(index >= 0 ? ((ListPreference) preference).getEntries()[index] : null);
                        return true;
                    }
                });
            } catch (NullPointerException e) {
                //do nothing
            }


            try {
                findPreference("server_address").setDefaultValue(mCurrentUser.getServerAddress());
                findPreference("server_address").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        mCurrentUser.setServerAddress(newValue.toString());
                        AccountManager accountManager = AccountManager.get(preference.getContext());
                        Account account = ApplicationUtilities.getAccountByIdFromAccountManager(preference.getContext(), mCurrentUser.getUserId());
                        accountManager.setUserData(account,
                                AccountGeneral.USERDATA_SERVER_ADDRESS,
                                newValue.toString());

                        // Set the summary to reflect the new value.
                        preference.setSummary(newValue.toString());
                        return true;
                    }
                });
            } catch (NullPointerException e) {
                //do nothing
            }

            if (BuildConfig.USE_PRODUCT_IMAGE) {
                bindPreferenceSummaryToValue(findPreference("save_images_in_device"));
                bindPreferenceSummaryToValue(findPreference("sync_thumb_images"));
                bindPreferenceSummaryToValue(findPreference("sync_original_images"));

                findPreference("save_images_in_device").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        final Context context = preference.getContext();
                        if((Boolean) newValue){
                            //findPreference("sync_thumb_images").setSelectable(true);
                            //TODO: setear el valor de sync_thumb_images en FALSE
                        }else{
                            //findPreference("sync_thumb_images").setSelectable(false);
                            context.stopService(new Intent(context, LoadProductsThumbImage.class));
                            new AlertDialog.Builder(context)
                                    .setMessage(R.string.clean_thumb_and_original_dir)
                                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Utils.clearOriginalImagesFolder(context);
                                            Utils.clearThumbImagesFolder(context);
                                        }
                                    })
                                    .setNegativeButton(R.string.no, null)
                                    .setCancelable(false)
                                    .show();
                        }
                        return true;
                    }
                });

                findPreference("sync_thumb_images").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        final Context context = preference.getContext();
                        if((Boolean) newValue){
                            if(!Utils.isServiceRunning(context, LoadProductsThumbImage.class)){
                                context.startService(new Intent(context, LoadProductsThumbImage.class));
                            }
                        }else{
                            context.stopService(new Intent(context, LoadProductsThumbImage.class));
                            new AlertDialog.Builder(context)
                                    .setMessage(R.string.clean_thumb_dir)
                                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Utils.clearThumbImagesFolder(context);
                                        }
                                    })
                                    .setNegativeButton(R.string.no, null)
                                    .setCancelable(false)
                                    .show();
                        }
                        return true;
                    }
                });

                findPreference("sync_original_images").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        final Context context = preference.getContext();
                        if((Boolean) newValue){
                            if(!Utils.isServiceRunning(context, LoadProductsOriginalImage.class)){
                                context.startService(new Intent(context, LoadProductsOriginalImage.class));
                            }
                        }else{
                            context.stopService(new Intent(context, LoadProductsOriginalImage.class));
                            new AlertDialog.Builder(context)
                                    .setMessage(R.string.clean_original_dir)
                                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Utils.clearOriginalImagesFolder(context);
                                        }
                                    })
                                    .setNegativeButton(R.string.no, null)
                                    .setCancelable(false)
                                    .show();
                        }
                        return true;
                    }
                });
            } else {
                findPreference("save_images_in_device").setShouldDisableView(true);
                findPreference("save_images_in_device").setEnabled(false);

                findPreference("sync_thumb_images").setShouldDisableView(true);
                findPreference("sync_thumb_images").setEnabled(false);

                findPreference("sync_original_images").setShouldDisableView(true);
                findPreference("sync_original_images").setEnabled(false);
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class BluetoothConnectionPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_bluetooth_connection);
            setHasOptionsMenu(true);

            try {
                Preference button = getPreferenceManager().findPreference("bluetooth_chat");
                if (button != null) {
                    button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference arg0) {
                            startActivity(new Intent(getActivity(),
                                    BluetoothChatActivity.class));
                            return true;
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

}
