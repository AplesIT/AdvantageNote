package com.production.advangenote.helpers.date;

import android.content.Context;
import android.text.format.DateUtils;

import com.production.advangenote.AdvantageNotes;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.production.advangenote.utils.ConstantsBase.DATE_FORMAT_SORTABLE;

/**
 * @author vietnh
 * @name DateHelper
 * @date 10/29/20
 **/
public class DateHelper {

    private DateHelper() {
        // hides public constructor
    }

    public static String getSortableDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_SORTABLE);
        return sdf.format(Calendar.getInstance().getTime());
    }

    public static String onDateSet(int year, int month, int day, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return sdf.format(cal.getTime());
    }

    public static String onTimeSet(int hour, int minute, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        return sdf.format(cal.getTime());
    }

    public static String getDateTimeShort(Context mContext, Long date) {
        int flags = DateUtils.FORMAT_ABBREV_WEEKDAY | DateUtils.FORMAT_SHOW_WEEKDAY
                | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_DATE;
        return (date == null) ? "" : DateUtils.formatDateTime(mContext, date, flags)
                + " " + DateUtils.formatDateTime(mContext, date, DateUtils.FORMAT_SHOW_TIME);
    }

    public static String getTimeShort(Context mContext, Long time) {
        if (time == null) {
            return "";
        }
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        return DateUtils.formatDateTime(mContext, time, DateUtils.FORMAT_SHOW_TIME);
    }

    public static String getTimeShort(Context mContext, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        return DateUtils.formatDateTime(mContext, c.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);
    }


    public static String formatShortTime(Context mContext, long time) {
        String m = String.valueOf(time / 1000 / 60);
        String s = String.format("%02d", (time / 1000) % 60);
        return m + ":" + s;
    }


    public static String getFormattedDate(Long timestamp, boolean prettified) {
        if (prettified) {
            return com.production.advangenote.utils.date.DateUtils.prettyTime(timestamp);
        } else {
            return DateHelper.getDateTimeShort(AdvantageNotes.getAppContext(), timestamp);
        }
    }

}
