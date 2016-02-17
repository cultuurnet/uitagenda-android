package org.uitagenda.api;

import org.uitagenda.model.RootObject;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Inneke on 11/08/2015.
 */
public interface UitOAuthApi
{
    @GET("/search?rows=15&group=event&datetype=today&q=*:*&fq=type:event&fq=language:nl&fq=-category_id:0.3.1.0.0")
    void getCurrentEvents(@Query("start") int startIndex,
                          @Query("pt") String latLong,
                          @Query("sort") String sort,
                          @Query("sfield") String locationType,
                          @Query("d") String radius,
                          Callback<RootObject> callback);

    @GET("/search?start=0&group=event&sort=startdate+asc&fq=type:event&fq=language:nl&past=true")
    void getFavoriteEvents(@Query("q") String favoriteQuery,
                           Callback<RootObject> callback);

    @GET("/search?rows=15&group=event&fq=type:event&fq=language:nl")
    void getFilteredEvents(@Query("start") int startIndex,
                           @Query("q") String searchQuery,
                           @Query("sort") String sort,
                           @Query("sfield") String locationType,
                           @Query("pt") String latLong,
                           @Query("d") String radius,
                           @Query("zipcode") String zipCodeWithRadius,
                           @Query("datetype") String when,
                           @Query("fq") List<String> extraSearchItems,
                           Callback<RootObject> callback);
}
