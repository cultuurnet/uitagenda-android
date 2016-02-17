package org.uitagenda.utils;

import android.text.TextPaint;
import android.text.style.URLSpan;

/**
 * Created by Inneke on 19/08/2015.
 */
public class UrlNoUnderlineSpan extends URLSpan
{
    public UrlNoUnderlineSpan(String url)
    {
        super(url);
    }

    @Override
    public void updateDrawState(TextPaint ds)
    {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
    }
}
