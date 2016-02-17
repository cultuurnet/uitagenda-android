package org.uitagenda.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oauth.signpost.http.HttpRequest;
import retrofit.client.Header;
import retrofit.client.Request;

/**
 * Created by Inneke on 11/08/2015.
 */
public class HttpRequestAdapter implements HttpRequest
{
    private static final String DEFAULT_CONTENT_TYPE = "application/json";

    private Request request;
    private String contentType;

    public HttpRequestAdapter(Request request)
    {
        this(request, request.getBody() != null ? request.getBody().mimeType() : DEFAULT_CONTENT_TYPE);
    }

    public HttpRequestAdapter(Request request, String contentType)
    {
        this.request = request;
        this.contentType = contentType;
    }

    @Override
    public Map<String, String> getAllHeaders()
    {
        Map<String, String> headers = new HashMap<>();
        for(Header header : this.request.getHeaders())
            headers.put(header.getName(), header.getValue());
        return headers;
    }

    @Override
    public String getContentType()
    {
        return this.contentType;
    }

    @Override
    public String getHeader(String key)
    {
        for(Header header : this.request.getHeaders())
            if(key.equals(header.getName()))
                return header.getValue();
        return null;
    }

    @Override
    public InputStream getMessagePayload() throws IOException
    {
        final String contentType = this.getContentType();
        if (null != contentType && contentType.startsWith("application/x-www-form-urlencoded"))
        {
            long contentLength = this.request.getBody().length();
            ByteArrayOutputStream output = new ByteArrayOutputStream(Long.valueOf(contentLength).intValue());
            this.request.getBody().writeTo(output);
            return new ByteArrayInputStream(output.toByteArray());
        }

        throw new UnsupportedOperationException("The content type" + (contentType != null ? " " + contentType : "") + " is not supported.");
    }

    @Override
    public String getMethod()
    {
        return this.request.getMethod();
    }

    @Override
    public String getRequestUrl()
    {
        return this.request.getUrl();
    }

    @Override
    public void setHeader(String key, String value)
    {
        List<Header> headers = new ArrayList<>();
        headers.addAll(this.request.getHeaders());
        headers.add(new Header(key, value));
        this.request = new Request(this.request.getMethod(), this.request.getUrl(), headers, this.request.getBody());
    }

    @Override
    public void setRequestUrl(String url)
    {
        this.request = new Request(this.request.getMethod(), url, this.request.getHeaders(), this.request.getBody());
    }

    @Override
    public Object unwrap()
    {
        return this.request;
    }
}
