
package com.production.advangenote.async.notes;

import android.os.AsyncTask;

import com.production.advangenote.async.bus.NotesLoadedEvent;
import com.production.advangenote.database.DAOSQL;
import com.production.advangenote.exception.NotesLoadingException;
import com.production.advangenote.models.Note;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;



public class NoteLoaderTask extends AsyncTask<Object, Void, List<Note>> {

  private static final String ERROR_RETRIEVING_NOTES = "Error retrieving notes";

  private static NoteLoaderTask instance;

  private NoteLoaderTask() {
  }


  public static  NoteLoaderTask getInstance() {

    if (instance != null) {
      if (instance.getStatus() == Status.RUNNING && !instance.isCancelled()) {
        instance.cancel(true);
      } else if (instance.getStatus() == Status.PENDING) {
        return instance;
      }
    }

    instance = new  NoteLoaderTask();
    return instance;
  }


  @Override
  protected List<Note> doInBackground(Object... params) {

    String methodName = params[0].toString();
    DAOSQL db = DAOSQL.getInstance();

    if (params.length < 2 || params[1] == null) {
      try {
        Method method = db.getClass().getDeclaredMethod(methodName);
        return (List<Note>) method.invoke(db);
      } catch (NoSuchMethodException e) {
        return new ArrayList<>();
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new NotesLoadingException(ERROR_RETRIEVING_NOTES, e);
      }
    } else {
      Object methodArgs = params[1];
      Class[] paramClass = new Class[]{methodArgs.getClass()};
      try {
        Method method = db.getClass().getDeclaredMethod(methodName, paramClass);
        return (List<Note>) method.invoke(db, paramClass[0].cast(methodArgs));
      } catch (Exception e) {
        throw new NotesLoadingException(ERROR_RETRIEVING_NOTES, e);
      }
    }
  }


  @Override
  protected void onPostExecute(List<Note> notes) {

    super.onPostExecute(notes);
    EventBus.getDefault().post(new NotesLoadedEvent(notes));
  }
}
