package com.example.ashleighwilson.schoolscheduler.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.views.PlaybackFragment;
import com.example.ashleighwilson.schoolscheduler.models.RecordingModel;
import com.example.ashleighwilson.schoolscheduler.utils.OnDatabaseChangedListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RecorderAdapter extends RecyclerView.Adapter<RecorderAdapter.ViewHolder>
    implements OnDatabaseChangedListener
{
    private static final String TAG = RecorderAdapter.class.getSimpleName();

    Context mContext;
    RecordingModel model;
    private DbHelper dbHelper;
    LinearLayoutManager manager;

    public RecorderAdapter(Context context, LinearLayoutManager layoutManager)
    {
        super();
        mContext = context;
        dbHelper = DbHelper.getInstance();
        DbHelper.setOnDatabasedChangedListener(this);
        manager = layoutManager;

    }

    @Override
    public RecorderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.record_list_item,
          //      parent, false));
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_list_item,
                parent, false);
        mContext = parent.getContext();

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecorderAdapter.ViewHolder holder, final int position)
    {
        model = getItem(position);

        long itemDuration = model.getLength();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration) -
                TimeUnit.MINUTES.toSeconds(minutes);

        holder.vName.setText(model.getName());
        holder.vLength.setText(String.format("%02d:%02d", minutes, seconds));
        holder.vDateAdded.setText(
                DateUtils.formatDateTime(
                        mContext,
                        model.getTime(),
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE
                        | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR
                )
        );

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PlaybackFragment playbackFragment = new
                            PlaybackFragment().newInstance(getItem(holder.getAdapterPosition()));

                    android.support.v4.app.FragmentTransaction transaction = ((FragmentActivity) mContext)
                    .getSupportFragmentManager()
                    .beginTransaction();

                    playbackFragment.show(transaction, "dialog_playback");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Rename?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        renameFileDialog(holder.getAdapterPosition());
                    }
                });
                builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return false;
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        protected TextView vName;
        protected TextView vLength;
        protected TextView vDateAdded;
        protected View cardView;

        public ViewHolder(final View itemView)
        {
            super(itemView);
            vName = itemView.findViewById(R.id.file_name_text);
            vLength = itemView.findViewById(R.id.file_length_text);
            vDateAdded = itemView.findViewById(R.id.file_date_added_text);
            cardView = itemView.findViewById(R.id.record_card_view);
        }
    }

    @Override
    public int getItemCount() {
        return dbHelper.getRecordCount();
    }

    public RecordingModel getItem(int position)
    {
        return dbHelper.getRecordAt(position);
    }

    @Override
    public void onNewDatabaseEntryAdded()
    {
        notifyItemInserted(getItemCount() - 1);
        manager.scrollToPosition(getItemCount() - 1);
    }

    @Override
    public void onDatabaseEntryRenamed()
    {

    }

    public void removeRecord(int position)
    {
        File file = new File(getItem(position).getFilePath());
        file.delete();

        dbHelper.removeRecordWithId(getItem(position).getId());
        notifyItemRemoved(position);
    }

    public void rename(int position, String name)
    {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFilePath += timeStamp + ".wav" + name;
        File file = new File(mFilePath);

        File oldFilePath = new File(getItem(position).getFilePath());
        oldFilePath.renameTo(file);
        dbHelper.renameRecord(getItem(position), name, mFilePath);
        notifyItemChanged(position);
    }

    public void renameFileDialog(final int position)
    {
        AlertDialog.Builder renameFileBuilder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_rename_file, null);

        final EditText input = view.findViewById(R.id.new_name);

        renameFileBuilder.setTitle(mContext.getString(R.string.dialog_title_rename));
        renameFileBuilder.setCancelable(true);
        renameFileBuilder.setPositiveButton(mContext.getString(R.string.dialog_action_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        try {
                            String value = input.getText().toString().trim() + ".wav";
                            rename(position, value);

                        } catch (Exception e) {
                            Log.e(TAG, "exception", e);
                        }

                        dialog.cancel();
                    }
                });
        renameFileBuilder.setNegativeButton(mContext.getString(R.string.dialog_action_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });
        renameFileBuilder.setView(view);
        AlertDialog alertDialog = renameFileBuilder.create();
        alertDialog.show();
    }
}
