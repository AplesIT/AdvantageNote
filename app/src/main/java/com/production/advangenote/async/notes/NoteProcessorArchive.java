
package com.production.advangenote.async.notes;

import com.production.advangenote.database.DAOSQL;
import com.production.advangenote.models.Note;

import java.util.List;




public class NoteProcessorArchive extends  NoteProcessor {

  boolean archive;


  public NoteProcessorArchive(List<Note> notes, boolean archive) {
    super(notes);
    this.archive = archive;
  }


  @Override
  protected void processNote(Note note) {
   DAOSQL.getInstance().archiveNote(note, archive);
  }
}
