package com.example.ashleighwilson.schoolscheduler;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ashleighwilson.schoolscheduler.adapter.AgendaAdapter;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.editors.AgendaEditor;
import com.example.ashleighwilson.schoolscheduler.models.AgendaModel;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;


public class AgendaFrag extends Fragment
{
    FloatingActionMenu fabAll;
    com.github.clans.fab.FloatingActionButton agendaFab;
    private TextView emptyView;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private DbHelper dbHelper;
    private AgendaAdapter agendaAdapter;
    private ArrayList<AgendaModel> agendaList = new ArrayList<>();
    AgendaEditor agendaEditor;


    public AgendaFrag() {
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

        View view = inflater.inflate(R.layout.fragment_agendas, container, false);

        setHasOptionsMenu(true);

        agendaFab = view.findViewById(R.id.fab_agenda);
        fabAll = view.findViewById(R.id.fab_all_agenda);
        fabAll.showMenu(true);
        fabAll.setClosedOnTouchOutside(true);
        agendaFab.setOnClickListener(listener);
        emptyView = view.findViewById(R.id.empty_agenda_view);

        recyclerView = view.findViewById(R.id.agenda_recycler_view);
        recyclerView.setHasFixedSize(true);

        agendaAdapter = new AgendaAdapter(getContext(), agendaList);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        if (!(agendaList.size()< 1))
        {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            recyclerView.setAdapter(agendaAdapter);
            agendaAdapter.notifyDataSetChanged();
        }
        else
        {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(agendaAdapter);
            agendaAdapter.notifyDataSetChanged();
        }

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_overview, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}