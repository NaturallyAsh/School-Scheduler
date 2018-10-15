package com.example.ashleighwilson.schoolscheduler;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class NotesActivity extends AppCompatActivity
{
    private static final String TAG = NotesActivity.class.getSimpleName();

    private FragmentManager mFragmentManager;
    public final String FRAGMENT_NOTE_LIST_TAG = "fragment_note_list";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_activity);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*FloatingActionButton fab = findViewById(R.id.fab_notes);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */

        getFragmentManagerInstance();
        if (getFragmentManagerInstance().findFragmentByTag(FRAGMENT_NOTE_LIST_TAG ) == null)
        {
            FragmentTransaction transaction = getFragmentManagerInstance().beginTransaction();
            transaction.add(R.id.fragment_note_container, new NoteListFragment(), FRAGMENT_NOTE_LIST_TAG).commit();
        }

    }

    private FragmentManager getFragmentManagerInstance()
    {
        if (mFragmentManager == null)
        {
            mFragmentManager = getSupportFragmentManager();
        }
        return mFragmentManager;
    }

}
