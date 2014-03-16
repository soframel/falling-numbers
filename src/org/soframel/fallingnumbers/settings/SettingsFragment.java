package org.soframel.fallingnumbers.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import org.soframel.fallingnumbers.MainActivity;
import org.soframel.fallingnumbers.R;

import java.util.Locale;

/**
 * User: sophie
 * Date: 16/3/14
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String pref=sharedPreferences.getString(key, null);
        if(MainActivity.LOCALE_PREF_KEY.equals(key) && pref!=null){
            //reload activity
            if(this.getActivity()!=null){
                Intent i = this.getActivity().getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( this.getActivity().getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        }
    }
}
