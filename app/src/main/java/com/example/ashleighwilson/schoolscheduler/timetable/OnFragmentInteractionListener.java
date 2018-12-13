package com.example.ashleighwilson.schoolscheduler.timetable;

import com.example.ashleighwilson.schoolscheduler.models.WeekViewEvent;

import java.util.List;

public interface OnFragmentInteractionListener
{
    //void getList(String key, List<WeekViewEvent> data);
    void refreshData(List<WeekViewEvent> data);
}
