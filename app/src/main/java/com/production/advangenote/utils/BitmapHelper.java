package com.production.advangenote.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Looper;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.production.advangenote.AdvantageNotes;
import com.production.advangenote.R;
import com.production.advangenote.helpers.AttachmentsHelper;
import com.production.advangenote.models.Attachment;

import org.apache.commons.io.FilenameUtils;

import java.util.concurrent.ExecutionException;

import it.feio.android.simplegallery.util.BitmapUtils;
import lombok.experimental.UtilityClass;

import static com.production.advangenote.utils.ConstantsBase.MIME_TYPE_AUDIO;
import static com.production.advangenote.utils.ConstantsBase.MIME_TYPE_CONTACT_EXT;
import static com.production.advangenote.utils.ConstantsBase.MIME_TYPE_FILES;
import static com.production.advangenote.utils.ConstantsBase.MIME_TYPE_IMAGE;
import static com.production.advangenote.utils.ConstantsBase.MIME_TYPE_SKETCH;
import static com.production.advangenote.utils.ConstantsBase.MIME_TYPE_VIDEO;

/**
 * @author vietnh
 * @name BitmapHelper
 * @date 10/12/20
 **/
@UtilityClass
public class BitmapHelper {

    private static final String ANDROID_RESOURCE = "android.resource://";

    /**
     * Retrieves a the bitmap relative to attachment based on mime type
     */
    public static Bitmap getBitmapFromAttachment(Context mContext, Attachment mAttachment, int width,
                                                 int height) {
        Bitmap bmp = null;

        if (AttachmentsHelper.typeOf(mAttachment, MIME_TYPE_VIDEO, MIME_TYPE_IMAGE, MIME_TYPE_SKETCH)) {
            bmp = getImageBitmap(mContext, mAttachment, width, height);

        } else if (MIME_TYPE_AUDIO.equals(mAttachment.getMime_type())) {
            bmp = ThumbnailUtils.extractThumbnail(
                    BitmapUtils
                            .decodeSampledBitmapFromResourceMemOpt(mContext.getResources().openRawResource(R
                                    .raw.play), width, height), width, height);

        } else if (MIME_TYPE_FILES.equals(mAttachment.getMime_type())) {
            if (MIME_TYPE_CONTACT_EXT.equals(FilenameUtils.getExtension(mAttachment.getName()))) {
                bmp = ThumbnailUtils.extractThumbnail(
                        BitmapUtils
                                .decodeSampledBitmapFromResourceMemOpt(mContext.getResources().openRawResource(R
                                        .raw.vcard), width, height), width, height);
            } else {
                bmp = ThumbnailUtils.extractThumbnail(
                        BitmapUtils
                                .decodeSampledBitmapFromResourceMemOpt(mContext.getResources().openRawResource(R
                                        .raw.files), width, height), width, height);
            }
        }

        return bmp;
    }

    private static Bitmap getImageBitmap(Context mContext, Attachment mAttachment, int width,
                                         int height) {
        try {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                return BitmapUtils.getThumbnail(mContext, mAttachment.getUri(), width, height);
            } else {
                return Glide.with(AdvantageNotes.getAppContext()).asBitmap()
                        .apply(new RequestOptions()
                                .centerCrop()
                                .error(R.drawable.attachment_broken))
                        .load(mAttachment.getUri())
                        .submit(width, height).get();
            }
        } catch (NullPointerException | ExecutionException e) {
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public static Uri getThumbnailUri(Context mContext, Attachment mAttachment) {
        Uri uri = mAttachment.getUri();
        String mimeType = StorageHelper.getMimeType(uri.toString());
        if (!TextUtils.isEmpty(mimeType)) {
            String type = mimeType.split("/")[0];
            String subtype = mimeType.split("/")[1];
            switch (type) {
                case "image":
                case "video":
                    // Nothing to do, bitmap will be retrieved from this
                    break;
                case "audio":
                    uri = Uri.parse(ANDROID_RESOURCE + mContext.getPackageName() + "/" + R.raw.play);
                    break;
                default:
                    int drawable = "x-vcard".equals(subtype) ? R.raw.vcard : R.raw.files;
                    uri = Uri.parse(ANDROID_RESOURCE + mContext.getPackageName() + "/" + drawable);
                    break;
            }
        } else {
            uri = Uri.parse(ANDROID_RESOURCE + mContext.getPackageName() + "/" + R.raw.files);
        }
        return uri;
    }
}
