package com.example.ashleighwilson.schoolscheduler.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.ashleighwilson.schoolscheduler.NotesActivity;
import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.models.Attachment;
import com.example.ashleighwilson.schoolscheduler.notes.Constants;
import com.example.ashleighwilson.schoolscheduler.notes.Note;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotesDetailFragment extends Fragment
{
    private static final int TAKE_PHOTO = 1;
    private static final int TAKE_VIDEO = 2;
    private static final int SET_PASSWORD = 3;
    private static final int SKETCH = 4;
    private static final int CATEGORY = 5;
    private static final int DETAIL = 6;
    private static final int FILES = 7;

    @BindView(R.id.notedetail_root)
    ViewGroup root;
    @BindView(R.id.notedetail_wrapper)
    ViewManager detailWrapperView;
    @BindView(R.id.notedetail_title_card)
    View titleCard;
    @BindView(R.id.notetitle_wrapper)
    View titleWrapper;
    @BindView(R.id.notetitle_tv)
    EditText title;
    @BindView(R.id.notedetail_content_wrapper)
    ScrollView scrollView;
    @BindView(R.id.notedetaillist_content)
    EditText detailContent;
    @BindView(R.id.notedetail_timestamps)
    View timestampView;
    @BindView(R.id.note_creation)
    TextView creationTV;
    @BindView(R.id.last_modification)
    TextView lastModTV;
    @BindView(R.id.note_reminder_layout)
    LinearLayout reminderLayout;
    @BindView(R.id.note_reminder_icon)
    ImageView reminderIcon;
    @BindView(R.id.note_reminder_datetime)
    TextView dateTime;
    @BindView(R.id.detail_attachments_above)
    ViewStub attachmentsAbove;
    @BindView(R.id.detail_attachments_below)
    ViewStub attachmentsBelow;
    @BindView(R.id.attachGridView)
    ExpandableHeightGridView mGridView;

    private NotesDetailFragment mNotesDetailFragment;
    private NotesActivity mNotesActivity;
    public boolean goBack = false;
    private boolean activityPausing;
    private Attachment sketchEdited;
    private Note note;
    private Note noteTmp;
    private Note noteOriginal;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mNotesDetailFragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_note_detail, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mNotesActivity = (NotesActivity) getActivity();

        mNotesActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        mNotesActivity.getToolbar().setNavigationOnClickListener(v -> navigateUp());

        if (mNotesActivity.sketchUri != null)
        {
            Attachment mAttachment = new Attachment(mNotesActivity.sketchUri, Constants.MIME_TYPE_SKETCH);
            addAttachment(mAttachment);
            mNotesActivity.sketchUri = null;
            if (sketchEdited != null)
            {
                noteTmp.getAttachmentsList().remove(sketchEdited);
                sketchEdited = null;
            }
        }

        setHasOptionsMenu(true);
        setRetainInstance(false);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        activityPausing = false;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        activityPausing = true;

        if (!goBack)
        {

        }
    }

    private void initViews()
    {

    }

    private void initViewAttachments()
    {
        attachmentsAbove.inflate();

        mGridView = (ExpandableHeightGridView) root.findViewById(R.id.gridView);
    }

    private void navigateUp()
    {
        //saveAndExit(this);
    }

    private void addAttachment(Attachment attachment)
    {
        noteTmp.addAttachment(attachment);
    }
}
