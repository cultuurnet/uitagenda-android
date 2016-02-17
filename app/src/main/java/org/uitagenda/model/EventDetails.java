package org.uitagenda.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Inneke on 12/08/2015.
 */
public class EventDetails
{
    @SerializedName("eventdetail")
    private List<EventDetail> eventDetails;

    public EventDetail getEventDetail()
    {
        return this.eventDetails != null && this.eventDetails.size() > 0 ? this.eventDetails.get(0) : null;
    }
}
