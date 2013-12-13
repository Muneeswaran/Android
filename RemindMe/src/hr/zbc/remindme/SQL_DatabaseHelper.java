package hr.zbc.remindme;


import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
//import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQL_DatabaseHelper extends SQLiteOpenHelper{
	
	SQLiteDatabase db;
	Cursor cur;

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "listManager";
	public static String TABLE_OF_QUOTES = "quotes";
	public static String TABLE_OF_LISTS = "lists";
	
	public static final String KEY_ID = "id";
	public static final String KEY_TEXT = "text";
	public static final String KEY_DATE_INSERTED = "date";
	public static final String KEY_WAS_USED = "used";
	
	public static final String KEY_LIST_NAME = "list_name";
	public static final String KEY_START_TIME = "start_time";
	public static final String KEY_END_TIME = "end_time";
	public static final String KEY_TIMES_OF_REPETITION = "times_of_repetition";
	public static final String KEY_DAILY_OR_WEEKLY = "daily_or_weekly";
	
	// Should the message appear every day or a few times a week
	public static final int DAILY = 0;
	public static final int WEEKLY = 1;
	// How many messages do you want to get daily or weekly
	public static final int DEFAULT_TIMES_OF_REPETITION = 1;
	// At which time of the day should the messages appear
	public static final int DEFAULT_START_TIME = 8;
	public static final int DEFAULT_END_TIME = 20;
	
	private static final String CREATE_TABLE_OF_QUOTES = "create  table " + TABLE_OF_QUOTES + " (" + KEY_ID + " integer primary key autoincrement, "
			+ KEY_TEXT + " text not null, " + KEY_LIST_NAME + " text not null, " + KEY_WAS_USED + " integer default 0)"; 
	
	private static final String CREATE_TABLE_OF_LISTS = "create table " + TABLE_OF_LISTS + " (" + KEY_ID + " integer primary key autoincrement, "
			+ KEY_LIST_NAME + " text not null, " + KEY_DAILY_OR_WEEKLY + " integer default " + DAILY + ", " + KEY_TIMES_OF_REPETITION + " integer default " 
			+ DEFAULT_TIMES_OF_REPETITION + ", " + KEY_START_TIME + " integer default " + DEFAULT_START_TIME + ", " + KEY_END_TIME + " integer default " 
			+ DEFAULT_END_TIME + ")";
	/*
	public SqlDatabaseHelper(Context context, String table_name) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		//this.TABLE_OF_QUOTES = table_name;
	}
	*/
	public SQL_DatabaseHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_OF_LISTS);
		db.execSQL(CREATE_TABLE_OF_QUOTES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS" + CREATE_TABLE_OF_LISTS);
		db.execSQL("DROP TABLE IF EXISTS" + CREATE_TABLE_OF_QUOTES);
		
		onCreate(db);
	}
	//---------------------------METHODS for TABLE_QUOTES-----------------------------------------------------------------------------------------//
	
	public void open(){
		db = this.getWritableDatabase();
	}
	
	public void close(){
		if (db != null && db.isOpen())
            db.close();
	}
	
	public void closeCursor(){
		cur.close();
	}
	
	public boolean addQuotes(String[] text, String listName){
		//SQLiteDatabase db = this.getWritableDatabase();

		try {
			// batch/bulk/multiple rows insert
			// http://tech.vg.no/2011/04/04/speeding-up-sqlite-insert-operations/
			String sql = "INSERT INTO " + TABLE_OF_QUOTES + " (" + KEY_TEXT
					+ ", " + KEY_LIST_NAME + ")  VALUES (?,?)";
			db.beginTransaction();
			SQLiteStatement stmt = db.compileStatement(sql);
			for (String str : text) {
				//values.put(MyDbHelperClass.COLUMN_QUOTE, str);
				stmt.bindString(1, str);
				stmt.bindString(2, listName);
				stmt.execute();
				stmt.clearBindings();
			}
			db.setTransactionSuccessful();
			db.endTransaction();
			//db.close();
			return true;
		} catch (Exception e) {
			//db.close();
			return false;
		}
	}
	
	public Cursor getQuotes(String title){
		//SQLiteDatabase db = this.getWritableDatabase();
		cur = db.query(TABLE_OF_QUOTES, new String[]{KEY_ID, KEY_TEXT, KEY_LIST_NAME}, KEY_LIST_NAME + " = ?", new String[]{title}, null, null, null);
		return cur;
	}
	
	public String getSingleQuote(String id){
		
		return null;
	}
	
	public void addSingleQuote(String quote, String listName){
		//SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_TEXT, "Neki tekst");
		values.put(KEY_LIST_NAME, listName);
		db.insert(TABLE_OF_QUOTES, null, values);
		
	}
	
	public void deleteSingleQuote(String quote, String title){
		//SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_OF_QUOTES, KEY_TEXT + " = ? and " + KEY_LIST_NAME + " = ?", new String[]{quote, title});
	}
	
	public void deleteTableOfQuotes(String tableName){
		//this.TABLE_OF_QUOTES = tableName;
		//SQLiteDatabase db = this.getWritableDatabase();
		
	}
	
	//--------------------------------------METHODS for TABLE_LISTS----------------------------------------------------------------------//
	
	public void addAllTitles(ArrayList<String> lists){
		
		
	}
	
	// I'm not sure why this method is still here
	// In the class MainActivity I am using the method which returns a Cursor
	public ArrayList<String> getAllTitles(){
		Log.i("GET ALL TITLES", "Here");
		//SQLiteDatabase db = this.getWritableDatabase();
		
		ArrayList<String> titles = new ArrayList<String>();
		cur = db.query(this.TABLE_OF_LISTS, null, null, null, null, null, null);
		int titleIndex = cur.getColumnIndex(KEY_LIST_NAME);
		if(cur.moveToFirst()){
			do{
				titles.add(cur.getString(titleIndex));
			}while(cur.moveToNext());
			
		}
		Log.i("GET ALL TITLES", "Here: " + titles.size());
		
		//cur.close();
		//db.close();
		
		return titles;
	}
	
	public Cursor getAllTitlesCursor(){
		//cur = db.query(TABLE_OF_LISTS, new String[]{KEY_ID, KEY_LIST_NAME}, null, null, null, null, null);
		return db.query(TABLE_OF_LISTS, null, null, null, null, null, null);
	}
	public void addTitle(String str){
		//SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_LIST_NAME, str);
		db.insert(this.TABLE_OF_LISTS, null, values);
		
		//db.close();
	}
	
	public String getTitle(String id){
		//SQLiteDatabase db = this.getReadableDatabase();
		
		return null;
	}
	
	public void deleteTitle(String title){
		//SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_OF_LISTS, KEY_LIST_NAME + " = ?", new String[]{title});
		db.delete(TABLE_OF_QUOTES, KEY_LIST_NAME + " = ?", new String[]{title});
		
	}
	
	public void deleteAllTitles(){
		//SQLiteDatabase db = this.getWritableDatabase();
		
	}
	
	public void updateTitle(String title, String repetitions, String begin, String end){
		ContentValues cv = new ContentValues();
		cv.put(KEY_TIMES_OF_REPETITION, repetitions);
		cv.put(KEY_START_TIME, begin);
		cv.put(KEY_END_TIME, end);
		db.update(TABLE_OF_LISTS, cv, KEY_LIST_NAME + " =?", new String[]{title});
	}
	
	public void updateTitle(String title, int repetitions, int begin, int end){
		ContentValues cv = new ContentValues();
		cv.put(KEY_TIMES_OF_REPETITION, repetitions);
		cv.put(KEY_START_TIME, begin);
		cv.put(KEY_END_TIME, end);
		Log.d("SQL", title + " " + repetitions + " " + begin + end);
		db.update(TABLE_OF_LISTS, cv, KEY_LIST_NAME + "=?", new String[]{title});
	}
	
	
	
}
