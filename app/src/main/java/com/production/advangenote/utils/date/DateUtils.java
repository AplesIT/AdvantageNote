package com.production.advangenote.utils.date;

import android.content.Context;

import com.production.advangenote.AdvantageNotes;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.prettytime.PrettyTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.production.advangenote.utils.ConstantsBase.DATE_FORMAT_SORTABLE_OLD;

/**
 * @author vietnh
 * @name DateUtils
 * @date 9/12/20
 **/
public class DateUtils {
    private DateUtils() {
        throw new IllegalStateException("Utility class");
    }
    private static Logger logger = LoggerFactory.getLogger("DateUtils.class");
    public static String getString(long date, String format) {
        Date d = new Date(date);
        return getString(d, format);
    }


    public static String getString(Date d, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(d);
    }


    public static Calendar getDateFromString(String str, String format) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            cal.setTime(sdf.parse(str));
        } catch (ParseException e) {
            logger.error("Malformed datetime string" + e.getMessage());

        } catch (NullPointerException e) {
            logger.error("Date or time not set");
        }
        return cal;
    }


    public static Calendar getLongFromDateTime(String date, String dateFormat, String time,
                                               String timeFormat) {
        Calendar cal = Calendar.getInstance();
        Calendar cDate = Calendar.getInstance();
        Calendar cTime = Calendar.getInstance();
        SimpleDateFormat sdfDate = new SimpleDateFormat(dateFormat);
        SimpleDateFormat sdfTime = new SimpleDateFormat(timeFormat);
        try {
            cDate.setTime(sdfDate.parse(date));
            cTime.setTime(sdfTime.parse(time));
        } catch (ParseException e) {
            logger.error("Date or time parsing error: " + e.getMessage());
        }
        cal.set(Calendar.YEAR, cDate.get(Calendar.YEAR));
        cal.set(Calendar.MONTH, cDate.get(Calendar.MONTH));
        cal.set(Calendar.DAY_OF_MONTH, cDate.get(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, cTime.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cTime.get(Calendar.MINUTE));
        cal.set(Calendar.SECOND, 0);
        return cal;
    }


    public static Calendar getCalendar(Long dateTime) {
        Calendar cal = Calendar.getInstance();
        if (dateTime != null && dateTime != 0) {
            cal.setTimeInMillis(dateTime);
        }
        return cal;
    }


    public static String getLocalizedDateTime(Context mContext,
                                              String dateString, String format) {
        String res = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            sdf = new SimpleDateFormat(DATE_FORMAT_SORTABLE_OLD);
            try {
                date = sdf.parse(dateString);
            } catch (ParseException e1) {
                logger.error("String is not formattable into date");
            }
        }

        if (date != null) {
            String dateFormatted = android.text.format.DateUtils
                    .formatDateTime(mContext, date.getTime(), android
                            .text.format.DateUtils.FORMAT_ABBREV_MONTH);
            String timeFormatted = android.text.format.DateUtils
                    .formatDateTime(mContext, date.getTime(), android
                            .text.format.DateUtils.FORMAT_SHOW_TIME);
            res = dateFormatted + " " + timeFormatted;
        }

        return res;
    }


    public static boolean is24HourMode(Context mContext) {
        Calendar c = Calendar.getInstance();
        String timeFormatted = android.text.format.DateUtils
                .formatDateTime(mContext, c.getTimeInMillis(), android
                        .text.format.DateUtils.FORMAT_SHOW_TIME);
        return !timeFormatted.toLowerCase().contains("am") && !timeFormatted.toLowerCase()
                .contains("pm");
    }


    public static boolean isSameDay(long date1, long date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(date1);
        cal2.setTimeInMillis(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get
                (Calendar.DAY_OF_YEAR);
    }


    public static long getNextMinute() {
        return Calendar.getInstance().getTimeInMillis() + 1000 * 60;
    }

    /**
     * Returns actually set reminder if that is on the future, next-minute-reminder otherwise
     */
    public static long getPresetReminder(Long currentReminder) {
        long now = Calendar.getInstance().getTimeInMillis();
        return currentReminder != null && currentReminder > now ? currentReminder : getNextMinute();
    }

    public static Long getPresetReminder(String alarm) {
        long alarmChecked = alarm == null ? 0 : Long.parseLong(alarm);
        return getPresetReminder(alarmChecked);
    }

    /**
     * Checks if a epoch-date timestamp is in the future
     */
    public static boolean isFuture(String timestamp) {
        return !StringUtils.isEmpty(timestamp) && isFuture(Long.parseLong(timestamp));
    }

    /**
     * Checks if a epoch-date timestamp is in the future
     */
    public static boolean isFuture(Long timestamp) {
        return timestamp != null && timestamp > Calendar.getInstance().getTimeInMillis();
    }

    public static String prettyTime(String timeInMillisec) {
        if (timeInMillisec == null) {
            return "";
        }
        return prettyTime(Long.parseLong(timeInMillisec),
                AdvantageNotes.getAppContext().getResources().getConfiguration().locale);
    }


    public static String prettyTime(Long timeInMillisec) {
        return prettyTime(timeInMillisec,
                AdvantageNotes.getAppContext().getResources().getConfiguration().locale);
    }


    static String prettyTime(Long timeInMillisec, Locale locale) {
        if (timeInMillisec == null) {
            return "";
        }
        Date d = new Date(timeInMillisec);
        PrettyTime pt = new PrettyTime();
        if (locale != null) {
            pt.setLocale(locale);
        }
        return pt.format(d);
    }
}
