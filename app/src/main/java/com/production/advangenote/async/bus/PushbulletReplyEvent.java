
package com.production.advangenote.async.bus;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;


public class PushbulletReplyEvent {
  public static final Logger logger = LoggerFactory.getLogger("PushbulletReplyEvent.class");
  @Getter
  @Setter
  private String message;

  public PushbulletReplyEvent(String message) {
    logger.info(this.getClass().getName());
    this.message = message;
  }
}
