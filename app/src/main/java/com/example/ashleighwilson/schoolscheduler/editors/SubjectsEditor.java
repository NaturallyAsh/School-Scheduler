package com.example.ashleighwilson.schoolscheduler.editors;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.ashleighwilson.schoolscheduler.R;

import butterknife.BindView;

public class SubjectsEditor extends DialogFragment
{
    Context context;
    private OnAddSubjectListener listener;
    @BindView(R.id.main_root)
    ViewGroup root;

    public interface OnAddSubjectListener
    {
        void OnAddSubjectSubmit(String title, String teacher, String room);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try{
            listener = (OnAddSubjectListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement onAddSubjectListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.editor_subjects, container, false);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.editor_subjects, root);
        final EditText mTitle = view.findViewById(R.id.edit_subject);
        final EditText mTeacher = view.findViewById(R.id.edit_subject_teacher);
        final EditText mRoom = view.findViewById(R.id.subject_room);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setTitle("Add Subject")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String sTitle = mTitle.getText().toString();
                        final String sTeacher = mTeacher.getText().toString();
                        final String sRoom = mRoom.getText().toString();

                        listener.OnAddSubjectSubmit(sTitle, sTeacher, sRoom);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }
}


