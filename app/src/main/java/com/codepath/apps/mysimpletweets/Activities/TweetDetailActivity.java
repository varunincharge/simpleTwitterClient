package com.codepath.apps.mysimpletweets.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.Models.Tweet;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.Utils.LinkifiedTextView;
import com.codepath.apps.mysimpletweets.Utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by vjobanputra on 10/2/15.
 */
public class TweetDetailActivity extends ActionBarActivity {

    Tweet tweet;
    ImageView ivProfilePic;
    TextView tvUsername;
    TextView tvTweetBody;
    TextView tvTimestamp;
    EditText etNewTweet;
    TextView tvCharCount;
    TwitterClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        tweet = (Tweet) getIntent().getSerializableExtra("tweet");
        client = TwitterApplication.getRestClient();
        setupViews();
        setupListeners();
    }

    private void setupViews() {
        ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvTweetBody = (TextView) findViewById(R.id.tvTweetBody);
        tvTimestamp = (TextView) findViewById(R.id.tvTimestamp);
        tvCharCount = (TextView) findViewById(R.id.tvCharCount);
        etNewTweet = (EditText) findViewById(R.id.etNewTweet);

        TextView tvFavorites = (TextView) findViewById(R.id.tvFavorites);
        TextView tvRetweets = (TextView) findViewById(R.id.tvRetweets);
        TextView tvReply = (TextView) findViewById(R.id.tvReply);
        tvFavorites.setText("\u2605 " + tweet.getFavouriteCount());
        tvRetweets.setText("↺ " + tweet.getRetweetCount()); //u+27f2
        tvReply.setText("←");

        tvUsername.setText(Html.fromHtml(tweet.getUser().getDisplayName()));
        String uname = "<B>" + "@" + tweet.getUser().getScreenName() + " </B>";
        etNewTweet.setText(Html.fromHtml(uname));
        int length = etNewTweet.getText().length();
        etNewTweet.setSelection(length);
        tvCharCount.setText(Integer.toString(length));

        tvTweetBody.setText(tweet.getBody());
        tvTweetBody.setMovementMethod(null);

        tvTimestamp.setText(tweet.getCreatedAt());
        ivProfilePic.setImageResource(android.R.color.transparent);
        Picasso.with(this).load(tweet.getUser().getProfileImageUrl()).into(ivProfilePic);

        if (tweet.getMedia() != null) {
            /************************
             * Create Image View
             ************************/
            ImageView ivMedia = new ImageView(this);
            ivMedia.setId(R.id.ivMedia);
            Picasso.with(this).load(tweet.getMedia().getMediaUrl()).into(ivMedia);
            /************************
             * Set Layout Params
             * ************************/
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_LEFT, R.id.tvTweetBody);
            params.addRule(RelativeLayout.BELOW, R.id.tvTweetBody);
            params.topMargin = 10;
            /************************
             * Add View
             * ************************/
            RelativeLayout topLayout = (RelativeLayout)findViewById(R.id.topLayout);
            topLayout.addView(ivMedia, params);
            /************************
             * Adjust retweet bar
             * ************************/
            LinearLayout ll = (LinearLayout)findViewById(R.id.llBottom);
            RelativeLayout.LayoutParams llParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            llParams.addRule(RelativeLayout.BELOW, 0);
            llParams.addRule(RelativeLayout.BELOW, R.id.ivMedia);
            llParams.addRule(RelativeLayout.ALIGN_LEFT, R.id.tvTweetBody);
            llParams.addRule(RelativeLayout.ALIGN_RIGHT, R.id.tvTweetBody);
            ll.setLayoutParams(llParams);

//            RelativeLayout.LayoutParams etParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT);
//            etParams.addRule(RelativeLayout.BELOW, R.id.ivMedia);
//            etNewTweet.setLayoutParams(etParams);
        }
    }

    private void setupListeners() {
        etNewTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharCount.setText(Integer.toString(etNewTweet.getText().length()));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Fires right after the text has changed
                //tvDisplay.setText(s.toString());
            }
        });
    }

    public void onReply(View view) {
        String text = etNewTweet.getText().toString();
        String replyToId = tvUsername.getText().toString();

        client.postTweet(replyToId, text, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet t = Tweet.fromJson(response);
                Intent intent = new Intent();
                intent.putExtra("tweet", t);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getApplicationContext(), "Tweet Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

}