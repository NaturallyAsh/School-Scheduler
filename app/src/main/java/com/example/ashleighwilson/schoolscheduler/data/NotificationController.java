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
public class NotificationController
{
    private static final String TAG = NotificationController.class.getSimpleName();

    private Context mContext;
    private AlarmManager alarmManager;
    private ArrayList<PendingIntent> list;
    private AgendaModel agendaModel;
    public static final String ARG_ITEM = "agenda_item";
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

    public void notificationTest3(AgendaModel model)
    {
        AlarmManager alrmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        this.agendaModel = model;
        //Log.i(TAG, "recurrence: " + agendaModel.getmRecurrence());

        String dueDate = agendaModel.getDueDate();
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
            //long timeToNotify = futureDay.getTimeInMillis() - 86500000;
            long reminder = futureDay.getTimeInMillis();
            long timeToNotify = setNextRecurrence(agendaModel.getmRecurrenceOption(),
                    agendaModel.getmRecurrenceRule(), reminder);
            Intent notifyEvent = new Intent(mContext, NotificationReceiver.class);
            notifyEvent.putExtra(ARG_ITEM, agendaModel);
            id = (int) System.currentTimeMillis();
            notifyEvent.putExtra(NotificationReceiver.NOTIFICATION_ID, id);
            //PendingIntent pendingIntent = PendingIntent.getActivity(mContext, id, notifyEvent,
              //      PendingIntent.FLAG_CANCEL_CURRENT);
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, id, notifyEvent, flags);
            alrmManager.set(AlarmManager.RTC_WAKEUP, timeToNotify, pendingIntent);


            NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext, MySchedulerApp.CHANNEL_ID)
                    .setContentTitle(agendaModel.getAgendaTitle())
                    .setContentText("Alarm Set: " + dueDate).setSound(alarmSound)
                    .setSmallIcon(R.drawable.notification_important_black_18dp)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(id, notification.build());

        }
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
