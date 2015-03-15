package com.example.genesis_amd.app.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.genesis_amd.app.sunshine.Http;
import com.example.genesis_amd.app.sunshine.OpenWeatherMapManager;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Sunshine ForecastFragment
 */
public class ForecastFragment extends Fragment
{

    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    public ForecastFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ArrayList<String> forecasts = new ArrayList<String>();
        forecasts.add("Today - Sunny - 88/63");
        forecasts.add("Tomorrow - Foggy - 70/46");
        forecasts.add("Weds - Cloudy - 72/63");
        forecasts.add("Thurs - Rainy - 61/51");
        forecasts.add("Fri - Foggy - 70/46");
        forecasts.add("Sat - Sunny - 76/68");
        ArrayAdapter<String> forecastAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                forecasts);
        ListView forecastLV = (ListView)rootView.findViewById(R.id.listview_forecast);
        forecastLV.setAdapter(forecastAdapter);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh)
        {
            Log.d(LOG_TAG, "action_refresh selected");
            new FetchWeatherTask().execute("Tucson");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static class FetchWeatherTask extends AsyncTask<String, Void, String[]>
    {
        private static final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
        private static final boolean ENABLE_LOG_VERBOSE = false;

        void log_verbose(String message)
        {
            if (ENABLE_LOG_VERBOSE) Log.v(LOG_TAG, message);
        }

        @Override
        protected String[] doInBackground(String... locations)
        {
            if (null == locations || locations.length == 0)
                return null;

            try
            {
                final int numDays = 7;
                String location = locations[0];
                String getForecastURL = OpenWeatherMapManager.makeGetForecastUrl(location, numDays);
                log_verbose("Fetching forecast JSON");
                String forecastJSON = Http.readDataFromUrl(getForecastURL);

                if (null == forecastJSON) return null;

                log_verbose("have response, parsing");
                try
                {
                    String[] forecasts = OpenWeatherMapManager.getWeatherDataFromJson(forecastJSON, numDays);
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
    }
}
