package com.smartbuilders.smartsales.ecommerce;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.services.LoadProductsOriginalImage;
import com.smartbuilders.smartsales.ecommerce.services.LoadProductsThumbImage;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;

import java.io.File;

/**
 * Created by AlbertoSarco on 29/11/2016.
 */
public class SettingsImagesManagement extends AppCompatPreferenceActivity {

    private static User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_image_management);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mCurrentUser = Utils.getCurrentUser(getApplicationContext());

        bindPreferenceSummaryToValue(findPreference("save_images_in_device"));
        bindPreferenceSummaryToValue(findPreference("sync_thumb_images"));
        bindPreferenceSummaryToValue(findPreference("save_original_images_in_device"));
        bindPreferenceSummaryToValue(findPreference("sync_original_images"));

        findPreference("save_images_in_device").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final Context context = preference.getContext();
                if(!((Boolean) newValue)){
                    context.stopService(new Intent(context, LoadProductsThumbImage.class));
                    new AlertDialog.Builder(context)
                            .setMessage(R.string.clean_thumb_dir)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Utils.deleteThumbImagesFolder(context)) {
                                        Toast.makeText(context, R.string.folder_removed_successfully, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, R.string.folder_was_not_removed, Toast.LENGTH_SHORT).show();
                                    }
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
                                    if (Utils.deleteThumbImagesFolder(context)) {
                                        Toast.makeText(context, R.string.folder_removed_successfully, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, R.string.folder_was_not_removed, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .setCancelable(false)
                            .show();
                }
                return true;
            }
        });

        findPreference("thumb_images_folder_info").setSummary(
                "Espacio total requerido: " + Parameter.getThumbImagesRequiredDiskSpace(getApplicationContext(), mCurrentUser) + "\n" +
                        "Espacio total utilizado: " + Utils.getFolderSize(new File(Utils.getImagesThumbFolderPath(getApplicationContext()))));

        findPreference("delete_thumb_images_folder").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final Context context = preference.getContext();
                context.stopService(new Intent(context, LoadProductsThumbImage.class));
                new AlertDialog.Builder(context)
                        .setMessage(R.string.clean_thumb_dir)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Utils.deleteThumbImagesFolder(context)) {
                                    Toast.makeText(context, R.string.folder_removed_successfully, Toast.LENGTH_SHORT).show();
                                    findPreference("thumb_images_folder_info").setSummary(
                                            "Espacio total requerido: " + Parameter.getThumbImagesRequiredDiskSpace(getApplicationContext(), mCurrentUser) + "\n" +
                                                    "Espacio total utilizado: " + Utils.getFolderSize(new File(Utils.getImagesThumbFolderPath(getApplicationContext()))));
                                } else {
                                    Toast.makeText(context, R.string.folder_was_not_removed, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .setCancelable(false)
                        .show();
                return true;
            }
        });

        findPreference("save_original_images_in_device").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final Context context = preference.getContext();
                if(!((Boolean) newValue)){
                    context.stopService(new Intent(context, LoadProductsOriginalImage.class));
                    new AlertDialog.Builder(context)
                            .setMessage(R.string.clean_original_dir)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Utils.deleteOriginalImagesFolder(context)) {
                                        Toast.makeText(context, R.string.folder_removed_successfully, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, R.string.folder_was_not_removed, Toast.LENGTH_SHORT).show();
                                    }
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
                                    if (Utils.deleteOriginalImagesFolder(context)) {
                                        Toast.makeText(context, R.string.folder_removed_successfully, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, R.string.folder_was_not_removed, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .setCancelable(false)
                            .show();
                }
                return true;
            }
        });

        findPreference("original_images_folder_info").setSummary(
                "Espacio total requerido: " + Parameter.getOriginalImagesRequiredDiskSpace(getApplicationContext(), mCurrentUser) + "\n" +
                        "Espacio total utilizado: " + Utils.getFolderSize(new File(Utils.getImagesOriginalFolderPath(getApplicationContext()))));

        findPreference("delete_original_images_folder").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final Context context = preference.getContext();
                context.stopService(new Intent(context, LoadProductsOriginalImage.class));
                new AlertDialog.Builder(context)
                        .setMessage(R.string.clean_original_dir)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Utils.deleteOriginalImagesFolder(context)) {
                                    Toast.makeText(context, R.string.folder_removed_successfully, Toast.LENGTH_SHORT).show();
                                    findPreference("original_images_folder_info").setSummary(
                                            "Espacio total requerido: " + Parameter.getOriginalImagesRequiredDiskSpace(getApplicationContext(), mCurrentUser) + "\n" +
                                                    "Espacio total utilizado: " + Utils.getFolderSize(new File(Utils.getImagesOriginalFolderPath(getApplicationContext()))));
                                } else {
                                    Toast.makeText(context, R.string.folder_was_not_removed, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .setCancelable(false)
                        .show();
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
                boolean defaultValue = false;
                if(preference.getKey().equals("save_images_in_device")){
                    defaultValue = true;
                }
                sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                        PreferenceManager
                                .getDefaultSharedPreferences(preference.getContext())
                                .getBoolean(preference.getKey(), defaultValue));
            }else{
                sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                        PreferenceManager
                                .getDefaultSharedPreferences(preference.getContext())
                                .getString(preference.getKey(), ""));
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
