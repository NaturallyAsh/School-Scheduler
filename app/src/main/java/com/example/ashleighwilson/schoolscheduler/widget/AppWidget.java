package com.example.ashleighwilson.schoolscheduler.widget;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.ashleighwilson.schoolscheduler.NotesActivity;
import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.notes.Constants;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider
{

    public void setLayout(Context context, AppWidgetManager appWidgetManager, int widgetId) {

        Intent intentCamera = new Intent(context, NotesActivity.class);
        intentCamera.setAction(Constants.ACTION_WIDGET_TAKE_PHOTO);
        intentCamera.putExtra(Constants.INTENT_WIDGET, widgetId);
        @SuppressLint("WrongConstant") PendingIntent pendingCameraIntent = PendingIntent.getActivity(context, widgetId, intentCamera,
                Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent intentAdd = new Intent(context, NotesActivity.class);
        intentAdd.setAction(Constants.ACTION_WIDGET);
        intentAdd.putExtra(Constants.INTENT_WIDGET, widgetId);
        @SuppressLint("WrongConstant") PendingIntent pendingAddIntent = PendingIntent.getActivity(context, widgetId, intentAdd,
                Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent intentRecord = new Intent(context, NotesActivity.class);
        intentRecord.setAction(Constants.ACTION_WIDGET_RECORD);
        intentRecord.putExtra(Constants.INTENT_WIDGET, widgetId);
        @SuppressLint("WrongConstant") PendingIntent pendingRecordIntent = PendingIntent.getActivity(context, widgetId, intentRecord,
                Intent.FLAG_ACTIVITY_NEW_TASK);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget_layout);
        views.setOnClickPendingIntent(R.id.widget_camera, pendingCameraIntent);
        views.setOnClickPendingIntent(R.id.widget_add, pendingAddIntent);
        views.setOnClickPendingIntent(R.id.widget_mic, pendingRecordIntent);

        appWidgetManager.updateAppWidget(widgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            setLayout(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

