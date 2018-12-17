package com.example.ashleighwilson.schoolscheduler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.editors.TimeTableEditor;
import com.example.ashleighwilson.schoolscheduler.timetable.CalendarAdapter;
import com.example.ashleighwilson.schoolscheduler.timetable.DateTimeInterpreter;
import com.example.ashleighwilson.schoolscheduler.timetable.Event;
import com.example.ashleighwilson.schoolscheduler.timetable.EventRect;
import com.example.ashleighwilson.schoolscheduler.timetable.ExtendedCalendarView;
import com.example.ashleighwilson.schoolscheduler.timetable.MonthLoader;
import com.example.ashleighwilson.schoolscheduler.timetable.OnFragmentInteractionListener;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekView;
import com.example.ashleighwilson.schoolscheduler.models.WeekViewEvent;

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
    Toolbar toolbar;
    Calendar dayCalendar;
    private Event dayEventObj;
    CalendarAdapter calendarAdapter;
    OnFragmentInteractionListener listener;
    public List<WeekViewEvent> events = new ArrayList<>();
    private Event allEvents = new Event();
    private DbHelper dbHelper;

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
        setHasOptionsMenu(true);
        setRetainInstance(true);

        onAttachToParent(getParentFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)

    {
        Log.i(TAG, "onCreateView");

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        rootView = inflater.inflate(R.layout.week_view_fragment, container, false);

        toolbar = getActivity().findViewById(R.id.week_toolbar);

        mAppBarLayout = rootView.findViewById(R.id.app_bar_cal);

        dbHelper = DbHelper.getInstance();



        emptyView = rootView.findViewById(R.id.empty_calendar_view);

        calendar = rootView.findViewById(R.id.calendar);

        calendar.setOnDayClickListener(this);

        calendar.setMonthLoaderListener(this);

        calendar.setOnMonthChangeListener(this);

        todayTV = rootView.findViewById(R.id.calendar_day_tv);

        add_event = rootView.findViewById(R.id.week_view_fab);

        eventList = rootView.findViewById(R.id.event_RV);
        eventAdapter = new EventAdapter(getContext(), events, this);
        //eventList.setAdapter(eventAdapter);
        eventList.setLayoutManager(new LinearLayoutManager(getContext()));
        calendar.refreshCalendar();
        updateEventAdapter();

        // Get a reference for the week view in the layout.
        mWeekView = rootView.findViewById(R.id.weekView);

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

        todayTV.setText(R.string.today_string);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);

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

        add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewEventScreen(null, dayCalendar);
            }
        });

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
                            calendar.getEvents();
                            mWeekView.notifyDatasetChanged();
                            updateEventAdapter();
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
    public void onDayClicked(AdapterView<?> adapter, View view, int position, long id, Event event)
    {
        if (view != null)
        {
            view.setBackgroundResource(R.drawable.normal_day);

            dayCalendar = Calendar.getInstance();
            dayCalendar.set(event.getYear(), event.getMonth(), event.getDay());

            Calendar tomCal = Calendar.getInstance();
            tomCal.add(Calendar.DAY_OF_WEEK, 1);
            //Log.i(TAG, "tom cal: " + tomCal.get(Calendar.DAY_OF_WEEK));


            Calendar yesCal = Calendar.getInstance();
            yesCal.add(Calendar.DAY_OF_WEEK, -1);
            //Log.i(TAG, "yes cal: " + yesCal.get(Calendar.DAY_OF_WEEK));

            Date today = dayCalendar.getTime();
            Date notToday = tomCal.getTime();
            Date yesterday = yesCal.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd, yyyy");
            String dateString = sdf.format(dayCalendar.getTime());

            if (notToday.compareTo(today) < 1 || today.compareTo(yesterday) < 1) {
                todayTV.setText(dateString);
            } else {
                todayTV.setText(R.string.today_string);
            }
        }
        buildEventList(event);
    }

    private void buildEventList(Event eventObj)
    {
        Log.i(TAG, "build events");
        dayEventObj = eventObj;
        events = dayEventObj.events;
        Log.i(TAG, "build events size: " + events.size());

        if (events == null || events.size() == 0)
        {
            eventList.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            /*Calendar cal = Calendar.getInstance();
            cal.set(eventObj.getYear(), eventObj.getMonth(), eventObj.getDay());
            showEventDetailsScreen(null, cal);*/
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
                eventAdapter.notifyDataSetChanged();
            }
        }

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

    public void updateView()
    {
        calendar.setGesture(ExtendedCalendarView.LEFT_RIGHT_GESTURE);
        calendar.getEvents();
        calendar.refreshCalendar();
        Log.i(TAG, "updateView()!");
        updateEventAdapter();
    }

    public void updateEventAdapter() {
        if (eventAdapter.getItemCount() == 0) {
            eventList.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            eventList.setAdapter(eventAdapter);
            eventAdapter.notifyDataSetChanged();

        } else {
            eventList.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            //eventAdapter = new EventAdapter(getContext(), events, this);
            eventList.setAdapter(eventAdapter);
            eventAdapter.setData(events);
            eventAdapter.notifyDataSetChanged();
        }
    }

    public void notifyWeekView()
    {
        mWeekView.notifyDatasetChanged();
    }

    public void addLoadedEvents(List<WeekViewEvent> loadedEvent) {
        //events.addAll(loadedEvent);
        //eventList.setAdapter(eventAdapter);
        //eventAdapter.setData(events);
        //eventAdapter.notifyDataSetChanged();
        //Log.i(TAG, "loaded event: " + loadedEvent.size());
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_week, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_month_view:
                if (mWeekViewType != TYPE_MONTH_VIEW) {
                    mWeekViewType = TYPE_MONTH_VIEW;
                    mWeekView.setVisibility(View.GONE);
                    mAppBarLayout.setVisibility(View.VISIBLE);
                    add_event.setVisibility(View.VISIBLE);
                    updateView();}
                return true;
            case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    setupDateTimeInterpreter(false);
                    mWeekView.setVisibility(View.VISIBLE);
                    eventList.setVisibility(View.GONE);
                    mAppBarLayout.setVisibility(View.GONE);
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);
                    add_event.setVisibility(View.INVISIBLE);
                    //getSupportActionBar().setTitle(getTitle());
                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));}
                return true;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    setupDateTimeInterpreter(true);
                    mWeekView.setRefreshEvents(true);
                    mWeekView.setVisibility(View.VISIBLE);
                    eventList.setVisibility(View.GONE);
                    mAppBarLayout.setVisibility(View.GONE);

                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);
                    add_event.setVisibility(View.INVISIBLE);
                    //getSupportActionBar().setTitle(getTitle());
                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));}
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {


        //super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onMonthChange(Calendar cal, List<EventRect> mEventRects) {
       if (calendar.mAdapter != null)
       {
           if (calendar.mAdapter.currentDay != null)
           {
               Log.i(TAG, "month change build events");
               buildEventList(calendar.mAdapter.currentDay);
           }
       }
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {

    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        showEventDetailScreen(event);
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

    private void showEventDetailScreen(WeekViewEvent event) {
        Intent intent = new Intent(getActivity(), EventDetailActivity.class);
        Bundle bundle = new Bundle();
        if (event != null) {
            bundle.putSerializable("event", event);
        }
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }

    private void showNewEventScreen(WeekViewEvent event, Calendar startTime)
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
