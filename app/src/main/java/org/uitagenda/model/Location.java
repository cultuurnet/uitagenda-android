package org.uitagenda.model;

/**
 * Created by Inneke on 12/08/2015.
 */
public class Location
{
    private Label label;

    public String getValue()
    {
        return this.label != null ? this.label.getValue() : null;
    }
}
