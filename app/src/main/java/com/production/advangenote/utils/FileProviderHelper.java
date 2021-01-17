
package com.production.advangenote.utils;

import android.net.Uri;

import androidx.core.content.FileProvider;

import com.production.advangenote.AdvantageNotes;
import com.production.advangenote.models.Attachment;

import java.io.File;

public class FileProviderHelper {

  private FileProviderHelper() {
    // hides public constructor
  }

  /**
   * Generates the FileProvider URI for a given existing file
   */
  public static Uri getFileProvider(File file) {
    return FileProvider.getUriForFile(AdvantageNotes.getAppContext(),
        AdvantageNotes.getAppContext().getPackageName() + ".authority", file);
  }

  /**
   * Generates a shareable URI for a given attachment by evaluating its stored (into DB) path
   */
  public static Uri getShareableUri(Attachment attachment) {
    File attachmentFile = new File(attachment.getUri().getPath());
    if (attachmentFile.exists()) {
      return FileProviderHelper.getFileProvider(attachmentFile);
    } else {
      return attachment.getUri();
    }
  }
}
