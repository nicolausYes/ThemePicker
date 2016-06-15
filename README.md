# ThemePicker

Simple customizable dialog to pick a theme (background and text colors). Supports small screens. May be useful for custom text readers.

Quick Look
-----

Portrait:

![](https://raw.githubusercontent.com/nicolausYes/ThemePicker/master/screenshots/portrait.png)


Small screens:

![](https://raw.githubusercontent.com/nicolausYes/ThemePicker/master/screenshots/landscape.png)


#### In action (Sample & Usage in real app)
![](https://raw.githubusercontent.com/nicolausYes/ThemePicker/master/screenshots/in_action_1.gif) ![](https://raw.githubusercontent.com/nicolausYes/ThemePicker/master/screenshots/in_action_2.gif)


Usage
-----
```
ThemePickerDialogFragment themePickerDialog = ThemePickerDialogFragment.newInstance(
        new ThemePickerDialog.Builder(MainActivity.this)
                .initBackgroundColor(color)
                .initTextColor(color)
                .dialogBackgroundColor(color)
                .dialogDefaultThemesBackgroundColor(color)
                .tabTextColor(color)
                .tabIndicatorColor(color)
                .tabDividerColor(color)
                .buttonsTextColor(color)
                .themeTextColor(color)
                .onPositive(onPositiveCallback)
                .onNegative(onNegativeCallback)
);

themePickerDialog.show(getFragmentManager(), TAG);

// ...

ThemePickerDialog.OnPositiveCallback onPositiveCallback = new ThemePickerDialog.OnPositiveCallback() {
    @Override
    public void onClick(@ColorInt int backgroundColor, @ColorInt int textColor) {
        // do stuff
    }
};
```

You can also use xxxColorRes(int colorRes) methods instead of all methods described above to provide color resource instead of int color variable.

For example:
```
.initBackgroundColorRes(colorRes)
```

For details take a look at sample application.

Colors explanations.
![](https://raw.githubusercontent.com/nicolausYes/ThemePicker/master/screenshots/colors.png)


Saving state
-----

The dialog is able to save state and change its layout either activity is recreating or not (android:configChanges="..." in AndroidManifest.xml) on configuration changes.

You have to take care only about callbacks when activity is recreating. There are two ways to do it.

1. If your activity is implementing dialog's callbacks, you don't have to carry about anything. During dialog recreation it will try to cast attached activity to a listener.

```
public class MainActivity extends AppCompatActivity implements ThemePickerDialog.OnPositiveCallback {
```

2. If you are setting callbacks like this:     

```
ThemePickerDialogFragment themePickerDialog = ThemePickerDialogFragment.newInstance(
        new ThemePickerDialog.Builder(MainActivity.this)
                // ...
                .onPositive(onPositiveCallback)
                .onNegative(onNegativeCallback)
);
```

Then you have to re-set them in onCreate method if dialog is showing:

```
@Override
protected void onCreate(Bundle savedInstanceState) {
    // ...
    if (savedInstanceState != null) {
        ThemePickerDialogFragment themePickerDialogFragment = (ThemePickerDialogFragment) getFragmentManager()
                .findFragmentByTag(TAG);
        if (themePickerDialogFragment != null)
            themePickerDialogFragment.onPositive(onPositiveCallback);
    } 
}
```

For details take a look at sample application.

TODO
-----
* Make showing default themes optional and add a possibility to customize them.


Gradle
------
```
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    compile 'com.github.nicolausYes:ThemePicker:0.1'
}

```

Used libraries
-----

* [CircleView](https://github.com/nicolausYes/CircleView)
* [ColorPickerView](https://github.com/nicolausYes/color-picker-view)
* [SmartTabLayout](https://github.com/ogaclejapan/SmartTabLayout)
