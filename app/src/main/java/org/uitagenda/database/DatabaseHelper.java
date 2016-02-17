package org.uitagenda.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Inneke on 18/08/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "UiTagenda.db";
    public static final int DATABASE_VERSION = 3;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        EventTable.onCreate(db);
        SearchTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if(oldVersion == 1)
        {
            db.execSQL("DROP TABLE IF EXISTS SearchQueries");
            db.execSQL("DROP TABLE IF EXISTS Favorites");
            db.execSQL("DROP TABLE IF EXISTS LatestLocations");
        }

        EventTable.onUpgrade(db, oldVersion, newVersion);
        SearchTable.onUpgrade(db, oldVersion, newVersion);
    }
}
