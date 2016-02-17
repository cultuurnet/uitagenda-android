package org.uitagenda.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Inneke on 12/08/2015.
 */
public class Event
{
    @SerializedName("Long")
    private int totalNumberOfEvents;
    private EventInner event;

    public int getTotalNumberOfEvents()
    {
        return totalNumberOfEvents;
    }

    public EventInner getEvent()
    {
        return event;
    }
}
