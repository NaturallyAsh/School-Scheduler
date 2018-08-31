package com.example.ashleighwilson.schoolscheduler;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ashleighwilson.schoolscheduler.editors.SubjectsEditorActivity;
import com.github.clans.fab.FloatingActionMenu;


public class TasksFrag extends Fragment
{
    FloatingActionMenu fabAll;
    com.github.clans.fab.FloatingActionButton taskFab;


    public TasksFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        taskFab = view.findViewById(R.id.fab_task);
        fabAll = view.findViewById(R.id.fab_all_task);
        fabAll.showMenu(true);
        taskFab.setOnClickListener(listener);

        FloatingClicked();

        return view;
    }

    private void FloatingClicked()
    {
        fabAll.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId())
                {
                    case R.id.fab_task:

                        break;
                    case R.id.fab_record_task:
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
                case R.id.fab_task:
                    //startActivity(new Intent(getContext(), SubjectsEditorActivity.class));
                    fabAll.close(true);
                    break;
                case R.id.fab_record_task:
                    break;
                default:
                    fabAll.close(true);
            }
        }
    };


}
