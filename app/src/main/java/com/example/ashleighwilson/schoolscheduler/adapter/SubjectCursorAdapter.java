package com.example.ashleighwilson.schoolscheduler.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;

public class SubjectCursorAdapter extends CursorAdapter
{
    public SubjectCursorAdapter(Context context, Cursor c)
    {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        return LayoutInflater.from(context).inflate(R.layout.subjects_item_list, parent,
                false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TextView titleView = view.findViewById(R.id.subject_subject);
        TextView teacherView = view.findViewById(R.id.subject_teacher_text);

        int titleColumnIndex = cursor.getColumnIndex(DbHelper.SchoolEntry.COLUMN_TITLE);
        int teacherColumnIndex = cursor.getColumnIndex(DbHelper.SchoolEntry.COLUMN_TEACHER);

        String title = cursor.getString(titleColumnIndex);
        String teacher = cursor.getString(teacherColumnIndex);

        titleView.setText(title);
        teacherView.setText(teacher);

    }
}
