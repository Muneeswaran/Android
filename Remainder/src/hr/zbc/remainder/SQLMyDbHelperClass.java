package hr.zbc.remainder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLMyDbHelperClass extends SQLiteOpenHelper{

	public static final String TABLE_BUCKET_LIST = "bucket_list";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_QUOTE = "quote";
	public static final String COLUMN_USED = "used";
	
	private static final String DATABASE_NAME = "commments.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_BUCKET_LIST + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_QUOTE
			+ " text not null, " + COLUMN_USED + " integer default 0);";

	public SQLMyDbHelperClass(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	   /*
		Log.w(SQLMyDbHelperClass.class.getName(),
	            "Upgrading database from version " + oldVersion + " to "
	                + newVersion + ", which will destroy all old data");
	    */
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUCKET_LIST);
	        onCreate(db);
	}

}
