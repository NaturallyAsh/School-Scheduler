package com.example.ashleighwilson.schoolscheduler;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashleighwilson.schoolscheduler.adapter.DetailAssignmentAdapter;
import com.example.ashleighwilson.schoolscheduler.adapter.RecyclerSubAdapter;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.editors.SubjectsEditorActivity;
import com.example.ashleighwilson.schoolscheduler.models.AgendaModel;
import com.example.ashleighwilson.schoolscheduler.models.SubjectsModel;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;
import java.util.List;

public class SubjectDetailsActivity extends AppCompatActivity {

    private static final String TAG = SubjectDetailsActivity.class.getSimpleName();

    private SubjectsModel subModel;
    private AgendaModel agendaModel;
    private List<AgendaModel> agendaModelList;
    private TextView teacherTV, emptyTV;
    private ArrayList<AgendaModel> agendaList = new ArrayList<>();
    private List<AgendaModel> agendaTestList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager manager;
    private DetailAssignmentAdapter adapter;
    private DbHelper dbHelper;
    private static final String EXTRA_ID = "id";
    ExpandableTextView expandableTextView;
    Context mContext;
    CollapsingToolbarLayout cT;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        subModel = getIntent().getParcelableExtra(RecyclerSubAdapter.EXTRA_ID);

        dbHelper = DbHelper.getInstance();
        recyclerView = findViewById(R.id.detail_events_rv);
        recyclerView.setHasFixedSize(true);

        agendaModelList = dbHelper.getAllAgendas();

        emptyTV = findViewById(R.id.detail_emptyview);

        adapter = new DetailAssignmentAdapter(this, agendaList);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        loadAssignments();

        /*expandableTextView = findViewById(R.id.expand_detail_view)
                .findViewById(R.id.expand_text_view);
        expandableTextView.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {
                Toast.makeText(getApplicationContext(), isExpanded ? "Expanded" : "Collapsed", Toast.LENGTH_SHORT).show();
            }
        });
        expandableTextView.setText(getString(R.string.dummy_text1));*/

        cT = findViewById(R.id.detail_collapsing_toolbar);

        cT.setTitle(subModel.getmTitle());
        cT.setBackgroundColor(subModel.getmColor());
        //Log.i(TAG, "color: " + subModel.getmColor());

        teacherTV = findViewById(R.id.detail_teacherTV);
        teacherTV.setText(subModel.getmTeacher());

        FloatingActionButton fab = findViewById(R.id.detail_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SubjectsEditorActivity.class);
                intent.putExtra(RecyclerSubAdapter.EXTRA_ID, subModel);
                startActivityForResult(intent, 1);
            }
        });

    }

    public void loadAssignments() {
        updateUI();
        boolean nameExists = false;
        for (AgendaModel agendaModel1 : agendaModelList) {
            if (subModel.getmTitle().equals(agendaModel1.getClassName())) {
                nameExists = true;
                break;
            }
        }
        Log.i(TAG, "name exists: " + nameExists);
        if (nameExists) {
            agendaList.clear();
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String[] projection = {
                    DbHelper.AgendaEntry._ID,
                    DbHelper.AgendaEntry.COLUMN_TITLE,
                    DbHelper.AgendaEntry.COLUMN_NAME,
                    DbHelper.AgendaEntry.COLUMN_DUEDATE,
                    DbHelper.AgendaEntry.COLUMN_COLOR
            };

            String selection = DbHelper.AgendaEntry.COLUMN_NAME + " =?";
            String[] selectionArgs = {subModel.getmTitle()};

            Cursor cursor = db.query(
                    DbHelper.AgendaEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
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

                AgendaModel model = new AgendaModel(id, name, title, dueDate, color);

                agendaList.add(model);
            }
            updateUI();
        }
    }

    private void updateUI() {
        if (adapter.getItemCount() == 0)
        {
            recyclerView.setVisibility(View.GONE);
            emptyTV.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        else
        {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTV.setVisibility(View.GONE);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //loadAssignments();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overview, menu);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (data != null) {
                subModel = data.getParcelableExtra(RecyclerSubAdapter.EXTRA_ID);
                cT.setBackgroundColor(subModel.getmColor());
                //Log.i(TAG, "on result color: " + subModel.getmColor());
                cT.setTitle(subModel.getmTitle());
            }
        }
    }

}
