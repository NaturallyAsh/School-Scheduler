package com.example.ashleighwilson.schoolscheduler.data;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.ashleighwilson.schoolscheduler.AgendaFrag;
import com.example.ashleighwilson.schoolscheduler.MySchedulerApp;
import com.example.ashleighwilson.schoolscheduler.OverviewActivity;
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
        //agendaModel = dbHelper.getAllAgendas(id);
        calendar = Calendar.getInstance();
        Log.i(TAG, "agenda reminder: " + agendaModel.getmAddReminder());

        createNotification(agendaModel, id, context);

        if (agendaModel.getmAddReminder() == 0) {
            switch (agendaModel.getmRepeatType()) {
                case 1: calendar.add(Calendar.DATE, 1); break;
                case 2: calendar.add(Calendar.WEEK_OF_YEAR, 1); break;
                case 3: calendar.add(Calendar.MONTH, 1); break;
                case 4: calendar.add(Calendar.YEAR, 1); break;
                case 5: setDayOfWeek(); break;
            }
        }

        if (agendaModel.getDateTime() > calendar.getTimeInMillis()) {
            calendar.setTimeInMillis(agendaModel.getDateTime());

            long newReminder = 0;
            agendaModel.setmAddReminder(newReminder);
            dbHelper.updateAgenda(agendaModel);

            //Intent alarmIntent = new Intent(context, NotificationReceiver.class);
            NotificationController.notificationTest3(context, agendaModel, calendar);
        } else if (agendaModel.getmRepeatType() != 0) {
            NotificationController.notificationTest3(context, agendaModel, calendar);
        }
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
                Log.i(TAG, "DOW: " + position + 1);
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
                .setContentText("Due Today!" + ":" + model.getAgendaTitle()).setSound(alarmSound)
                .setVibrate(new long[]{1000, 1000, 1000, 1000});

        Intent resultIntent = new Intent(context, AgendaFrag.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(OverviewActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(id, mBuilder.build());
        Log.i(TAG, "notification received");
    }
}
