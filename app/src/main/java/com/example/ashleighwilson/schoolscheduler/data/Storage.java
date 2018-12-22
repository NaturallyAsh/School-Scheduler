package com.example.ashleighwilson.schoolscheduler.data;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.ashleighwilson.schoolscheduler.MySchedulerApp;
import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.notes.Attachment;
import com.example.ashleighwilson.schoolscheduler.notes.Constants;
import com.example.ashleighwilson.schoolscheduler.utils.MediaStoreFactory;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static java.lang.Long.parseLong;

public class Storage
{
    private static final String TAG = Storage.class.getSimpleName();

    private final MySchedulerApp mContext;

    public Storage(MySchedulerApp context)
    {
        mContext = context;
    }

    public static boolean checkStorage() {
        boolean mExternalStorageAvailable;
        boolean mExternalStorageWriteable;
        String state = Environment.getExternalStorageState();

        switch (state) {
            case Environment.MEDIA_MOUNTED:
                // We can read and write the media
                mExternalStorageAvailable = mExternalStorageWriteable = true;
                break;
            case Environment.MEDIA_MOUNTED_READ_ONLY:
                // We can only read the media
                mExternalStorageAvailable = true;
                mExternalStorageWriteable = false;
                break;
            default:
                // Something else is wrong. It may be one of many other states, but
                // all we need
                // to know is we can neither read nor write
                mExternalStorageAvailable = mExternalStorageWriteable = false;
                break;
        }
        return mExternalStorageAvailable && mExternalStorageWriteable;
    }

    public static File createNewAttachmentFile(Context mContext, String extension) {
        File f = null;
        if (checkStorage()) {
            f = new File(mContext.getExternalFilesDir(null), createNewAttachmentName(extension));
        }
        return f;
    }


    public static synchronized String createNewAttachmentName(String extension) {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_SORTABLE);
        String name = sdf.format(now.getTime());
        name += extension != null ? extension : "";
        return name;
    }

    public String getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public String getInternalFilesDirectory() {
        return mContext.getFilesDir().getAbsolutePath();
    }

    public static boolean isExternalWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalReadable()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
        {
            return true;
        }
        return false;
    }

    public boolean createDirectory(String path) {
        File directory = new File(path);
        if (directory.exists()) {
            Log.w(TAG, "Directory '" + path + "' already exists");
            return false;
        }
        return directory.mkdirs();
    }

    public boolean deleteFile(String path) {
        File file = new File(path);
        return file.delete();
    }

    public File getFile(String path) {
        return new File(path);
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        if (uri == null)
            return null;

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        parseLong(DocumentsContract.getDocumentId(uri)));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                MediaStoreFactory mediaStoreFactory = new MediaStoreFactory();
                Uri contentUri = mediaStoreFactory.createURI(type);

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving uri path", e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getMimeType(Context mContext, Uri uri) {
        ContentResolver cR = mContext.getContentResolver();
        String mimeType = cR.getType(uri);
        if (mimeType == null) {
            mimeType = getMimeType(uri.toString());
        }
        return mimeType;
    }



    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }


    /**
     * Retrieves uri mime-type between the ones managed by application
     *
     * @param mContext
     * @param uri
     * @return
     */
    public static String getMimeTypeInternal(Context mContext, Uri uri) {
        String mimeType = getMimeType(mContext, uri);
        mimeType = getMimeTypeInternal(mContext, mimeType);
        return mimeType;
    }


    /**
     * Retrieves mime-type between the ones managed by application from given string
     *
     * @param mContext
     * @param mimeType
     * @return
     */
    public static String getMimeTypeInternal(Context mContext, String mimeType) {
        if (mimeType != null) {
            if (mimeType.contains("image/")) {
                mimeType = Constants.MIME_TYPE_IMAGE;
            } else if (mimeType.contains("audio/")) {
                mimeType = Constants.MIME_TYPE_AUDIO;
            } else if (mimeType.contains("video/")) {
                mimeType = Constants.MIME_TYPE_VIDEO;
            } else {
                mimeType = Constants.MIME_TYPE_FILES;
            }
        }
        return mimeType;
    }

    public static boolean delete(Context mContext, String name) {
        boolean res = false;

        // Checks for external storage availability
        if (!checkStorage()) {
            Toast.makeText(mContext, mContext.getString(R.string.storage_not_available), Toast.LENGTH_SHORT).show();
            return false;
        }

        File file = new File(name);
        if (file.isFile()) {
            res = file.delete();
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file2 : files) {
                res = delete(mContext, file2.getAbsolutePath());
            }
            res = file.delete();
        }

        return res;
    }

    public static boolean deleteExternalStoragePrivateFile(Context mContext, String name) {
        if (!checkStorage()) {
            Toast.makeText(mContext, "Storage not available", Toast.LENGTH_SHORT).show();
            return false;
        }

        File file = new File(mContext.getExternalFilesDir(null), name);
        file.delete();

        return true;
    }


    public static String getRealPathFromURI(Context mContext, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = mContext.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) {
            return null;
        }
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    public static Attachment createAttachmentFromUri(Context mContext, Uri uri) {
        return createAttachmentFromUri(mContext, uri, false);
    }


    /**
     * Creates a fiile to be used as attachment.
     */
    public static Attachment createAttachmentFromUri(Context mContext, Uri uri, boolean moveSource) {
        String name = Storage.getNameFromUri(mContext, uri);
        String extension = Storage.getFileExtension(Storage.getNameFromUri(mContext, uri)).toLowerCase(
                Locale.getDefault());
        File f;
        if (moveSource) {
            f = createNewAttachmentFile(mContext, extension);
            try {
                FileUtils.moveFile(new File(uri.getPath()), f);
            } catch (IOException e) {
                Log.e(TAG, "Can't move file " + uri.getPath());
            }
        } else {
            f = Storage.createExternalStoragePrivateFile(mContext, uri, extension);
        }
        Attachment mAttachment = null;
        if (f != null) {
            mAttachment = new Attachment(Uri.fromFile(f), Storage.getMimeTypeInternal(mContext, uri));
            mAttachment.setName(name);
            mAttachment.setSize(f.length());
        }
        return mAttachment;
    }


    /**
     * Creates new attachment from web content
     *
     * @param mContext
     * @param url
     * @return
     * @throws IOException
     */
    public static File createNewAttachmentFileFromHttp(Context mContext, String url)
            throws IOException {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        return getFromHttp(url, createNewAttachmentFile(mContext, Storage.getFileExtension(url)));
    }


    /**
     * Retrieves a file from its web url
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static File getFromHttp(String url, File file) throws IOException {
        URL imageUrl = new URL(url);

        FileUtils.copyURLToFile(imageUrl, file);
        // os.close();
        return file;
    }

    public static String getNameFromUri(Context mContext, Uri uri) {
        String fileName = "";
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(uri, new String[]{"_display_name"}, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        fileName = cursor.getString(0);
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Error managing diskk cache", e);
                }
            } else {
                fileName = uri.getLastPathSegment();
            }
        } catch (SecurityException e) {
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return fileName;
    }

    public static String getFileExtension(File file) {
        return getFileExtension(file.getName());
    }


    public static String getFileExtension(String fileName) {
        if (TextUtils.isEmpty(fileName)) return "";
        String extension = "";
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            extension = fileName.substring(index, fileName.length());
        }
        return extension;
    }

    /**
     * Create a path where we will place our private file on external
     */
    public static File createExternalStoragePrivateFile(Context mContext, Uri uri, String extension) {

        if (!checkStorage()) {
            Toast.makeText(mContext, mContext.getString(R.string.storage_not_available), Toast.LENGTH_SHORT).show();
            return null;
        }
        File file = createNewAttachmentFile(mContext, extension);

        InputStream contentResolverInputStream = null;
        OutputStream contentResolverOutputStream = null;
        try {
            contentResolverInputStream = mContext.getContentResolver().openInputStream(uri);
            contentResolverOutputStream = new FileOutputStream(file);
            copyFile(contentResolverInputStream, contentResolverOutputStream);
        } catch (IOException e) {
            try {
                FileUtils.copyFile(new File(Storage.getPath(mContext, uri)), file);
                // It's a path!!
            } catch (NullPointerException e1) {
                try {
                    FileUtils.copyFile(new File(uri.getPath()), file);
                } catch (IOException e2) {
                    Log.e(TAG, "Error writing " + file, e2);
                    file = null;
                }
            } catch (IOException e2) {
                Log.e(TAG, "Error writing " + file, e2);
                file = null;
            }
        } finally {
            try {
                if (contentResolverInputStream != null) {
                    contentResolverInputStream.close();
                }
                if (contentResolverOutputStream != null) {
                    contentResolverOutputStream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error closing streams", e);
            }

        }
        return file;
    }

    public static boolean copyFile(InputStream is, OutputStream os) {
        boolean res = false;
        byte[] data = new byte[1024];
        int len;
        try {
            while ((len = is.read(data)) > 0) {
                os.write(data, 0, len);
            }
            is.close();
            os.close();
            res = true;
        } catch (IOException e) {
            Log.e(TAG, "Error copying file", e);
        }
        return res;
    }
}
