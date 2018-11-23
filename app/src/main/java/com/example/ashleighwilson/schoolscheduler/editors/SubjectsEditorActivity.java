package com.example.ashleighwilson.schoolscheduler.editors;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.adapter.RecyclerSubAdapter;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.views.SimpleColorDialog;
import com.example.ashleighwilson.schoolscheduler.views.SimpleTimeDialog;
import com.example.ashleighwilson.schoolscheduler.models.SubjectsModel;

import java.util.ArrayList;


public class SubjectsEditorActivity extends AppCompatActivity implements
        SimpleTimeDialog.OnDialogResultListener
{
    private static final String TAG = SubjectsEditorActivity.class.getSimpleName();

    private EditText mTitleEditText;
    private EditText mTeacherEditText;
    private EditText mRoomEditText;
    private TextView viewColor;
    TextView mStartTime;
    TextView mEndTime;
    private static final String EXTRA_ID = "id";
    final static private String COLOR_DIALOG = "colorDialog";
    private static final String START_TIME_DIALOG = "SimpleTimeStartDialog";
    private static final String END_TIME_DIALOG = "SimpleTimeEndDialog";
    static private int subColor;
    DbHelper dbHelper;
    private SubjectsModel itemModel;
    String editTitle = "";
    public ArrayList<SubjectsModel> model;

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
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_subjects);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        dbHelper = DbHelper.getInstance();
        //model = dbHelper.getAllSubjects();

        Button colorSelector = findViewById(R.id.sub_create_button);
        viewColor = findViewById(R.id.sub_view_color);


        subColor = getMatColor("200");
        viewColor.setBackgroundColor(subColor);


        mTitleEditText = findViewById(R.id.edit_subject);
        mTeacherEditText = findViewById(R.id.edit_subject_teacher);
        mRoomEditText = findViewById(R.id.subject_room);
        mStartTime = findViewById(R.id.sub_start_time);
        mEndTime = findViewById(R.id.sub_end_time);

        mTitleEditText.setOnTouchListener(mTouchListener);
        mTeacherEditText.setOnTouchListener(mTouchListener);
        mRoomEditText.setOnTouchListener(mTouchListener);


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
                Log.i(TAG, "color: " + itemModel.getmColor());
                mStartTime.setText(itemModel.getmStartTime());
                mEndTime.setText(itemModel.getmEndTime());
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
        return false;
    }

    private void saveSubject()
    {
        SubjectsModel model = new SubjectsModel();
        String titleString = mTitleEditText.getText().toString().trim();
        String teacherString = mTeacherEditText.getText().toString().trim();
        String roomString = mRoomEditText.getText().toString().trim();
        String startString = mStartTime.getText().toString().trim();
        String endString = mEndTime.getText().toString().trim();

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

        dbHelper.addClass(model);
        dbHelper.addToSpinner(titleString);
        if (dbHelper == null)
        {
            Toast.makeText(this, "Error with saving Subject", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Subject saved", Toast.LENGTH_SHORT).show();
        }
        int resultCode = 1;
        Intent resultIntent = new Intent();
        resultIntent.putExtra(RecyclerSubAdapter.EXTRA_ID, model);
        Log.i(TAG, "result intent color: " + subColor);
        setResult(resultCode, resultIntent);

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
                finish();
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
}
