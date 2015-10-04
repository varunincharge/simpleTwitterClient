package com.codepath.apps.mysimpletweets;

import android.content.Context;

import com.codepath.apps.mysimpletweets.Utils.TwitterClient;
import com.codepath.apps.mysimpletweets.Models.User;


public class TwitterApplication extends com.activeandroid.app.Application {
	private static Context context;

	private static User authenticatedUser;

	@Override
	public void onCreate() {
		super.onCreate();
		TwitterApplication.context = this;
	}

	public static TwitterClient getRestClient() {
		return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, TwitterApplication.context);
	}

	public static User getAuthenticatedUser() {
		return authenticatedUser;
	}

	public static void setAuthenticatedUser(User user) {
		authenticatedUser = user;
	}
}