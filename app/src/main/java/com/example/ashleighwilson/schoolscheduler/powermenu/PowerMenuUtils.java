package com.example.ashleighwilson.schoolscheduler.powermenu;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;

import com.example.ashleighwilson.schoolscheduler.R;

public class PowerMenuUtils
{
    public static PowerMenu getIconPowerMenu(Context context, OnMenuItemClickListener onMenuItemClickListener) {

        return new PowerMenu.Builder(context)
                .addItem(new PowerMenuItem("Completed", R.drawable.check_black_18dp))
                .addItem(new PowerMenuItem("Edit", R.drawable.edit_black_18dp))
                //.setLifecycleOwner(lifecycleOwner)
                .setOnMenuItemClickListener(onMenuItemClickListener)
                .setAnimation(MenuAnimation.FADE)
                .setMenuRadius(10f)
                .setMenuShadow(10f)
                .build();
    }


}
