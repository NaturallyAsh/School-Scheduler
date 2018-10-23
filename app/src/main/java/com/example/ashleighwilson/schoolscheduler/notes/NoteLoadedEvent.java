package com.example.ashleighwilson.schoolscheduler.notes;

import java.util.ArrayList;

public class NoteLoadedEvent
{
    public ArrayList<Note> notes;

    public NoteLoadedEvent(ArrayList<Note> notes) {
        this.notes = notes;
    }
}
