package com.example.ashleighwilson.schoolscheduler;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.notes.Attachment;
import com.example.ashleighwilson.schoolscheduler.notes.Constants;
import com.example.ashleighwilson.schoolscheduler.notes.Note;

import java.util.ArrayList;
import java.util.HashMap;

import eltos.simpledialogfragment.SimpleDialog;

public class NotesActivity extends AppCompatActivity
{
    private static final String TAG = NotesActivity.class.getSimpleName();

    private FragmentManager mFragmentManager;
    public final String FRAGMENT_NOTE_DETAIL_TAG = "fragment_note_detail";
    public final String FRAGMENT_NOTE_LIST_TAG = "fragment_note_list";
    public final String FRAGMENT_SKETCH_TAG = "fragment_sketch";
    public final int TRANSITION_VERTICAL = 0;
    public final int TRANSITION_HORIZONTAL = 1;
    public Toolbar toolbar;
    public Uri sketchUri;
    private DbHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_activity);

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Notes");
        //EventBus.getDefault().register(this);

        dbHelper = DbHelper.getInstance();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        init();

    }

    @Override
    public void onStop()
    {
        super.onStop();
        //EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        init();
    }


    private FragmentManager getFragmentManagerInstance()
    {
        if (mFragmentManager == null)
        {
            mFragmentManager = getSupportFragmentManager();
        }
        return mFragmentManager;
    }

    private void init()
    {
        getFragmentManagerInstance();
        if (getFragmentManagerInstance().findFragmentByTag(FRAGMENT_NOTE_LIST_TAG ) == null)
        {
            FragmentTransaction transaction = getFragmentManagerInstance().beginTransaction();
            transaction.add(R.id.fragment_note_container, new NoteListFragment(), FRAGMENT_NOTE_LIST_TAG).commit();
        }

        handleIntents();
    }

    public void switchToDetail(Note note)
    {
        FragmentTransaction transaction = getFragmentManagerInstance().beginTransaction();
        animateTransition(transaction, TRANSITION_HORIZONTAL);
        NotesDetailFragment mNotesDetailFragment = new NotesDetailFragment();
        Bundle b = new Bundle();
        b.putParcelable(Constants.INTENT_NOTE, note);
        mNotesDetailFragment.setArguments(b);
        if (getFragmentManagerInstance().findFragmentByTag(FRAGMENT_NOTE_DETAIL_TAG) == null)
        {
            transaction.replace(R.id.fragment_note_container, mNotesDetailFragment, FRAGMENT_NOTE_DETAIL_TAG)
                    .addToBackStack(FRAGMENT_NOTE_LIST_TAG)
                    .commitAllowingStateLoss();
        }
        else
        {
            getFragmentManagerInstance().popBackStackImmediate();
            transaction.replace(R.id.fragment_note_container, mNotesDetailFragment, FRAGMENT_NOTE_DETAIL_TAG)
                    .addToBackStack(FRAGMENT_NOTE_DETAIL_TAG)
                    .commitAllowingStateLoss();
        }
    }

    public void switchToList()
    {
        FragmentTransaction transaction = getFragmentManagerInstance().beginTransaction();
        animateTransition(transaction, TRANSITION_HORIZONTAL);
        NoteListFragment mNoteListFragment = new NoteListFragment();
        transaction.replace(R.id.fragment_note_container, mNoteListFragment, FRAGMENT_NOTE_LIST_TAG)
                .addToBackStack(FRAGMENT_NOTE_DETAIL_TAG).commitAllowingStateLoss();
        getFragmentManagerInstance().getFragments();
    }

    private boolean receivedIntent(Intent i) {
        return Constants.ACTION_SHORTCUT.equals(i.getAction())
                || Constants.ACTION_NOTIFICATION_CLICK.equals(i.getAction())
                || Constants.ACTION_WIDGET.equals(i.getAction())
                || Constants.ACTION_WIDGET_TAKE_PHOTO.equals(i.getAction())
                || Constants.ACTION_WIDGET_RECORD.equals(i.getAction())
                || ((Intent.ACTION_SEND.equals(i.getAction())
                || Intent.ACTION_SEND_MULTIPLE.equals(i.getAction())
                || Constants.INTENT_GOOGLE_NOW.equals(i.getAction()))
                && i.getType() != null)
                || i.getAction().contains(Constants.ACTION_NOTIFICATION_CLICK);
    }

    @Override
    public void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntents();
        //Log.i(TAG, "onNewIntent");
    }

    private void handleIntents()
    {
        //Log.i(TAG, "handling intents");
        Intent i = getIntent();
        if (i.getAction() == null)
            return;

        if (receivedIntent(i))
        {
            Note note = i.getParcelableExtra(Constants.INTENT_NOTE);
            if (note == null)
            {
                //note = new Note();
                //note = DbHelper.getInstance().getmNote(i.getIntExtra(Constants.INTENT_KEY, 0));
            }
            if (note == null) {
                note = new Note();
            }
            switchToDetail(note);
            return;
        }

        if (Constants.ACTION_SEND_AND_EXIT.equals(i.getAction()))
        {
            saveAndExit(i);
            return;
        }

        if (Intent.ACTION_VIEW.equals(i.getAction()))
        {
            switchToList();
            return;
        }
    }

    private void saveAndExit(Intent intent)
    {
        Note note = new Note();
        note.setTitle(intent.getStringExtra(Intent.EXTRA_SUBJECT));
        note.setContent(intent.getStringExtra(Intent.EXTRA_TEXT));
        dbHelper.updateNote(note, true);
        showToast("Note saved", Toast.LENGTH_SHORT);
        finish();
    }

    private Fragment checkFragmentInstance(int id, Object instanceClass)
    {
        Fragment result = null;
        Fragment fragment = getFragmentManagerInstance().findFragmentById(id);
        if (fragment != null && instanceClass.equals(fragment.getClass()))
        {
            result = fragment;
        }
        return result;
    }

    public void onBackPressed()
    {
        Fragment f = checkFragmentInstance(R.id.fragment_note_container, NotesDetailFragment.class);
        if (f != null)
        {
            ((NotesDetailFragment) f).goBack = true;
            //getFragmentManagerInstance().popBackStack();
            //((NotesDetailFragment) f).saveAndExit((NotesDetailFragment) f);
            return;
        }
        f = checkFragmentInstance(R.id.fragment_note_container, NoteListFragment.class);
        if (f != null)
        {
            super.onBackPressed();
            //getFragmentManagerInstance().popBackStack();
        }
        f = checkFragmentInstance(R.id.fragment_note_container, SketchFragment.class);
        if (f != null)
        {
            ((SketchFragment) f).save();

            getFragmentManagerInstance().popBackStack();
            return;
        }

        //getFragmentManagerInstance().popBackStack();
        super.onBackPressed();
    }

    public void shareNote(Note note) {
        String title = note.getTitle();
        String content = note.getContent();

        Intent shareIntent = new Intent();
        if (note.getAttachmentsList().size() == 0) {
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");

        } else if (note.getAttachmentsList().size() == 1) {
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType(note.getAttachmentsList().get(0).getMime_type());
            shareIntent.putExtra(Intent.EXTRA_STREAM, note.getAttachmentsList().get(0).getUri());

        } else if (note.getAttachmentsList().size() > 1) {
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            ArrayList<Uri> uris = new ArrayList<>();
            HashMap<String, Boolean> mimeTypes = new HashMap<>();
            for (Attachment attachment : note.getAttachmentsList()) {
                uris.add(attachment.getUri());
                mimeTypes.put(attachment.getMime_type(), true);
            }
            if (mimeTypes.size() > 1) {
                shareIntent.setType("*/*");

            } else {
                shareIntent.setType((String) mimeTypes.keySet().toArray()[0]);
            }

            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        }
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);

        startActivity(Intent.createChooser(shareIntent, "Share with"));
    }

    public void animateTransition(FragmentTransaction transaction, int direction)
    {
        if (direction == TRANSITION_HORIZONTAL)
        {
            transaction.setCustomAnimations(R.anim.fade_in_support, R.anim.fade_out_support,
                    R.anim.fade_in_support, R.anim.fade_out_support);
        }
        if (direction == TRANSITION_VERTICAL)
        {
            transaction.setCustomAnimations(R.anim.anim_in, R.anim.anim_out, R.anim.anim_in_pop,
                    R.anim.anim_out_pop);
        }
    }

    public Toolbar getToolbar() {
        return this.toolbar;
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_notes_detail, menu);
        return super.onCreateOptionsMenu(menu);
    } */

    public void showError(String title, String message)
    {
        SimpleDialog.build()
                .title(title)
                .msg(message)
                .show(this);
    }

    public void showToast(CharSequence text, int duration)
    {
        Toast.makeText(getApplicationContext(), text, duration).show();
    }
}
