package org.uitagenda.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Inneke on 12/08/2015.
 */
public class RootObject
{
    @SerializedName("rootObject")
    private List<Event> events;

    public List<Event> getEvents()
    {
        if(this.events != null && this.events.size() > 0)
            return this.events.subList(1, this.events.size());
        return null;
    }

    public int getTotalNumberOfEvents()
    {
        if(this.events != null && this.events.size() > 0 && this.events.get(0) != null)
            return this.events.get(0).getTotalNumberOfEvents();
        return 0;
    }
}
