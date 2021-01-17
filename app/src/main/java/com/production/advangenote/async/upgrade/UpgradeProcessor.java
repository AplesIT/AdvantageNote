
package com.production.advangenote.async.upgrade;

import android.content.ContentValues;
import android.net.Uri;
import android.text.TextUtils;

import com.production.advangenote.AdvantageNotes;
import com.production.advangenote.database.DAOSQL;
import com.production.advangenote.models.Attachment;
import com.production.advangenote.models.Note;
import com.production.advangenote.utils.ReminderHelper;
import com.production.advangenote.utils.StorageHelper;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.production.advangenote.utils.ConstantsBase.MIME_TYPE_AUDIO;
import static com.production.advangenote.utils.ConstantsBase.MIME_TYPE_AUDIO_EXT;
import static com.production.advangenote.utils.ConstantsBase.MIME_TYPE_FILES;
import static com.production.advangenote.utils.ConstantsBase.MIME_TYPE_IMAGE;
import static com.production.advangenote.utils.ConstantsBase.MIME_TYPE_VIDEO;


public class UpgradeProcessor {
  private final static Logger logger = LoggerFactory.getLogger("UpgradeProcessor.class");
  private final static String METHODS_PREFIX = "onUpgradeTo";

  private static  UpgradeProcessor instance;


  private UpgradeProcessor() {
  }


  private static  UpgradeProcessor getInstance() {
    if (instance == null) {
      instance = new  UpgradeProcessor();
    }
    return instance;
  }


  public static void process(int dbOldVersion, int dbNewVersion)
      throws InvocationTargetException, IllegalAccessException {
    try {
      List<Method> methodsToLaunch = getInstance().getMethodsToLaunch(dbOldVersion, dbNewVersion);
      for (Method methodToLaunch : methodsToLaunch) {
        logger.info("Running upgrade processing method: " + methodToLaunch.getName());
        methodToLaunch.invoke(getInstance());
      }
    } catch (SecurityException | IllegalAccessException | InvocationTargetException e) {
      logger.error("Explosion processing upgrade!", e);
      throw e;
    }
  }


  private List<Method> getMethodsToLaunch(int dbOldVersion, int dbNewVersion) {
    List<Method> methodsToLaunch = new ArrayList<>();
    Method[] declaredMethods = getInstance().getClass().getDeclaredMethods();
    for (Method declaredMethod : declaredMethods) {
      if (declaredMethod.getName().contains(METHODS_PREFIX)) {
        int methodVersionPostfix = Integer
            .parseInt(declaredMethod.getName().replace(METHODS_PREFIX, ""));
        if (dbOldVersion <= methodVersionPostfix && methodVersionPostfix <= dbNewVersion) {
          methodsToLaunch.add(declaredMethod);
        }
      }
    }
    return methodsToLaunch;
  }


  /**
   * Adjustment of all the old attachments without mimetype field set into DB
   */
  private void onUpgradeTo476() {
    final DAOSQL dbHelper = DAOSQL.getInstance();
    for (Attachment attachment : dbHelper.getAllAttachments()) {
      if (attachment.getMime_type() == null) {
        String mimeType = StorageHelper.getMimeType(attachment.getUri().toString());
        if (!TextUtils.isEmpty(mimeType)) {
          String type = mimeType.replaceFirst("/.*", "");
          switch (type) {
            case "image":
              attachment.setMime_type(MIME_TYPE_IMAGE);
              break;
            case "video":
              attachment.setMime_type(MIME_TYPE_VIDEO);
              break;
            case "audio":
              attachment.setMime_type(MIME_TYPE_AUDIO);
              break;
            default:
              attachment.setMime_type(MIME_TYPE_FILES);
              break;
          }
          dbHelper.updateAttachment(attachment);
        } else {
          attachment.setMime_type(MIME_TYPE_FILES);
        }
      }
    }
  }


  /**
   * Upgrades all the old audio attachments to the new format 3gpp to avoid mixing with videos
   */
  private void onUpgradeTo480() {
    final DAOSQL dbHelper = DAOSQL.getInstance();
    for (Attachment attachment : dbHelper.getAllAttachments()) {
      if ("audio/3gp".equals(attachment.getMime_type()) || "audio/3gpp"
          .equals(attachment.getMime_type
              ())) {

        File from = new File(attachment.getUriPath());
        FilenameUtils.getExtension(from.getName());
        File to = new File(from.getParent(), from.getName().replace(FilenameUtils.getExtension(from
            .getName()), MIME_TYPE_AUDIO_EXT));
        boolean successRenaming = from.renameTo(to);

        if (successRenaming) {
          attachment.setUri(Uri.fromFile(to));
          attachment.setMime_type(MIME_TYPE_AUDIO);
          dbHelper.updateAttachment(attachment);
        } else {
          logger.error("onUpgradeTo480 - Error renaming attachment: " + attachment.getName());
        }

      }
    }
  }


  /**
   * Reschedule reminders after upgrade
   */
  private void onUpgradeTo482() {
    for (Note note : DAOSQL.getInstance().getNotesWithReminderNotFired()) {
      ReminderHelper.addReminder(AdvantageNotes.getAppContext(), note);
    }
  }


  /**
   * Ensures that no duplicates will be found during the creation-to-ID transition
   */
  private void onUpgradeTo501() {
    List<Long> creations = new ArrayList<>();
    for (Note note : DAOSQL.getInstance().getAllNotes(false)) {
      if (creations.contains(note.getCreation())) {

        ContentValues values = new ContentValues();
        values.put(DAOSQL.KEY_CREATION, note.getCreation() + (long) (Math.random() * 999));
        DAOSQL.getInstance().getDatabase()
            .update(DAOSQL.TABLE_NOTES, values, DAOSQL.KEY_TITLE +
                    " = ? AND " + DAOSQL.KEY_CREATION + " = ? AND " + DAOSQL.KEY_CONTENT + " = ?",
                new String[]{note
                    .getTitle(), String.valueOf(note.getCreation()), note.getContent()});
      }
      creations.add(note.getCreation());
    }
  }

}
