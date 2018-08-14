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
import com.example.ashleighwilson.schoolscheduler.models.SubjectsModel;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder>
{
    private static final String TAG = CardAdapter.class.getSimpleName();

    public ArrayList<SubjectsModel> subMod;
    private OnItemClicked onClick;
    static ClickListener clickListener;
    Context context;

    public CardAdapter(Context context, ArrayList<SubjectsModel> subMod)
    {
        this.context = context;
        this.subMod = subMod;
    }

    @NonNull
    @Override
    public CardAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new CardViewHolder(LayoutInflater.from(context).inflate(R.layout.subjects_item_list,
                parent, false));

    }

    @Override
    public void onBindViewHolder(final CardAdapter.CardViewHolder holder, final int position)
    {
       SubjectsModel currentSubject = subMod.get(position);

        holder.titleView.setText(currentSubject.getmTitle());
        holder.teacher.setText(currentSubject.getmTeacher());
    }

    public class CardViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener
    {
        TextView titleView;
        TextView teacher;
        CardView cardView;

        public CardViewHolder(View itemView)
        {
            super(itemView);
            titleView = itemView.findViewById(R.id.subject_subject);
            teacher = itemView.findViewById(R.id.subject_teacher_text);
            cardView = itemView.findViewById(R.id.card_view);

            cardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view)
        {
            if (clickListener != null)
            {
                clickListener.itemClicked(view, getAdapterPosition());
                Toast.makeText(context, R.string.hello_blank_fragment, Toast.LENGTH_SHORT).show();
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
        CardAdapter.clickListener = clicked;
    }

    public interface ClickListener
    {
        public void itemClicked(View view, int position);
    }

    public void setData(ArrayList<SubjectsModel> data)
    {
        if (data != null && !data.isEmpty())
        {
            this.subMod = data;
            //this.subMod.clear();
            this.subMod.addAll(data);
            notifyDataSetChanged();
        }
        else
            this.subMod.clear();
    }
}
