package com.example.ashleighwilson.schoolscheduler.utils;

public class RecordUtil
{
    private RecordUtil()
    {

    }

    public static String formatSeconds(int seconds)
    {
        return getTwoDecimalsValue(seconds / 3600) + ":"
                + getTwoDecimalsValue(seconds / 60) + ":"
                + getTwoDecimalsValue(seconds % 60);
    }

    private static String getTwoDecimalsValue(int value)
    {
        if (value >= 0 && value <= 9)
        {
            return "0" + value;
        }
        else
        {
            return value + "";
        }
    }
}
