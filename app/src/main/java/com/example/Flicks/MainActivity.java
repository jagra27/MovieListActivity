package com.example.Flicks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.Flicks.models.Config;
import com.example.Flicks.models.Movie;
import com.example.movielistactivity.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    //constants for string values
    //url for API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    //the param for the API Key
    public final static String API_KEY_PARAM = "api_key";

    //tag for all logging calls from this activity
    public final static String TAG = "MainActivity";

    //instance fields
    AsyncHttpClient client;

    // the list of currently playing movies
    ArrayList<Movie> movies;
    // track the adapter and recycler view
    RecyclerView rvMovies;
    MovieAdapter adapter;
    Config config;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new AsyncHttpClient();
        // now we have to initialize the list
        movies = new ArrayList<>();
        //initialize the adapter -- movies array cannot be reinitialized after this point
        adapter = new MovieAdapter(movies);
        // image config to track it


        //resolve the recycler view and connect a layout manager and the adapter
        rvMovies = (RecyclerView) findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);

        // now get the configuration on the app creation
        getConfiguration();
        getNowPlaying();
    }
    // get the list of currently playing movies from the API
    // try to use this as an example when adding other features
    private void getNowPlaying(){
        //create the url
        String url = API_BASE_URL + "/movie/now_playing";
        // set the request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));
        // now do a get request expecting a JSON object response which is what the api mostly is
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //load the results array (check api site) into movies list
                try {
                    JSONArray results = response.getJSONArray("results");
                    // because results is an array we have to iterate through and create Movie object so we can add the movies to the movie object
                    for (int i = 0; i <results.length(); i++){
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);
                        // we have to notify the adapter when a new movie has been added
                        adapter.notifyItemInserted(movies.size()-1);
                    }
                    // string format used to replace %s with the length of results
                    Log.i(TAG, String.format("Loaded %s movies", results.length()));
                } catch (JSONException e) {
                    logError("Failed to parse now playing movies", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get data from now playing endpoint", throwable, true);
            }
        });}

    // get the config from the Api

    private void getConfiguration() {
        //create the url
        String url = API_BASE_URL + "/configuration";
        // set the request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key)); // Api key, is always required
        // now do a get request expecting a JSON object response which is what the api mostly is
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //get image base url
                try {
                    config = new Config(response);
                    Log.i(TAG, String.format("Loaded configuration imageBaseUrl %s and posterSize %s",
                            config.getImageBaseUrl(),
                            config.getPosterSize()));
                    // pass config to adapter
                    adapter.setConfig(config);
                     // get the now playing movie list
                    getNowPlaying();
                } catch (JSONException e) {
                    logError("Failed parsing configuration", e, true);
                }
            }


                @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed getting configuration", throwable, true);
            }
        });
    }

    // handle errors, log and alert the user
    private void logError(String message, Throwable error, boolean alertUser){
        //always log the error
        Log.e(TAG, message, error);
        if (alertUser){
            //show a long toast with the error message so the user can really see it
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}


