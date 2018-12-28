package com.example.ashleighwilson.schoolscheduler;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Button;
import android.widget.TextView;

import com.example.ashleighwilson.schoolscheduler.adapter.AgendaAdapter;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.editors.AgendaEditor;
import com.example.ashleighwilson.schoolscheduler.models.AgendaModel;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.Collections;


public class AgendaFrag extends Fragment
{
    private static final String TAG = AgendaFrag.class.getSimpleName();

    FloatingActionMenu fabAll;
    com.github.clans.fab.FloatingActionButton agendaFab;
    private TextView emptyView;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private DbHelper dbHelper;
    private AgendaAdapter agendaAdapter;
    private ArrayList<AgendaModel> agendaList = new ArrayList<>();
    private Context mContext = MySchedulerApp.getInstance();
    private OverviewActivity mOverviewActivity;
    private boolean[] dayOfWeek;
    private DbHelper DOWhelper;

    public AgendaFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate!");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_agendas, container, false);
        Log.i(TAG, "onCreateView!");

        agendaFab = view.findViewById(R.id.fab_agenda);
        fabAll = view.findViewById(R.id.fab_all_agenda);
        fabAll.showMenu(true);
        fabAll.setClosedOnTouchOutside(true);
        agendaFab.setOnClickListener(listener);
        emptyView = view.findViewById(R.id.empty_agenda_view);

        dbHelper = DbHelper.getInstance();
        DOWhelper = DbHelper.getInstance();
        dayOfWeek = new boolean[7];

        recyclerView = view.findViewById(R.id.agenda_recycler_view);
        recyclerView.setHasFixedSize(true);

        agendaAdapter = new AgendaAdapter(getContext(), agendaList);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        agendaDbList();

        FloatingClicked();

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.DOWN |
                ItemTouchHelper.UP, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();

                Collections.swap(agendaList, from, to);
                agendaAdapter.notifyItemMoved(from, to);

                return true;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Are you sure to delete?");
                    builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            agendaAdapter.dismissAgenda(viewHolder.getAdapterPosition());
                            if (agendaAdapter.getItemCount() == 0)
                                updateUI();
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            agendaAdapter.notifyItemRemoved(position + 1);
                            agendaAdapter.notifyItemRangeChanged(position, agendaAdapter.getItemCount());
                        }
                    }).show();
                }
            }
        });

        helper.attachToRecyclerView(recyclerView);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mOverviewActivity = (OverviewActivity) getActivity();
        //mOverviewActivity.getSupportActionBar();
    }

    public void agendaDbList()
    {
        agendaList.clear();
        /*Cursor cursor = dbHelper.getAgenda();
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String title = cursor.getString(2);
            String dueDate = cursor.getString(3);
            int color = cursor.getInt(4);
            long timeToNotify = cursor.getLong(5);
            long dayToNotify = cursor.getLong(6);
            long addReminder = cursor.getLong(7);
            int repeatType = cursor.getInt(8);

            AgendaModel model = new AgendaModel(id, name, title, dueDate, color, timeToNotify,
                    dayToNotify, addReminder, repeatType);

            agendaList.add(model);*/
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                DbHelper.AgendaEntry._ID,
                DbHelper.AgendaEntry.COLUMN_TITLE,
                DbHelper.AgendaEntry.COLUMN_NAME,
                DbHelper.AgendaEntry.COLUMN_DUEDATE,
                DbHelper.AgendaEntry.COLUMN_COLOR,
                DbHelper.AgendaEntry.COLUMN_TIME_TO_NOTIFY,
                DbHelper.AgendaEntry.COLUMN_ADD_REMINDER,
                DbHelper.AgendaEntry.COLUMN_REPEAT_TYPE,
                DbHelper.AgendaEntry.COLUMN_DATETIME
        };

        Cursor cursor = db.query(
                DbHelper.AgendaEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String title = cursor.getString(2);
            String dueDate = cursor.getString(3);
            int color = cursor.getInt(4);
            long timeToNotify = cursor.getLong(5);
            long addReminder = cursor.getLong(6);
            int repeatType = cursor.getInt(7);
            long dateTime = cursor.getLong(8);

            AgendaModel model = new AgendaModel(id, name, title, dueDate, color, timeToNotify,
                    addReminder, repeatType, dateTime);

            if (repeatType == 5) {
                Cursor dayCursor = db.rawQuery("SELECT * FROM " + DbHelper.DaysOfWeekEntry.TABLE_NAME +
                        " WHERE " + DbHelper.DaysOfWeekEntry._ID + " = " + id, null);

                if (dayCursor.moveToFirst()) {
                    do {
                        boolean[] daysOfWeek = new boolean[7];
                        for (int i = 0; i < 7; i++) {
                            //daysOfWeek[i] = Boolean.parseBoolean(dayCursor.getString(i + 1));
                            daysOfWeek[i] = (dayCursor.getInt(i + 1) == 1);

                            Log.i(TAG, "DOW: " + daysOfWeek[i]);
                        }
                        model.setmDayOfWeekInt(daysOfWeek);

                    } while (dayCursor.moveToNext());

                }
            }
            agendaList.add(model);
        }
        updateUI();
    }

    private void updateUI() {
        if (agendaAdapter.getItemCount() == 0)
        {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(agendaAdapter);
            agendaAdapter.notifyDataSetChanged();
        }
        else
        {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            recyclerView.setAdapter(agendaAdapter);
            agendaAdapter.notifyDataSetChanged();
        }
    }

    private void FloatingClicked()
    {
        fabAll.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId())
                {
                    case R.id.fab_agenda:

                        break;
                    case R.id.fab_record_agenda:
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
                case R.id.fab_agenda:
                    startActivity(new Intent(getContext(), AgendaEditor.class));
                    fabAll.close(true);
                    break;
                case R.id.fab_record_agenda:
                    break;
                default:
                    fabAll.close(true);
            }
        }
    };

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_overview, menu);

    }

    @Override
    public void onResume()
    {
        super.onResume();
        agendaDbList();
    }
}
