package org.uitagenda.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Inneke on 25/08/2015.
 */
public class TypefaceHelper
{
    private static Typeface typefaceRegular;
    private static Typeface typefaceBold;

    public static Typeface getTypefaceRegular(Context context)
    {
        if(typefaceRegular == null)
        {
            typefaceRegular = Typeface.createFromAsset(context.getAssets(), "fonts/PTN57F.ttf");
        }

        return typefaceRegular;
    }

    public static Typeface getTypefaceBold(Context context)
    {
        if(typefaceBold == null)
        {
            typefaceBold = Typeface.createFromAsset(context.getAssets(), "fonts/PTN77F.ttf");
        }

        return typefaceBold;
    }
}
