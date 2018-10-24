package com.example.ashleighwilson.schoolscheduler.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.example.ashleighwilson.schoolscheduler.NotesActivity;
import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.RecordActivity;
import com.example.ashleighwilson.schoolscheduler.SketchFragment;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.data.NoteEvent;
import com.example.ashleighwilson.schoolscheduler.data.NotificationController;
import com.example.ashleighwilson.schoolscheduler.data.SaveNoteTask;
import com.example.ashleighwilson.schoolscheduler.data.Storage;
import com.example.ashleighwilson.schoolscheduler.dialog.RecurrenceDialog;
import com.example.ashleighwilson.schoolscheduler.notes.Attachment;
import com.example.ashleighwilson.schoolscheduler.notes.Constants;
import com.example.ashleighwilson.schoolscheduler.notes.Note;
import com.example.ashleighwilson.schoolscheduler.notes.NoteLoadedEvent;
import com.example.ashleighwilson.schoolscheduler.notes.OnNoteSaved;
import com.example.ashleighwilson.schoolscheduler.notes.OnReminderPickedListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotesDetailFragment extends Fragment implements OnNoteSaved,
        OnReminderPickedListener, View.OnTouchListener, View.OnClickListener {

    private static final String TAG = NotesDetailFragment.class.getSimpleName();

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
    //@BindView(R.id.attachGridView)
    //ExpandableHeightGridView mGridView;
    SelectedDate mSelectedDate;
    int mHour, mMinute, mReminderYear, mReminderMonth, mReminderDay;
    String mRecurrenceOption, mRecurrenceRule;
    private OnReminderPickedListener mOnReminderPickedListener;
    private NotesDetailFragment mNotesDetailFragment;
    private NotesActivity mNotesActivity;
    public boolean goBack = false;
    private boolean activityPausing;
    private Attachment sketchEdited;
    private Note note;
    private Note noteTmp;
    private Note noteOriginal;
    private MaterialDialog attachmentDialog;
    private RecordActivity recordActivity;
    private DbHelper dbHelper;
    public Uri attachmentUri;
    RecurrenceDialog.Callback mCallback = new RecurrenceDialog.Callback() {
        @Override
        public void onCancelled() {
            if (mCallback != null)
                mCallback.onCancelled();

        }

        @Override
        public void onDateTimeRecurrenceSet(SelectedDate selectedDate, int hourOfDay, int minute, SublimeRecurrencePicker.RecurrenceOption recurrenceOption, String recurrenceRule) {

            mSelectedDate = selectedDate;
            mReminderYear = mSelectedDate.getFirstDate().get(Calendar.YEAR);
            mReminderMonth = mSelectedDate.getFirstDate().get(Calendar.MONTH);
            mReminderDay = mSelectedDate.getFirstDate().get(Calendar.DAY_OF_MONTH);

            Log.i(TAG, "year: " + mReminderYear + " month: " + mReminderMonth + " day: " + mReminderDay);
            mHour = hourOfDay;
            mMinute = minute;
            mRecurrenceOption = recurrenceOption != null ? recurrenceOption.name() : "n/a";
            mRecurrenceRule = recurrenceRule != null ? recurrenceRule : "n/a";

            Log.i(TAG, "selected date: " + selectedDate + " hour: " + hourOfDay + " minute: " +
                minute + " recurrence rule: " + recurrenceRule);

            Calendar calendar = Calendar.getInstance();
            calendar.set(mReminderYear, mReminderMonth, mReminderDay, mHour, mMinute, 0);

            dateTime.setText(DateHelper.dateFormatter(calendar.getTimeInMillis()));
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mNotesDetailFragment = this;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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
        dbHelper = DbHelper.getInstance();

        mNotesActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        mNotesActivity.getToolbar().setNavigationOnClickListener(v -> navigateUp());

        if (savedInstanceState != null) {
            noteTmp = savedInstanceState.getParcelable("noteTmp");
            note = savedInstanceState.getParcelable("note");
            noteOriginal = savedInstanceState.getParcelable("noteOriginal");
            attachmentUri = savedInstanceState.getParcelable("attachmentUri");
        }

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

        reminderLayout.setOnClickListener(this);
        init();

        setHasOptionsMenu(true);
        setRetainInstance(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if (noteTmp != null) {
            noteTmp.setTitle(getNoteTitle());
            noteTmp.setContent(getNoteContent());
            outState.putParcelable("noteTmp", noteTmp);
            outState.putParcelable("note", note);
            outState.putParcelable("noteOriginal", noteOriginal);
            outState.putParcelable("attachmentUri", attachmentUri);
        }
        super.onSaveInstanceState(outState);
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
            saveNote(this);
        }
    }

    private void init()
    {
        handleIntents();

        if (noteOriginal == null) {
            noteOriginal = getArguments().getParcelable(Constants.INTENT_NOTE);
        }

        if (note == null) {
            note = new Note(noteOriginal);
        }

        if (noteTmp == null) {
            noteTmp = new Note(note);
            Log.i(TAG, "noteTmp: " + noteTmp);
        }

        initViews();
    }

    private void handleIntents()
    {
        Intent intent = mNotesActivity.getIntent();

        if (IntentChecker.checkAction(intent, Constants.ACTION_MERGE)) {
            noteOriginal = new Note();
            note = new Note(noteOriginal);
            noteTmp = getArguments().getParcelable(Constants.INTENT_NOTE);
            if (intent.getStringArrayListExtra("merged_notes") != null) {

            }
        }

        if (IntentChecker.checkAction(intent, Constants.ACTION_FAB_TAKE_PHOTO)) {
            takePhoto();
        }

        if(IntentChecker.checkAction(intent, Intent.ACTION_SEND, Intent.ACTION_SEND_MULTIPLE, Constants.INTENT_GOOGLE_NOW)
                && intent.getType() != null) {

            if (noteTmp == null)
                noteTmp = new Note();

            String title = intent.getStringExtra(Intent.EXTRA_SUBJECT);
            if (title != null)
                noteTmp.setTitle(title);

            String content = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (content != null)
                noteTmp.setContent(content);
        }

        intent.setAction(null);
    }

    private void initViews()
    {
        root.setOnTouchListener(this);


        initViewTitle();
        initViewContent();
        //initViewReminder();
        initViewFooter();
    }

    private void initViewFooter() {
        String creation = DateHelper.dateFormatter(noteTmp.getCreation());
        creationTV.append(creation.length() > 0 ? getString(R.string.creation) + " " +
            creation : "");
        if (creationTV.getText().length() == 0)
            creationTV.setVisibility(View.GONE);

        String lastMod = DateHelper.dateFormatter(noteTmp.getLastModification());
        lastModTV.append(lastMod.length() > 0 ? getString(R.string.last_update) + " " +
            lastMod : "");
        if (lastModTV.getText().length() == 0)
            lastModTV.setVisibility(View.GONE);
    }

    private void initViewTitle() {
        title.setText(noteTmp.getTitle());

    }

    @Override
    public void onClick(View v) {
        RecurrenceDialog recurrenceDialog = new RecurrenceDialog();
        recurrenceDialog.setCallback(mCallback);

        Pair<Boolean, SublimeOptions> optionsPair = getOptions();

        Bundle bundle = new Bundle();
        bundle.putParcelable("SUBLIME_OPTIONS", optionsPair.second);
        recurrenceDialog.setArguments(bundle);

        recurrenceDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        recurrenceDialog.show(mNotesActivity.getSupportFragmentManager(),"SUBLIME_PICKER");

        /*String reminderString = initReminder(noteTmp);
        if (reminderString != null)
            dateTime.setText(reminderString);

        Log.i(TAG, "noteTmp: " + noteTmp); */
    }

    private void initViewReminder()
    {
        reminderLayout.setOnClickListener(v -> {
            RecurrenceDialog recurrenceDialog = new RecurrenceDialog();
            recurrenceDialog.setCallback(mCallback);

            Pair<Boolean, SublimeOptions> optionsPair = getOptions();

            Bundle bundle = new Bundle();
            bundle.putParcelable("SUBLIME_OPTIONS", optionsPair.second);
            recurrenceDialog.setArguments(bundle);

            recurrenceDialog.show(mNotesActivity.getSupportFragmentManager(),"SUBLIME_PICKER");
        });
    }

    private String initReminder(Note note)
    {
        if (noteTmp.getAlarm() == null)
            return "";

        long reminder = Long.parseLong(note.getAlarm());
        String rule = note.getRecurrenceRule();
        if (!TextUtils.isEmpty(rule)) {
            return DateHelper.getNoteRecurrentReminderText(reminder, rule);
        } else {
            return DateHelper.getNoteReminderText("Reminder for: ", reminder);
        }

    }

    private void initViewContent()
    {
        detailContent.setText(noteTmp.getContent());
    }

    private void navigateUp()
    {
        saveAndExit(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_notes_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_save:

                break;
            case R.id.menu_attachment:
                showAttachmentsPopup();
                break;
            case R.id.menu_share:
                break;
            case R.id.menu_checklist_on:
                break;
            case R.id.menu_checklist_off:
                break;
            case R.id.menu_trash:
                break;
            case R.id.menu_untrash:
                break;
            default:
                Log.i(TAG, "Invalid menu option selected");
        }

        return super.onOptionsItemSelected(item);
    }

    private void addAttachment(Attachment attachment)
    {
        noteTmp.addAttachment(attachment);
    }

    private void showAttachmentsPopup()
    {
        LayoutInflater inflater = mNotesActivity.getLayoutInflater();
        final View layout = inflater.inflate(R.layout.attachment_dialog, null);

        attachmentDialog = new MaterialDialog.Builder(mNotesActivity)
                .autoDismiss(true)
                .customView(layout, false)
                .build();
        attachmentDialog.show();

        android.widget.TextView cameraSelection = (android.widget.TextView) layout.findViewById(R.id.camera);
        cameraSelection.setOnClickListener(new AttachmentOnClickListener());

        android.widget.TextView recordingSelection = (android.widget.TextView) layout.findViewById(R.id.recording);
        recordingSelection.setOnClickListener(new AttachmentOnClickListener());

        android.widget.TextView videoSelection = (android.widget.TextView) layout.findViewById(R.id.video);
        videoSelection.setOnClickListener(new AttachmentOnClickListener());

        android.widget.TextView filesSelection = (android.widget.TextView) layout.findViewById(R.id.files);
        filesSelection.setOnClickListener(new AttachmentOnClickListener());

        android.widget.TextView sketchSelection = (android.widget.TextView) layout.findViewById(R.id.sketch);
        sketchSelection.setOnClickListener(new AttachmentOnClickListener());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        Attachment attachment;
        if (resultCode == Activity.RESULT_OK)
        {
            switch (requestCode)
            {
                case TAKE_PHOTO:
                    attachment = new Attachment(attachmentUri, Constants.MIME_TYPE_IMAGE);
                    addAttachment(attachment);
                    break;
                case TAKE_VIDEO:
                    attachment = new Attachment(attachmentUri, Constants.MIME_TYPE_VIDEO);
                    addAttachment(attachment);
                    break;
                case FILES:
                    onActivityResultManageReceivedFiles(intent);
                    break;
                case SKETCH:
                    attachment = new Attachment(attachmentUri, Constants.MIME_TYPE_SKETCH);
                    addAttachment(attachment);
                    break;
                default:
                    Log.i(TAG, "Wrong element chosen");
            }
        }
    }

    private void onActivityResultManageReceivedFiles(Intent intent)
    {

    }

    public void saveAndExit(OnNoteSaved noteSaved)
    {
        //Log.i(TAG, "saveAndExit");
       /*if (isAdded()) {
           mNotesActivity.showToast("Note Updated", Toast.LENGTH_SHORT);
           goBack = true;
           //if (note != null)
             //  NotificationController.scheduleReminder(mNotesActivity, note);
           saveNote(noteSaved);
       } */
        mNotesActivity.showToast("Note Updated", Toast.LENGTH_SHORT);
        goBack = true;
        if (note != null)
            NotificationController.scheduleReminder(mNotesActivity, note);
        saveNote(noteSaved);
    }

    public void saveNote(OnNoteSaved noteSaved)
    {
        noteTmp.setTitle(getNoteTitle());
        noteTmp.setContent(getNoteContent());

        if (goBack && TextUtils.isEmpty(noteTmp.getTitle()) && TextUtils.isEmpty(noteTmp.getContent())
                && noteTmp.getAttachmentsList().size() ==0)
        {
            Log.i(TAG, "Empty note not saved");
            goHome();
            return;
        }
        if (saveNotNeeded()) {
            //mNotesActivity.showToast("Save Not Needed", Toast.LENGTH_SHORT);
            if (goBack) {
                goHome();
            }
            return;
        }

        noteTmp.setAttachmentsListOld(note.getAttachmentsList());

        new SaveNoteTask(noteSaved, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                noteTmp);
        Log.i(TAG, "saveNote");
        //goHome();

    }

    private boolean saveNotNeeded() {
        return !noteTmp.isChanged(note);
    }

    private String getNoteTitle()
    {
        if (title != null && !TextUtils.isEmpty(title.getText())) {
            return title.getText().toString();
        } else {
            return "";
        }
    }

    private String getNoteContent()
    {
        if (detailContent != null && !TextUtils.isEmpty(detailContent.getText())) {
            return detailContent.getText().toString();
        } else {
            return "";
        }
    }

    @Override
    public void onNoteSaved(Note noteSaved) {
        //EventBus.getDefault().postSticky(new NoteUpdatedEvent());
        Log.i(TAG, "onNoteSaved: note posted");

        EventBus.getDefault().postSticky(new NoteEvent(noteSaved));
        note = new Note(noteSaved);
        if (goBack)
            goHome();
    }

    public boolean goHome()
    {
        if (mNotesActivity != null && mNotesActivity.getSupportFragmentManager() != null)
        {
            mNotesActivity.getSupportFragmentManager().popBackStack();
        }
        return true;
    }

    @Override
    public void onReminderPicked(long reminder) {
        noteTmp.setAlarm(reminder);
        dateTime.setText(DateHelper.dateFormatter(reminder));
    }

    @Override
    public void onRecurrenceReminderPicked(String recurrenceRule) {
        noteTmp.setRecurrenceRule(recurrenceRule);
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    public class AttachmentOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.camera:
                    takePhoto();
                    break;
                case R.id.recording:
                    startActivity(new Intent(getContext(), RecordActivity.class));
                    break;
                case R.id.video:
                    takeVideo();
                    break;
                case R.id.files:
                    break;
                case R.id.sketch:
                    takeSketch(null);
                    break;
            }
        }
    }

    private void takeSketch(Attachment attachment)
    {
        File f = Storage.createNewAttachmentFile(mNotesActivity, Constants.MIME_TYPE_SKETCH_EXT);
        if (f == null)
        {
            Log.i(TAG, "Error");
            return;
        }
        attachmentUri = Uri.fromFile(f);

        FragmentTransaction transaction = mNotesActivity.getSupportFragmentManager().beginTransaction();
        mNotesActivity.animateTransition(transaction, mNotesActivity.TRANSITION_HORIZONTAL);
        SketchFragment sketchFragment = new SketchFragment();
        Bundle b = new Bundle();
        b.putParcelable(MediaStore.EXTRA_OUTPUT, attachmentUri);
        if (attachment != null)
            b.putParcelable("base", attachment.getUri());
        sketchFragment.setArguments(b);
        transaction.replace(R.id.fragment_note_container, sketchFragment, mNotesActivity.FRAGMENT_SKETCH_TAG)
                .addToBackStack(mNotesActivity.FRAGMENT_NOTE_DETAIL_TAG).commit();
    }

    private void takePhoto()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (!IntentChecker.isAvailable(mNotesActivity, intent, new String[] {PackageManager.FEATURE_CAMERA}))
        {
            mNotesActivity.showError("Warning!", "\"Feature not available on device!\"" );
            return;
        }

        File f = Storage.createNewAttachmentFile(mNotesActivity, Constants.MIME_TYPE_IMAGE_EXT);
        if (f == null) {
            mNotesActivity.showError("Error", "File invalid!");
            return;
        }

        attachmentUri = Uri.fromFile(f);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, attachmentUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private void takeVideo()
    {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (!IntentChecker.isAvailable(mNotesActivity, videoIntent, new String[] {PackageManager.FEATURE_CAMERA})) {
            mNotesActivity.showError("Warning!", "\"Feature not available on device!\"" );
            return;
        }

        File f = Storage.createNewAttachmentFile(mNotesActivity, Constants.MIME_TYPE_VIDEO_EXT);
        if (f == null)
        {
            mNotesActivity.showError("Error", "File invalid!");
            return;
        }

        attachmentUri = Uri.fromFile(f);
        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, attachmentUri);
        startActivityForResult(videoIntent, TAKE_VIDEO);
    }

    public Pair<Boolean, SublimeOptions> getOptions()
    {
        SublimeOptions options = new SublimeOptions();
        int displayOptions = 0;

        displayOptions = SublimeOptions.ACTIVATE_DATE_PICKER + SublimeOptions.ACTIVATE_TIME_PICKER +
                SublimeOptions.ACTIVATE_RECURRENCE_PICKER;

        options.setDisplayOptions(displayOptions);

        return new Pair<>(displayOptions != 0 ? Boolean.TRUE : Boolean.FALSE, options);

    }

    private NotesActivity getNotesActivity()
    {
        return (NotesActivity) getActivity();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(NoteLoadedEvent noteLoadedEvent) {

    }
}
