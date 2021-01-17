
package com.production.advangenote.extensions;

import com.production.advangenote.async.bus.PushbulletReplyEvent;
import com.pushbullet.android.extension.MessagingExtension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.greenrobot.event.EventBus;


public class PushBulletExtension extends MessagingExtension {

  private static final String TAG = "PushBulletExtension";
  private  static final Logger logger = LoggerFactory.getLogger("PushBulletExtension.class");

  @Override
  protected void onMessageReceived(final String conversationIden, final String message) {
    logger
        .info("Pushbullet MessagingExtension: onMessageReceived(" + conversationIden + ", " + message
            + ")");
    EventBus.getDefault().post(new PushbulletReplyEvent(message));
//        MainActivity runningMainActivity = MainActivity.getInstance();
//        if (runningMainActivity != null && !runningMainActivity.isFinishing()) {
//            runningMainActivity.onPushBulletReply(message);
//        }
  }


  @Override
  protected void onConversationDismissed(final String conversationIden) {
    logger
        .info("Pushbullet MessagingExtension: onConversationDismissed(" + conversationIden + ")");
  }
}
