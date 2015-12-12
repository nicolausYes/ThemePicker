package com.github.nicolausyes.themepicker;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;


public class ThemePickerDialogFragment extends DialogFragment {

    public static final String KEY_BUILDER = "builder";
    ThemePickerDialog.Builder mBuilder;

    public ThemePickerDialogFragment() {
    }

    public static ThemePickerDialogFragment newInstance(ThemePickerDialog.Builder builder) {
        //Log.d("fragment", "FRAGMENT newInstance");
        ThemePickerDialogFragment themePickerDialogFragment = new ThemePickerDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_BUILDER, builder);
        themePickerDialogFragment.setArguments(args);

        return themePickerDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
        //Log.d("fragment", "FRAGMENT onCreate");
        mBuilder = getArguments().getParcelable(KEY_BUILDER);
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ((ThemePickerDialog)getDialog()).onConfigurationChanged();
        //Log.d("fragment", "FRAGMENT onConfigurationChanged");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Log.d("fragment", "FRAGMENT onCreateDialog");
        if(mBuilder.context != getActivity()) {
            try {
                ThemePickerDialog.OnPositiveCallback onPositiveCallback = (ThemePickerDialog.OnPositiveCallback) getActivity();
                mBuilder.onPositive(onPositiveCallback);
            } catch (Exception ignored) { // it can be NPE or ClassCastException, it doesn't matter
            }

            try {
                ThemePickerDialog.OnNegativeCallback onNegativeCallback = (ThemePickerDialog.OnNegativeCallback) getActivity();
                mBuilder.onNegative(onNegativeCallback);
            } catch (Exception ignored) { // it can be NPE or ClassCastException, it doesn't matter
            }
        }

        mBuilder.context = getActivity();

        return mBuilder.build();
    }

    public void onPositive(ThemePickerDialog.OnPositiveCallback onPositiveCallback) {
        if(mBuilder != null)
            mBuilder.onPositive(onPositiveCallback);
    }

    public void onNegative(ThemePickerDialog.OnNegativeCallback onNegativeCallback) {
        if(mBuilder != null)
            mBuilder.onNegative(onNegativeCallback);
    }
}