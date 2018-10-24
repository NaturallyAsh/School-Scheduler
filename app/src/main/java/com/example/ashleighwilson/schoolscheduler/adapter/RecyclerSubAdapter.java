package com.example.ashleighwilson.schoolscheduler.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.editors.SubjectsEditorActivity;
import com.example.ashleighwilson.schoolscheduler.models.SubjectsModel;

import java.util.ArrayList;
import java.util.List;

public class RecyclerSubAdapter extends RecyclerView.Adapter<RecyclerSubAdapter.ViewHolder>
{
    private static final String TAG = RecyclerSubAdapter.class.getSimpleName();

    public List<SubjectsModel> subMod;
    static ClickListener clickListener;
    Context context;
    public DbHelper dbHelper;
    public SubjectsModel model;

    public static final String EXTRA_ID = "ID";
    public static final String EXTRA_TITLE = "TITLE";
    public static final String EXTRA_TEACHER = "TEACHER";
    public static final String EXTRA_ROOM = "ROOM";
    public static final String EXTRA_COLOR = "COLOR";
    public static final String EXTRA_START = "START";
    public static final String EXTRA_END = "END";

    public RecyclerSubAdapter(Context context, ArrayList<SubjectsModel> subList)
    {
        this.context = context;
        this.subMod = subList;
        this.dbHelper = DbHelper.getInstance();

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


        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SubjectsEditorActivity.class);

                passData(currentSubject.getmTitle(), currentSubject.getmTeacher(), currentSubject.getmRoom(),
                        currentSubject.getmColor(), currentSubject.getmStartTime(), currentSubject.getmEndTime());
            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView titleView;
        TextView teacher;
        TextView room;
        Button edit;
        TextView color;
        TextView startTime;
        TextView endTime;

        public ViewHolder(final View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.subject_subject);
            teacher = itemView.findViewById(R.id.subject_teacher_text);
            room = itemView.findViewById(R.id.room_item);
            edit = itemView.findViewById(R.id.edit_button);
            color = itemView.findViewById(R.id.color_item);
            startTime = itemView.findViewById(R.id.start_time_item);
            endTime = itemView.findViewById(R.id.end_time_item);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null)
                clickListener.itemClicked(getAdapterPosition());
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

    public void setData(List<SubjectsModel> data) {
        subMod = data;
        notifyDataSetChanged();
        notifyItemInserted(getItemCount());
        notifyItemRangeChanged(0, data.size());
    }

    private void passData(String title, String teacher, String room, int color, String start, String end)
    {
        Intent intent = new Intent(context, SubjectsEditorActivity.class);
        //intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_TEACHER, teacher);
        intent.putExtra(EXTRA_ROOM, room);
        intent.putExtra(EXTRA_COLOR, color);
        intent.putExtra(EXTRA_START, start);
        intent.putExtra(EXTRA_END, end);

        context.startActivity(intent);
    }

    public void dismissItem(int position)
    {
        dbHelper.deleteSubject(subMod.get(position).getId());
        subMod.remove(position);
        notifyItemRemoved(position);
    }
}
