package com.example.ashleighwilson.schoolscheduler.timetable;

import java.io.Serializable;
import java.util.AbstractCollection;

public interface CalendarObjectList<T extends AbstractCollection<? extends CalendarObject>,
    E extends CalendarObject> extends Serializable{
    T getList();
    void setList(T list);
    void addEvent(E obj);

}
