package com.codepath.apps.mysimpletweets.Utils;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.FlickrApi;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "WpPkBUQVfEJ61DRd1q1APDXqC";       // Change this
	public static final String REST_CONSUMER_SECRET = "XjGy8dqhSXEXaEqdzhthMoHpLHxk3i5dDoEy44Wc9QzGOPgxJS"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://varunstwitterapp"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

    public void getUserInfo(AsyncHttpResponseHandler handler) {
        String apiUrl = "https://api.twitter.com/1.1/account/verify_credentials.json";
        getClient().get(apiUrl, handler);
    }

	public void getHomeTimeline(String since_id, String max_id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.add("count", "25");
        String paramSinceId = since_id != null ? since_id : "1";
        params.add("since_id", paramSinceId);
		if (max_id != null) {
            params.add("max_id", max_id);
        }
        getClient().get(apiUrl, params, handler);
    }

    public void postTweet(String replyToId, String text, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.add("status", text);
        if (replyToId != null) {
            params.add("in_reply_to_status_id", replyToId);
        }
        getClient().post(apiUrl, params, handler);
    }

    public void getSearch(String text, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("search/tweets.json");
        RequestParams params = new RequestParams();
        params.add("count", "25");
        params.add("q", text);
        getClient().get(apiUrl, params, handler);

    }

}