
package com.production.advangenote.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.production.advangenote.async.AlarmRestoreOnRebootService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BootCompleteReceiver extends BroadcastReceiver {
  private final static Logger logger = LoggerFactory.getLogger("BootCompleteReceiver.class");

  @Override
  public void onReceive(Context ctx, Intent intent) {
    logger.info("System rebooted: refreshing reminders");
    if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
      AlarmRestoreOnRebootService.enqueueWork(ctx, intent);
    }
  }


}
