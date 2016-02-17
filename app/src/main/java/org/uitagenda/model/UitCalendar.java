package org.uitagenda.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by thijscoorevits on 9/12/15.
 */
public class UitCalendar
{
    private Periods periods;
    @SerializedName("permanentopeningtimes")
    private PermanentOpeningTimes permanentOpeningTimes;
    private Timestamps timestamps;

    public Period getPeriod()
    {
        return this.periods != null ? this.periods.getPeriod() : null;
    }

    public boolean isPermanent()
    {
        return this.permanentOpeningTimes != null;
    }

    public List<Timestamp> getTimestamp()
    {
        return this.timestamps != null ? this.timestamps.getTimestamp() : null;
    }
}
