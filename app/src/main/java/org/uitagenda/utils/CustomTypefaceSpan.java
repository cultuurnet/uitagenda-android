package org.uitagenda.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

/**
 * Created by Inneke on 13/08/2015.
 */
public class CustomTypefaceSpan extends TypefaceSpan
{
    private final Typeface newType;

    public static final String REGULAR = "fonts/PTN57F.ttf";
    public static final String BOLD = "fonts/PTN77F.ttf";

    public CustomTypefaceSpan(Context context, String typeface)
    {
        super("");

        if(BOLD.equals(typeface))
            this.newType = TypefaceHelper.getTypefaceBold(context);
        else
            this.newType = TypefaceHelper.getTypefaceRegular(context);
    }

    @Override
    public void updateDrawState(TextPaint ds)
    {
        ds.setTypeface(this.newType);
    }

    @Override
    public void updateMeasureState(TextPaint paint)
    {
        paint.setTypeface(this.newType);
    }
}
