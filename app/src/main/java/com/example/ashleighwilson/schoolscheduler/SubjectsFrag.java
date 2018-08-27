package com.example.ashleighwilson.schoolscheduler;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;

import com.example.ashleighwilson.schoolscheduler.adapter.RecyclerSubAdapter;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.editors.SubjectsEditor;
import com.example.ashleighwilson.schoolscheduler.editors.SubjectsEditorActivity;
import com.example.ashleighwilson.schoolscheduler.models.SubjectsModel;

import java.util.ArrayList;


public class SubjectsFrag extends DialogFragment implements RecyclerSubAdapter.ClickListener
{
    private static final String TAG = SubjectsFrag.class.getSimpleName();

    @NonNull
    Context context;
    private EditText titleView, teacherView;
    private String sTitle, sTeacher;
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public RecyclerSubAdapter recyclerSubAdapter;
    public ArrayList<SubjectsModel> subMod = new ArrayList<>();
    public TextView emptyView;
    DbHelper dbHelper;

    public SubjectsFrag() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_subjects, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fab_sub);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showDialog();
                Intent intent = new Intent(getContext(), SubjectsEditorActivity.class);
                startActivity(intent);
            }
        });

        dbHelper = new DbHelper(getActivity());
        //subMod = dbHelper.getAllSubjects();


        titleView = view.findViewById(R.id.edit_subject);
        teacherView = view.findViewById(R.id.edit_subject_teacher);

        emptyView = view.findViewById(R.id.empty_subject_view);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerSubAdapter = new RecyclerSubAdapter(getContext(), subMod);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        subjectDatabaseList();

        return view;
    }

    private void subjectDatabaseList()
    {
        subMod.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.getAltSub();
        //while (cursor.moveToNext())
        if (cursor.moveToFirst())
        {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String teacher = cursor.getString(2);
            String room = cursor.getString(3);

            SubjectsModel model = new SubjectsModel(id, title, teacher, room);

            subMod.add(model);
        }

        if (!(subMod.size()<1))
        {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(recyclerSubAdapter);
            recyclerSubAdapter.notifyDataSetChanged();
        }
        else
        {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(recyclerSubAdapter);
            recyclerSubAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void itemClicked(View view, int position) {

    }


    public void OnAddSubjectSubmit(String title, String teacher, String room) {
        SubjectsModel model = new SubjectsModel();

        model.setmTitle(title);
        model.setmTeacher(teacher);
        model.setmRoom(room);

        dbHelper.addClass(model);


        //recyclerSubAdapter.setData(subMod);

        //recyclerSubAdapter.notifyDataSetChanged();

    }

    private void showDialog() {
        SubjectsEditor addSubjectDialog = new SubjectsEditor();
        addSubjectDialog.setTargetFragment(this, 0);
        addSubjectDialog.show(getFragmentManager(), null);

    }

    @Override
    public void onResume()
    {
        super.onResume();
        subjectDatabaseList();
    }
}
