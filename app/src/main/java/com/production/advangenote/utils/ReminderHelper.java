
package com.production.advangenote.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.production.advangenote.AdvantageNotes;
import com.production.advangenote.R;
import com.production.advangenote.helpers.date.DateHelper;
import com.production.advangenote.models.Note;
import com.production.advangenote.receiver.AlarmReceiver;
import com.production.advangenote.utils.date.DateUtils;

import java.util.Calendar;

import static com.production.advangenote.utils.ConstantsBase.INTENT_NOTE;


public class ReminderHelper {

  private ReminderHelper() {
    // hides public constructor
  }

  public static void addReminder(Context context, Note note) {
    if (note.getAlarm() != null) {
      addReminder(context, note, Long.parseLong(note.getAlarm()));
    }
  }

  public static void addReminder(Context context, Note note, long reminder) {
    if (DateUtils.isFuture(reminder)) {
      Intent intent = new Intent(context, AlarmReceiver.class);
      intent.putExtra(INTENT_NOTE, ParcelableUtil.marshall(note));
      PendingIntent sender = PendingIntent.getBroadcast(context, getRequestCode(note), intent,
          PendingIntent.FLAG_CANCEL_CURRENT);
      AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
      am.setExact(AlarmManager.RTC_WAKEUP, reminder, sender);
    }
  }

  /**
   * Checks if exists any reminder for given note
   */
  public static boolean checkReminder(Context context, Note note) {
    return
        PendingIntent.getBroadcast(context, getRequestCode(note), new Intent(context, AlarmReceiver
            .class), PendingIntent.FLAG_NO_CREATE) != null;
  }

  static int getRequestCode(Note note) {
    long longCode = note.getCreation() != null ? note.getCreation()
        : Calendar.getInstance().getTimeInMillis() / 1000L;
    return (int) longCode;
  }

  public static void removeReminder(Context context, Note note) {
    if (!TextUtils.isEmpty(note.getAlarm())) {
      AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
      Intent intent = new Intent(context, AlarmReceiver.class);
      PendingIntent p = PendingIntent.getBroadcast(context, getRequestCode(note), intent, 0);
      am.cancel(p);
      p.cancel();
    }
  }

  public static void showReminderMessage(String reminderString) {
    if (reminderString != null) {
      long reminder = Long.parseLong(reminderString);
      if (reminder > Calendar.getInstance().getTimeInMillis()) {
        new Handler(AdvantageNotes.getAppContext().getMainLooper()).post(() ->
            Toast.makeText(AdvantageNotes.getAppContext(),
                AdvantageNotes.getAppContext().getString(R.string.alarm_set_on) + " " + DateHelper
                    .getDateTimeShort
                        (AdvantageNotes.getAppContext(), reminder), Toast.LENGTH_LONG).show());
      }
    }
  }

}
