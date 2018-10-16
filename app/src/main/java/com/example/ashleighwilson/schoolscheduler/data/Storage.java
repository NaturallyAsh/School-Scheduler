package com.example.ashleighwilson.schoolscheduler.data;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.ashleighwilson.schoolscheduler.MySchedulerApp;
import com.example.ashleighwilson.schoolscheduler.notes.Constants;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekViewEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class Storage
{
    private static final String TAG = Storage.class.getSimpleName();

    private final MySchedulerApp mContext;

    public Storage(MySchedulerApp context)
    {
        mContext = context;
    }

    public static boolean checkStorage() {
        boolean mExternalStorageAvailable;
        boolean mExternalStorageWriteable;
        String state = Environment.getExternalStorageState();

        switch (state) {
            case Environment.MEDIA_MOUNTED:
                // We can read and write the media
                mExternalStorageAvailable = mExternalStorageWriteable = true;
                break;
            case Environment.MEDIA_MOUNTED_READ_ONLY:
                // We can only read the media
                mExternalStorageAvailable = true;
                mExternalStorageWriteable = false;
                break;
            default:
                // Something else is wrong. It may be one of many other states, but
                // all we need
                // to know is we can neither read nor write
                mExternalStorageAvailable = mExternalStorageWriteable = false;
                break;
        }
        return mExternalStorageAvailable && mExternalStorageWriteable;
    }

    public static File createNewAttachmentFile(Context mContext, String extension) {
        File f = null;
        if (checkStorage()) {
            f = new File(mContext.getExternalFilesDir(null), createNewAttachmentName(extension));
        }
        return f;
    }


    public static synchronized String createNewAttachmentName(String extension) {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_SORTABLE);
        String name = sdf.format(now.getTime());
        name += extension != null ? extension : "";
        return name;
    }

    public String getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public String getInternalFilesDirectory() {
        return mContext.getFilesDir().getAbsolutePath();
    }

    public static boolean isExternalWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalReadable()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
        {
            return true;
        }
        return false;
    }

    public boolean createDirectory(String path) {
        File directory = new File(path);
        if (directory.exists()) {
            Log.w(TAG, "Directory '" + path + "' already exists");
            return false;
        }
        return directory.mkdirs();
    }

    public boolean deleteFile(String path) {
        File file = new File(path);
        return file.delete();
    }

    public File getFile(String path) {
        return new File(path);
    }

    /*public static void createHashFile(HashMap<String, WeekViewEvent> master, HashMap<String, List<WeekViewEvent>> monthMaster)
    {
        File file =
        try{
            //FileOutputStream fileOut = mContext.openFileOutput(String.valueOf(fileName), Context.MODE_PRIVATE);
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);

            objOut.writeObject(master);
            objOut.writeObject(monthMaster);
            objOut.flush();

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "empty hashmap!");
        }
    } */
}
