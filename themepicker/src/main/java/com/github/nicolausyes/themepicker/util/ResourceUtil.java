package com.github.nicolausyes.themepicker.util;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.github.nicolausyes.themepicker.R;

/**
 * Created by Nick on 23-Nov-15.
 */
public class ResourceUtil {

    public static int getColor(@NonNull Context context, @ColorRes int colorRes) {
        return ContextCompat.getColor(context, colorRes);
    }

    public static int getDimenInPixels(@NonNull Context context, @DimenRes int dimenRes) {
        return context.getResources().getDimensionPixelSize(dimenRes);
    }
}
