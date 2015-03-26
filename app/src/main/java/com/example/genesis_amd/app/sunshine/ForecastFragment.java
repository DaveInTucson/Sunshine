package com.example.genesis_amd.app.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.genesis_amd.app.sunshine.Http;
import com.example.genesis_amd.app.sunshine.OpenWeatherMapManager;
import com.example.genesis_amd.app.sunshine.DetailActivity;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Sunshine ForecastFragment
 */
public class ForecastFragment extends Fragment
{

    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
    private final String BUNDLE_FORECAST_KEY = "bundle.forecast";

    public ForecastFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_general, false);
    }

    private ArrayAdapter<String> makeForecastAdapter()
    {
        ArrayList<String> forecasts = new ArrayList<String>();
        return new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                forecasts);
    }

    private ListView getForecastListView(View parentView)
    {
        return (ListView)parentView.findViewById(R.id.listview_forecast);
    }

    private void initializeForecastListView(ListView forecastListView, Bundle savedInstanceState)
    {
        ArrayAdapter<String> forecastAdapter = makeForecastAdapter();
        m_forecastAdapter = forecastAdapter;

        forecastListView.setAdapter(forecastAdapter);
        forecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent raiseDetailView = new Intent(getActivity(), DetailActivity.class);
                raiseDetailView.putExtra("Forecast", m_forecastAdapter.getItem(position));
                startActivity(raiseDetailView);
            }
        });

    }
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView forecastListView = getForecastListView(rootView);
        initializeForecastListView(forecastListView, savedInstanceState);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        updateForecastDisplay();
    }

    private void updateForecastDisplay()
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String locationKey = getString(R.string.pref_location_key);
        String locationDefault = getString(R.string.pref_location_default);
        String location = sp.getString(locationKey, locationDefault);

        String tempUnitKey = getString(R.string.pref_temp_units_key);
        String tempUnitDefault = getString(R.string.pref_temp_units_centigrade);
        String tempUnits = sp.getString(tempUnitKey, tempUnitDefault);
        new FetchWeatherTask(m_forecastAdapter).execute(location, tempUnits);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh)
        {
            Log.d(LOG_TAG, "action_refresh selected");
            updateForecastDisplay();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    ArrayAdapter<String> m_forecastAdapter = null;

    /* =============================================================================== */

    static class FetchWeatherTask extends AsyncTask<String, Void, String[]>
    {
        private static final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
        private static final boolean ENABLE_LOG_VERBOSE = false;

        public FetchWeatherTask(ArrayAdapter<String> forecastAdapter)
        { m_forecastAdapter = forecastAdapter; }

        private void log_verbose(String message)
        {
            if (ENABLE_LOG_VERBOSE) Log.v(LOG_TAG, message);
        }

        @Override
        protected String[] doInBackground(String... parms)
        {
            if (null == parms || parms.length < 2)
                return null;

            String location = parms[0];
            String tempUnits = parms[1];
            if (null == tempUnits || !tempUnits.equals("F")) tempUnits = "C";
            try
            {
                final int numDays = 7;
                String getForecastURL = OpenWeatherMapManager.makeGetForecastUrl(location, numDays);
                log_verbose("Fetching forecast JSON");
                String forecastJSON = Http.readDataFromUrl(getForecastURL);

                if (null == forecastJSON) return null;

                log_verbose("have response, parsing");
                try
                {
                    String[] forecasts = OpenWeatherMapManager.getWeatherDataFromJson(forecastJSON, tempUnits);
                    log_verbose("JSON parse succeeded");
                    return forecasts;
                }
                catch (Exception e)
                {
                    Log.e(LOG_TAG, "Failed to parse JSON: ", e);
                }
            }
            catch (IOException e)
            {
                Log.e(LOG_TAG, "exception in doInBackground: ", e);
            }

            log_verbose("reached return null");
            return null;
        }

        @Override
        protected void onPostExecute(String[] forecasts)
        {
            //super.onPostExecute(strings);
            m_forecastAdapter.clear();
            if (forecasts.length > 0)
                m_forecastAdapter.addAll(forecasts);
        }

        ArrayAdapter<String> m_forecastAdapter = null;
    }
}
