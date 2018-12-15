package com.example.ashleighwilson.schoolscheduler.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.models.WeekViewEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>
{
    private static final String TAG = EventAdapter.class.getSimpleName();

    private Context mContext;
    private List<WeekViewEvent> eventList;
    private final View.OnClickListener onClickListener;
    LayoutInflater inflater;
    WeekViewEvent event;
    DbHelper dbHelper;

    public EventAdapter(Context context, List<WeekViewEvent> events, View.OnClickListener listener)
    {
        this.mContext = context;
        //this.eventList = new ArrayList<>();
        this.eventList = events;
        if (events != null) {
            //events.addAll(events);
        }
        Log.i(TAG, "events: " + events.size());
        //inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        onClickListener = listener;
        dbHelper = DbHelper.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.event_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        event = eventList.get(position);
        if (event != null)
        {
            holder.eventTitle.setText(event.getName());
            holder.eventTitle.setBackgroundColor(event.getColor());
            holder.eventTitle.setTag(event);
            if (event.getStartTime() != null && event.getEndTime() != null) {
                String start = dateFormatter(event.getStartTime());
                String end = dateFormatter(event.getEndTime());

                holder.eventSchedule.setText(new StringBuilder().append(start).append(" - ").append(end).toString());
            } else {
                holder.eventSchedule.setText("N/A");
            }
            holder.eventSchedule.setBackgroundColor(event.getColor());
            holder.eventSchedule.setTag(event);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView eventTitle;
        TextView eventSchedule;

        public ViewHolder(View itemView)
        {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.event_list_title_tv);
            eventSchedule = itemView.findViewById(R.id.event_list_schedule_tv);
            eventTitle.setOnClickListener(onClickListener);
        }
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getItemCount()
    {
        return eventList.size();
    }

    public void setData(List<WeekViewEvent> events)
    {
        if (this.eventList == null)
        {
            this.eventList.clear();
        }
        //this.eventList.clear();
        //this.eventList.addAll(events);
        this.eventList = events;
        notifyDataSetChanged();
    }

    public void dismissEvent(int position) {

        dbHelper.deleteTimetable(eventList.get(position).getId());
        Log.i(TAG, "eventlist id: " + eventList.get(position).getId());
        //event.removeEvent(eventList.get(position).getId());
        eventList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, eventList.size());
    }

    private String dateFormatter(Calendar time)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String msg = sdf.format(time.getTime());

        return msg;
    }
}
