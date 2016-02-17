package org.uitagenda.api;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by Inneke on 11/08/2015.
 */
public class ApiHelper
{
    private static UitOAuthApi serviceOAuth;
    private static UitApi service;

    public static UitOAuthApi getServiceOAuth()
    {
        if(serviceOAuth == null)
        {
            RetrofitHttpOAuthConsumer oAuthConsumer = new RetrofitHttpOAuthConsumer("", "");
            OkClient client = new SigningOkClient(oAuthConsumer);
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("http://www.uitid.be/uitid/rest/searchv2")
                    .setClient(client)
                    .setRequestInterceptor(new RequestInterceptor()
                    {
                        @Override
                        public void intercept(RequestFacade request)
                        {
                            request.addHeader("Accept", "application/json");
                        }
                    })
                    .build();

            serviceOAuth = restAdapter.create(UitOAuthApi.class);
        }

        return serviceOAuth;
    }

    public static UitApi getService()
    {
        if(service == null)
        {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("https://www.uitinvlaanderen.be")
                    .build();

            service = restAdapter.create(UitApi.class);
        }

        return service;
    }
}
