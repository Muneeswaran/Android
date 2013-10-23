package hr.diplomski.organizator;


import java.util.ArrayList;

import android.os.Bundle;
import android.app.ListActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;



public class Biljeske extends ListActivity {

	private static final int STVORI = 0;
	private static final int UREDI = 1;

	//private int mNoteNumber = 1;
	AdapterContextMenuInfo info;
	
	String order = "date desc";
	
	private BiljeskeDbAdapter mDbHelper;
	Cursor notesCursor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_biljeske);
		// Show the Up button in the action bar.
		setupActionBar();
		registerForContextMenu(getListView());
		
		// Kreira objekt mDbHelper i tipku
		podesiSve();
		dohvatiPodatke();
		//registerForContextMenu(getListView());
		
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		dohvatiPodatke();
	}



	private void podesiSve() {
		mDbHelper = new BiljeskeDbAdapter(this);
		mDbHelper.open();
	}

	private void novaBiljeska(int code) {
		Intent i = new Intent(this, NovaBiljeska.class);
        startActivityForResult(i, code);   
	}
	
	// Pogledati više o TextViewu: http://developer.android.com/reference/android/widget/TextView.html
	// Omoguæiti šarena polja
	private void dohvatiPodatke() {
		ArrayList<BiljeskeVrijednosti> zaListu = new ArrayList<BiljeskeVrijednosti>();
		// Get all of the notes from the database and create the item list
        notesCursor = mDbHelper.dohvatiSveBiljeske(order);
        if(notesCursor.moveToFirst()){
        	do{
                BiljeskeVrijednosti podaci = new BiljeskeVrijednosti();
        		podaci.setNaslov(notesCursor.getString(notesCursor.getColumnIndex(BiljeskeDbAdapter.KEY_TITLE)));
        		podaci.setDatum(notesCursor.getLong(notesCursor.getColumnIndex(BiljeskeDbAdapter.KEY_DATE)));
        		zaListu.add(podaci);
        	}while(notesCursor.moveToNext());
        }
        BiljeskeListAdapter adapter = new BiljeskeListAdapter(zaListu, this);
        setListAdapter(adapter);
		
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
	
	
	// Možda napraviti Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.biljeske, menu);
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
		case R.id.mAbc:
			order = "title collate nocase";
			dohvatiPodatke();
			break;
		case R.id.mKreirano:
			order = "null";
			dohvatiPodatke();
			break;
		case R.id.mPromjena:
			order = "date desc";
			dohvatiPodatke();
			break;
		case R.id.mNovaBiljeska:
			novaBiljeska(STVORI);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// Pogledati još o ContextMenu http://developer.android.com/guide/topics/ui/menus.html#FloatingContextMenu
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	    getMenuInflater().inflate(R.menu.biljeske_context, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		//Log.e("Context", "Ušao u context menu");
		switch(item.getItemId()) {
        case R.id.mUredi:

        	info = (AdapterContextMenuInfo) item.getMenuInfo();
        	notesCursor.moveToPosition((int) info.id);
        	Intent i = new Intent(this, NovaBiljeska.class);
        	i.putExtra(BiljeskeDbAdapter.KEY_ROWID, notesCursor.getLong(notesCursor.getColumnIndex(BiljeskeDbAdapter.KEY_ROWID)));
        	startActivityForResult(i, UREDI);
        	break;
        case R.id.mObrisi:
        	info = (AdapterContextMenuInfo) item.getMenuInfo();
        	//_id = info.id;
        	notesCursor.moveToPosition((int) info.id);
        	mDbHelper.obrisiBiljesku(notesCursor.getLong(notesCursor.getColumnIndex(BiljeskeDbAdapter.KEY_ROWID)));
        	dohvatiPodatke();
        	break;
    }
		return super.onContextItemSelected(item);
	}
	
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        notesCursor.moveToPosition((int) id);
        //Log.e("Naslov", notesCursor.getString(notesCursor.getColumnIndex(BiljeskeDbAdapter.KEY_TITLE)));
        Intent i = new Intent(this, NovaBiljeska.class);
        i.putExtra(BiljeskeDbAdapter.KEY_ROWID, notesCursor.getLong(notesCursor.getColumnIndex(BiljeskeDbAdapter.KEY_ROWID)));
        startActivityForResult(i, 1);
        
    }

}

