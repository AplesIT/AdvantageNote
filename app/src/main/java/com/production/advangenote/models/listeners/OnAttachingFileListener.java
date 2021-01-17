

package com.production.advangenote.models.listeners;


import com.production.advangenote.models.Attachment;

public interface OnAttachingFileListener {

  void onAttachingFileErrorOccurred(Attachment mAttachment);

  void onAttachingFileFinished(Attachment mAttachment);
}
