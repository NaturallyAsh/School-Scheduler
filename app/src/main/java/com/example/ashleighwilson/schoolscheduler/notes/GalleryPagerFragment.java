package com.example.ashleighwilson.schoolscheduler.notes;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import it.feio.android.simplegallery.views.TouchImageView;

import com.bumptech.glide.request.RequestOptions;
import com.example.ashleighwilson.schoolscheduler.R.drawable;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import com.example.ashleighwilson.schoolscheduler.notes.Display;

public class GalleryPagerFragment extends Fragment {

    public static final String ARG_PAGE = "page";
    public static final String ARG_PATH = "path";
    private int mPageNumber;
    private Uri mImagePath;

    public GalleryPagerFragment() {
    }

    public static GalleryPagerFragment create(int pageNumber, Uri imagePath) {
        GalleryPagerFragment fragment = new GalleryPagerFragment();
        Bundle args = new Bundle();
        args.putInt("page", pageNumber);
        args.putSerializable("path", imagePath.toString());
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mPageNumber = this.getArguments().getInt("page");
        this.mImagePath = Uri.parse(this.getArguments().getString("path"));
    }

    @SuppressLint({"NewApi"})
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TouchImageView rootView = new TouchImageView(this.getActivity());
        rootView.setLayoutParams(new ActionBar.LayoutParams(-1, -1));
        Point dimensions = Display.getUsableSize(this.getActivity());
        Glide.with(this.getActivity())
                .load(this.mImagePath)
                .transition(withCrossFade())
                .apply(new RequestOptions().override(dimensions.x, dimensions.y).error(drawable.my_image_broken))
                .into(rootView);
        return rootView;
    }

    public int getPageNumber() {
        return this.mPageNumber;
    }
}
