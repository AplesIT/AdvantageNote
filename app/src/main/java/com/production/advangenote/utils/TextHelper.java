
package com.production.advangenote.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;

import com.production.advangenote.R;
import com.production.advangenote.database.DAOSQL;
import com.production.advangenote.helpers.date.DateHelper;
import com.production.advangenote.models.Note;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.production.advangenote.utils.Constants.PREFS_NAME;
import static com.production.advangenote.utils.ConstantsBase.PREF_PRETTIFIED_DATES;
import static com.production.advangenote.utils.ConstantsBase.PREF_SORTING_COLUMN;
import static it.feio.android.checklistview.interfaces.Constants.CHECKED_ENTITY;
import static it.feio.android.checklistview.interfaces.Constants.CHECKED_SYM;
import static it.feio.android.checklistview.interfaces.Constants.UNCHECKED_ENTITY;
import static it.feio.android.checklistview.interfaces.Constants.UNCHECKED_SYM;



public class TextHelper {

  /**
   *
   */
  public static Spanned[] parseTitleAndContent(Context mContext, Note note) {

    final int CONTENT_SUBSTRING_LENGTH = 300;

    String titleText = note.getTitle();
    String contentText = limit(note.getContent().trim(), CONTENT_SUBSTRING_LENGTH, false, true);

    // Masking title and content string if note is locked
    if (note.isLocked()
        && !mContext.getSharedPreferences(PREFS_NAME, Context.MODE_MULTI_PROCESS).getBoolean(
        "settings_password_access", false)) {
      // This checks if a part of content is used as title and should be partially masked
      if (!note.getTitle().equals(titleText) && titleText.length() > 3) {
        titleText = limit(titleText, 4, false, false);
      }
      contentText = "";
    }

    // Replacing checkmarks symbols with html entities
    Spanned contentSpanned;
    if (note.isChecklist() && !TextUtils.isEmpty(contentText)) {
      contentSpanned = Html.fromHtml(contentText
          .replace(CHECKED_SYM, CHECKED_ENTITY)
          .replace(UNCHECKED_SYM, UNCHECKED_ENTITY)
          .replace(System.getProperty("line.separator"), "<br/>"));
    } else {
      contentSpanned = new SpannedString(contentText);
    }

    return new Spanned[]{new SpannedString(titleText), contentSpanned};
  }


  private static String limit(String value, int length, boolean singleLine, boolean elipsize) {
    StringBuilder buf = new StringBuilder(value);
    int indexNewLine = buf.indexOf(System.getProperty("line.separator"));
    int endIndex =
        singleLine && indexNewLine < length ? indexNewLine : length < buf.length() ? length : -1;
    if (endIndex != -1) {
      buf.setLength(endIndex);
      if (elipsize) {
        buf.append("...");
      }
    }
    return buf.toString();
  }


  public static String capitalize(String string) {
    return string.substring(0, 1).toUpperCase(Locale.getDefault()) + string.substring(1
    ).toLowerCase(Locale.getDefault());
  }


  /**
   * Checks if a query conditions searches for category
   *
   * @param sqlCondition query "where" condition
   * @return Category ID
   */
  public static String checkIntentCategory(String sqlCondition) {
    String pattern = DAOSQL.KEY_CATEGORY + "\\s*=\\s*([\\d]+)";
    Pattern p = Pattern.compile(pattern);
    Matcher matcher = p.matcher(sqlCondition);
    if (matcher.find() && matcher.group(1) != null) {
      return matcher.group(1).trim();
    }
    return null;
  }


  /**
   * Choosing which date must be shown depending on sorting criteria
   *
   * @return String ith formatted date
   */
  public static String getDateText(Context mContext, Note note, int navigation) {
    String dateText;
    String sort_column;
    SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_MULTI_PROCESS);

    // Reminder screen forces sorting
    if (Navigation.REMINDERS == navigation) {
      sort_column = DAOSQL.KEY_REMINDER;
    } else {
      sort_column = prefs.getString(PREF_SORTING_COLUMN, "");
    }

    switch (sort_column) {
      case DAOSQL.KEY_CREATION:
        dateText = mContext.getString(R.string.creation) + " " + DateHelper
            .getFormattedDate(note.getCreation
                (), prefs.getBoolean(PREF_PRETTIFIED_DATES, true));
        break;
      case DAOSQL.KEY_REMINDER:
        if (note.getAlarm() == null) {
          dateText = mContext.getString(R.string.no_reminder_set);
        } else {
          dateText = mContext.getString(R.string.alarm_set_on) + " " + DateHelper
              .getDateTimeShort(mContext,
                  Long.parseLong(note.getAlarm()));
        }
        break;
      default:
        dateText = mContext.getString(R.string.last_update) + " " + DateHelper.getFormattedDate(note
            .getLastModification(), prefs.getBoolean(PREF_PRETTIFIED_DATES, true));
        break;
    }
    return dateText;
  }


  /**
   * Gets an alternative title if empty
   */
  public static String getAlternativeTitle(Context context, Note note, Spanned spanned) {
    if (spanned.length() > 0) {
      return spanned.toString();
    }
    return context.getString(R.string.note) + " " + context.getString(R.string.creation) + " "
        + DateHelper
        .getDateTimeShort(context, note.getCreation());
  }

}
