 

package com.production.advangenote.helpers;

import com.production.advangenote.AdvantageNotes;
import com.production.advangenote.helpers.count.CountFactory;
import com.production.advangenote.models.Attachment;
import com.production.advangenote.models.Note;
import com.production.advangenote.models.StatsSingleNote;
import com.production.advangenote.utils.StorageHelper;
import com.production.advangenote.utils.TagsHelper;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import lombok.experimental.UtilityClass;

import static com.production.advangenote.utils.ConstantsBase.MERGED_NOTES_SEPARATOR;
import static com.production.advangenote.utils.ConstantsBase.MIME_TYPE_AUDIO;
import static com.production.advangenote.utils.ConstantsBase.MIME_TYPE_FILES;
import static com.production.advangenote.utils.ConstantsBase.MIME_TYPE_IMAGE;
import static com.production.advangenote.utils.ConstantsBase.MIME_TYPE_SKETCH;
import static com.production.advangenote.utils.ConstantsBase.MIME_TYPE_VIDEO;
import static it.feio.android.checklistview.interfaces.Constants.CHECKED_SYM;
import static it.feio.android.checklistview.interfaces.Constants.UNCHECKED_SYM;

@UtilityClass
public class NotesHelper {

  public static boolean haveSameId(Note note, Note currentNote) {
    return currentNote != null
        && currentNote.get_id() != null
        && currentNote.get_id().equals(note.get_id());

  }

  public static StringBuilder appendContent(Note note, StringBuilder content,
                                            boolean includeTitle) {
    if (content.length() > 0
        && (!StringUtils.isEmpty(note.getTitle()) || !StringUtils.isEmpty(note.getContent()))) {
      content.append(System.getProperty("line.separator"))
          .append(System.getProperty("line.separator"))
          .append(MERGED_NOTES_SEPARATOR).append(System.getProperty("line.separator"))
          .append(System.getProperty("line.separator"));
    }
    if (includeTitle && !StringUtils.isEmpty(note.getTitle())) {
      content.append(note.getTitle());
    }
    if (!StringUtils.isEmpty(note.getTitle()) && !StringUtils.isEmpty(note.getContent())) {
      content.append(System.getProperty("line.separator"))
          .append(System.getProperty("line.separator"));
    }
    if (!StringUtils.isEmpty(note.getContent())) {
      content.append(note.getContent());
    }
    return content;
  }

  public static void addAttachments(boolean keepMergedNotes, Note note,
      ArrayList<Attachment> attachments) {
    if (keepMergedNotes) {
      for (Attachment attachment : note.getAttachmentsList()) {
        attachments
            .add(StorageHelper.createAttachmentFromUri(AdvantageNotes.getAppContext(), attachment.getUri
                ()));
      }
    } else {
      attachments.addAll(note.getAttachmentsList());
    }
  }

  public static Note mergeNotes(List<Note> notes, boolean keepMergedNotes) {
    boolean locked = false;
    ArrayList<Attachment> attachments = new ArrayList<>();
    String reminder = null;
    String reminderRecurrenceRule = null;
    Double latitude = null;
    Double longitude = null;

    Note mergedNote = new Note();
    mergedNote.setTitle(notes.get(0).getTitle());
    mergedNote.setArchived(notes.get(0).isArchived());
    mergedNote.setCategory(notes.get(0).getCategory());
    StringBuilder content = new StringBuilder();
    // Just first note title must not be included into the content
    boolean includeTitle = false;

    for (Note note : notes) {
      appendContent(note, content, includeTitle);
      locked = locked || note.isLocked();
      String currentReminder = note.getAlarm();
      if (!StringUtils.isEmpty(currentReminder) && reminder == null) {
        reminder = currentReminder;
        reminderRecurrenceRule = note.getRecurrenceRule();
      }
      latitude = ObjectUtils.defaultIfNull(latitude, note.getLatitude());
      longitude = ObjectUtils.defaultIfNull(longitude, note.getLongitude());
      addAttachments(keepMergedNotes, note, attachments);
      includeTitle = true;
    }

    mergedNote.setContent(content.toString());
    mergedNote.setLocked(locked);
    mergedNote.setAlarm(reminder);
    mergedNote.setRecurrenceRule(reminderRecurrenceRule);
    mergedNote.setLatitude(latitude);
    mergedNote.setLongitude(longitude);
    mergedNote.setAttachmentsList(attachments);

    return mergedNote;
  }

  /**
   * Retrieves statistics data for a single note
   */
  public static StatsSingleNote getNoteInfos(Note note) {
    StatsSingleNote infos = new StatsSingleNote();

    int words;
    int chars;
    if (note.isChecklist()) {
      infos.setChecklistCompletedItemsNumber(
          StringUtils.countMatches(note.getContent(), CHECKED_SYM));
      infos.setChecklistItemsNumber(infos.getChecklistCompletedItemsNumber() +
          StringUtils.countMatches(note.getContent(), UNCHECKED_SYM));
    }
    infos.setTags(TagsHelper.retrieveTags(note).size());
    words = getWords(note);
    chars = getChars(note);
    infos.setWords(words);
    infos.setChars(chars);

    int attachmentsAll = 0;
    int images = 0;
    int videos = 0;
    int audioRecordings = 0;
    int sketches = 0;
    int files = 0;

    for (Attachment attachment : note.getAttachmentsList()) {
      if (MIME_TYPE_IMAGE.equals(attachment.getMime_type())) {
        images++;
      } else if (MIME_TYPE_VIDEO.equals(attachment.getMime_type())) {
        videos++;
      } else if (MIME_TYPE_AUDIO.equals(attachment.getMime_type())) {
        audioRecordings++;
      } else if (MIME_TYPE_SKETCH.equals(attachment.getMime_type())) {
        sketches++;
      } else if (MIME_TYPE_FILES.equals(attachment.getMime_type())) {
        files++;
      }
      attachmentsAll++;
    }
    infos.setAttachments(attachmentsAll);
    infos.setImages(images);
    infos.setVideos(videos);
    infos.setAudioRecordings(audioRecordings);
    infos.setSketches(sketches);
    infos.setFiles(files);

    if (note.getCategory() != null) {
      infos.setCategoryName(note.getCategory().getName());
    }

    return infos;
  }

  /**
   * Counts words in a note
   */
  public static int getWords(Note note) {
    return CountFactory.getWordCounter().countWords(note);
  }

  /**
   * Counts chars in a note
   */
  public static int getChars(Note note) {
    return CountFactory.getWordCounter().countChars(note);
  }

}
