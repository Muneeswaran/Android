package hr.diplomski.organizator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;

public class NoviTermin extends Activity {
	
	public static final String DEBUG_TAG = "CalendarActivity";
	Spinner izborKalendara;
	ArrayAdapter<String> imeKalendara;
	ArrayList<String> idKalendara;
	
	Button tipkaDatumPocetak, tipkaVrijemePocetak, tipkaDatumKraj, tipkaVrijemeKraj;
	EditText naslov, opis, lokacija, prisustvuje;
	CheckBox cijeli_dan;
	Calendar pocetakCal, krajCal;
	Uri uriCalendar, uriEvent, uriAttendee;
	int verzija;
	Long idEvent = null;
	Bundle extra;
	Cursor cur;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_novi_termin);
		// Show the Up button in the action bar.
		setupActionBar();
		
		extra = new Bundle();
		extra = (getIntent().getExtras() == null) ? null : getIntent().getExtras();
		
		
		//idEvent = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable("ID");
		/*if(idEvent == null){
			extra = new Bundle();
			extra = getIntent().getExtras();
			idEvent = extra != null ? extra.getLong("ID") : null;
		}*/
			
		
		verzija = android.os.Build.VERSION.SDK_INT;
		inicijalzacijaSucelja();
		verzijaAndroida();
		
		//Spinner
		spinnerFja();
		
		popisKalendara();
		
		postaviTipkePocetak();
		postaviTipkeKraj();
		if(extra!=null){
			postojeciDogadjaj();
		}
	}
	
	private void postaviTipkePocetak() {
		tipkaDatumPocetak.setText("   " + new SimpleDateFormat("E").format(pocetakCal.getTime()) + ", " + new SimpleDateFormat("dd/MM/yyyy").format(pocetakCal.getTime()) + "   ");
		tipkaVrijemePocetak.setText("   " + new SimpleDateFormat("HH:mm").format(pocetakCal.getTime()) + "   ");
		
	}

	private void postaviTipkeKraj() {
		tipkaDatumKraj.setText("   " + new SimpleDateFormat("E").format(krajCal.getTime()) + ", " + new SimpleDateFormat("dd/MM/yyyy").format(krajCal.getTime()) + "   ");
		tipkaVrijemeKraj.setText("   " + new SimpleDateFormat("HH:mm").format(krajCal.getTime()) + "   ");
		
	}

	@SuppressLint("NewApi")
	private void popisKalendara() {
        cur = null;
        String selection;
        
		if(verzija <= 10){
			//String[] projection = new String[] { "_id", "name" };
	        //String path = "calendars";
	        selection = "selected=1 AND access_level=700";
	        /*
	        Cursor managedCursor = getCalendarManagedCursor(null, selection,
	                path);
	        */
	        cur = getContentResolver().query(uriCalendar, null, selection, null, null);
	        
	        if (cur != null && cur.moveToFirst()) {

	            Log.i(DEBUG_TAG, "Listing Selected Calendars Only");

	            int nameColumn = cur.getColumnIndex("name");
	            int idColumn = cur.getColumnIndex("_id");

	            do {
	                String calName = cur.getString(nameColumn);
	                String calId = cur.getString(idColumn);
	                Log.i(DEBUG_TAG, "Found Calendar '" + calName + "' (ID="
	                        + calId + ")");
	                imeKalendara.add(calName);
	                idKalendara.add(calId);
	                /*
	                if (calName != null && calName.contains("Test")) {
	                    result = Integer.parseInt(calId);
	                }
	                */
	            } while (cur.moveToNext());
	        } else {
	        	Uri uri = Calendars.CONTENT_URI;   
	        	selection = "OWNER_ACCESS=700 AND SELECTED=selected";
	        	// Submit the query and get a Cursor object back. 
	        	cur = getContentResolver().query(uri, null, selection, null, null);
	        	if (cur != null && cur.moveToFirst()) {
	        		Log.i(DEBUG_TAG, "Listing Selected Calendars Only");

		            int nameColumn = cur.getColumnIndex("name");
		            int idColumn = cur.getColumnIndex("_id");

		            do {
		                String calName = cur.getString(nameColumn);
		                String calId = cur.getString(idColumn);
		                Log.i(DEBUG_TAG, "Found Calendar '" + calName + "' (ID="
		                        + calId + ")");
		                imeKalendara.add(calName);
		                idKalendara.add(calId);
		                /*
		                if (calName != null && calName.contains("Test")) {
		                    result = Integer.parseInt(calId);
		                }
		                */
		            } while (cur.moveToNext());
	        	}
	        }
		}
		
	}

	private void spinnerFja() {
		izborKalendara = (Spinner) findViewById(R.id.sKalendari);
		imeKalendara = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		imeKalendara.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		izborKalendara.setAdapter(imeKalendara);
		
		
		idKalendara = new ArrayList<String>();
	}

	@SuppressLint("NewApi")
	private void verzijaAndroida() {
		if (verzija <= 8) {
			uriCalendar = Uri.parse("content://calendar/calendars");
			uriEvent = Uri.parse("content://calendar/events");
			uriAttendee = Uri.parse("content://calendar/attendees");
		}
		else if (verzija <= 10) {
			uriCalendar = Uri.parse("content://com.android.calendar/calendars");
			uriEvent = Uri.parse("content://com.android.calendar/events");
			uriAttendee = Uri.parse("content://com.android.calendar/attendees");
		}else {
			uriCalendar = Calendars.CONTENT_URI;
			//uriEvent = CalendarContract.Events.CONTENT_URI;
			uriEvent = Events.CONTENT_URI;
			uriAttendee = Attendees.CONTENT_URI;
		}
		//android.os.Build.VERSION_CODES.GINGERBREAD
		
	}

	private void inicijalzacijaSucelja() {
		// Tipke
		tipkaDatumPocetak = (Button) findViewById(R.id.bNoviTerminDatumPocetak);
		tipkaVrijemePocetak = (Button) findViewById(R.id.bNoviTerminVrijemePocetak);
		tipkaDatumKraj = (Button) findViewById(R.id.bNoviTerminDatumKraj);
		tipkaVrijemeKraj = (Button) findViewById(R.id.bNoviTerminVrijemeKraj);
		
		// Kalendar
		pocetakCal = Calendar.getInstance();
		krajCal = Calendar.getInstance();
		// Ovdje treba uvesti varijable za sate i minute; da bi se kasnije u postavkama moglo koristiti proizvoljno vrijeme
		pocetakCal.set(pocetakCal.get(Calendar.YEAR), pocetakCal.get(Calendar.MONTH), pocetakCal.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		krajCal.set(krajCal.get(Calendar.YEAR), krajCal.get(Calendar.MONTH), krajCal.get(Calendar.DAY_OF_MONTH), 13, 0, 0);
		
		// Edit text
		naslov = (EditText) findViewById(R.id.etNoviTerminNaslov);
		opis = (EditText) findViewById(R.id.etNoviTerminOpis);
		lokacija = (EditText) findViewById(R.id.etNoviTerminLokacija);
		prisustvuje = (EditText) findViewById(R.id.etNoviTerminPrisustvuje);
		
		cijeli_dan = (CheckBox) findViewById(R.id.cbNoviTerminCijeliDan);
		
		
		// Postavljanje Edit texta
		if (idEvent != null){
			naslov.setText(extra.getString("Naslov"));
			
		}
		
		
	}
	
	public void klik(View v){
		switch(v.getId()){
		case R.id.bNoviTerminSpremi:
			
			try {
				//noviDogadjaj(4);
				
	            // ListAllCalendarDetails();
				
	            // will return the last found calendar with "Test" in the name
	            //int iTestCalendarID = ListSelectedCalendars();
	            //int iTestCalendarID = 2;
	            
	            // change this when you know which calendar you want to use
	            // If you create a new calendar, you may need to manually sync the
	            // phone first
	            if (imeKalendara != null) {
	            	
	            	String odabraniKalendar = idKalendara.get(izborKalendara.getSelectedItemPosition());
	                //Uri newEvent2 = MakeNewCalendarEntry2(4);
	            	Uri newEvent2 = noviDogadjaj(Integer.parseInt(odabraniKalendar));
	                int eventID2 = Integer.parseInt(newEvent2.getLastPathSegment());
	                //ListCalendarEntry(eventID2);
	                
	                //Uri newEvent = MakeNewCalendarEntry(iTestCalendarID);
	                //int eventID = Integer.parseInt(newEvent.getLastPathSegment());
	                //ListCalendarEntry(eventID);

	                //UpdateCalendarEntry(eventID);
	                //ListCalendarEntrySummary(eventID);
	                //DeleteCalendarEntry(eventID);
	                //ListCalendarEntrySummary(eventID);
	               // ListAllCalendarEntries(iTestCalendarID);
	            } else {
	                Log.i(DEBUG_TAG, "No 'Test' calendar found.");
	            }

	            Log.i(DEBUG_TAG, "Ending Calendar Test");
	            
	        } catch (Exception e) {
	            Log.e(DEBUG_TAG, "General failure", e);
	        }
			
			break;
		case R.id.bNoviTerminOdustani:
			finish();
			break;
			
		case R.id.bNoviTerminDatumPocetak:
			DatePickerDialog pocetakDatuma=new DatePickerDialog(this,odsl,pocetakCal.get(Calendar.YEAR),pocetakCal.get(Calendar.MONTH),pocetakCal.get(Calendar.DAY_OF_MONTH));
		    pocetakDatuma.show();
			break;
		case R.id.bNoviTerminVrijemePocetak:
			TimePickerDialog pocetakVremena=new TimePickerDialog(this,otsl,pocetakCal.get(Calendar.HOUR_OF_DAY),pocetakCal.get(Calendar.MINUTE),true);
		    pocetakVremena.show();
			break;
		case R.id.bNoviTerminDatumKraj:
			DatePickerDialog krajDatuma=new DatePickerDialog(this,krajodsl,krajCal.get(Calendar.YEAR),krajCal.get(Calendar.MONTH),krajCal.get(Calendar.DAY_OF_MONTH));
		    krajDatuma.show();
			break;
		case R.id.bNoviTerminVrijemeKraj:
			TimePickerDialog krajVremena=new TimePickerDialog(this,krajotsl,krajCal.get(Calendar.HOUR_OF_DAY),krajCal.get(Calendar.MINUTE),true);
		    krajVremena.show();
			break;
		
		}
	}
	
	//http://jennifer-androidworld.blogspot.in/2012/02/datepickerdialog-timepickerdialog-in.html
	private DatePickerDialog.OnDateSetListener odsl = new DatePickerDialog.OnDateSetListener()
    {

		public void onDateSet(DatePicker arg0, int year, int month, int dayOfMonth) {
			pocetakCal.set(year, month, dayOfMonth);
			if (krajCal.before(pocetakCal) || krajCal.equals(pocetakCal)){
				krajCal.setTime(pocetakCal.getTime());
				krajCal.add(Calendar.HOUR_OF_DAY, 1);
				// Podesiti Toast; umjesto teksta treba varijabla
				//Toast.makeText(getApplicationContext(), "Krivo podešeno vrijeme", Toast.LENGTH_SHORT).show();
				postaviTipkeKraj();
			}
			
			postaviTipkePocetak();
		}
    };
    
    private DatePickerDialog.OnDateSetListener krajodsl = new DatePickerDialog.OnDateSetListener()
    {

		public void onDateSet(DatePicker arg0, int year, int month, int dayOfMonth) {
			
			krajCal.set(year, month, dayOfMonth);
			if (krajCal.before(pocetakCal) || krajCal.equals(pocetakCal)){
				krajCal.setTime(pocetakCal.getTime());
				krajCal.add(Calendar.HOUR_OF_DAY, 1);
				// Podesiti Toast; umjesto teksta treba varijabla
				Toast.makeText(getApplicationContext(), "Krivo podešeno vrijeme", Toast.LENGTH_SHORT).show();
			}
			
			postaviTipkeKraj();
		}
    };
    
    private TimePickerDialog.OnTimeSetListener otsl=new TimePickerDialog.OnTimeSetListener()
    {

    	public void onTimeSet(TimePicker arg0, int hourOfDay, int minute) {
    		
    		pocetakCal.set(pocetakCal.get(Calendar.YEAR), pocetakCal.get(Calendar.MONTH), pocetakCal.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
    		if (krajCal.before(pocetakCal) || krajCal.equals(pocetakCal)){
				krajCal.setTime(pocetakCal.getTime());
				krajCal.add(Calendar.HOUR_OF_DAY, 1);
				// Podesiti Toast; umjesto teksta treba varijabla
				//Toast.makeText(getApplicationContext(), "Krivo podešeno vrijeme", Toast.LENGTH_SHORT).show();
				postaviTipkeKraj();
			}	
			
    		postaviTipkePocetak();
    	}
    };
    
    private TimePickerDialog.OnTimeSetListener krajotsl=new TimePickerDialog.OnTimeSetListener()
    {

    	public void onTimeSet(TimePicker arg0, int hourOfDay, int minute) {
    		
    		krajCal.set(krajCal.get(Calendar.YEAR), krajCal.get(Calendar.MONTH), krajCal.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
    		if (krajCal.before(pocetakCal) || krajCal.equals(pocetakCal)){
				krajCal.setTime(pocetakCal.getTime());
				krajCal.add(Calendar.HOUR_OF_DAY, 1);
				// Podesiti Toast; umjesto teksta treba varijabla
				Toast.makeText(getApplicationContext(), "Krivo podešeno vrijeme", Toast.LENGTH_SHORT).show();
			}
    		
			
			postaviTipkeKraj();
    	}
    };
	
	private void postojeciDogadjaj(){
		naslov.setText(extra.getString("Naslov"));
		opis.setText(extra.getString("sadržaj"));
		lokacija.setText(extra.getString("mjesto"));
		if(extra.getString("cijeli_dan").equals("1")){
			cijeli_dan.setChecked(true);
		}
		if(extra.getString("access").equals("700")){
			izborKalendara.setSelection(idKalendara.indexOf(extra.getString("kalendar")));
		}
		pocetakCal.setTimeInMillis(extra.getLong("Poèetak"));
		krajCal.setTimeInMillis(extra.getLong("kraj"));
		postaviTipkePocetak();
		postaviTipkeKraj();
	}
	
	private Uri noviDogadjaj(int calId) {
        ContentValues event = new ContentValues();
        Uri insertedUri = null;
        if(verzija<=10){


            event.put("calendar_id", calId);
            event.put("title", naslov.getText().toString());
            event.put("description", opis.getText().toString());
            event.put("eventLocation", lokacija.getText().toString());

            event.put("dtstart", pocetakCal.getTimeInMillis());
            event.put("dtend", krajCal.getTimeInMillis());
            
            if(cijeli_dan.isChecked()){
                event.put("allDay", 1);
            }else{
                event.put("allDay", 0);
            }
            event.put("eventStatus", 1);
            event.put("visibility", 0);
            event.put("transparency", 0);
            event.put("hasAlarm", 0); // 0 for false, 1 for true

            //Uri eventsUri = Uri.parse(getCalendarUriBase()+"events");
            
            if(extra != null){
            	if(extra.getString("access").equals("700") && extra.getBoolean("uredi")){
                    getContentResolver().update(ContentUris.withAppendedId(uriEvent, Long.valueOf(extra.getString("event_id")).longValue()), event, null, null);
            	}else {
                    insertedUri = getContentResolver().insert(uriEvent, event);
				}
            }else {
                insertedUri = getContentResolver().insert(uriEvent, event);
			}
            
            // Ne bih trebao hard kodirati poruku veæ napraviti string
			Toast.makeText(getApplicationContext(), "Dogaðaj spremljen", Toast.LENGTH_SHORT).show();
        }
        else{
            event.put(Events.CALENDAR_ID, calId);
            event.put(Events.TITLE, naslov.getText().toString());
            event.put(Events.DESCRIPTION, opis.getText().toString());

            event.put(Events.DTSTART, pocetakCal.getTimeInMillis());
            event.put(Events.DTEND, krajCal.getTimeInMillis());
            //event.put("allDay", 0); // 0 for false, 1 for true
            //event.put("eventStatus", 1);
            //event.put("visibility", 0);
            //event.put("transparency", 0);
            //event.put("hasAlarm", 0); // 0 for false, 1 for true

            insertedUri = getContentResolver().insert(uriEvent, event);
			Toast.makeText(getApplicationContext(), "Dogaðaj spremljen", Toast.LENGTH_SHORT).show();
        	
        }
        return insertedUri;
        
        /*
         * Novi dogaðaj za 4.0
         * http://developer.android.com/guide/topics/providers/calendar-provider.html#add-event
        long calID = 3;
        long startMillis = 0; 
        long endMillis = 0;     
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2012, 9, 14, 7, 30);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2012, 9, 14, 8, 45);
        endMillis = endTime.getTimeInMillis();
        ...

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, startMillis);
        values.put(Events.DTEND, endMillis);
        values.put(Events.TITLE, "Jazzercise");
        values.put(Events.DESCRIPTION, "Group workout");
        values.put(Events.CALENDAR_ID, calID);
        values.put(Events.EVENT_TIMEZONE, "America/Los_Angeles");
        Uri uri = cr.insert(Events.CONTENT_URI, values);

        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());
        // 
        // ... do something with event ID
        //
        //
         * 
         */
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
		getMenuInflater().inflate(R.menu.novi_termin, menu);
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
