package com.example.ashleighwilson.schoolscheduler.notes;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class GalleryPagerAdapter extends FragmentStatePagerAdapter {

    private List<Uri> resources;

    public GalleryPagerAdapter(FragmentActivity activity, List<Uri> resources) {
        super(activity.getSupportFragmentManager());
        this.resources = resources;
    }

    public Fragment getItem(int position) {
        try {
            return GalleryPagerFragment.create(position, (Uri)this.resources.get(position));

        } catch (Exception vr3) {
            return null;
        }
    }

    public int getCount() {
        return this.resources.size();
    }
}
