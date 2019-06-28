package com.example.Flicks.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Config {
    // track base url for loading our images and the poster size to use when getting images

    String imageBaseurl;
    String posterSize;
    String backdropSize;

    public Config(JSONObject object) throws JSONException {

            JSONObject images = object.getJSONObject("images");
            // get the image base url which is under the images object
            imageBaseurl = images.getString("secure_base_url");
            //get the poster size
            JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");
            // use the option of poster sizes at index 3 or w342 as a fallback
            posterSize = posterSizeOptions.optString(3, "w342");
            JSONArray backdropSizeOptions = images.getJSONArray("backdrop_sizes");
            backdropSize = backdropSizeOptions.optString(1, "w780");
    }
    //helper method for creating urls

    public String getImageUrl(String size, String path) {
        return String.format("%s%s%s", imageBaseurl, size, path); //concatenate all three
    }

    public String getImageBaseUrl(){
        return imageBaseurl;
    }

    public String getPosterSize() {
        return posterSize;
    }
    public String getBackdropSize(){
        return backdropSize;
    }
}
