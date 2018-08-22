package com.example.ashleighwilson.schoolscheduler.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.example.ashleighwilson.schoolscheduler.models.SubjectsModel;

public class DbHelper extends SQLiteOpenHelper
{
    private static DbHelper dbHelper = null;
    String[] allColumns = new String[] {
            SchoolEntry._ID,
            SchoolEntry.COLUMN_TITLE,
            SchoolEntry.COLUMN_TEACHER
    };

    private static final String DATABASE_NAME = "school.db";
    private static final int DATABASE_VERSION = 1;

    public static final class SchoolEntry implements BaseColumns
    {
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
                + SchoolEntry.COLUMN_COLOR + " TEXT)";

        db.execSQL(SQL_CREATE_SUBJECTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + SchoolEntry.TABLE_NAME);
        onCreate(db);
    }

    public void addClass(SubjectsModel model)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SchoolEntry.COLUMN_TITLE, model.getmTitle());
        values.put(SchoolEntry.COLUMN_TEACHER, model.getmTeacher());

        db.insert(SchoolEntry.TABLE_NAME, null, values);
        db.close();
    }

    public Cursor getSubject()
    {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(SchoolEntry.TABLE_NAME, allColumns, null, null,
                null, null, null);

        return cursor;
    }
}
