package org.uitagenda.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Inneke on 12/08/2015.
 */
public class AddressPhysical
{
    private Label city;
    private String country;
    private Label street;
    @SerializedName("housenr")
    private String number;
    @SerializedName("zipcode")
    private String zipCode;
    @SerializedName("gis")
    private Coordinate coordinate;

    public String getCity()
    {
        return this.city != null ? this.city.getValue() : null;
    }

    public String getCountry()
    {
        return country;
    }

    public String getStreet()
    {
        return this.street != null ? this.street.getValue() : null;
    }

    public String getNumber()
    {
        return number;
    }

    public String getZipCode()
    {
        return zipCode;
    }

    public double getLatitude()
    {
        return this.coordinate != null ? this.coordinate.getLatitude() : 0;
    }

    public double getLongitude()
    {
        return this.coordinate != null ? this.coordinate.getLongitude() : 0;
    }
}
