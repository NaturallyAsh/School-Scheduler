package com.example.ashleighwilson.schoolscheduler.utils;

import java.util.Calendar;

public interface DateTimeInterpreter
{
    String interpretDate(Calendar date);
    String interpretTime(int hour);
}
