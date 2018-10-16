package com.example.ashleighwilson.schoolscheduler.notes;

public interface OnReminderPickedListener
{
    void onReminderPicked(long reminder);
    void onRecurrenceReminderPicked(String recurrenceRule);
}
