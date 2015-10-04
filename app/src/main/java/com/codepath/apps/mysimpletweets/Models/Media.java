package com.codepath.apps.mysimpletweets.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by vjobanputra on 10/3/15.
 */
public class Media implements Serializable {

    private String mediaUrl;

    public Media(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public static Media fromJson(JSONArray medias) {
        Media media = null;
        try {
            if (medias != null) {
                for (int i = 0; i < medias.length(); i++) {
                    JSONObject mediaJson = (JSONObject) medias.get(i);
                    String mediaType = mediaJson.getString("type");
                    if (mediaType.equals("photo")) {
                        String url = mediaJson.getString("media_url");
                        media = new Media(url);
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return media;
    }


}
