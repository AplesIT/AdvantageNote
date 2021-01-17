
package com.production.advangenote.utils;

import android.content.Context;
import android.content.pm.PackageManager;


public class MiscUtils {

  private MiscUtils() {
    // hides public constructor
  }



  public static boolean isGooglePlayAvailable(Context context) {
    try {
      context.getPackageManager().getPackageInfo("com.android.vending", 0);
      return true;
    } catch (PackageManager.NameNotFoundException e) {
      return false;
    }
  }

}