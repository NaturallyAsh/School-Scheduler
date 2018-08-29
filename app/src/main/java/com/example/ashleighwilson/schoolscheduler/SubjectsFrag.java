package com.example.ashleighwilson.schoolscheduler;


import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import java.util.Collections;


public class SubjectsFrag extends DialogFragment
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
    public ArrayList<SubjectsModel> dataSub;
    public TextView emptyView;
    DbHelper dbHelper;
    public static final int SUB_ADD = 1;

    public SubjectsFrag() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_subjects, container, false);

        dbHelper = new DbHelper(getActivity());

        dataSub = dbHelper.getAllSubjects();

        FloatingActionButton fab = view.findViewById(R.id.fab_sub);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SubjectsEditorActivity.class);
                startActivity(intent);
            }
        });

        titleView = view.findViewById(R.id.edit_subject);
        teacherView = view.findViewById(R.id.edit_subject_teacher);

        emptyView = view.findViewById(R.id.empty_subject_view);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerSubAdapter = new RecyclerSubAdapter(getContext(), subMod);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        subjectDatabaseList();

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback
                (ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN |
                ItemTouchHelper.UP, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();

                Collections.swap(subMod, from, to);
                recyclerSubAdapter.notifyItemMoved(from, to);
                return true;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction)
            {
                final int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Are you sure to delete?");
                    builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dbHelper.delete(position);
                            subMod.remove(viewHolder.getAdapterPosition());
                            recyclerSubAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());

                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            recyclerSubAdapter.notifyItemRemoved(position + 1);
                            recyclerSubAdapter.notifyItemRangeChanged(position, recyclerSubAdapter.getItemCount());

                        }
                    }).show();
                }
            }

        });

        helper.attachToRecyclerView(recyclerView);

        return view;
    }

    private void subjectDatabaseList()
    {
        subMod.clear();
        Cursor cursor = dbHelper.getAltSub();

        while (cursor.moveToNext())
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
            emptyView.setVisibility(View.GONE);
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

    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction)
    {
        final int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure to delete?");
            builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    recyclerSubAdapter.notifyItemRemoved(position);
                    dbHelper.delete(position);

                    return;
                }
            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    recyclerSubAdapter.notifyItemRemoved(position + 1);
                    recyclerSubAdapter.notifyItemRangeChanged(position, recyclerSubAdapter.getItemCount());

                    return;
                }
            }).show();
        }
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
