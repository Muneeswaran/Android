package hr.zbc.remindme;

import java.util.ArrayList;

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

public class ActMainActivity extends Activity implements OnItemClickListener, OnCreateContextMenuListener{
	
	public static int CODE_PICK = 1024;
	public static int CODE_FIND = 1023;
	ArrayList<String> titles;
	Cursor cur;
	SqlDatabaseHelper db;
	
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
		db = new SqlDatabaseHelper(this);
		db.open();
		titles = new ArrayList<String>();
		cur = db.getAllTitlesCursor();
		if(cur.getCount() > 0){
			int index = cur.getColumnIndex(SqlDatabaseHelper.KEY_LIST_NAME);
			if(cur.moveToFirst()){
				do{
					titles.add(cur.getString(index));
				}while(cur.moveToNext());
			}
			loadListView(titles);
		}
		db.closeCursor();
		db.close();
	}
	
	// Adding items (list titles) to ListView
	private void loadListView(ArrayList<String> items){
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, R.layout.row_find_files, R.id.tvRowFindFilesNames, items);
		lv.setAdapter(mAdapter);
		
	}
	
	// After the list was imported
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("MAIN ACTIVITY", data.getExtras().getBoolean("list_added") + "");
		Log.i("MAIN ACTIVITY", "ReqCode: " + Integer.toString(requestCode));
		if((resultCode == RESULT_OK) && (data.getExtras().getBoolean("list_added"))){
			Log.i("MAIN ACTIVITY", "ReqCode: " + Integer.toString(requestCode));
			if(requestCode == CODE_PICK){
				getTitleList();
			}else if (requestCode == CODE_FIND) {
				getTitleList();
			}
		}
	}
	
	// Buttons
	public void importClick(View v){
		switch (v.getId()) {
		case R.id.bMainPick:
			startActivityForResult(new Intent(this, ActPickFiles.class).putExtra("request_code", CODE_PICK), CODE_PICK);
			break;
		case R.id.bMainFind:
			startActivityForResult(new Intent(this, ActPickFiles.class).putExtra("request_code", CODE_FIND), CODE_FIND);
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
		Log.d("MAIN ACTIVITY", titles.get(position));
		startActivity(new Intent(this, ActListDetails.class).putExtra("title", titles.get(position)));
		
	}

}
