package org.uitagenda.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Inneke on 12/08/2015.
 */
public class Address
{
    @SerializedName("physical")
    private AddressPhysical addressPhysical;

    public String getCity()
    {
        return this.addressPhysical != null ? this.addressPhysical.getCity() : null;
    }

    public String getCountry()
    {
        return this.addressPhysical != null ? this.addressPhysical.getCountry() : null;
    }

    public String getStreet()
    {
        return this.addressPhysical != null ? this.addressPhysical.getStreet() : null;
    }

    public String getNumber()
    {
        return this.addressPhysical != null ? this.addressPhysical.getNumber() : null;
    }

    public String getZipCode()
    {
        return this.addressPhysical != null ? this.addressPhysical.getZipCode() : null;
    }

    public double getLatitude()
    {
        return this.addressPhysical != null ? this.addressPhysical.getLatitude() : 0;
    }

    public double getLongitude()
    {
        return this.addressPhysical != null ? this.addressPhysical.getLongitude() : 0;
    }
}
