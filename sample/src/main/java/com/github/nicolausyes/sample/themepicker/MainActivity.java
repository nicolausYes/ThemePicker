package com.github.nicolausyes.sample.themepicker;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.nicolausyes.themepicker.ThemePickerDialog;
import com.github.nicolausyes.themepicker.ThemePickerDialogFragment;

public class MainActivity extends AppCompatActivity /*implements ThemePickerDialog.OnPositiveCallback*/ {

    public static final String KEY_BACKGROUND = "background";
    public static final String KEY_TEXT = "text";

    public static final String TAG = "theme_picker";

    ThemePickerDialogFragment themePickerDialog;

    int backgroundColor;
    int textColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            backgroundColor = savedInstanceState.getInt(KEY_BACKGROUND);
            textColor = savedInstanceState.getInt(KEY_TEXT);

            ThemePickerDialogFragment themePickerDialogFragment = (ThemePickerDialogFragment) getFragmentManager()
                    .findFragmentByTag(TAG);
            if (themePickerDialogFragment != null)
                themePickerDialogFragment.onPositive(onPositiveCallback);
        } else {
            backgroundColor = Color.DKGRAY;
            textColor = Color.WHITE;
        }

        refreshColors(backgroundColor, textColor);

        findViewById(R.id.button_change_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
                themePickerDialog.show(getFragmentManager(), TAG);
            }
        });
    }

    /*@Override
    public void onClick(@ColorInt int backgroundColor, @ColorInt int textColor) {
        refreshColors(backgroundColor, textColor);
    }*/

    private void createDialog() {
        themePickerDialog = ThemePickerDialogFragment.newInstance(
                new ThemePickerDialog.Builder(MainActivity.this)
                        .initBackgroundColor(backgroundColor)
                        .initTextColor(textColor)
                        .dialogBackgroundColorRes(R.color.dialog_background)
                        .dialogDefaultThemesBackgroundColorRes(R.color.themes_background)
                        .tabTextColorRes(R.color.tab_text)
                        .tabIndicatorColorRes(R.color.tab_indicator)
                        .tabDividerColorRes(R.color.tab_divider)
                        .buttonsTextColorRes(R.color.buttons_color)
                        .themeTextColorRes(R.color.theme_text)
                        //.onPositive(this)
                        .onPositive(onPositiveCallback)
        );
    }

    ThemePickerDialog.OnPositiveCallback onPositiveCallback = new ThemePickerDialog.OnPositiveCallback() {
        @Override
        public void onClick(@ColorInt int backgroundColor, @ColorInt int textColor) {
            refreshColors(backgroundColor, textColor);
        }
    };

    private void refreshColors(int backgroundColor, int textColor) {
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;

        findViewById(R.id.preview_background).setBackgroundColor(backgroundColor);
        ((TextView) findViewById(R.id.preview_text)).setTextColor(textColor);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_BACKGROUND, backgroundColor);
        outState.putInt(KEY_TEXT, textColor);
    }
}