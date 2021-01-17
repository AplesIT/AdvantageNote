
package com.production.advangenote.factory;

import android.net.Uri;
import android.provider.MediaStore;

public class MediaStoreFactory {

  public Uri createURI(String type) {
    switch (type) {
      case "image":
        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
      case "video":
        return MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
      case "audio":
        return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
      default:
        return null;
    }
  }

}
