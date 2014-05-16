package org.uitagenda.utils;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by jarno on 16/01/14.
 */
public class UiTagenda extends Application {

    private static Typeface fontFaceRegular;
    private static Typeface fontFaceBold;

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        fontFaceRegular = Typeface.createFromAsset(getApplicationContext().getAssets(), "PTN57F.ttf");
        fontFaceBold = Typeface.createFromAsset(getApplicationContext().getAssets(), "PTN77F.ttf");
        context = getApplicationContext();
    }

    public static void setFontRegular(Integer textViewId, View view) {
        TextView tv = (TextView) view.findViewById(textViewId);
        tv.setTypeface(UiTagenda.fontFaceRegular);
    }


    public static void trackGoogleAnalytics(Context context, String screen) {
        Tracker tracker = EasyTracker.getInstance(context);

        tracker.set(Fields.SCREEN_NAME, screen);

        tracker.send(MapBuilder
                .createAppView()
                .build()
        );
    }

    public static void setFontRegularDirect(TextView tv) {
        tv.setTypeface(UiTagenda.fontFaceRegular);
    }

    public static void setFontRegularDirectOnButton(Button button) {
        button.setTypeface(UiTagenda.fontFaceRegular);
    }

    public static void setFontBold(Integer textViewId, View view) {
        TextView tv = (TextView) view.findViewById(textViewId);
        tv.setTypeface(fontFaceBold);
    }

    public static void setFontBoldDirect(TextView tv) {
        tv.setTypeface(UiTagenda.fontFaceBold);
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static SortedMap<String, String> getEventTypes(JSONArray arrayWithTypes) {
        SortedMap<String, String> eventTypes = new TreeMap<String, String>();

        try {
            for (int i = 0; i < arrayWithTypes.length(); i++) {
                if (arrayWithTypes.getJSONObject(i).has("term")) {
                    JSONArray termArray = arrayWithTypes.getJSONObject(i).getJSONArray("term");

                    for (int j = 0; j < termArray.length(); j++) {
                        String label = termArray.getJSONObject(j).getString("labelnl");
                        String id = termArray.getJSONObject(j).getString("id");
                        eventTypes.put(label, id);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Iterator iter = eventTypes.keySet().iterator();

        return eventTypes;
    }

    public static LinkedHashMap<String, String> getFlandersRegion(JSONArray arrayWithRegions) {
        LinkedHashMap<String, String> flandersRegion = new LinkedHashMap<String, String>();

        try {
            int termLength1 = arrayWithRegions.length();
            for (int i = 0; i < termLength1; i++) {
                int termLength2 = arrayWithRegions.getJSONObject(i).getJSONArray("term").length();
                for (int j = 0; j < termLength2; j++) {
                    int termLength3 = arrayWithRegions.getJSONObject(i).getJSONArray("term").getJSONObject(j).getJSONArray("term").length();
                    for (int k = 0; k < termLength3; k++) {
                        int termLength4 = arrayWithRegions.getJSONObject(i).getJSONArray("term").getJSONObject(j).getJSONArray("term").getJSONObject(k).getJSONArray("term").length();
                        for (int l = 0; l < termLength4; l++) {
                            String label = arrayWithRegions.getJSONObject(i).getJSONArray("term").getJSONObject(j).getJSONArray("term").getJSONObject(k).getJSONArray("term")
                                    .getJSONObject(l).getString("label");
                            flandersRegion.put(label, label.substring(0, 4));
                        }
                    }
                }
            }
        } catch (JSONException e) {

        }

        Iterator iter = flandersRegion.values().iterator();

        return flandersRegion;
    }

    public static String readFromFile(String fileName) {

        String ret = "";

        try {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(fileName)));
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((receiveString = bufferedReader.readLine()) != null) {
                stringBuilder.append(receiveString);
            }

            ret = stringBuilder.toString();

        } catch (FileNotFoundException e) {
            Log.e("", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("", "Can not read file: " + e.toString());
        }
        return ret;
    }
}
