package com.example.ashleighwilson.schoolscheduler;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.feio.android.simplegallery.util.BitmapUtils;

import com.example.ashleighwilson.schoolscheduler.adapter.NoteAdapter;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.data.NoteEvent;
import com.example.ashleighwilson.schoolscheduler.data.NoteLoaderTask;
import com.example.ashleighwilson.schoolscheduler.data.NoteProcessorDelete;
import com.example.ashleighwilson.schoolscheduler.data.Storage;
import com.example.ashleighwilson.schoolscheduler.notes.Attachment;
import com.example.ashleighwilson.schoolscheduler.notes.Constants;
import com.example.ashleighwilson.schoolscheduler.notes.InterceptorLinearLayout;
import com.example.ashleighwilson.schoolscheduler.notes.Note;
import com.example.ashleighwilson.schoolscheduler.utils.AnimationsHelper;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    @BindView(R.id.fab_note_sketch)
    FloatingActionButton sketchFab;
    @BindView(R.id.expanded_imageview)
    ImageView expandedIV;
    @BindView(R.id.note_list_root)
    InterceptorLinearLayout listRoot;
    private NoteListFragment mNoteListFragment;
    private NotesActivity notesActivity;
    public Uri attachmentUri;
    private List<Note> selectedNotes = new ArrayList<>();
    private List<Note> getAllNotes = new ArrayList<>();
    public NoteAdapter listAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private DbHelper dbHelper;
    private NoteEvent noteEvent;
    private Note note;
    NoteAdapter.NoteClickListener clickListener;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mNoteListFragment = this;
        setHasOptionsMenu(true);
        setRetainInstance(true);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop(){
        EventBus.getDefault().unregister(this);
        super.onStop();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        initListView();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(NoteEvent noteEvent) {

        selectedNotes.add(noteEvent.mNote);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_note_list, container, false);
        ButterKnife.bind(this, rootView);

        dbHelper = DbHelper.getInstance();


        fabNoteMenu.showMenu(true);
        fabNoteMenu.setClosedOnTouchOutside(true);

        noteFab.setOnClickListener(listener);
        photoFab.setOnClickListener(listener);
        sketchFab.setOnClickListener(listener);

        recyclerView.setHasFixedSize(true);
        listAdapter = new NoteAdapter(getContext(), selectedNotes);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        listAdapter.setOnNoteClickListener(new NoteAdapter.NoteClickListener() {
            @Override
            public void OnNoteClicked(View view, int position) {

                editNote(listAdapter.getNotes().get(position), view);
            }
        });

        noteEvent = EventBus.getDefault().removeStickyEvent(NoteEvent.class);
        if (noteEvent != null) {
            selectedNotes.add(noteEvent.mNote);
        }

        initListView();

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.DOWN |
                ItemTouchHelper.UP, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();

                Collections.swap(selectedNotes, from, to);
                listAdapter.notifyItemMoved(from, to);

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Are you sure to delete?");
                    builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listAdapter.dismissNote(viewHolder.getAdapterPosition());
                            if (listAdapter.getItemCount() == 0)
                                updateUI();
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listAdapter.notifyItemRemoved(position + 1);
                            listAdapter.notifyItemRangeChanged(position, listAdapter.getItemCount());
                        }
                    }).show();
                }
            }
        });

        helper.attachToRecyclerView(recyclerView);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        notesActivity = (NotesActivity) getActivity();
        notesActivity.getSupportActionBar().setTitle("Notes");
    }

    private void initListView()
    {
        selectedNotes.clear();
        Cursor cursor = dbHelper.getAltNotes();

        while (cursor.moveToNext())
        {
            long id = cursor.getLong(0);
            Long creation = cursor.getLong(1);
            Long lastMod = cursor.getLong(2);
            String title = cursor.getString(3);
            String content = cursor.getString(4);
            String alarm = cursor.getString(5);
            String rule = cursor.getString(6);

            Note note = new Note(id, creation, lastMod, title, content, alarm, rule);
            note.setAttachmentsList(dbHelper.getNoteAttachment(note));

            selectedNotes.add(note);

        }

        updateUI();

        NoteLoaderTask.getInstance().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "getEveryNote");

    }

    private void updateUI() {
        if (listAdapter.getItemCount() == 0)
        {
            recyclerView.setVisibility(View.GONE);
            emptyNotesView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyNotesView.setVisibility(View.GONE);
            recyclerView.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();
        }
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
                    editNote2(new Note());
                    break;
                case R.id.fab_note_photo:
                    Intent intent = notesActivity.getIntent();
                    intent.setAction(Constants.ACTION_FAB_TAKE_PHOTO);
                    notesActivity.setIntent(intent);
                    editNote2(new Note());
                    break;
                case R.id.fab_note_sketch:
                    takeSketch(null);
                    break;
                default:
                    fabNoteMenu.close(true);
                    break;
            }
            fabNoteMenu.toggle(true);
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_nav, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }

        return super.onOptionsItemSelected(item);
    }

    void editNote2(Note note)
    {
        if (note.getID() == null) {
            Log.i(TAG, "adding new note");
        } else {
            Log.i(TAG, "editing note with id: " + note.getID());
        }
        notesActivity.switchToDetail(note);
    }

    void editNote(final Note note, final View view)
    {
        AnimationsHelper.zoomListItem(notesActivity, view, getZoomListItemView(view, note),
                listRoot, buildAnimatorListenerAdapter(note));
    }

    private ImageView getZoomListItemView(View view, Note note)
    {
        if (expandedIV != null)
        {
            View targetView = null;
            if (note.getAttachmentsList().size() > 0)
            {
                targetView = view.findViewById(R.id.attachmentThumbnail);
            }
            if (targetView == null) {
                targetView = new ImageView(notesActivity);
                targetView.setBackgroundColor(Color.WHITE);
            }
            targetView.setDrawingCacheEnabled(true);
            targetView.buildDrawingCache();
            Bitmap bmp = targetView.getDrawingCache();
            expandedIV.setBackgroundColor(BitmapUtils.getDominantColor(bmp));
        }
        return expandedIV;
    }

    private AnimatorListenerAdapter buildAnimatorListenerAdapter(final Note note)
    {
        return new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                editNote2(note);
            }
        };
    }

    private void takeSketch(Attachment attachment)
    {
        File f = Storage.createNewAttachmentFile(notesActivity, Constants.MIME_TYPE_SKETCH_EXT);
        if (f == null)
        {
            Log.i(TAG, "Error");
            return;
        }
        attachmentUri = Uri.fromFile(f);

        FragmentTransaction transaction = notesActivity.getSupportFragmentManager().beginTransaction();
        notesActivity.animateTransition(transaction, notesActivity.TRANSITION_HORIZONTAL);
        SketchFragment sketchFragment = new SketchFragment();
        Bundle b = new Bundle();
        b.putParcelable(MediaStore.EXTRA_OUTPUT, attachmentUri);
        if (attachment != null)
            b.putParcelable("base", attachment.getUri());
        sketchFragment.setArguments(b);
        transaction.replace(R.id.fragment_note_container, sketchFragment, notesActivity.FRAGMENT_SKETCH_TAG)
                .addToBackStack(notesActivity.FRAGMENT_NOTE_DETAIL_TAG).commit();
    }

    private List<Note> getSelectedNotes() {
        return selectedNotes;
    }

    private void deleteNotesExecute() {
        listAdapter.removeNote(selectedNotes.size());
        new NoteProcessorDelete(getSelectedNotes()).process();
        selectedNotes.clear();
    }
}
