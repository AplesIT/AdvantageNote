package com.production.advangenote;
/*@name BaseActivity
@author vietnh
@date 12/20/20 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.ViewConfiguration;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.production.advangenote.helpers.LanguageHelper;
import com.production.advangenote.models.Note;
import com.production.advangenote.models.PasswordValidator;
import com.production.advangenote.utils.Navigation;
import com.production.advangenote.utils.PasswordHelper;
import com.production.advangenote.widget.ListWidgetProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static com.production.advangenote.utils.Constants.PREFS_NAME;
import static com.production.advangenote.utils.ConstantsBase.INTENT_UPDATE_DASHCLOCK;
import static com.production.advangenote.utils.ConstantsBase.PREF_NAVIGATION;

public class BaseActivity extends AppCompatActivity {
    protected static final int TRANSITION_VERTICAL = 0;
    protected static final int TRANSITION_HORIZONTAL = 1;
    protected static final Logger logger = LoggerFactory.getLogger("BaseActivity.class");
    protected SharedPreferences sharedPreferences;

    protected String navigation;
    protected String navigationtemp;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = LanguageHelper.updateLanguage(newBase, null);
        super.attachBaseContext(context);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_MULTI_PROCESS);
        // Forces menu overflow icon
        try {
            ViewConfiguration config = ViewConfiguration.get(this.getApplicationContext());
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
           logger.error("Just a little issue in physical menu button management {}", ex);
        }
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void onResume() {
        super.onResume();
        String navNotes = getResources().getStringArray(R.array.navigation_list_codes)[0];
        navigation = sharedPreferences.getString(PREF_NAVIGATION, navNotes);
        logger.info(sharedPreferences.getAll().toString());
    }
    protected void showToast(CharSequence text, int duration) {
        if (sharedPreferences.getBoolean("settings_enable_info", true)) {
            Toast.makeText(getApplicationContext(), text, duration).show();
        }
    }

    public void requestPassword(final Activity mActivity, List<Note> notes,
                                final PasswordValidator mPasswordValidator) {
        if (sharedPreferences.getBoolean("settings_password_access", false)) {
            mPasswordValidator.onPasswordValidated(PasswordValidator.Result.SUCCEED);
            return;
        }

        boolean askForPassword = false;
        for (Note note : notes) {
            if (note.isLocked()) {
                askForPassword = true;
                break;
            }
        }
        if (askForPassword) {
            PasswordHelper.requestPassword(mActivity, mPasswordValidator);
        } else {
            mPasswordValidator.onPasswordValidated(PasswordValidator.Result.SUCCEED);
        }
    }
    public boolean updateNavigation(String nav) {
        if (nav.equals(navigationtemp) || (navigationtemp == null && Navigation.getNavigationText()
                .equals(nav))) {
            return false;
        }
        sharedPreferences.edit().putString(PREF_NAVIGATION, nav).apply();
        navigation = nav;
        navigationtemp = null;
        return true;
    }
    public static void notifyAppWidgets(Context context) {

        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        int[] ids = mgr.getAppWidgetIds(new ComponentName(context, ListWidgetProvider.class));
        logger.info("Notifies AppWidget data changed for widgets " + Arrays.toString(ids));
        mgr.notifyAppWidgetViewDataChanged(ids, R.id.widget_list);


        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(INTENT_UPDATE_DASHCLOCK));
    }
    @SuppressLint("InlinedApi")
    protected void animateTransition(FragmentTransaction transaction, int direction) {
        if (direction == TRANSITION_HORIZONTAL) {
            transaction.setCustomAnimations(R.anim.fade_in_support, R.anim.fade_out_support,
                    R.anim.fade_in_support, R.anim.fade_out_support);
        }
        if (direction == TRANSITION_VERTICAL) {
            transaction.setCustomAnimations(
                    R.anim.anim_in, R.anim.anim_out, R.anim.anim_in_pop, R.anim.anim_out_pop);
        }
    }
    protected void setActionBarTitle(String title) {
        // Creating a spannable to support custom fonts on ActionBar
        int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "ID", "android");
        android.widget.TextView actionBarTitleView = getWindow().findViewById(actionBarTitle);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        if (actionBarTitleView != null) {
            actionBarTitleView.setTypeface(font);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }
    public String getNavigationTmp() {
        return navigationtemp;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_MENU || super.onKeyDown(keyCode, event);
    }
}
