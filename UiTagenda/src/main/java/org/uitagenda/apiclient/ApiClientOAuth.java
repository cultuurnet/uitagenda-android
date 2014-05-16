package org.uitagenda.apiclient;

import android.content.Context;

import org.json.JSONObject;
import org.uitagenda.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;

/**
 * Created by jarno on 13/01/14.
 */


public class ApiClientOAuth {

    Context context;
    String url;
    int start;

    public ApiClientOAuth(Context context, String url, int start) {
        this.context = context;
        this.url = url;
        this.start = start;
    }

    public JSONObject fetchData() {
        try {
            OAuthConsumer consumer = new DefaultOAuthConsumer(this.context.getString(R.string.consumer_key), this.context.getString(R.string.consumer_secret));

            URL fullURL = new URL(url + "&start=" + start + "");

            HttpURLConnection request = (HttpURLConnection) fullURL.openConnection();

            consumer.sign(request);
            request.setConnectTimeout(15000);
            request.setRequestMethod("GET");
            request.setRequestProperty("Accept", "application/json");
            request.setRequestProperty("Accept-Encoding", "gzip");
            request.connect();

            switch (request.getResponseCode()) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    return new JSONObject(sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}