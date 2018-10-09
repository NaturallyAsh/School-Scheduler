package com.example.ashleighwilson.schoolscheduler.powermenu;

public class PowerMenuItem
{
    protected String title;
    protected int icon;
    protected boolean isSelected;
    protected Object tag;
    private int mId;

    public PowerMenuItem(String title) {
        this.title = title;
    }

    public PowerMenuItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public PowerMenuItem(String title, Object tag) {
        this.title = title;
        this.tag = tag;
    }

    public PowerMenuItem(String title, boolean isSelected) {
        this.title = title;
        this.isSelected = isSelected;
    }

    public PowerMenuItem(String title, int icon, Object tag) {
        this.title = title;
        this.icon = icon;
        this.tag = tag;
    }

    public PowerMenuItem(String title, int icon, boolean isSelected) {
        this.title = title;
        this.icon = icon;
        this.isSelected = isSelected;
    }

    public PowerMenuItem(String title, boolean isSelected, Object tag) {
        this.title = title;
        this.isSelected = isSelected;
        this.tag = tag;
    }

    public PowerMenuItem(String title, int icon, boolean isSelected, Object tag) {
        this.title = title;
        this.icon = icon;
        this.isSelected = isSelected;
        this.tag = tag;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getmId()
    {
        return mId;
    }

    public void setmId(int id)
    {
        this.mId = id;
    }
}
