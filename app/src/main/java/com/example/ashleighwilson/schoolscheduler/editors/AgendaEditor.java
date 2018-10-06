package com.example.ashleighwilson.schoolscheduler.editors;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;

import java.util.List;

public class AgendaEditor extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    private static final String TAG = AgendaEditor.class.getSimpleName();

    private EditText mAssignmentTitle;
    private TextView mDueDate, viewColor;
    private Spinner mClassName;
    private DbHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_agenda);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DbHelper(getApplicationContext());

        mAssignmentTitle = findViewById(R.id.assignment_title_edit_text);
        mClassName = findViewById(R.id.agenda_subject_spinner);
        mDueDate = findViewById(R.id.agenda_due_date_text);
        viewColor = findViewById(R.id.agenda_view_color);

        loadSpinnerData();

    }

    private void loadSpinnerData()
    {
        List<String> labels = dbHelper.getAllLabels();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, labels);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mClassName.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
       String label = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {

    }
}
