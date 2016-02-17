package org.uitagenda.api;

import oauth.signpost.AbstractOAuthConsumer;
import oauth.signpost.http.HttpRequest;
import retrofit.client.Request;

/**
 * Created by Inneke on 11/08/2015.
 */
public class RetrofitHttpOAuthConsumer extends AbstractOAuthConsumer
{
    private static final long serialVersionUID = 1L;

    public RetrofitHttpOAuthConsumer(String consumerKey, String consumerSecret)
    {
        super(consumerKey, consumerSecret);
    }

    @Override
    protected HttpRequest wrap(Object request)
    {
        if (!(request instanceof Request))
            throw new IllegalArgumentException("This consumer expects requests of type " + Request.class.getCanonicalName());

        return new HttpRequestAdapter((Request) request);
    }
}
