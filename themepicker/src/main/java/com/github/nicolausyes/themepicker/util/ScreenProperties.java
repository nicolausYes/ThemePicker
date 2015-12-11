package com.github.nicolausyes.themepicker.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

/**
 * Created by Work on 31.08.2015.
 */
public class ScreenProperties {
    public static int getWidth(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        return configuration.screenWidthDp;
    }

    public static int getHeight(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        return configuration.screenHeightDp;
    }

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}
