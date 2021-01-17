
package com.production.advangenote.async.bus;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavigationUpdatedEvent {

  private static final Logger logger = LoggerFactory.getLogger("NavigationUpdatedEvent.class");
  public final Object navigationItem;

  public NavigationUpdatedEvent(Object navigationItem) {
    logger.info(this.getClass().getName());
    this.navigationItem = navigationItem;
  }

}
