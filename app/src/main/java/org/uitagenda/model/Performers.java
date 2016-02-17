package org.uitagenda.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Inneke on 12/08/2015.
 */
public class Performers
{
    @SerializedName("performer")
    private List<Performer> performers;

    public List<Performer> getPerformers()
    {
        return performers;
    }
}
