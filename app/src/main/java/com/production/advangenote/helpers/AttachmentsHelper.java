package com.production.advangenote.helpers;

import com.production.advangenote.models.Attachment;

import org.apache.commons.io.FileUtils;

import java.io.File;

import lombok.experimental.UtilityClass;

/**
 * @author vietnh
 * @name AttachmentsHelper
 * @date 9/30/20
 **/
@UtilityClass
public class AttachmentsHelper {

    /**
     * Retrieves attachment file size
     *
     * @param attachment Attachment to evaluate
     * @return Human readable file size string
     */
    public static String getSize(Attachment attachment) {
        long sizeInKb = attachment.getSize();
        if (attachment.getSize() == 0) {
            sizeInKb = new File(attachment.getUri().getPath()).length();
        }
        return FileUtils.byteCountToDisplaySize(sizeInKb);
    }

    /**
     * Checks type of attachment
     */
    public static boolean typeOf(Attachment attachment, String... mimeTypes) {
        for (String mimeType : mimeTypes) {
            if (mimeType.equals(attachment.getMime_type())) {
                return true;
            }
        }
        return false;
    }

}
