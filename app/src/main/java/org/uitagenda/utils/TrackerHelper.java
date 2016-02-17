package org.uitagenda.utils;

import android.content.Context;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.uitagenda.application.UitAgendaApplication;

/**
 * Created by Inneke on 24/08/2015.
 */
public class TrackerHelper
{
    public static final String SCREEN_ABOUT = "Android: Over";
    public static final String SCREEN_DETAIL = "Android: Detail";
    public static final String SCREEN_FAVORITES = "Android: Favorieten";
    public static final String SCREEN_CURRENT = "Android: Home";
    public static final String SCREEN_SEARCH = "Android: Zoeken";
    public static final String SCREEN_SEARCH_HISTORY = "Android: Zoekopdrachten";
    public static final String SCREEN_SEARCH_RESULTS = "Android: Zoekresultaten";

    public static final String CATEGORY_SEARCH = "Uitgebreid zoeken";

    public static final String ACTION_TERM = "Android: Zoekveld";
    public static final String ACTION_LOCATION = "Android: Waar";
    public static final String ACTION_DISTANCE = "Android: Straal";
    public static final String ACTION_DATE = "Android: Wanneer";
    public static final String ACTION_CATEGORY = "Android: Wat";
    public static final String ACTION_EXTRA = "Android: Extra zoekcriteria";

    public static final String LABEL_KIDS = "Enkel voor kinderen";
    public static final String LABEL_FREE = "Enkel gratis";
    public static final String LABEL_NO_COURSES = "Geen cursussen en workshops";

    public static void trackScreen(Context context, String screenName)
    {
        if(context != null && context.getApplicationContext() != null && context.getApplicationContext() instanceof UitAgendaApplication)
        {
            Tracker tracker = ((UitAgendaApplication) context.getApplicationContext()).getTracker();
            tracker.setScreenName(screenName);
            tracker.send(new HitBuilders.AppViewBuilder().build());
        }
    }

    public static void trackAction(Context context, String category, String action, String label)
    {
        if(context != null && context.getApplicationContext() != null && context.getApplicationContext() instanceof UitAgendaApplication)
        {
            Tracker tracker = ((UitAgendaApplication) context.getApplicationContext()).getTracker();
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .setLabel(label)
                    .build());
        }
    }
}
