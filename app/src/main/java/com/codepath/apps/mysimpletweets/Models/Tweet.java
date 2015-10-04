package com.codepath.apps.mysimpletweets.Models;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


/**
 * Created by vjobanputra on 9/30/15.
 */

@Table(name = "TweetsNew")
public class Tweet extends Model implements Serializable {

    @Column(name = "tweet_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long tweetId;

    @Column(name = "user", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private User user;

    @Column(name = "body")
    private String body;

    @Column(name = "createdAt")
    private String createdAt;

    @Column(name = "favouriteCount")
    private int favouriteCount;

    @Column(name = "retweetCount")
    private int retweetCount;

    // Not available in offline-mode.
    private Media media;

    public Tweet(){
        super();
    }

    public Tweet(long id, User user, String body, String createdAt, int favouriteCount, int retweetCount) {
        super();
        this.tweetId = id;
        this.user = user;
        this.body = body;
        this.createdAt = createdAt;
        this.favouriteCount = favouriteCount;
        this.retweetCount = retweetCount;
        this.media = null;
    }

    public String getBody() {
        return body;
    }

    public long getTweetId() {
        return tweetId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public int getFavouriteCount() {
        return favouriteCount;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public Media getMedia() {return media; }

    public static Tweet fromJson (JSONObject json) {
        Tweet tweet = new Tweet();

        try {
            tweet.body = json.getString("text");
            tweet.user = User.fromJson(json.getJSONObject("user"));
            tweet.tweetId = json.getLong("id");
            tweet.createdAt = getRelativeTimeAgo(json.getString("created_at"));
            tweet.retweetCount = json.getInt("retweet_count");
            tweet.favouriteCount = json.getInt("favorite_count");
            JSONObject entities = json.getJSONObject("entities");
            if (entities != null) {
                JSONArray mediaJson = entities.getJSONArray("media");
                tweet.media = Media.fromJson(mediaJson);
            }
        } catch (JSONException e) {
                e.printStackTrace();
        }

        tweet.save();

        return tweet;

    }

    public static ArrayList<Tweet> fromJsonArray (JSONArray json) {
        ArrayList<Tweet> tweets = new ArrayList();

        for (int i = 0; i < json.length(); i++) {
            try {
                tweets.add(i, fromJson(json.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        return tweets;
    }

    private static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        String h1 = relativeDate.replaceAll("hours", "h");
        String h2 = h1.replaceAll("hour", "h");
        String m1 = h2.replaceAll("minutes", "m");
        String m2 = m1.replaceAll("minute", "m");
        String s1 = m2.replaceAll("seconds", "s");
        String s2 = s1.replaceAll("second", "s");
        String f = s2.replaceAll("ago", "");

        return f; //relativeDate;
    }
}
