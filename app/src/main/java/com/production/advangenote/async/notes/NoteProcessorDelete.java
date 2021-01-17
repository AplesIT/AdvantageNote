package com.production.advangenote.async.notes;

import com.production.advangenote.AdvantageNotes;
import com.production.advangenote.async.bus.NotesDeletedEvent;
import com.production.advangenote.database.DAOSQL;
import com.production.advangenote.models.Attachment;
import com.production.advangenote.models.Note;
import com.production.advangenote.utils.StorageHelper;

import java.util.List;

import de.greenrobot.event.EventBus;




public class NoteProcessorDelete extends NoteProcessor {


  private final boolean keepAttachments;


  public NoteProcessorDelete(List<Note> notes) {
    this(notes, false);
  }


  public NoteProcessorDelete(List<Note> notes, boolean keepAttachments) {
    super(notes);
    this.keepAttachments = keepAttachments;
  }


  @Override
  protected void processNote(Note note) {
    DAOSQL db = DAOSQL.getInstance();
    if (db.deleteNote(note) && !keepAttachments) {
      for (Attachment mAttachment : note.getAttachmentsList()) {
        StorageHelper.deleteExternalStoragePrivateFile(AdvantageNotes.getAppContext(), mAttachment.getUri()
                .getLastPathSegment());
      }
    }
  }


  @Override
  protected void afterProcess(List<Note> notes) {
    EventBus.getDefault().post(new NotesDeletedEvent(notes));
  }

}
