package org.uitagenda.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Inneke on 12/08/2015.
 */
public class MediaFile
{
    @SerializedName("mediatype")
    private String mediaType;
    @SerializedName("hlink")
    private String link;

    private static final String MEDIA_TYPE_PHOTO = "photo";
    private static final String MEDIA_TYPE_WEB_RESOURCE = "webresource";
    private static final String MEDIA_TYPE_FACEBOOK = "facebook";

    public String getMediaType()
    {
        return mediaType;
    }

    public String getLink()
    {
        if(this.link != null && this.link.startsWith("//"))
            this.link = "http:" + this.link;
        return this.link;
    }

    public boolean isMediaInfo()
    {
        return MEDIA_TYPE_WEB_RESOURCE.equals(this.mediaType) || MEDIA_TYPE_FACEBOOK.equals(this.mediaType);
    }

    public boolean isPhoto()
    {
        return MEDIA_TYPE_PHOTO.equals(this.mediaType);
    }
}
