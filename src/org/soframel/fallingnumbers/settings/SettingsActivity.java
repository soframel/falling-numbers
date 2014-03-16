package org.soframel.fallingnumbers.settings;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

import java.util.Locale;

/**
 * User: sophie
 * Date: 16/3/14
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingsFragment()).commit();
    }


}
