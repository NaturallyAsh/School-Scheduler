package com.example.ashleighwilson.schoolscheduler.editors;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.dialog.SimpleColorDialog;
import com.example.ashleighwilson.schoolscheduler.dialog.SimpleDateDialog;
import com.example.ashleighwilson.schoolscheduler.dialog.SimpleTimeDialog;
import com.example.ashleighwilson.schoolscheduler.timetable.MonthLoader;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekViewEvent;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekViewLoader;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekViewUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeTableEditor extends AppCompatActivity implements
        SimpleTimeDialog.OnDialogResultListener, MonthLoader.MonthLoaderListener
{
    private static final String TAG = TimeTableEditor.class.getSimpleName();

    EditText mTitleEditText;
    TextView mDayEditText;
    EditText mRoomEditText;
    TextView viewColor;

    Calendar time = Calendar.getInstance();
    DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyy");
    TextView mStartTime;
    TextView mEndTime;
    final static private String COLOR_DIALOG = "colorDialog";
    private static final String START_TIME_DIALOG = "SimpleTimeStartDialog";
    private static final String END_TIME_DIALOG = "SimpleTimeEndDialog";
    private static final String DATE_DIALOG = "dateDialog";
    private static final String ARG_ITEM = "event_item";
    public static int START_YEAR, START_MONTH, START_DAY, START_HOUR, START_MINUTE = 0;
    public static int END_YEAR, END_MONTH, END_DAY, END_HOUR, END_MINUTE = 0;
    static private int subColor;
    private boolean mSubjectHasChanged = false;
    boolean isEvent = true;
    DbHelper dbHelper;
    private WeekViewLoader mWeekViewLoader;
    WeekViewEvent event;
    boolean checkEventCreatedSuccessfully = true;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_timetable_event);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //setHasOptionsMenu(true);

        Intent intent = getIntent();
        if (intent.getExtras() != null)
        {
            Bundle bundle = intent.getExtras();
            if (bundle.containsKey("event"))
                event = (WeekViewEvent) bundle.get("event");
        }

        mWeekViewLoader = new MonthLoader(this);

        Button colorSelector = findViewById(R.id.timetable_create_color);
        viewColor = findViewById(R.id.timetable_view_color);

        subColor = getMatColor("200");
        viewColor.setBackgroundColor(subColor);


        mTitleEditText = findViewById(R.id.edit_timetable_name);
        mDayEditText = findViewById(R.id.edit_timetable_day);
        mRoomEditText = findViewById(R.id.room_timetable);
        mStartTime = findViewById(R.id.timetable_start_time);
        mEndTime = findViewById(R.id.timetable_end_time);

        colorSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleColorDialog.build()
                        .title("Pick a subject color")
                        .colorPreset(Color.WHITE)
                        .allowCustom(false)
                        .show(TimeTableEditor.this, COLOR_DIALOG);
            }
        });

        mStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleTimeDialog.build()
                        .neut()
                        .hour(12).minute(0)
                        .set24HourView(false)
                        .show(TimeTableEditor.this, START_TIME_DIALOG);
            }
        });

        mEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleTimeDialog.build()
                        .neut()
                        .hour(12).minute(0)
                        .set24HourView(false)
                        .show(TimeTableEditor.this, END_TIME_DIALOG);
            }
        });

        mDayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateDialog.build()
                        .firstDayOfWeek(Calendar.MONDAY)
                        .show(TimeTableEditor.this, DATE_DIALOG);
            }
        });
    }

    private int getMatColor(String typeColor)
    {
        int returnColor = Color.BLACK;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.BLACK);
            colors.recycle();
        }
        return returnColor;
    }

    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        if (dialogTag.equals(COLOR_DIALOG))
        {
            subColor = extras.getInt(SimpleColorDialog.COLOR);
            viewColor.setBackgroundColor(subColor);
            return true;
        }
        if (dialogTag.equals(START_TIME_DIALOG))
        {
            if (which == BUTTON_POSITIVE)
            {
                Calendar calender = Calendar.getInstance();
                calender.set(Calendar.HOUR_OF_DAY, extras.getInt(SimpleTimeDialog.HOUR));
                calender.set(Calendar.MINUTE, extras.getInt(SimpleTimeDialog.MINUTE));

                SimpleDateFormat formatter = new SimpleDateFormat("h:mm a", java.util.Locale.getDefault());

                START_HOUR = extras.getInt(SimpleTimeDialog.HOUR);
                START_MINUTE = extras.getInt(SimpleTimeDialog.MINUTE);
                mStartTime.setText(formatter.format(calender.getTime()));
                return true;
            }
        }
        if (dialogTag.equals(END_TIME_DIALOG))
        {
            if (which == BUTTON_POSITIVE)
            {
                Calendar calender = Calendar.getInstance();
                calender.set(Calendar.HOUR_OF_DAY, extras.getInt(SimpleTimeDialog.HOUR));
                calender.set(Calendar.MINUTE, extras.getInt(SimpleTimeDialog.MINUTE));

                SimpleDateFormat formatter = new SimpleDateFormat("h:mm a", java.util.Locale.getDefault());

                END_HOUR = extras.getInt(SimpleTimeDialog.HOUR);
                END_MINUTE = extras.getInt(SimpleTimeDialog.MINUTE);
                mEndTime.setText(formatter.format(calender.getTime()));
                return true;
            }
        }
        if (dialogTag.equals(DATE_DIALOG))
        {
            if (which == BUTTON_POSITIVE)
            {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, START_YEAR);
                c.set(Calendar.YEAR, END_YEAR);
                c.set(Calendar.MONTH, START_MONTH);
                c.set(Calendar.MONTH, END_MONTH);
                c.set(Calendar.DAY_OF_MONTH, START_DAY);
                c.set(Calendar.DAY_OF_MONTH, END_DAY);
                Date date = new Date(extras.getLong(SimpleDateDialog.DATE));

                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", java.util.Locale.getDefault());
                mDayEditText.setText(dateFormat.format(date));
                return true;
            }
        }
        return false;
    }

    public void saveSubject()
    {
        //Event event = new Event();
        //EventEvent eventEvent = new EventEvent();
        String nameString = mTitleEditText.getText().toString().trim();
        String startString = mStartTime.getText().toString().trim();
        String endString = mEndTime.getText().toString().trim();
        String roomString = mRoomEditText.getText().toString().trim();
        //String dayString = mDayEditText.getText().toString().trim();

        Calendar startTime = Calendar.getInstance();
        startTime.set(START_YEAR, START_MONTH, START_DAY, START_HOUR, START_MINUTE);
        Calendar endTime = Calendar.getInstance();
        endTime.set(END_YEAR, END_MONTH, END_DAY, END_HOUR, END_MINUTE);

        Log.i(TAG,"AddEventToList" + nameString + roomString + subColor);

        WeekViewEvent createdEvent;
        createdEvent = new WeekViewEvent(WeekViewUtil.eventId++, nameString, startTime, endTime);
        createdEvent.setColor(subColor);
        createdEvent.setLocation(roomString);

        if (event != null && !event.getStartTime().equals(startTime))
        {
            int periodToFetch = (int) mWeekViewLoader.toWeekViewPeriodIndex(event.getStartTime());
            int year = periodToFetch / 12;
            int month = periodToFetch % 12 + 1;
            String monthKey = "" + (month - 1) + "-" + year;

            List<WeekViewEvent> eventListByMonth = WeekViewUtil.monthMasterEvents.get(monthKey);

            if (eventListByMonth != null && eventListByMonth.contains(event))
            {
                eventListByMonth.remove(event);
            }
            WeekViewUtil.monthMasterEvents.put(monthKey, eventListByMonth);
        }

        WeekViewUtil.masterEvents.put("" + createdEvent.getId(), createdEvent);
        int periodToFetch = (int) mWeekViewLoader.toWeekViewPeriodIndex(startTime);
        int year = periodToFetch / 12;
        int month = periodToFetch % 12 + 1;
        String monthKey = "" + (month - 1) + "-" + year;

        List<WeekViewEvent> eventListByMonth = WeekViewUtil.monthMasterEvents.get(monthKey);
        if (eventListByMonth == null)
        {
            eventListByMonth = new ArrayList<>();
        }
        eventListByMonth.add(createdEvent);
        WeekViewUtil.monthMasterEvents.put(monthKey, eventListByMonth);
        setResult(RESULT_OK);
        finish();

    }

    @Override
    public List<WeekViewEvent> onMonthLoad(int newYear, int newMonth)
    {
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_subject_editor, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_save:
                saveSubject();
                getFragmentManager().popBackStack();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mSubjectHasChanged)
                {
                    //onBackPressed();
                    getFragmentManager().popBackStack();
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       getFragmentManager().popBackStack();
                    }
                };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onBackPressed()
    {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0)
        {
            manager.popBackStack();
        }
        else
            super.onBackPressed();
    } */

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (dialog != null)
                    dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this subject?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (dialog != null)
                    dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}