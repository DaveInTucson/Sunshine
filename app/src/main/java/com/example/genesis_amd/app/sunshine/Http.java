package com.example.genesis_amd.app.sunshine;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by GENESIS-AMD on 3/14/2015.
 */
public class Http
{
    private static final String LOG_TAG = Http.class.getSimpleName();
    private static final boolean ENABLE_VERBOSE_LOGGING = false;

    private static void log_verbose(String message)
    {
        if (ENABLE_VERBOSE_LOGGING) Log.v(LOG_TAG, message);
    }

    private static HttpURLConnection sendGetRequest(URL url) throws IOException
    {
        log_verbose("opening connection");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        log_verbose("sending GET request...");
        connection.connect();
        log_verbose("returning connection");
        return connection;

    }

    private static String readRequestResponse(HttpURLConnection connection) throws IOException
    {
        BufferedReader reader = null;

        try
        {
            InputStream inputStream = connection.getInputStream();
            if (null == inputStream) return null;

            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null)
            {
                buffer.append(line);
            }

            return buffer.toString();
        } finally
        {
            if (null != reader)
                reader.close();
        }

    }

    public static String readDataFromUrl(URL url) throws IOException
    {
        log_verbose("opening Url");
        HttpURLConnection connection = null;
        try
        {
            connection = sendGetRequest(url);
            log_verbose("reading response");
            return readRequestResponse(connection);
        } finally
        {
            if (null != connection)
            {
                log_verbose("disconnecting");
                connection.disconnect();
            }
        }
    }

    public static String readDataFromUrl(String urlString) throws IOException
    {
        return readDataFromUrl(new URL(urlString));
    }
}
