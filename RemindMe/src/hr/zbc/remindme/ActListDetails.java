package hr.zbc.remindme;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.database.Cursor;
import android.os.Build;

public class ActListDetails extends Activity implements OnItemClickListener{

	SqlDatabaseHelper db = new SqlDatabaseHelper(this);
	ListView lv;
	String title;
	ArrayList<String> quotes;
	Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_details);
		// Show the Up button in the action bar.
		setupActionBar();
		
		lv = (ListView) findViewById(R.id.lvListDetails);
		lv.setOnItemClickListener(this);
		registerForContextMenu(lv);
		title = getIntent().getExtras().getString("title");
		getAllQuotes();
		
	}

	private void getAllQuotes() {
		db.open();
		quotes = new ArrayList<String>();
		cursor = db.getQuotes(title);
		int index = cursor.getColumnIndex("text");
		Log.d("LIST DETAILS", cursor.getCount() + " Index: " + index);
		if(cursor.moveToFirst()){
			do{
				Log.d("LIST DETAILS", cursor.getString(index));
				quotes.add(cursor.getString(index));
			}while(cursor.moveToNext());
		}
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, R.layout.row_find_files, R.id.tvRowFindFilesNames, quotes);
		lv.setAdapter(mAdapter);
		db.closeCursor();
		db.close();
	}


	public void addNote(View v){
		switch (v.getId()) {
		case R.id.bListDetailsAddNote:
			
			break;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(R.string.select___);  
        menu.add(0, v.getId(), 0, R.string.edit); 
        menu.add(0, v.getId(), 0, R.string.delete); 
        
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		//return super.onContextItemSelected(item);
		if(item.getTitle().equals(getResources().getString(R.string.edit))){
			
		}else if(item.getTitle().equals(getResources().getString(R.string.delete))){
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			db.open();
			db.deleteSingleQuote(quotes.get(info.position), title);
			getAllQuotes();
		}
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		// TODO Auto-generated method stub
		
	}



	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
