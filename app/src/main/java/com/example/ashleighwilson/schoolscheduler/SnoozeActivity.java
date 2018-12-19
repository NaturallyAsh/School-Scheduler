package com.example.ashleighwilson.schoolscheduler;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.example.ashleighwilson.schoolscheduler.data.NotificationController;
import com.example.ashleighwilson.schoolscheduler.data.SaveAgendaTask;
import com.example.ashleighwilson.schoolscheduler.data.SaveNoteTask;
import com.example.ashleighwilson.schoolscheduler.models.AgendaModel;
import com.example.ashleighwilson.schoolscheduler.notes.Constants;
import com.example.ashleighwilson.schoolscheduler.notes.Note;
import com.example.ashleighwilson.schoolscheduler.notes.OnReminderPickedListener;
import com.example.ashleighwilson.schoolscheduler.utils.DateHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SnoozeActivity extends AppCompatActivity implements OnReminderPickedListener
{
    private static final String TAG = SnoozeActivity.class.getSimpleName();
    private Note note;
    private Note[] notes;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getParcelableExtra(Constants.INTENT_NOTE) != null) {
            note = getIntent().getParcelableExtra(Constants.INTENT_NOTE);
            manageNotification();
        }
    }

    private void manageNotification()
    {
        if (Constants.ACTION_DISMISS.equals(getIntent().getAction())) {
            setNextRecurrentReminder(note);
            finish();
        } else if (Constants.ACTION_SNOOZE.equals(getIntent().getAction())) {
            String snoozeDelay = Constants.PREF_SNOOZE_DEFAULT;
            long newReminder = Calendar.getInstance().getTimeInMillis() + Integer.parseInt(snoozeDelay)
                    * 60 * 1000;
            updateNoteReminder(newReminder, note);
            finish();
        } else {
            Intent intent = new Intent(this, NotesActivity.class);
            intent.putExtra(Constants.INTENT_KEY, note.get_id());
            intent.setAction(Constants.ACTION_NOTIFICATION_CLICK);
            startActivity(intent);
            finish();
        }

        removeNotification(note);
    }

    private void removeNotification(Note note)
    {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(String.valueOf(note.get_id()), 0);
    }

    public static void setNextRecurrentReminder(Note note)
    {
        if (!TextUtils.isEmpty(note.getRecurrenceRule())) {
            long nextReminder = DateHelper.nextReminderFromRecurrenceRule(Long.parseLong(note.getAlarm()),
                    note.getRecurrenceRule());
            if (nextReminder > 0) {
                updateNoteReminder(nextReminder, note, true);
            }
        } else {
            new SaveNoteTask(false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, note);
        }
    }

    private static void updateNoteReminder(long reminder, Note note) {
        updateNoteReminder(reminder, note, false);
    }

    private static void updateNoteReminder(long reminder, Note noteToUpdate, boolean updateNote) {
        if (updateNote)
            noteToUpdate.setAlarm(reminder);
        else {
            NotificationController.addReminder(MySchedulerApp.getInstance(), noteToUpdate, reminder);
            DateHelper.getNoteReminderText(noteToUpdate.getAlarm(), reminder);
        }
    }

    /*public static void setNextRecurrentReminder(AgendaModel model) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd, yyyy");
        Date d = null;
        try{
            d = formatter.parse(model.getDueDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (model.getmRecurrenceRule() != null || !model.getmRecurrenceRule().equals("n/a")) {
            long nextReminder = DateHelper.nextReminderFromRecurrenceRule(d.getTime(), model.getmRecurrenceRule());
            if (nextReminder > 0) {
                updateAgendaReminder(nextReminder, model);
            }
        } else {
            new SaveAgendaTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, model);
        }
    }

    private static void updateAgendaReminder(long reminder, AgendaModel model) {
        Date date = new Date(reminder);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", java.util.Locale.getDefault());
        model.setDueDate(dateFormat.format(date));
        new SaveAgendaTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, model);
        NotificationController.notificationTest3(MySchedulerApp.getInstance(), model);
        Log.i(TAG, "snooze agenda reminder reset");
    }*/

    @Override
    public void onReminderPicked(long reminder) {
        if (this.note != null) {
            this.note.setAlarm(reminder);
        } else {
            for (Note note : this.notes) {
                note.setAlarm(reminder);
            }
        }
    }

    @Override
    public void onRecurrenceReminderPicked(String recurrenceRule) {
        if (this.note != null) {
            this.note.setRecurrenceRule(recurrenceRule);
            setNextRecurrentReminder(this.note);
        } else {
            for (Note note : this.notes) {
                note.setRecurrenceRule(recurrenceRule);
                setNextRecurrentReminder(note);
            }
            setResult(RESULT_OK, getIntent());
        }
        finish();
    }
}
