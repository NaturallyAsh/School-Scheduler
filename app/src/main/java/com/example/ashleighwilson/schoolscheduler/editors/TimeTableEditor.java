package com.example.ashleighwilson.schoolscheduler.editors;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.dialog.SimpleColorDialog;
import com.example.ashleighwilson.schoolscheduler.dialog.SimpleDateDialog;
import com.example.ashleighwilson.schoolscheduler.dialog.SimpleTimeDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeTableEditor extends AppCompatActivity implements
        SimpleTimeDialog.OnDialogResultListener
{
    private static final String TAG = SubjectsEditorActivity.class.getSimpleName();

    private EditText mTitleEditText;
    private TextView mDayEditText;
    private EditText mRoomEditText;
    private TextView viewColor;
    TextView mStartTime;
    TextView mEndTime;
    final static private String COLOR_DIALOG = "colorDialog";
    private static final String START_TIME_DIALOG = "SimpleTimeStartDialog";
    private static final String END_TIME_DIALOG = "SimpleTimeEndDialog";
    private static final String DATE_DIALOG = "dateDialog";
    private static final String ARG_ITEM = "event_item";
    static private int subColor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_timetable_event);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button colorSelector = findViewById(R.id.timetable_create_color);
        viewColor = findViewById(R.id.timetable_view_color);

        subColor = getMatColor("200");
        viewColor.setBackgroundColor(subColor);


        mTitleEditText = findViewById(R.id.edit_timetable_name);
        mDayEditText = findViewById(R.id.edit_timetable_day);
        mRoomEditText = findViewById(R.id.room_timetable);
        mStartTime = findViewById(R.id.timetable_start_time);
        mEndTime = findViewById(R.id.timetable_end_time);

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
                        .hour(12).minute(0)
                        .set24HourView(false)
                        .show(TimeTableEditor.this, START_TIME_DIALOG);
            }
        });

        mEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleTimeDialog.build()
                        .neut()
                        .hour(12).minute(0)
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
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getApplicationContext().getPackageName());

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

                SimpleDateFormat formatter = new SimpleDateFormat("h:mm a", java.util.Locale.getDefault());

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

                SimpleDateFormat formatter = new SimpleDateFormat("h:mm a", java.util.Locale.getDefault());

                mEndTime.setText(formatter.format(calender.getTime()));
                return true;
            }
        }
        if (dialogTag.equals(DATE_DIALOG))
        {
            if (which == BUTTON_POSITIVE)
            {
                Date date = new Date(extras.getLong(SimpleDateDialog.DATE));

                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", java.util.Locale.getDefault());
                mDayEditText.setText(dateFormat.format(date));
                return true;
            }
        }
        return false;
    }
}
