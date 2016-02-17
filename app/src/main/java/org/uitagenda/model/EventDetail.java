package org.uitagenda.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Inneke on 12/08/2015.
 */
public class EventDetail
{
    private String title;
    @SerializedName("calendarsummary")
    private String calendarSummary;
    @SerializedName("shortdescription")
    private String shortDescription;
    @SerializedName("longdescription")
    private String longDescription;
    private Performers performers;
    @SerializedName("media")
    private MediaFiles mediaFiles;
    private Price price;

    public String getTitle()
    {
        return title;
    }

    public String getCalendarSummary()
    {
        return calendarSummary;
    }

    public String getShortDescription()
    {
        return shortDescription;
    }

    public String getLongDescription()
    {
        return longDescription;
    }

    public List<String> getPerformers()
    {
        List<String> performers = new ArrayList<>();
        if(this.performers != null)
            for(Performer performer : this.performers.getPerformers())
                if(performer != null)
                    performers.add(performer.getValue());
        return performers;
    }

    public String getPhoto()
    {
        if(this.mediaFiles != null && this.mediaFiles.getFiles() != null)
            for(MediaFile file : this.mediaFiles.getFiles())
                if(file != null && file.isPhoto() && file.getLink() != null)
                    return file.getLink();
        return null;
    }

    public List<String> getMediaInfos()
    {
        List<String> media = new ArrayList<>();
        if(this.mediaFiles != null && this.mediaFiles.getFiles() != null)
            for(MediaFile file : this.mediaFiles.getFiles())
                if(file != null && file.isMediaInfo())
                    media.add(file.getLink());
        return media;
    }

    public double getPriceValue()
    {
        return this.price != null ? this.price.getValue() : 0;
    }

    public String getPriceDescription()
    {
        return this.price != null ? this.price.getDescription() : null;
    }
}
