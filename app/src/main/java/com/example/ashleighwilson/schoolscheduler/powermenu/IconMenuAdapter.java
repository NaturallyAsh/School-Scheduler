package com.example.ashleighwilson.schoolscheduler.powermenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ashleighwilson.schoolscheduler.R;

public class IconMenuAdapter extends MenuBaseAdapter<IconMenuModel>
{
    public IconMenuAdapter()
    {
        super();
    }

    @Override
    public View getView(int index, View view, ViewGroup viewGroup)
    {
        final Context mContext = viewGroup.getContext();
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_power_menu, viewGroup, false);
        }

        IconMenuModel item = (IconMenuModel) getItem(index);
        final ImageView icon = view.findViewById(R.id.item_power_menu_icon);
        icon.setImageDrawable(item.getmIcon());
        final TextView title = view.findViewById(R.id.item_power_menu_title);
        title.setText(item.getIconTitle());
        return super.getView(index, view, viewGroup);
    }
}
