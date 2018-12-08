package com.example.ashleighwilson.schoolscheduler.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.models.SubjectsModel;

import java.util.ArrayList;
import java.util.List;

import com.example.ashleighwilson.schoolscheduler.SubjectDetailsActivity;
import com.example.ashleighwilson.schoolscheduler.utils.DateHelper;

import net.cachapa.expandablelayout.ExpandableLayout;

public class RecyclerSubAdapter extends RecyclerView.Adapter<RecyclerSubAdapter.ViewHolder>
{
    private static final String TAG = RecyclerSubAdapter.class.getSimpleName();

    public List<SubjectsModel> subMod;
    static ClickListener clickListener;
    Context context;
    public DbHelper dbHelper;
    public SubjectsModel model;
    private static final int UNSELECTED = -1;
    public int selectedItem = UNSELECTED;
    private RecyclerView recyclerView;
    public static final String EXTRA_ID = "ID";
    public static final String EXTRA_TITLE = "TITLE";
    public static final String EXTRA_TEACHER = "TEACHER";
    public static final String EXTRA_ROOM = "ROOM";
    public static final String EXTRA_COLOR = "COLOR";
    public static final String EXTRA_START = "START";
    public static final String EXTRA_END = "END";

    public RecyclerSubAdapter(Context context, ArrayList<SubjectsModel> subList, RecyclerView recyclerView)
    {
        this.context = context;
        this.subMod = subList;
        this.dbHelper = DbHelper.getInstance();
        //setClickListener(clickListener);
        this.recyclerView = recyclerView;
        setData(subList);
    }

    @NonNull
    @Override
    public RecyclerSubAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.subjects_item_list,
                parent, false));

    }

    @Override
    public void onBindViewHolder(final RecyclerSubAdapter.ViewHolder holder,final int position)
    {
       final SubjectsModel currentSubject = subMod.get(position);

        holder.titleView.setText(currentSubject.getmTitle());
        holder.teacher.setText(currentSubject.getmTeacher());
        holder.room.setText(currentSubject.getmRoom());
        holder.color.setBackgroundColor(currentSubject.getmColor());
        holder.startTime.setText(currentSubject.getmStartTime());
        holder.endTime.setText(currentSubject.getmEndTime());
        if (currentSubject.getmRecurrence_option() != null) {
            if (!currentSubject.getmRecurrence_option().equals("CUSTOM")) {
                Log.i(TAG, "option: " + currentSubject.getmRecurrence_option());
                holder.recurrenceTv.setText(currentSubject.getmRecurrence_option());
            }
        } else {
            if (currentSubject.getmRecurrence_rule() != null) {
                String formattedRule = DateHelper.formatRecurrence(context, currentSubject.getmRecurrence_rule());
                holder.recurrenceTv.setText(formattedRule);
                Log.i(TAG, "rule: " + currentSubject.getmRecurrence_rule());
            } else {
                holder.recurrenceTv.setText("n/a");
            }
        }

        boolean isSelected = position == selectedItem;
        holder.container.setSelected(isSelected);
        holder.layout.setExpanded(isSelected, false);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SubjectDetailsActivity.class);
                intent.putExtra(EXTRA_ID, getSubItem(holder.getAdapterPosition()));
                context.startActivity(intent);

                holder.layout.collapse();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView titleView;
        TextView teacher;
        TextView room;
        TextView color;
        TextView startTime;
        TextView endTime;
        TextView recurrenceTv;
        View itemView;
        LinearLayout container;
        ExpandableLayout layout;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.itemView = itemView;
            titleView = itemView.findViewById(R.id.subject_subject);
            teacher = itemView.findViewById(R.id.subject_teacher_text);
            room = itemView.findViewById(R.id.room_item);
            color = itemView.findViewById(R.id.color_item);
            startTime = itemView.findViewById(R.id.start_time_item);
            endTime = itemView.findViewById(R.id.end_time_item);
            recurrenceTv = itemView.findViewById(R.id.recurrenceTv);
            layout = itemView.findViewById(R.id.expandable_layout);
            container = itemView.findViewById(R.id.cardContainer);
            container.setOnClickListener(this);

            layout.setInterpolator(new OvershootInterpolator());
            layout.setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
                @Override
                public void onExpansionUpdate(float expansion, int state) {
                    if (state == ExpandableLayout.State.EXPANDING) {
                        recyclerView.smoothScrollToPosition(getAdapterPosition());
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            ViewHolder viewHolder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedItem);
            if (viewHolder != null) {
                viewHolder.container.setSelected(false);
                viewHolder.layout.collapse();
            }
            int position = getAdapterPosition();
            if (position == selectedItem) {
                selectedItem = UNSELECTED;
            } else {
                container.setSelected(true);
                layout.expand();
                selectedItem = position;
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return subMod.size();
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    public void setClickListener(ClickListener clicked)
    {
        RecyclerSubAdapter.clickListener = clicked;
    }

    public interface ClickListener
    {
        void itemClicked(int position);
    }

    public SubjectsModel getSubItem(int position) {
        return dbHelper.getSubAt(position);
    }

    public List<SubjectsModel> getSubjects() {
        return this.subMod;
    }

    public void setData(List<SubjectsModel> data) {
        this.subMod = data;
        //Log.i(TAG, "data size: " + data.size());
        notifyDataSetChanged();
        notifyItemInserted(getItemCount());
        notifyItemRangeChanged(0, data.size());
    }

    public void dismissItem(int position)
    {
        dbHelper.deleteSubject(subMod.get(position).getId());
        dbHelper.deleteLabel(subMod.get(position).getmTitle());
        subMod.remove(position);
        notifyItemRemoved(position);
    }
}
