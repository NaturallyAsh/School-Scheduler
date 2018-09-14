package com.example.ashleighwilson.schoolscheduler.timetable;

import android.content.Context;

import java.io.IOException;
import java.util.AbstractCollection;

public class CalendarDB
{
    static CalendarObjectList<? extends AbstractCollection<? extends CalendarObject>, ? extends CalendarObject> list0 = null;

    static CalendarObjectListInputStream in = null;
    static CalendarObjectListOutputStream out = null;
    static final String[] filenames = new String[] {"staticData"};

    public static void initDBLocal(Context context) throws IOException {
        CalendarDB.initListLocal(0, context);
    }

    public static CalendarObjectList<? extends AbstractCollection<? extends CalendarObject>, ? extends CalendarObject> retriveListLocal(int listID, Context context) throws IOException {
        in = new CalendarObjectListInputStream(filenames[listID], context);
        CalendarObjectList<? extends AbstractCollection<? extends CalendarObject>, ? extends CalendarObject> result = in.readList();
        in.close();

        return result;
    }

    public static void updateListLocal(int listID, CalendarObjectList<? extends AbstractCollection<? extends CalendarObject>, ? extends CalendarObject> obj, Context context) throws IOException {

        out = new CalendarObjectListOutputStream(filenames[listID], context);
        out.writeList(obj);
    }

    private static void initListLocal(int listID, Context context) throws IOException {
        CalendarObjectList<? extends AbstractCollection<? extends CalendarObject>, ? extends CalendarObject> list = null;

        switch (listID) {
            case 0: EventListHandler.initEventList(); list = EventListHandler.getEventList(); break;
        }

        out = new CalendarObjectListOutputStream(filenames[listID], context);
        out.writeList(list);
        out.close();
    }
}
