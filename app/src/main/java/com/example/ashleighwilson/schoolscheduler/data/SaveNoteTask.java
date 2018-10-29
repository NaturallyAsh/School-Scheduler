package com.example.ashleighwilson.schoolscheduler.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.ashleighwilson.schoolscheduler.MySchedulerApp;
import com.example.ashleighwilson.schoolscheduler.notes.Attachment;
import com.example.ashleighwilson.schoolscheduler.notes.Note;
import com.example.ashleighwilson.schoolscheduler.notes.NoteLoadedEvent;
import com.example.ashleighwilson.schoolscheduler.notes.OnNoteSaved;
import com.example.ashleighwilson.schoolscheduler.utils.DateHelper;

import java.util.List;

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

        dbHelper = DbHelper.getInstance();
    }

    public SaveNoteTask(boolean updateLastMod) {
        this(null, updateLastMod);
    }

    @Override
    protected Note doInBackground(Note... params) {
        Note note = params[0];
        purgeRemovedAttachments(note);
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

    private void purgeRemovedAttachments(Note note) {
        List<Attachment> deletedAttachments = note.getAttachmentsListOld();
        for (Attachment attachment : note.getAttachmentsList()) {
            if (attachment.getId() != null) {
                if (deletedAttachments.indexOf(attachment) == -1) {
                    attachment = getFixedAttachmentInstance(deletedAttachments, attachment);
                }
                deletedAttachments.remove(attachment);
            }
        }
        //Remove from db deleted attachments
        for (Attachment deletedAttachment : deletedAttachments) {
            Storage.delete(mContext, deletedAttachment.getUri().getPath());
            Log.i(TAG, "Removed attachment: " + deletedAttachment.getUri());
        }
    }

    private Attachment getFixedAttachmentInstance(List<Attachment> deletedAttachments, Attachment attachment) {
        for (Attachment deletedAttachment : deletedAttachments) {
            if (deletedAttachment.getId().equals(attachment.getId())) {
                return deletedAttachment;
            }
        }
        return attachment;
    }

    @Override
    protected void onPostExecute(Note note) {
        super.onPostExecute(note);
        if (this.mOnNoteSaved != null) {
            mOnNoteSaved.onNoteSaved(note);
            //Log.i(TAG, "OnNoteSaved: " + mOnNoteSaved);
        }
    }
}
