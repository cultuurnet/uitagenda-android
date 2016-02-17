package org.uitagenda.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Inneke on 12/08/2015.
 */
public class ContactInfos
{
    @SerializedName("addressAndMailAndPhone")
    private List<ContactInfo> contactInfos;

    public List<ContactInfo> getContactInfos()
    {
        return contactInfos;
    }
}
