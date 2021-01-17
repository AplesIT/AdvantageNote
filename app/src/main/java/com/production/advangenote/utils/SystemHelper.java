package com.production.advangenote.utils;

import android.app.Activity;
import android.content.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author vietnh
 * @name SystemHelper
 * @date 10/1/20
 **/
public class SystemHelper {

    /**
     * Performs a full app restart
     */
    private final static Logger logger = LoggerFactory.getLogger("SystemHelper.class");

    public static void restartApp(final Context mContext, Class activityClass) {

        System.exit(0);
    }


    /**
     * Performs closure of multiple closeables objects
     *
     * @param closeables Objects to close
     */
    public static void closeCloseable(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    logger.info("Can't close " + closeable, e);
                }
            }
        }
    }

    public static void copyToClipboard(Context context, String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
                .getSystemService(
                        Activity.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("text label", text);
        clipboard.setPrimaryClip(clip);
    }
}


