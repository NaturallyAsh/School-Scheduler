package com.example.ashleighwilson.schoolscheduler;


import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;

import com.example.ashleighwilson.schoolscheduler.adapter.RecyclerSubAdapter;
import com.example.ashleighwilson.schoolscheduler.adapter.SubjectCursorAdapter;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.editors.SubjectsEditor;
import com.example.ashleighwilson.schoolscheduler.editors.SubjectsEditorActivity;
import com.example.ashleighwilson.schoolscheduler.models.SubjectsModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class SubjectsFrag extends DialogFragment implements RecyclerSubAdapter.ClickListener,
        SubjectsEditor.OnAddSubjectListener
{
    private static final String TAG = SubjectsFrag.class.getSimpleName();

    @NonNull
    Context context;
    private EditText titleView, teacherView;
    private String sTitle, sTeacher;
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public SubjectCursorAdapter mCursorAdapter;
    public RecyclerSubAdapter recyclerSubAdapter;
    public ArrayList<SubjectsModel> subMod = new ArrayList<>();
    public TextView emptyView;
    DbHelper dbHelper;
    private static final int SUBJECT_LOADER = 0;
    @BindView(R.id.main_root)
    ViewGroup root;

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
        subMod = dbHelper.getAllSubjects();

        titleView = view.findViewById(R.id.edit_subject);
        teacherView = view.findViewById(R.id.edit_subject_teacher);

        //emptyView = view.findViewById(R.id.empty_subject_view);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerSubAdapter = new RecyclerSubAdapter(getContext(), subMod);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        //isRecyclerViewEmpty();

        recyclerView.setAdapter(recyclerSubAdapter);

        return view;
    }

    public void isRecyclerViewEmpty() {
        if (recyclerView == null) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            recyclerView.setAdapter(recyclerSubAdapter);
        }
    }

    @Override
    public void itemClicked(View view, int position) {

    }

    @Override
    public void OnAddSubjectSubmit(String title, String teacher) {
        SubjectsModel model = new SubjectsModel();

        model.setmTitle(title);
        model.setmTeacher(teacher);

        //dbHelper.addClass(model);
        subMod.add(model);

        recyclerSubAdapter.setData(subMod);

        //recyclerSubAdapter.notifyDataSetChanged();

    }

    private void showDialog() {
        SubjectsEditor addSubjectDialog = new SubjectsEditor();
        addSubjectDialog.setTargetFragment(this, 0);
        addSubjectDialog.show(getFragmentManager(), null);

    }
}
