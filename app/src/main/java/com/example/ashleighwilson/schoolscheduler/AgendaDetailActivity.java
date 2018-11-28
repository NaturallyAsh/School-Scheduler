package com.example.ashleighwilson.schoolscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.ashleighwilson.schoolscheduler.adapter.DetailAssignmentAdapter;
import com.example.ashleighwilson.schoolscheduler.models.AgendaModel;

public class AgendaDetailActivity extends AppCompatActivity {

    private static final String TAG = AgendaDetailActivity.class.getSimpleName();

    private AgendaModel agendaModel;
    private CollapsingToolbarLayout collapsingToolbar;
    private TextView titleTv, dueDateTv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_detail);

        final Toolbar toolbar = findViewById(R.id.agenda_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        agendaModel = getIntent().getParcelableExtra(DetailAssignmentAdapter.ARG_ID);

        collapsingToolbar = findViewById(R.id.agenda_detail_CT);
        titleTv = findViewById(R.id.agenda_detail_titleTv);
        dueDateTv = findViewById(R.id.agenda_detail_dueTv);

        collapsingToolbar.setTitle(agendaModel.getAgendaTitle());
        collapsingToolbar.setBackgroundColor(agendaModel.getmColor());
        titleTv.setText(agendaModel.getAgendaTitle());
        dueDateTv.setText(agendaModel.getDueDate());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
