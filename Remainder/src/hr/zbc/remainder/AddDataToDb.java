package hr.zbc.remainder;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class AddDataToDb {
	private SQLiteDatabase database;
	private MyDbHelperClass dbHelper;
	private String[] allColumns = {MyDbHelperClass.COLUMN_ID, MyDbHelperClass.COLUMN_QUOTE, MyDbHelperClass.COLUMN_USED};
	Context ctx;
	
	public AddDataToDb(Context context) {
		dbHelper = new MyDbHelperClass(context);
		this.ctx = ctx;
	}
	
	public void open(){
		database = dbHelper.getWritableDatabase();
	}
	
	public void close(){
		dbHelper.close();
	}
	
	public void addBucketList(String bckList){
		database.delete(MyDbHelperClass.TABLE_BUCKET_LIST, null, null);
		String[] oneByOne = bckList.split("\n");
		
		// batch/bulk/multiple rows insert
		// http://tech.vg.no/2011/04/04/speeding-up-sqlite-insert-operations/
		String sql = "INSERT INTO " + MyDbHelperClass.TABLE_BUCKET_LIST + " (" + MyDbHelperClass.COLUMN_QUOTE + ")  VALUES (?)";
		database.beginTransaction();
		SQLiteStatement stmt = database.compileStatement(sql);

		for (String str:oneByOne) {
			//values.put(MyDbHelperClass.COLUMN_QUOTE, str);
		    stmt.bindString(1, str);
		    stmt.execute();
		    stmt.clearBindings();
		}
		 
		database.setTransactionSuccessful();
		database.endTransaction();
		
		/*
		for (String str:oneByOne) {
			ContentValues values = new ContentValues();
			values.put(MyDbHelperClass.COLUMN_QUOTE, str);
			database.insert(MyDbHelperClass.TABLE_BUCKET_LIST, null, values);
		}*/
		
	}
	
	public boolean deleteQuote(String id){
		return database.delete(MyDbHelperClass.TABLE_BUCKET_LIST, MyDbHelperClass.COLUMN_ID + " = " + id, null) > 0;
	}
	
	public Cursor getBucketList(){
		//List<String> quotes = new ArrayList<String>();
		Cursor cursor = database.query(MyDbHelperClass.TABLE_BUCKET_LIST, allColumns, null, null, null, null, null);
		/*if(cursor.moveToFirst()){
			int quoteIndex = cursor.getColumnIndex(MyDbHelperClass.COLUMN_QUOTE);
			do{
				quotes.add(cursor.getString(quoteIndex));
			}while(cursor.moveToNext());
		}*/
		return cursor;
	}
	
	public Cursor getUnusedBucketList(){
		
		return database.query(MyDbHelperClass.TABLE_BUCKET_LIST, allColumns, MyDbHelperClass.COLUMN_USED + " = 0", null, null, null, null);
	}
	
	public boolean setUsed(String id){
		ContentValues values = new ContentValues();
		values.put(MyDbHelperClass.COLUMN_USED, 1);
		
		return database.update(MyDbHelperClass.TABLE_BUCKET_LIST, values, MyDbHelperClass.COLUMN_ID + " = " + id, null) > 0;
	}
	
	// treba napraviti moguænost za ruèno ureðivanje
	public boolean updateQuote(String id){
		
		return false;
	}

}
