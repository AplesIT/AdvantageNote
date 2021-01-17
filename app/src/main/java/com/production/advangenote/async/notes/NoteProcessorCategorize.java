
package com.production.advangenote.async.notes;

import com.production.advangenote.database.DAOSQL;
import com.production.advangenote.models.Category;
import com.production.advangenote.models.Note;

import java.util.List;


public class NoteProcessorCategorize extends  NoteProcessor {

  Category category;


  public NoteProcessorCategorize(List<Note> notes, Category category) {
    super(notes);
    this.category = category;
  }


  @Override
  protected void processNote(Note note) {
    note.setCategory(category);
    DAOSQL.getInstance().updateNote(note, false);
  }
}
