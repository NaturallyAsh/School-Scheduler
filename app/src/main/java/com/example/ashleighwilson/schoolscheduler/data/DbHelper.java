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

import com.example.ashleighwilson.schoolscheduler.models.SubjectsModel;

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

    private static final String TAG = DbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "school.db";
    private static final int DATABASE_VERSION = 3;
    public static final String CONTENT_AUTHORITY = "com.example.ashleighwilson.schoolscheduler";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_SCHOOL = "schoolscheduler";

    public static final class SchoolEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SCHOOL);
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_SCHOOL;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_SCHOOL;

        public final static String TABLE_NAME = "subjects";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_TITLE = "title";
        public final static String COLUMN_TEACHER = "teacher";
        public final static String COLUMN_ROOM = "room";
        public final static String COLUMN_COLOR = "color";
    }

    public DbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String SQL_CREATE_SUBJECTS_TABLE = "CREATE TABLE " + SchoolEntry.TABLE_NAME +
                " ("
                + SchoolEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SchoolEntry.COLUMN_TITLE + " TEXT, "
                + SchoolEntry.COLUMN_TEACHER + " TEXT, "
                + SchoolEntry.COLUMN_ROOM + " TEXT, "
                + SchoolEntry.COLUMN_COLOR + " INTEGER);";

        db.execSQL(SQL_CREATE_SUBJECTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + SchoolEntry.TABLE_NAME);
        onCreate(db);
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

    public long count()
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

    public int update(int id, String title, String teacher, String room, int color)
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

    public long delete(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

            return db.delete(SchoolEntry.TABLE_NAME, SchoolEntry._ID + " =?",
                    new String[]{String.valueOf(id)});

    }
}
