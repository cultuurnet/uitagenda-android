package org.uitagenda.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thijscoorevits on 9/12/15.
 */
public class Period
{

    @SerializedName("datefrom")
    private String dateFrom;
    @SerializedName("dateto")
    private String dateTo;

    public String getDateFrom()
    {
        return dateFrom;
    }

    public String getDateTo()
    {
        return dateTo;
    }

}
