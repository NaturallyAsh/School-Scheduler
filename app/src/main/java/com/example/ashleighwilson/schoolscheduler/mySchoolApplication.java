package com.example.ashleighwilson.schoolscheduler;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import com.example.ashleighwilson.schoolscheduler.timetable.CalendarDB;
import com.example.ashleighwilson.schoolscheduler.timetable.CalendarObject;
import com.example.ashleighwilson.schoolscheduler.timetable.CalendarObjectList;
import com.example.ashleighwilson.schoolscheduler.timetable.EventList;
import com.example.ashleighwilson.schoolscheduler.timetable.EventListHandler;

import java.io.IOException;
import java.util.AbstractCollection;

public class mySchoolApplication extends Application
{
    private static final String TAG = mySchoolApplication.class.getSimpleName();
    private static mySchoolApplication mySchoolApp;

    public mySchoolApplication getInstance()
    {
        return mySchoolApp;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        mySchoolApp = this;
        Log.i(TAG, "App started");

        try {
            EventListHandler.initEventList();
            CalendarObjectList<? extends AbstractCollection<? extends CalendarObject>, ? extends CalendarObject> list0 = CalendarDB.retriveListLocal(0, this);
            EventListHandler.setEventList((EventList) list0);

        } catch (Exception e) {
            Log.e(TAG, "Error");
            try {
                CalendarDB.initDBLocal(this);
            } catch (IOException ex) {
                Log.e(TAG, "double error");
            }
        }
    }
}
