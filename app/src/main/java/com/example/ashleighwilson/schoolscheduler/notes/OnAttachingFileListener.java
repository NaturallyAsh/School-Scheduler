package com.example.ashleighwilson.schoolscheduler.notes;

public interface OnAttachingFileListener {

    void onAttachingFileErrorOccurred(Attachment attachment);
    void onAttachingFileFinished(Attachment attachment);
}
