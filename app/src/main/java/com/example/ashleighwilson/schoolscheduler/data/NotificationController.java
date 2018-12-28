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
import android.os.Bundle;
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
        Calendar futureDay = Calendar.getInstance();
        futureDay.setTime(calendar.getTime());
        Calendar curr = Calendar.getInstance();

        long timeToNotify = calendar.getTimeInMillis();
        //Log.i(TAG, "future TTN: " + timeToNotify);
        if (futureDay.after(curr))
        {
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
        }
        //Log.i(TAG, "After remind TTN: " + timeToNotify);
        sendToReceiver(mContext, model, timeToNotify);
    }

    private static long remindBeforeDate(long dateTime, long time) {

        return dateTime - time;
    }

    public static void sendToReceiver(Context mContext, AgendaModel model, long timeToNotify) {
        AlarmManager alrmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_ITEM, model);

        Intent notifyEvent = new Intent(mContext, NotificationReceiver.class);
        id = model.getmId();
        Log.i(TAG, "model reminder: " + model.getmAddReminder());
        notifyEvent.putExtra(NotificationReceiver.NOTIFICATION_ID, id);
        //notifyEvent.putExtras(bundle);
        notifyEvent.putExtra(ARG_ITEM, ParcelableUtil.marshall(model));

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, id, notifyEvent, flags);
        alrmManager.setExact(AlarmManager.RTC_WAKEUP, timeToNotify, pendingIntent);

        Toast.makeText(mContext, "Alarm set", Toast.LENGTH_SHORT).show();
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
}
