/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.app;

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
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class ForecastFragment extends Fragment {

    private PostsAdapter mForecastAdapter;
    private ProgressBar spinner;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute("");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create some dummy data for the ListView.  Here's a sample weekly forecast

        ArrayList<Post> arrayOfPosts = new ArrayList<Post>();

        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        mForecastAdapter =
                new PostsAdapter(getActivity(),arrayOfPosts);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        spinner = (ProgressBar)rootView.findViewById(R.id.progressBar);
        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        weatherTask.execute("");

    }

    public class FetchWeatherTask extends AsyncTask<String, Void, Vector<Post>> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();


        /**
         * Prepare the weather high/lows for presentation.
         */
        private String cncat(int a, int b,String c, String d) {



            return a+"/"+b+"/"+c+"/"+d;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private Vector<Post> getWeatherDataFromJson(String forecastJsonStr)
                throws JSONException {

            final String ID = "id";
            final String USER_ID = "userId";
            final String TITLE = "title";
            final String BODY = "body";

forecastJsonStr="{\"jarr\" : "+forecastJsonStr+"}";
            Log.v(LOG_TAG, "Forecast entry: " + forecastJsonStr);
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray jsonArray = forecastJson.getJSONArray("jarr");
//            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
               Vector<Post> posts = new Vector<Post>(jsonArray.length());
                for(int i = 0; i < jsonArray.length(); i++) {

                int id;
                int userId;
                String title;
                String body;

                JSONObject post = jsonArray.getJSONObject(i);

                id = post.getInt(ID);
                userId = post.getInt(USER_ID);
                title = post.getString(TITLE);
                body = post.getString(BODY);
                posts.add(new Post(userId,id,title,body));

            }



            for (Post s : posts) {
                Log.v(LOG_TAG, "Forecast entry: " + cncat(s.id,s.userId,s.title,s.body));
            }
            return posts;

        }
        @Override
        protected Vector<Post> doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL =
                        "http://jsonplaceholder.typicode.com";


                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendPath("posts")
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Forecast string: " + forecastJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getWeatherDataFromJson(forecastJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
//            super.onPreExecute();

            spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Vector<Post> result) {
            if (result != null) {
                mForecastAdapter.clear();
                for(Post dayForecastStr : result) {
                    mForecastAdapter.add(dayForecastStr);
                }

                spinner.setVisibility(View.GONE);
            }
        }
    }
}
