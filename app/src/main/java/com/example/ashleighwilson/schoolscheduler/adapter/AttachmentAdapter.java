package com.example.ashleighwilson.schoolscheduler.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.notes.Attachment;
import com.example.ashleighwilson.schoolscheduler.notes.Constants;
import com.example.ashleighwilson.schoolscheduler.utils.BitmapHelper;
import com.example.ashleighwilson.schoolscheduler.utils.DateHelper;
import com.example.ashleighwilson.schoolscheduler.utils.ExpandableHeightGridView;
import com.example.ashleighwilson.schoolscheduler.utils.SquareImageView;

import java.util.Collections;
import java.util.List;

public class AttachmentAdapter extends BaseAdapter {

    private static final String TAG = AttachmentAdapter.class.getSimpleName();

    private Activity mActivity;
    private List<Attachment> attachmentList;
    private LayoutInflater inflater;

    public AttachmentAdapter(Activity activity, List<Attachment> attachments, ExpandableHeightGridView gridView) {
        this.mActivity = activity;
        if (attachments == null) {
            attachments = Collections.emptyList();
        }
        this.attachmentList = attachments;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class AttachmentHolder {
        TextView text;
        SquareImageView image;
    }

    public View getView(int position, View convertView, ViewGroup parent) {


        Attachment currentAttachment = attachmentList.get(position);
        AttachmentHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.gridview_item, parent, false);

            holder = new AttachmentHolder();
            holder.image = convertView.findViewById(R.id.gridview_item_picture);
            holder.text = convertView.findViewById(R.id.gridview_item_text);
            convertView.setTag(holder);
        } else {
            holder = (AttachmentHolder) convertView.getTag();
        }

        if (currentAttachment.getMime_type() != null && currentAttachment.getMime_type().equals(Constants.MIME_TYPE_AUDIO)) {
            String text;

            if (currentAttachment.getLength() > 0) {
                // Recording duration
                text = DateHelper.formatShortTime(mActivity, currentAttachment.getLength());
            } else {
                // Recording date otherwise
                text = DateHelper.getLocalizedDateTime(mActivity, currentAttachment
                        .getUri().getLastPathSegment().split("\\.")[0],
                        Constants.DATE_FORMAT_SORTABLE);
            }

            if (text == null) {
                text = "Attachment";
            }
            holder.text.setText(text);
            holder.text.setVisibility(View.VISIBLE);
        } else {
            holder.text.setVisibility(View.GONE);
        }

        //Draw name in case the type is an audio recording
        //(or file in the future)
        if (currentAttachment.getMime_type() != null && currentAttachment.getMime_type().equals(Constants.MIME_TYPE_FILES)) {
            holder.text.setText(currentAttachment.getName());
            holder.text.setVisibility(View.VISIBLE);
        }
        //Starts the Async to draw bitmap into ImageView
        Uri thumbnailUri = BitmapHelper.getThumbnailUri(mActivity, currentAttachment);
        Glide.with(mActivity.getApplicationContext())
                .load(thumbnailUri)
                .centerCrop()
                .crossFade()
                .into(holder.image);

        return convertView;
    }

    public List<Attachment> getAttachmentsList() {
        return this.attachmentList;
    }

    public int getCount() {
        return attachmentList.size();
    }

    public Attachment getItem(int position) {
        return attachmentList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }
}
