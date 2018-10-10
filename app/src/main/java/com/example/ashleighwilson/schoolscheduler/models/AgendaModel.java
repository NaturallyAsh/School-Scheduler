package com.example.ashleighwilson.schoolscheduler.models;

import android.content.Context;

import com.example.ashleighwilson.schoolscheduler.MySchedulerApp;
import com.example.ashleighwilson.schoolscheduler.data.NotificationController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AgendaModel
{
    private static final String TAG = AgendaModel.class.getSimpleName();

    private Context mContext = MySchedulerApp.getInstance();
    private int mId;
    private String mClassName;
    private String mAgendaTitle;
    private int mColor;
    private String mDueDate;
    private boolean mBefore;
    private long mInterval;
    private boolean mNotification;
    private ArrayList<AgendaModel> allList = new ArrayList<>();
    private NotificationController controller = new NotificationController(mContext);

    public AgendaModel(int id, String name, String title, String date, int color)
    {
        this.mId = id;
        this.mClassName = name;
        this.mAgendaTitle = title;
        this.mColor = color;
        this.mDueDate = date;
    }

    public AgendaModel() {

    }

    public int getmId()
    {
        return mId;
    }

    public void setmId(int id)
    {
        this.mId = id;
    }

    public String getClassName()
    {
        return mClassName;
    }

    public void setClassName(String name)
    {
        this.mClassName = name;
    }

    public String getAgendaTitle()
    {
        return mAgendaTitle;
    }

    public void setAgendaTitle(String title)
    {
        this.mAgendaTitle = title;
    }

    public int getmColor()
    {
        return mColor;
    }

    public void setmColor(int color)
    {
        this.mColor = color;
    }

    public String getDueDate()
    {
        return mDueDate;
    }

    public void setDueDate(String date)
    {
        this.mDueDate = date;
        setmInterval();
    }

    public boolean ismBefore() {
        return mBefore;
    }

    public boolean ismNotification()
    {
        return mNotification;
    }

    public void setmNotification(boolean notification)
    {
        this.mNotification = notification;
        //controller.scheduleNotification();
    }

    public long getmInterval() {
        return mInterval;
    }

    public void setmInterval()
    {
        String strThatDay = mDueDate;
        //Log.i(TAG, "date: " + mDueDate);

        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd, yyyy");
        Date d = null;
        try{
            d = formatter.parse(strThatDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar thatDay = Calendar.getInstance();
        thatDay.setTime(d);
        Calendar today = Calendar.getInstance();
        long diff;
        if (thatDay.before(today))
        {
            mBefore = true;
            diff = today.getTimeInMillis() - thatDay.getTimeInMillis();
        } else {
            mBefore = false;
            diff = thatDay.getTimeInMillis() - today.getTimeInMillis();
        }
        long days = diff / (24 * 60 * 60 * 1000);
        mInterval = days;
    }

    public ArrayList<AgendaModel> getAllList()
    {
        return allList;
    }

    public void setAllList(ArrayList<AgendaModel> list)
    {
        this.allList = list;
    }

    public void addAgendaList(AgendaModel model)
    {
        this.allList.add(model);
    }

    public AgendaModel getListById(int id)
    {
        ArrayList<AgendaModel> list = getAllList();
        if (list != null)
        {
            for (int i = 0; i < list.size(); i++)
            {
                if (list.get(i).getmId() == id)
                {
                    return list.get(i);
                }
            }
        }
        return null;
    }
}
