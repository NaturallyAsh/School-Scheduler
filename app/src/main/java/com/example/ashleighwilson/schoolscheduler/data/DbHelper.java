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

import com.example.ashleighwilson.schoolscheduler.MySchedulerApp;
import com.example.ashleighwilson.schoolscheduler.models.AgendaModel;
import com.example.ashleighwilson.schoolscheduler.models.RecordingModel;
import com.example.ashleighwilson.schoolscheduler.models.SubjectsModel;
import com.example.ashleighwilson.schoolscheduler.models.TimeTableModel;
import com.example.ashleighwilson.schoolscheduler.notes.Attachment;
import com.example.ashleighwilson.schoolscheduler.notes.Note;
import com.example.ashleighwilson.schoolscheduler.timetable.Event;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekViewEvent;
import com.example.ashleighwilson.schoolscheduler.utils.OnDatabaseChangedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper
{
    private Context mContext;
    private static DbHelper instance = null;

    public static synchronized DbHelper getInstance() {
        return getInstance(MySchedulerApp.getInstance());
    }

    public static synchronized DbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbHelper(context);
        }
        return instance;
    }

    private DbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }


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

    String[] spinnerColumns = new String[] {
            SpinnerEntry._ID,
            SpinnerEntry.COLUMN_SUBJECT,
            SpinnerEntry.COLUMN_SUB_ID
    };

    String[] agendaColumns = new String[] {
            AgendaEntry._ID,
            AgendaEntry.COLUMN_NAME,
            AgendaEntry.COLUMN_TITLE,
            AgendaEntry.COLUMN_DUEDATE,
            AgendaEntry.COLUMN_COLOR
    };

    String[] noteColumns = new String[] {
            NoteEntry.KEY_ID,
            NoteEntry.KEY_CREATION,
            NoteEntry.KEY_LAST_MOD,
            NoteEntry.KEY_TITLE,
            NoteEntry.KEY_CONTENT,
            NoteEntry.KEY_REMINDER,
            NoteEntry.KEY_RECURRENCE_RULE
    };

    private static final String TAG = DbHelper.class.getSimpleName();

    private static OnDatabaseChangedListener mOnDatabaseChangedListener;

    private static final String DATABASE_NAME = "school.db";
    private static final int DATABASE_VERSION = 51;
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

    public static final class NoteEntry implements BaseColumns
    {
        public final static String TABLE_NAME = "notes";
        public final static String KEY_ID = "note_id";
        public final static String KEY_CREATION = "creation";
        public final static String KEY_LAST_MOD = "last_mod";
        public final static String KEY_TITLE = "title";
        public final static String KEY_CONTENT = "content";
        public final static String KEY_REMINDER = "alarm";
        public final static String KEY_RECURRENCE_RULE = "recurrence_rule";
    }

    String SQL_CREATE_NOTES_TABLE = "CREATE TABLE " + NoteEntry.TABLE_NAME +
            " ("
            + NoteEntry.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NoteEntry.KEY_CREATION + " INTEGER, "
            + NoteEntry.KEY_LAST_MOD + " INTEGER, "
            + NoteEntry.KEY_TITLE + " TEXT, "
            + NoteEntry.KEY_CONTENT + " TEXT, "
            + NoteEntry.KEY_REMINDER + " TEXT, "
            + NoteEntry.KEY_RECURRENCE_RULE + " TEXT);";

    public static final class AttachEntry implements BaseColumns
    {
        public static final String TABLE_ATTACHMENTS = "attachments";
        // Attachments table columns
        public static final String KEY_ATTACHMENT_ID = "attachment_id";
        public static final String KEY_ATTACHMENT_URI = "uri";
        public static final String KEY_ATTACHMENT_NAME = "name";
        public static final String KEY_ATTACHMENT_SIZE = "size";
        public static final String KEY_ATTACHMENT_LENGTH = "length";
        public static final String KEY_ATTACHMENT_MIME_TYPE = "mime_type";
        public static final String KEY_ATTACHMENT_NOTE_ID = "note_id";
    }

    String SQL_CREATE_ATTACHMENT_TABLE = "CREATE TABLE " + AttachEntry.TABLE_ATTACHMENTS +
            " ("
            + AttachEntry.KEY_ATTACHMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + AttachEntry.KEY_ATTACHMENT_URI + " TEXT, "
            + AttachEntry.KEY_ATTACHMENT_NAME + " TEXT, "
            + AttachEntry.KEY_ATTACHMENT_SIZE + " INTEGER, "
            + AttachEntry.KEY_ATTACHMENT_LENGTH + " INTEGER, "
            + AttachEntry.KEY_ATTACHMENT_MIME_TYPE + " TEXT, "
            + AttachEntry.KEY_ATTACHMENT_NOTE_ID + " INTEGER);";


    public static final class SpinnerEntry implements BaseColumns
    {
        public final static String TABLE_NAME = "subject_spinner";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_SUBJECT = "name";
        public final static String COLUMN_SUB_ID = "sub_id";
    }

    String SQL_CREATE_SPINNER_TABLE = "CREATE TABLE " + SpinnerEntry.TABLE_NAME +
            " ("
            + SpinnerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SpinnerEntry.COLUMN_SUBJECT + " TEXT, "
            + SpinnerEntry.COLUMN_SUB_ID + " INTEGER);";

    public static final class AgendaEntry implements BaseColumns
    {
        public final static String TABLE_NAME = "agenda";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_TITLE = "title";
        public final static String COLUMN_DUEDATE = "date";
        public final static String COLUMN_COLOR = "color";
    }

    String SQL_CREATE_AGENDA_TABLE = "CREATE TABLE " + AgendaEntry.TABLE_NAME +
            " ("
            + AgendaEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + AgendaEntry.COLUMN_NAME + " TEXT, "
            + AgendaEntry.COLUMN_TITLE + " TEXT, "
            + AgendaEntry.COLUMN_DUEDATE + " TEXT, "
            + AgendaEntry.COLUMN_COLOR + " INTEGER);";


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_SUBJECTS_TABLE);
        db.execSQL(SQL_CREATE_RECORDINGS_TABLE);
        db.execSQL(SQL_CREATE_TIMETABLE_TABLE);
        db.execSQL(SQL_CREATE_SPINNER_TABLE);
        db.execSQL(SQL_CREATE_AGENDA_TABLE);
        db.execSQL(SQL_CREATE_NOTES_TABLE);
        db.execSQL(SQL_CREATE_ATTACHMENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + SchoolEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RecordEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TimeTableEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SpinnerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AgendaEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NoteEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AttachEntry.TABLE_ATTACHMENTS);
        onCreate(db);
    }

    public static void setOnDatabasedChangedListener(OnDatabaseChangedListener listener)
    {
        mOnDatabaseChangedListener = listener;
    }

    public SubjectsModel addClass(SubjectsModel model)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();

        ContentValues values = new ContentValues();
        values.put(SchoolEntry._ID, model.getId());
        values.put(SchoolEntry.COLUMN_TITLE, model.getmTitle());
        values.put(SchoolEntry.COLUMN_TEACHER, model.getmTeacher());
        values.put(SchoolEntry.COLUMN_ROOM, model.getmRoom());
        values.put(SchoolEntry.COLUMN_COLOR, model.getmColor());
        values.put(SchoolEntry.COLUMN_STARTTIME, model.getmStartTime());
        values.put(SchoolEntry.COLUMN_ENDTIME, model.getmEndTime());

        db.insertWithOnConflict(SchoolEntry.TABLE_NAME, SchoolEntry._ID, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.setTransactionSuccessful();
        db.endTransaction();

        return model;
    }

    public Cursor getAltSub()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(SchoolEntry.TABLE_NAME, allColumns, null, null,
                null, null, null);
    }

    public SubjectsModel getSubAt(int position)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(SchoolEntry.TABLE_NAME, allColumns, null, null,
                null, null, null);

        if (cursor.moveToPosition(position)) {
            SubjectsModel model = new SubjectsModel();
            model.setId(cursor.getInt(0));
            model.setmTitle(cursor.getString(1));
            model.setmTeacher(cursor.getString(2));
            model.setmRoom(cursor.getString(3));
            model.setmColor(cursor.getInt(4));
            model.setmStartTime(cursor.getString(5));
            model.setmEndTime(cursor.getString(6));
            cursor.close();
            return model;
        }
        return null;
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

        long id = db.insert(RecordEntry.TABLE_NAME, null, values);
        db.close();
        return id;
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

        long id = db.insert(TimeTableEntry.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public Cursor fetchEvents()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(TimeTableEntry.TABLE_NAME, timeTableColumns, null, null,
                null, null, null);
    }

    public void addToSpinner(String label, SubjectsModel model)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        //values.put(SpinnerEntry.COLUMN_SUBJECT_ID, model.getId());
        values.put(SpinnerEntry.COLUMN_SUBJECT, label);
        //values.put(SpinnerEntry.COLUMN_COLOR, model.getmColor());

        db.insertWithOnConflict(SpinnerEntry.TABLE_NAME, SpinnerEntry._ID, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<String> getAllLabels()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> labels = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + SpinnerEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst())
        {
            do {
                labels.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return labels;
    }

    public void getAssignmentByLabelAt(SubjectsModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + AgendaEntry.TABLE_NAME + " WHERE " +
                AgendaEntry.COLUMN_NAME + " = " + model.getmTitle();

        List<String> list = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            index++;
        }

    }

    public long addAgenda(AgendaModel model)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AgendaEntry.COLUMN_NAME, model.getClassName());
        values.put(AgendaEntry.COLUMN_TITLE, model.getAgendaTitle());
        values.put(AgendaEntry.COLUMN_DUEDATE, model.getDueDate());
        values.put(AgendaEntry.COLUMN_COLOR, model.getmColor());

        long id = db.insert(AgendaEntry.TABLE_NAME, null, values);
        db.close();

        return id;
    }

    public Cursor getAgenda()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(AgendaEntry.TABLE_NAME, agendaColumns, null, null,
                null, null, null);
    }

    public List<AgendaModel> getAllAgendas()
    {
        List<AgendaModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(AgendaEntry.TABLE_NAME, agendaColumns, null, null,
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int i = 0;
                AgendaModel model = new AgendaModel();
                model.setmId(cursor.getInt(i++));
                model.setClassName(cursor.getString(i++));
                model.setAgendaTitle(cursor.getString(i++));
                model.setDueDate(cursor.getString(i++));
                model.setmColor(cursor.getInt(i++));

                list.add(model);

            } while (cursor.moveToNext());
        }
        cursor.close();
        //db.close();
        return list;
    }

    public AgendaModel getAgendaAt(int position) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(AgendaEntry.TABLE_NAME, agendaColumns, null, null,
                null, null, null);

        if (cursor.moveToPosition(position)) {
            AgendaModel model = new AgendaModel();
            model.setmId(cursor.getInt(0));
            model.setClassName(cursor.getString(1));
            model.setAgendaTitle(cursor.getString(2));
            model.setDueDate(cursor.getString(3));
            model.setmColor(cursor.getInt(4));

            cursor.close();
            return model;
        }
        return null;
    }

    public long deleteAgenda(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(AgendaEntry.TABLE_NAME, AgendaEntry._ID + " =?",
                new String[]{String.valueOf(id)});
    }

    public Note updateNote(Note note, boolean updateLastMod)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();

        ContentValues values = new ContentValues();
        values.put(NoteEntry.KEY_ID, note.getID());
        values.put(NoteEntry.KEY_CREATION, note.getCreation() != null ? note.getCreation() :
                Calendar.getInstance().getTimeInMillis());
        values.put(NoteEntry.KEY_LAST_MOD, updateLastMod ? Calendar.getInstance().getTimeInMillis() :
                (note.getLastModification() != null ? note.getLastModification() :
                Calendar.getInstance().getTimeInMillis()));
        values.put(NoteEntry.KEY_TITLE, note.getTitle());
        values.put(NoteEntry.KEY_CONTENT, note.getContent());
        values.put(NoteEntry.KEY_REMINDER, note.getAlarm());
        values.put(NoteEntry.KEY_RECURRENCE_RULE, note.getRecurrenceRule());

        db.insertWithOnConflict(NoteEntry.TABLE_NAME, NoteEntry.KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);

        //Updating attachments
        List<Attachment> deletedAttachments = note.getAttachmentsListOld();
        for (Attachment attachment : note.getAttachmentsList()) {
            updateAttachment(note.get_id() != null ? note.get_id() : values.getAsLong(NoteEntry.KEY_CREATION),
                    attachment);
            deletedAttachments.remove(attachment);
        }
        //Remove from db deleted attachments
        for (Attachment attachmentDeleted : deletedAttachments) {
            db.delete(AttachEntry.TABLE_ATTACHMENTS, AttachEntry.KEY_ATTACHMENT_ID + " = ?",
                    new String[]{String.valueOf(attachmentDeleted.getId())});
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        note.setCreation(note.getCreation() != null ? note.getCreation() :
            values.getAsLong(NoteEntry.KEY_CREATION));
        note.setLastModification(values.getAsLong(NoteEntry.KEY_LAST_MOD));

        //db.close();

        return note;
    }

    public Note getNoteAt(int position) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(NoteEntry.TABLE_NAME, noteColumns, null, null,
                null, null, null);

        if (cursor.moveToPosition(position)) {
                Note note = new Note();
                note.setID(cursor.getLong(0));
                note.setCreation(cursor.getLong(1));
                note.setLastModification(cursor.getLong(2));
                note.setTitle(cursor.getString(3));
                note.setContent(cursor.getString(4));
                note.setAlarm(cursor.getString(5));
                note.setRecurrenceRule(cursor.getString(6));

                note.setAttachmentsList(getNoteAttachment(note));

                cursor.close();
                return note;
            }

            return null;
    }

    public Cursor getAltNotes() {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(NoteEntry.TABLE_NAME, noteColumns, null, null,
                null, null, null);
    }

    public List<Note> getEveryNote() {
        return getAllNotes();
    }

    public List<Note> getAllNotes()
    {
        List<Note> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(NoteEntry.TABLE_NAME, noteColumns, null, null,
                null, null, null);

       if (cursor.moveToFirst()) {
            do {
                int i = 0;
                Note note = new Note();
                note.set_id(cursor.getLong(i++));
                note.setCreation(cursor.getLong(i++));
                note.setLastModification(cursor.getLong(i++));
                note.setTitle(cursor.getString(i++));
                note.setContent(cursor.getString(i++));
                note.setAlarm(cursor.getString(i++));
                note.setRecurrenceRule(cursor.getString(i++));
                list.add(note);

                note.setAttachmentsList(getNoteAttachment(note));

            } while (cursor.moveToNext());
        }
        cursor.close();
        //db.close();
        return list;
    }

    public int getNoteCount()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {NoteEntry.KEY_ID};

        Cursor cursor = db.query(NoteEntry.TABLE_NAME, projection, null, null,
                null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void deleteNote(long id) {
        SQLiteDatabase db = this.getWritableDatabase();


        db.delete(NoteEntry.TABLE_NAME, NoteEntry.KEY_ID + " = ?",
                new String[]{String.valueOf(id)});

        db.close();
    }

    public boolean deleteNoteProcess(Note note) {
        return deleteNoteProcess(note, false);
    }

    private boolean deleteNoteProcess(Note note, boolean keepAttachments) {
        int deletedNotes;
        boolean result = true;
        SQLiteDatabase db = this.getWritableDatabase();

        deletedNotes = db.delete(NoteEntry.TABLE_NAME, NoteEntry._ID + " = ?", new String[]
                {String.valueOf(note.get_id())});
        if (!keepAttachments) {
            int deletedAttachments = db.delete(AttachEntry.TABLE_ATTACHMENTS, AttachEntry.KEY_ATTACHMENT_NOTE_ID +
             " = ?", new String[]{String.valueOf(note.get_id())});
            result = result && deletedAttachments == note.getAttachmentsList().size();
        }

        result = result && deletedNotes == 1;
        return result;
    }


    public Attachment updateAttachment(long noteId, Attachment attachment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AttachEntry.KEY_ATTACHMENT_ID, attachment.getId() != null ? attachment.getId() :
            Calendar.getInstance().getTimeInMillis());
        values.put(AttachEntry.KEY_ATTACHMENT_NOTE_ID, noteId);
        values.put(AttachEntry.KEY_ATTACHMENT_URI, attachment.getUri().toString());
        values.put(AttachEntry.KEY_ATTACHMENT_MIME_TYPE, attachment.getMime_type());
        values.put(AttachEntry.KEY_ATTACHMENT_NAME, attachment.getName());
        values.put(AttachEntry.KEY_ATTACHMENT_SIZE, attachment.getSize());
        values.put(AttachEntry.KEY_ATTACHMENT_LENGTH, attachment.getLength());

        db.insertWithOnConflict(AttachEntry.TABLE_ATTACHMENTS, AttachEntry.KEY_ATTACHMENT_ID, values,
                SQLiteDatabase.CONFLICT_REPLACE);
        return attachment;
    }

    public ArrayList<Attachment> getNoteAttachment(Note note) {
        ArrayList<Attachment> attachmentList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + AttachEntry.TABLE_ATTACHMENTS + " WHERE " +
                AttachEntry.KEY_ATTACHMENT_NOTE_ID + " = " + note.get_id();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                Attachment mAttachment;
                do {
                    mAttachment = new Attachment(
                            cursor.getLong(0),
                            Uri.parse(cursor.getString(1)),
                            cursor.getString(2),
                            cursor.getInt(3),
                            cursor.getLong(4),
                            cursor.getString(5));
                    attachmentList.add(mAttachment);
                }while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return attachmentList;

    }

    public ArrayList<Attachment> getAttachments() {
        ArrayList<Attachment> attachmentList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(AttachEntry.TABLE_ATTACHMENTS, null);

            if (cursor.moveToFirst()) {
                Attachment mAttachment;
                do {
                    mAttachment = new Attachment(
                            cursor.getLong(0),
                            Uri.parse(cursor.getString(1)),
                            cursor.getString(2),
                            cursor.getInt(3),
                            cursor.getLong(4),
                            cursor.getString(5));
                    attachmentList.add(mAttachment);
                }while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return attachmentList;
    }

    public long getNoteById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(NoteEntry.TABLE_NAME, noteColumns);

        while (cursor.moveToNext()) {
            id = cursor.getLong(cursor.getColumnIndex(NoteEntry.KEY_ID));
        }
        cursor.close();
        return id;
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

