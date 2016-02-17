package org.uitagenda.asynctasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import org.uitagenda.contentproviders.EventContentProvider;
import org.uitagenda.model.UitEvent;

import java.util.List;

/**
 * Created by Inneke on 18/08/2015.
 */
public class EventDatabaseAsyncTask extends AsyncTask<Void, Void, Void>
{
    private Context context;
    private List<UitEvent> events;
    private boolean deleteAll;

    public EventDatabaseAsyncTask(Context context, List<UitEvent> events, boolean deleteAll)
    {
        this.context = context;
        this.events = events;
        this.deleteAll = deleteAll;
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        if(this.deleteAll)
        {
            this.context.getContentResolver().delete(EventContentProvider.CONTENT_URI, null, null);
        }

        if(this.events != null && this.context != null)
        {
            ContentValues[] cv = new ContentValues[this.events.size()];

            for(int i = 0; i < this.events.size(); i++)
            {
                cv[i] = this.events.get(i).getContentValues();
            }

            this.context.getContentResolver().bulkInsert(EventContentProvider.CONTENT_URI, cv);
        }

        return null;
    }
}
