package org.uitagenda.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Inneke on 12/08/2015.
 */
public class Coordinate
{
    @SerializedName("ycoordinate")
    private double latitude;
    @SerializedName("xcoordinate")
    private double longitude;

    public double getLatitude()
    {
        return latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }
}
