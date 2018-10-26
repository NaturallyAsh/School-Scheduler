package com.example.ashleighwilson.schoolscheduler.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.data.Storage;
import com.example.ashleighwilson.schoolscheduler.notes.Attachment;
import com.example.ashleighwilson.schoolscheduler.notes.Constants;


import org.apache.commons.io.FilenameUtils;

import it.feio.android.simplegallery.util.BitmapUtils;

public class BitmapHelper {
    /**
     * Retrieves a the bitmap relative to attachment based on mime type
     */
    public static Bitmap getBitmapFromAttachment(Context mContext, Attachment mAttachment, int width, int height) {
        Bitmap bmp = null;
        String path;
        mAttachment.getUri().getPath();

        // Video
        if (Constants.MIME_TYPE_VIDEO.equals(mAttachment.getMime_type())) {
            // Tries to retrieve full path from ContentResolver if is a new video
            path = Storage.getRealPathFromURI(mContext, mAttachment.getUri());
            // .. or directly from local directory otherwise
            if (path == null) {
                path = Storage.getPath(mContext, mAttachment.getUri());
            }
            bmp = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
            if (bmp == null) {
                return null;
            } else {
                bmp = BitmapUtils.createVideoThumbnail(mContext, bmp, width, height);
            }

            // Image
        } else if (Constants.MIME_TYPE_IMAGE.equals(mAttachment.getMime_type())
                || Constants.MIME_TYPE_SKETCH.equals(mAttachment.getMime_type())) {
            try {
                bmp = BitmapUtils.getThumbnail(mContext, mAttachment.getUri(), width, height);
            } catch (NullPointerException e) {
                bmp = null;
            }

            // Audio
        } else if (Constants.MIME_TYPE_AUDIO.equals(mAttachment.getMime_type())) {
            bmp = ThumbnailUtils.extractThumbnail(
                    BitmapUtils.decodeSampledBitmapFromResourceMemOpt(mContext.getResources().openRawResource(R
                            .raw.play), width, height), width, height);

            // File
        } else if (Constants.MIME_TYPE_FILES.equals(mAttachment.getMime_type())) {

            // vCard
            if (Constants.MIME_TYPE_CONTACT_EXT.equals(FilenameUtils.getExtension(mAttachment.getName()))) {
                bmp = ThumbnailUtils.extractThumbnail(
                        BitmapUtils.decodeSampledBitmapFromResourceMemOpt(mContext.getResources().openRawResource(R
                                .raw.vcard), width, height), width, height);
            } else {
                bmp = ThumbnailUtils.extractThumbnail(
                        BitmapUtils.decodeSampledBitmapFromResourceMemOpt(mContext.getResources().openRawResource(R
                                .raw.files), width, height), width, height);
            }
        }

        return bmp;
    }


    public static Uri getThumbnailUri(Context mContext, Attachment mAttachment) {
        Uri uri = mAttachment.getUri();
        String mimeType = Storage.getMimeType(uri.toString());
        if (!TextUtils.isEmpty(mimeType)) {
            String type = mimeType.split("/")[0];
            String subtype = mimeType.split("/")[1];
            switch (type) {
                case "image":
                case "video":
                    // Nothing to do, bitmap will be retrieved from this
                    break;
                case "audio":
                    uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.play);
                    break;
                default:
                    int drawable = "x-vcard".equals(subtype) ? R.raw.vcard : R.raw.files;
                    uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + drawable);
                    break;
            }
        } else {
            uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.files);
        }
        return uri;
    }
}
