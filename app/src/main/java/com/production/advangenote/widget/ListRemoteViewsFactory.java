
package com.production.advangenote.widget;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spanned;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.production.advangenote.AdvantageNotes;
import com.production.advangenote.R;
import com.production.advangenote.database.DAOSQL;
import com.production.advangenote.models.Attachment;
import com.production.advangenote.models.Note;
import com.production.advangenote.utils.BitmapHelper;
import com.production.advangenote.utils.Navigation;
import com.production.advangenote.utils.TextHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.production.advangenote.utils.Constants.PREFS_NAME;
import static com.production.advangenote.utils.ConstantsBase.INTENT_NOTE;
import static com.production.advangenote.utils.ConstantsBase.PREF_COLORS_APP_DEFAULT;
import static com.production.advangenote.utils.ConstantsBase.PREF_WIDGET_PREFIX;


public class ListRemoteViewsFactory implements RemoteViewsFactory {

  private static final String SET_BACKGROUND_COLOR = "setBackgroundColor";
  private static boolean showThumbnails = true;
  private static boolean showTimestamps = true;
  private final int WIDTH = 80;
  private final int HEIGHT = 80;
  private  AdvantageNotes app;
  private int appWidgetId;
  private List<Note> notes;
  private int navigation;

  public ListRemoteViewsFactory(Application app, Intent intent) {
    this.app = (AdvantageNotes) app;
    appWidgetId = intent
        .getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
  }
  private final static Logger logger = LoggerFactory.getLogger("ListRemoteViewsFactory.class");

  static void updateConfiguration(Context mContext, int mAppWidgetId, String sqlCondition,
                                  boolean thumbnails, boolean timestamps) {
    logger.info("Widget configuration updated");
    mContext.getSharedPreferences(PREFS_NAME, Context.MODE_MULTI_PROCESS).edit()
        .putString(PREF_WIDGET_PREFIX + mAppWidgetId, sqlCondition).apply();
    showThumbnails = thumbnails;
    showTimestamps = timestamps;
  }

  @Override
  public void onCreate() {
    logger.info("Created widget " + appWidgetId);
    String condition = app.getSharedPreferences(PREFS_NAME, Context.MODE_MULTI_PROCESS)
        .getString(
            PREF_WIDGET_PREFIX
                + appWidgetId, "");
    notes = DAOSQL.getInstance().getNotes(condition, true);
  }

  @Override
  public void onDataSetChanged() {
    logger.info("onDataSetChanged widget " + appWidgetId);
    navigation = Navigation.getNavigation();

    String condition = app.getSharedPreferences(PREFS_NAME, Context.MODE_MULTI_PROCESS)
        .getString(
            PREF_WIDGET_PREFIX
                + appWidgetId, "");
    notes = DAOSQL.getInstance().getNotes(condition, true);
  }

  @Override
  public void onDestroy() {
    app.getSharedPreferences(PREFS_NAME, Context.MODE_MULTI_PROCESS)
        .edit()
        .remove(PREF_WIDGET_PREFIX + appWidgetId)
        .apply();
  }

  @Override
  public int getCount() {
    return notes.size();
  }

  @Override
  public RemoteViews getViewAt(int position) {
    RemoteViews row = new RemoteViews(app.getPackageName(), R.layout.note_layout_widget);

    Note note = notes.get(position);

    Spanned[] titleAndContent = TextHelper.parseTitleAndContent(app, note);

    row.setTextViewText(R.id.note_title, titleAndContent[0]);
    row.setTextViewText(R.id.note_content, titleAndContent[1]);

    color(note, row);

    if (!note.isLocked() && showThumbnails && !note.getAttachmentsList().isEmpty()) {
      Attachment mAttachment = note.getAttachmentsList().get(0);
      Bitmap bmp = BitmapHelper.getBitmapFromAttachment(app, mAttachment, WIDTH, HEIGHT);
      row.setBitmap(R.id.attachmentThumbnail, "setImageBitmap", bmp);
      row.setInt(R.id.attachmentThumbnail, "setVisibility", View.VISIBLE);
    } else {
      row.setInt(R.id.attachmentThumbnail, "setVisibility", View.GONE);
    }
    if (showTimestamps) {
      row.setTextViewText(R.id.note_date, TextHelper.getDateText(app, note, navigation));
    } else {
      row.setTextViewText(R.id.note_date, "");
    }

    // Next, set a fill-intent, which will be used to fill in the pending intent template
    // that is set on the collection view in StackWidgetProvider.
    Bundle extras = new Bundle();
    extras.putParcelable(INTENT_NOTE, note);
    Intent fillInIntent = new Intent();
    fillInIntent.putExtras(extras);
    // Make it possible to distinguish the individual on-click
    // action of a given item
    row.setOnClickFillInIntent(R.id.root, fillInIntent);

    return row;
  }

  @Override
  public RemoteViews getLoadingView() {
    return null;
  }

  @Override
  public int getViewTypeCount() {
    return 1;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public boolean hasStableIds() {
    return false;
  }

  private void color(Note note, RemoteViews row) {

    String colorsPref = app.getSharedPreferences(PREFS_NAME, Context.MODE_MULTI_PROCESS)
        .getString("settings_colors_widget",
            PREF_COLORS_APP_DEFAULT);

    // Checking preference
    if (!colorsPref.equals("disabled")) {

      // Resetting transparent color to the view
      row.setInt(R.id.tag_marker, SET_BACKGROUND_COLOR, Color.parseColor("#00000000"));

      // If tag is set the color will be applied on the appropriate target
      if (note.getCategory() != null && note.getCategory().getColor() != null) {
        if (colorsPref.equals("list")) {
          row.setInt(R.id.card_layout, SET_BACKGROUND_COLOR,
              Integer.parseInt(note.getCategory().getColor()));
        } else {
          row.setInt(R.id.tag_marker, SET_BACKGROUND_COLOR,
              Integer.parseInt(note.getCategory().getColor()));
        }
      } else {
        row.setInt(R.id.tag_marker, SET_BACKGROUND_COLOR, 0);
      }
    }
  }

}
