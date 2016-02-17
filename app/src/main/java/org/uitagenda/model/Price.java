package org.uitagenda.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Inneke on 12/08/2015.
 */
public class Price
{
    @SerializedName("pricevalue")
    private double value;
    @SerializedName("pricedescription")
    private String description;

    public double getValue()
    {
        return value;
    }

    public String getDescription()
    {
        return description;
    }
}
