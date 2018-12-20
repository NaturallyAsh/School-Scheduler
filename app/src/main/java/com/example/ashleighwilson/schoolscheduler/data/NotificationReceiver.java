package com.example.ashleighwilson.schoolscheduler.data;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.ashleighwilson.schoolscheduler.AgendaFrag;
import com.example.ashleighwilson.schoolscheduler.MySchedulerApp;
import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.SnoozeActivity;
import com.example.ashleighwilson.schoolscheduler.models.AgendaModel;
import com.example.ashleighwilson.schoolscheduler.utils.AlarmUtil;
import com.example.ashleighwilson.schoolscheduler.utils.DateHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = NotificationReceiver.class.getSimpleName();

    public static String NOTIFICATION_ID = "notification_id";
    private AgendaModel agendaModel;
    private Calendar calendar;
    private DbHelper dbHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        agendaModel = ParcelableUtil.unmarshall(intent.getExtras().getByteArray(NotificationController.ARG_ITEM),
                AgendaModel.CREATOR);
        dbHelper = DbHelper.getInstance();
        calendar = Calendar.getInstance();

        createNotification(agendaModel, id, context);

        switch (agendaModel.getmRepeatType()) {
            case 1: calendar.add(Calendar.DATE, 1); break;
            case 2: calendar.add(Calendar.WEEK_OF_YEAR, 1); break;
            case 3: calendar.add(Calendar.MONTH, 1); break;
            case 4: calendar.add(Calendar.YEAR, 1); break;
            case 5: setDayOfWeek(); break;
        }

        calendar.setTimeInMillis(agendaModel.getTimeToNotify());

        Intent alarmIntent = new Intent(context, NotificationReceiver.class);
        AlarmUtil.setAlarm(context, alarmIntent, id, calendar);
    }

    public void setDayOfWeek() {
        int day = getNextDay();
        if (day <= calendar.get(Calendar.DAY_OF_WEEK)) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        }
        calendar.set(Calendar.DAY_OF_WEEK, day);
    }

    public int getNextDay() {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.add(Calendar.DATE, 1);
        for (int i = 0; i < 7; i++) {
            int position = (i + (mCalendar.get(Calendar.DAY_OF_WEEK) - 1)) %
                    agendaModel.getmDayOfWeek().length;
            if (agendaModel.getmDayOfWeek()[position]) {
                return position + 1;
            }
        }
        return 0;
    }

    private void createNotification(AgendaModel model, int id, Context context) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, MySchedulerApp.CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_important_black_18dp)
                .setContentTitle(model.getAgendaTitle())
                .setContentText("Due Today!").setSound(alarmSound)
                .setVibrate(new long[]{1000, 1000, 1000, 1000});

        Intent resultIntent = new Intent(context, AgendaFrag.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(AgendaFrag.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(id, mBuilder.build());
        Log.i(TAG, "notification received");
    }

    private void testRefire(Context context, String title, String dueDate, String option,
                String rule, int id) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        AlarmManager alrmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //this.agendaModel = model;

        String date = dueDate;
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd, yyyy");
        Date d = null;
        try {
            d = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar futureDay = Calendar.getInstance();
        futureDay.setTime(d);
        Calendar curr = Calendar.getInstance();
        if (futureDay.after(curr))
        {
            long reminder = futureDay.getTimeInMillis();
            long timeToNotify = setNextRecurrence(option,
                    rule, reminder);
            Intent notifyEvent = new Intent(context, NotificationReceiver.class);
            notifyEvent.putExtra("title", title);
            notifyEvent.putExtra("dueDate", date);
            notifyEvent.putExtra(NOTIFICATION_ID, id);
            //PendingIntent pendingIntent = PendingIntent.getActivity(mContext, id, notifyEvent,
            //      PendingIntent.FLAG_CANCEL_CURRENT);
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, notifyEvent, flags);
            alrmManager.set(AlarmManager.RTC_WAKEUP, timeToNotify, pendingIntent);


            NotificationCompat.Builder notification = new NotificationCompat.Builder(context, MySchedulerApp.CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText("Alarm Re-Set: " + date).setSound(alarmSound)
                    .setSmallIcon(R.drawable.notification_important_black_18dp)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(id, notification.build());

        }
        else {
            Log.i(TAG, "unable to set notification receiver");
        }
    }

    public long setNextRecurrence(String recurrenceOption, String recurrenceRule, long reminder) {
        long timeToNotify = 0;

        switch (recurrenceOption) {
            case "DAILY":
                timeToNotify = 86500000;
                Log.i(TAG, "daily set");
                break;
            case "WEEKLY":
                timeToNotify = 86500000 * 7;
                Log.i(TAG, "weekly set");
                break;
            case "CUSTOM":
                if (recurrenceRule != null || !(recurrenceRule.equals("n/a"))) {
                    timeToNotify = DateHelper.nextReminderFromRecurrenceRule(reminder,
                            recurrenceRule);
                }
                Log.i(TAG, "custom set");
                break;
        }
        Log.i(TAG, "time to notify: " + timeToNotify);
        return timeToNotify;
    }
}
