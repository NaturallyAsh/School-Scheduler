package com.example.ashleighwilson.schoolscheduler;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
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
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.example.ashleighwilson.schoolscheduler.timetable.CalendarAdapter;
import com.example.ashleighwilson.schoolscheduler.timetable.DateTimeInterpreter;
import com.example.ashleighwilson.schoolscheduler.timetable.Event;
import com.example.ashleighwilson.schoolscheduler.timetable.EventRect;
import com.example.ashleighwilson.schoolscheduler.timetable.ExtendedCalendarView;
import com.example.ashleighwilson.schoolscheduler.timetable.MonthLoader;
import com.example.ashleighwilson.schoolscheduler.timetable.OnFragmentInteractionListener;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekView;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekViewEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
    TextView monthView, weekView, dayView, todayTV;
    private TextView emptyView;
    private CalendarAdapter calendarAdapter;
    Toolbar toolbar;
    WeekViewEvent weekViewEvent;
    Calendar weekViewTime;
    OnFragmentInteractionListener listener;
    private List<WeekViewEvent> events;

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
        Log.i(TAG, "onCreate!");
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
        Log.i(TAG, "onCreateView");

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        rootView = inflater.inflate(R.layout.week_view_fragment, container, false);

        toolbar = (Toolbar)getActivity().findViewById(R.id.week_toolbar);

        mAppBarLayout = (AppBarLayout) rootView.findViewById(R.id.app_bar_cal);

        emptyView = rootView.findViewById(R.id.empty_calendar_view);

        calendar = (ExtendedCalendarView) rootView.findViewById(R.id.calendar);

        calendar.setOnDayClickListener(this);

        calendar.setMonthLoaderListener(this);

        calendar.setOnMonthChangeListener(this);

        todayTV = rootView.findViewById(R.id.calendar_day_tv);

        eventList = (RecyclerView) rootView.findViewById(R.id.event_RV);
        eventList.setLayoutManager(new LinearLayoutManager(getContext()));
        eventAdapter = new EventAdapter(getContext(), events, this);
        eventList.setAdapter(eventAdapter);

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

        /*Calendar cal = Calendar.getInstance();
        cal.getTimeInMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()); */

        //todayTV.setText(formatter.format(cal.getTime()));
        todayTV.setText(R.string.today_string);

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

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.DOWN |
                ItemTouchHelper.UP, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();

                Collections.swap(events, from, to);
                eventAdapter.notifyItemMoved(from, to);

                return true;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT)
                {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                    builder.setMessage("Are you sure to delete?");
                    builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            eventAdapter.dismissEvent(viewHolder.getAdapterPosition());
                            if (eventAdapter.getItemCount() == 0)
                                updateView();
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            eventAdapter.notifyItemRemoved(position + 1);
                            eventAdapter.notifyItemRangeChanged(position, eventAdapter.getItemCount());
                        }
                    }).show();
                }
            }
        });

        helper.attachToRecyclerView(eventList);

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

            Calendar calendar = Calendar.getInstance();
            calendar.set(event.getYear(), event.getMonth(), event.getDay());

            Calendar tomCal = Calendar.getInstance();
            tomCal.add(Calendar.DAY_OF_WEEK, 1);
            Date today = calendar.getTime();
            Date notToday = tomCal.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd, yyyy");
            String dateString = sdf.format(calendar.getTime());

            if (notToday.compareTo(today) < 1) {
                todayTV.setText(dateString);
            } else {
                todayTV.setText(R.string.today_string);
            }
        }
        buildEventList(event);
    }

    private void buildEventList(Event eventObj)
    {
        events = eventObj.events;

        if (events == null || events.size() == 0)
        {
            eventList.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            Calendar cal = Calendar.getInstance();
            cal.set(eventObj.getYear(), eventObj.getMonth(), eventObj.getDay());
            showEventDetailsScreen(null, cal);
        }
        else
        {
            eventList.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
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
        Log.i(TAG, "onResume!");
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

    public void addLoadedEvents(WeekViewEvent loadedEvent) {
        events = new ArrayList<>();
        events.add(loadedEvent);

        eventAdapter.notifyDataSetChanged();
        Log.i(TAG, "loaded event: " + loadedEvent);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        Log.i(TAG, "onDetach!");
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
               //buildEventList(calendar.mAdapter.currentDay);
           }
       }
    }

    public void updateView()
    {
        calendar.setGesture(ExtendedCalendarView.LEFT_RIGHT_GESTURE);
        calendar.getEvents();
        calendar.refreshCalendar();
        Log.i(TAG, "updateView()!");
        if (events == null || events.size() == 0) {
            Log.i(TAG, "events showing null: " + events);
            eventList.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            eventList.setAdapter(eventAdapter);
            eventAdapter.notifyDataSetChanged();

        } else {
            eventList.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            //eventAdapter = new EventAdapter(getContext(), events, this);
            //eventList.setAdapter(eventAdapter);
        }

    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {

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
                time.get(Calendar.HOUR_OF_DAY);
                time.get(Calendar.MINUTE);
                time.get(Calendar.SECOND);
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

    public String getEventName(String name, Calendar startTime, Calendar endTime) {

        String start = dateFormatter(startTime);
        String end = dateFormatter(endTime);

        String eventMsg = name + "\n" + start + " - " + end;

        return eventMsg;
    }

    private String dateFormatter(Calendar time)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String msg = sdf.format(time.getTime());

        return msg;
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
