
package com.production.advangenote.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.Spanned;

import com.production.advangenote.R;
import com.production.advangenote.SnoozeActivity;
import com.production.advangenote.database.DAOSQL;
import com.production.advangenote.helpers.IntentHelper;
import com.production.advangenote.helpers.notifications.NotificationChannels;
import com.production.advangenote.helpers.notifications.NotificationsHelper;
import com.production.advangenote.models.Attachment;
import com.production.advangenote.models.Note;
import com.production.advangenote.services.NotificationListener;
import com.production.advangenote.utils.BitmapHelper;
import com.production.advangenote.utils.ParcelableUtil;
import com.production.advangenote.utils.TextHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static android.content.Context.MODE_MULTI_PROCESS;
import static com.production.advangenote.utils.Constants.PREFS_NAME;
import static com.production.advangenote.utils.ConstantsBase.ACTION_POSTPONE;
import static com.production.advangenote.utils.ConstantsBase.ACTION_SNOOZE;
import static com.production.advangenote.utils.ConstantsBase.INTENT_NOTE;
import static com.production.advangenote.utils.ConstantsBase.MIME_TYPE_FILES;


public class AlarmReceiver extends BroadcastReceiver {
  private static Logger logger = LoggerFactory.getLogger("AlarmReceiver.class");
  @Override
  public void onReceive(Context mContext, Intent intent) {
    try {
      if (intent.hasExtra(INTENT_NOTE)) {
        Note note = ParcelableUtil.unmarshall(intent.getExtras().getByteArray(INTENT_NOTE), Note
            .CREATOR);
        createNotification(mContext, note);
        SnoozeActivity.setNextRecurrentReminder(note);
        updateNote(note);
      }
    } catch (Exception e) {
      logger.error("Error on receiving reminder", e);
    }
  }

  private void updateNote(Note note) {
    note.setArchived(false);
    if (!NotificationListener.isRunning()) {
      note.setReminderFired(true);
    }
    DAOSQL.getInstance().updateNote(note, false);
  }

  private void createNotification(Context mContext, Note note) {
    SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, MODE_MULTI_PROCESS);

    PendingIntent piSnooze = IntentHelper
        .getNotePendingIntent(mContext, SnoozeActivity.class, ACTION_SNOOZE, note);
    PendingIntent piPostpone = IntentHelper
        .getNotePendingIntent(mContext, SnoozeActivity.class, ACTION_POSTPONE, note);
    PendingIntent notifyIntent = IntentHelper
        .getNotePendingIntent(mContext, SnoozeActivity.class, null, note);

    Spanned[] titleAndContent = TextHelper.parseTitleAndContent(mContext, note);
    String title = TextHelper.getAlternativeTitle(mContext, note, titleAndContent[0]);
    String text = titleAndContent[1].toString();

    NotificationsHelper notificationsHelper = new NotificationsHelper(mContext);
    notificationsHelper.createStandardNotification(NotificationChannels.NotificationChannelNames.REMINDERS,
        R.drawable.ic_stat_notification,
        title, notifyIntent).setLedActive().setMessage(text);

    List<Attachment> attachments = note.getAttachmentsList();
    if (!attachments.isEmpty() && !attachments.get(0).getMime_type().equals(MIME_TYPE_FILES)) {
      Bitmap notificationIcon = BitmapHelper
          .getBitmapFromAttachment(mContext, note.getAttachmentsList().get(0), 128,
              128);
      notificationsHelper.setLargeIcon(notificationIcon);
    }

    String snoozeDelay = mContext.getSharedPreferences(PREFS_NAME, MODE_MULTI_PROCESS).getString(
        "settings_notification_snooze_delay", "10");

    notificationsHelper.getBuilder()
        .addAction(R.drawable.ic_material_reminder_time_light,
            TextHelper.capitalize(mContext.getString(R.string.snooze)) + ": " + snoozeDelay,
            piSnooze)
        .addAction(R.drawable.ic_remind_later_light,
            TextHelper.capitalize(mContext.getString(R.string
                .add_reminder)), piPostpone);

    setRingtone(prefs, notificationsHelper);
    setVibrate(prefs, notificationsHelper);

    notificationsHelper.show(note.get_id());
  }


  private void setRingtone(SharedPreferences prefs, NotificationsHelper notificationsHelper) {
    String ringtone = prefs.getString("settings_notification_ringtone", null);
    notificationsHelper.setRingtone(ringtone);
  }


  private void setVibrate(SharedPreferences prefs, NotificationsHelper notificationsHelper) {
    if (prefs.getBoolean("settings_notification_vibration", true)) {
      notificationsHelper.setVibration();
    }
  }

}
