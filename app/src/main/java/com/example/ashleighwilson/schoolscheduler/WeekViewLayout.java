package com.example.ashleighwilson.schoolscheduler;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alamkanak.weekview.WeekView;
import com.appeaser.sublimepickerlibrary.recurrencepicker.EventRecurrence;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.editors.TimeTableEditor;
import com.example.ashleighwilson.schoolscheduler.models.WeekViewEvent;
import com.example.ashleighwilson.schoolscheduler.timetable.DateTimeInterpreter;
import com.example.ashleighwilson.schoolscheduler.timetable.MonthLoader;
import com.example.ashleighwilson.schoolscheduler.timetable.NewWeekView;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekViewUtil;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WeekViewLayout extends AppCompatActivity implements
        MonthLoader.MonthLoaderListener, NewWeekView.EmptyViewClickListener,
        NewWeekView.EventClickListener, NewWeekView.EventLongPressListener {

    private static final String TAG = WeekViewLayout.class.getSimpleName();

    private NewWeekView newWeekView;
    private static String INSTANCE_ARG = "weekType";
    private int TYPE_DAY_VIEW = 1;
    private int TYPE_WEEK_VIEW = 3;
    private int WeekType = TYPE_WEEK_VIEW;
    private OverviewActivity mOverviewActivity;
    private WeekViewFragment weekViewFragment;
    private List<WeekViewEvent> eventListByMonth;
    private String monthKey;
    private WeekViewEvent dbEvent;
    private DbHelper dbHelper;
    private Toolbar toolbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_week_view);

        newWeekView = findViewById(R.id.weekView2);
        toolbar = findViewById(R.id.timetableToolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Timetable");

        newWeekView.setMonthLoaderListener(this);
        newWeekView.setEmptyViewClickListener(this);
        newWeekView.setOnEventClickListener(this);
        newWeekView.setEventLongPressListener(this);

        //weekViewFragment = new WeekViewFragment();

        dbHelper = DbHelper.getInstance();

        setUpWeekView();
        setupDateTimeInterpreter(true);

    }

    private void setUpWeekView() {
        if (newWeekView != null) {

            if (WeekType != TYPE_WEEK_VIEW) {
                WeekType = TYPE_WEEK_VIEW;
                setupDateTimeInterpreter(true);
                newWeekView.setRefreshEvents(true);
                newWeekView.setNumberOfVisibleDays(7);
                newWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                newWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                newWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
            }
        }
    }

    @Override
    public List<WeekViewEvent> onMonthLoad(int newYear, int newMonth) {
        monthKey = "" + (newMonth - 1) + "-" + newYear;
        eventListByMonth = WeekViewUtil.monthMasterEvents.get(monthKey);
        //Log.i(TAG, "event by month size: " + eventListByMonth.size());
        if (eventListByMonth == null)
        {
            eventListByMonth = new ArrayList<>();
        }
        else
        {
            eventListByMonth.clear();
        }

        List<WeekViewEvent> events = new ArrayList<>();

        return events;
    }

    private void setupDateTimeInterpreter(final boolean shortDate) {
        newWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour, int minutes) {
                String strMinutes = String.format("%02d", minutes);
                if (hour > 11) {
                    return (hour - 12) + ":" + strMinutes + " PM";
                } else {
                    if (hour == 0) {
                        return "12:" + strMinutes + " AM";
                    } else {
                        return hour + ":" + strMinutes + " AM";
                    }
                }
            }
        });
    }


    @Override
    public void onEmptyViewClicked(Calendar time) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Add new event?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                time.get(Calendar.HOUR_OF_DAY);
                time.get(Calendar.MINUTE);
                time.get(Calendar.SECOND);
                showNewEventScreen(null, time);
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

    }
    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

    }

    /*@Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.action_week, menu);
        menu.removeItem(R.id.action_month_view);
        //TODO: Figure out how to give fragToolbar options
    }*/

    private void showNewEventScreen(WeekViewEvent event, Calendar time) {
        //TODO: fix double entry that shows in fragment timetable
        Intent intent = new Intent(this, TimeTableEditor.class);
        Bundle bundle = new Bundle();
        if (event != null) {
            bundle.putSerializable("event", event);
        }
        if (time != null) {
            bundle.putSerializable("start", time);
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.action_timetable, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        newWeekView.notifyDatasetChanged();
    }
}
