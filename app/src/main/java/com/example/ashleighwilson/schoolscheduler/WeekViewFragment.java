package com.example.ashleighwilson.schoolscheduler;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.ashleighwilson.schoolscheduler.adapter.EventAdapter;
import com.example.ashleighwilson.schoolscheduler.editors.TimeTableEditor;
import com.example.ashleighwilson.schoolscheduler.timetable.DateTimeInterpreter;
import com.example.ashleighwilson.schoolscheduler.timetable.Event;
import com.example.ashleighwilson.schoolscheduler.timetable.EventRect;
import com.example.ashleighwilson.schoolscheduler.timetable.ExtendedCalendarView;
import com.example.ashleighwilson.schoolscheduler.timetable.MonthLoader;
import com.example.ashleighwilson.schoolscheduler.timetable.OnFragmentInteractionListener;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekView;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekViewEvent;
import static com.example.ashleighwilson.schoolscheduler.timetable.WeekViewUtil.masterEvents;
import static com.example.ashleighwilson.schoolscheduler.timetable.WeekViewUtil.monthMasterEvents;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public abstract class WeekViewFragment extends Fragment implements WeekView.EventClickListener,
        MonthLoader.MonthLoaderListener, WeekView.EventLongPressListener,
        WeekView.EmptyViewLongPressListener, ExtendedCalendarView.OnDayClickListener,
        ExtendedCalendarView.OnMonthChaneListener, View.OnClickListener,
        WeekView.EmptyViewClickListener
{
    private static final String TAG = WeekViewFragment.class.getSimpleName();

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private static final int TYPE_MONTH_VIEW = 4;
    private int mWeekViewType = TYPE_MONTH_VIEW;
    private WeekView mWeekView;
    View rootView;
    private ExtendedCalendarView calendar;
    private FloatingActionButton add_event;
    private RecyclerView eventList;
    private EventAdapter eventAdapter;
    private AppBarLayout mAppBarLayout;
    TextView monthView, weekView, dayView;
    Toolbar toolbar;
    WeekViewEvent weekViewEvent;
    Calendar weekViewTime;
    OnFragmentInteractionListener listener;

    public void onAttachToParent(Fragment fragment)
    {
        try {
            listener = (OnFragmentInteractionListener) fragment;
        }
        catch (ClassCastException e) {
            throw new ClassCastException("Must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        onAttachToParent(getParentFragment());

        Bundle bundle = getArguments();
        //weekViewEvent = (WeekViewEvent) bundle.getSerializable("event");
    }

    /*public static WeekViewFragment newInstance(WeekViewEvent event)
    {

    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)

    {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        rootView = inflater.inflate(R.layout.week_view_fragment, container, false);

        toolbar = (Toolbar)getActivity().findViewById(R.id.week_toolbar);

        mAppBarLayout = (AppBarLayout) rootView.findViewById(R.id.app_bar_cal);

        calendar = (ExtendedCalendarView) rootView.findViewById(R.id.calendar);

        calendar.setOnDayClickListener(this);

        calendar.setMonthLoaderListener(this);

        calendar.setOnMonthChangeListener(this);

        //ViewCompat.setNestedScrollingEnabled(eventList, true);

        eventList = (RecyclerView) rootView.findViewById(R.id.event_RV);
        eventList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) rootView.findViewById(R.id.weekView);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthLoaderListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set long press listener for empty view
        mWeekView.setEmptyViewLongPressListener(this);

        mWeekView.setEmptyViewClickListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);
        setHasOptionsMenu(true);

        if (mWeekViewType == TYPE_MONTH_VIEW) {
            //calendarViewLayout.setVisibility(View.VISIBLE);
//            calendar.setVisibility(View.VISIBLE);
            updateView();
            mWeekView.setVisibility(View.GONE);
//            eventListLayout.setVisibility(View.VISIBLE);
        } else {
            // calendarViewLayout.setVisibility(View.GONE);
//            calendar.setVisibility(View.GONE);
            mWeekView.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
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
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.calendar, menu);

        MenuItem actionViewItem = menu.findItem(R.id.calendar_action);

        // Retrieve the action-view from menu

        //View v = MenuItemCompat.getActionView(actionViewItem);
        View v = (View) actionViewItem.getActionView();

        // Find the button within action-view

        monthView = (TextView) v.findViewById(R.id.month_view);
        monthView.setOnClickListener(this);

        weekView = (TextView) v.findViewById(R.id.week_view);
        weekView.setOnClickListener(this);

        dayView = (TextView) v.findViewById(R.id.day_view);
        dayView.setOnClickListener(this);

        monthView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.today));
        weekView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.normal_day));
        dayView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.normal_day));
        //super.onCreateOptionsMenu(menu, inflater);
        //return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDayClicked(AdapterView<?> adapter, View view, int position, long id, Event event)
    {
        if (view != null)
        {
            view.setBackgroundResource(R.drawable.normal_day);
        }
        buildEventList(event);
    }

    private void buildEventList(Event eventObj)
    {
        List<WeekViewEvent> events = eventObj.events;

        if (events == null || events.size() == 0)
        {
            eventList.setVisibility(View.GONE);
            Calendar cal = Calendar.getInstance();
            cal.set(eventObj.getYear(), eventObj.getMonth(), eventObj.getDay());
            showEventDetailsScreen(null, cal);
        }
        else
        {
            eventList.setVisibility(View.VISIBLE);
            if (eventAdapter == null)
            {
                eventAdapter = new EventAdapter(getContext(), events, this);
                eventList.setAdapter(eventAdapter);
            }
            else
            {
                eventAdapter.setData(events);
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {


        //super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (mWeekViewType == TYPE_MONTH_VIEW) {
            updateView();
            mWeekView.setRefreshEvents(true);
        } else if (mWeekViewType == TYPE_WEEK_VIEW) {
            mWeekView.notifyDatasetChanged();
        } else if (mWeekViewType == TYPE_DAY_VIEW) {
            mWeekView.notifyDatasetChanged();
        }
        //listener.refreshData();
    }

    public void notifyWeekView()
    {
        mWeekView.notifyDatasetChanged();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        /*android.support.v4.app.FragmentManager manager = getFragmentManager();
        if (manager.getBackStackEntryCount() > 0)
        {
            manager.popBackStack();
        } */
        listener = null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.month_view) {
            if (mWeekViewType != TYPE_MONTH_VIEW) {
                mWeekViewType = TYPE_MONTH_VIEW;
                mWeekView.setVisibility(View.GONE);
                mAppBarLayout.setVisibility(View.VISIBLE);
                updateView();
                monthView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.today));
                weekView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.normal_day));
                dayView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.normal_day));
            }
        } else if (v.getId() == R.id.week_view) {
            if (mWeekViewType != TYPE_WEEK_VIEW) {
                setupDateTimeInterpreter(true);
                mWeekView.setVisibility(View.VISIBLE);
                eventList.setVisibility(View.GONE);
                mAppBarLayout.setVisibility(View.GONE);
                mWeekViewType = TYPE_WEEK_VIEW;
                mWeekView.setNumberOfVisibleDays(7);

                //getSupportActionBar().setTitle(getTitle());
                // Lets change some dimensions to best fit the view.
                mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));

                monthView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.normal_day));
                weekView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.today));
                dayView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.normal_day));
            }
        } else if (v.getId() == R.id.day_view) {
            if (mWeekViewType != TYPE_DAY_VIEW) {
                setupDateTimeInterpreter(false);
                mWeekView.setVisibility(View.VISIBLE);
                eventList.setVisibility(View.GONE);
                mAppBarLayout.setVisibility(View.GONE);
                mWeekViewType = TYPE_DAY_VIEW;
                mWeekView.setNumberOfVisibleDays(1);
                //getSupportActionBar().setTitle(getTitle());
                // Lets change some dimensions to best fit the view.
                mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));

                monthView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.normal_day));
                weekView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.normal_day));
                dayView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.today));
            }
        } else if (v.getId() == android.R.id.text1) {
            WeekViewEvent event = (WeekViewEvent) v.getTag();
            showEventDetailsScreen(event, null);
        }
    }

    @Override
    public void onMonthChange(Calendar cal, List<EventRect> mEventRects) {
       if (calendar.mAdapter != null)
       {
           if (calendar.mAdapter.currentDay != null)
           {
               buildEventList(calendar.mAdapter.currentDay);
           }
       }
    }



    public void updateView()
    {
        calendar.setGesture(ExtendedCalendarView.LEFT_RIGHT_GESTURE);
        calendar.getEvents();
        calendar.refreshCalendar();
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        //addNewEvent();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Add new event?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showEventDetailsScreen(null, time);
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
        showEventDetailsScreen(event, null);
    }

    @Override
    public void onEmptyViewClicked(Calendar time)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Add new event?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showEventDetailsScreen(null, time);
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
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

    }

    protected String getEventTitle(Calendar time, Calendar endTime)
    {
        //return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
        return String.format("Event of %02d:%02d %s/%d :: %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH),
            endTime.get(Calendar.HOUR_OF_DAY), endTime.get(Calendar.MINUTE), endTime.get(Calendar.MONTH)+1, endTime.get(Calendar.DAY_OF_MONTH));
    }

    public WeekView getWeekView()
    {
        return mWeekView;
    }

    private void showEventDetailsScreen(WeekViewEvent event, Calendar startTime)
    {
        Intent intent = new Intent(getActivity(), TimeTableEditor.class);
        Bundle bundle = new Bundle();
        if (event != null)
        {
            bundle.putSerializable("event", event);
        }
        else if (startTime != null)
        {
            bundle.putSerializable("start", startTime);
        }
        intent.putExtras(bundle);
        getActivity().startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "request" + requestCode + "result: " + resultCode + "data: " + data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK)
            if (requestCode == 1) {
                if (mWeekViewType == TYPE_MONTH_VIEW) {
                    updateView();
                    mWeekView.setRefreshEvents(true);
                } else if (mWeekViewType == TYPE_WEEK_VIEW) {
                    mWeekView.notifyDatasetChanged();
                } else if (mWeekViewType == TYPE_DAY_VIEW) {
                    mWeekView.notifyDatasetChanged();
                }
            }
    }
}
