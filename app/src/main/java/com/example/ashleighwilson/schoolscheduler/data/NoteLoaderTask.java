package com.example.ashleighwilson.schoolscheduler.data;

import android.os.AsyncTask;

import com.example.ashleighwilson.schoolscheduler.notes.Note;
import com.example.ashleighwilson.schoolscheduler.notes.NoteLoadedEvent;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class NoteLoaderTask extends AsyncTask<Object, Void, ArrayList<Note>> {

    private static NoteLoaderTask instance;

    public static NoteLoaderTask getInstance() {
        instance = new NoteLoaderTask();
        return instance;
    }

    @Override
    protected ArrayList<Note> doInBackground(Object... params) {

        ArrayList<Note> notes = new ArrayList<>();
        String methodName = params[0].toString();
        DbHelper dbHelper = DbHelper.getInstance();
        try {
            Method method = dbHelper.getClass().getDeclaredMethod(methodName);
            notes = (ArrayList<Note>)method.invoke(dbHelper);
        } catch (NoSuchMethodException e) {
            return notes;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return notes;
    }

    @Override
    protected void onPostExecute(ArrayList<Note> notes) {
        super.onPostExecute(notes);
        EventBus.getDefault().post(new NoteLoadedEvent(notes));
        //new NoteLoadedEvent(notes);
    }
}
