package hr.zbc.remainder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class SQLAddDataToDb {
	private SQLiteDatabase database;
	private SQLMyDbHelperClass dbHelper;
	private String[] allColumns = {SQLMyDbHelperClass.COLUMN_ID, SQLMyDbHelperClass.COLUMN_QUOTE, SQLMyDbHelperClass.COLUMN_USED};
	
	public SQLAddDataToDb(Context context) {
		dbHelper = new SQLMyDbHelperClass(context);
	}
	
	public void open(){
		database = dbHelper.getWritableDatabase();
	}
	
	public void close(){
		dbHelper.close();
	}
	
	public void addBucketList(String bckList){
		database.delete(SQLMyDbHelperClass.TABLE_BUCKET_LIST, null, null);
		String[] oneByOne = bckList.split("\n");
		
		// batch/bulk/multiple rows insert
		// http://tech.vg.no/2011/04/04/speeding-up-sqlite-insert-operations/
		String sql = "INSERT INTO " + SQLMyDbHelperClass.TABLE_BUCKET_LIST + " (" + SQLMyDbHelperClass.COLUMN_QUOTE + ")  VALUES (?)";
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
	}
	
	public boolean deleteQuote(String id){
		return database.delete(SQLMyDbHelperClass.TABLE_BUCKET_LIST, SQLMyDbHelperClass.COLUMN_ID + " = " + id, null) > 0;
	}
	
	public Cursor getBucketList(){
		Cursor cursor = database.query(SQLMyDbHelperClass.TABLE_BUCKET_LIST, allColumns, null, null, null, null, null);
		return cursor;
	}
	
	public Cursor getUnusedBucketList(){
		return database.query(SQLMyDbHelperClass.TABLE_BUCKET_LIST, allColumns, SQLMyDbHelperClass.COLUMN_USED + " = 0", null, null, null, null);
	}
	
	public boolean setUsedQuote(String id){
		ContentValues values = new ContentValues();
		values.put(SQLMyDbHelperClass.COLUMN_USED, 1);
		
		return database.update(SQLMyDbHelperClass.TABLE_BUCKET_LIST, values, SQLMyDbHelperClass.COLUMN_ID + " = " + id, null) > 0;
	}
	
	// treba napraviti moguænost za ruèno ureðivanje
	public boolean updateQuote(String id){
		
		return false;
	}

}
