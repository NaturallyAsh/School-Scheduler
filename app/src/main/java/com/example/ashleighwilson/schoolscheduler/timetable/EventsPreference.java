package com.example.ashleighwilson.schoolscheduler.timetable;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ashleighwilson.schoolscheduler.models.WeekViewEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class EventsPreference
{
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    Context mContext;

    private static final String PREF_NAME = "Calendar_Pref";
    private static final String KEY = "key";
    private static final String JSON_KEY = "json_key";
    private static final String JSON_KEY_KEY = "key_key";

    public EventsPreference(Context context)
    {
        this.mContext = context;
        pref = mContext.getSharedPreferences(PREF_NAME, 0);
        editor = pref.edit();
    }

    public void setList(String key, List<WeekViewEvent> list)
    {
        if (pref != null)
        {
            Gson gson = new Gson();
            String json = gson.toJson(list);
            set(key, json);
        }
    }

    public static void set(String key, String value)
    {
        if (pref != null)
        {
            editor.putString(KEY, value);
            editor.apply();
        }
    }

    public static List<WeekViewEvent> getHashEvent()
    {
        if (pref != null)
        {
            String json = pref.getString(KEY, "");
            Gson gson = new Gson();

            //WeekViewEvent obj = gson.fromJson(json, WeekViewEvent.class);

            Type type = new TypeToken<List<WeekViewEvent>>(){}.getType();
            List<WeekViewEvent> obj = gson.fromJson(json, type);

            return obj;
        }
        return null;
    }
}
