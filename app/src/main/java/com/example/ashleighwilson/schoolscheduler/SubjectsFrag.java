package com.example.ashleighwilson.schoolscheduler;


import android.content.Context;
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

import com.example.ashleighwilson.schoolscheduler.adapter.CardAdapter;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.editors.SubjectsEditor;
import com.example.ashleighwilson.schoolscheduler.models.SubjectsModel;

import java.util.ArrayList;

import butterknife.BindView;


public class SubjectsFrag extends DialogFragment implements CardAdapter.ClickListener,
        SubjectsEditor.OnAddSubjectListener
{
    private static final String TAG = SubjectsFrag.class.getSimpleName();

    @NonNull
    Context context;
    private EditText titleView, teacherView;
    private String sTitle, sTeacher;
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public CardAdapter cardAdapter;
    public SubjectsModel model = null;
    public ArrayList<SubjectsModel> subMod;
    DbHelper dbHelper;
    @BindView(R.id.main_root)
    ViewGroup root;

    public SubjectsFrag() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        subMod = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_subjects, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fab = view.findViewById(R.id.fab_sub);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        titleView = view.findViewById(R.id.edit_subject);
        teacherView = view.findViewById(R.id.edit_subject_teacher);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);


        cardAdapter = new CardAdapter(getContext(), subMod);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cardAdapter);

    }

    @Override
    public void itemClicked(View view, int position) {

    }

    @Override
    public void OnAddSubjectSubmit(String title, String teacher)
    {
        SubjectsModel model = new SubjectsModel(sTitle, sTeacher);

        model.setmTitle(title);
        model.setmTeacher(teacher);
        subMod.add(model);

        cardAdapter.notifyDataSetChanged();

    }

    private void showDialog()
    {
        SubjectsEditor addSubjectDialog = new SubjectsEditor();
        addSubjectDialog.setTargetFragment(this, 0);
        addSubjectDialog.show(getFragmentManager(), null);

    }
}
