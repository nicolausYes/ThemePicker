package com.github.nicolausyes.themepicker;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.Configuration;
import android.os.Bundle;


public class ThemePickerDialogFragment extends DialogFragment {

    public static final String KEY_BUILDER = "builder";
    ThemePickerDialog.Builder builder;

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
        builder = getArguments().getParcelable(KEY_BUILDER);
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
        if(builder.context != getActivity()) {
            try {
                ThemePickerDialog.OnPositiveCallback onPositiveCallback = (ThemePickerDialog.OnPositiveCallback) getActivity();
                builder.onPositive(onPositiveCallback);
            } catch (Exception ignored) { // it can be NPE or ClassCastException, it doesn't matter
            }

            try {
                ThemePickerDialog.OnNegativeCallback onNegativeCallback = (ThemePickerDialog.OnNegativeCallback) getActivity();
                builder.onNegative(onNegativeCallback);
            } catch (Exception ignored) { // it can be NPE or ClassCastException, it doesn't matter
            }
        }

        builder.context = getActivity();

        return builder.build();
    }

    public void onPositive(ThemePickerDialog.OnPositiveCallback onPositiveCallback) {
        if(builder != null)
            builder.onPositive(onPositiveCallback);
    }

    public void onNegative(ThemePickerDialog.OnNegativeCallback onNegativeCallback) {
        if(builder != null)
            builder.onNegative(onNegativeCallback);
    }
}