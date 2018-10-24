package com.example.ashleighwilson.schoolscheduler.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.ashleighwilson.schoolscheduler.MySchedulerApp;
import com.example.ashleighwilson.schoolscheduler.notes.Note;
import com.example.ashleighwilson.schoolscheduler.notes.NoteLoadedEvent;
import com.example.ashleighwilson.schoolscheduler.notes.OnNoteSaved;
import com.example.ashleighwilson.schoolscheduler.utils.DateHelper;

public class SaveNoteTask extends AsyncTask<Note, Void, Note> {

    private static final String TAG = SaveNoteTask.class.getSimpleName();

    private Context mContext;
    private boolean mUpdateLastMod = true;
    private OnNoteSaved mOnNoteSaved;
    private DbHelper dbHelper;
    public NoteLoadedEvent loadedEvent;

    public SaveNoteTask(OnNoteSaved onNoteSaved, boolean upDateLastMod) {
        super();
        this.mContext = MySchedulerApp.getInstance();
        this.mOnNoteSaved = onNoteSaved;
        this.mUpdateLastMod = upDateLastMod;

        dbHelper = new DbHelper(mContext);
    }

    public SaveNoteTask(boolean updateLastMod) {
        this(null, updateLastMod);
    }

    @Override
    protected Note doInBackground(Note... params) {
        Note note = params[0];
        boolean reminderMustBeSet = DateHelper.isFuture(note.getAlarm());
        if (reminderMustBeSet) {
            note.setReminderFired(false);
        }
        note = dbHelper.updateNote(note, mUpdateLastMod);
        if (reminderMustBeSet) {
            NotificationController.scheduleReminder(mContext, note);
        }
        return note;
    }

    @Override
    protected void onPostExecute(Note note) {
        super.onPostExecute(note);
        if (this.mOnNoteSaved != null) {
            mOnNoteSaved.onNoteSaved(note);
            Log.i(TAG, "OnNoteSaved: " + mOnNoteSaved);
            //loadedEvent.notes.add(note);
        }
    }
}
