package com.example.ashleighwilson.schoolscheduler.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.ashleighwilson.schoolscheduler.LoginActivity;

import java.util.HashMap;

public class SessionManager
{
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context mContext;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SchoolScheduler";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_PASS = "password";
    public static final String KEY_EMAIL = "email";

    public SessionManager (Context context)
    {
        this.mContext = context;
        pref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String pass, String email)
    {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASS, pass);
        editor.commit();
    }

    public void checkLogin()
    {
        if (!this.isLoggedIn())
        {
            Intent intent = new Intent(mContext, LoginActivity.class);
            //Closing all the Activities
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //Add new Flag to start new Activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //Starting Login Activity
            mContext.startActivity(intent);
        }
    }

    //Get stored session data
    public HashMap<String, String> getUserDetails()
    {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_PASS, pref.getString(KEY_PASS, null));


        return user;
    }

    public void logoutUser()
    {
        //Clearing all data from SP
        editor.clear();
        editor.commit();

        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //Add new Flag to start new Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //Starting Login Activity
        mContext.startActivity(intent);
    }

    public boolean isLoggedIn()
    {
        //set to false when ready with User model
        return pref.getBoolean(IS_LOGIN, false);
    }
}
