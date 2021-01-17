package com.production.advangenote;/*@name AdvantageNotes
@author vietnh
@date 1/15/21 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.StrictMode;

import androidx.multidex.MultiDexApplication;

import com.production.advangenote.helpers.LanguageHelper;
import com.production.advangenote.helpers.notifications.NotificationsHelper;

import org.acra.ACRA;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraHttpSender;
import org.acra.annotation.AcraToast;
import org.acra.sender.HttpSender;

import it.feio.android.analitica.AnalyticsHelper;
import it.feio.android.analitica.AnalyticsHelperFactory;
import it.feio.android.analitica.MockAnalyticsHelper;
import it.feio.android.analitica.exceptions.AnalyticsInstantiationException;
import it.feio.android.analitica.exceptions.InvalidIdentifierException;

import static com.production.advangenote.utils.Constants.PREFS_NAME;
import static com.production.advangenote.utils.ConstantsBase.PREF_LANG;
import static com.production.advangenote.utils.ConstantsBase.PREF_SEND_ANALYTICS;
import static com.production.advangenote.utils.ConstantsBase.PROPERTIES_PARAMS_SEPARATOR;

@AcraCore(buildConfigClass = BuildConfig.class)
@AcraHttpSender(uri = BuildConfig.CRASH_REPORTING_URL,
        httpMethod = HttpSender.Method.POST)
@AcraToast(resText = R.string.crash_toast)
public class AdvantageNotes  extends MultiDexApplication{
    static SharedPreferences prefs;
    private static Context mContext;
    private AnalyticsHelper analyticsHelper;

    public static boolean isDebugBuild() {
        return BuildConfig.BUILD_TYPE.equals("debug");
    }

    public static Context getAppContext() {
        return AdvantageNotes.mContext;
    }

    /**
     * Statically returns app's default SharedPreferences instance
     *
     * @return SharedPreferences object instance
     */
    public static SharedPreferences getSharedPreferences() {
        return getAppContext().getSharedPreferences(PREFS_NAME, MODE_MULTI_PROCESS);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
        ACRA.getErrorReporter().putCustomData("TRACEPOT_DEVELOP_MODE", isDebugBuild() ? "1" : "0");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();
        prefs = getSharedPreferences(PREFS_NAME, MODE_MULTI_PROCESS);

        enableStrictMode();

        new NotificationsHelper(this).initNotificationChannels();
    }

    private void enableStrictMode() {
        if (isDebugBuild()) {
            StrictMode.enableDefaults();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        String language = prefs.getString(PREF_LANG, "");
        LanguageHelper.updateLanguage(this, language);
    }

    public AnalyticsHelper getAnalyticsHelper() {
        if (analyticsHelper == null) {
            boolean enableAnalytics = prefs.getBoolean(PREF_SEND_ANALYTICS, true);
            try {
                String[] analyticsParams = BuildConfig.ANALYTICS_PARAMS.split(PROPERTIES_PARAMS_SEPARATOR);
                analyticsHelper = new AnalyticsHelperFactory().getAnalyticsHelper(this, enableAnalytics,
                        analyticsParams);
            } catch (AnalyticsInstantiationException | InvalidIdentifierException e) {
                analyticsHelper = new MockAnalyticsHelper();
            }
        }
        return analyticsHelper;
    }
}

