package com.example.ashleighwilson.schoolscheduler.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.models.AgendaModel;

import java.util.ArrayList;
import java.util.List;

public class DetailAssignmentAdapter extends RecyclerView.Adapter<DetailAssignmentAdapter.ViewHolder> {
    private static final String TAG = DetailAssignmentAdapter.class.getSimpleName();

    private Context mContext;
    private List<AgendaModel> agendaData;
    private DbHelper dbHelper;

    public DetailAssignmentAdapter(Context context, ArrayList<AgendaModel> models) {
        this.mContext = context;
        this.agendaData = models;
        this.dbHelper = DbHelper.getInstance();
    }

    @Override
    public DetailAssignmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.detail_event_list_item,
                parent, false));
    }

    @Override
    public void onBindViewHolder(final DetailAssignmentAdapter.ViewHolder holder, final int position) {

        final AgendaModel currentAgenda = agendaData.get(position);

        holder.titleTV.setText(currentAgenda.getAgendaTitle());
        holder.classTV.setText(currentAgenda.getClassName());
        holder.colorTV.setBackgroundColor(currentAgenda.getmColor());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTV;
        TextView classTV;
        TextView colorTV;

        public ViewHolder(final View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.detail_events_titleTv);
            classTV = itemView.findViewById(R.id.detail_events_classTv);
            colorTV = itemView.findViewById(R.id.detail_events_colorTv);
        }
    }

    @Override
    public int getItemCount() {
        return agendaData.size();
    }
}
