package com.github.nicolausyes.themepicker;

import android.support.annotation.ColorInt;

public class ColorPair {
    private int firstColor;
    private int secondColor;

    public ColorPair(@ColorInt int firstColor, @ColorInt int secondColor) {
        this.firstColor = firstColor;
        this.secondColor = secondColor;
    }

    public int getFirstColor() {
        return firstColor;
    }

    public void setFirstColor(@ColorInt int firstColor) {
        this.firstColor = firstColor;
    }

    public int getSecondColor() {
        return secondColor;
    }

    public void setSecondColor(@ColorInt int secondColor) {
        this.secondColor = secondColor;
    }
}
