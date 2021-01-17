
package com.production.advangenote.async.bus;

import android.service.notification.StatusBarNotification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;


public class NotificationRemovedEvent {
  public static final Logger logger = LoggerFactory.getLogger("NotesUpdatedEvent.class");
  @Getter
  @Setter
  private StatusBarNotification statusBarNotification;

  public NotificationRemovedEvent(StatusBarNotification statusBarNotification) {
    logger.info(this.getClass().getName());
    this.statusBarNotification = statusBarNotification;
  }
}
