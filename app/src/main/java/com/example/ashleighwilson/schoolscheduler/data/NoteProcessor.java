package com.example.ashleighwilson.schoolscheduler.data;

import android.os.AsyncTask;

import com.example.ashleighwilson.schoolscheduler.notes.Note;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public abstract class NoteProcessor {

    List<Note> notes;

    public NoteProcessor(List<Note> noteList) {
        this.notes = new ArrayList<>(noteList);
    }

    public void process() {
        NoteProcessorTask task = new NoteProcessorTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, notes);
    }

    protected abstract void processNote(Note note);

    class NoteProcessorTask extends AsyncTask<List<Note>, Void, Void> {
        @Override
        protected Void doInBackground(List<Note>... params) {
            List<Note> notes = params[0];
            for (Note note : notes) {
                processNote(note);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            EventBus.getDefault().post(new NoteUpdatedEvent());
        }
    }
}
