package com.example.ashleighwilson.schoolscheduler;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.WeekView;
import com.appeaser.sublimepickerlibrary.recurrencepicker.EventRecurrence;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.models.WeekViewEvent;
import com.example.ashleighwilson.schoolscheduler.timetable.DateTimeInterpreter;
import com.example.ashleighwilson.schoolscheduler.timetable.MonthLoader;
import com.example.ashleighwilson.schoolscheduler.timetable.NewWeekView;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekViewUtil;

import static com.example.ashleighwilson.schoolscheduler.OverviewActivity.fragToolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WeekViewLayout extends Fragment {

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

    public WeekViewLayout() {}

    public static WeekViewLayout getInstance(int weekType) {
        WeekViewLayout frag = new WeekViewLayout();
        Bundle bundle = new Bundle();
        bundle.putInt(INSTANCE_ARG, weekType);
        frag.setArguments(bundle);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mOverviewActivity = (OverviewActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.new_week_view, container, false);

        newWeekView = rootView.findViewById(R.id.weekView2);
        if (newWeekView != null) {
            newWeekView.setVisibility(View.VISIBLE);

        }
        if (fragToolbar != null) {
            fragToolbar.setVisibility(View.VISIBLE);
            fragToolbar.getContext().setTheme(R.style.ThemeOverlay_AppCompat_Dark_ActionBar);
            fragToolbar.setTitle("Timetable");
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(fragToolbar);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
        dbHelper = DbHelper.getInstance();

        if (getArguments() != null) {
            WeekType = getArguments().getInt(INSTANCE_ARG);
        }

        setUpWeekView();
        setupDateTimeInterpreter(true);

        return rootView;
    }

    private void setUpWeekView() {
        if (newWeekView != null) {

            newWeekView.setMonthLoaderListener(new MonthLoader.MonthLoaderListener() {
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

                    Cursor cursor = dbHelper.fetchEvents();

                    while (cursor.moveToNext())
                    {
                        long id = cursor.getLong(0);
                        String name = cursor.getString(1);
                        String location = cursor.getString(2);
                        Calendar start = Calendar.getInstance();
                        start.setTimeInMillis(cursor.getLong(3));
                        start.set(Calendar.HOUR_OF_DAY, start.get(Calendar.HOUR_OF_DAY));
                        start.set(Calendar.MINUTE, start.get(Calendar.MINUTE));
                        start.set(Calendar.MONTH, newMonth -1);
                        start.set(Calendar.YEAR, newYear);
                        Calendar end = Calendar.getInstance();
                        //end = (Calendar) start.clone();
                        end.setTimeInMillis(cursor.getLong(4));
                        end.set(Calendar.HOUR_OF_DAY, end.get(Calendar.HOUR_OF_DAY));
                        end.set(Calendar.MINUTE, end.get(Calendar.MINUTE));
                        end.set(Calendar.MONTH, newMonth -1);
                        int color = cursor.getInt(5);
                        String rule = cursor.getString(6);
                        //String dayOfWeek = cursor.getString(7);

                        dbEvent = new WeekViewEvent(id, name, start, end);
                        dbEvent.setColor(color);
                        dbEvent.setLocation(location);
                        dbEvent.setmRecurrenceRule(rule);

                        events.add(dbEvent);
                        WeekViewUtil.masterEvents.put("" + dbEvent.getId(), dbEvent);
                    }


                    eventListByMonth.addAll(events);
                    WeekViewUtil.monthMasterEvents.put(monthKey, eventListByMonth);

                    return events;
                }
            });
        }
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
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.action_week, menu);
        menu.removeItem(R.id.action_month_view);
        //TODO: Figure out how to give fragToolbar options
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //menu.clear();
        //inflater.inflate(R.menu.action_week, menu);
        //menu.removeItem(R.id.action_month_view);
        //super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_day_view:

                return true;
            case R.id.action_week_view:
                if (WeekType != TYPE_WEEK_VIEW) {
                    WeekType = TYPE_WEEK_VIEW;
                    setupDateTimeInterpreter(true);
                    newWeekView.setRefreshEvents(true);
                    newWeekView.setNumberOfVisibleDays(7);
                    newWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    newWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    newWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                return true;
            case android.R.id.home:
                mOverviewActivity.getSupportFragmentManager().popBackStack();
                fragToolbar.setVisibility(View.GONE);
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
