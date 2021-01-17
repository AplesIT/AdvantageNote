

package com.production.advangenote.models.listeners;


public interface OnReminderPickedListener {

  void onReminderPicked(long reminder);

  void onRecurrenceReminderPicked(String recurrenceRule);
}
