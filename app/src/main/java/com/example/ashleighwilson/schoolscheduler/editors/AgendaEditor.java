package com.example.ashleighwilson.schoolscheduler.editors;

import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.EventRecurrence;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.adapter.AgendaAdapter;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.data.NotificationController;
import com.example.ashleighwilson.schoolscheduler.utils.DateHelper;
import com.example.ashleighwilson.schoolscheduler.views.RecurrenceDialog;
import com.example.ashleighwilson.schoolscheduler.views.SimpleColorDialog;
import com.example.ashleighwilson.schoolscheduler.views.SimpleDateDialog;
import com.example.ashleighwilson.schoolscheduler.models.AgendaModel;
import com.example.ashleighwilson.schoolscheduler.views.SimpleTimeDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import eltos.simpledialogfragment.list.SimpleListDialog;

public class AgendaEditor extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        SimpleDateDialog.OnDialogResultListener, CompoundButton.OnCheckedChangeListener
{
    private static final String TAG = AgendaEditor.class.getSimpleName();

    private EditText mAssignmentTitle;
    private TextView mDueDate, viewColor, mNotificationTv, mTimeToNotify, mDayToNotify, mAddReminder,
        mRepeat;
    private Switch mNotification;
    private String label;
    private Spinner mClassName;
    private DbHelper dbHelper;
    private Date date;
    private static final String COLOR_DIALOG = "color dialog";
    private static final String DUE_DATE_DIALOG = "due date dialog";
    private static final String DAY_TO_NOTIFY_DIALOG = "day to notify dialog";
    private static final String ARG_ITEM = "agenda_arg";
    private static final String TIME_DIALOG = "time_dialog";
    private static final String CHOICE_DIALOG = "choice_dialog";
    private static final String WEEK_CHOICE_DIALOG = "week_choice_dialog";
    static private int agendaColor;
    NotificationController controller;
    private AgendaAdapter agendaAdapter;
    private AgendaModel model;
    private AgendaModel itemModel;
    private ArrayAdapter<String> dataAdapter;
    private List<String> labels;
    private String titleString, dueDate;
    private SelectedDate mSelectedDate;
    private int mHour, mMinute, mRepeatInt, dayWeekInt;
    private long NOTIFY_DATE;
    private String mRecurrenceOption, mRecurrenceRule;
    private Calendar calendar;
    private LinearLayout moreLayout, repeatContainer;
    private ArrayList<String> repeatLabels, weekLabels;


    private boolean mSubjectHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mSubjectHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_agenda);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = DbHelper.getInstance();
        controller = new NotificationController(this);
        moreLayout = findViewById(R.id.more_layout);

        mAssignmentTitle = findViewById(R.id.assignment_title_edit_text);
        mClassName = findViewById(R.id.agenda_subject_spinner);
        mDueDate = findViewById(R.id.agenda_due_date_text);
        viewColor = findViewById(R.id.agenda_view_color);
        mNotification = findViewById(R.id.notification_switch);
        mTimeToNotify = findViewById(R.id.assign_time_to_notify);
        mDayToNotify = findViewById(R.id.assign_day_to_notify);
        mAddReminder = findViewById(R.id.assign_add_reminder);
        mRepeat = findViewById(R.id.assign_repeat);
        repeatContainer = findViewById(R.id.assign_repeat_container);

        mAssignmentTitle.setOnTouchListener(mTouchListener);
        mClassName.setOnItemSelectedListener(this);
        mNotification.setOnCheckedChangeListener(this);

        loadSpinnerData();

        Button colorSelector = findViewById(R.id.agenda_create_color);
        agendaColor = getMatColor("200");
        viewColor.setBackgroundColor(agendaColor);

        itemModel = getIntent().getParcelableExtra(AgendaAdapter.AGENDA_ARG);
        if (itemModel != null) {
            mAssignmentTitle.setText(itemModel.getAgendaTitle());
            mDueDate.setText(itemModel.getDueDate());
            viewColor.setBackgroundColor(itemModel.getmColor());
            mClassName.setSelection(labels.indexOf(itemModel.getClassName()));
        }

        colorSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleColorDialog.build()
                        .title("Pick a subject color")
                        .colorPreset(Color.WHITE)
                        .allowCustom(false)
                        .show(AgendaEditor.this, COLOR_DIALOG);
            }
        });

        mDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateDialog.build()
                        //.firstDayOfWeek(Calendar.MONDAY)
                        .show(AgendaEditor.this, DUE_DATE_DIALOG);
            }
        });

        mTimeToNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleTimeDialog.build()
                        .hour(Calendar.HOUR_OF_DAY).minute(Calendar.MINUTE)
                        .set24HourView(false)
                        .show(AgendaEditor.this, TIME_DIALOG);
            }
        });

        mDayToNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateDialog.build()
                        .show(AgendaEditor.this, DAY_TO_NOTIFY_DIALOG);
            }
        });

        repeatContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleListDialog.build()
                        .title("Repeat Options")
                        .choiceMode(SimpleListDialog.SINGLE_CHOICE_DIRECT)
                        .items(getApplicationContext(), R.array.repeat_days)
                        .show(AgendaEditor.this, CHOICE_DIALOG);
            }
        });

    }

    private void loadSpinnerData()
    {
        labels = dbHelper.getAllLabels();

        dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, labels);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mClassName.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        label = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        AgendaModel model = new AgendaModel();

        if(isChecked)
        {
            mNotification.setChecked(true);
            //model.setmNotification(mNotification.isChecked());
            moreLayout.setVisibility(View.VISIBLE);
        } else {
            mNotification.setChecked(false);
            moreLayout.setVisibility(View.INVISIBLE);
        }
    }

    private int getMatColor(String typeColor) {
        int returnColor = Color.BLACK;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getPackageName());

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
            agendaColor = extras.getInt(SimpleColorDialog.COLOR);
            viewColor.setBackgroundColor(agendaColor);
            return true;
        }
        if (dialogTag.equals(DUE_DATE_DIALOG))
        {
            if (which == BUTTON_POSITIVE)
            {
                date = new Date(extras.getLong(SimpleDateDialog.DATE));

                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", java.util.Locale.getDefault());
                mDueDate.setText(dateFormat.format(date));
                return true;
            }
        }
        if (dialogTag.equals(DAY_TO_NOTIFY_DIALOG))
        {
            if (which == BUTTON_POSITIVE)
            {
                Date date = new Date(extras.getLong(SimpleDateDialog.DATE));

                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", java.util.Locale.getDefault());

                NOTIFY_DATE = extras.getLong(SimpleDateDialog.DATE);
                mDayToNotify.setText(dateFormat.format(date));
                return true;
            }
        }
        if (dialogTag.equals(TIME_DIALOG)) {
            if (which == BUTTON_POSITIVE) {
                Calendar calender = Calendar.getInstance();
                calender.set(Calendar.HOUR_OF_DAY, extras.getInt(SimpleTimeDialog.HOUR));
                calender.set(Calendar.MINUTE, extras.getInt(SimpleTimeDialog.MINUTE));

                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", java.util.Locale.getDefault());

                mHour = extras.getInt(SimpleTimeDialog.HOUR);
                mMinute = extras.getInt(SimpleTimeDialog.MINUTE);

                mTimeToNotify.setText(formatter.format(calender.getTime()));
                return true;
            }
        }
        if (dialogTag.equals(CHOICE_DIALOG)) {
            if (which == BUTTON_POSITIVE) {
                repeatLabels = extras.getStringArrayList(SimpleListDialog.SELECTED_LABELS);

                String labelString = "";
                if (repeatLabels != null) {
                    labelString = TextUtils.join("\t", repeatLabels);
                }
                getRepeatIntDays(labelString);
            }
        }
        if (dialogTag.equals(WEEK_CHOICE_DIALOG)) {
            if (which == BUTTON_POSITIVE) {
                weekLabels = extras.getStringArrayList(SimpleListDialog.SELECTED_LABELS);

                String weekLabelString = "";
                if (weekLabels != null) {
                    weekLabelString = TextUtils.join("\t", weekLabels);
                }
                getDayWeekInt(weekLabelString);
            }
        }
        return false;
    }

    private int getRepeatIntDays(String repeatLabel) {
        switch (repeatLabel) {
            case "Never":
                mRepeatInt = 0;
                mRepeat.setText(repeatLabel);
                break;
            case "Daily":
                mRepeatInt = 1;
                mRepeat.setText(repeatLabel);
                break;
            case "Weekly":
                mRepeatInt = 2;
                mRepeat.setText(repeatLabel);
                break;
            case "Monthly":
                mRepeatInt = 3;
                mRepeat.setText(repeatLabel);
                break;
            case "Yearly":
                mRepeatInt = 4;
                mRepeat.setText(repeatLabel);
                break;
            case "Custom days":
                mRepeatInt = 5;
                //mRepeat.setText(repeatLabel);
                showChoice(null);
                break;
            default:
                throw new RuntimeException("error with repeat: " + repeatLabel);
        }
        return 0;
    }

    private int getDayWeekInt(String dayLabel) {
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
                throw new RuntimeException("error with week: " + dayLabel);
        }
        return 0;
    }

    public void showChoice(View view) {
        SimpleListDialog.build()
                .title("Select a day")
                .choiceMode(SimpleListDialog.SINGLE_CHOICE_DIRECT)
                .items(getApplicationContext(), R.array.day_of_week)
                .show(this, WEEK_CHOICE_DIALOG);
    }

    private void onSave()
    {
        model = new AgendaModel();
        titleString = mAssignmentTitle.getText().toString().trim();
        dueDate = mDueDate.getText().toString().trim();

        Calendar timeNote = Calendar.getInstance();
        timeNote.set(Calendar.HOUR, mHour);
        timeNote.set(Calendar.MINUTE, mMinute);

        if (titleString.matches("") || dueDate.matches("")) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            //return;
        } else {
            if (itemModel != null) {
                model.setmId(itemModel.getmId());
            }
            model.setClassName(label);
            model.setAgendaTitle(titleString);
            model.setDueDate(dueDate);
            if (itemModel != null) {
                model.setmColor(itemModel.getmColor());
            } else {
                model.setmColor(agendaColor);
            }
            model.setTimeToNotify(timeNote.getTimeInMillis());
            model.setmDayToNotify(NOTIFY_DATE);
            model.setmAddReminder(0);
            model.setmRepeatType(mRepeatInt);
            /*if (mNotification.isChecked()) {
                model.setmNotification(mNotification.isChecked());

                NotificationController.notificationTest3(this, model);
                Log.i(TAG, "check notification: " + controller.checkNotification(this));
            }*/

            dbHelper.addAgenda(model);
            finish();
        }

        Toast.makeText(this, "Agenda saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_agenda_editor, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_save:
                onSave();
                //finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mSubjectHasChanged)
                {
                    NavUtils.navigateUpFromSameTask(AgendaEditor.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(AgendaEditor.this);
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
}
