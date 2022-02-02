package ie.spiddalsoftware.gearoid.eventtimer;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;

        import java.util.LinkedList;
        import java.util.List;

/**
 * Created by sarah on 13/12/2015.
 */
public class CompHelper extends SQLiteOpenHelper {
    private static final int database_VERSION = 1;
    private static final String database_NAME = "TIMINGS2_DB";
    private static final String table_NAME = "timing";
    private static final String timing_ID = "_id";
    private static final String timing_ENTRY_NUMBER = "entry";
    private static final String timing_EVENT_TYPE = "type";
    private static final String timing_LOCATION = "location";
    private static final String timing_VEHICLE_CLASS = "class";
    private static final String timing_VEHICLE_SIZE = "size";
    private static final String timing_WHEN = "time";

    private static final String[] COLUMNS = {timing_ID,timing_EVENT_TYPE,timing_ENTRY_NUMBER,timing_LOCATION,
            timing_WHEN, timing_VEHICLE_CLASS, timing_VEHICLE_SIZE};

    public CompHelper(Context context) {
        super(context, database_NAME, null, database_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // form string to create table
        String CREATE_TABLE = "CREATE TABLE " + table_NAME +" ( " + timing_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + timing_EVENT_TYPE+" TEXT, "+timing_ENTRY_NUMBER+" TEXT, "+timing_LOCATION+" TEXT, "
                + timing_WHEN+" TEXT, "+timing_VEHICLE_CLASS+" TEXT, " +timing_VEHICLE_SIZE+" TEXT )";
        // Create the table
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop tAsble
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+table_NAME);
        }
        this.onCreate(db);
    }

    // add new entry
    public void addEntry(String etype, String number, String loc, String when, String vclass, String vsize) {
        // get reference to this database
        SQLiteDatabase db = this.getWritableDatabase();
        // create the record to insert
        ContentValues values = new ContentValues();
        values.put(timing_EVENT_TYPE, etype);
        values.put(timing_ENTRY_NUMBER, number);
        values.put(timing_LOCATION, loc);
        values.put(timing_WHEN, when);
        values.put(timing_VEHICLE_CLASS,vclass);
        values.put(timing_VEHICLE_SIZE,vsize);
        // insert into the table
        db.insert(table_NAME,null,values);
        db.close();
    }

    public String retrieveEntry(int id) {
        // get referenceto the dbase
        SQLiteDatabase db = this.getWritableDatabase();
        // do the query
        Cursor cursor = db.query(table_NAME, COLUMNS, " _id = ?",
                new String[] { String.valueOf(id)},null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();// parse first match. should only be one as id is unique
        db.close();
        return cursor.getString(2)+cursor.getString(3)+cursor.getString(4)+cursor.getString(5)
                +cursor.getString(6)+cursor.getString(7);
    }

    public boolean isAlreadyEntered(String entryNo) {
        boolean search = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(table_NAME, COLUMNS,timing_ENTRY_NUMBER+" = ?",
                new String[] {entryNo }, null, null, null, null);
        if (cursor.moveToNext()) {
            search = true;
        } else search = false;
        db.close();
        return search;
    }

    public Cursor getEntryCursor(String entryNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(table_NAME, COLUMNS, timing_ENTRY_NUMBER + " = ?",
                new String[]{entryNo}, null, null, null, null);
        return cursor;
    }

    public List<String> getEntry(String entryNo) {
        List<String> records = new LinkedList<>();
        String a= "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(table_NAME, COLUMNS, timing_ENTRY_NUMBER + " = ?",
                new String[]{entryNo}, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                a = cursor.getString(1) + "," + cursor.getString(2) + "," + cursor.getString(3) + ","
                        + cursor.getString(4) + "," + cursor.getString(5) + "," + cursor.getString(6);
                records.add(a);
            } while (cursor.moveToNext());
        }
        db.close();
        return records;
    }

    public Cursor getAllinCursor() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + table_NAME;
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public List<String> getAllRecords() {
        List<String> records = new LinkedList<>();
        String record = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + table_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                record = cursor.getString(1) + "," + cursor.getString(2) + "," + cursor.getString(3) + ","
                        + cursor.getString(4) + "," + cursor.getString(5) + "," + cursor.getString(6);
                records.add(record);
            } while (cursor.moveToNext());
        }
        db.close();
        return records;
    }

    public List<String> getAllForDisplay() {
        List<String> records = new LinkedList<>();
        String record = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + table_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                record = cursor.getString(1) + "," + cursor.getString(2) + "," + cursor.getString(3) + ","
                        + cursor.getString(4);
                records.add(record);
            } while (cursor.moveToNext());
        }
        db.close();
        return records;
    }
}
