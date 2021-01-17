 

package com.production.advangenote.helpers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.production.advangenote.models.Note;
import lombok.experimental.UtilityClass;

import static com.production.advangenote.utils.ConstantsBase.INTENT_NOTE;

@UtilityClass
public class IntentHelper {

  public static Intent getNoteIntent(@NonNull Context context, @NonNull Class target, String action,
                                     Note note) {
    Intent intent = new Intent(context, target);
    intent.setAction(action);
    Bundle bundle = new Bundle();
    bundle.putParcelable(INTENT_NOTE, note);
    intent.putExtras(bundle);

//    // Sets the Activity to start in a new, empty task
//    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//    // Workaround to fix problems with multiple notifications
//    intent.setAction(ACTION_NOTIFICATION_CLICK + System.currentTimeMillis());

    return intent;
  }

  public static PendingIntent getNotePendingIntent(@NonNull Context context, @NonNull Class target,
                                                   String action,
                                                   Note note) {
    Intent intent = getNoteIntent(context, target, action, note);
    return PendingIntent.getActivity(context, getUniqueRequestCode(note), intent,
        PendingIntent.FLAG_UPDATE_CURRENT);
  }

  static int getUniqueRequestCode(Note note) {
    return note.get_id().intValue();
  }

}
