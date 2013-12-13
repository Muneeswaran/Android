package hr.zbc.remindme;

import java.util.ArrayList;

import android.R.integer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class A_MainActivity extends Activity implements OnItemClickListener, OnCreateContextMenuListener{
	
	public static final int CODE_PICK = 1024;
	public static final int CODE_FIND = 1023;
	public static final int SETTINGS_MENU = 1025;
	
	ArrayList<String> titles;
	Cursor cur;
	SQL_DatabaseHelper db = new SQL_DatabaseHelper(this);;
	
	ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		
		// Initializing the ListView
		lv = (ListView) findViewById(R.id.lvMainLists);
		lv.setOnItemClickListener(this);
		registerForContextMenu(lv);
		
		getTitleList();
	}
	
	// SQL query (only for titles)
	private void getTitleList(){
		prepareCursorFromDb();
		fillArrayAndLoadList();
		//db.closeCursor();
		db.close();
	}

	private void prepareCursorFromDb() {
		db.open();
		titles = new ArrayList<String>();
		cur = db.getAllTitlesCursor();
	}

	private void fillArrayAndLoadList() {
		if(cur.getCount() > 0){
			int index = cur.getColumnIndex(SQL_DatabaseHelper.KEY_LIST_NAME);
			if(cur.moveToFirst()){
				do{
					titles.add(cur.getString(index));
				}while(cur.moveToNext());
			}
			loadListView(titles);
		}
	}

	// Adding items (list titles) to ListView
	private void loadListView(ArrayList<String> items){
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, R.layout.row_find_files, R.id.tvRowFindFilesNames, items);
		lv.setAdapter(mAdapter);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			// If returned from file picking
			if (data.getExtras().getBoolean("list_added")) {
				if (requestCode == CODE_PICK) {
					getTitleList();
				} else if (requestCode == CODE_FIND) {
					getTitleList();
				}
			} 
			// If returned from setting the alarm
			if(requestCode == SETTINGS_MENU){
				// Number of reminders, interval beginning and end
				updateTitleSettings(data.getExtras().getInt("position"), data.getExtras().getInt("number_of_reminders"), data.getExtras().getInt("begin"), data.getExtras().getInt("end"));
			}
			
		}
	}
	
	private void updateTitleSettings(int position, int reminders, int begin, int end) {
		db.open();
		db.updateTitle(titles.get(position), reminders, begin, end);
		db.close();
		startAlarm(position, reminders, begin, end);
	}

	private void startAlarm(int position, int reminders, int begin, int end) {
		Log.i("POSITION", ""+ position);
		cur.moveToPosition(position);
		C_DailyAlarm scheduler = new C_DailyAlarm(this, 
				new DAO_AlarmDetails(cur.getLong(cur.getColumnIndex(SQL_DatabaseHelper.KEY_ID)), 
						cur.getString(cur.getColumnIndex(SQL_DatabaseHelper.KEY_LIST_NAME)), cur.getInt(cur.getColumnIndex(SQL_DatabaseHelper.KEY_ID)), 
						reminders, begin, end, cur.getInt(cur.getColumnIndex(SQL_DatabaseHelper.KEY_DAILY_OR_WEEKLY))));
		scheduler.startAlarm();
	}

	// Buttons
	public void importClick(View v){
		switch (v.getId()) {
		case R.id.bMainPick:
			startActivityForResult(new Intent(this, A_PickFiles.class).putExtra("request_code", CODE_PICK), CODE_PICK);
			break;
		case R.id.bMainFind:
			startActivityForResult(new Intent(this, A_PickFiles.class).putExtra("request_code", CODE_FIND), CODE_FIND);
			//new C_DailyAlarm(this, new DaoAlarmDetails(2, "Naslov", 102, 12, 13, 3, 0)).startAlarm();
			break;
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(R.string.select___);  
        menu.add(0, v.getId(), 0, R.string.start);  
        menu.add(0, v.getId(), 0, R.string.cancel); 
        menu.add(0, v.getId(), 0, R.string.edit); 
        menu.add(0, v.getId(), 0, R.string.delete); 
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		//return super.onContextItemSelected(item);
		
		// How to get the position of the selected item
		// http://learnandroideasily.blogspot.com/2013/01/creating-context-menu-in-android.html
		//AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		//  info.position will give the index of selected item
		//intIndexSelected=info.position   
		
		// http://www.stealthcopter.com/blog/2010/04/android-context-menu-example-on-long-press-gridview/
		// This probably gets the id from menu (if you add this elements through an xml layout)
		// item.getItemId();
		if (item.getTitle().equals(getResources().getString(R.string.start))){
			
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			startActivityForResult(new Intent(this, A_SettingsDailyAlarm.class).putExtra("position", info.position), SETTINGS_MENU);
			
		}else if (item.getTitle().equals(getResources().getString(R.string.cancel))) {
			
		}else if (item.getTitle().equals(getResources().getString(R.string.edit))) {
			
		}else if (item.getTitle().equals(getResources().getString(R.string.delete))) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			db.open();
			db.deleteTitle(titles.get(info.position));
			getTitleList();
		}
		
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		startActivity(new Intent(this, A_ListDetails.class).putExtra("title", titles.get(position)));
		
	}

}
