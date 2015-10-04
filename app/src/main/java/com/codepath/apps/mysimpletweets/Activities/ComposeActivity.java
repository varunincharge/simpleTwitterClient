package com.codepath.apps.mysimpletweets.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.Utils.TwitterClient;
import com.codepath.apps.mysimpletweets.Models.Tweet;
import com.codepath.apps.mysimpletweets.Models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by vjobanputra on 10/1/15.
 */

public class ComposeActivity extends AppCompatActivity {

    ImageView ivProfilePic;
    TextView tvUsername;
    TextView tvFullName;
    TextView tvCharCount;
    TwitterClient client;
    EditText etNewTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        setupViews();
        setupListeners();
        client = TwitterApplication.getRestClient();
    }

    private void setupViews() {
        etNewTweet = (EditText)findViewById(R.id.etNewTweet);
        tvCharCount = (TextView)findViewById(R.id.tvCharCount);
        tvCharCount.setText("0");
        ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);
        tvFullName = (TextView) findViewById(R.id.tvFullName);
        tvUsername = (TextView) findViewById(R.id.tvScreenName);
        User authenticatedUser = TwitterApplication.getAuthenticatedUser();
        if (authenticatedUser != null) {
            tvFullName.setText(authenticatedUser.getName());
            tvUsername.setText(authenticatedUser.getScreenName());
            Picasso.with(this).load(authenticatedUser.getProfileImageUrl()).into(ivProfilePic);
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

            }
        });
    }


    public void onCancel(View view) {
        Intent intent = new Intent();
        intent.putExtra("tweet", (Tweet)null);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public void onTweet(View view) {
        String text = etNewTweet.getText().toString();
        if (text == null || text.isEmpty()) {
            Toast.makeText(this, "Empty tweet", Toast.LENGTH_SHORT).show();
            return;
        }
        client.postTweet(null, text, new JsonHttpResponseHandler() {
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
                Log.d("Varun", "Tweet failed: " + errorResponse.toString());
                Toast.makeText(getApplicationContext(), "Tweet Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
