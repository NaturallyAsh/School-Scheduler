package com.example.ashleighwilson.schoolscheduler.data;

import com.example.ashleighwilson.schoolscheduler.MySchedulerApp;
import com.example.ashleighwilson.schoolscheduler.notes.Attachment;
import com.example.ashleighwilson.schoolscheduler.notes.Note;

import java.util.List;

import butterknife.BindView;

public class NoteProcessorDelete extends NoteProcessor {

    private static final String TAG = NoteProcessorDelete.class.getSimpleName();

    private final boolean keepAttachments;

    public NoteProcessorDelete(List<Note> notes) {
        this(notes, false);
    }

    public NoteProcessorDelete(List<Note> notes, boolean keepAttachments) {
        super(notes);
        this.keepAttachments = keepAttachments;
    }

    @Override
    protected void processNote(Note note) {
        DbHelper dbHelper = DbHelper.getInstance();
        if (dbHelper.deleteNoteProcess(note) && !keepAttachments) {
            for (Attachment mAttachment : note.getAttachmentsList()) {
                Storage.deleteExternalStoragePrivateFile(MySchedulerApp.getInstance(), mAttachment.getUri()
                        .getLastPathSegment());
            }
        }
    }
}
