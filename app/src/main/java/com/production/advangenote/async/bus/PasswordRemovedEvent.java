
package com.production.advangenote.async.bus;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordRemovedEvent {
  public static final Logger logger = LoggerFactory.getLogger("PasswordRemovedEvent.class");
  public PasswordRemovedEvent() {
    logger.info(this.getClass().getName());
  }
}
