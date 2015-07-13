package com.example.genesis_amd.app.sunshine;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;


public class DetailActivity extends ActionBarActivity
{
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private static final boolean ENABLE_LOG_VERBOSE = true;

    private static void log_verbose(String message)
    { if (ENABLE_LOG_VERBOSE) Log.v(LOG_TAG, message); }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            log_verbose("action_settings selected");
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment
    {
        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
        private String mForecastStr;

        private static void log_debug(String message)
        { Log.d(LOG_TAG, message);}

        public DetailFragment()
        {
            setHasOptionsMenu(true);
        }

        private String getForecastFromIntent()
        {
            Intent intent = getActivity().getIntent();
            if (null == intent)
            {
                log_debug("unable to get intent!?");
                return null;
            }

            if (!intent.hasExtra(Intent.EXTRA_INTENT))
            { log_debug("intent doesn't have EXTRA_INTENT!"); return null; }

            return intent.getStringExtra(Intent.EXTRA_INTENT);
        }

        @Override
        public View onCreateView(
                LayoutInflater inflater,
                ViewGroup container,
                Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            mForecastStr = getForecastFromIntent();
            if (null != mForecastStr)
            {
                TextView forecastView = (TextView)rootView.findViewById(R.id.textViewForecast);
                forecastView.setText(mForecastStr);
            }

            return rootView;
        }

        private Intent createShareForecastIntent()
        {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            //shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            log_debug("mForecastStr=" + mForecastStr);
            shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HASHTAG);
            return shareIntent;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
        {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.detailfragment, menu);

            // Retrieve the share menu item
            MenuItem menuItem = menu.findItem(R.id.action_share);

            // Get the provider and hold onto it to set/change the share intent.
            ShareActionProvider mShareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            // Attach an intent to this ShareActioinProvider. You can update this at any time,
            // like when the user selects a new piece of data they might like to share.
            if (null != mShareActionProvider)
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            else
            {
                log_debug("mShareActionProvider is null?!");
            }
        }
    }
}
