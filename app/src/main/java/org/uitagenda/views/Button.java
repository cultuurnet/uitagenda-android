package org.uitagenda.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import org.uitagenda.R;
import org.uitagenda.utils.TypefaceHelper;

/**
 * Created by Inneke on 18/08/2015.
 */
public class Button extends android.widget.Button
{
    public static final int REGULAR = 1;
    public static final int BOLD = 2;

    public Button(Context context)
    {
        this(context, null);
    }

    public Button(Context context, AttributeSet attrs)
    {
        this(context, attrs, android.support.v7.appcompat.R.attr.buttonStyle);
    }

    public Button(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        if(attrs != null)
        {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Button, 0, 0);

            int typefaceNumber = a.getInt(R.styleable.Button_typeface, REGULAR);
            this.setTypeface(context, typefaceNumber);

            a.recycle();
        }
    }

    public void setTypeface(Context context, int typefaceNumber)
    {
        switch (typefaceNumber)
        {
            case REGULAR:
                this.setTypeface(TypefaceHelper.getTypefaceRegular(context));
                break;

            case BOLD:
                this.setTypeface(TypefaceHelper.getTypefaceBold(context));
                break;
        }
    }
}
