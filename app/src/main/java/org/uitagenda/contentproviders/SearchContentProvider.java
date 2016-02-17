package org.uitagenda.contentproviders;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import org.uitagenda.database.DatabaseHelper;
import org.uitagenda.database.SearchTable;

/**
 * Created by Inneke on 21/08/2015.
 */
public class SearchContentProvider extends ContentProvider
{
    private SQLiteDatabase database;

    private static final String PROVIDER_NAME = "org.uitagenda.contentproviders.SearchContentProvider";

    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/");

    private static final int SEARCH_QUERIES = 1;

    private static final UriMatcher URI_MATCHER;

    static
    {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(PROVIDER_NAME, null, SEARCH_QUERIES);
    }

    @Override
    public boolean onCreate()
    {
        this.database = new DatabaseHelper(this.getContext()).getWritableDatabase();
        return this.database != null;
    }

    @Override
    public String getType(Uri uri)
    {
        switch (URI_MATCHER.match(uri))
        {
            case SEARCH_QUERIES:
                return "vnd.android.cursor.dir/" + PROVIDER_NAME;
        }

        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(SearchTable.TABLE_NAME);

        switch (URI_MATCHER.match(uri))
        {
            case SEARCH_QUERIES:
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Cursor cursor = queryBuilder.query(this.database, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(this.getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        long rowID;

        switch (URI_MATCHER.match(uri))
        {
            case SEARCH_QUERIES:
                rowID = this.database.replace(SearchTable.TABLE_NAME, null, values);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (rowID > 0)
        {
            this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
            return CONTENT_URI;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        int rowsDeleted;

        switch (URI_MATCHER.match(uri))
        {
            case SEARCH_QUERIES:
                rowsDeleted = this.database.delete(SearchTable.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        int rowsUpdated;

        switch (URI_MATCHER.match(uri))
        {
            case SEARCH_QUERIES:
                rowsUpdated = this.database.update(SearchTable.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return rowsUpdated;
    }
}
