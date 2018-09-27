package com.example.ashleighwilson.schoolscheduler;

import android.app.Application;

public class MySchedulerApp extends Application
{
    private static MySchedulerApp myApp;

    public static MySchedulerApp getInstance()
    {
        return myApp;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        myApp = this;
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
    }
}
