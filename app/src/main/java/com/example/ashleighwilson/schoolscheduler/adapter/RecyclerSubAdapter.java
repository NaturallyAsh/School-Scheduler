package com.example.ashleighwilson.schoolscheduler.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.models.SubjectsModel;

import java.util.ArrayList;
import java.util.List;

public class RecyclerSubAdapter extends RecyclerView.Adapter<RecyclerSubAdapter.ViewHolder>
{
    private static final String TAG = RecyclerSubAdapter.class.getSimpleName();

    public List<SubjectsModel> subMod;
    private OnItemClicked onClick;
    static ClickListener clickListener;
    Context context;
    public DbHelper dbHelper;

    public RecyclerSubAdapter(Context context, ArrayList<SubjectsModel> subList)
    {
        this.context = context;
        this.subMod = subList;
        this.dbHelper = new DbHelper(context);
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
    public void onBindViewHolder(final RecyclerSubAdapter.ViewHolder holder, final int position)
    {
       SubjectsModel currentSubject = subMod.get(position);

        holder.titleView.setText(currentSubject.getmTitle());
        holder.teacher.setText(currentSubject.getmTeacher());
        holder.room.setText(currentSubject.getmRoom());

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener
    {
        TextView titleView;
        TextView teacher;
        TextView room;

        public ViewHolder(View itemView)
        {
            super(itemView);
            titleView = itemView.findViewById(R.id.subject_subject);
            teacher = itemView.findViewById(R.id.subject_teacher_text);
            room = itemView.findViewById(R.id.room_item);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view)
        {
            if (clickListener != null)
            {
                clickListener.itemClicked(view, getAdapterPosition());
            }
        }
    }

    @Override
    public int getItemCount()
    {
        if (subMod == null)
        {
            Log.d(TAG, "sub is null");
        }
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

    public interface OnItemClicked
    {
        void onItemClick(int position);
    }

    public void setOnClick(OnItemClicked onClick)
    {
        this.onClick = onClick;
    }

    public void setClickListener(ClickListener clicked)
    {
        RecyclerSubAdapter.clickListener = clicked;
    }

    public interface ClickListener
    {
        void itemClicked(View view, int position);
    }

    public void setData(List<SubjectsModel> data) {
        subMod = data;
        notifyDataSetChanged();
        notifyItemInserted(getItemCount());
        notifyItemRangeChanged(0, data.size());
    }
}
