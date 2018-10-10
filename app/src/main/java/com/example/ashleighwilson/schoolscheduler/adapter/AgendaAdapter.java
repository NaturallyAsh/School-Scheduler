package com.example.ashleighwilson.schoolscheduler.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.models.AgendaModel;
import com.example.ashleighwilson.schoolscheduler.powermenu.MenuListAdapter;
import com.example.ashleighwilson.schoolscheduler.powermenu.OnMenuItemClickListener;
import com.example.ashleighwilson.schoolscheduler.powermenu.PowerMenu;
import com.example.ashleighwilson.schoolscheduler.powermenu.PowerMenuItem;
import com.example.ashleighwilson.schoolscheduler.powermenu.PowerMenuUtils;

import java.util.ArrayList;
import java.util.List;

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.ViewHolder>
{
    private Context mContext;
    private List<AgendaModel> agendaData;
    private PowerMenu iconMenu;
    private OnMenuItemClickListener listener;
    private DbHelper dbHelper;


    public AgendaAdapter(Context context, ArrayList<AgendaModel> models)
    {
        this.mContext = context;
        this.agendaData = models;
        this.dbHelper = new DbHelper(mContext);

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

        currentAgenda.setmInterval();

        holder.agendaTitle.setText(currentAgenda.getAgendaTitle());
        holder.className.setText(currentAgenda.getClassName());
        holder.dueDate.setText(currentAgenda.getDueDate());
        holder.dueDate.setTag(holder);
        holder.color.setBackgroundColor(currentAgenda.getmColor());
        holder.countdownDate.setText(String.valueOf(currentAgenda.getmInterval()) + " days remaining");

        holder.popMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconMenu = PowerMenuUtils.getIconPowerMenu(mContext , onIconMenuItemClickListener);
                if (iconMenu.isShowing())
                {
                    iconMenu.dismiss();
                }
                iconMenu.showAsDropDown(v, -375, 0);
                //onIconMenuItemClickListener.onItemClick(position, s);

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
        TextView countdownDate;

        public ViewHolder(final View itemView)
        {
            super(itemView);
            className = itemView.findViewById(R.id.agenda_class_name);
            agendaTitle = itemView.findViewById(R.id.agenda_assignment_title_text);
            dueDate = itemView.findViewById(R.id.due_date_text);
            color = itemView.findViewById(R.id.agenda_color_item);
            popMenu = itemView.findViewById(R.id.agenda_popup_bt);
            countdownDate = itemView.findViewById(R.id.countdown_date);

        }
    }

    @Override
    public int getItemCount()
    {
        return agendaData.size();
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    private OnMenuItemClickListener<PowerMenuItem> onIconMenuItemClickListener = new OnMenuItemClickListener<PowerMenuItem>() {
        @Override
        public void onItemClick(int position, PowerMenuItem item) {
            switch (position)
            {
                case 0:
                    Toast.makeText(mContext, "completed clicked", Toast.LENGTH_SHORT).show();
                    iconMenu.setSelectedPosition(position);
                    break;
                case 1:
                    Toast.makeText(mContext, "edit clicked", Toast.LENGTH_SHORT).show();
                    iconMenu.setSelectedPosition(position);
            }
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

    public void dismissAgenda(int position)
    {
        dbHelper.deleteAgenda(agendaData.get(position).getmId());
        agendaData.remove(position);
        notifyItemRemoved(position);
    }
}
