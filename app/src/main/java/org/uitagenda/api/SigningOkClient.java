package org.uitagenda.api;

import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;

/**
 * Created by Inneke on 11/08/2015.
 */
public class SigningOkClient extends OkClient
{

    private final RetrofitHttpOAuthConsumer oAuthConsumer;

    public SigningOkClient(RetrofitHttpOAuthConsumer consumer)
    {
        this.oAuthConsumer = consumer;
    }

    public SigningOkClient(OkHttpClient client, RetrofitHttpOAuthConsumer consumer)
    {
        super(client);
        this.oAuthConsumer = consumer;
    }

    @Override
    public Response execute(Request request) throws IOException
    {
        try
        {
            HttpRequestAdapter signedAdapter = (HttpRequestAdapter) this.oAuthConsumer.sign(request);
            Request requestToSend = (Request) signedAdapter.unwrap();
            return super.execute(requestToSend);
        } catch (OAuthMessageSignerException e)
        {
            // Fail to sign, ignore
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e)
        {
            // Fail to sign, ignore
            e.printStackTrace();
        } catch (OAuthCommunicationException e)
        {
            // Fail to sign, ignore
            e.printStackTrace();
        }
        return super.execute(request);
    }
}
