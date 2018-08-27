package com.example.ashleighwilson.schoolscheduler.editors;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.adapter.RecyclerSubAdapter;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.models.SubjectsModel;

import java.util.ArrayList;

public class SubjectsEditorActivity extends AppCompatActivity
{
    private static final String TAG = SubjectsEditorActivity.class.getSimpleName();

    private EditText mTitleEditText;
    private EditText mTeacherEditText;
    private EditText mRoomEditText;
    DbHelper dbHelper;
    SubjectsModel subjectsModel;
    private static final int NO_ID = -99;
    private static final String NO_TITLE = "";
    private static final String NO_TEACHER = "";
    private static final String NO_ROOM = "";
    int ID;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_subjects);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DbHelper(getApplicationContext());
        model = dbHelper.getAllSubjects();

        mTitleEditText = findViewById(R.id.edit_subject);
        mTeacherEditText = findViewById(R.id.edit_subject_teacher);
        mRoomEditText = findViewById(R.id.subject_room);

        mTitleEditText.setOnTouchListener(mTouchListener);
        mTeacherEditText.setOnTouchListener(mTouchListener);
        mRoomEditText.setOnTouchListener(mTouchListener);

        Intent intent = getIntent();
        //Bundle extras = getIntent().getExtras();
        ID = intent.getIntExtra(RecyclerSubAdapter.EXTRA_ID, NO_ID);
        /*String bTitle = extras.getString(RecyclerSubAdapter.EXTRA_TITLE, NO_TITLE);
        String bTeacher = extras.getString(RecyclerSubAdapter.EXTRA_TEACHER, NO_TEACHER);
        String bRoom = extras.getString(RecyclerSubAdapter.EXTRA_ROOM, NO_ROOM); */

        for (SubjectsModel subjectsModel : dbHelper.getAllSubjects())
        {
            /*if ((ID != NO_ID) && (!bTitle.equals(NO_TITLE) && (!bTeacher.equals(NO_TEACHER)
                && (!bRoom.equals(NO_ROOM))))) */
            if (ID != NO_ID)
            {
                setTitle("Add Subject");
                invalidateOptionsMenu();
            }
            else
            {
                setTitle("Edit Subject");
            }
        }


    }

    private void saveSubject()
    {
        SubjectsModel model = new SubjectsModel();
        String titleString = mTitleEditText.getText().toString().trim();
        String teacherString = mTeacherEditText.getText().toString().trim();
        String roomString = mRoomEditText.getText().toString().trim();

        model.setmTitle(titleString);
        model.setmTeacher(teacherString);
        model.setmRoom(roomString);

        dbHelper.addClass(model);

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
                    NavUtils.navigateUpFromSameTask(SubjectsEditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(SubjectsEditorActivity.this);
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
