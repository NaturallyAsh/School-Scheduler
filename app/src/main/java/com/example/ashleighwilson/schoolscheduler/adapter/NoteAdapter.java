package com.example.ashleighwilson.schoolscheduler.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashleighwilson.schoolscheduler.NotesActivity;
import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.notes.Note;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private static final String TAG = NoteAdapter.class.getSimpleName();

    private Context mContext;
    private List<Note> notes;
    private Note note;
    private DbHelper dbHelper;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    NoteClickListener listener;

    public static final String EXTRA_TITLE = "TITLE";
    public static final String EXTRA_CONTENT = "CONTENT";
    public static final String EXTRA_DATE = "DATE";

    public NoteAdapter(Context context, List<Note> noteList)
    {
        this.mContext = context;
        this.notes = noteList;
        this.dbHelper = new DbHelper(mContext);
        setOnNoteClickListener(listener);
    }

    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        View itemView = LayoutInflater.from(mContext).inflate(R.layout.fragment_note_list_item,
                parent, false);

        //ButterKnife.bind(this, itemView);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final NoteAdapter.ViewHolder holder, final int position)
    {
        final Note currentNote = notes.get(position);

        holder.title.setText(currentNote.getmTitle());
        //holder.title.setText(currentNote.getTitle());
        holder.content.setText(currentNote.getmContent());
        //holder.content.setText(currentNote.getContent());
        holder.alarmIcon.setVisibility(currentNote.getmAlarm() != null ? View.VISIBLE : View.INVISIBLE);
        holder.date.setText(currentNote.getmAlarm());
        //holder.noteData = getItem(position);

    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        //public Note noteData;

        @BindView(R.id.note_item_root) public View root;
        @BindView(R.id.note_item_cardlayout) public View cardLayout;
        @BindView(R.id.note_item_title) public TextView title;
        @BindView(R.id.note_item_content) public TextView content;
        @BindView(R.id.note_item_date) public TextView date;
        @BindView(R.id.note_item_alarmIcon) public ImageView alarmIcon;

        public ViewHolder(final View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listener.OnNoteClicked(v, getAdapterPosition());
                    //Log.i(TAG, "adapterposition: " + getAdapterPosition() + " view: " + v);
                }
            });
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

    public Note getItem(int position) {
        return notes != null ? notes.get(position) : null;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public interface NoteClickListener
    {
        void OnNoteClicked(View view, int position);
    }

    public void setOnNoteClickListener(NoteClickListener clickListener) {
        this.listener = clickListener;
    }

    public void dismissNote(int position)
    {
        dbHelper.deleteNote(notes.get(position).getmId());
        notes.remove(position);
        notifyItemRemoved(position);
    }

}
