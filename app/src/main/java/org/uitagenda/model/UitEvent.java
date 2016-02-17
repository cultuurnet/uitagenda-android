package org.uitagenda.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import org.uitagenda.R;
import org.uitagenda.database.EventTable;
import org.uitagenda.utils.DateUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Inneke on 12/08/2015.
 */
public class UitEvent implements Serializable
{
    private String location;
    private String cdbid;
    private String organiser;
    private boolean forChildren;
    private long availableTo;
    private String contactInfo;
    private String reservationInfo;
    private String firstCategory;
    private String categories;
    private String city;
    private String contactHouseNr;
    private String contactStreet;
    private String contactZipcode;
    private double latitude;
    private double longitude;
    private String title;
    private String calendarSummary;
    private String imageURL;
    private String longDescription;
    private String shortDescription;
    private String priceDescription;
    private String priceValue;
    private String performers;
    private String mediaInfo;
    private String dateFrom;
    private String dateTo;
    private boolean isPermanent;
    private String timestamp;

    public UitEvent(EventInner event)
    {
        if (event != null)
        {
            this.location = event.getLocation() != null ? event.getLocation().trim() : null;
            this.cdbid = event.getCdbid();
            this.organiser = event.getOrganizer();
            this.forChildren = event.getAgeFrom() != null && event.getAgeFrom() <= 11;
            this.availableTo = event.getAvailableTo();
            List<String> contactList = event.getContactList();
            this.contactInfo = contactList != null ? TextUtils.join("\n\n", contactList) : null;
            List<String> reservationList = event.getReservationList();
            this.reservationInfo = reservationList != null ? TextUtils.join("\n\n", reservationList) : null;
            List<String> categories = event.getCategories();
            this.firstCategory = categories != null && categories.size() > 0 ? categories.get(0) : null;
            this.categories = categories != null ? TextUtils.join(", ", categories) : null;

            Address address = event.getAddress();
            if (address != null)
            {
                this.city = address.getCity();
                this.contactHouseNr = address.getNumber();
                this.contactStreet = address.getStreet();
                this.contactZipcode = address.getZipCode();
                this.latitude = address.getLatitude();
                this.longitude = address.getLongitude();
            }

            EventDetail detail = event.getEventDetail();
            if (detail != null)
            {
                this.title = detail.getTitle() != null ? detail.getTitle().trim() : null;
                this.calendarSummary = detail.getCalendarSummary() != null ? detail.getCalendarSummary().trim() : null;
                this.imageURL = detail.getPhoto();
                this.longDescription = detail.getLongDescription();
                this.shortDescription = detail.getShortDescription();
                this.priceDescription = detail.getPriceDescription();
                this.priceValue = String.valueOf(detail.getPriceValue());
                List<String> performers = detail.getPerformers();
                this.performers = performers != null ? TextUtils.join("\n", performers) : null;
                List<String> mediaInfo = detail.getMediaInfos();
                this.mediaInfo = mediaInfo != null ? TextUtils.join("\n\n", mediaInfo) : null;
            }

            UitCalendar calendar = event.getCalendar();
            if (calendar != null)
            {
                if (calendar.getPeriod() != null)
                {
                    this.dateFrom = calendar.getPeriod().getDateFrom();
                    this.dateTo = calendar.getPeriod().getDateTo();
                } else if (calendar.isPermanent())
                {
                    this.isPermanent = true;
                } else if (calendar.getTimestamp() != null)
                {
                    StringBuilder sbTimestamp = new StringBuilder();
                    for (Timestamp t : calendar.getTimestamp())
                    {
                        if (t != null)
                        {
                            if (sbTimestamp.length() > 0)
                                sbTimestamp.append(";");
                            String date = t.getDate() != null ? t.getDate() : "";
                            String start = t.getTimestart() != null ? t.getTimestart() : "";
                            sbTimestamp.append(date).append(",").append(start);
                        }
                    }
                    this.timestamp = sbTimestamp.toString();
                }
            }
        }
    }

    public UitEvent(String location, String cdbid, String organiser, boolean forChildren,
                    long availableTo, String contactInfo, String reservationInfo, String firstCategory,
                    String categories, String city, String contactHouseNr, String contactStreet, String contactZipcode,
                    double latitude, double longitude, String title, String calendarSummary, String imageURL,
                    String longDescription, String shortDescription, String priceDescription, String priceValue,
                    String performers, String mediaInfo, String dateFrom, String dateTo, boolean isPermanent, String timestamp)
    {
        this.location = location;
        this.cdbid = cdbid;
        this.organiser = organiser;
        this.forChildren = forChildren;
        this.availableTo = availableTo;
        this.contactInfo = contactInfo;
        this.reservationInfo = reservationInfo;
        this.firstCategory = firstCategory;
        this.categories = categories;
        this.city = city;
        this.contactHouseNr = contactHouseNr;
        this.contactStreet = contactStreet;
        this.contactZipcode = contactZipcode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.calendarSummary = calendarSummary;
        this.imageURL = imageURL;
        this.longDescription = longDescription;
        this.shortDescription = shortDescription;
        this.priceDescription = priceDescription;
        this.priceValue = priceValue;
        this.performers = performers;
        this.mediaInfo = mediaInfo;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.isPermanent = isPermanent;
        this.timestamp = timestamp;
    }

    public String getLocation()
    {
        return location;
    }

    public String getCdbid()
    {
        return cdbid;
    }

    public String getOrganiser()
    {
        return organiser;
    }

    public boolean isForChildren()
    {
        return forChildren;
    }

    public long getAvailableTo()
    {
        return availableTo;
    }

    public String getContactInfo()
    {
        return contactInfo;
    }

    public String getReservationInfo()
    {
        return reservationInfo;
    }

    public String getCategories()
    {
        return categories;
    }

    public String getFirstCategory()
    {
        return firstCategory;
    }

    public String getCity()
    {
        return city;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public String getTitle()
    {
        return title;
    }

    public String getCalendarSummary()
    {
        return calendarSummary;
    }

    public String getImageURL()
    {
        return imageURL;
    }

    public String getLongDescription()
    {
        return longDescription;
    }

    public String getShortDescription()
    {
        if (this.shortDescription != null && this.shortDescription.equals("NB"))
            return null;
        return this.shortDescription;
    }

    public String getDateFrom()
    {
        return dateFrom;
    }

    public String getDateTo()
    {
        return dateTo;
    }

    public boolean isPermanent()
    {
        return isPermanent;
    }

    public List<FormattedTimestamp> getTimestamps(Context context)
    {
        List<FormattedTimestamp> formattedTimestamps = new ArrayList<>();
        if (this.timestamp != null)
        {
            String[] dates = this.timestamp.split(";");

            for (String date : dates)
            {
                String[] dateParts = date.split(",");
                if (dateParts.length > 1 && !TextUtils.isEmpty(dateParts[0]) && !TextUtils.isEmpty(dateParts[1]) && DateUtils.isNotBeforeToday(dateParts[0], dateParts[1]))
                {
                    String day = DateUtils.formatLong(DateUtils.millisToCalendar(dateParts[0]));
                    String time = DateUtils.formatTime(DateUtils.millisToCalendar(dateParts[1]));
                    formattedTimestamps.add(new FormattedTimestamp(context.getString(R.string.djubble_dialog_at, day, time), dateParts[0], dateParts[1]));
                }
            }
        }
        return formattedTimestamps;
    }

    public String getPriceDescription()
    {
        return priceDescription;
    }

    public String getPriceValue()
    {
        return priceValue;
    }

    public String getPerformers()
    {
        return performers;
    }

    public String getMediaInfo()
    {
        return mediaInfo;
    }

    public String getFullAddress()
    {
        StringBuilder sb = new StringBuilder();

        if (!TextUtils.isEmpty(this.contactStreet))
            sb.append(this.contactStreet);
        if (!TextUtils.isEmpty(this.contactHouseNr))
        {
            if (sb.length() > 0)
                sb.append(" ");
            sb.append(this.contactHouseNr);
        }
        if (sb.length() > 0)
            sb.append(", ");
        if (!TextUtils.isEmpty(this.contactZipcode))
            sb.append(this.contactZipcode);
        if (!TextUtils.isEmpty(this.city))
        {
            if (sb.length() > 0)
                sb.append(" ");
            sb.append(this.city);
        }
        return sb.toString();
    }

    public String getFullLocation()
    {
        StringBuilder sb = new StringBuilder();

        if (!TextUtils.isEmpty(this.location))
            sb.append(this.location);
        if (!TextUtils.isEmpty(this.city))
        {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(this.city);
        }
        return sb.toString();
    }

    public ContentValues getContentValues()
    {
        ContentValues cv = new ContentValues();

        cv.put(EventTable.COLUMN_LOCATION, this.location);
        cv.put(EventTable.COLUMN_CDBID, this.cdbid);
        cv.put(EventTable.COLUMN_ORGANISER, this.organiser);
        cv.put(EventTable.COLUMN_FOR_CHILDREN, this.forChildren ? 1 : 0);
        cv.put(EventTable.COLUMN_AVAILABLE_TO, this.availableTo);
        cv.put(EventTable.COLUMN_CONTACT_INFO, this.contactInfo);
        cv.put(EventTable.COLUMN_RESERVATION_INFO, this.reservationInfo);
        cv.put(EventTable.COLUMN_FIRST_CATEGORY, this.firstCategory);
        cv.put(EventTable.COLUMN_CATEGORIES, this.categories);
        cv.put(EventTable.COLUMN_CITY, this.city);
        cv.put(EventTable.COLUMN_HOUSE_NUMBER, this.contactHouseNr);
        cv.put(EventTable.COLUMN_STREET, this.contactStreet);
        cv.put(EventTable.COLUMN_ZIP_CODE, this.contactZipcode);
        cv.put(EventTable.COLUMN_LATITUDE, this.latitude);
        cv.put(EventTable.COLUMN_LONGITUDE, this.longitude);
        cv.put(EventTable.COLUMN_TITLE, this.title);
        cv.put(EventTable.COLUMN_CALENDAR_SUMMARY, this.calendarSummary);
        cv.put(EventTable.COLUMN_IMAGE_URL, this.imageURL);
        cv.put(EventTable.COLUMN_LONG_DESCRIPTION, this.longDescription);
        cv.put(EventTable.COLUMN_SHORT_DESCRIPTION, this.shortDescription);
        cv.put(EventTable.COLUMN_PRICE_DESCRIPTION, this.priceDescription);
        cv.put(EventTable.COLUMN_PRICE_VALUE, this.priceValue);
        cv.put(EventTable.COLUMN_PERFORMERS, this.performers);
        cv.put(EventTable.COLUMN_MEDIA_INFO, this.mediaInfo);
        cv.put(EventTable.COLUMN_DATEFROM, this.dateFrom);
        cv.put(EventTable.COLUMN_DATETO, this.dateTo);
        cv.put(EventTable.COLUMN_ISPERMANENT, this.isPermanent);
        cv.put(EventTable.COLUMN_TIMESTAMP, this.timestamp);

        return cv;
    }

    public static UitEvent constructFromCursor(Cursor cursor)
    {
        String location = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_LOCATION));
        String cdbid = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_CDBID));
        String organiser = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_ORGANISER));
        boolean forChildren = cursor.getInt(cursor.getColumnIndex(EventTable.COLUMN_FOR_CHILDREN)) == 1;
        long availableTo = cursor.getLong(cursor.getColumnIndex(EventTable.COLUMN_AVAILABLE_TO));
        String contactInfo = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_CONTACT_INFO));
        String reservationInfo = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_RESERVATION_INFO));
        String firstCategory = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_FIRST_CATEGORY));
        String categories = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_CATEGORIES));
        String city = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_CITY));
        String contactHouseNr = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_HOUSE_NUMBER));
        String contactStreet = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_STREET));
        String contactZipcode = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_ZIP_CODE));
        double latitude = cursor.getDouble(cursor.getColumnIndex(EventTable.COLUMN_LATITUDE));
        double longitude = cursor.getDouble(cursor.getColumnIndex(EventTable.COLUMN_LONGITUDE));
        String title = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_TITLE));
        String calendarSummary = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_CALENDAR_SUMMARY));
        String imageURL = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_IMAGE_URL));
        String longDescription = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_LONG_DESCRIPTION));
        String shortDescription = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_SHORT_DESCRIPTION));
        String priceDescription = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_PRICE_DESCRIPTION));
        String priceValue = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_PRICE_VALUE));
        String performers = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_PERFORMERS));
        String mediaInfo = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_MEDIA_INFO));
        String dateFrom = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_DATEFROM));
        String dateTo = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_DATETO));
        boolean isPermanent = cursor.getInt(cursor.getColumnIndex(EventTable.COLUMN_ISPERMANENT)) == 1;
        String timestamp = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_TIMESTAMP));

        return new UitEvent(location, cdbid, organiser, forChildren, availableTo, contactInfo, reservationInfo, firstCategory, categories, city, contactHouseNr, contactStreet, contactZipcode, latitude, longitude, title, calendarSummary, imageURL, longDescription, shortDescription, priceDescription, priceValue, performers, mediaInfo, dateFrom, dateTo, isPermanent, timestamp);
    }
}