package org.uitagenda.api;

import org.uitagenda.model.About;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by Inneke on 24/08/2015.
 */
public interface UitApi
{
    @GET("/appinfo.json")
    void getAbout(Callback<About> callback);
}
