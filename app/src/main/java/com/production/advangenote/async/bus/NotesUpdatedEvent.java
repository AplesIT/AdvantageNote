
package com.production.advangenote.async.bus;

import com.production.advangenote.models.Note;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import lombok.Getter;
import lombok.Setter;


public class NotesUpdatedEvent {
  public static final Logger logger = LoggerFactory.getLogger("NotesUpdatedEvent.class");
  @Getter
  @Setter
  private List<Note> notes;


  public NotesUpdatedEvent(List<Note> notes) {
    logger.info(this.getClass().getName());
    this.notes = notes;
  }
}
