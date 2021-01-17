
package com.production.advangenote.services;

import android.content.ContentResolver;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.production.advangenote.AdvantageNotes;
import com.production.advangenote.async.bus.NotificationRemovedEvent;
import com.production.advangenote.database.DAOSQL;
import com.production.advangenote.models.Note;
import com.production.advangenote.utils.date.DateUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.greenrobot.event.EventBus;



public class NotificationListener extends NotificationListenerService {
  private static Logger logger = LoggerFactory.getLogger("NotificationListener.class");
  @Override
  public void onCreate() {
    super.onCreate();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
  }

  @Override
  public void onNotificationPosted(StatusBarNotification sbn) {
    logger.info("Notification posted for note: " + sbn.getId());
  }

  @Override
  public void onNotificationRemoved(StatusBarNotification sbn) {
    if (getPackageName().equals(sbn.getPackageName())) {
      EventBus.getDefault().post(new NotificationRemovedEvent(sbn));
      logger.info("Notification removed for note: " + sbn.getId());
    }
  }

  public void onEventAsync(NotificationRemovedEvent event) {
    long nodeId = Long.parseLong(event.getStatusBarNotification().getTag());
    Note note = DAOSQL.getInstance().getNote(nodeId);
    if (!DateUtils.isFuture(note.getAlarm())) {
      DAOSQL.getInstance().setReminderFired(nodeId, true);
    }
  }

  public static boolean isRunning() {
    ContentResolver contentResolver = AdvantageNotes.getAppContext().getContentResolver();
    String enabledNotificationListeners = Settings.Secure
        .getString(contentResolver, "enabled_notification_listeners");
    return enabledNotificationListeners != null && enabledNotificationListeners
        .contains(NotificationListener
            .class.getSimpleName());
  }

}
