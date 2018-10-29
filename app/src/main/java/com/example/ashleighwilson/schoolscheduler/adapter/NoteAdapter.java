package com.example.ashleighwilson.schoolscheduler.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ashleighwilson.schoolscheduler.NotesActivity;
import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.notes.Attachment;
import com.example.ashleighwilson.schoolscheduler.notes.Note;
import com.example.ashleighwilson.schoolscheduler.utils.BitmapHelper;
import com.example.ashleighwilson.schoolscheduler.utils.DateHelper;
import com.example.ashleighwilson.schoolscheduler.utils.SquareImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private static final String TAG = NoteAdapter.class.getSimpleName();

    private Context mContext;
    private List<Note> notes;
    private Note currentNote;
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
        this.dbHelper = DbHelper.getInstance();
        setOnNoteClickListener(listener);

        //setData(noteList);
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
        currentNote = notes.get(position);

        holder.title.setText(currentNote.getTitle());
        holder.content.setText(currentNote.getContent());
        holder.alarmIcon.setVisibility(currentNote.getAlarm() != null ? View.VISIBLE : View.INVISIBLE);
        //holder.date.setText(currentNote.getAlarm());
        String modText = DateHelper.dateFormatter(currentNote.getLastModification());
        holder.date.setText("Updated: " + "" + modText);
        holder.attachmentIcon.setVisibility(currentNote.getAttachmentsList().size() > 0
            ? View.VISIBLE : View.GONE);
        if (currentNote.getAttachmentsList().size() == 0) {
            holder.attachmentThumbnail.setVisibility(View.GONE);
        } else {
            Attachment mAttachment = currentNote.getAttachmentsList().get(0);
            Uri thumbnailUri = BitmapHelper.getThumbnailUri(mContext, mAttachment);
            Log.i(TAG, "thumbnail Uri: " + thumbnailUri);
            Glide.with(mContext)
                    .load(thumbnailUri)
                    .centerCrop()
                    .crossFade()
                    .into(holder.attachmentThumbnail);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        @BindView(R.id.note_item_root) public View root;
        @BindView(R.id.note_item_cardlayout) public View cardLayout;
        @BindView(R.id.note_item_title) public TextView title;
        @BindView(R.id.note_item_content) public TextView content;
        @BindView(R.id.note_item_date) public TextView date;
        @BindView(R.id.note_item_alarmIcon) public ImageView alarmIcon;
        @BindView(R.id.attachmentIcon) public ImageView attachmentIcon;
        @BindView(R.id.attachmentThumbnail) public SquareImageView attachmentThumbnail;

        public ViewHolder(final View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listener.OnNoteClicked(v, getAdapterPosition());
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
        return notes != null ? dbHelper.getNoteAt(position) : null;
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

    public void setData(List<Note> noteModel) {
        notes = noteModel;
        notifyDataSetChanged();
        notifyItemInserted(getItemCount());
        notifyItemRangeChanged(0, noteModel.size());
    }

    public void deleteNote() {
        dbHelper.deleteNoteProcess(currentNote);
    }

    public void dismissNote(int position)
    {
        //dbHelper.deleteNote(getItem(position).get_id());
        dbHelper.deleteNote(notes.get(position).getID());
        notes.remove(position);
        notifyItemRemoved(position);
        //notifyItemRangeChanged(position, getItemCount());
    }

    public void removeNote(int position) {
        notes.remove(position);
        notifyItemRemoved(position);
    }
}
