package com.example.ashleighwilson.schoolscheduler.notes;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class InterceptorLinearLayout extends LinearLayout
{
    private OnViewTouchedListener mOnViewTouchedListener;

    public InterceptorLinearLayout(Context context)
    {
        super(context);
    }

    public InterceptorLinearLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        if (mOnViewTouchedListener != null)
        {
            mOnViewTouchedListener.onViewTouchOccured(event);
        }
        return super.onInterceptTouchEvent(event);
    }

    public void setOnViewTouchedListener(OnViewTouchedListener mOnViewTouchedListener)
    {
        this.mOnViewTouchedListener = mOnViewTouchedListener;
    }
}
