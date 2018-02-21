package com.example.clang.carfixtracker;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

/**
 * Created by clang on 8/02/2018.
 */

public class JSONParser {

    private static final String URL = "https://script.google.com/macros/s/AKfycbwdNq0UqTQihusnLenGXx3xhJ7_ycRO5JZgQcl7XkPO4P0BnXw/exec";

    private static Response response;

    public static JSONArray getDataFromWeb(){
        try{
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(URL).build();
            response = client.newCall(request).execute();
            Log.d("RESPONSE", String.valueOf(response.body().equals(null)));
            try {
                JSONArray json = new JSONArray(response.body().string());
                return json;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
