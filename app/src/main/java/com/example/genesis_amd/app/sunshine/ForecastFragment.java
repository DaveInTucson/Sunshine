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
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Sunshine ForecastFragment
 */
public class ForecastFragment extends Fragment
{

    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    public ForecastFragment() {
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

    class FetchWeatherTask extends AsyncTask<String, Void, String>
    {
        private final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
        private final String QUERY_PARAM  = "q";
        private final String FORMAT_PARAM = "mode";
        private final String UNITS_PARAM  = "units";
        private final String DAYS_PARAM   = "cnt";

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        private URL makeOpenWeatherURL(String location) throws IOException
        {
            Uri.Builder builder = Uri.parse(FORECAST_BASE_URL).buildUpon();
            builder.appendQueryParameter(QUERY_PARAM, location);
            builder.appendQueryParameter(FORMAT_PARAM, "json");
            builder.appendQueryParameter(UNITS_PARAM , "metric");
            builder.appendQueryParameter(DAYS_PARAM  , "7");
            Uri uri = builder.build();
            Log.v(LOG_TAG, "Built URI " + uri.toString());
            return new URL(uri.toString());
        }

        @Override
        protected String doInBackground(String... locations)
        {
            if (null == locations || locations.length == 0)
                return null;

            try
            {
                String location = locations[0];
                Log.v(LOG_TAG, "Fetching forecast JSON");
                String forecastJSON = Http.readDataFromUrl(makeOpenWeatherURL(location));

                Log.d(LOG_TAG, "have response=" + forecastJSON);
                return forecastJSON;
            }
            catch (IOException e)
            {
                Log.e(LOG_TAG, "exception in doInBackground: ", e);
            }

            Log.v(LOG_TAG, "reached return null");
            return null;
        }
    }
}
