package org.uitagenda.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by alexf_000 on 1/28/14.
 */
public class UiTLatestLocationsDataSource {

    private SQLiteDatabase database;
    private UitDatabaseHelper dbHelper;

    Cursor cursor;

    public UiTLatestLocationsDataSource(Context context) {
        dbHelper = new UitDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void addLocation(String id) {
        ContentValues values = new ContentValues();
        values.put(UitDatabaseHelper.LATEST_LOCATIONS_CITY, id);
        try {
            this.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(database.rawQuery("SELECT * FROM LatestLocations WHERE City = '" + id + "'", null).getCount() == 0){
if(database.rawQuery("SELECT * FROM LatestLocations", null).getCount() > 4){


     Cursor cursor = database.rawQuery("SELECT * FROM LatestLocations", null);
    cursor.moveToFirst();
    Log.d("LatestLocations", "more than 4 latestLocations" + cursor.getString(cursor.getColumnIndexOrThrow(UitDatabaseHelper.LATEST_LOCATIONS_CITY)));
    database.rawQuery("DELETE FROM LatestLocations WHERE City = '" + cursor.getString(cursor.getColumnIndexOrThrow(UitDatabaseHelper.LATEST_LOCATIONS_CITY)) + "'", null).moveToFirst();
}}else{
       database.rawQuery("DELETE FROM LatestLocations WHERE City = '" + id + "'", null).moveToFirst();
    }
        database.insert(UitDatabaseHelper.LATEST_LOCATIONS_TABLE, null, values);

        this.close();
    }

    public ArrayList<String> findLatestLocations() {
        try {
            this.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ArrayList<String> latestLocations = new ArrayList<String>();

        Cursor cursor = database.rawQuery("SELECT * FROM LatestLocations", null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            latestLocations.add(cursor.getString(cursor.getColumnIndexOrThrow(UitDatabaseHelper.LATEST_LOCATIONS_CITY)));
            cursor.moveToNext();
        }
        this.close();

        return latestLocations;
    }


}
