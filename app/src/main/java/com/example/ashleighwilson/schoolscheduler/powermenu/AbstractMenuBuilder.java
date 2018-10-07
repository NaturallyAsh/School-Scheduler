package com.example.ashleighwilson.schoolscheduler.powermenu;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;


public abstract class AbstractMenuBuilder
{
    protected Context context;
    protected LayoutInflater layoutInflater;

    protected boolean showBackground = true;
    protected LifecycleOwner lifecycleOwner = null;
    protected View.OnClickListener backgroundClickListener = null;
    protected com.example.ashleighwilson.schoolscheduler.powermenu.OnDismissedListener onDismissedListener = null;
    protected com.example.ashleighwilson.schoolscheduler.powermenu.MenuAnimation menuAnimation = MenuAnimation.DROP_DOWN;
    protected View headerView = null;
    protected View footerView = null;
    protected int animationStyle = -1;
    protected float menuRadius = 5;
    protected float menuShadow = 5;
    protected int width = 0;
    protected int height = 0;
    protected int dividerHeight = 0;
    protected Drawable divider = null;
    protected int backgroundColor = Color.BLACK;
    protected float backgroundAlpha = 0.6f;
    protected boolean focusable = false;
    protected int selected = -1;
    protected boolean isClipping = true;
    protected boolean autoDismiss = false;
}
