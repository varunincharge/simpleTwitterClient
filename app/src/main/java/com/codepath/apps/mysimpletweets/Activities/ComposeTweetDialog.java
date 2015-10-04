package com.codepath.apps.mysimpletweets.Activities;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.Models.Tweet;
import com.codepath.apps.mysimpletweets.Models.User;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.Utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by vjobanputra on 10/2/15.
 */
public class ComposeTweetDialog extends DialogFragment {

    Button btnTweet;
    Button btnCancel;
    TextView tvUsername;
    TextView tvFullName;
    TextView tvCharCount;
    EditText etNewTweet;
    ImageView ivProfilePic;
    TwitterClient client;

    public interface ComposeTweetDialogListener {
        void onFinishComposeTweetDialog(Tweet t);
    }

    public ComposeTweetDialog() {
        // Empty
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public static ComposeTweetDialog newInstance(String title) {
        ComposeTweetDialog frag = new ComposeTweetDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_compose, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String title = getArguments().getString("title", "Tweet");
        getDialog().setTitle(title);
        setupViews(view);
        setupListeners();
        client = TwitterApplication.getRestClient();
    }

    private void setupViews(View view) {
        btnTweet = (Button)view.findViewById(R.id.btnTweet);
        btnCancel = (Button)view.findViewById(R.id.btnCancel);
        etNewTweet = (EditText)view.findViewById(R.id.etNewTweet);
        tvCharCount = (TextView)view.findViewById(R.id.tvCharCount);
        tvCharCount.setText("0");
        ivProfilePic = (ImageView)view.findViewById(R.id.ivProfilePic);
        tvFullName = (TextView) view.findViewById(R.id.tvFullName);
        tvUsername = (TextView)view.findViewById(R.id.tvScreenName);
        User authenticatedUser = TwitterApplication.getAuthenticatedUser();
        if (authenticatedUser != null) {
            tvFullName.setText(authenticatedUser.getName());
            String uname = "<B>" + "@" + authenticatedUser.getScreenName() + " </B>";
            tvUsername.setText(Html.fromHtml(uname));
            Picasso.with(getContext()).load(authenticatedUser.getProfileImageUrl()).into(ivProfilePic);
        }
    }

    private void setupListeners() {
        ////////////////////////////////////////////////////////
        // Text change listener to display the character count
        ///////////////////////////////////////////////////////
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
        ////////////////////////////////////////////////////////
        // On Click listener for Button "TWEET"
        ////////////////////////////////////////////////////////
        btnTweet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String text = etNewTweet.getText().toString();
                if (text == null || text.isEmpty()) {
                    Toast.makeText(getContext(), "Empty tweet", Toast.LENGTH_SHORT).show();
                    return;
                }
                client.postTweet(null, text, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Tweet t = Tweet.fromJson(response);
                        ComposeTweetDialogListener listener = (ComposeTweetDialogListener) getActivity();
                        listener.onFinishComposeTweetDialog(t);
                        dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("Varun", "Tweet failed: " + errorResponse.toString());
                        Toast.makeText(getContext(), "Tweet Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        ////////////////////////////////////////////////////////
        // On Click listener for Button "CANCEL"
        ////////////////////////////////////////////////////////
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ComposeTweetDialogListener listener = (ComposeTweetDialogListener) getActivity();
                listener.onFinishComposeTweetDialog(null);
                dismiss();
            }
        });
    }

}
