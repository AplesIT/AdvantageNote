
package com.production.advangenote.async.bus;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;

public class SwitchFragmentEvent {
  public static final Logger logger = LoggerFactory.getLogger("SwitchFragmentEvent.class");
  public enum Direction {
    CHILDREN, PARENT
  }

  @Getter
  @Setter
  private Direction direction;

  public SwitchFragmentEvent(Direction direction) {
    logger.info(this.getClass().getName());
    this.direction = direction;
  }
}
