package com.example.ashleighwilson.schoolscheduler.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.ashleighwilson.schoolscheduler.CalenderFrag;
import com.example.ashleighwilson.schoolscheduler.SubjectsFrag;
import com.example.ashleighwilson.schoolscheduler.TasksFrag;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekViewEvent;

public class ViewPagerAdapter extends FragmentPagerAdapter
{
    CharSequence tabTitles[];
    int numOfTabs;
    private Context mContext;


    public ViewPagerAdapter(Context context, FragmentManager fm, CharSequence titles[],
                            int tabs)
    {
        super(fm);
        mContext = context;
        numOfTabs = tabs;
        tabTitles = titles;

    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                return new SubjectsFrag();
            case 1:
                return new TasksFrag();
            case 2:
                return new CalenderFrag();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return tabTitles[position];
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
