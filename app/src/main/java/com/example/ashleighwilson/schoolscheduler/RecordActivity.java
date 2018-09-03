package com.example.ashleighwilson.schoolscheduler;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.ashleighwilson.schoolscheduler.adapter.RecorderAdapter;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.dialog.RecordDialog;
import com.example.ashleighwilson.schoolscheduler.editors.SubjectsEditor;
import com.example.ashleighwilson.schoolscheduler.models.RecordingModel;

import java.util.ArrayList;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class RecordActivity extends AppCompatActivity
{
    static final int ALL_PERMISSIONS = 101;
    //private int requestCode;
    //private int grantResults[];
    FloatingActionButton recordFab;
    RecordDialog recordDialog;
    RecyclerView recyclerView;
    RecorderAdapter adapter;
    DbHelper dbHelper;
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

        dbHelper = new DbHelper(getApplicationContext());

        recordFab = findViewById(R.id.show_rec_dialog);
        recyclerView = findViewById(R.id.record_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);

        adapter = new RecorderAdapter(this, manager);

        recyclerView.setLayoutManager(manager);

        //recyclerView.setAdapter(adapter);


        recordFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
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

                setData(name, path, length);

            }
        });
    }

    @AfterPermissionGranted(ALL_PERMISSIONS)
    private void recordCall()
    {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms))
        {
            showDialog();
        }
        else
        {
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your storage and microphone.",
                    ALL_PERMISSIONS,
                    perms
            );
        }
    }

    public void setData(String name, String path, long length)
    {
        RecordingModel model = new RecordingModel();
        model.setName(name);
        model.setFilePath(path);
        model.setNewLength(length);
        dbHelper.addAltRecording(model);
        recyclerView.setAdapter(adapter);
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
        setData(name, file, length);
    }
}
