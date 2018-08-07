package com.example.ashleighwilson.schoolscheduler;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class CalenderFrag extends Fragment
{

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;


    public CalenderFrag() {
        // Required empty public constructor
    }

    /*public static CalenderFrag newInstance(int page)
    {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        CalenderFrag frag = new CalenderFrag();
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    } */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calender, container, false);


        return view;
    }

}
