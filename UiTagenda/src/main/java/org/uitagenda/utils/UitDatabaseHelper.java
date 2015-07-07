package org.uitagenda.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jarno on 15/01/14.
 */
public class UitDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "UiTagenda.db";
    private static final int DB_VERSION = 1;

    public static final String FAVORITES_TABLE = "Favorites";
    public static final String FAVORITES_ID = "ID";

    public static final String SEARCH_TABLE = "SearchQueries";
    public static final String SEARCH_URL = "Url";
    public static final String SEARCH_TITLE = "Title";
    public static final String SEARCH_CURRENTLOCATION = "Currentlocation";

    public static final String LATEST_LOCATIONS_TABLE = "LatestLocations";
    public static final String LATEST_LOCATIONS_CITY = "City";

    public UitDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + SEARCH_TABLE + " ("
                + SEARCH_TITLE + " TEXT PRIMARY KEY ,"
                + SEARCH_URL + " TEXT ,"
                + SEARCH_CURRENTLOCATION + " BOOLEAN DEFAULT 'FALSE' "
                + ")";

        db.execSQL(sql);

        db.execSQL("CREATE TABLE IF NOT EXISTS " + FAVORITES_TABLE + " (" + FAVORITES_ID + " TEXT PRIMARY KEY )");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + LATEST_LOCATIONS_TABLE + " (" + LATEST_LOCATIONS_CITY + " TEXT PRIMARY KEY )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldDB, int newDB) {
        db.execSQL("DROP TABLE IF EXISTS " + FAVORITES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SEARCH_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LATEST_LOCATIONS_TABLE);
    }
}
