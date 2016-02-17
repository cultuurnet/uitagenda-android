package org.uitagenda.utils;

import org.uitagenda.model.FormattedTimestamp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Inneke De Clippel on 17/02/2016.
 */
public class DateUtils
{
    private static final SimpleDateFormat SDF_DATE_SHORT = new SimpleDateFormat("d MMMM", Locale.getDefault());
    private static final SimpleDateFormat SDF_DATE_LONG = new SimpleDateFormat("cc d MMM yyyy", Locale.getDefault());
    private static final SimpleDateFormat SDF_TIME = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat SDF_DJUBBLE = new SimpleDateFormat("yyyyMMdHHmm", Locale.getDefault());

    public static boolean isNotBeforeToday(String dateMillisAsString, String timeMillisAsString)
    {
        try
        {
            return DateUtils.isNotBeforeToday(DateUtils.combineMillis(dateMillisAsString, timeMillisAsString));
        } catch (NumberFormatException e)
        {
            return false;
        }
    }

    public static boolean isNotBeforeToday(Calendar calendar)
    {
        return System.currentTimeMillis() <= calendar.getTimeInMillis();
    }

    public static Calendar millisToCalendar(String millisAsString)
    {
        Calendar c = Calendar.getInstance();
        try
        {
            long millis = Long.parseLong(millisAsString);
            c.setTimeInMillis(millis);
        } catch (NumberFormatException e)
        {

        }
        return c;
    }

    public static String formatShort(Calendar calendar)
    {
        return SDF_DATE_SHORT.format(calendar.getTime());
    }

    public static String formatLong(Calendar calendar)
    {
        return SDF_DATE_LONG.format(calendar.getTime());
    }

    public static String formatTime(Calendar calendar)
    {
        return SDF_TIME.format(calendar.getTime());
    }

    public static String formatDjubbleDate(Calendar calendarDate, Calendar calendarTime)
    {
        Calendar c = Calendar.getInstance();
        c.set(calendarDate.get(Calendar.YEAR),
                calendarDate.get(Calendar.MONTH),
                calendarDate.get(Calendar.DAY_OF_MONTH),
                calendarTime.get(Calendar.HOUR_OF_DAY),
                calendarTime.get(Calendar.MINUTE));
        return SDF_DJUBBLE.format(c.getTime());
    }

    public static String formatDjubbleDate(FormattedTimestamp timestamp)
    {
        return SDF_DJUBBLE.format(DateUtils.combineMillis(timestamp.getDate(), timestamp.getHour()).getTime());
    }

    public static Calendar combineMillis(String dateMillisAsString, String timeMillisAsString)
    {
        try
        {
            long dateMillis = Long.parseLong(dateMillisAsString);
            long timeMillis = Long.parseLong(timeMillisAsString);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dateMillis);
            Calendar calendarTime = Calendar.getInstance();
            calendarTime.setTimeInMillis(timeMillis);

            calendar.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));

            return calendar;
        } catch (NumberFormatException e)
        {
            return Calendar.getInstance();
        }
    }
}
