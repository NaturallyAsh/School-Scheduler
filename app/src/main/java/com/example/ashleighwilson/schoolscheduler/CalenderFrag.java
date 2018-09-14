package com.example.ashleighwilson.schoolscheduler;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ashleighwilson.schoolscheduler.adapter.CalenderFragAdapter;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.editors.TimeTableEditor;
import com.example.ashleighwilson.schoolscheduler.models.TimeTableModel;
import com.example.ashleighwilson.schoolscheduler.timetable.ByteArrayViaString;
import com.example.ashleighwilson.schoolscheduler.timetable.CalendarDB;
import com.example.ashleighwilson.schoolscheduler.timetable.CalendarObject;
import com.example.ashleighwilson.schoolscheduler.timetable.CalendarObjectList;
import com.example.ashleighwilson.schoolscheduler.timetable.EventListHandler;
import com.example.ashleighwilson.schoolscheduler.timetable.Serializer;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekViewBase;
import com.github.clans.fab.FloatingActionMenu;
import com.github.eunsiljo.timetablelib.data.TimeData;
import com.github.eunsiljo.timetablelib.data.TimeTableData;
import com.github.eunsiljo.timetablelib.view.TimeTableView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.os.AsyncTask;


public class CalenderFrag extends Fragment
{
    DbHelper dbHelper;
    TimeTableView tableView;
    //test titles
    private List<String> mTitles = Arrays.asList("Math", "Science", "Computers", "Theatre", "Physics");
    //private List<String> mDays = Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat");
    long today = 0;
    int colorData = 0;
    int row = 0;
    //private static DateTime mNow = DateTime.now();
    private long mNow = 0;
    ArrayList<TimeTableData> tableData;
    ArrayList<TimeTableModel> tableModel;

    ArrayList<TimeData> mon = new ArrayList<>();
    ArrayList<TimeData> tue = new ArrayList<>();
    ArrayList<TimeData> wed = new ArrayList<>();
    ArrayList<TimeData> thu = new ArrayList<>();
    ArrayList<TimeData> fri = new ArrayList<>();
    ArrayList<TimeData> sat = new ArrayList<>();

    private FloatingActionMenu fab_all_cal;
    private com.github.clans.fab.FloatingActionButton fab_cal;
    private com.github.clans.fab.FloatingActionButton fab_cal_rec;
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public CalenderFragAdapter calFragAdapter;
    private HttpURLConnection connection = null;
    private static String urlServer = "https://calendarplusproject.herokuapp.com/CalendarS";



    public CalenderFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_calender, container, false);

        fab_all_cal = view.findViewById(R.id.fab_all_cal);
        fab_cal = view.findViewById(R.id.fab_cal);
        fab_cal_rec = view.findViewById(R.id.fab_cal_rec);

        fab_all_cal.showMenu(true);
        fab_all_cal.setClosedOnTouchOutside(true);
        fab_cal.setOnClickListener(mListener);
        fab_cal_rec.setOnClickListener(mListener);

        WeekViewBase fragment = new WeekViewBase();
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack("Cal");
        ft.commit();

        //recyclerView = view.findViewById(R.id.cal_frag_RV);
        //recyclerView.setHasFixedSize(true);

        //calFragAdapter = new CalenderFragAdapter(getContext());

        //layoutManager = new LinearLayoutManager(getActivity());
        //recyclerView.setLayoutManager(layoutManager);

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
                    //startActivity(new Intent(getContext(), TimeTableEditor.class));
                    TimeTableEditor fragment = new TimeTableEditor();
                    FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, fragment);
                    ft.addToBackStack("TimeEdit");
                    ft.commit();
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
        refreshCalendar();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        refreshCalendar();

        try {
            CalendarDB.updateListLocal(0, EventListHandler.getEventList(), getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
