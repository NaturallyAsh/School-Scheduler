package com.example.ashleighwilson.schoolscheduler;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashleighwilson.schoolscheduler.adapter.RecorderAdapter;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.dialog.RecordDialog;

import java.util.ArrayList;


public class RecordActivity extends AppCompatActivity
{
    private static final String TAG = RecordActivity.class.getSimpleName();

    static final int ALL_PERMISSIONS = 101;
    FloatingActionButton recordFab;
    RecordDialog recordDialog;
    RecyclerView recyclerView;
    RecorderAdapter recorderAdapter;
    DbHelper dbHelper;
    TextView emptyView;
    String name;
    String file;
    long length;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        ArrayList<String> arrayPerm = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            arrayPerm.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            arrayPerm.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED)
        {
            arrayPerm.add(Manifest.permission.RECORD_AUDIO);
        }
        if (!arrayPerm.isEmpty())
        {
            String[] permissions = new String[arrayPerm.size()];
            permissions = arrayPerm.toArray(permissions);
            ActivityCompat.requestPermissions(this, permissions, ALL_PERMISSIONS);
        }

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = DbHelper.getInstance();

        emptyView = findViewById(R.id.empty_record_view);
        recordFab = findViewById(R.id.show_rec_dialog);
        recyclerView = findViewById(R.id.record_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);

        recorderAdapter = new RecorderAdapter(this, manager);

        recyclerView.setLayoutManager(manager);

        //recyclerView.setAdapter(recorderAdapter);


        recordFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback
                (ItemTouchHelper.LEFT | ItemTouchHelper.DOWN |
                        ItemTouchHelper.UP, ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //int from = viewHolder.getAdapterPosition();
                //int to = target.getAdapterPosition();

                return true;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction)
            {
                final int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
                    builder.setMessage("Are you sure to delete?");
                    builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {

                            recorderAdapter.removeRecord(viewHolder.getAdapterPosition());
                            if (recorderAdapter.getItemCount() == 0)
                                setData();
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            recorderAdapter.notifyItemRemoved(position + 1);
                            recorderAdapter.notifyItemRangeChanged(position, recorderAdapter.getItemCount());

                        }
                    }).show();
                }
            }

        });

        helper.attachToRecyclerView(recyclerView);

    }

    public void showDialog()
    {
        recordDialog = RecordDialog.newInstance("Record Audio");
        recordDialog.setMessage("Press for record");
        recordDialog.show(RecordActivity.this.getFragmentManager(), "TAG");
        recordDialog.setPositiveButton("Save", new RecordDialog.ClickListener() {
            @Override
            public void OnClickListener(String name, String path, long length) {
                Toast.makeText(RecordActivity.this, "Save audio: " + name +
                        path + length, Toast.LENGTH_LONG).show();

                Log.i(TAG, "name: " + name + " path: " + path + " length: " + length);

                dbHelper.addRecording(name, path, length);
                setData();

            }
        });
    }

    public void setData()
    {
        if (recorderAdapter.getItemCount() < 1)
        {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(recorderAdapter);
            recorderAdapter.notifyDataSetChanged();
        }
        else
        {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            recyclerView.setAdapter(recorderAdapter);
            recorderAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults)
    {
        switch (requestCode)
        {
            case ALL_PERMISSIONS:
            {
                if (grantResults.length > 0)
                {
                    for (int i = 0; i < grantResults.length; i++)
                    {
                        String permission = permissions[i];
                        if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission))
                        {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                            {

                            }
                        }
                        if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission))
                        {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                            {

                            }
                        }
                        if (Manifest.permission.RECORD_AUDIO.equals(permission))
                        {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                            {

                            }
                        }
                    }
                }
                else
                {

                }
                break;
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setData();
    }
}
