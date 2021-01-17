
package com.production.advangenote.async.notes;

import com.production.advangenote.AdvantageNotes;
import com.production.advangenote.database.DAOSQL;
import com.production.advangenote.models.Note;
import com.production.advangenote.utils.ReminderHelper;
import com.production.advangenote.utils.ShortcutHelper;

import java.util.List;



public class NoteProcessorTrash extends NoteProcessor {

  boolean trash;


  public NoteProcessorTrash(List<Note> notes, boolean trash) {
    super(notes);
    this.trash = trash;
  }


  @Override
  protected void processNote(Note note) {
    if (trash) {
      ShortcutHelper.removeShortcut(AdvantageNotes.getAppContext(), note);
      ReminderHelper.removeReminder(AdvantageNotes.getAppContext(), note);
    } else {
      ReminderHelper.addReminder(AdvantageNotes.getAppContext(), note);
    }
    DAOSQL.getInstance().trashNote(note, trash);
  }
}
