 
package com.production.advangenote;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import com.production.advangenote.databinding.ActivityNoteInfosBinding;
import com.production.advangenote.helpers.NotesHelper;
import com.production.advangenote.models.Note;
import com.production.advangenote.models.StatsSingleNote;

import static com.production.advangenote.utils.ConstantsBase.INTENT_NOTE;


public class NoteInfosActivity extends Activity {

  private ActivityNoteInfosBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    binding = ActivityNoteInfosBinding.inflate(getLayoutInflater());
    View view = binding.getRoot();
    setContentView(view);

    Note note = Objects.requireNonNull(getIntent().getExtras()).getParcelable(INTENT_NOTE);
    populateViews(note);
  }

  private void populateViews(Note note) {
    StatsSingleNote infos = NotesHelper.getNoteInfos(note);

    populateView(binding.noteInfosCategory, infos.getCategoryName());
    populateView(binding.noteInfosTags, infos.getTags());
    populateView(binding.noteInfosChars, infos.getChars());
    populateView(binding.noteInfosWords, infos.getWords());
    populateView(binding.noteInfosChecklistItems, infos.getChecklistItemsNumber());
    populateView(binding.noteInfosCompletedChecklistItems, getChecklistCompletionState(infos),
        !note.isChecklist());
    populateView(binding.noteInfosImages, infos.getImages());
    populateView(binding.noteInfosVideos, infos.getVideos());
    populateView(binding.noteInfosAudiorecordings, infos.getAudioRecordings());
    populateView(binding.noteInfosSketches, infos.getSketches());
    populateView(binding.noteInfosFiles, infos.getFiles());
  }

  static String getChecklistCompletionState(StatsSingleNote infos) {
    int percentage = Math.round(
        (float) infos.getChecklistCompletedItemsNumber() / infos.getChecklistItemsNumber() * 100);
    return infos.getChecklistCompletedItemsNumber() + " (" + percentage + "%)";
  }

  private void populateView(TextView textView, int numberValue) {
    String stringValue = numberValue > 0 ? String.valueOf(numberValue) : "";
    populateView(textView, stringValue);
  }

  private void populateView(TextView textView, String value) {
    populateView(textView, value, false);
  }

  private void populateView(TextView textView, String value, boolean forceHide) {
    if (StringUtils.isNotEmpty(value) && !forceHide) {
      textView.setText(value);
    } else {
      ((View) textView.getParent()).setVisibility(View.GONE);
    }
  }

}
