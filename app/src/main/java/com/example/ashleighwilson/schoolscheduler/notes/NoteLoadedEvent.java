package com.example.ashleighwilson.schoolscheduler.notes;

import java.util.ArrayList;
import java.util.List;

public class NoteLoadedEvent
{
    public ArrayList<Note> notes;

    public NoteLoadedEvent(ArrayList<Note> notes) {
        this.notes = notes;
    }
}
