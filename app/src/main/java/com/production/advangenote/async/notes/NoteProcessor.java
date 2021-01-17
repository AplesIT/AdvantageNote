
package com.production.advangenote.async.notes;

import android.os.AsyncTask;

import com.production.advangenote.async.bus.NotesUpdatedEvent;
import com.production.advangenote.models.Note;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


public abstract class NoteProcessor {

  List<Note> notes;


  protected NoteProcessor(List<Note> notes) {
    this.notes = new ArrayList<>(notes);
  }


  public void process() {
    NotesProcessorTask task = new NotesProcessorTask();
    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, notes);
  }


  protected abstract void processNote(Note note);


  class NotesProcessorTask extends AsyncTask<List<Note>, Void, List<Note>> {

    @Override
    protected List<Note> doInBackground(List<Note>... params) {
      List<Note> processableNote = params[0];
      for (Note note : processableNote) {
        processNote(note);
      }
      return processableNote;
    }


    @Override
    protected void onPostExecute(List<Note> notes) {
      afterProcess(notes);
    }
  }


  protected void afterProcess(List<Note> notes) {
    EventBus.getDefault().post(new NotesUpdatedEvent(notes));
  }
}
