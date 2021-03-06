package com.example.ashleighwilson.schoolscheduler.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.data.NotificationController;
import com.example.ashleighwilson.schoolscheduler.editors.AgendaEditor;
import com.example.ashleighwilson.schoolscheduler.login.SessionManager;
import com.example.ashleighwilson.schoolscheduler.models.AgendaModel;
import com.example.ashleighwilson.schoolscheduler.utils.DateHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.ViewHolder>
{
    private static final String TAG = AgendaModel.class.getSimpleName();

    private Context mContext;
    private List<AgendaModel> agendaData;
    private DbHelper dbHelper;
    AgendaMenuClickListener menuClickListener;
    public static final String AGENDA_ARG = "agenda_item";
    private SessionManager manager;
    private String newDate;

    public AgendaAdapter(Context context, ArrayList<AgendaModel> models)
    {
        this.mContext = context;
        this.agendaData = models;
        this.dbHelper = DbHelper.getInstance();
        manager = new SessionManager(mContext);

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
        if (!currentAgenda.getTimeToNotify().matches("")) {
            holder.alarmIcon.setVisibility(View.VISIBLE);
        }
        holder.color.setBackgroundColor(currentAgenda.getmColor());
        //holder.countdownDate.setText(String.valueOf(currentAgenda.getmInterval()) + " days remaining");
        holder.countdownDate.setText(String.valueOf(currentAgenda.getmInterval()) + " days remaining");
        if (currentAgenda.getmInterval() == 0) {
            holder.countdownDate.setText(R.string.due_today_string);
        } else {
            if (currentAgenda.getmInterval() < 0) {
                holder.countdownDate.setText(R.string.overdue_string);
                holder.countdownDate.setTextColor(Color.RED);
            }
        }
        holder.popMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPopUp(v, holder.agendaTitle, holder.dueDate, getAgendaItem(holder.getAdapterPosition()));
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
        ImageView alarmIcon;

        public ViewHolder(final View itemView)
        {
            super(itemView);
            className = itemView.findViewById(R.id.agenda_class_name);
            agendaTitle = itemView.findViewById(R.id.agenda_assignment_title_text);
            dueDate = itemView.findViewById(R.id.due_date_text);
            color = itemView.findViewById(R.id.agenda_color_item);
            popMenu = itemView.findViewById(R.id.agenda_popup_bt);
            countdownDate = itemView.findViewById(R.id.countdown_date);
            alarmIcon = itemView.findViewById(R.id.notification_icon);
        }
    }

    private void showPopUp(View view, TextView title, TextView due, AgendaModel model) {

        PopupMenu  popupMenu = new PopupMenu(mContext, view);
        popupMenu.getMenu().clear();

        String origDue = due.getText().toString();

        if (!origDue.equals("Finished")) {
            manager.setDatePref(origDue);
        }
        newDate = manager.getDatePref();

        if (!due.getText().toString().equals("Finished")) {
            popupMenu.inflate(R.menu.menu_agenda_popup);
        } else {
            popupMenu.inflate(R.menu.menu_agenda_popup2);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_completed:
                        title.setPaintFlags(title.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG);
                        due.setText(R.string.finished_string);
                        return true;
                    case R.id.menu_edit:
                        Intent intent = new Intent(mContext, AgendaEditor.class);
                        intent.putExtra(AGENDA_ARG, model);
                        mContext.startActivity(intent);
                        return true;
                    case R.id.menu_not_complete:
                        title.setPaintFlags(title.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG);
                        due.setText(newDate);
                        return true;
                }
                return false;
            }

        });
        Object menuHelper;
        Class[] argTypes;
        try {
            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popupMenu);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);

        } catch (Exception e) {
            e.printStackTrace();
        }

        popupMenu.show();
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

    public AgendaModel getAgendaItem(int position) {
        return dbHelper.getAgendaAt(position);
    }

    public interface AgendaMenuClickListener {
        void MenuClicked(View view, int position);
    }

    public void setAgendaMenuClickListener(AgendaMenuClickListener menuClickListener) {
        this.menuClickListener = menuClickListener;
    }

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
        NotificationController.removeAgendaReminder(mContext, agendaData.get(position).getmId());
        agendaData.remove(position);
        notifyItemRemoved(position);
    }
}
