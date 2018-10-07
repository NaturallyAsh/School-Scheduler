package com.example.ashleighwilson.schoolscheduler.powermenu;

import android.widget.ListView;

import java.util.List;

public interface IMenuItem<T>
{
    void addItem(T item);
    void addItem(int position, T item);
    void addItemList(List<T> itemList);

    void setListView(ListView listView);
    ListView getListView();

    void setSelectedPosition(int position);
    int getSelectedPosition();

    void removeItem(T item);
    void removeItem(int position);

    void clearItems();

    List<T> getItemList();

    int getContentViewHeight();
}
