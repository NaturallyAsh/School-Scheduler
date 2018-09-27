package com.example.ashleighwilson.schoolscheduler;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ashleighwilson.schoolscheduler.adapter.CalenderFragAdapter;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.editors.TimeTableEditor;
import com.example.ashleighwilson.schoolscheduler.timetable.OnFragmentInteractionListener;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekViewBase;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekViewEvent;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekViewUtil;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class CalenderFrag extends Fragment implements OnFragmentInteractionListener
{
    DbHelper dbHelper;

    private static long ARG_EVENT_ID;
    private static final String TAG = CalenderFrag.class.getSimpleName();

    View view;
    private FloatingActionMenu fab_all_cal;
    private com.github.clans.fab.FloatingActionButton fab_cal;
    private com.github.clans.fab.FloatingActionButton fab_cal_rec;
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    WeekViewBase mWeekViewBase;
    private HashMap<String, List<WeekViewEvent>> eventHash = new HashMap();
    private List<WeekViewEvent> calendarEvent = new ArrayList<>();


    public CalenderFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ARG_EVENT_ID = WeekViewUtil.eventId;
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        if (view == null)
        {
            view = inflater.inflate(R.layout.fragment_calender, container, false);

        }

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        dbHelper = new DbHelper(getActivity());

        fab_all_cal = view.findViewById(R.id.fab_all_cal);
        fab_cal = view.findViewById(R.id.fab_cal);
        fab_cal_rec = view.findViewById(R.id.fab_cal_rec);

        fab_all_cal.showMenu(true);
        fab_all_cal.setClosedOnTouchOutside(true);
        fab_cal.setOnClickListener(mListener);
        fab_cal_rec.setOnClickListener(mListener);

        if (savedInstanceState != null)
        {
            mWeekViewBase = (WeekViewBase)getChildFragmentManager().findFragmentByTag("WeekViewBase");
        }
        else
        {
            mWeekViewBase = WeekViewBase.newInstance(ARG_EVENT_ID);
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.add(R.id.fragment_container, mWeekViewBase, "WeekViewBase");
            ft.addToBackStack(null);
            ft.commit();
        }

        if (eventHash != null)
            eventHash = WeekViewUtil.getHashMap();
        Log.i(TAG, "hashMap: " + eventHash);

        FloatingClicked();

        return view;
    }

    private void FloatingClicked()
    {
        fab_all_cal.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId())
                {
                    case R.id.fab_cal:

                        break;
                    case R.id.fab_cal_rec:
                        break;
                    default:
                        fab_all_cal.close(true);
                        break;
                }
                fab_all_cal.toggle(true);
            }
        });
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean check = fab_all_cal.isOpened();
            switch (view.getId())
            {
                case R.id.fab_cal:
                    startActivity(new Intent(getContext(), TimeTableEditor.class));
                    /*TimeTableEditor fragment = new TimeTableEditor();
                    FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, fragment);
                    ft.addToBackStack("TimeEdit");
                    ft.commit(); */
                    fab_all_cal.close(true);
                    break;
                case R.id.fab_cal_rec:
                    startActivity(new Intent(getContext(), RecordActivity.class));
                    fab_all_cal.close(true);
                    break;
                default:
                    fab_all_cal.close(true);
            }
        }
    };

    public void refreshCalendar()
    {
        WeekViewBase fragment = new WeekViewBase();
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //refreshData(calendarEvent);
        eventDatabaseList();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        //refreshCalendar();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        //inflater.inflate(R.menu.calendar, menu);
        //super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void refreshData(List<WeekViewEvent> data) {
        //mWeekViewBase.loadEvents(calendarEvent);
        calendarEvent = data;

        Log.i(TAG, "calendarEvent: " + calendarEvent);
    }

    public void eventDatabaseList()
    {
        calendarEvent.clear();
        Cursor cursor = dbHelper.fetchEvents();

        while (cursor.moveToNext())
        {
            long id = cursor.getLong(0);
            String name = cursor.getString(1);
            String location = cursor.getString(2);
            Calendar start = Calendar.getInstance();
            start.setTimeInMillis(cursor.getLong(3));
            Calendar end = Calendar.getInstance();
            end.setTimeInMillis(cursor.getLong(4));
            int color = cursor.getInt(5);

            WeekViewEvent event = new WeekViewEvent(id, name, location, start, end, color);

            calendarEvent.add(event);
        }

        if (!(calendarEvent.size() < 1))
        {

        }
        else
        {

        }
    }
}
