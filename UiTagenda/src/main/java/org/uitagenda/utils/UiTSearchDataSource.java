package org.uitagenda.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.uitagenda.model.SearchQueryListItem;

import java.util.ArrayList;


public class UiTSearchDataSource {
    private SQLiteDatabase database;
    private UitDatabaseHelper dbHelper;

    Cursor cursor;

    public UiTSearchDataSource(Context context) {
        dbHelper = new UitDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void addSearchUrl(String title, String url, boolean currentLocation) {
        ContentValues values = new ContentValues();
        values.put(UitDatabaseHelper.SEARCH_TITLE, title);
        values.put(UitDatabaseHelper.SEARCH_URL, url);
        values.put(UitDatabaseHelper.SEARCH_CURRENTLOCATION, currentLocation);

        this.open();

        database.insertWithOnConflict(UitDatabaseHelper.SEARCH_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        this.close();
    }

    public ArrayList<SearchQueryListItem> findAllSearchUrls() {
        this.open();
        ArrayList<SearchQueryListItem> searchList = new ArrayList<SearchQueryListItem>();
        this.open();

        Cursor cursor = database.rawQuery("SELECT * FROM SearchQueries", null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            searchList.add(new SearchQueryListItem(cursor.getString(cursor.getColumnIndexOrThrow(UitDatabaseHelper.SEARCH_TITLE)), cursor.getString(cursor.getColumnIndexOrThrow(UitDatabaseHelper.SEARCH_URL)), cursor.getInt(cursor.getColumnIndexOrThrow(UitDatabaseHelper.SEARCH_CURRENTLOCATION)) > 0));
            cursor.moveToNext();
        }
        this.close();

        return searchList;
    }

    public boolean containsTitle(String title) {
        this.open();

        Cursor cursor = database.rawQuery("SELECT Title FROM SearchQueries WHERE title = '" + title + "'", null);

        if (cursor.getCount() == 0) {
            this.close();
            return true;
        }
        this.close();
        return false;
    }

    public void deleteSearchQuery(String title) {
        this.open();

        cursor = database.rawQuery("DELETE FROM SearchQueries WHERE title = '" + title + "'", null);
        cursor.moveToFirst();
        cursor.close();

        this.close();
    }
}