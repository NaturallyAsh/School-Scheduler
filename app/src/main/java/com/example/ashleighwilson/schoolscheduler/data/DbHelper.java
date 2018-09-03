package com.example.ashleighwilson.schoolscheduler.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.ashleighwilson.schoolscheduler.models.RecordingModel;
import com.example.ashleighwilson.schoolscheduler.models.SubjectsModel;
import com.example.ashleighwilson.schoolscheduler.utils.OnDatabaseChangedListener;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper
{
    private static DbHelper dbHelper = null;
    String[] allColumns = new String[] {
            SchoolEntry._ID,
            SchoolEntry.COLUMN_TITLE,
            SchoolEntry.COLUMN_TEACHER,
            SchoolEntry.COLUMN_ROOM,
            SchoolEntry.COLUMN_COLOR
    };

    String[] recordColumns = new String[] {
            RecordEntry._ID,
            RecordEntry.COLUMN_NAME,
            RecordEntry.COLUMN_FILE_PATH,
            RecordEntry.COLUMN_RECORD_LENGTH,
            RecordEntry.COLUMN_RECORD_TIME
    };

    private static final String TAG = DbHelper.class.getSimpleName();

    private static OnDatabaseChangedListener mOnDatabaseChangedListener;

    private static final String DATABASE_NAME = "school.db";
    private static final int DATABASE_VERSION = 3;
    public static final String CONTENT_AUTHORITY = "com.example.ashleighwilson.schoolscheduler";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_SCHOOL = "schoolscheduler";

    public static final class SchoolEntry implements BaseColumns
    {

        public final static String TABLE_NAME = "subjects";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_TITLE = "title";
        public final static String COLUMN_TEACHER = "teacher";
        public final static String COLUMN_ROOM = "room";
        public final static String COLUMN_COLOR = "color";
    }

    String SQL_CREATE_SUBJECTS_TABLE = "CREATE TABLE " + SchoolEntry.TABLE_NAME +
            " ("
            + SchoolEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SchoolEntry.COLUMN_TITLE + " TEXT, "
            + SchoolEntry.COLUMN_TEACHER + " TEXT, "
            + SchoolEntry.COLUMN_ROOM + " TEXT, "
            + SchoolEntry.COLUMN_COLOR + " INTEGER);";

    public static final class RecordEntry implements BaseColumns
    {
        public final static String TABLE_NAME = "recordings";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_FILE_PATH = "file_path";
        public final static String COLUMN_RECORD_LENGTH = "length";
        public final static String COLUMN_RECORD_TIME = "time";
    }

    String SQL_CREATE_RECORDINGS_TABLE = "CREATE TABLE " + RecordEntry.TABLE_NAME +
            " ("
            + RecordEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RecordEntry.COLUMN_NAME + " TEXT, "
            + RecordEntry.COLUMN_FILE_PATH + " TEXT, "
            + RecordEntry.COLUMN_RECORD_LENGTH + " INTEGER, "
            + RecordEntry.COLUMN_RECORD_TIME + " INTEGER);";

    public DbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_SUBJECTS_TABLE);
        db.execSQL(SQL_CREATE_RECORDINGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + SchoolEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RecordEntry.TABLE_NAME);
        onCreate(db);
    }

    public static void setOnDatabasedChangedListener(OnDatabaseChangedListener listener)
    {
        mOnDatabaseChangedListener = listener;
    }

    public long addClass(SubjectsModel model)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(SchoolEntry._ID, model.getId());
        values.put(SchoolEntry.COLUMN_TITLE, model.getmTitle());
        values.put(SchoolEntry.COLUMN_TEACHER, model.getmTeacher());
        values.put(SchoolEntry.COLUMN_ROOM, model.getmRoom());
        values.put(SchoolEntry.COLUMN_COLOR, model.getmColor());

        long res = db.insert(SchoolEntry.TABLE_NAME, null, values);
        db.close();

        return res;
    }

    public Cursor getAltSub()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(SchoolEntry.TABLE_NAME, allColumns, null, null,
                null, null, null);
    }

    public long getSubjectCount()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        return DatabaseUtils.queryNumEntries(db, SchoolEntry.TABLE_NAME);
    }

    public ArrayList<SubjectsModel> getAllSubjects()
    {
        ArrayList<SubjectsModel> modelArrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(SchoolEntry.TABLE_NAME, allColumns, null, null,
                null, null, null);

        if (cursor.moveToFirst())
        {
            do {
                SubjectsModel model = new SubjectsModel();
                        model.setId(Integer.parseInt(cursor.getString(0)));
                        model.setmTitle(cursor.getString(1));
                        model.setmTeacher(cursor.getString(2));
                        model.setmRoom(cursor.getString(3));
                        model.setmColor(Integer.parseInt(cursor.getString(4)));
                modelArrayList.add(model);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return modelArrayList;
    }

    public int updateSubject(int id, String title, String teacher, String room, int color)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int numOfRowsUpdated = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(SchoolEntry.COLUMN_TITLE, title);
            values.put(SchoolEntry.COLUMN_TEACHER, teacher);
            values.put(SchoolEntry.COLUMN_ROOM, room);
            values.put(SchoolEntry.COLUMN_COLOR, color);

            numOfRowsUpdated = db.update(SchoolEntry.TABLE_NAME, values, SchoolEntry._ID + " = ?",
                    new String[]{String.valueOf(id)});
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return numOfRowsUpdated;
    }

    public void removeSubject(SubjectsModel model)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(SchoolEntry.TABLE_NAME, SchoolEntry._ID + " =?",
                null);
        db.close();
    }

    public long deleteSubject(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

            return db.delete(SchoolEntry.TABLE_NAME, SchoolEntry._ID + " =?",
                    new String[]{String.valueOf(id)});

    }

    public RecordingModel getRecordAt(int position)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(RecordEntry.TABLE_NAME, recordColumns, null, null,
                null, null, null);
        if (cursor.moveToPosition(position))
        {
            RecordingModel record = new RecordingModel();
            record.setId(cursor.getInt(cursor.getColumnIndex(RecordEntry._ID)));
            record.setName(cursor.getString(cursor.getColumnIndex(RecordEntry.COLUMN_NAME)));
            record.setFilePath(cursor.getString(cursor.getColumnIndex(RecordEntry.COLUMN_FILE_PATH)));
            record.setLength(cursor.getInt(cursor.getColumnIndex(RecordEntry.COLUMN_RECORD_LENGTH)));
            record.setTime(cursor.getLong(cursor.getColumnIndex(RecordEntry.COLUMN_RECORD_TIME)));
            cursor.close();
            return record;
        }
        return null;
    }

    public long addRecording(String name, String filePath, long length)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(RecordEntry.COLUMN_NAME, name);
        values.put(RecordEntry.COLUMN_FILE_PATH, filePath);
        values.put(RecordEntry.COLUMN_RECORD_LENGTH, length);
        values.put(RecordEntry.COLUMN_RECORD_TIME, System.currentTimeMillis());

        return db.insert(RecordEntry.TABLE_NAME, null, values);
    }

    public long addAltRecording(RecordingModel model)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(RecordEntry.COLUMN_NAME, model.getName());
        values.put(RecordEntry.COLUMN_FILE_PATH, model.getFilePath());
        values.put(RecordEntry.COLUMN_RECORD_LENGTH, model.getNewLength());
        values.put(RecordEntry.COLUMN_RECORD_TIME, System.currentTimeMillis());

        long rowId = db.insert(RecordEntry.TABLE_NAME, null, values);

        if (mOnDatabaseChangedListener != null)
            mOnDatabaseChangedListener.onNewDatabaseEntryAdded();

        return rowId;
    }

    public int getRecordCount()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {RecordEntry._ID};

        Cursor cursor = db.query(RecordEntry.TABLE_NAME, projection, null, null,
                null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void removeRecordWithId(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

         db.delete(RecordEntry.TABLE_NAME, RecordEntry._ID + " =?",
                new String[]{String.valueOf(id)});
    }
}
