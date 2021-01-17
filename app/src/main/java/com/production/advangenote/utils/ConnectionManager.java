
package com.production.advangenote.utils;

import android.content.Context;
import android.net.ConnectivityManager;


public class ConnectionManager {

  private ConnectionManager() {
    // hides public constructor
  }

  /**
   * Checks for available internet connection
   */
  public static boolean internetAvailable(Context ctx) {
    ConnectivityManager conMgr = (ConnectivityManager) ctx
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    if (conMgr.getActiveNetworkInfo() != null) {
      return conMgr.getActiveNetworkInfo().isConnected();
    }
    return false;
  }
}
