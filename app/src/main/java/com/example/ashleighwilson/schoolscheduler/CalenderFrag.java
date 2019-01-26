package com.example.ashleighwilson.schoolscheduler;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.editors.TimeTableEditor;
import com.example.ashleighwilson.schoolscheduler.timetable.EventsPreference;
import com.example.ashleighwilson.schoolscheduler.models.WeekViewEvent;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CalenderFrag extends Fragment
{
    DbHelper dbHelper;
    private static String MONTH_KEY;
    private static final String TAG = CalenderFrag.class.getSimpleName();
    Context mContext;
    View view;
    private FloatingActionMenu fab_all_cal;
    private com.github.clans.fab.FloatingActionButton fab_cal;
    private com.github.clans.fab.FloatingActionButton fab_cal_rec;
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    WeekViewFragment mWeekViewFrag;
    public static List<WeekViewEvent> readMonthEvent;
    public static List<WeekViewEvent> writeMonthEvent;
    public static List<WeekViewEvent> readPrefHash;
    private List<WeekViewEvent> calendarEvent = new ArrayList<>();
    public EventsPreference pref;
    WeekViewEvent event;
    WeekViewEvent readEvent;

    public CalenderFrag() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate!");
        setHasOptionsMenu(false);
        pref = new EventsPreference(getActivity());
        //ARG_EVENT_ID = WeekViewUtil.eventId;
        setRetainInstance(true);
        mContext = MySchedulerApp.getInstance();
    }

    public static WeekViewFragment newInstance()
    {
        WeekViewFragment fragment = new WeekViewFragment();
        Bundle bundle = new Bundle();
        //bundle.putLong(ARG_EVENT_ID, event_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.i(TAG, "onCreateView!");

        if (view == null)
        {
            view = inflater.inflate(R.layout.fragment_calender, container, false);
            Log.i(TAG, "view is null");
        }

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        dbHelper = DbHelper.getInstance();

        /*fab_all_cal = view.findViewById(R.id.fab_all_cal);
        fab_cal = view.findViewById(R.id.fab_cal);
        fab_cal_rec = view.findViewById(R.id.fab_cal_rec);

        fab_all_cal.showMenu(true);
        fab_all_cal.setClosedOnTouchOutside(true);
        fab_cal.setOnClickListener(mListener);
        fab_cal_rec.setOnClickListener(mListener);*/

        if (savedInstanceState != null)
        {
            mWeekViewFrag = (WeekViewFragment)getChildFragmentManager().findFragmentByTag("WeekViewFrag");
            Log.i(TAG, "saved instance not null: " + mWeekViewFrag);
        }
        else
        {
            mWeekViewFrag = new WeekViewFragment();
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.add(R.id.fragment_container, mWeekViewFrag, "WeekViewFrag");
            ft.addToBackStack(null);
            ft.commit();
        }

        //FloatingClicked();

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
        WeekViewFragment fragment = new WeekViewFragment();
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.i(TAG, "Resumed!");

        /*
        //getting List from SP. works correctly.
        readPrefHash = EventsPreference.getHashEvent();

        if (readPrefHash != null)
        {
            writeMonthEvent = new ArrayList<>(readPrefHash);
            writeList(writeMonthEvent);
            Log.i(TAG, "pref event: " + writeMonthEvent);
        } */
        eventDatabaseList();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.i(TAG, "Paused!");
        /*
        //works correctly
        if (writeMonthEvent != null)
        {
            readMonthEvent = readList();
            Log.i(TAG, "file event: " + readMonthEvent);
        } */

        if (event != null)
        {
            //readEvent = readObj(mContext);
            Log.i(TAG, "read object: " + readEvent);
        }
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
            String rule = cursor.getString(6);

            event = new WeekViewEvent(id, name, location, start, end, color, rule);

            writeObj(mContext, event);

            calendarEvent.add(event);

        }

        if (!(calendarEvent.size() < 1))
        {

        }
        else
        {

        }
    }
    //next 2 methods work correctly to write/read List to file

    /*public void writeList(List<WeekViewEvent> list)
    {
        File directory = new File(getContext().getFilesDir().getAbsolutePath() + File.separator +
            "serialization");
        if (!directory.exists())
            directory.mkdirs();

        String fileName = "eventlist.srl";
        ObjectOutput out = null;

        try {
            out = new ObjectOutputStream(new FileOutputStream(directory + File.separator + fileName));
            out.writeObject(writeMonthEvent);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<WeekViewEvent> readList()
    {
        ObjectInputStream input = null;
        List<WeekViewEvent> returnList = null;
        String fileName = "eventlist.srl";
        File directory = new File(getContext().getFilesDir().getAbsolutePath() + File.separator +
            "serialization");

        try {
            input = new ObjectInputStream(new FileInputStream(directory + File.separator + fileName));
            returnList = (List<WeekViewEvent>)((ObjectInputStream) input).readObject();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return returnList;
    } */

    public void writeObj(Context context, WeekViewEvent obj)
    {
        File directory = new File(context.getFilesDir().getAbsolutePath() + File.separator +
                "serialization");
        if (!directory.exists())
            directory.mkdirs();

        String fileName = "eventlistObj.srl";
        ObjectOutput out = null;

        try {
            out = new ObjectOutputStream(new FileOutputStream(directory + File.separator + fileName));
            out.writeObject(event);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static WeekViewEvent readObj(Context context)
    {
        ObjectInputStream input = null;
        WeekViewEvent returnList = null;
        String fileName = "eventlistObj.srl";
        File directory = new File(context.getFilesDir().getAbsolutePath() + File.separator +
                "serialization");

        try {
            input = new ObjectInputStream(new FileInputStream(directory + File.separator + fileName));
            returnList = (WeekViewEvent) input.readObject();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return returnList;
    }
}
