package com.example.genesis_amd.app.sunshine;

import android.net.Uri;
import android.text.format.Time;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;

/**
 * Created by GENESIS-AMD on 3/15/2015.
 *
 * Some methods for dealing with OpenWeatherMap.org
 */
public class OpenWeatherMapManager
{
    private static final String LOG_TAG = OpenWeatherMapManager.class.getSimpleName();

    // These constants are used to build the url in makeGetForecastUrl
    private static final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
    private static final String QUERY_PARAM  = "q";
    private static final String FORMAT_PARAM = "mode";
    private static final String UNITS_PARAM  = "units";
    private static final String DAYS_PARAM   = "cnt";

    // These are the names of the JSON objects that need to be extracted.
    private static final String OWM_LIST = "list";
    private static final String OWM_WEATHER = "weather";
    private static final String OWM_TEMPERATURE = "temp";
    private static final String OWM_MAX = "max";
    private static final String OWM_MIN = "min";
    private static final String OWM_DESCRIPTION = "main";

    private static final boolean ENABLE_VERBOSE_LOGGING = false;

    private static void log_verbose(String message)
    {
        if (ENABLE_VERBOSE_LOGGING) Log.v(LOG_TAG, message);
    }

    /** Create the URL to geth the forecast JSON given a location and number of days */
    public static String makeGetForecastUrl(String location, int numDays)
    {
        Uri.Builder builder = Uri.parse(FORECAST_BASE_URL).buildUpon();
        builder.appendQueryParameter(QUERY_PARAM, location);
        builder.appendQueryParameter(FORMAT_PARAM, "json");
        builder.appendQueryParameter(UNITS_PARAM , "metric");
        builder.appendQueryParameter(DAYS_PARAM  , String.valueOf(numDays));
        Uri uri = builder.build();
        log_verbose("Built URI " + uri.toString());
        return uri.toString();
    }

    /* The date/time conversion code is going to be moved outside the asynctask later,
     * so for convenience we're breaking it out into its own method now.
     */
    private static String getReadableDateString(long time)
    {
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private static String formatHighLows(double high, double low)
    {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    /** Get the current Julian Day (http://en.wikipedia.org/wiki/Julian_day) */
    private static int getCurrentJulianDay()
    {
        Time dayTime = new Time();
        dayTime.setToNow();

        // we start at the day returned by local time. Otherwise this is a mess.
        return Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    public static String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException
    {
        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        // OWM returns daily forecasts based upon the local time of the city that is being
        // asked for, which means that we need to know the GMT offset to translate this data
        // properly.

        // Since this data is also sent in-order and the first day is always the
        // current day, we're going to take advantage of that to get a nice
        // normalized UTC date for all of our weather.
        int julianStartDay = getCurrentJulianDay();

        // now we work exclusively in UTC
        Time dayTime = new Time();

        String[] resultStrs = new String[numDays];
        for(int i = 0; i < weatherArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

            // Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".
            long dateTime;
            // Cheating to convert this to UTC time, which is what we want anyhow
            dateTime = dayTime.setJulianDay(julianStartDay+i);
            day = getReadableDateString(dateTime);

            // description is in a child array called "weather", which is 1 element long.
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            // Temperatures are in a child object called "temp".  Try not to name variables
            // "temp" when working with temperature.  It confuses everybody.
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            highAndLow = formatHighLows(high, low);
            resultStrs[i] = day + " - " + description + " - " + highAndLow;
        }

        for (String s : resultStrs)
        {
            log_verbose("Forecast entry: " + s);
        }
        return resultStrs;
    }
}
