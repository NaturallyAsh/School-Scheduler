package com.example.ashleighwilson.schoolscheduler;


import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashleighwilson.schoolscheduler.adapter.CalenderFragAdapter;
import com.example.ashleighwilson.schoolscheduler.adapter.RecyclerSubAdapter;
import com.example.ashleighwilson.schoolscheduler.editors.SubjectsEditorActivity;
import com.example.ashleighwilson.schoolscheduler.utils.DateTimeInterpreter;
import com.example.ashleighwilson.schoolscheduler.utils.MonthLoader;
import com.example.ashleighwilson.schoolscheduler.utils.WeekView;
import com.example.ashleighwilson.schoolscheduler.utils.WeekViewEvent;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.models.TimeTableModel;
import com.github.clans.fab.FloatingActionMenu;
import com.github.eunsiljo.timetablelib.adapter.TableAdapter;
import com.github.eunsiljo.timetablelib.adapter.TimeTableAdapter;
import com.github.eunsiljo.timetablelib.data.TimeData;
import com.github.eunsiljo.timetablelib.data.TimeGridData;
import com.github.eunsiljo.timetablelib.data.TimeTableData;
import com.github.eunsiljo.timetablelib.view.TimeTableView;
import com.github.eunsiljo.timetablelib.viewholder.TimeTableItemViewHolder;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


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
        fab_cal.setOnClickListener(mListener);
        fab_cal_rec.setOnClickListener(mListener);

        recyclerView = view.findViewById(R.id.cal_frag_RV);
        recyclerView.setHasFixedSize(true);

        calFragAdapter = new CalenderFragAdapter(getContext());

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

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
                    startActivity(new Intent(getContext(), WeekViewActivity.class));
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
}
