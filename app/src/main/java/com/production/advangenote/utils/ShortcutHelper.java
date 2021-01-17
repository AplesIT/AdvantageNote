
package com.production.advangenote.utils;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;

import com.production.advangenote.AdvantageNotes;
import com.production.advangenote.MainActivity;
import com.production.advangenote.R;
import com.production.advangenote.helpers.date.DateHelper;
import com.production.advangenote.models.Note;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import static com.production.advangenote.utils.ConstantsBase.ACTION_SHORTCUT;
import static com.production.advangenote.utils.ConstantsBase.INTENT_KEY;
import static com.production.advangenote.utils.ConstantsBase.PREF_PRETTIFIED_DATES;
import static java.util.Collections.singletonList;


@UtilityClass
public class ShortcutHelper {

  /**
   * Adding shortcut on Home screen
   */
  public static void addShortcut(Context context, Note note) {
    String shortcutTitle =
        note.getTitle().length() > 0 ? note.getTitle() : DateHelper.getFormattedDate(note
                .getCreation(),
            AdvantageNotes.getSharedPreferences().getBoolean(PREF_PRETTIFIED_DATES, true));

    if (Build.VERSION.SDK_INT < 26) {
      createShortcutPreOreo(context, note, shortcutTitle);
    } else {
      createShortcutPostOreo(context, note, shortcutTitle);
    }
  }

  @TargetApi(VERSION_CODES.O)
  private static void createShortcutPostOreo(Context context, Note note, String shortcutTitle) {
    ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

    if (shortcutManager.isRequestPinShortcutSupported()) {
      Uri uri = new Uri.Builder().scheme("app")
          .authority("advangtagenote")
          .appendQueryParameter("id", String.valueOf(note.get_id()))
          .build();
      Intent intent = new Intent(Intent.ACTION_VIEW, uri);

      ShortcutInfo pinShortcutInfo = new ShortcutInfo
          .Builder(context, String.valueOf(note.get_id()))
          .setIcon(createShortcutIcon(context, note))
          .setIntent(intent)
          .setShortLabel(shortcutTitle)
          .build();
      Intent pinnedShortcutCallbackIntent = shortcutManager
          .createShortcutResultIntent(pinShortcutInfo);
      //Get notified when a shortcut is pinned successfully//
      PendingIntent successCallback = PendingIntent
          .getBroadcast(context, 0, pinnedShortcutCallbackIntent, 0);
      shortcutManager.requestPinShortcut(pinShortcutInfo, successCallback.getIntentSender()
      );
    }
  }

  @SneakyThrows
  @TargetApi(VERSION_CODES.O)
  private static Icon createShortcutIcon(Context context, Note note) {
    if (note.getAttachmentsList().isEmpty()) {
      return Icon.createWithResource(context, R.drawable.ic_shortcut);
    } else {
      return Icon.createWithBitmap(
          BitmapHelper.getBitmapFromAttachment(context, note.getAttachmentsList().get(0), 64, 64));
    }
  }

  private static void createShortcutPreOreo(Context context, Note note, String shortcutTitle) {
    Intent shortcutIntent = new Intent(context, MainActivity.class);
    shortcutIntent.putExtra(INTENT_KEY, note.get_id());
    shortcutIntent.setAction(ACTION_SHORTCUT);

    Intent addIntent = new Intent();
    addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
    addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutTitle);
    addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
        Intent.ShortcutIconResource.fromContext(context, R.drawable.ic_shortcut));
    addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

    context.sendBroadcast(addIntent);
  }

  /**
   * Removes note shortcut from home launcher
   */
  public static void removeShortcut(Context context, Note note) {
    if (Build.VERSION.SDK_INT < 26) {
      removeShortcutPreOreo(context, note);
    } else {
      removeShortcutPostOreo(context, note);
    }
  }

  @TargetApi(VERSION_CODES.O)
  private static void removeShortcutPostOreo(Context context, Note note) {
    ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
    shortcutManager.getPinnedShortcuts().stream()
        .filter(ps -> ps.getId().equals(String.valueOf(note.get_id())))
        .forEach(ps -> shortcutManager.disableShortcuts(singletonList(ps.getId()),
            context.getString(R.string.shortcut_disabled)));
  }

  private static void removeShortcutPreOreo(Context context, Note note) {
    Intent shortcutIntent = new Intent(context, MainActivity.class);
    shortcutIntent.putExtra(INTENT_KEY, note.get_id());
    shortcutIntent.setAction(ACTION_SHORTCUT);

    Intent addIntent = new Intent();
    addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
    String shortcutTitle =
        note.getTitle().length() > 0 ? note.getTitle() : DateHelper.getFormattedDate(note
                .getCreation(),
            AdvantageNotes.getSharedPreferences().getBoolean(PREF_PRETTIFIED_DATES, true));

    addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutTitle);

    addIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
    context.sendBroadcast(addIntent);
  }

}
