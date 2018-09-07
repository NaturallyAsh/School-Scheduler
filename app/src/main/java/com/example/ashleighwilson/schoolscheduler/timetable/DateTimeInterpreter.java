package com.example.ashleighwilson.schoolscheduler.timetable;

import java.util.Calendar;

public interface DateTimeInterpreter
{
    String interpretDate(Calendar date);
    String interpretTime(int hour);
}
