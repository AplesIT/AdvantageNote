 
package com.production.advangenote.async;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;

import com.production.advangenote.AdvantageNotes;
import com.production.advangenote.models.Attachment;
import com.production.advangenote.models.listeners.OnAttachingFileListener;
import com.production.advangenote.utils.StorageHelper;

import java.lang.ref.WeakReference;


public class AttachmentTask extends AsyncTask<Void, Void, Attachment> {

  private final WeakReference<Fragment> mFragmentWeakReference;
  private OnAttachingFileListener mOnAttachingFileListener;
  private Uri uri;
  private String fileName;


  public AttachmentTask(Fragment mFragment, Uri uri,
      OnAttachingFileListener mOnAttachingFileListener) {
    this(mFragment, uri, null, mOnAttachingFileListener);
  }


  public AttachmentTask(Fragment mFragment, Uri uri, String fileName,
                        OnAttachingFileListener mOnAttachingFileListener) {
    mFragmentWeakReference = new WeakReference<>(mFragment);
    this.uri = uri;
    this.fileName = TextUtils.isEmpty(fileName) ? "" : fileName;
    this.mOnAttachingFileListener = mOnAttachingFileListener;
  }


  @Override
  protected Attachment doInBackground(Void... params) {
    Attachment attachment = StorageHelper.createAttachmentFromUri(AdvantageNotes.getAppContext(), uri);
    attachment.setName(this.fileName);
    return attachment;
  }


  @Override
  protected void onPostExecute(Attachment mAttachment) {
    if (isAlive()) {
      if (mAttachment != null) {
        mOnAttachingFileListener.onAttachingFileFinished(mAttachment);
      } else {
        mOnAttachingFileListener.onAttachingFileErrorOccurred(null);
      }
    } else {
      if (mAttachment != null) {
        StorageHelper.delete(AdvantageNotes.getAppContext(), mAttachment.getUri().getPath());
      }
    }
  }


  private boolean isAlive() {
    return mFragmentWeakReference != null
        && mFragmentWeakReference.get() != null
        && mFragmentWeakReference.get().isAdded()
        && mFragmentWeakReference.get().getActivity() != null
        && !mFragmentWeakReference.get().getActivity().isFinishing();
  }

}
