package com.example.ashleighwilson.schoolscheduler.editors;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.dialog.SimpleColorDialog;
import com.example.ashleighwilson.schoolscheduler.dialog.SimpleDateDialog;
import com.example.ashleighwilson.schoolscheduler.dialog.SimpleTimeDialog;
import com.example.ashleighwilson.schoolscheduler.timetable.EventsPreference;
import com.example.ashleighwilson.schoolscheduler.timetable.MonthLoader;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekViewEvent;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekViewLoader;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekViewUtil;

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

    private Calendar originalStartTime;
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
    private boolean isEditMode;
    private WeekViewLoader mWeekViewLoader;
    WeekViewEvent event;
    DbHelper dbHelper;
    EventsPreference pref;

    private boolean mSubjectHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mSubjectHasChanged = true;
            return false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_timetable_event);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dbHelper = DbHelper.getInstance();

        pref = new EventsPreference(getApplicationContext());

        Intent intent = getIntent();
        if (intent.getExtras() != null)
        {
            Bundle bundle = intent.getExtras();
            if (bundle.containsKey("event"))
            {
                event = (WeekViewEvent) bundle.get("event");
            }
            if (bundle.containsKey("start"))
            {
                originalStartTime = (Calendar) bundle.get("start");
                Log.i(TAG, "original start time: " + originalStartTime);
            }
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

        mTitleEditText.setOnTouchListener(mTouchListener);
        mRoomEditText.setOnTouchListener(mTouchListener);

        if (event != null)
        {
            mTitleEditText.setText(event.getName());
            mRoomEditText.setText(event.getLocation());
            viewColor.setBackgroundColor(event.getColor());
            setStartEndTime(event.getStartTime(), event.getEndTime());
        }
        else
        {

        }

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
                        .hour(Calendar.HOUR_OF_DAY).minute(Calendar.MINUTE)
                        .set24HourView(false)
                        .show(TimeTableEditor.this, START_TIME_DIALOG);
            }
        });

        mEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleTimeDialog.build()
                        .neut()
                        .hour(Calendar.HOUR_OF_DAY).minute(Calendar.MINUTE)
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

                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", java.util.Locale.getDefault());

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

                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", java.util.Locale.getDefault());

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

                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy, hh:mm a", java.util.Locale.getDefault());
                mDayEditText.setText(dateFormat.format(date));
                return true;
            }
        }
        return false;
    }

    private void setStartEndTime(Calendar start, Calendar end)
    {
        String startTime = dateFormatter(start);
        int commaIndex = startTime.lastIndexOf(",");
        if (commaIndex != -1)
        {
            String date = startTime.substring(0, commaIndex);
            String time = startTime.substring(commaIndex +1, startTime.length());
            mDayEditText.setText(date);
            mStartTime.setText(time);
        }
        else
        {
            mDayEditText.setText(startTime);
        }
        String endTime = dateFormatter(end);
        commaIndex = endTime.lastIndexOf(",");
        if (commaIndex != -1)
        {
            String date = endTime.substring(0, commaIndex);
            String time = endTime.substring(commaIndex +1, endTime.length());
            mEndTime.setText(time);
        }
    }

    private String dateFormatter(Calendar time)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd, yyyy, hh:mm a");
        String msg = sdf.format(time.getTime());

        return msg;
    }

    public void saveSubject()
    {

        String nameString = mTitleEditText.getText().toString().trim();
        String roomString = mRoomEditText.getText().toString().trim();

        Calendar startTime = originalStartTime;
        //Calendar startTime = Calendar.getInstance();
        //startTime.setTimeInMillis(System.currentTimeMillis());
        //startTime.set(Calendar.HOUR_OF_DAY, START_HOUR);
        //startTime.set(Calendar.MINUTE, START_MINUTE);
        if (startTime == null)
        {
            startTime.setTimeInMillis(System.currentTimeMillis());
            //startTime.set(Calendar.HOUR_OF_DAY, START_HOUR);
            //startTime.set(Calendar.MINUTE, START_MINUTE);
            //startTime.set(Calendar.MONTH, START_MONTH);
            //startTime.set(Calendar.YEAR, START_YEAR);
            //startTime.set(START_YEAR, START_MONTH, START_DAY, START_HOUR, START_MINUTE);
        }


        Calendar endTime = (Calendar) startTime.clone();
        //Calendar endTime = Calendar.getInstance();
        endTime.setTimeInMillis(startTime.getTimeInMillis() + (1000 * 60 * 60 * 2));
        //endTime.set(Calendar.HOUR_OF_DAY, END_HOUR);
        //endTime.set(Calendar.MINUTE, END_MINUTE);
        //endTime.set(Calendar.MONTH, END_MONTH);
        //endTime.set(END_YEAR, END_MONTH, END_DAY, END_HOUR, END_MINUTE);
        
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

        WeekViewEvent model = new WeekViewEvent();
        //model.setId(WeekViewUtil.eventId);
        model.setName(nameString);
        model.setLocation(roomString);
        model.setStartTime(startTime);
        model.setEndTime(endTime);
        model.setColor(subColor);

        dbHelper.addTimetable(model);
        Log.i(TAG, "model: " + model);


        eventListByMonth.add(createdEvent);
        WeekViewUtil.monthMasterEvents.put(monthKey, eventListByMonth);

        //below code works correctly to save List to SP
        //pref.setList("key", eventListByMonth);

        setResult(RESULT_OK);
        finish();

    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (event == null)
        {
            if (originalStartTime == null)
            {
                originalStartTime = Calendar.getInstance();
                originalStartTime.setTimeInMillis(System.currentTimeMillis());
            }
            Calendar endTime = (Calendar) originalStartTime.clone();
            endTime.setTimeInMillis(originalStartTime.getTimeInMillis() + (1000 * 60 * 60 *2));
            setStartEndTime(originalStartTime, endTime);

            Log.i(TAG, "onResume start time:" + dateFormatter(originalStartTime) + "onResume end time: " + dateFormatter(endTime));
        }
    }

    @Override
    public List<WeekViewEvent> onMonthLoad(int newYear, int newMonth)
    {
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_timetable_editor, menu);
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
                    NavUtils.navigateUpFromSameTask(TimeTableEditor.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(TimeTableEditor.this);
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
