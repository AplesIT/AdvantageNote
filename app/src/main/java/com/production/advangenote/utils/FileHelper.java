
package com.production.advangenote.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.text.TextUtils;

import com.production.advangenote.factory.MediaStoreFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static java.lang.Long.parseLong;


public class FileHelper {

  private FileHelper() {
    // hides public constructor
  }

  private static final Logger logger = LoggerFactory.getLogger("FileHelper.class");
  public static String getPath(final Context context, final Uri uri) {

    if (uri == null) {
      return null;
    }

    // DocumentProvider
    if (DocumentsContract.isDocumentUri(context, uri)) {
      // ExternalStorageProvider
      if (isExternalStorageDocument(uri)) {
        final String docId = DocumentsContract.getDocumentId(uri);
        final String[] split = docId.split(":");
        final String type = split[0];

        if ("primary".equalsIgnoreCase(type)) {
          return Environment.getExternalStorageDirectory() + "/" + split[1];
        }

        // TODO handle non-primary volumes
      }
      // DownloadsProvider
      else if (isDownloadsDocument(uri)) {
        final Uri contentUri = ContentUris
            .withAppendedId(Uri.parse("content://downloads/public_downloads"),
                parseLong(DocumentsContract.getDocumentId(uri)));
        return getDataColumn(context, contentUri, null, null);
      }
      // MediaProvider
      else if (isMediaDocument(uri)) {
        final String docId = DocumentsContract.getDocumentId(uri);
        final String[] split = docId.split(":");
        final String type = split[0];
        MediaStoreFactory mediaStoreFactory = new MediaStoreFactory();
        Uri contentUri = mediaStoreFactory.createURI(type);

        final String selection = "_id=?";
        final String[] selectionArgs = new String[]{split[1]};

        return getDataColumn(context, contentUri, selection, selectionArgs);
      }
    }
    // MediaStore (and general)
    else if ("content".equalsIgnoreCase(uri.getScheme())) {
      return getDataColumn(context, uri, null, null);
    }
    // File
    else if ("file".equalsIgnoreCase(uri.getScheme())) {
      return uri.getPath();
    }

    return null;
  }


  /**
   * Get the value of the data column for this Uri. This is useful for MediaStore Uris, and other
   * file-based ContentProviders.
   *
   * @param context       The context.
   * @param uri           The Uri to query.
   * @param selection     (Optional) Filter used in the query.
   * @param selectionArgs (Optional) Selection arguments used in the query.
   * @return The value of the _data column, which is typically a file path.
   */
  public static String getDataColumn(Context context, Uri uri,
                                     String selection, String[] selectionArgs) {

    final String column = "_data";
    final String[] projection = {column};
    try (Cursor cursor = context.getContentResolver()
        .query(uri, projection, selection, selectionArgs, null)) {
      if (cursor != null && cursor.moveToFirst()) {
        final int column_index = cursor.getColumnIndexOrThrow(column);
        return cursor.getString(column_index);
      }
    } catch (Exception e) {
      logger.error("Error retrieving uri path {}", e);
    }
    return null;
  }


  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is ExternalStorageProvider.
   */
  public static boolean isExternalStorageDocument(Uri uri) {
    return "com.android.externalstorage.documents".equals(uri.getAuthority());
  }


  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is DownloadsProvider.
   */
  public static boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
  }


  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is MediaProvider.
   */
  public static boolean isMediaDocument(Uri uri) {
    return "com.android.providers.media.documents".equals(uri.getAuthority());
  }

  /**
   * Trying to retrieve file name from content resolver
   */
  public static String getNameFromUri(Context mContext, Uri uri) {
    String fileName = "";
    Cursor cursor = null;
    try {
      cursor = mContext.getContentResolver()
          .query(uri, new String[]{"_display_name"}, null, null, null);
      if (cursor != null) {
        try {
          if (cursor.moveToFirst()) {
            fileName = cursor.getString(0);
          }

        } catch (Exception e) {
          logger.error("Error managing diskk cache", e);
        }
      } else {
        fileName = uri.getLastPathSegment();
      }
    } catch (SecurityException e) {
      return null;
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return fileName;
  }


  public static String getFilePrefix(File file) {
    return getFilePrefix(file.getName());
  }


  public static String getFilePrefix(String fileName) {
    String prefix = fileName;
    int index = fileName.indexOf('.');
    if (index != -1) {
      prefix = fileName.substring(0, index);
    }
    return prefix;
  }


  public static String getFileExtension(File file) {
    return getFileExtension(file.getName());
  }


  public static String getFileExtension(String fileName) {
    if (TextUtils.isEmpty(fileName)) {
      return "";
    }
    String extension = "";
    int index = fileName.lastIndexOf('.');
    if (index != -1) {
      extension = fileName.substring(index);
    }
    return extension;
  }
}
