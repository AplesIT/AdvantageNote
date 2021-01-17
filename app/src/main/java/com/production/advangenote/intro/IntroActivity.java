
package com.production.advangenote.intro;

import android.content.Context;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.production.advangenote.AdvantageNotes;

import static com.production.advangenote.utils.Constants.PREFS_NAME;
import static com.production.advangenote.utils.ConstantsBase.PREF_TOUR_COMPLETE;


public class IntroActivity extends AppIntro2 {

  @Override
  public void init(Bundle savedInstanceState) {
    addSlide(new IntroSlide1(), getApplicationContext());
  }

  @Override
  public void onDonePressed() {
    AdvantageNotes.getAppContext().getSharedPreferences(PREFS_NAME, Context.MODE_MULTI_PROCESS).edit()
        .putBoolean(PREF_TOUR_COMPLETE, true).apply();
    finish();
  }

  public static boolean mustRun() {
    return !AdvantageNotes.isDebugBuild() && !AdvantageNotes.getAppContext().getSharedPreferences(PREFS_NAME,
        Context.MODE_MULTI_PROCESS).getBoolean(PREF_TOUR_COMPLETE, false);
  }

  @Override
  public void onBackPressed() {
    // Does nothing, you HAVE TO SEE THE INTRO!
  }

}
