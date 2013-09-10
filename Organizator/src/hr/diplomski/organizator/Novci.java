package hr.diplomski.organizator;

import java.util.Date;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.database.Cursor;
import android.os.Build;
import android.provider.CalendarContract;

public class Novci extends Activity {
	
	ListView listaNovca;
	Uri uriCalendar, uriEvent;
	Cursor kursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_novci);
		// Show the Up button in the action bar.
		setupActionBar();
		
		init();
		verzijaAndroida();
		popisTroskova();
		
		
	}

	private void popisTroskova() {
		String[] projection = new String[] { "_id", "event_id", "title", "begin", "end", "calendar_id", "description"
				, "eventLocation", "dtstart", "lastDate", "dtend"};
		kursor = null;
		kursor = dohvatiKursor(projection, null, null);
		
		int title = kursor.getColumnIndex("title");
		int description = kursor.getColumnIndex("description");
		int begin = kursor.getColumnIndex("begin");
		
		String naslov;
		String opis;
		
		if(kursor.moveToFirst()){
			do{
				naslov = kursor.getString(title);
				opis = kursor.getString(description);
				try {
					String[] tempNaslov = naslov.split(" ");
					if(tempNaslov[1].equals("kn")){
						Double iznos = Double.parseDouble(tempNaslov[0]);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}while(kursor.moveToNext());
		}
		
	}

	@SuppressLint("NewApi")
	private void verzijaAndroida() {
		if (android.os.Build.VERSION.SDK_INT <= 7) {
			uriCalendar = Uri.parse("content://calendar/calendars");
			uriEvent = Uri.parse("content://calendar/events");
			// Izgleda da je Froyo (API 8) na istom Uri-ju
		}
		else if (android.os.Build.VERSION.SDK_INT <= 10) {
			uriCalendar = Uri.parse("content://com.android.calendar/events");
			uriEvent = Uri.parse("content://com.android.calendar/events");
		}else {
			uriCalendar = CalendarContract.Calendars.CONTENT_URI;
			uriEvent = CalendarContract.Events.CONTENT_URI;
		}
		
	}

	private void init() {
		listaNovca = (ListView) findViewById(R.id.lNovci);
		
	}

	private Cursor dohvatiKursor(String[] projection, String selection,
			String path) {
        //Uri calendars = Uri.parse("content://calendar/" + path);

		//long now = System.currentTimeMillis();
		long now = new Date().getTime();
		//long window = 1000*60*60*24*365;
		
		// now - (2*DateUtils.YEAR_IN_MILLIS)....
		// ne koristim Uri iz verzijaAndroida() jer sam tamo stavio krivi Uri; (/events), a meni trebaju instances
		// zato ne radi za 4.0
        uriCalendar = Uri.parse("content://com.android.calendar/instances/when/" + (now - (DateUtils.YEAR_IN_MILLIS)) + "/" + (now + (DateUtils.YEAR_IN_MILLIS)));
        
        // startDay je Julian Day
        // begin je u milisekundama
        Cursor managedCursor = null;
        try {
        	managedCursor = getContentResolver().query(uriCalendar, null, null,
                    null, "begin ASC");
        } catch (IllegalArgumentException e) {
            Log.w("DEBUG_TAG", "Failed to get provider at ["
                    + uriCalendar.toString() + "]");
        }
        
        return managedCursor;
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
		getMenuInflater().inflate(R.menu.novci, menu);
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
