package com.production.advangenote.helpers;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import lombok.experimental.UtilityClass;

import static com.production.advangenote.utils.Constants.PREFS_NAME;
import static com.production.advangenote.utils.ConstantsBase.PREF_CURRENT_APP_VERSION;

/**
 * @author vietnh
 * @name AppVersionHelper
 * @date 10/30/20
 **/
@UtilityClass
public class AppVersionHelper {

    public static boolean isAppUpdated(Context context) throws PackageManager.NameNotFoundException {
        int currentAppVersion = getCurrentAppVersion(context);
        int savedAppVersion = getAppVersionFromPreferences(context);
        return currentAppVersion > savedAppVersion;
    }

    public static int getAppVersionFromPreferences(Context context)
            throws PackageManager.NameNotFoundException {
        try {
            return context.getSharedPreferences(PREFS_NAME,
                    Context.MODE_MULTI_PROCESS).getInt(PREF_CURRENT_APP_VERSION, 1);
        } catch (ClassCastException e) {
            return getCurrentAppVersion(context) - 1;
        }
    }

    public static void updateAppVersionInPreferences(Context context)
            throws PackageManager.NameNotFoundException {
        context.getSharedPreferences(PREFS_NAME,
                Context.MODE_MULTI_PROCESS).edit()
                .putInt(PREF_CURRENT_APP_VERSION, getCurrentAppVersion(context)).apply();
    }

    public static int getCurrentAppVersion(Context context)
            throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
    }

    public static String getCurrentAppVersionName(Context context)
            throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        return pInfo.versionName;
    }

}
