package com.example.nitish.tweets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by nitish on 25/06/16.
 */
public class TwitterHttpService implements HttpService {

    private static final String API_URL = "https://api.twitter.com/1.1/search/tweets.json?q=cleartax%20-%40cleartax_in%20-%23cleartax&count=100";

    private static final String OAUTH_TOKEN = "AAAAAAAAAAAAAAAAAAAAAHvquwAAAAAAGYxYEdPzdKk%2BsLjWkP9npvRLafE%3D7mrJAAJ6xy1mMqA68uUSayPwLoo7aEgYFy6Dp0YTEI3PS7fLWj";

    @Override
    public List<Tweet> getData() throws IOException {

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .build();
        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + OAUTH_TOKEN)
                .build();

        Response response = client.newCall(request).execute();
        String responseStr = response.body().string();
        TwitterPayload payload = new Gson().fromJson(responseStr, TwitterPayload.class );
        return payload.getStatuses();
    }
}
