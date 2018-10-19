package com.example.ashleighwilson.schoolscheduler.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.notes.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    private Context mContext;
    private List<Note> notes;
    private DbHelper dbHelper;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();

    public NoteAdapter(Context context, List<Note> noteList)
    {
        this.mContext = context;
        this.notes = noteList;
        this.dbHelper = new DbHelper(mContext);
    }

    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.fragment_note_list_item,
                parent, false));
    }

    @Override
    public void onBindViewHolder(final NoteAdapter.ViewHolder holder, final int position)
    {

    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        public ViewHolder(final View itemView)
        {
            super(itemView);
        }
    }

    @Override
    public int getItemCount()
    {
        return notes.size();
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

}
