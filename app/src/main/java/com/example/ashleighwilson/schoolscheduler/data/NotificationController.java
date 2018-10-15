package com.example.ashleighwilson.schoolscheduler.data;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.ashleighwilson.schoolscheduler.AgendaFrag;
import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.models.AgendaModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
public class NotificationController
{
    private Context mContext;
    private AlarmManager alarmManager;
    private ArrayList<PendingIntent> list;
    private AgendaModel model;
    public static final String CHANNEL_ID = "channel_id";

    public NotificationController(Context context)
    {
        this.mContext = context;
        this.alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        this.list = new ArrayList<>();
    }

    public void scheduleNotification()
    {
        for (int i = 0; i < list.size(); i++)
        {
            alarmManager.cancel(list.get(i));
        }
        list.clear();


        //ArrayList<AgendaModel> noteList = new ArrayList<>();
        //noteList = model.getAllList();
        int id = model.getmId();
        long timeToNotify = model.getmInterval();
        Intent notify = new Intent(mContext, NotificationReceiver.class);
        //notify.putExtra("id", id);
        final int _id = (int) System.currentTimeMillis();
        PendingIntent notifyIntent = PendingIntent.getBroadcast(mContext, _id, notify, 0);
        list.add(notifyIntent);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeToNotify, notifyIntent);
    }

    public void notificationTest2(String title, String date)
    {
        Intent intent = new Intent(mContext, AgendaFrag.class);

        int requestID = (int) System.currentTimeMillis();
        int flags = PendingIntent.FLAG_CANCEL_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, requestID, intent, flags);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(date)
                .setSmallIcon(R.drawable.notification_important_black_18dp)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(requestID, notification.build());
    }

    public void notificationTest3(String title, String date)
    {
        AgendaModel model = new AgendaModel();
        AlarmManager alrmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String dueDate = date;
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd, yyyy");
        Date d = null;
        try {
            d = formatter.parse(dueDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar futureDay = Calendar.getInstance();
        futureDay.setTime(d);
        Calendar curr = Calendar.getInstance();
        if (futureDay.after(curr))
        {
            long timeToNotify = futureDay.getTimeInMillis() - 1000 * 60 * 5;
            Intent notifyEvent = new Intent(mContext, NotificationReceiver.class);
            notifyEvent.putExtra("title", title);
            final int id = (int) System.currentTimeMillis();
            //notifyEvent.putExtra(NotificationReceiver.NOTIFICATION_ID, id);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, id, notifyEvent, 0);
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            //PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, id, intent, flags);
            alrmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeToNotify, AlarmManager.INTERVAL_DAY, pendingIntent);


            NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText("DUE: " + date).setSound(alarmSound)
                    .setSmallIcon(R.drawable.notification_important_black_18dp)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(id, notification.build());

        }
    }
}
