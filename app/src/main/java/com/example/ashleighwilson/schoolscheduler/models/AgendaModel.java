package com.example.ashleighwilson.schoolscheduler.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.example.ashleighwilson.schoolscheduler.MySchedulerApp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AgendaModel implements Parcelable
{
    private static final String TAG = AgendaModel.class.getSimpleName();

    private int mId;
    private String mClassName;
    private String mAgendaTitle;
    private int mColor;
    private String mDueDate;
    private boolean mBefore;
    private long mInterval;
    private long dateTimeInterval;
    private String mTimeToNotify;
    private long mAddReminder;
    private int mRepeatType;
    private boolean[] mDayOfWeek;
    private long dateTime;


    public AgendaModel(int id, String name, String title, String dueDate, int color, String timeToNotify,
                       long reminder, int repeatType, long dateTime)
    {
        this.mId = id;
        this.mClassName = name;
        this.mAgendaTitle = title;
        this.mColor = color;
        this.mDueDate = dueDate;
        this.mTimeToNotify = timeToNotify;
        this.mAddReminder = reminder;
        this.mRepeatType = repeatType;
        this.dateTime = dateTime;
    }

    public AgendaModel() {

    }

    public AgendaModel(Parcel in) {
        mId = in.readInt();
        mClassName = in.readString();
        mAgendaTitle = in.readString();
        mColor = in.readInt();
        mDueDate = in.readString();
        mTimeToNotify = in.readString();
        mAddReminder = in.readLong();
        mRepeatType = in.readInt();
        dateTime = in.readLong();
        if (mDayOfWeek == null) {
            mDayOfWeek = new boolean[7];
        }
        in.readBooleanArray(mDayOfWeek);
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

    public String getTimeToNotify() {
        return mTimeToNotify;
    }

    public void setTimeToNotify(String timeToNotify) {
        this.mTimeToNotify = timeToNotify;
    }

    public long getmAddReminder() {
        return mAddReminder;
    }

    public void setmAddReminder(long reminder) {
        this.mAddReminder = reminder;
    }

    public int getmRepeatType()
    {
        return mRepeatType;
    }

    public void setmRepeatType(int repeatType)
    {
        this.mRepeatType = repeatType;
    }

    public boolean[] getmDayOfWeek() {
        return mDayOfWeek;
    }

    public void setmDayOfWeekInt(boolean[] dayOfWeekInt) {
        this.mDayOfWeek = dayOfWeekInt;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
        setDateTimeInterval(dateTime);
    }

    public long getmInterval() {
        return mInterval;
    }

    public long getDateTimeInterval() {
        return dateTimeInterval;
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

    public void setDateTimeInterval(long calendar) {
        Calendar futureDay = Calendar.getInstance();
        futureDay.setTimeInMillis(calendar);
        Calendar today = Calendar.getInstance();
        long diff;
        if (futureDay.before(today)) {
            mBefore = true;
            diff = today.getTimeInMillis() - futureDay.getTimeInMillis();
        } else {
            mBefore = false;
            diff = futureDay.getTimeInMillis() - today.getTimeInMillis();
        }
        dateTimeInterval = diff / (24 * 60 * 60 * 1000);
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
        dest.writeString(mTimeToNotify);
        dest.writeLong(mAddReminder);
        dest.writeInt(mRepeatType);
        dest.writeLong(dateTime);
        if (mDayOfWeek == null) {
            mDayOfWeek = new boolean[7];
        }
        dest.writeBooleanArray(mDayOfWeek);
    }
}
