package com.example.ashleighwilson.schoolscheduler;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class TasksFrag extends Fragment
{
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;


    public TasksFrag() {
        // Required empty public constructor
    }

    /*public static TasksFrag newInstance(int page)
    {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        TasksFrag frag = new TasksFrag();
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    } */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fab_tasks);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        return view;
    }

}
