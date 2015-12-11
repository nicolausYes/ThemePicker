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

        Log.d("TAG", "newInstance: " + builder.context.toString());

        return themePickerDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
        //Log.d("fragment", "FRAGMENT onCreate");
        //Log.d("fragment", "in onCreate" + Boolean.toString(getArguments().getParcelable("TAG") == null));
        mBuilder = getArguments().getParcelable(KEY_BUILDER);
        Log.d("TAG", "onCreate: " + mBuilder.context.toString());
        Log.d("TAG", "getActivity() in onCreate: " + getActivity().toString());
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
        Log.d("TAG", "getActivity() in onCreateDialog: " + getActivity().toString());
        Log.d("TAG", "onCreateDialog1: " + mBuilder.context.toString());

        if(mBuilder.context != getActivity()) {
            Log.d("TAG", "onCreateDialog: context not the same");
            try {
                ThemePickerDialog.OnPositiveCallback onPositiveCallback = (ThemePickerDialog.OnPositiveCallback) getActivity();
                mBuilder.onPositive(onPositiveCallback);
                Log.d("TAG", "onCreateDialog: set listener ok");
            } catch (Exception ignored) { // it can be NPE or ClassCastException, it doesn't matter
            }
        }

        mBuilder.context = getActivity();
        Log.d("TAG", "onCreateDialog2: " + mBuilder.context.toString());

        return mBuilder.build();
    }

    public void onPositive(ThemePickerDialog.OnPositiveCallback onPositiveCallback) {
        Log.d("TAG", "onPositive called: " + mBuilder.context.toString());
        if(mBuilder != null)
            mBuilder.onPositive(onPositiveCallback);
    }
}