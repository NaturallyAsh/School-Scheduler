package com.example.ashleighwilson.schoolscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.ashleighwilson.schoolscheduler.models.WeekViewEvent;

public class EventDetailActivity extends AppCompatActivity {

    private static final String TAG = EventDetailActivity.class.getSimpleName();

    private WeekViewEvent weekViewEvent;
    private CollapsingToolbarLayout ct;
    private TextView schedule;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail);

        final Toolbar toolbar = findViewById(R.id.event_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle.containsKey("event")) {
                weekViewEvent = (WeekViewEvent) bundle.get("event");
            }
        }

        ct = findViewById(R.id.event_detail_collapsing_toolbar);
        schedule = findViewById(R.id.event_detail_weekly_schedule);

        if (weekViewEvent != null) {
            ct.setTitle(weekViewEvent.getName());
            ct.setBackgroundColor(weekViewEvent.getColor());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overview, menu);
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

    @Override
    public void onBackPressed()
    {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0)
        {
            manager.popBackStack();
        }
        else
            super.onBackPressed();
    }
}
