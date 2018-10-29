package com.example.ashleighwilson.schoolscheduler.data;

import android.os.AsyncTask;
import android.util.Log;

import com.example.ashleighwilson.schoolscheduler.notes.Note;
import com.example.ashleighwilson.schoolscheduler.notes.NoteLoadedEvent;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class NoteLoaderTask extends AsyncTask<Object, Void, ArrayList<Note>> {

    private static final String TAG = NoteLoaderTask.class.getSimpleName();
    private static NoteLoaderTask instance;
    private DbHelper dbHelper = DbHelper.getInstance();


    public static NoteLoaderTask getInstance() {
        instance = new NoteLoaderTask();
        return instance;
    }

    @Override
    public ArrayList<Note> doInBackground(Object... params) {

        ArrayList<Note> notes = new ArrayList<>();
        String methodName = params[0].toString();
        //notes = dbHelper.getAllNotes();
        if (params.length < 2 || params[1] == null){
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
        } else {
            Object methodArgs = params[1];
            Class[] paramClass = new Class[]{methodArgs.getClass()};
            try {
                Method method = dbHelper.getClass().getDeclaredMethod(methodName, paramClass);
                notes = (ArrayList<Note>) method.invoke(dbHelper, paramClass[0].cast(methodArgs));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return notes;
    }

    @Override
    public void onPostExecute(ArrayList<Note> notes) {
        super.onPostExecute(notes);
        EventBus.getDefault().post(new NoteLoadedEvent(notes));
    }
}
