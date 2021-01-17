package com.production.advangenote.helpers.date;

import android.content.Context;
import android.text.format.Time;

import com.appeaser.sublimepickerlibrary.recurrencepicker.EventRecurrence;
import com.appeaser.sublimepickerlibrary.recurrencepicker.EventRecurrenceFormatter;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.production.advangenote.AdvantageNotes;
import com.production.advangenote.R;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.RRule;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker.RecurrenceOption.DOES_NOT_REPEAT;

/**
 * @author vietnh
 * @name RecurrentHelper
 * @date 9/30/20
 **/
public class RecurrenceHelper {
    private RecurrenceHelper() {

    }
    private static final Logger logger= LoggerFactory.getLogger("RecurrenceHelper.class");

    public static String formatRecurrence(Context mContext, String recurrenceRule) {
        if (StringUtils.isEmpty(recurrenceRule)) {
            return "";
        }
        EventRecurrence recurrenceEvent = new EventRecurrence();
        recurrenceEvent.setStartDate(new Time("" + new Date().getTime()));
        recurrenceEvent.parse(recurrenceRule);
        return EventRecurrenceFormatter.getRepeatString(mContext.getApplicationContext(),
                mContext.getResources(), recurrenceEvent, true);
    }

    public static Long nextReminderFromRecurrenceRule(long reminder, String recurrenceRule) {
        return nextReminderFromRecurrenceRule(reminder, Calendar.getInstance().getTimeInMillis(),
                recurrenceRule);
    }

    public static Long nextReminderFromRecurrenceRule(long reminder, long currentTime,
                                                      String recurrenceRule) {
        try {
            RRule rule = new RRule();
            rule.setValue(recurrenceRule);
            long startTimestamp = reminder + 60 * 1000;
            if (startTimestamp < currentTime) {
                startTimestamp = currentTime;
            }
            Date nextDate = rule.getRecur()
                    .getNextDate(new DateTime(reminder), new DateTime(startTimestamp));
            return nextDate == null ? 0L : nextDate.getTime();
        } catch (ParseException e) {
            logger.error("Error parsing rrule");
            return 0L;
        }
    }

    public static String getNoteReminderText(long reminder) {
        return AdvantageNotes.getAppContext().getString(R.string.alarm_set_on) + " " + DateHelper
                .getDateTimeShort(AdvantageNotes
                        .getAppContext(), reminder);
    }

    public static String getNoteRecurrentReminderText(long reminder, String rrule) {
        return formatRecurrence(AdvantageNotes.getAppContext(), rrule) + " " + AdvantageNotes.getAppContext()
                .getString
                        (R.string.starting_from) + " " + DateHelper
                .getDateTimeShort(AdvantageNotes.getAppContext(), reminder);
    }

    public static String buildRecurrenceRuleByRecurrenceOptionAndRule(
            SublimeRecurrencePicker.RecurrenceOption recurrenceOption,
            String recurrenceRule) {
        if (recurrenceRule == null && recurrenceOption != DOES_NOT_REPEAT) {
            Recur.Frequency freq = Recur.Frequency.valueOf(recurrenceOption.toString());
            Recur recur = new Recur(freq, new DateTime(32519731800000L));
            return new RRule(recur).toString().replace("RRULE:", "");
        }
        return recurrenceRule;
    }
}
