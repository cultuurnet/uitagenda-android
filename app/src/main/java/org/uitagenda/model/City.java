package org.uitagenda.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import org.uitagenda.R;

import java.io.Serializable;

/**
 * Created by Inneke on 20/08/2015.
 */
public class City implements Serializable
{
    @SerializedName("zipcode")
    private String zipCode;
    private String name;

    private static final String CITY_CURRENT = "0000";
    private static final String CITY_ALL = "000000";

    public City(String zipCode, String name)
    {
        this.zipCode = zipCode;
        this.name = name;
    }

    public static City newCurrentCity(Context context)
    {
        return new City(CITY_CURRENT, context.getString(R.string.search_location_current));
    }

    public static City newAllCity(Context context)
    {
        return new City(CITY_ALL, context.getString(R.string.search_location_all));
    }

    public String getZipCode()
    {
        return zipCode;
    }

    public String getName()
    {
        return name;
    }

    public boolean isCurrent()
    {
        return CITY_CURRENT.equals(this.zipCode);
    }

    public boolean isAll()
    {
        return CITY_ALL.equals(this.zipCode);
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null)
            return false;

        if(!(o instanceof City))
            return false;

        City other = (City) o;

        return other.getName().equals(this.getName());
    }

    public static City copyOf(City city)
    {
        if(city == null)
            return null;

        return new City(city.getZipCode(), city.getName());
    }
}
