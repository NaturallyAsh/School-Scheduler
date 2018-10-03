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
import com.example.ashleighwilson.schoolscheduler.models.TimeTableModel;
import com.example.ashleighwilson.schoolscheduler.timetable.Event;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekViewEvent;
import com.example.ashleighwilson.schoolscheduler.utils.OnDatabaseChangedListener;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper
{
    private Context mContext;

    private static DbHelper dbHelper = null;
    String[] allColumns = new String[] {
            SchoolEntry._ID,
            SchoolEntry.COLUMN_TITLE,
            SchoolEntry.COLUMN_TEACHER,
            SchoolEntry.COLUMN_ROOM,
            SchoolEntry.COLUMN_COLOR,
            SchoolEntry.COLUMN_STARTTIME,
            SchoolEntry.COLUMN_ENDTIME
    };

    String[] recordColumns = new String[] {
            RecordEntry._ID,
            RecordEntry.COLUMN_NAME,
            RecordEntry.COLUMN_FILE_PATH,
            RecordEntry.COLUMN_RECORD_LENGTH,
            RecordEntry.COLUMN_RECORD_TIME
    };

    String[] timeTableColumns = new String[] {
            TimeTableEntry._ID,
            TimeTableEntry.COLUMN_NAME,
            TimeTableEntry.COLUMN_LOCATION,
            TimeTableEntry.COLUMN_STARTHOUR,
            TimeTableEntry.COLUMN_ENDHOUR,
            TimeTableEntry.COLUMN_COLOR
    };

    private static final String TAG = DbHelper.class.getSimpleName();

    private static OnDatabaseChangedListener mOnDatabaseChangedListener;

    private static final String DATABASE_NAME = "school.db";
    private static final int DATABASE_VERSION = 15;
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
        public final static String COLUMN_STARTTIME = "startTime";
        public final static String COLUMN_ENDTIME = "endTime";
    }

    String SQL_CREATE_SUBJECTS_TABLE = "CREATE TABLE " + SchoolEntry.TABLE_NAME +
            " ("
            + SchoolEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SchoolEntry.COLUMN_TITLE + " TEXT, "
            + SchoolEntry.COLUMN_TEACHER + " TEXT, "
            + SchoolEntry.COLUMN_ROOM + " TEXT, "
            + SchoolEntry.COLUMN_COLOR + " INTEGER, "
            + SchoolEntry.COLUMN_STARTTIME + " TEXT, "
            +SchoolEntry.COLUMN_ENDTIME + " TEXT);";

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

    public static final class TimeTableEntry implements BaseColumns
    {
        public final static String TABLE_NAME = "timetable";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_LOCATION = "location";
        public final static String COLUMN_STARTHOUR = "starthour";
        public final static String COLUMN_ENDHOUR = "endhour";
        public final static String COLUMN_COLOR = "color";

    }

    String SQL_CREATE_TIMETABLE_TABLE = "CREATE TABLE " + TimeTableEntry.TABLE_NAME +
            " ("
            + TimeTableEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TimeTableEntry.COLUMN_NAME + " TEXT, "
            + TimeTableEntry.COLUMN_LOCATION + " TEXT, "
            + TimeTableEntry.COLUMN_STARTHOUR + " INTEGER, "
            + TimeTableEntry.COLUMN_ENDHOUR + " INTEGER, "
            + TimeTableEntry.COLUMN_COLOR + " INTEGER);";

    public DbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_SUBJECTS_TABLE);
        db.execSQL(SQL_CREATE_RECORDINGS_TABLE);
        db.execSQL(SQL_CREATE_TIMETABLE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + SchoolEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RecordEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TimeTableEntry.TABLE_NAME);
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
        values.put(SchoolEntry.COLUMN_STARTTIME, model.getmStartTime());
        values.put(SchoolEntry.COLUMN_ENDTIME, model.getmEndTime());

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
                        model.setmStartTime(cursor.getString(5));
                        model.setmEndTime(cursor.getString(6));
                modelArrayList.add(model);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return modelArrayList;
    }

    public int updateSubject(int id, String title, String teacher, String room, int color, String start, String end)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int numOfRowsUpdated = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(SchoolEntry.COLUMN_TITLE, title);
            values.put(SchoolEntry.COLUMN_TEACHER, teacher);
            values.put(SchoolEntry.COLUMN_ROOM, room);
            values.put(SchoolEntry.COLUMN_COLOR, color);
            values.put(SchoolEntry.COLUMN_STARTTIME, start);
            values.put(SchoolEntry.COLUMN_ENDTIME, end);

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

    public void renameRecord(RecordingModel item, String name, String filePath)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RecordEntry.COLUMN_NAME, name);
        values.put(RecordEntry.COLUMN_FILE_PATH, filePath);
        db.update(RecordEntry.TABLE_NAME, values, RecordEntry._ID + "=" +
        item.getId(), null);

        if (mOnDatabaseChangedListener != null)
            mOnDatabaseChangedListener.onDatabaseEntryRenamed();
    }

    public long addTimetable(WeekViewEvent model)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(TimeTableEntry._ID, model.getId());
        values.put(TimeTableEntry.COLUMN_NAME, model.getName());
        values.put(TimeTableEntry.COLUMN_LOCATION, model.getLocation());
        values.put(TimeTableEntry.COLUMN_STARTHOUR, model.getStartTime().getTimeInMillis());
        values.put(TimeTableEntry.COLUMN_ENDHOUR, model.getEndTime().getTimeInMillis());
        values.put(TimeTableEntry.COLUMN_COLOR, model.getColor());

        return db.insert(TimeTableEntry.TABLE_NAME, null, values);
    }

    /*public void timeTableList (List<WeekViewEvent> list)
    {
        WeekViewEvent event;
        long start = event.getStartTime().getTimeInMillis();
        long end = event.getEndTime().getTimeInMillis();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(TimeTableEntry.TABLE_NAME, timeTableColumns, null);
        while (cursor.moveToNext())
        {
            WeekViewEvent current = new WeekViewEvent(
                    cursor.getLong(1),
                    cursor.getString(2),
                    cursor.getLong(3),
                    cursor.getLong(4)),
                    cursor.getInt(5);
            current.setmId(cursor.getInt(cursor.getColumnIndex(TimeTableEntry._ID)));
            list.add(current);
        }
        cursor.close();
    } */

    public Cursor fetchEvents()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(TimeTableEntry.TABLE_NAME, timeTableColumns, null, null,
                null, null, null);
    }

    public long getTimeTableId(long id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(TimeTableEntry.TABLE_NAME, timeTableColumns);

        while (cursor.moveToNext())
        {
            id = cursor.getLong(cursor.getColumnIndex(TimeTableEntry._ID));
        }
        cursor.close();
        return id;
    }

    public int getSubjectId(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(SchoolEntry.TABLE_NAME, allColumns);

        while (cursor.moveToNext())
        {
            id = cursor.getInt(cursor.getColumnIndex(SchoolEntry._ID));
        }
        cursor.close();
        return id;
    }

    /*public void removeTimeTableWithId(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TimeTableEntry.TABLE_NAME, TimeTableEntry._ID + " =?",
                new String[]{String.valueOf(id)});
    } */
}

