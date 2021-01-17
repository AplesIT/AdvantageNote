
package com.production.advangenote.widget;


import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.RemoteViews;

import com.production.advangenote.MainActivity;
import com.production.advangenote.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.production.advangenote.utils.ConstantsBase.ACTION_WIDGET;
import static com.production.advangenote.utils.ConstantsBase.ACTION_WIDGET_SHOW_LIST;
import static com.production.advangenote.utils.ConstantsBase.ACTION_WIDGET_TAKE_PHOTO;
import static com.production.advangenote.utils.ConstantsBase.INTENT_WIDGET;


public abstract class WidgetProvider extends AppWidgetProvider {

  public static final String EXTRA_WORD = "com.production.advangenote.widget.WORD";
  public static final String TOAST_ACTION = "com.production.advangenote.widget.NOTE";
  public static final String EXTRA_ITEM = "com.production.advangenote.widget.EXTRA_FIELD";
  protected static final Logger logger = LoggerFactory.getLogger("WidgetProvider.class");

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    // Get all ids

    ComponentName thisWidget = new ComponentName(context, getClass());
    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
    for (int appWidgetId : allWidgetIds) {
      logger.info("WidgetProvider onUpdate() widget " + appWidgetId);
      // Get the layout for and attach an on-click listener to views
      setLayout(context, appWidgetManager, appWidgetId);
    }
    super.onUpdate(context, appWidgetManager, appWidgetIds);
  }


  @Override
  public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId,
                                        Bundle newOptions) {
    logger.info("Widget size changed");
    setLayout(context, appWidgetManager, appWidgetId);
  }


  private void setLayout(Context context, AppWidgetManager appWidgetManager, int widgetId) {

    // Create an Intent to launch DetailActivity
    Intent intentDetail = new Intent(context, MainActivity.class);
    intentDetail.setAction(ACTION_WIDGET);
    intentDetail.putExtra(INTENT_WIDGET, widgetId);
    @SuppressLint("WrongConstant") PendingIntent pendingIntentDetail = PendingIntent
        .getActivity(context, widgetId, intentDetail, FLAG_ACTIVITY_NEW_TASK);

    // Create an Intent to launch ListActivity
    Intent intentList = new Intent(context, MainActivity.class);
    intentList.setAction(ACTION_WIDGET_SHOW_LIST);
    intentList.putExtra(INTENT_WIDGET, widgetId);
    @SuppressLint("WrongConstant") PendingIntent pendingIntentList = PendingIntent
        .getActivity(context, widgetId, intentList, FLAG_ACTIVITY_NEW_TASK);

    // Create an Intent to launch DetailActivity to take a photo
    Intent intentDetailPhoto = new Intent(context, MainActivity.class);
    intentDetailPhoto.setAction(ACTION_WIDGET_TAKE_PHOTO);
    intentDetailPhoto.putExtra(INTENT_WIDGET, widgetId);
    @SuppressLint("WrongConstant") PendingIntent pendingIntentDetailPhoto = PendingIntent
        .getActivity(context, widgetId, intentDetailPhoto,
            FLAG_ACTIVITY_NEW_TASK);

    // Check various dimensions aspect of widget to choose between layouts
    boolean isSmall = false;
    boolean isSingleLine = true;
    Bundle options = appWidgetManager.getAppWidgetOptions(widgetId);
    // Width check
    isSmall = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) < 110;
    // Height check
    isSingleLine = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) < 110;

    // Creation of a map to associate PendingIntent(s) to views
    SparseArray<PendingIntent> map = new SparseArray<>();
    map.put(R.id.list, pendingIntentList);
    map.put(R.id.add, pendingIntentDetail);
    map.put(R.id.camera, pendingIntentDetailPhoto);

    RemoteViews views = getRemoteViews(context, widgetId, isSmall, isSingleLine, map);

    // Tell the AppWidgetManager to perform an update on the current app widget
    appWidgetManager.updateAppWidget(widgetId, views);
  }


  abstract protected RemoteViews getRemoteViews(Context context, int widgetId, boolean isSmall,
                                                boolean isSingleLine,
                                                SparseArray<PendingIntent> pendingIntentsMap);

}
