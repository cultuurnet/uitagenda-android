package org.uitagenda.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Inneke on 18/08/2015.
 */
public class EventTable
{
    public static final String TABLE_NAME = "events";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_CDBID = "cdbid";
    public static final String COLUMN_ORGANISER = "organiser";
    public static final String COLUMN_FOR_CHILDREN = "for_children";
    public static final String COLUMN_AVAILABLE_TO = "available_to";
    public static final String COLUMN_CONTACT_INFO = "contact_info";
    public static final String COLUMN_RESERVATION_INFO = "reservation_info";
    public static final String COLUMN_FIRST_CATEGORY = "first_category";
    public static final String COLUMN_CATEGORIES = "categories";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_HOUSE_NUMBER = "house_number";
    public static final String COLUMN_STREET = "street";
    public static final String COLUMN_ZIP_CODE = "zip_code";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CALENDAR_SUMMARY = "calendar_summary";
    public static final String COLUMN_IMAGE_URL = "image_url";
    public static final String COLUMN_LONG_DESCRIPTION = "long_description";
    public static final String COLUMN_SHORT_DESCRIPTION = "shortDescription";
    public static final String COLUMN_PRICE_DESCRIPTION = "price_description";
    public static final String COLUMN_PRICE_VALUE = "price_value";
    public static final String COLUMN_PERFORMERS = "performers";
    public static final String COLUMN_MEDIA_INFO = "media_info";
    public static final String COLUMN_DATEFROM = "date_from";
    public static final String COLUMN_DATETO = "date_to";
    public static final String COLUMN_ISPERMANENT = "ispermanent";
    public static final String COLUMN_TIMESTAMP = "timestamp";


    private static final String CREATE_TABLE = "create table IF NOT EXISTS " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_LOCATION + " text, "
            + COLUMN_CDBID + " text, "
            + COLUMN_ORGANISER + " text, "
            + COLUMN_FOR_CHILDREN + " integer, "
            + COLUMN_AVAILABLE_TO + " integer, "
            + COLUMN_CONTACT_INFO + " text, "
            + COLUMN_RESERVATION_INFO + " text, "
            + COLUMN_FIRST_CATEGORY + " text, "
            + COLUMN_CATEGORIES + " text, "
            + COLUMN_CITY + " text, "
            + COLUMN_HOUSE_NUMBER + " text, "
            + COLUMN_STREET + " text, "
            + COLUMN_ZIP_CODE + " text, "
            + COLUMN_LATITUDE + " real, "
            + COLUMN_LONGITUDE + " real, "
            + COLUMN_TITLE + " text, "
            + COLUMN_CALENDAR_SUMMARY + " text, "
            + COLUMN_IMAGE_URL + " text, "
            + COLUMN_LONG_DESCRIPTION + " text, "
            + COLUMN_SHORT_DESCRIPTION + " text, "
            + COLUMN_PRICE_DESCRIPTION + " text, "
            + COLUMN_PRICE_VALUE + " text, "
            + COLUMN_PERFORMERS + " text, "
            + COLUMN_MEDIA_INFO + " text, "
            + COLUMN_DATEFROM + " text, "
            + COLUMN_DATETO + " text, "
            + COLUMN_ISPERMANENT + " integer, "
            + COLUMN_TIMESTAMP + " text"
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
                    database.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_DATEFROM + " TEXT");
                    database.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_DATETO + " TEXT");
                    database.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_ISPERMANENT + " INTEGER");
                    database.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_TIMESTAMP+ " TEXT");
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
