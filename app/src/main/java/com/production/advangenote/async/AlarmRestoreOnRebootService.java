 
package com.production.advangenote.async;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.production.advangenote.AdvantageNotes;
import com.production.advangenote.BaseActivity;
import com.production.advangenote.database.DAOSQL;
import com.production.advangenote.models.Note;
import com.production.advangenote.utils.ReminderHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Verify version code and add wake lock in manifest is important to avoid crash
 */
public class AlarmRestoreOnRebootService extends JobIntentService {
  private final static Logger logger = LoggerFactory.getLogger("AlarmRestoreOnRebootService.class");

  public static final int JOB_ID = 0x01;

  public static void enqueueWork(Context context, Intent work) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      enqueueWork(context, com.production.advangenote.async.AlarmRestoreOnRebootService.class, JOB_ID, work);
    } else {
      Intent jobIntent = new Intent(context, com.production.advangenote.async.AlarmRestoreOnRebootService.class);
      context.startService(jobIntent);
    }
  }

  @Override
  protected void onHandleWork(@NonNull Intent intent) {
   logger.info("System rebooted: service refreshing reminders");
    Context mContext = getApplicationContext();

    BaseActivity.notifyAppWidgets(mContext);

    List<Note> notes = DAOSQL.getInstance().getNotesWithReminderNotFired();
    logger.info("Found " + notes.size() + " reminders");
    for (Note note : notes) {
      ReminderHelper.addReminder(AdvantageNotes.getAppContext(), note);
    }
  }

}
