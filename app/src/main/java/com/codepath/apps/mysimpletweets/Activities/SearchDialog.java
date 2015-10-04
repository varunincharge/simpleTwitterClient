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
public class SearchDialog extends DialogFragment {

    Button btnSearch;
    EditText etSearch;

    public interface SearchDialogListener {
        void onFinishSearchDialog(String s);
    }

    public SearchDialog() {
        // Empty
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public static SearchDialog newInstance(String title) {
        SearchDialog frag = new SearchDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_search, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String title = getArguments().getString("title", "Tweet");
        getDialog().setTitle(title);
        setupViews(view);
        setupListeners();
    }

    private void setupViews(View view) {
        btnSearch = (Button)view.findViewById(R.id.btnSearch);
        etSearch = (EditText)view.findViewById(R.id.etSearch);
    }

    private void setupListeners() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String text = etSearch.getText().toString();
                if (text == null || text.isEmpty()) {
                    Toast.makeText(getContext(), "Empty search", Toast.LENGTH_SHORT).show();
                    return;
                }
                SearchDialogListener listener = (SearchDialogListener) getActivity();
                listener.onFinishSearchDialog(text);
                dismiss();
            }
        });
    }

}
