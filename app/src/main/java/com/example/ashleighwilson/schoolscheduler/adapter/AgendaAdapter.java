package com.example.ashleighwilson.schoolscheduler.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.editors.AgendaEditor;
import com.example.ashleighwilson.schoolscheduler.models.AgendaModel;
import com.example.ashleighwilson.schoolscheduler.powermenu.MenuListAdapter;
import com.example.ashleighwilson.schoolscheduler.powermenu.OnMenuItemClickListener;
import com.example.ashleighwilson.schoolscheduler.powermenu.PowerMenu;
import com.example.ashleighwilson.schoolscheduler.powermenu.PowerMenuItem;
import com.example.ashleighwilson.schoolscheduler.powermenu.PowerMenuUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.ViewHolder>
{
    private static final String TAG = AgendaModel.class.getSimpleName();

    private Context mContext;
    private List<AgendaModel> agendaData;
    private PowerMenu iconMenu;
    private OnMenuItemClickListener listener;
    private DbHelper dbHelper;
    AgendaMenuClickListener menuClickListener;
    public static final String EXTRA_TITLE = "TITLE";
    public static final String EXTRA_CLASSNAME = "CLASS_NAME";
    public static final String EXTRA_COLOR = "COLOR";
    public static final String EXTRA_DATE = "DATE";


    public AgendaAdapter(Context context, ArrayList<AgendaModel> models)
    {
        this.mContext = context;
        this.agendaData = models;
        this.dbHelper = DbHelper.getInstance();

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

                showPopUp(v, holder.dueDate);
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

    private void showPopUp(View view, TextView textView) {
        PopupMenu  popupMenu = new PopupMenu(mContext, view);

        popupMenu.inflate(R.menu.menu_agenda_popup);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_completed:
                        Toast.makeText(mContext, "context completed clicked", Toast.LENGTH_SHORT).show();
                        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        return true;

                    case R.id.menu_edit:
                        Toast.makeText(mContext, "context edit clicked", Toast.LENGTH_SHORT).show();
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

    public List<AgendaModel> getAgendaData() {
        return this.agendaData;
    }

    public OnMenuItemClickListener<PowerMenuItem> onIconMenuItemClickListener = new OnMenuItemClickListener<PowerMenuItem>() {
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
                    Intent intent = new Intent(mContext, AgendaEditor.class);

                    mContext.startActivity(intent);
            }
            iconMenu.dismiss();
        }
    };

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
        agendaData.remove(position);
        notifyItemRemoved(position);
    }

    private void passData(String title, String name, String due, int color) {

    }
}
