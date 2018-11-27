package com.example.ashleighwilson.schoolscheduler;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;

import com.github.clans.fab.FloatingActionMenu;


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
    private EditText titleView, teacherView;
    private String sTitle, sTeacher;
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public RecyclerSubAdapter recyclerSubAdapter;
    public ArrayList<SubjectsModel> subMod = new ArrayList<>();
    public ArrayList<SubjectsModel> dataSub;
    public TextView emptyView;
    DbHelper dbHelper;
    FloatingActionMenu fabAll;
    com.github.clans.fab.FloatingActionButton subFab;
    com.github.clans.fab.FloatingActionButton recFab;

    public SubjectsFrag() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate!");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         View view = inflater.inflate(R.layout.fragment_subjects, container, false);

        setHasOptionsMenu(true);
        Log.i(TAG, "onCreateView!");

        titleView = view.findViewById(R.id.edit_subject);
        teacherView = view.findViewById(R.id.edit_subject_teacher);
        emptyView = view.findViewById(R.id.empty_subject_view);
        subFab = view.findViewById(R.id.fab_sub);
        recFab = view.findViewById(R.id.fab_record);
        fabAll = view.findViewById(R.id.fab_all);
        fabAll.showMenu(true);
        fabAll.setClosedOnTouchOutside(true);
        subFab.setOnClickListener(listener);
        recFab.setOnClickListener(listener);

        FloatingClicked();

        dbHelper = DbHelper.getInstance();

        //dataSub = dbHelper.getAllSubjects();

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerSubAdapter = new RecyclerSubAdapter(getContext(), subMod, recyclerView);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);


        subjectDatabaseList();

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback
                (ItemTouchHelper.LEFT | ItemTouchHelper.DOWN |
                ItemTouchHelper.UP, ItemTouchHelper.LEFT ) {
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
                            recyclerSubAdapter.dismissItem(viewHolder.getAdapterPosition());
                            if (recyclerSubAdapter.getItemCount() == 0) {
                                updateUI();
                            }
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            recyclerSubAdapter.notifyItemRemoved(position + 1);
                            recyclerSubAdapter.notifyItemRangeChanged(position, recyclerSubAdapter.getItemCount());
                            recyclerSubAdapter.notifyItemChanged(position);
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
            int color = cursor.getInt(4);
            String start = cursor.getString(5);
            String end = cursor.getString(6);

            SubjectsModel model = new SubjectsModel(id, title, teacher, room, color, start, end);

            subMod.add(model);
        }

        updateUI();
    }

    private void updateUI() {
        Log.i(TAG, "updateUI");
        if (recyclerSubAdapter.getItemCount() == 0)
        {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(recyclerSubAdapter);
            recyclerSubAdapter.notifyDataSetChanged();
        }
        else
        {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            recyclerView.setAdapter(recyclerSubAdapter);
            recyclerSubAdapter.setData(subMod);
            recyclerSubAdapter.notifyDataSetChanged();
        }
    }

    private void showDialog() {
        SubjectsEditor addSubjectDialog = new SubjectsEditor();
        addSubjectDialog.setTargetFragment(this, 0);
        addSubjectDialog.show(getFragmentManager(), null);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.i(TAG, "onResume!");
        subjectDatabaseList();
    }

    private void FloatingClicked()
    {
        fabAll.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId())
                {
                    case R.id.fab_sub:

                        break;
                    case R.id.fab_record:
                        break;
                    default:
                        fabAll.close(true);
                        break;
                }
                fabAll.toggle(true);
            }
        });
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean check = fabAll.isOpened();
            switch (view.getId())
            {
                case R.id.fab_sub:
                    startActivity(new Intent(getContext(), SubjectsEditorActivity.class));
                    fabAll.close(true);
                    break;
                case R.id.fab_record:
                    startActivity(new Intent(getContext(), RecordActivity.class));
                    fabAll.close(true);
                    break;
                default:
                    fabAll.close(true);
            }
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        menu.clear();
        inflater.inflate(R.menu.menu_overview, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
