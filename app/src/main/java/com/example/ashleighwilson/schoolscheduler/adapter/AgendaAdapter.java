package com.example.ashleighwilson.schoolscheduler.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.models.AgendaModel;
import com.example.ashleighwilson.schoolscheduler.powermenu.OnMenuItemClickListener;
import com.example.ashleighwilson.schoolscheduler.powermenu.PowerMenu;
import com.example.ashleighwilson.schoolscheduler.powermenu.PowerMenuItem;
import com.example.ashleighwilson.schoolscheduler.powermenu.PowerMenuUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.ViewHolder>
{
    private Context mContext;
    private List<AgendaModel> agendaData;
    private PowerMenu iconMenu;


    public AgendaAdapter(Context context, ArrayList<AgendaModel> models)
    {
        this.mContext = context;
        this.agendaData = models;

        setAgendaData(models);
    }

    @Override
    public AgendaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.agenda_list_item,
                parent, false));
    }

    @Override
    public void onBindViewHolder(final AgendaAdapter.ViewHolder holder, final int position)
    {
        final AgendaModel currentAgenda = agendaData.get(position);

        holder.agendaTitle.setText(currentAgenda.getAgendaTitle());
        holder.className.setText(currentAgenda.getClassName());
        holder.color.setBackgroundColor(currentAgenda.getmColor());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentAgenda.getDueDate().getTimeInMillis());
        //holder.dueDate.setText(String.valueOf(calendar));

        holder.popMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconMenu = PowerMenuUtils.getIconPowerMenu(mContext , onIconMenuItemClickListener);
                if (iconMenu.isShowing())
                {
                    iconMenu.dismiss();
                }
                iconMenu.showAsDropDown(v, -375, 0);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView className;
        TextView agendaTitle;
        TextView dueDate;
        TextView color;
        Button popMenu;

        public ViewHolder(final View itemView)
        {
            super(itemView);
            className = itemView.findViewById(R.id.agenda_class_name);
            agendaTitle = itemView.findViewById(R.id.agenda_assignment_title_text);
            dueDate = itemView.findViewById(R.id.agenda_due_date_text);
            color = itemView.findViewById(R.id.agenda_color_item);
            popMenu = itemView.findViewById(R.id.agenda_popup_bt);
        }
    }

    @Override
    public int getItemCount()
    {
        return agendaData.size();
    }

    private OnMenuItemClickListener<PowerMenuItem> onIconMenuItemClickListener = new OnMenuItemClickListener<PowerMenuItem>() {
        @Override
        public void onItemClick(int position, PowerMenuItem item) {
            iconMenu.dismiss();
        }
    };

    public void setAgendaData(List<AgendaModel> data)
    {
        agendaData = data;
        notifyDataSetChanged();
        notifyItemInserted(getItemCount());
        notifyItemRangeChanged(0, data.size());
    }
}
