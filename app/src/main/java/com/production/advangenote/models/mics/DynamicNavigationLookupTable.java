

package com.production.advangenote.models.mics;


import com.production.advangenote.async.bus.DynamicNavigationReadyEvent;
import com.production.advangenote.async.bus.NotesUpdatedEvent;
import com.production.advangenote.database.DAOSQL;
import com.production.advangenote.models.Note;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import de.greenrobot.event.EventBus;


public class DynamicNavigationLookupTable {

  private static  DynamicNavigationLookupTable instance;
  private  final static Logger logger = LoggerFactory.getLogger("DynamicNavigationLookupTable.class");
  int archived;
  int trashed;
  int uncategorized;
  int reminders;


  private DynamicNavigationLookupTable() {
    EventBus.getDefault().register(this);
    update();
  }


  public static  DynamicNavigationLookupTable getInstance() {
    if (instance == null) {
      instance = new  DynamicNavigationLookupTable();
    }
    return instance;
  }


  public void update() {
    ((Runnable) () -> {
      archived = trashed = uncategorized = reminders = 0;
      List<Note> notes = DAOSQL.getInstance().getAllNotes(false);
      for (int i = 0; i < notes.size(); i++) {
        if (notes.get(i).isTrashed()) {
          trashed++;
        } else if (notes.get(i).isArchived()) {
          archived++;
        } else if (notes.get(i).getAlarm() != null) {
          reminders++;
        }
        if (notes.get(i).getCategory() == null || notes.get(i).getCategory().getId().equals(0L)) {
          uncategorized++;
        }
      }
      EventBus.getDefault().post(new DynamicNavigationReadyEvent());
      logger.info("Dynamic menu finished counting items");
    }).run();
  }


  public void onEventAsync(NotesUpdatedEvent event) {
    update();
  }


  public int getArchived() {
    return archived;
  }


  public int getTrashed() {
    return trashed;
  }


  public int getReminders() {
    return reminders;
  }


  public int getUncategorized() {
    return uncategorized;
  }

}
