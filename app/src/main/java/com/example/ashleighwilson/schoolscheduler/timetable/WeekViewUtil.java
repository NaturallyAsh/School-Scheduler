package com.example.ashleighwilson.schoolscheduler.timetable;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.example.ashleighwilson.schoolscheduler.MySchedulerApp;
import com.example.ashleighwilson.schoolscheduler.data.Storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeekViewUtil {

    public static long eventId = 0;

    private static final String TAG = WeekViewUtil.class.getSimpleName();

    public static Storage mStorage;
    public static String file = "/hashEvents.ser";
    public static String path = null;
    public static File directory = null;

    public static HashMap<String, WeekViewEvent> masterEvents = new HashMap();
    public static HashMap<String, List<WeekViewEvent>> monthMasterEvents = new HashMap();

    public static HashMap<String, List<WeekViewEvent>> getMonthMasterHashMap()
    {
        return monthMasterEvents;
    }

    public static HashMap<String, WeekViewEvent> getMasterHashMap()
    {
        return masterEvents;
    }

    public static void saveHasToApp(HashMap<String, WeekViewEvent> master, HashMap<String, List<WeekViewEvent>> monthMaster)
    {
        Context mContext = MySchedulerApp.getInstance();
        //File pathFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), file);
        /*if (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            path = pathFile.getPath();
        }
        else*/
        path = Environment.getExternalStorageState();

        directory = new File(path);
        if (directory.exists()) {
            Log.w(TAG, "Directory '" + path + "' already exists");
            return;
        }
        directory.mkdirs();

        //mStorage.createDirectory(path);
        Log.i(TAG, "path: " + path);

        //File fileName = new File(path);


        try{
            //FileOutputStream fileOut = mContext.openFileOutput(String.valueOf(fileName), Context.MODE_PRIVATE);
            FileOutputStream fileOut = new FileOutputStream(path);
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);

            objOut.writeObject(master);
            objOut.writeObject(monthMaster);
            objOut.flush();
            objOut.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "empty hashmap!");
        }
    }

    public static HashMap<String, WeekViewEvent> readMasterHashToApp()
    {

        HashMap<String, WeekViewEvent> masterRestored = new HashMap<>();
        //File pathFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), file);

        //path = Environment.getExternalStorageState();

        /*directory = new File(path);
        if (directory.exists()) {
            Log.w(TAG, "Directory '" + path + "' already exists");
            return;
        }
        directory.mkdirs();


        //File fileName = new File(path); */
        try{
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(path));
            HashMap<String, WeekViewEvent> myReadInMap = (HashMap<String, WeekViewEvent>) input.readObject();
            //HashMap<String, List<WeekViewEvent>> myReadInMap2 = (HashMap<String, List<WeekViewEvent>>) input.readObject();
            //master = myReadInMap;
            masterRestored = myReadInMap;
            //monthMasterEvents = myReadInMap2;
            Log.i(TAG, "master:" + masterEvents.size() + "monthMaster: " + monthMasterEvents.size());
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return masterRestored;
    }

    public static HashMap<String, List<WeekViewEvent>> readMonthMasterHash()
    {
        HashMap<String, List<WeekViewEvent>> monthRestored = new HashMap<>();
        /*File pathFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), file);

            path = pathFile.getPath();

        /*directory = new File(path);
        if (directory != null && !directory.exists()) {
            Log.w(TAG, "Directory '" + path + "' already exists");
            directory.mkdir();
        } */

        //File fileName = new File(path);
        //Log.i(TAG, "doesExist: " + pathFile);

        try{
            //ObjectInputStream input = new ObjectInputStream(new FileInputStream(path + file));
            //FileInputStream fis = new FileInputStream(fileName);
            //ObjectInputStream ois = new ObjectInputStream(fis);
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(path));
            Log.i(TAG, "input stream: " + input);

            //HashMap<String, WeekViewEvent> myReadInMap = (HashMap<String, WeekViewEvent>) input.readObject();
            HashMap<String, List<WeekViewEvent>> myReadInMap2 = (HashMap<String, List<WeekViewEvent>>) input.readObject();
            //masterEvents = myReadInMap;
            monthRestored = myReadInMap2;
            Log.i(TAG, "master:" + masterEvents.size() + "monthMaster: " + monthMasterEvents.size());
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return monthRestored;
    }


    public static Bundle getHashEvents(Bundle args)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable("hashMap", monthMasterEvents);

        return bundle;
    }

    /////////////////////////////////////////////////////////////////
    //
    //      Helper methods.
    //
    /////////////////////////////////////////////////////////////////

    /**
     * Checks if two times are on the same day.
     * @param dayOne The first day.
     * @param dayTwo The second day.
     * @return Whether the times are on the same day.
     */
    public static boolean isSameDay(Calendar dayOne, Calendar dayTwo) {
        return dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR) && dayOne.get(Calendar.DAY_OF_YEAR) == dayTwo.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Returns a calendar instance at the start of this day
     * @return the calendar instance
     */
    public static Calendar today(){
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        return today;
    }

    public static void sortAndCacheEvents(List<WeekViewEvent> events, List<EventRect> mEventRects)
    {
        sortEvents(events);
        for (WeekViewEvent event: events)
        {
            cacheEvent(event, mEventRects);
        }
    }

    private static void sortEvents(List<WeekViewEvent> events)
    {
        Collections.sort(events, (event1, event2) -> {
            long start1 = event1.getStartTime().getTimeInMillis();
            long start2 = event2.getStartTime().getTimeInMillis();
            int comparator = start1 > start2 ? 1 : (start1 < start2 ? -1 : 0);
            if (comparator == 0) {
                long end1 = event1.getEndTime().getTimeInMillis();
                long end2 = event2.getEndTime().getTimeInMillis();
                comparator = end1 > end2 ? 1 : (end1 < end2 ? -1 : 0);

            }
            return comparator;
        });
    }

    private static void cacheEvent(WeekViewEvent event, List<EventRect> mEventRects)
    {
        if (event.getStartTime().compareTo(event.getEndTime()) >= 0)
            return;
        List<WeekViewEvent> splitedEvents = event.splitWeekViewEvents();
        for (WeekViewEvent splitedEvent: splitedEvents)
        {
            mEventRects.add(new EventRect(splitedEvent, event, null));
        }
    }
}
