package com.example.ashleighwilson.schoolscheduler.editors;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.adapter.RecyclerSubAdapter;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.dialog.SimpleColorDialog;
import com.example.ashleighwilson.schoolscheduler.models.SubjectsModel;

import java.util.ArrayList;

import eltos.simpledialogfragment.SimpleDialog;

public class SubjectsEditorActivity extends AppCompatActivity implements SimpleDialog.OnDialogResultListener
{
    private static final String TAG = SubjectsEditorActivity.class.getSimpleName();

    private EditText mTitleEditText;
    private EditText mTeacherEditText;
    private EditText mRoomEditText;
    private TextView viewColor;
    final static private String COLOR_DIALOG = "colorDialog";
    static private int subColor;
    DbHelper dbHelper;
    private static final int NO_ID = -99;
    private static final String NO_TITLE = "";
    private static final String NO_TEACHER = "";
    private static final String NO_ROOM = "";
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_subjects);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DbHelper(getApplicationContext());
        model = dbHelper.getAllSubjects();

        Button colorSelector = findViewById(R.id.sub_create_button);
        viewColor = findViewById(R.id.sub_view_color);

        subColor = getMatColor("200");
        viewColor.setBackgroundColor(subColor);

        mTitleEditText = findViewById(R.id.edit_subject);
        mTeacherEditText = findViewById(R.id.edit_subject_teacher);
        mRoomEditText = findViewById(R.id.subject_room);

        mTitleEditText.setOnTouchListener(mTouchListener);
        mTeacherEditText.setOnTouchListener(mTouchListener);
        mRoomEditText.setOnTouchListener(mTouchListener);

        colorSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleColorDialog.build()
                        .title("Pick a subject color")
                        .colorPreset(Color.WHITE)
                        .allowCustom(true)
                        .show(SubjectsEditorActivity.this, COLOR_DIALOG);
            }
        });

        Bundle intent = getIntent().getExtras();
        if (intent != null)
        {
            //int ID = intent.getIntExtra(RecyclerSubAdapter.EXTRA_ID, NO_ID);
            String bTitle = intent.getString(RecyclerSubAdapter.EXTRA_TITLE);
            String bTeacher = intent.getString(RecyclerSubAdapter.EXTRA_TEACHER);
            String bRoom = intent.getString(RecyclerSubAdapter.EXTRA_ROOM);
            int bColor = intent.getInt(RecyclerSubAdapter.EXTRA_COLOR);

            //if ((ID != NO_ID) && (!bTitle.equals(NO_TITLE) && (!bTeacher.equals(NO_TEACHER)
              //  && (!bRoom.equals(NO_ROOM)))))
            if (bTitle.equals(editTitle))
            {
                setTitle("Add Subject");
                invalidateOptionsMenu();
            }
            else
            {
                setTitle("Edit Subject");
                mTitleEditText.setText(bTitle);
                mTeacherEditText.setText(bTeacher);
                mRoomEditText.setText(bRoom);


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
        return false;
    }

    private void saveSubject()
    {
        SubjectsModel model = new SubjectsModel();
        String titleString = mTitleEditText.getText().toString().trim();
        String teacherString = mTeacherEditText.getText().toString().trim();
        String roomString = mRoomEditText.getText().toString().trim();


        /*if (titleString.equals(""))
        {
            Toast.makeText(this, "Subject name needed", Toast.LENGTH_SHORT).show();
        }
        else
        {
            model.setmTitle(titleString);

        }
        if (teacherString.equals(""))
        {
            Toast.makeText(this, "Teacher name needed", Toast.LENGTH_SHORT).show();
        }
        else
        {
            model.setmTeacher(teacherString);

        }
        if (roomString.equals(""))
        {
            Toast.makeText(this, "Room number needed", Toast.LENGTH_SHORT).show();
        }
        else
        {
            model.setmRoom(roomString);

        } */

        model.setmTitle(titleString);
        model.setmTeacher(teacherString);
        model.setmRoom(roomString);
        model.setmColor(subColor);

        dbHelper.addClass(model);
        if (dbHelper == null)
        {
            Toast.makeText(this, "Error with saving Subject", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Subject saved", Toast.LENGTH_SHORT).show();
        }

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
