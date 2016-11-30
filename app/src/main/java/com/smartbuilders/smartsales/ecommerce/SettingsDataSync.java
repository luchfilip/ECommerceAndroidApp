package com.smartbuilders.smartsales.ecommerce;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.syncadapter.model.AccountGeneral;
import com.smartbuilders.synchronizer.ids.utils.ApplicationUtilities;

/**
 * Created by AlbertoSarco on 29/11/2016.
 */
public class SettingsDataSync extends AppCompatPreferenceActivity {

    private static User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_data_sync);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mCurrentUser = Utils.getCurrentUser(getApplicationContext());

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.
        bindPreferenceSummaryToValue(findPreference("sync_periodicity"));
        bindPreferenceSummaryToValue(findPreference("server_address"));

        getPreferenceManager().findPreference("server_address").setSummary(mCurrentUser.getServerAddress());
        ((EditTextPreference) getPreferenceManager().findPreference("server_address")).setText(mCurrentUser.getServerAddress());

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
                            Utils.getSyncPeriodicityFromPreferences(getApplicationContext()));
                }

                int index = ((ListPreference) preference).findIndexOfValue(newValue.toString());
                // Set the summary to reflect the new value.
                preference.setSummary(index >= 0 ? ((ListPreference) preference).getEntries()[index] : null);
                return true;
            }
        });


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
    }

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
                sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                        PreferenceManager
                                .getDefaultSharedPreferences(preference.getContext())
                                .getBoolean(preference.getKey(), false));
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
