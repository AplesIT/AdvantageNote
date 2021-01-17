
package com.production.advangenote.utils.date;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.production.advangenote.helpers.date.RecurrenceHelper;
import com.production.advangenote.models.listeners.OnReminderPickedListener;

import java.util.Calendar;



public class ReminderPickers {

  private FragmentActivity mActivity;
  private OnReminderPickedListener mOnReminderPickedListener;


  public ReminderPickers(FragmentActivity mActivity,
      OnReminderPickedListener mOnReminderPickedListener) {
    this.mActivity = mActivity;
    this.mOnReminderPickedListener = mOnReminderPickedListener;
  }

  public void pick(Long presetDateTime, String recurrenceRule) {
    showDateTimeSelectors(DateUtils.getCalendar(presetDateTime), recurrenceRule);
  }


  /**
   * Show date and time pickers
   */
  private void showDateTimeSelectors(Calendar reminder, String recurrenceRule) {
    SublimePickerFragment pickerFrag = new SublimePickerFragment();
    pickerFrag.setCallback(new SublimePickerFragment.Callback() {
      @Override
      public void onCancelled() {
        // Nothing to do
      }

      @Override
      public void onDateTimeRecurrenceSet(SelectedDate selectedDate, int hourOfDay, int minute,
          SublimeRecurrencePicker.RecurrenceOption recurrenceOption, String recurrenceRule) {
        Calendar reminder = selectedDate.getFirstDate();
        reminder.set(Calendar.HOUR_OF_DAY, hourOfDay);
        reminder.set(Calendar.MINUTE, minute);

        mOnReminderPickedListener.onReminderPicked(reminder.getTimeInMillis());
        mOnReminderPickedListener.onRecurrenceReminderPicked(
            RecurrenceHelper
                .buildRecurrenceRuleByRecurrenceOptionAndRule(recurrenceOption, recurrenceRule));
      }
    });

    int displayOptions = 0;
    displayOptions |= SublimeOptions.ACTIVATE_DATE_PICKER;
    displayOptions |= SublimeOptions.ACTIVATE_TIME_PICKER;
    displayOptions |= SublimeOptions.ACTIVATE_RECURRENCE_PICKER;

    SublimeOptions sublimeOptions = new SublimeOptions();
    sublimeOptions.setPickerToShow(SublimeOptions.Picker.TIME_PICKER);
    sublimeOptions.setDisplayOptions(displayOptions);
    sublimeOptions.setDateParams(reminder);
    sublimeOptions
        .setRecurrenceParams(SublimeRecurrencePicker.RecurrenceOption.CUSTOM, recurrenceRule);
    sublimeOptions.setTimeParams(reminder.get(Calendar.HOUR_OF_DAY), reminder.get(Calendar.MINUTE),
        DateUtils.is24HourMode(mActivity));

    Bundle bundle = new Bundle();
    bundle.putParcelable("SUBLIME_OPTIONS", sublimeOptions);
    pickerFrag.setArguments(bundle);

    pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    pickerFrag.show(mActivity.getSupportFragmentManager(), "SUBLIME_PICKER");
  }

}
