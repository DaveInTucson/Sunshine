package com.example.genesis_amd.app.sunshine;

import android.content.Intent;
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
    }

    private ArrayAdapter<String> makeForecastAdapter()
    {
        ArrayList<String> forecasts = new ArrayList<String>();
        forecasts.add("Today - Sunny - 88/63");
        forecasts.add("Tomorrow - Foggy - 70/46");
        forecasts.add("Weds - Cloudy - 72/63");
        forecasts.add("Thurs - Rainy - 61/51");
        forecasts.add("Fri - Foggy - 70/46");
        forecasts.add("Sat - Sunny - 76/68");
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh)
        {
            Log.d(LOG_TAG, "action_refresh selected");
            new FetchWeatherTask(m_forecastAdapter).execute("Tucson");
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
                    String[] forecasts = OpenWeatherMapManager.getWeatherDataFromJson(forecastJSON);
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
