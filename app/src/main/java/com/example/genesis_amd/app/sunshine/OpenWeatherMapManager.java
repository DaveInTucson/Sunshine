package com.example.genesis_amd.app.sunshine;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

/**
 * Created by GENESIS-AMD on 3/15/2015.
 */
public class OpenWeatherMapManager
{
    private static final String LOG_TAG = OpenWeatherMapManager.class.getSimpleName();

    private static final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
    private static final String QUERY_PARAM  = "q";
    private static final String FORMAT_PARAM = "mode";
    private static final String UNITS_PARAM  = "units";
    private static final String DAYS_PARAM   = "cnt";

    private static final boolean ENABLE_VERBOSE_LOGGING = false;

    private static void log_verbose(String message)
    {
        if (ENABLE_VERBOSE_LOGGING) Log.v(LOG_TAG, message);
    }

    public static String makeGetForecastUrl(String location)
    {
        Uri.Builder builder = Uri.parse(FORECAST_BASE_URL).buildUpon();
        builder.appendQueryParameter(QUERY_PARAM, location);
        builder.appendQueryParameter(FORMAT_PARAM, "json");
        builder.appendQueryParameter(UNITS_PARAM , "metric");
        builder.appendQueryParameter(DAYS_PARAM  , "7");
        Uri uri = builder.build();
        log_verbose("Built URI " + uri.toString());
        return uri.toString();
    }
}
