package com.example.ashleighwilson.schoolscheduler.data;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.ashleighwilson.schoolscheduler.AgendaFrag;
import com.example.ashleighwilson.schoolscheduler.MySchedulerApp;
import com.example.ashleighwilson.schoolscheduler.NoteListFragment;
import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.models.AgendaModel;
import com.example.ashleighwilson.schoolscheduler.notes.Constants;
import com.example.ashleighwilson.schoolscheduler.notes.Note;
import com.example.ashleighwilson.schoolscheduler.utils.DateHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotificationController
{
    private static final String TAG = NotificationController.class.getSimpleName();

    private Context mContext;
    private AlarmManager alarmManager;
    private ArrayList<PendingIntent> list;
    public static final String ARG_ITEM = "agenda_item";
    public static final String ARG_DUE_DATE = "agenda_due";
    public static final String ARG_TITLE = "agenda_title";
    public static final String ARG_RECUR_OPTION = "agenda_recur_option";
    public static final String ARG_RECUR_RULE = "agenda_recur_rule";
    public static int id;

    public NotificationController(Context context)
    {
        this.mContext = context;
        this.alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        this.list = new ArrayList<>();
    }

    public static void scheduleReminder(Context context, Note note)
    {
        if (note.getAlarm() != null) {
            addReminder(context, note, Long.parseLong(note.getAlarm()));
        }
    }

    public static void addReminder(Context context, Note note, long reminder)
    {

        if (DateHelper.isFuture(reminder)) {


            SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd, yyyy");
            Date date = new Date(reminder);


            Calendar futureDay = Calendar.getInstance();
            futureDay.setTime(date);

            long timeToNotify = futureDay.getTimeInMillis() - 1000 * 60 * 5;

            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(Constants.INTENT_NOTE, ParcelableUtil.marshall(note));
            PendingIntent sender = PendingIntent.getBroadcast(context, getRequestCode(note), intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            //alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder, sender);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeToNotify, AlarmManager.INTERVAL_DAY,
                    sender);
        }
    }

    static int getRequestCode(Note note) {
        Long longCode = note.getCreation() != null ? note.getCreation() :
                Calendar.getInstance().getTimeInMillis() / 1000L;
        return longCode.intValue();
    }

    public static void notificationTest3(Context mContext, AgendaModel model, Calendar calendar)
    {
        //Log.i(TAG, "calendar: " + calendar.get(Calendar.YEAR) + "," + calendar.get(Calendar.MONTH) + "," +
          //      calendar.get(Calendar.DAY_OF_MONTH) + "," + calendar.get(Calendar.HOUR_OF_DAY) + "," + calendar.get(Calendar.MINUTE));


        Calendar futureDay = Calendar.getInstance();
        futureDay.setTime(calendar.getTime());
        Calendar curr = Calendar.getInstance();

        long timeToNotify = 0;
        timeToNotify = calendar.getTimeInMillis();
        Log.i(TAG, "future TTN: " + timeToNotify);
        if (model.getmAddReminder() != 0) {
            switch ((int) model.getmAddReminder()) {
                case 0:
                    timeToNotify = remindBeforeDate(calendar.getTimeInMillis(), 0);
                    break;
                case 10 * 60000:
                    timeToNotify = remindBeforeDate(calendar.getTimeInMillis(), 10 * 60000);
                    break;
                case 30 * 60000:
                    timeToNotify = remindBeforeDate(calendar.getTimeInMillis(), 30 * 60000);
                    break;
                case 60 * 60000:
                    timeToNotify = remindBeforeDate(calendar.getTimeInMillis(), 60 * 60000);
                    break;
                case 60 * 24 * 60000:
                    timeToNotify = remindBeforeDate(calendar.getTimeInMillis(), 60 * 24 * 60000);
                    break;
                case 60 * 24 * 7 * 60000:
                    timeToNotify = remindBeforeDate(calendar.getTimeInMillis(), 60 * 24 * 7 * 60000);
                    break;
                default:
                    throw new RuntimeException("error with repeat alarm");
            }
        }
        /*if (futureDay.after(curr))
        {

        }*/
        Log.i(TAG, "After remind TTN: " + timeToNotify);
        sendToReceiver(mContext, model, timeToNotify);
    }

    private static long remindBeforeDate(long dateTime, long time) {

        return dateTime - time;
    }

    public static void sendToReceiver(Context mContext, AgendaModel model, long timeToNotify) {
        Log.i(TAG, "send to receiver called");
        AlarmManager alrmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent notifyEvent = new Intent(mContext, NotificationReceiver.class);
        id = model.getmId();
        notifyEvent.putExtra(NotificationReceiver.NOTIFICATION_ID, id);
        //notifyEvent.putExtra(ARG_ITEM, ParcelableUtil.marshall(model));

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, id, notifyEvent, flags);
        alrmManager.setExact(AlarmManager.RTC_WAKEUP, timeToNotify, pendingIntent);

        Toast.makeText(mContext, "Alarm set", Toast.LENGTH_SHORT).show();


        /*NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext, MySchedulerApp.CHANNEL_ID)
                .setContentTitle(model.getAgendaTitle())
                .setContentText("Due on: " + model.getDueDate()).setSound(alarmSound)
                .setSmallIcon(R.drawable.notification_important_black_18dp)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification.build());*/
    }

    public boolean checkNotification(Context context) {
        return PendingIntent.getBroadcast(context, id, new Intent(context,
                NotificationReceiver.class), PendingIntent.FLAG_NO_CREATE) != null;
    }

    public static void removeReminder(Context context, Note note) {
        if (!TextUtils.isEmpty(note.getAlarm())) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, NotificationReceiver.class);
            PendingIntent p = PendingIntent.getBroadcast(context, getRequestCode(note), intent, 0);
            am.cancel(p);
            p.cancel();
        }
    }

    public static void showReminderMessage(String reminderString) {
        if (reminderString != null) {
            long reminder = Long.parseLong(reminderString);
            if (reminder > Calendar.getInstance().getTimeInMillis()) {
                new Handler(MySchedulerApp.getInstance().getMainLooper()).post(() -> Toast.makeText(MySchedulerApp.getInstance(),
                        "Reminder set for " + DateHelper.getDateTimeShort(MySchedulerApp.getInstance(),
                                reminder), Toast.LENGTH_LONG).show());
            }
        }
    }

    public static long setNextRecurrence(String recurrenceOption, String recurrenceRule, long reminder) {
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
        Date date = new Date(timeToNotify);
        Log.i(TAG, "time to notify: " + date);
        return timeToNotify;
    }
}
