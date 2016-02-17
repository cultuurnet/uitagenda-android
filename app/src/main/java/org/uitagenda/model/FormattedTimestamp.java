package org.uitagenda.model;

/**
 * Created by thijscoorevits on 11/12/15.
 */
public class FormattedTimestamp
{
    private String timeText;
    private String date;
    private String hour;

    public FormattedTimestamp(String timeText, String date, String hour)
    {
        this.timeText = timeText;
        this.date = date;
        this.hour = hour;
    }


    public String getTimeText()
    {
        return timeText;
    }

    public String getDate()
    {
        return date;
    }

    public String getHour()
    {
        return hour;
    }
}
