package org.uitagenda.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Inneke on 21/08/2015.
 */
public class SearchTable
{
    public static final String TABLE_NAME = "search_queries";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_COMPLETE_QUERY = "complete_query";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_TERM = "term";
    public static final String COLUMN_SORT = "search_sort";
    public static final String COLUMN_CURRENT = "current";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_ZIP_CODE = "zip_code";
    public static final String COLUMN_DATE = "search_date";
    public static final String COLUMN_KIDS = "kids";
    public static final String COLUMN_FREE = "free";
    public static final String COLUMN_NO_COURSES = "no_courses";

    private static final String CREATE_TABLE = "create table IF NOT EXISTS " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_COMPLETE_QUERY + " text, "
            + COLUMN_CATEGORY + " text, "
            + COLUMN_TERM + " text, "
            + COLUMN_SORT + " text, "
            + COLUMN_CURRENT + " text, "
            + COLUMN_DISTANCE + " text, "
            + COLUMN_ZIP_CODE + " text, "
            + COLUMN_DATE + " text, "
            + COLUMN_KIDS + " text, "
            + COLUMN_FREE + " text, "
            + COLUMN_NO_COURSES + " text"
            + ");";

    public static void onCreate(SQLiteDatabase database)
    {
        database.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {

        while (oldVersion < newVersion)
        {

            switch (oldVersion)
            {
                case 1:
                    database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
                    onCreate(database);
                    break;
                case 2:
                    break;
                default:
                    database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
                    onCreate(database);
                    break;
            }
            oldVersion++;
        }
    }
}
