package com.example.ashleighwilson.schoolscheduler;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.w3c.dom.Text;

public class NoteListFragment extends Fragment
{
    private static final String TAG = NoteListFragment.class.getSimpleName();


    @BindView(R.id.note_list_rv)
    RecyclerView recyclerView;
    @BindView(R.id.notes_frag_emptyView)
    TextView emptyNotesView;
    @BindView(R.id.fab_note_all)
    FloatingActionMenu fabNoteMenu;
    @BindView(R.id.fab_note_note)
    FloatingActionButton noteFab;
    @BindView(R.id.fab_note_photo)
    FloatingActionButton photoFab;
    private NoteListFragment mNoteListFragment;
    private OverviewActivity overviewActivity;

    public NoteListFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mNoteListFragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_note_list, container, false);
        ButterKnife.bind(this, rootView);

        fabNoteMenu.showMenu(true);
        fabNoteMenu.setClosedOnTouchOutside(true);

        noteFab.setOnClickListener(listener);
        photoFab.setOnClickListener(listener);

        return rootView;
    }

    private void FloatingClicked()
    {
        fabNoteMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.fab_note_note:
                        break;
                    case R.id.fab_note_photo:
                        break;
                    default:
                        fabNoteMenu.close(true);
                        break;
                }
                fabNoteMenu.toggle(true);
            }
        });
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.fab_note_note:
                    break;
                case R.id.fab_note_photo:
                    break;
                default:
                    fabNoteMenu.close(true);
                    break;
            }
            fabNoteMenu.toggle(true);
        }
    };
}
