package com.example.ashleighwilson.schoolscheduler.data;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.SnoozeActivity;
import com.example.ashleighwilson.schoolscheduler.notes.Constants;
import com.example.ashleighwilson.schoolscheduler.notes.Note;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context mContext, Intent intent) {
        try {
            Note note = ParcelableUtil.unmarshall(intent.getExtras().getByteArray(Constants.INTENT_NOTE),
                    Note.CREATOR);
            createNotification(mContext, note);
            SnoozeActivity.setNextRecurrentReminder(note);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNotification(Context mContext, Note note) {

        String title = "";
        if (note.getTitle() != null) {
            title = note.getTitle();
        } else {
            title = "Note Alert!";
        }
        String text = "";
        if (note.getAlarm() != null) {
            text = note.getAlarm();
        } else {
            text = "Check notes!";
        }

        Intent snoozeIntent = new Intent(mContext, SnoozeActivity.class);
        snoozeIntent.setAction(Constants.ACTION_SNOOZE);
        snoozeIntent.putExtra(Constants.INTENT_NOTE, (android.os.Parcelable) note);
        PendingIntent pSnooze = PendingIntent.getActivity(mContext, getUniqueRequestCode(note),
                snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent = new Intent(mContext, SnoozeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.INTENT_NOTE, note);
        intent.putExtras(bundle);
        intent.setAction(Constants.ACTION_NOTIFICATION_CLICK + Long.toString(System.currentTimeMillis()));

        PendingIntent notifyIntent = PendingIntent.getActivity(mContext, getUniqueRequestCode(note),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, "channel_id")
                .setSmallIcon(R.drawable.notification_important_black_18dp)
                .setContentTitle(title)
                .setContentText(text).setSound(alarmSound)
                .setVibrate(new long[]{1000, 1000, 1000, 1000})
                .addAction(R.drawable.ic_remind_later_light, "Remind me later: ", pSnooze);

        PendingIntent resultIntent = PendingIntent.getActivity(mContext, 0, new Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultIntent);

        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(String.valueOf(note.get_id()), 0, mBuilder.build());
    }

    private int getUniqueRequestCode(Note note) {
        return note.get_id().intValue();
    }
}
