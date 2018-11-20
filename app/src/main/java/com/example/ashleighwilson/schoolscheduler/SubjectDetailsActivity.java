package com.example.ashleighwilson.schoolscheduler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.example.ashleighwilson.schoolscheduler.adapter.RecyclerSubAdapter;
import com.example.ashleighwilson.schoolscheduler.editors.SubjectsEditorActivity;
import com.example.ashleighwilson.schoolscheduler.models.SubjectsModel;

public class SubjectDetailsActivity extends AppCompatActivity {

    private static final String TAG = SubjectDetailsActivity.class.getSimpleName();

    private SubjectsModel model;
    private TextView teacherTV;
    private static final String EXTRA_ID = "id";
    Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        model = getIntent().getParcelableExtra(RecyclerSubAdapter.EXTRA_ID);

        CollapsingToolbarLayout cT = findViewById(R.id.detail_collapsing_toolbar);

        cT.setTitle(model.getmTitle());
        cT.setBackgroundColor(model.getmColor());

        teacherTV = findViewById(R.id.detail_teacherTV);
        teacherTV.setText(model.getmTeacher());

        FloatingActionButton fab = findViewById(R.id.detail_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SubjectsEditorActivity.class);
                intent.putExtra(RecyclerSubAdapter.EXTRA_ID, model);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }


}
