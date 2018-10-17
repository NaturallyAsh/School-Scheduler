package com.example.ashleighwilson.schoolscheduler;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.feio.android.simplegallery.util.BitmapUtils;

import com.example.ashleighwilson.schoolscheduler.data.Storage;
import com.example.ashleighwilson.schoolscheduler.models.Attachment;
import com.example.ashleighwilson.schoolscheduler.notes.Constants;
import com.example.ashleighwilson.schoolscheduler.notes.InterceptorLinearLayout;
import com.example.ashleighwilson.schoolscheduler.notes.Note;
import com.example.ashleighwilson.schoolscheduler.utils.AnimationsHelper;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.w3c.dom.Text;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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

    //public NoteListFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mNoteListFragment = this;
        setHasOptionsMenu(true);
        setRetainInstance(true);
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
        sketchFab.setOnClickListener(listener);


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        notesActivity = (NotesActivity) getActivity();
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

    void editNote2(Note note)
    {
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
            if (targetView == null)
            {
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


}
