package oscar.riksdagskollen.Activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(RiksdagskollenApp.getInstance().getThemeManager().getCurrentTheme(true));
        setContentView(R.layout.activity_preferences);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyPreferenceFragment()).commit();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Inställningar");

    }

    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.main_preferences);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RiksdagskollenApp.getInstance());
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onDetach() {
            super.onDetach();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RiksdagskollenApp.getInstance());
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (s.equals("update_freq") || s.equals("only_wifi")) {
                RiksdagskollenApp.getInstance().scheduleAndCheckAlertsJob();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) finish();

        return super.onOptionsItemSelected(item);
    }
}
