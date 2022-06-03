package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;

import okhttp3.Headers;

public class MovieTrailerActivity extends YouTubeBaseActivity {
    public static final String TMDB_API_KEY = "9278ce2e152a6743954f29bc32491a77";
    public static final String YOUTUBE_API_KEY = "AIzaSyDYbPdz5Htp1bTPv_Iu7ePZdxXtGR4P_mg";
    public static final String TAG = "MovieTrailerActivity";
    Movie movie;
    String youTubeID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_trailer);

        // Unwrap the movie passed in via intent
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));

        Log.d("MovieTrailerActivity", Integer.toString(movie.getId()));

        // API call to retrieve movie ID
        AsyncHttpClient client = new AsyncHttpClient();
        String VIDEOS_URL = "https://api.themoviedb.org/3/movie/" + Integer.toString(movie.getId()) + "/videos?api_key=" + TMDB_API_KEY;

        client.get(VIDEOS_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    JSONObject firstResult = (JSONObject) results.get(0);
                    youTubeID = (String) firstResult.get("key");
                    Log.d("MovieTrailerActivity", youTubeID);
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });

        // Resolve the player view from the layout
        YouTubePlayerView playerView = (YouTubePlayerView) findViewById(R.id.player);

        // Initialize with API key
        playerView.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener(){
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                // Cue the video
                youTubePlayer.cueVideo(youTubeID);
            }
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                // Log the error
                Log.e("MovieTrailerActivity", "Error initializing YouTube player");
                Log.e("MovieTrailerActivity", youTubeInitializationResult.toString());
            }
        });
    }
}