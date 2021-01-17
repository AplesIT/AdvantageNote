
package com.production.advangenote.async.notes;

import android.content.Context;
import android.os.AsyncTask;

import com.production.advangenote.AdvantageNotes;
import com.production.advangenote.database.DAOSQL;
import com.production.advangenote.models.Attachment;
import com.production.advangenote.models.Note;
import com.production.advangenote.models.listeners.OnNoteSaved;
import com.production.advangenote.utils.ReminderHelper;
import com.production.advangenote.utils.StorageHelper;
import com.production.advangenote.utils.date.DateUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;



public class SaveNoteTask extends AsyncTask<Note, Void, Note> {

  private Context context;
  private boolean updateLastModification = true;
  private OnNoteSaved mOnNoteSaved;
  private final Logger logger = LoggerFactory.getLogger("SaveNoteTask.class");

  public SaveNoteTask(boolean updateLastModification) {
    this(null, updateLastModification);
  }


  public SaveNoteTask(OnNoteSaved mOnNoteSaved, boolean updateLastModification) {
    super();
    this.context = AdvantageNotes.getAppContext();
    this.mOnNoteSaved = mOnNoteSaved;
    this.updateLastModification = updateLastModification;
  }


  @Override
  protected Note doInBackground(Note... params) {
    Note note = params[0];
    purgeRemovedAttachments(note);
    boolean reminderMustBeSet = DateUtils.isFuture(note.getAlarm());
    if (reminderMustBeSet) {
      note.setReminderFired(false);
    }
    note = DAOSQL.getInstance().updateNote(note, updateLastModification);
    if (reminderMustBeSet) {
      ReminderHelper.addReminder(context, note);
    }
    return note;
  }


  private void purgeRemovedAttachments(Note note) {
    List<Attachment> deletedAttachments = note.getAttachmentsListOld();
    for (Attachment attachment : note.getAttachmentsList()) {
      if (attachment.getId() != null) {
        // Workaround to prevent deleting attachments if instance is changed (app restart)
        if (deletedAttachments.indexOf(attachment) == -1) {
          attachment = getFixedAttachmentInstance(deletedAttachments, attachment);
        }
        deletedAttachments.remove(attachment);
      }
    }
    // Remove from database deleted attachments
    for (Attachment deletedAttachment : deletedAttachments) {
      StorageHelper.delete(context, deletedAttachment.getUri().getPath());
      logger.info("Removed attachment " + deletedAttachment.getUri());
    }
  }


  private Attachment getFixedAttachmentInstance(List<Attachment> deletedAttachments,
                                                Attachment attachment) {
    for (Attachment deletedAttachment : deletedAttachments) {
      if (deletedAttachment.getId().equals(attachment.getId())) {
        return deletedAttachment;
      }
    }
    return attachment;
  }


  @Override
  protected void onPostExecute(Note note) {
    super.onPostExecute(note);
    if (this.mOnNoteSaved != null) {
      mOnNoteSaved.onNoteSaved(note);
    }
  }
}
