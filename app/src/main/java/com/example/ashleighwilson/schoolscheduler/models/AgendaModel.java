package com.example.ashleighwilson.schoolscheduler.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.ashleighwilson.schoolscheduler.MySchedulerApp;
import com.example.ashleighwilson.schoolscheduler.data.NotificationController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AgendaModel implements Parcelable
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

    public AgendaModel(Parcel in) {
        mId = in.readInt();
        mClassName = in.readString();
        mAgendaTitle = in.readString();
        mColor = in.readInt();
        mDueDate = in.readString();
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
        if (date != null)
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
    }

    public long getmInterval() {
        return mInterval;
    }

    public void setmInterval()
    {
        String strThatDay = mDueDate;

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

    public static final Parcelable.Creator<AgendaModel> CREATOR = new
            Parcelable.Creator<AgendaModel>() {
                @Override
                public AgendaModel createFromParcel(Parcel in) {
                    return new AgendaModel(in);
                }
                @Override
                public AgendaModel[] newArray(int size) {
                    return new AgendaModel[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeInt(mId);
        dest.writeString(mClassName);
        dest.writeString(mAgendaTitle);
        dest.writeInt(mColor);
        dest.writeString(mDueDate);
    }
}
