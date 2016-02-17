package org.uitagenda.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Inneke on 12/08/2015.
 */
public class MediaFiles
{
    @SerializedName("file")
    private List<MediaFile> files;

    public List<MediaFile> getFiles()
    {
        return files;
    }
}
