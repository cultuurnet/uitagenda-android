package org.uitagenda.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by jarno on 15/01/14.
 */
public class UiTFavoriteDataSource {

    private SQLiteDatabase database;
    private UitDatabaseHelper dbHelper;

    Cursor cursor;

    public UiTFavoriteDataSource(Context context) {
        dbHelper = new UitDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void addFavorite(String id) {
        ContentValues values = new ContentValues();
        values.put(UitDatabaseHelper.FAVORITES_ID, id);

        try {
            this.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        database.insert(UitDatabaseHelper.FAVORITES_TABLE, null, values);
        this.close();
    }

    public ArrayList<String> findAllFavorites() {
        try {
            this.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ArrayList<String> favList = new ArrayList<String>();

        Cursor cursor = database.rawQuery("SELECT * FROM Favorites", null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            favList.add(cursor.getString(cursor.getColumnIndexOrThrow(UitDatabaseHelper.FAVORITES_ID)));
            cursor.moveToNext();
        }
        this.close();

        return favList;
    }

    public void deleteFromFavorites(String id) {
        try {
            this.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        cursor = database.rawQuery("DELETE FROM Favorites WHERE ID = '" + id + "'", null);
        cursor.moveToFirst();
        cursor.close();

        this.close();
    }

    public boolean containsId(String id) {
        try {
            this.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        cursor = database.rawQuery("SELECT * FROM Favorites WHERE ID = '" + id + "'", null);

        if (cursor.getCount() == 1) {
            this.close();
            return true;
        } else {
            this.close();
            return false;
        }
    }
}
