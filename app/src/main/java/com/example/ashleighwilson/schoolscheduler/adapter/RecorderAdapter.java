package com.example.ashleighwilson.schoolscheduler.adapter;

import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.dialog.PlaybackFragment;
import com.example.ashleighwilson.schoolscheduler.models.RecordingModel;
import com.example.ashleighwilson.schoolscheduler.utils.OnDatabaseChangedListener;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class RecorderAdapter extends RecyclerView.Adapter<RecorderAdapter.ViewHolder>
    implements OnDatabaseChangedListener
{

    Context mContext;
    RecordingModel model;
    private DbHelper dbHelper;
    LinearLayoutManager manager;

    public RecorderAdapter(Context context, LinearLayoutManager layoutManager)
    {
        super();
        mContext = context;
        dbHelper = new DbHelper(mContext);
        manager = layoutManager;
    }

    @Override
    public RecorderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.record_list_item,
                parent, false));
    }

    @Override
    public void onBindViewHolder(final RecorderAdapter.ViewHolder holder, final int position)
    {
        model = getItem(position);

        long itemDuration = model.getLength();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration) -
                TimeUnit.MINUTES.toSeconds(minutes);

        holder.vName.setText(model.getName());
        holder.vLength.setText(String.format("%02d:%02d", minutes, seconds));
        holder.vDateAdded.setText(
                DateUtils.formatDateTime(
                        mContext,
                        model.getTime(),
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE
                        | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR
                )
        );

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PlaybackFragment playbackFragment = new
                            PlaybackFragment().newInstance(getItem(holder.getAdapterPosition()));

                    android.support.v4.app.FragmentTransaction transaction = ((FragmentActivity) mContext)
                    .getSupportFragmentManager()
                    .beginTransaction();

                    playbackFragment.show(transaction, "dialog_playback");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        protected TextView vName;
        protected TextView vLength;
        protected TextView vDateAdded;
        protected View cardView;

        public ViewHolder(final View itemView)
        {
            super(itemView);
            vName = itemView.findViewById(R.id.file_name_text);
            vLength = itemView.findViewById(R.id.file_length_text);
            vDateAdded = itemView.findViewById(R.id.file_date_added_text);
            cardView = itemView.findViewById(R.id.record_card_view);
        }
    }

    @Override
    public int getItemCount() {
        return dbHelper.getRecordCount();
    }

    public RecordingModel getItem(int position)
    {
        return dbHelper.getRecordAt(position);
    }

    @Override
    public void onNewDatabaseEntryAdded()
    {
        notifyItemInserted(getItemCount() - 1);
        manager.scrollToPosition(getItemCount() - 1);
    }

    public void removeRecord(int position)
    {
        File file = new File(getItem(position).getFilePath());
        file.delete();

        dbHelper.removeRecordWithId(getItem(position).getId());
        notifyItemRemoved(position);
    }
}
