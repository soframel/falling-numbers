package org.soframel.fallingnumbers;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import org.soframel.fallingnumbers.characteristics.Difficulty;
import org.soframel.fallingnumbers.characteristics.GameSettings;
import org.soframel.fallingnumbers.characteristics.Operation;
import org.soframel.fallingnumbers.settings.SettingsActivity;

import java.util.Locale;

public class MainActivity extends Activity {

    public final static String LOCALE_PREF_KEY="language";
    private GameSettings settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.loadLocale();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        settings=new GameSettings();
        settings.setOperation(Operation.Plus);
        settings.setDifficulty(Difficulty.veryEasy);
    }

    public void play(View view){
        Intent intent=new Intent(this, PlayActivity.class);
        Bundle b=new Bundle();
        b.putString(PlayActivity.GAME_CHARACTERISTICS, settings.toString());
        intent.putExtras(b);
        startActivity(intent);
    }

    public void setOperationPlus(View v){
        settings.setOperation(Operation.Plus);
    }
    public void setOperationMinus(View v){
        settings.setOperation(Operation.Minus);
    }

    public void setSpeed0(View v){
        settings.setDifficulty(Difficulty.veryEasy);
    }
    public void setSpeed1(View v){
        settings.setDifficulty(Difficulty.easy);
    }
    public void setSpeed2(View v){
        settings.setDifficulty(Difficulty.medium);
    }
    public void setSpeed3(View v){
        settings.setDifficulty(Difficulty.hard);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                showSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSettings(){
        Intent i = new Intent(this, SettingsActivity.class);
        this.startActivity(i);
    }

    public void loadLocale(){
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String language=prefs.getString(LOCALE_PREF_KEY, null);
        if(language==null){
            //set current language in preferences
            Configuration conf=this.getBaseContext().getResources().getConfiguration();
            Locale locale=conf.locale;
            SharedPreferences.Editor editor=prefs.edit();
            editor.putString(LOCALE_PREF_KEY, locale.getISO3Language());
            editor.commit();
        }
        else{
            //change locale from value in preferences
            this.setLocale(language);
        }
    }

    public void setLocale(String localeString){
        Locale locale=new Locale(localeString);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        this.getBaseContext().getResources().updateConfiguration(config, null);
    }

}
