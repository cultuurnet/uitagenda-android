package org.uitagenda.contentproviders;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import org.uitagenda.database.DatabaseHelper;
import org.uitagenda.database.EventTable;

/**
 * Created by Inneke on 18/08/2015.
 */
public class EventContentProvider extends ContentProvider
{
    private SQLiteDatabase database;

    private static final String PROVIDER_NAME = "org.uitagenda.contentproviders.EventContentProvider";

    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/");

    private static final int EVENTS = 1;

    private static final UriMatcher URI_MATCHER;

    static
    {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(PROVIDER_NAME, null, EVENTS);
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
            case EVENTS:
                return "vnd.android.cursor.dir/" + PROVIDER_NAME;
        }

        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(EventTable.TABLE_NAME);

        switch (URI_MATCHER.match(uri))
        {
            case EVENTS:
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
            case EVENTS:
                rowID = this.database.replace(EventTable.TABLE_NAME, null, values);
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
            case EVENTS:
                rowsDeleted = this.database.delete(EventTable.TABLE_NAME, selection, selectionArgs);
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
            case EVENTS:
                rowsUpdated = this.database.update(EventTable.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        switch (URI_MATCHER.match(uri))
        {
            case EVENTS:
                int insertedRows = 0;
                DatabaseUtils.InsertHelper inserter = new DatabaseUtils.InsertHelper(this.database, EventTable.TABLE_NAME);

                this.database.beginTransaction();
                try
                {
                    if(values != null)
                    {
                        for (ContentValues cv : values)
                        {
                            if(cv != null)
                            {
                                inserter.prepareForInsert();
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_LOCATION), cv.getAsString(EventTable.COLUMN_LOCATION));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_CDBID), cv.getAsString(EventTable.COLUMN_CDBID));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_ORGANISER), cv.getAsString(EventTable.COLUMN_ORGANISER));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_FOR_CHILDREN), cv.getAsInteger(EventTable.COLUMN_FOR_CHILDREN));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_AVAILABLE_TO), cv.getAsLong(EventTable.COLUMN_AVAILABLE_TO));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_CONTACT_INFO), cv.getAsString(EventTable.COLUMN_CONTACT_INFO));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_RESERVATION_INFO), cv.getAsString(EventTable.COLUMN_RESERVATION_INFO));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_FIRST_CATEGORY), cv.getAsString(EventTable.COLUMN_FIRST_CATEGORY));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_CATEGORIES), cv.getAsString(EventTable.COLUMN_CATEGORIES));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_CITY), cv.getAsString(EventTable.COLUMN_CITY));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_HOUSE_NUMBER), cv.getAsString(EventTable.COLUMN_HOUSE_NUMBER));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_STREET), cv.getAsString(EventTable.COLUMN_STREET));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_ZIP_CODE), cv.getAsString(EventTable.COLUMN_ZIP_CODE));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_LATITUDE), cv.getAsDouble(EventTable.COLUMN_LATITUDE));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_LONGITUDE), cv.getAsDouble(EventTable.COLUMN_LONGITUDE));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_TITLE), cv.getAsString(EventTable.COLUMN_TITLE));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_CALENDAR_SUMMARY), cv.getAsString(EventTable.COLUMN_CALENDAR_SUMMARY));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_IMAGE_URL), cv.getAsString(EventTable.COLUMN_IMAGE_URL));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_LONG_DESCRIPTION), cv.getAsString(EventTable.COLUMN_LONG_DESCRIPTION));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_SHORT_DESCRIPTION), cv.getAsString(EventTable.COLUMN_SHORT_DESCRIPTION));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_PRICE_DESCRIPTION), cv.getAsString(EventTable.COLUMN_PRICE_DESCRIPTION));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_PRICE_VALUE), cv.getAsString(EventTable.COLUMN_PRICE_VALUE));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_PERFORMERS), cv.getAsString(EventTable.COLUMN_PERFORMERS));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_MEDIA_INFO), cv.getAsString(EventTable.COLUMN_MEDIA_INFO));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_DATEFROM), cv.getAsString(EventTable.COLUMN_DATEFROM));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_DATETO), cv.getAsString(EventTable.COLUMN_DATETO));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_ISPERMANENT), cv.getAsBoolean(EventTable.COLUMN_ISPERMANENT));
                                inserter.bind(inserter.getColumnIndex(EventTable.COLUMN_TIMESTAMP), cv.getAsString(EventTable.COLUMN_TIMESTAMP));

                                long rowId = inserter.execute();
                                if (rowId != -1)
                                    insertedRows++;
                            }
                        }
                    }
                    this.database.setTransactionSuccessful();
                }
                catch (Exception e)
                {
                    insertedRows = 0;
                }
                finally
                {
                    this.database.endTransaction();
                    inserter.close();
                }

                if (insertedRows > 0)
                {
                    this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
                }

                return insertedRows;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}
