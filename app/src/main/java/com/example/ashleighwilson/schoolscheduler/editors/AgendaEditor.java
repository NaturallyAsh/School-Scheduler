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
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AgendaEditor extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        SimpleDateDialog.OnDialogResultListener, CompoundButton.OnCheckedChangeListener
{
    private static final String TAG = AgendaEditor.class.getSimpleName();

    private EditText mAssignmentTitle;
    private TextView mDueDate, viewColor, mNotificationTv;
    private Switch mNotification;
    private String label;
    private Spinner mClassName;
    private DbHelper dbHelper;
    private Date date;
    private static final String COLOR_DIALOG = "color dialog";
    private static final String DATE_DIALOG = "date dialog";
    private static final String ARG_ITEM = "agenda_arg";
    static private int agendaColor;
    NotificationController controller;
    private AgendaAdapter agendaAdapter;
    private AgendaModel model;
    private AgendaModel itemModel;
    private ArrayAdapter<String> dataAdapter;
    private List<String> labels;
    private String titleString, dueDate;
    private SelectedDate mSelectedDate;
    private int mHour, mMinute, mReminderYear, mReminderMonth, mReminderDay;
    private String mRecurrenceOption, mRecurrenceRule;
    private Calendar calendar;

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
            mReminderYear = mSelectedDate.getFirstDate().get(Calendar.YEAR);
            mReminderMonth = mSelectedDate.getFirstDate().get(Calendar.MONTH);
            mReminderDay = mSelectedDate.getFirstDate().get(Calendar.DAY_OF_MONTH);

            mHour = hourOfDay;
            mMinute = minute;
            mRecurrenceOption = recurrenceOption != null ? recurrenceOption.name() : "n/a";
            mRecurrenceRule = recurrenceRule != null ? recurrenceRule : "n/a";


            calendar = Calendar.getInstance();
            calendar.set(mReminderYear, mReminderMonth, mReminderDay, mHour, mMinute, 0);

            mNotificationTv.setText(DateHelper.dateFormatter(calendar.getTimeInMillis()));

            //model.setTimeToNotify(calendar);
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

        mAssignmentTitle = findViewById(R.id.assignment_title_edit_text);
        mClassName = findViewById(R.id.agenda_subject_spinner);
        mDueDate = findViewById(R.id.agenda_due_date_text);
        viewColor = findViewById(R.id.agenda_view_color);
        mNotification = findViewById(R.id.notification_switch);
        mNotificationTv = findViewById(R.id.notification_tv);

        mAssignmentTitle.setOnTouchListener(mTouchListener);
        mClassName.setOnItemSelectedListener(this);
        mNotification.setOnCheckedChangeListener(this);

        loadSpinnerData();

        Button colorSelector = findViewById(R.id.agenda_create_color);
        agendaColor = getMatColor("200");
        viewColor.setBackgroundColor(agendaColor);

        itemModel = getIntent().getParcelableExtra(AgendaAdapter.AGENDA_ARG);
        //Bundle intent = getIntent().getExtras();
        if (itemModel != null) {
            /*String title = intent.getString(AgendaAdapter.ARG_TITLE);
            String name = intent.getString(AgendaAdapter.ARG_CLASSNAME);
            String due = intent.getString(AgendaAdapter.ARG_DATE);
            int color = intent.getInt(AgendaAdapter.ARG_COLOR);*/

            mAssignmentTitle.setText(itemModel.getAgendaTitle());
            mDueDate.setText(itemModel.getDueDate());
            viewColor.setBackgroundColor(itemModel.getmColor());
            mClassName.setSelection(labels.indexOf(itemModel.getClassName()));
            mNotification.setChecked(itemModel.ismNotification());
            Log.i(TAG, "item notify: " + itemModel.ismNotification());
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
                        .show(AgendaEditor.this, DATE_DIALOG);
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
        }
        RecurrenceDialog recurrenceDialog = new RecurrenceDialog();
        recurrenceDialog.setCallback(mCallback);

        Pair<Boolean, SublimeOptions> optionsPair = getOptions();

        Bundle bundle = new Bundle();
        bundle.putParcelable("SUBLIME_OPTIONS", optionsPair.second);
        recurrenceDialog.setArguments(bundle);

        recurrenceDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        recurrenceDialog.show(getSupportFragmentManager(), "sublime");

    }

    public Pair<Boolean, SublimeOptions> getOptions()
    {
        SublimeOptions options = new SublimeOptions();
        int displayOptions = 0;

        displayOptions = SublimeOptions.ACTIVATE_DATE_PICKER + SublimeOptions.ACTIVATE_TIME_PICKER +
                SublimeOptions.ACTIVATE_RECURRENCE_PICKER;

        options.setDisplayOptions(displayOptions);

        return new Pair<>(displayOptions != 0 ? Boolean.TRUE : Boolean.FALSE, options);

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
        if (dialogTag.equals(DATE_DIALOG))
        {
            if (which == BUTTON_POSITIVE)
            {
                date = new Date(extras.getLong(SimpleDateDialog.DATE));

                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", java.util.Locale.getDefault());
                mDueDate.setText(dateFormat.format(date));
                return true;
            }
        }
        return false;
    }

    private void onSave()
    {
        model = new AgendaModel();
        titleString = mAssignmentTitle.getText().toString().trim();
        dueDate = mDueDate.getText().toString().trim();

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
            model.setmRecurrenceOption(mRecurrenceOption);
            model.setmRecurrenceRule(mRecurrenceRule);
            Log.i(TAG, "recurrence rule:" + mRecurrenceRule);
            model.setTimeToNotify(calendar);
            if (mNotification.isChecked()) {
                model.setmNotification(mNotification.isChecked());

                controller.notificationTest3(model);
                Log.i(TAG, "check notification: " + controller.checkNotification(this));
            }

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
