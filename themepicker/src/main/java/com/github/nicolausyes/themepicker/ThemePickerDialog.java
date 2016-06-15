    package com.github.nicolausyes.themepicker;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.nicolausyes.colorpickerview.view.ColorPickerView;
import com.github.nicolausyes.themepicker.util.ResourceUtil;
import com.github.nicolausyes.themepicker.util.ViewUtil;
import com.github.nicolausyes.circleview.TwoColorsCircleView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.ViewPagerItems;

import java.util.ArrayList;
import java.util.List;


public class ThemePickerDialog extends AlertDialog {

    public interface OnPositiveCallback {
        void onClick(@ColorInt int backgroundColor, @ColorInt int textColor) ;
    }

    public interface OnNegativeCallback {
        void onClick();
    }

    /**
     * Key for saving background color state
     */
    public static final String KEY_BACKGROUND = "background";

    /**
     * Key for saving text color state
     */
    public static final String KEY_TEXT = "text";

    Builder builder;
    ViewGroup rootView;
    ViewPager viewPager;

    SmartTabLayout smartTabLayout;

    View previewBackground;
    TextView previewText;

    ColorPickerView backgroundColorPickerView;
    ColorPickerView textColorPickerView;

    protected ThemePickerDialog(Context context, Builder builder) {
        super(context, R.style.ThemePickerDialog);
        init(builder);
    }

    protected void init(@NonNull final Builder builder) {
        this.builder = builder;
        // means if we are creating completely new dialog or recreating already existing
        boolean initialization = rootView == null;

        if(initialization) {
            final LayoutInflater inflater = LayoutInflater.from(getContext());
            rootView = (ViewGroup) inflater.inflate(R.layout.layout_main, null);
        }

        // get preview panel views
        previewBackground = rootView.findViewById(R.id.preview_background);
        previewText = (TextView) rootView.findViewById(R.id.preview_text);

        // set themes panel background availiable height is below 500dp
        if(rootView.findViewById(R.id.default_themes_container_wrapper) != null)
            rootView.findViewById(R.id.default_themes_container_wrapper)
                    .setBackgroundColor(builder.dialogDefaultThemesBackgroundColor);

        // set dialog background color
        rootView.setBackgroundColor(builder.dialogBackgroundColor);

        // set default themes label text color
        ((TextView) rootView.findViewById(R.id.theme_header)).setTextColor(builder.themeTextColor);

        setupTabs();
        setupDefaultThemes();
        setupTypefaces();
        setupButtons();
        if(initialization) {
            setView(rootView, 0, 0, 0, 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set dialog size
        getWindow().setLayout(ResourceUtil.getDimenInPixels(getContext(), R.dimen.dialog_width),
                ResourceUtil.getDimenInPixels(getContext(), R.dimen.dialog_height));
    }

    @UiThread
    public void onConfigurationChanged() {
        // remove all views from the root view and inflate new content there
        rootView.removeAllViews();
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.layout_main_content, rootView);
        init(builder);
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle bundle = super.onSaveInstanceState();
        bundle.putInt(KEY_BACKGROUND, builder.initBackgroundColor);
        bundle.putInt(KEY_TEXT, builder.initTextColor);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        builder.initBackgroundColor(savedInstanceState.getInt(KEY_BACKGROUND));
        builder.initTextColor(savedInstanceState.getInt(KEY_TEXT));
    }

    @UiThread
    void setPreviewBackgroundColor(@ColorInt int color) {
        previewBackground.setBackgroundColor(color);
        builder.initBackgroundColor(color);
    }

    @UiThread
    void setPreviewTextColor(@ColorInt int color) {
        previewText.setTextColor(color);
        builder.initTextColor(color);
    }

    /**
     * This method should be called when viewpager items are instantiated
     */
    @UiThread
    void initColorPickerView() {
        backgroundColorPickerView = (ColorPickerView) viewPager.findViewWithTag(0).findViewById(R.id.colorpickerview);
        textColorPickerView = (ColorPickerView) viewPager.findViewWithTag(1).findViewById(R.id.colorpickerview);

        backgroundColorPickerView.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                setPreviewBackgroundColor(color);
            }
        });

        textColorPickerView.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                setPreviewTextColor(color);
            }
        });

        backgroundColorPickerView.setColor(builder.initBackgroundColor);
        textColorPickerView.setColor(builder.initTextColor);
    }

    @UiThread
    private void setupButtons() {
        Button positive = (Button) rootView.findViewById(R.id.button_apply);
        Button negative = (Button) rootView.findViewById(R.id.button_cancel);

        positive.setTextColor(builder.buttonsTextColor);
        negative.setTextColor(builder.buttonsTextColor);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (builder.onPositiveCallback != null)
                    builder.onPositiveCallback.onClick(builder.initBackgroundColor, builder.initTextColor);
                dismiss();
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(builder.onNegativeCallback != null)
                    builder.onNegativeCallback.onClick();
                dismiss();
            }
        });
    }

    @UiThread
    void setupTypefaces() {
        try {
            Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Medium.ttf");
            ((Button) rootView.findViewById(R.id.button_apply)).setTypeface(typeface);
            ((Button) rootView.findViewById(R.id.button_cancel)).setTypeface(typeface);
            ((TextView) rootView.findViewById(R.id.preview_text)).setTypeface(typeface);

            List<View> textViews = ViewUtil.getAllChildrenOfType(smartTabLayout, TextView.class);
            for(View view : textViews)
                ((TextView)view).setTypeface(typeface);

        } catch (Exception ignored) {}
    }

    @UiThread
    void setupTabs() {
        CustomViewPagerItemAdapter adapter = new CustomViewPagerItemAdapter(ViewPagerItems.with(getContext())
                .add(R.string.tab_background, R.layout.layout_colorpicker)
                .add(R.string.tab_text, R.layout.layout_colorpicker)
                .create());

        adapter.setOnInstantiateItemListener(new CustomViewPagerItemAdapter.OnInstantiateItemListener() {
            @Override
            public void onItemInstantiated() {
                initColorPickerView();
            }
        });

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        smartTabLayout = (SmartTabLayout) rootView.findViewById(R.id.viewpagertab);

        smartTabLayout.setDividerColors();

        smartTabLayout.setBackgroundColor(builder.dialogBackgroundColor);
        smartTabLayout.setDefaultTabTextColor(builder.tabTextColor);
        smartTabLayout.setCustomTabColorizer(new SmartTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return builder.tabIndicatorColor;
            }

            @Override
            public int getDividerColor(int position) {
                return builder.tabDividerColor;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                builder.tabSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        smartTabLayout.setViewPager(viewPager);
        viewPager.setCurrentItem(builder.tabSelected);
    }

    @UiThread
    void setupDefaultThemes() {

        List<ColorPair> colorPairs = new ArrayList<>();
        colorPairs.add(new ColorPair(ContextCompat.getColor(getContext(), R.color.theme_white_background),
                (ContextCompat.getColor(getContext(), R.color.theme_white_text))));
        colorPairs.add(new ColorPair(ContextCompat.getColor(getContext(), R.color.theme_sepia_background),
                (ContextCompat.getColor(getContext(), R.color.theme_sepia_text))));
        colorPairs.add(new ColorPair(ContextCompat.getColor(getContext(), R.color.theme_grey_background),
                (ContextCompat.getColor(getContext(), R.color.theme_grey_text))));
        colorPairs.add(new ColorPair(ContextCompat.getColor(getContext(), R.color.theme_dark_background),
                (ContextCompat.getColor(getContext(), R.color.theme_dark_text))));

        ViewGroup rootThemes = (ViewGroup) rootView.findViewById(R.id.default_themes_container);
        List<View> themeViews = ViewUtil.getAllChildrenOfType(rootThemes, TwoColorsCircleView.class);
        if(themeViews.size() != 4)
            return;

        for(int i = 0; i < themeViews.size(); i++) {
            TwoColorsCircleView twoColorsCircleView = (TwoColorsCircleView) themeViews.get(i);
            twoColorsCircleView.setFirstColor(colorPairs.get(i).getFirstColor());
            twoColorsCircleView.setSecondColor(colorPairs.get(i).getSecondColor());
            twoColorsCircleView.setFillColor(colorPairs.get(i).getFirstColor());
            themeViews.get(i).setTag(colorPairs.get(i));
            themeViews.get(i).setOnClickListener(onThemeClickListener);
        }
    }

    View.OnClickListener onThemeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ColorPair colorPair = (ColorPair) v.getTag();
            backgroundColorPickerView.setColor(colorPair.getFirstColor());
            textColorPickerView.setColor(colorPair.getSecondColor());
        }
    };


    public static class Builder implements Parcelable {

        protected Context context;
        protected int initBackgroundColor;
        protected int initTextColor;

        protected int dialogBackgroundColor;
        protected int dialogDefaultThemesBackgroundColor;

        protected int tabTextColor;
        protected int tabIndicatorColor;
        protected int tabDividerColor;
        protected int tabUnderlineColor;
        protected int tabSelected;

        protected int buttonsTextColor;

        protected int themeTextColor;

        protected OnPositiveCallback onPositiveCallback;
        protected OnNegativeCallback onNegativeCallback;


        public Builder(@NonNull Context context) {
            this.context = context;
            initBackgroundColor = ResourceUtil.getColor(context, R.color.default_theme_preview_background);
            initTextColor = ResourceUtil.getColor(context, R.color.default_theme_preview_text);

            dialogBackgroundColor = ResourceUtil.getColor(context, R.color.default_dialog_background);
            dialogDefaultThemesBackgroundColor = ResourceUtil.getColor(context, R.color.default_themes_background);

            tabTextColor = ResourceUtil.getColor(context, R.color.default_tab_text);
            tabIndicatorColor = ResourceUtil.getColor(context, R.color.default_tab_indicator);
            tabDividerColor = ResourceUtil.getColor(context, R.color.default_tab_divider);
            tabUnderlineColor = ResourceUtil.getColor(context, R.color.default_tab_underline_color);
            tabSelected = 0;

            buttonsTextColor = ResourceUtil.getColor(context, R.color.default_buttons_color);

            themeTextColor = ResourceUtil.getColor(context, R.color.default_theme_text);
        }

        public Builder initBackgroundColor(@ColorInt int color) {
            initBackgroundColor = color;
            return this;
        }

        public Builder initBackgroundColorRes(@ColorRes int colorRes) {
            initBackgroundColor = ResourceUtil.getColor(context, colorRes);
            return this;
        }

        public Builder initTextColor(@ColorInt int color) {
            initTextColor = color;
            return this;
        }

        public Builder initTextColorRes(@ColorRes int colorRes) {
            initTextColor = ResourceUtil.getColor(context, colorRes);
            return this;
        }

        public Builder dialogBackgroundColor(@ColorInt int color) {
            dialogBackgroundColor = color;
            return this;
        }

        public Builder dialogBackgroundColorRes(@ColorRes int colorRes) {
            dialogBackgroundColor = ResourceUtil.getColor(context, colorRes);
            return this;
        }

        public int getDialogDefaultThemesBackgroundColor() {
            return dialogDefaultThemesBackgroundColor;
        }

        public Builder dialogDefaultThemesBackgroundColor(@ColorInt int color) {
            dialogDefaultThemesBackgroundColor = color;
            return this;
        }

        public Builder dialogDefaultThemesBackgroundColorRes(@ColorRes int colorRes) {
            dialogDefaultThemesBackgroundColor = ResourceUtil.getColor(context, colorRes);
            return this;
        }

        public Builder tabTextColor(@ColorInt int color) {
            tabTextColor = color;
            return this;
        }

        public Builder tabTextColorRes(@ColorRes int colorRes) {
            tabTextColor = ResourceUtil.getColor(context, colorRes);
            return this;
        }

        public Builder tabIndicatorColor(@ColorInt int color) {
            tabIndicatorColor = color;
            return this;
        }

        public Builder tabIndicatorColorRes(@ColorRes int colorRes) {
            tabIndicatorColor = ResourceUtil.getColor(context, colorRes);
            return this;
        }

        public Builder tabDividerColor(@ColorInt int color) {
            tabDividerColor = color;
            return this;
        }

        public Builder tabDividerColorRes(@ColorRes int colorRes) {
            tabDividerColor = ResourceUtil.getColor(context, colorRes);
            return this;
        }

        /**
         * Will be implemented later
         * @param color
         * @return Builder object
         */
        protected Builder tabUnderlineColor(@ColorInt int color) {
            tabUnderlineColor = color;
            return this;
        }

        protected Builder tabUnderlineColorRes(@ColorRes int colorRes) {
            tabUnderlineColor = ResourceUtil.getColor(context, colorRes);
            return this;
        }

        public Builder tabSelected(int index) {
            tabSelected = index;
            return this;
        }

        public Builder buttonsTextColor(@ColorInt int color) {
            buttonsTextColor = color;
            return this;
        }

        public Builder buttonsTextColorRes(@ColorRes int colorRes) {
            buttonsTextColor = ResourceUtil.getColor(context, colorRes);
            return this;
        }

        public Builder themeTextColor(@ColorInt int color) {
            themeTextColor = color;
            return this;
        }

        public Builder themeTextColorRes(@ColorRes int colorRes) {
            themeTextColor = ResourceUtil.getColor(context, colorRes);
            return this;
        }

        public Builder onPositive(@NonNull OnPositiveCallback callback) {
            this.onPositiveCallback = callback;
            return this;
        }

        public Builder onNegative(@NonNull OnNegativeCallback callback) {
            this.onNegativeCallback = callback;
            return this;
        }

        @UiThread
        public ThemePickerDialog build() {
            return new ThemePickerDialog(context, this);
        }

        @UiThread
        public ThemePickerDialog show() {
            ThemePickerDialog dialog = build();
            dialog.show();
            return dialog;
        }

        protected Builder(Parcel in) {
            initBackgroundColor = in.readInt();
            initTextColor = in.readInt();
            dialogBackgroundColor = in.readInt();
            dialogDefaultThemesBackgroundColor = in.readInt();
            tabTextColor = in.readInt();
            tabIndicatorColor = in.readInt();
            tabDividerColor = in.readInt();
            //tabUnderlineColor = in.readInt();
            tabSelected = in.readInt();
            themeTextColor = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(initBackgroundColor);
            dest.writeInt(initTextColor);
            dest.writeInt(dialogBackgroundColor);
            dest.writeInt(dialogDefaultThemesBackgroundColor);
            dest.writeInt(tabTextColor);
            dest.writeInt(tabIndicatorColor);
            dest.writeInt(tabDividerColor);
            //dest.writeInt(tabUnderlineColor);
            dest.writeInt(tabSelected);
            dest.writeInt(themeTextColor);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<Builder> CREATOR = new Parcelable.Creator<Builder>() {
            @Override
            public Builder createFromParcel(Parcel in) {
                return new Builder(in);
            }

            @Override
            public Builder[] newArray(int size) {
                return new Builder[size];
            }
        };
    }
}
