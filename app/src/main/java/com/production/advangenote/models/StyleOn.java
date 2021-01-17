package com.production.advangenote.models;

import android.view.Gravity;
import android.widget.LinearLayout;

import com.production.advangenote.R;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * @author vietnh
 * @name StyleOn
 * @date 1/15/21
 **/
public class StyleOn {
    public StyleOn() {
    }
    public static final int DURATION_INFINITE = -1;
    public static final int DURATION_SHORT = 950;
    public static final int DURATION_MEDIUM = 1650;
    public static final int DURATION_LONG = 2300;

    public static final Configuration CONFIGURATION;
    public static final Style ALERT;
    public static final Style WARN;
    public static final Style CONFIRM;
    public static final Style INFO;

    public static final int ALERT_COLOR = R.color.alert;
    public static final int WARN_COLOR = R.color.warning;
    public static final int CONFIRM_COLOR = R.color.confirm;
    public static final int INFO_COLOR = R.color.info;


    static {
        CONFIGURATION = new Configuration.Builder()
                .setDuration(DURATION_SHORT)
                .setInAnimation(R.animator.fade_in_support)
                .setOutAnimation(R.animator.fade_out_support)
                .build();
        ALERT = new Style.Builder()
                .setBackgroundColor(ALERT_COLOR)
                .setHeight(LinearLayout.LayoutParams.MATCH_PARENT)
                .setGravity(Gravity.CENTER)
                .setTextAppearance(R.style.crouton_text)
                .setConfiguration(CONFIGURATION)
                .build();
        WARN = new Style.Builder()
                .setBackgroundColor(WARN_COLOR)
                .setHeight(LinearLayout.LayoutParams.MATCH_PARENT)
                .setGravity(Gravity.CENTER)
                .setTextAppearance(R.style.crouton_text)
                .setConfiguration(CONFIGURATION)
                .build();
        CONFIRM = new Style.Builder()
                .setBackgroundColor(CONFIRM_COLOR)
                .setHeight(LinearLayout.LayoutParams.MATCH_PARENT)
                .setGravity(Gravity.CENTER)
                .setTextAppearance(R.style.crouton_text)
                .setConfiguration(CONFIGURATION)
                .build();
        INFO = new Style.Builder()
                .setBackgroundColor(INFO_COLOR)
                .setHeight(LinearLayout.LayoutParams.MATCH_PARENT)
                .setGravity(Gravity.CENTER)
                .setTextAppearance(R.style.crouton_text)
                .setConfiguration(CONFIGURATION)
                .build();
    }
}
