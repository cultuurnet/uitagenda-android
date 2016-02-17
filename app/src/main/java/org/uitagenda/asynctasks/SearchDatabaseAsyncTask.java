package org.uitagenda.asynctasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import org.uitagenda.contentproviders.SearchContentProvider;

/**
 * Created by Inneke on 21/08/2015.
 */
public class SearchDatabaseAsyncTask extends AsyncTask<Void, Void, Void>
{
    private Context context;
    private ContentValues contentValues;

    public SearchDatabaseAsyncTask(Context context, ContentValues contentValues)
    {
        this.context = context;
        this.contentValues = contentValues;
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        if(this.contentValues != null)
            this.context.getContentResolver().insert(SearchContentProvider.CONTENT_URI, this.contentValues);

        return null;
    }
}
