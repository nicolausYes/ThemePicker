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
import com.nicolausyes.circleview.TwoColorsCircleView;
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

    Context mContext;
    Builder mBuilder;
    ViewGroup mRootView;
    ViewPager mViewPager;

    SmartTabLayout mSmartTabLayout;

    View mPreviewBackground;
    TextView mPreviewText;

    ColorPickerView mBackgroundColorPickerView;
    ColorPickerView mTextColorPickerView;

    protected ThemePickerDialog(Context context, Builder builder) {
        super(context, R.style.ThemePickerDialog);
        mContext = context;
        init(builder);
    }

    protected void init(@NonNull final Builder builder) {
        Log.d("TAG", "Dialog getContext(): " + getContext().toString());
        Log.d("TAG", "Dialog builder.context: " + builder.context.toString());

        mBuilder = builder;
        // means if we are creating completely new dialog or recreating already existing
        boolean initialization = mRootView == null;

        if(initialization) {
            final LayoutInflater inflater = LayoutInflater.from(getContext());
            mRootView = (ViewGroup) inflater.inflate(R.layout.layout_main, null);
        }

        // set dialog size
        getWindow().setLayout(ResourceUtil.getDimenInPixels(getContext(), R.dimen.dialog_width),
                ResourceUtil.getDimenInPixels(getContext(), R.dimen.dialog_height));

        // get preview panel views
        mPreviewBackground = mRootView.findViewById(R.id.preview_background);
        mPreviewText = (TextView) mRootView.findViewById(R.id.preview_text);

        // set themes panel background availiable height is below 500dp
        if(mRootView.findViewById(R.id.default_themes_container_wrapper) != null)
            mRootView.findViewById(R.id.default_themes_container_wrapper)
                    .setBackgroundColor(builder.dialogDefaultThemesBackgroundColor);

        // set dialog background color
        mRootView.setBackgroundColor(builder.dialogBackgroundColor);

        // set default themes label text color
        ((TextView)mRootView.findViewById(R.id.theme_header)).setTextColor(builder.themeTextColor);

        setupTabs();
        setupDefaultThemes();
        setupTypefaces();
        setupButtons();
        if(initialization) {
            setView(mRootView, 0, 0, 0, 0);
        }

   //     Log.d("TAG", "CREATED");
    }

    @UiThread
    public void onConfigurationChanged() {

        // remove all views from the root view and inflate new content there
        mRootView.removeAllViews();
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.layout_main_content, mRootView);
        init(mBuilder);
    }

    @Override
    public Bundle onSaveInstanceState() {
  //      Log.d("TAG", "SAVED");
        Bundle bundle = super.onSaveInstanceState();
        bundle.putInt(KEY_BACKGROUND, mBuilder.initBackgroundColor);
        bundle.putInt(KEY_TEXT, mBuilder.initTextColor);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
  //      Log.d("TAG", "RESTORED");
        super.onRestoreInstanceState(savedInstanceState);
        mBuilder.initBackgroundColor(savedInstanceState.getInt(KEY_BACKGROUND));
        mBuilder.initTextColor(savedInstanceState.getInt(KEY_TEXT));
    }

    @UiThread
    void setPreviewBackgroundColor(@ColorInt int color) {
        mPreviewBackground.setBackgroundColor(color);
        mBuilder.initBackgroundColor(color);
    }

    @UiThread
    void setPreviewTextColor(@ColorInt int color) {
        mPreviewText.setTextColor(color);
        mBuilder.initTextColor(color);
    }

    /**
     * This method should be called when viewpager items are instantiated
     */
    @UiThread
    void initColorPickerView() {
        mBackgroundColorPickerView = (ColorPickerView) mViewPager.findViewWithTag(0).findViewById(R.id.colorpickerview);
        mTextColorPickerView = (ColorPickerView) mViewPager.findViewWithTag(1).findViewById(R.id.colorpickerview);

        mBackgroundColorPickerView.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                setPreviewBackgroundColor(color);
            }
        });

        mTextColorPickerView.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                setPreviewTextColor(color);
            }
        });

        mBackgroundColorPickerView.setColor(mBuilder.initBackgroundColor);
        mTextColorPickerView.setColor(mBuilder.initTextColor);
    }

    @UiThread
    private void setupButtons() {
        Button positive = (Button)mRootView.findViewById(R.id.button_apply);
        Button negative = (Button)mRootView.findViewById(R.id.button_cancel);

        positive.setTextColor(mBuilder.buttonsTextColor);
        negative.setTextColor(mBuilder.buttonsTextColor);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBuilder.onPositiveCallback != null)
                    mBuilder.onPositiveCallback.onClick(mBuilder.initBackgroundColor, mBuilder.initTextColor);
                dismiss();
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBuilder.onNegativeCallback != null)
                    mBuilder.onNegativeCallback.onClick();
                dismiss();
            }
        });
    }

    @UiThread
    void setupTypefaces() {
        try {
            Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Medium.ttf");
            ((Button)mRootView.findViewById(R.id.button_apply)).setTypeface(typeface);
            ((Button)mRootView.findViewById(R.id.button_cancel)).setTypeface(typeface);
            ((TextView)mRootView.findViewById(R.id.preview_text)).setTypeface(typeface);

            List<View> textViews = ViewUtil.getAllChildrenOfType(mSmartTabLayout, TextView.class);
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

        mViewPager = (ViewPager)mRootView.findViewById(R.id.viewpager);
        mViewPager.setAdapter(adapter);

        mSmartTabLayout = (SmartTabLayout) mRootView.findViewById(R.id.viewpagertab);

        mSmartTabLayout.setBackgroundColor(mBuilder.dialogBackgroundColor);
        mSmartTabLayout.setDefaultTabTextColor(mBuilder.tabTextColor);
        mSmartTabLayout.setCustomTabColorizer(new SmartTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return mBuilder.tabIndicatorColor;
            }

            @Override
            public int getDividerColor(int position) {
                return mBuilder.tabDividerColor;
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mBuilder.tabSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mSmartTabLayout.setViewPager(mViewPager);
        mViewPager.setCurrentItem(mBuilder.tabSelected);
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

        ViewGroup rootThemes = (ViewGroup) mRootView.findViewById(R.id.default_themes_container);
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
            mBackgroundColorPickerView.setColor(colorPair.getFirstColor());
            mTextColorPickerView.setColor(colorPair.getSecondColor());
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
