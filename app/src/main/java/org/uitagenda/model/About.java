package org.uitagenda.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Inneke on 24/08/2015.
 */
public class About
{
    @SerializedName("appinfo")
    private String aboutText;

    public String getAboutText()
    {
        return aboutText;
    }
}
