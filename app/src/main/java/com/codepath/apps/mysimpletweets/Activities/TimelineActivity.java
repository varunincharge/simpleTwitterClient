package com.codepath.apps.mysimpletweets.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.Adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.Utils.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.Utils.TwitterClient;
import com.codepath.apps.mysimpletweets.Models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetDialog.ComposeTweetDialogListener, SearchDialog.SearchDialogListener{

    private ListView lvTweets;
    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private SwipeRefreshLayout swipeContainer;
    private final int REQUEST_CODE_COMPOSE = 20;
    private final int REQUEST_CODE_DETAIL = 30;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        getSupportActionBar().setTitle("Home"); // Couldn't get this to work :-(
        setup();
        if (isNetworkAvailable()) {
            populateTimeLine(false, null, null);
        } else {
            Toast.makeText(this, "Network unavailable", Toast.LENGTH_SHORT).show();
            populateTimeLineOffline();
        }
    }

    private void setup() {
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(this, tweets);
        lvTweets.setAdapter(aTweets);
        client = TwitterApplication.getRestClient();

        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                long lastTweetId = aTweets.getItem(totalItemsCount - 1).getTweetId();
                populateTimeLine(false, null, Long.toString(lastTweetId));
                return true;
            }
        });

        lvTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Intent i = new Intent(TimelineActivity.this, TweetDetailActivity.class);
                i.putExtra("tweet", tweets.get(pos));
                startActivityForResult(i, REQUEST_CODE_DETAIL);
            }
        });

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                long firstTweetId = aTweets.getItem(0).getTweetId();
                populateTimeLine(true, Long.toString(firstTweetId), null);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void populateTimeLine(final boolean addFirst, final String since_id, String max_id) {
        client.getHomeTimeline(since_id, max_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList tw = Tweet.fromJsonArray(response);
                if (addFirst) {
                    tweets.addAll(0, tw);
                } else {
                    tweets.addAll(tw);
                }
                aTweets.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                swipeContainer.setRefreshing(false);
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    private void populateTimeLineOffline() {
        List<Tweet> queryResults = new Select().from(Tweet.class).execute();
        aTweets.addAll(queryResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.miCompose) {
            FragmentManager fm = getSupportFragmentManager();
            ComposeTweetDialog composeTweetDialog = ComposeTweetDialog.newInstance("Tweet");
            composeTweetDialog.show(fm, "activity_compose");
        } else if (id == R.id.miSearch) {
            FragmentManager fm = getSupportFragmentManager();
            SearchDialog searchDialog = SearchDialog.newInstance("");
            searchDialog.show(fm, "activity_search");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishComposeTweetDialog(Tweet t) {
        if (t != null) {
            tweets.add(0, t);
            aTweets.notifyDataSetChanged();
        }
    }

    @Override
    public void onFinishSearchDialog(String s) {
        if (s != null) {
            client.getSearch(s, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    JSONArray tweetsArray = null;
                    try {
                        tweetsArray = response.getJSONArray("statuses");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ArrayList tw = Tweet.fromJsonArray(tweetsArray);
                    aTweets.clear();
                    aTweets.addAll(tw);
                    swipeContainer.setRefreshing(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    swipeContainer.setRefreshing(false);
                    Log.d("DEBUG", errorResponse.toString());
                }
            });

        }
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}