package com.example.genesis_amd.app.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final boolean ENABLE_LOG_VERBOSE = true;

    private static void log_verbose(String message)
    {
        if (ENABLE_LOG_VERBOSE) Log.v(LOG_TAG, message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            log_verbose("action_settings selected");
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_view_location_on_map)
        {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            String locationKey = getString(R.string.pref_location_key);
            String locationDefault = getString(R.string.pref_location_default);
            String location = sp.getString(locationKey, locationDefault);

            log_verbose("action_view_location_on_map selected");
            String mapUri = "geo:0,0?q=" + location;
            Intent viewLocationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUri));
            if (null != viewLocationIntent.resolveActivity(getPackageManager()))
                startActivity(viewLocationIntent);

        }

        return super.onOptionsItemSelected(item);
    }
}
