package org.uitagenda.application;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import org.uitagenda.BuildConfig;
import org.uitagenda.R;

/**
 * Created by Inneke on 24/08/2015.
 */
public class UitAgendaApplication extends Application
{
    private Tracker tracker;

    public synchronized Tracker getTracker()
    {
        if (this.tracker == null)
        {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            this.tracker = analytics.newTracker(R.xml.app_tracker);
            this.tracker.setAppVersion(BuildConfig.VERSION_NAME);
        }
        return this.tracker;
    }
}
