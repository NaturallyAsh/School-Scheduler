package com.example.ashleighwilson.schoolscheduler.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.models.RecordingModel;

import java.util.concurrent.TimeUnit;

public class RecorderAdapter extends RecyclerView.Adapter<RecorderAdapter.ViewHolder>
{
    Context mContext;
    RecordingModel model;
    private DbHelper dbHelper;

    public RecorderAdapter(Context context)
    {
        mContext = context;
        dbHelper = new DbHelper(mContext);
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
}
