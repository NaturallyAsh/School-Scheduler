package com.example.ashleighwilson.schoolscheduler.models;

public class RecordingModel
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

    public RecordingModel(int id, String name, String filePath, long length, long time)
    {
        mId = id;
        mName = name;
        mFilePath = filePath;
        newLength = length;
        mTime = time;
    }

    public RecordingModel(int id, String name, String filePath, long length)
    {
        mId = id;
        mName = name;
        mFilePath = filePath;
        newLength = length;
    }

    public RecordingModel(String name, String filePath, long length)
    {
        mName = name;
        mFilePath = filePath;
        newLength = length;
    }

    public RecordingModel(int id, String name, String filePath, int length, long time)
    {
        mId = id;
        mName = name;
        mFilePath = filePath;
        mLength = length;
        mTime = time;
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
}
