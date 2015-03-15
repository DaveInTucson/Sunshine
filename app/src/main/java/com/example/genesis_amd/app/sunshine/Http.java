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

    private static HttpURLConnection sendGetRequest(URL url) throws IOException
    {
        Log.v(LOG_TAG, "opening connection");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");

        Log.v(LOG_TAG, "sending GET request...");
        connection.connect();
        Log.v(LOG_TAG, "returning connection");
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
                Log.v(LOG_TAG, "read line " + line);
            }

            if (buffer.length() == 0)
            {
                Log.v(LOG_TAG, "read buffer is empty!");
                return null;
            }

            return buffer.toString();
        }
        finally
        {
            if (null != reader)
                reader.close();
        }

    }

    public static String readDataFromUrl(URL url) throws IOException
    {
        Log.v(LOG_TAG, "opening Url");
        HttpURLConnection connection = null;
        try
        {
            connection = sendGetRequest(url);
            Log.v(LOG_TAG, "reading response");
            return readRequestResponse(connection);
        }
        finally
        {
            if (null != connection)
            {
                Log.v(LOG_TAG, "disconnecting");
                connection.disconnect();

            }
        }
    }
}
