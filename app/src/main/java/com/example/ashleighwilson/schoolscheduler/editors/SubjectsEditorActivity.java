package com.example.ashleighwilson.schoolscheduler.editors;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.adapter.RecyclerSubAdapter;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.timetable.MonthLoader;
import com.example.ashleighwilson.schoolscheduler.models.WeekViewEvent;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekViewLoader;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekViewUtil;
import com.example.ashleighwilson.schoolscheduler.utils.DateHelper;
import com.example.ashleighwilson.schoolscheduler.views.RecurrenceDialog;
import com.example.ashleighwilson.schoolscheduler.views.SimpleColorDialog;
import com.example.ashleighwilson.schoolscheduler.views.SimpleTimeDialog;
import com.example.ashleighwilson.schoolscheduler.models.SubjectsModel;

import java.util.ArrayList;
import java.util.List;

import eltos.simpledialogfragment.list.SimpleListDialog;


public class SubjectsEditorActivity extends AppCompatActivity implements
        SimpleTimeDialog.OnDialogResultListener, CompoundButton.OnCheckedChangeListener, MonthLoader.MonthLoaderListener
{
    private static final String TAG = SubjectsEditorActivity.class.getSimpleName();

    private EditText mTitleEditText;
    private EditText mTeacherEditText;
    private EditText mRoomEditText;
    private TextView viewColor;
    private TextView daysOfWeek;
    private Switch mSwitch;
    TextView mStartTime;
    TextView mEndTime;
    private SelectedDate mSelectedDate;
    private int mHour, mMinute, mReminderYear, mReminderMonth, mReminderDay;
    private String mRecurrenceOption, mRecurrenceRule;
    private Calendar calendar;
    private static final String EXTRA_ID = "id";
    final static private String COLOR_DIALOG = "colorDialog";
    private static final String START_TIME_DIALOG = "SimpleTimeStartDialog";
    private static final String END_TIME_DIALOG = "SimpleTimeEndDialog";
    private static final String CHOICE_DIALOG = "dialogTagChoice";
    public static int START_HOUR, START_MINUTE = 0;
    public static int END_HOUR, END_MINUTE = 0;
    static private int subColor;
    DbHelper dbHelper;
    private ArrayList<String> labels;
    private int dayWeekInt;
    private SubjectsModel itemModel;
    private SubjectsModel model;
    private WeekViewLoader mWeekViewLoader;
    String editTitle = "";
    //public ArrayList<SubjectsModel> model;
    /*public int SU = 1;
    public int MO = 2;
    public int TU = 3;
    public int WE = 4;
    public int TH = 5;
    public int FR = 6;
    public int SA = 7;*/

    private boolean mSubjectHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mSubjectHasChanged = true;
            return false;
        }
    };

    private RecurrenceDialog.Callback mCallback = new RecurrenceDialog.Callback() {
        @Override
        public void onCancelled() {
            if (mCallback != null) {
                mCallback.onCancelled();
            }
        }

        @Override
        public void onDateTimeRecurrenceSet(SelectedDate selectedDate, int hourOfDay, int minute, SublimeRecurrencePicker.RecurrenceOption recurrenceOption, String recurrenceRule) {

            mSelectedDate = selectedDate;
            /*mReminderYear = mSelectedDate.getFirstDate().get(Calendar.YEAR);
            mReminderMonth = mSelectedDate.getFirstDate().get(Calendar.MONTH);
            mReminderDay = mSelectedDate.getFirstDate().get(Calendar.DAY_OF_MONTH);

            mHour = hourOfDay;
            mMinute = minute; */
            mRecurrenceOption = recurrenceOption != null ? recurrenceOption.name() : "n/a";
            mRecurrenceRule = recurrenceRule != null ? recurrenceRule : "n/a";


            //calendar = Calendar.getInstance();
            //calendar.set(mReminderYear, mReminderMonth, mReminderDay, mHour, mMinute, 0);

            //daysOfWeek.setText(applyBoldStyle("Days: ").append(mRecurrenceRule));
            if (mRecurrenceOption.equals("CUSTOM")) {
                daysOfWeek.setText(DateHelper.formatRecurrence(getApplicationContext(), mRecurrenceRule));
            }
            else {
                //daysOfWeek.setText("N/A");
                daysOfWeek.setText(mRecurrenceOption);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_subjects);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        dbHelper = DbHelper.getInstance();
        mWeekViewLoader = new MonthLoader(this);


        Button colorSelector = findViewById(R.id.sub_create_button);
        viewColor = findViewById(R.id.sub_view_color);


        subColor = getMatColor("200");
        viewColor.setBackgroundColor(subColor);


        mTitleEditText = findViewById(R.id.edit_subject);
        mTeacherEditText = findViewById(R.id.edit_subject_teacher);
        mRoomEditText = findViewById(R.id.subject_room);
        mStartTime = findViewById(R.id.sub_start_time);
        mEndTime = findViewById(R.id.sub_end_time);
        daysOfWeek = findViewById(R.id.recurrence_editor_tv);
        mSwitch = findViewById(R.id.add_sub_to_calendar);


        mTitleEditText.setOnTouchListener(mTouchListener);
        mTeacherEditText.setOnTouchListener(mTouchListener);
        mRoomEditText.setOnTouchListener(mTouchListener);
        mSwitch.setOnCheckedChangeListener(this);


        colorSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleColorDialog.build()
                        .title("Pick a subject color")
                        .colorPreset(Color.WHITE)
                        .allowCustom(false)
                        .show(SubjectsEditorActivity.this, COLOR_DIALOG);
            }
        });

        mStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleTimeDialog.build()
                        .neut()
                        .hour(12).minute(0)
                        .set24HourView(false)
                        .show(SubjectsEditorActivity.this, START_TIME_DIALOG);
            }
        });

        mEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleTimeDialog.build()
                        .neut()
                        .hour(12).minute(0)
                        .set24HourView(false)
                        .show(SubjectsEditorActivity.this, END_TIME_DIALOG);
            }
        });

        daysOfWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecurrenceDialog recurrenceDialog = new RecurrenceDialog();
                recurrenceDialog.setCallback(mCallback);

                Pair<Boolean, SublimeOptions> optionsPair = getOptions();

                Bundle bundle = new Bundle();
                bundle.putParcelable("SUBLIME_OPTIONS", optionsPair.second);
                recurrenceDialog.setArguments(bundle);

                recurrenceDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                recurrenceDialog.show(getSupportFragmentManager(), "sublime");
            }
        });

        itemModel = getIntent().getParcelableExtra(RecyclerSubAdapter.EXTRA_ID);
        if (itemModel != null)
        {
            if (itemModel.getmTitle().equals(editTitle))
            {
                setTitle("Add Subject");
                invalidateOptionsMenu();
            }
            else
            {
                setTitle("Edit Subject");
                mTitleEditText.setText(itemModel.getmTitle());
                mTeacherEditText.setText(itemModel.getmTeacher());
                mRoomEditText.setText(itemModel.getmRoom());
                viewColor.setBackgroundColor(itemModel.getmColor());
                //Log.i(TAG, "color: " + itemModel.getmColor());
                mStartTime.setText(itemModel.getmStartTime());
                mEndTime.setText(itemModel.getmEndTime());
                if (itemModel.getmRecurrence_rule() != null) {
                    mRecurrenceRule = itemModel.getmRecurrence_rule();
                    daysOfWeek.setText(DateHelper.formatRecurrence(getApplicationContext(), mRecurrenceRule));
                } else {
                    mRecurrenceOption = itemModel.getmRecurrence_option();
                    daysOfWeek.setText(mRecurrenceOption);
                }
            }
        }
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

                START_HOUR = extras.getInt(SimpleTimeDialog.HOUR);
                START_MINUTE = extras.getInt(SimpleTimeDialog.MINUTE);

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

                END_HOUR = extras.getInt(SimpleTimeDialog.HOUR);
                END_MINUTE = extras.getInt(SimpleTimeDialog.MINUTE);

                mEndTime.setText(formatter.format(calender.getTime()));
                return true;
            }
        }
        if (which == BUTTON_POSITIVE) {
            switch (dialogTag) {
                case CHOICE_DIALOG:
                    labels = extras.getStringArrayList(SimpleListDialog.SELECTED_LABELS);
                    String labelString = "";
                    if (labels != null) {
                        labelString = TextUtils.join("\t", labels);
                    }
                    //Log.i(TAG, "label: " + labelString);
                    getIntDays(labelString);
            }
        }
        return false;
    }

    private int getIntDays(String dayLabel) {
        switch (dayLabel) {
            case "SUNDAY":
                dayWeekInt = Calendar.SUNDAY;
                Log.i(TAG, "day int: " + dayWeekInt);
                break;
            case "MONDAY":
                dayWeekInt = Calendar.MONDAY;
                break;
            case "TUESDAY":
                dayWeekInt = Calendar.TUESDAY;
                break;
            case "WEDNESDAY":
                dayWeekInt = Calendar.WEDNESDAY;
                break;
            case "THURSDAY":
                dayWeekInt = Calendar.THURSDAY;
                break;
            case "FRIDAY":
                dayWeekInt = Calendar.FRIDAY;
                break;
            case "SATURDAY":
                dayWeekInt = Calendar.SATURDAY;
                break;
            default:
                throw new RuntimeException("error with day: " + dayLabel);
        }
        return 0;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mSwitch.setChecked(true);
            showChoice(mSwitch);

        }
    }

    public void showChoice(View view) {
        SimpleListDialog.build()
                .title("Select a day")
                .choiceMode(SimpleListDialog.SINGLE_CHOICE_DIRECT)
                .items(getApplicationContext(), R.array.day_of_week)
                .show(this, CHOICE_DIALOG);
    }

    private void saveSubject()
    {
        model = new SubjectsModel();
        String titleString = mTitleEditText.getText().toString().trim();
        String teacherString = mTeacherEditText.getText().toString().trim();
        String roomString = mRoomEditText.getText().toString().trim();
        String startString = mStartTime.getText().toString().trim();
        String endString = mEndTime.getText().toString().trim();

        if (dbHelper.hasLabel(titleString)) {
            Log.i(TAG, "db already has label: " + titleString);
        } else {
            dbHelper.addToSpinner(titleString);
            Log.i(TAG, "label added: " + titleString);
        }

        if (startString.matches("") || endString.matches("")) {
            Toast.makeText(getApplicationContext(), "Please enter a time", Toast.LENGTH_SHORT).show();
        } else {
            if (itemModel != null) {
                model.setId(itemModel.getId());
            }
            model.setmTitle(titleString);
            model.setmTeacher(teacherString);
            model.setmRoom(roomString);
            if (itemModel != null) {
                model.setmColor(itemModel.getmColor());
            } else {
                model.setmColor(subColor);
            }
            model.setmStartTime(startString);
            model.setmEndTime(endString);
            model.setmRecurrence_rule(mRecurrenceRule);
            model.setmRecurrence_option(mRecurrenceOption);

            if (mSwitch.isChecked()) {
                addToTimetable(model);
            }

            int resultCode = 1;
            Intent resultIntent = new Intent();
            resultIntent.putExtra(RecyclerSubAdapter.EXTRA_ID, model);
            //Log.i(TAG, "result intent color: " + subColor);
            setResult(resultCode, resultIntent);

            dbHelper.addClass(model);
            Toast.makeText(this, "Subject saved", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void addToTimetable(SubjectsModel subjectsModel) {
        String titleString = subjectsModel.getmTitle();
        String roomString = subjectsModel.getmRoom();

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, START_HOUR);
        startTime.set(Calendar.MINUTE, START_MINUTE);
        startTime.set(Calendar.DAY_OF_WEEK, dayWeekInt);
        Log.i(TAG, "day of week: " + startTime.get(Calendar.DAY_OF_WEEK));


        Calendar endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.HOUR_OF_DAY, END_HOUR);
        endTime.set(Calendar.MINUTE, END_MINUTE);

        WeekViewEvent createdEvent;
        createdEvent = new WeekViewEvent(WeekViewUtil.eventId++, titleString, startTime, endTime);
        createdEvent.setColor(subjectsModel.getmColor());
        createdEvent.setLocation(roomString);
        createdEvent.setmRecurrenceRule(subjectsModel.getmRecurrence_rule());
        //Log.i(TAG, "convert start time: " + subjectsModel.getConvertStartTime());

        if (subjectsModel != null && !subjectsModel.getConvertStartTime().equals(startTime)) {
            int periodToFetch = (int) mWeekViewLoader.toWeekViewPeriodIndex(subjectsModel.getConvertStartTime());
            int year = periodToFetch / 12;
            int month = periodToFetch % 12 + 1;
            String monthKey = "" + (month - 1) + "-" + year;
            Log.i(TAG, "called");

            List<WeekViewEvent> eventListByMonth = WeekViewUtil.monthMasterEvents.get(monthKey);

            if (eventListByMonth != null)
            {
                eventListByMonth.remove(subjectsModel);
            }
            WeekViewUtil.monthMasterEvents.put(monthKey, eventListByMonth);
        }
        WeekViewUtil.masterEvents.put("" + createdEvent.getId(), createdEvent);

        int periodToFetch = (int) mWeekViewLoader.toWeekViewPeriodIndex(startTime);
        int year = periodToFetch / 12;
        int month = periodToFetch % 12 + 1;
        String monthKey = "" + (month - 1) + "-" + year;

        List<WeekViewEvent> eventListByMonth = WeekViewUtil.monthMasterEvents.get(monthKey);

        if (eventListByMonth == null)
        {
            eventListByMonth = new ArrayList<>();
        }

        WeekViewEvent model = new WeekViewEvent();
        model.setName(titleString);
        model.setLocation(roomString);
        model.setStartTime(startTime);
        model.setEndTime(endTime);
        model.setColor(subjectsModel.getmColor());
        model.setmRecurrenceRule(subjectsModel.getmRecurrence_rule());

        dbHelper.addTimetable(model);

        eventListByMonth.add(createdEvent);
        WeekViewUtil.monthMasterEvents.put(monthKey, eventListByMonth);


    }

    public String getEventName(String name, Calendar startTime, Calendar endTime) {

        String start = timeFormatter(startTime);
        String end = timeFormatter(endTime);

        String eventMsg = name + "\n" + start + " - " + end;

        return eventMsg;
    }

    private String timeFormatter(Calendar time)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String msg = sdf.format(time.getTime());

        return msg;
    }

    private SpannableStringBuilder applyBoldStyle(String text) {
        SpannableStringBuilder ss = new SpannableStringBuilder(text);
        ss.setSpan(new StyleSpan(Typeface.BOLD), 0, text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    private Pair<Boolean, SublimeOptions> getOptions()
    {
        SublimeOptions options = new SublimeOptions();
        int displayOptions = 0;

        displayOptions = SublimeOptions.ACTIVATE_RECURRENCE_PICKER;

        options.setPickerToShow(SublimeOptions.Picker.REPEAT_OPTION_PICKER);
        options.setDisplayOptions(displayOptions);

        return new Pair<>(displayOptions != 0 ? Boolean.TRUE : Boolean.FALSE, options);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_subject_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_save:
                saveSubject();
                //finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mSubjectHasChanged)
                {
                    //NavUtils.getParentActivityIntent(SubjectsEditorActivity.this);
                    onBackPressed();
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //NavUtils.getParentActivityIntent(SubjectsEditorActivity.this);
                        onBackPressed();
                    }
                };

            showUnsavedChangesDialog(discardButtonClickListener);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (dialog != null)
                    dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this subject?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (dialog != null)
                    dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public List<WeekViewEvent> onMonthLoad(int newYear, int newMonth) {
        return null;
    }
}
