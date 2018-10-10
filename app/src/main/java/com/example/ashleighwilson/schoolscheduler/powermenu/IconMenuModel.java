package com.example.ashleighwilson.schoolscheduler.powermenu;

import android.graphics.drawable.Drawable;

public class IconMenuModel
{
    private Drawable icon;
    private String title;

    public IconMenuModel(String title, Drawable icon)
    {
        this.title = title;
        this.icon = icon;
    }

    public String getIconTitle()
    {
        return title;
    }

    public void setIconTitle(String title)
    {
        this.title = title;
    }

    public Drawable getmIcon()
    {
        return icon;
    }

    public void setmIcon(Drawable icon)
    {
        this.icon = icon;
    }
}
