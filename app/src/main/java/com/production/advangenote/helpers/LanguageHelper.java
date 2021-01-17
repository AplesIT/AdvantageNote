package com.production.advangenote.helpers;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.production.advangenote.utils.Constants;

import java.util.Locale;

import static android.content.Context.MODE_MULTI_PROCESS;
import static com.production.advangenote.utils.ConstantsBase.PREF_LANG;
/**
 * @author vietnh
 * @name LanguageHelper
 * @date 12/12/21
 **/

public class LanguageHelper {
    @SuppressLint("ApplySharedPref")
    public static Context updateLanguage(Context ctx, String lang) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREFS_NAME, MODE_MULTI_PROCESS);
        String language = prefs.getString(PREF_LANG, "");

        Locale locale = null;
        if (TextUtils.isEmpty(language) && lang == null) {
            locale = Locale.getDefault();
        } else if (lang != null) {
            locale = getLocale(lang);
            prefs.edit().putString(PREF_LANG, lang).commit();
        } else if (!TextUtils.isEmpty(language)) {
            locale = getLocale(language);
        }

        return setLocale(ctx, locale);
    }

    public static Context resetSystemLanguage(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREFS_NAME, MODE_MULTI_PROCESS);
        prefs.edit().remove(PREF_LANG).apply();

        return setLocale(ctx, Locale.getDefault());
    }

    private static Context setLocale(Context context, Locale locale) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.locale = locale;
        context.getResources().updateConfiguration(configuration, null);
        return context;
    }

    /**
     * Checks country AND region
     */
    private static Locale getLocale(String lang) {
        if (lang.contains("_")) {
            return new Locale(lang.split("_")[0], lang.split("_")[1]);
        } else {
            return new Locale(lang);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @NonNull
    static String getLocalizedString(Context context, String desiredLocale, int resourceId) {
        if (desiredLocale.equals(getCurrentLocaleAsString(context))) {
            return context.getResources().getString(resourceId);
        }
        Configuration conf = context.getResources().getConfiguration();
        conf = new Configuration(conf);
        conf.setLocale(getLocale(desiredLocale));
        Context localizedContext = context.createConfigurationContext(conf);
        return localizedContext.getResources().getString(resourceId);
    }

    public static String getCurrentLocaleAsString(Context context) {
        return getCurrentLocale(context).toString();
    }

    public static Locale getCurrentLocale(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            return context.getResources().getConfiguration().locale;
        }
    }
}
