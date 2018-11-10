package com.example.ashleighwilson.schoolscheduler;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

public class MySchedulerApp extends Application
{
    private static final String TAG = MySchedulerApp.class.getSimpleName();

    private static MySchedulerApp myApp;
    public static String CHANNEL_ID = "channel_id";
    public static NotificationChannel channel;

    public static MySchedulerApp getInstance()
    {
        return myApp;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.i(TAG, "onCreate");
        myApp = this;
        createChannel();
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
    }

    public void createChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "notification_channel";
            String description = "Reminders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);

        }
    }
}
