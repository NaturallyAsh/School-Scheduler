package com.example.ashleighwilson.schoolscheduler.editors;

import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.dialog.SimpleColorDialog;
import com.example.ashleighwilson.schoolscheduler.dialog.SimpleDateDialog;
import com.example.ashleighwilson.schoolscheduler.dialog.SimpleTimeDialog;
import com.example.ashleighwilson.schoolscheduler.models.AgendaModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AgendaEditor extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        SimpleDateDialog.OnDialogResultListener
{
    private static final String TAG = AgendaEditor.class.getSimpleName();

    private EditText mAssignmentTitle;
    private TextView mDueDate, viewColor;
    private String label;
    private Spinner mClassName;
    private DbHelper dbHelper;
    private Date date;
    private static final String COLOR_DIALOG = "color dialog";
    private static final String DATE_DIALOG = "date dialog";
    static private int agendaColor;

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

        dbHelper = new DbHelper(getApplicationContext());

        mAssignmentTitle = findViewById(R.id.assignment_title_edit_text);
        mClassName = findViewById(R.id.agenda_subject_spinner);
        mDueDate = findViewById(R.id.agenda_due_date_text);
        viewColor = findViewById(R.id.agenda_view_color);

        mAssignmentTitle.setOnTouchListener(mTouchListener);

        loadSpinnerData();

        Button colorSelector = findViewById(R.id.agenda_create_color);
        agendaColor = getMatColor("200");
        viewColor.setBackgroundColor(agendaColor);

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
        List<String> labels = dbHelper.getAllLabels();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
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
                Calendar c = Calendar.getInstance();
                //c.set(Calendar.YEAR, START_YEAR);
                //c.set(Calendar.YEAR, END_YEAR);
                //c.set(Calendar.MONTH, START_MONTH);
                //c.set(Calendar.MONTH, END_MONTH);
                //c.set(Calendar.DAY_OF_MONTH, START_DAY);
                //c.set(Calendar.DAY_OF_MONTH, END_DAY);
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
        AgendaModel model = new AgendaModel();
        String titleString = mAssignmentTitle.getText().toString().trim();

        model.setClassName(label);
        model.setAgendaTitle(titleString);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", java.util.Locale.getDefault());
        Calendar time = Calendar.getInstance();
        time.setTime(date);
        model.setDueDate(time);
        model.setmColor(agendaColor);

        dbHelper.addAgenda(model);
        Log.i(TAG, "spinner label: " + label);

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
                finish();
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
