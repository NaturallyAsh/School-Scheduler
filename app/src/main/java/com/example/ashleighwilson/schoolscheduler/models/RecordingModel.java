package com.example.ashleighwilson.schoolscheduler.models;

import android.os.Parcel;
import android.os.Parcelable;

public class RecordingModel implements Parcelable
{
    private String mName;
    private String mFilePath;
    private int mId;
    private int mLength;
    private long newLength;
    private long mTime;

    public RecordingModel()
    {

    }

    public RecordingModel(Parcel in)
    {
        mName = in.readString();
        mFilePath = in.readString();
        mId = in.readInt();
        mLength = in.readInt();
        mTime = in.readLong();
    }

    public long getNewLength()
    {
        return newLength;
    }

    public void setNewLength(long length)
    {
        newLength = length;
    }

    public int getId()
    {
        return mId;
    }

    public void setId(int id)
    {
        mId = id;
    }

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        mName = name;
    }

    public String getFilePath()
    {
        return mFilePath;
    }

    public void setFilePath(String filePath)
    {
        mFilePath = filePath;
    }

    public int getLength()
    {
        return mLength;
    }

    public void setLength(int length)
    {
        mLength = length;
    }

    public long getTime()
    {
        return mTime;
    }

    public void setTime(long time)
    {
        mTime = time;
    }

    public static final Parcelable.Creator<RecordingModel> CREATOR = new
            Parcelable.Creator<RecordingModel>()
            {
                @Override
                public RecordingModel createFromParcel(Parcel in) {
                    return new RecordingModel(in);
                }

                @Override
                public RecordingModel[] newArray(int size) {
                    return new RecordingModel[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeInt(mId);
        dest.writeInt(mLength);
        dest.writeLong(mTime);
        dest.writeString(mFilePath);
        dest.writeString(mName);
    }
}
