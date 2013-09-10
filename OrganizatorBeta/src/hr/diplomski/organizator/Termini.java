package hr.diplomski.organizator;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.provider.CalendarContract;

public class Termini extends Activity implements OnItemClickListener, OnScrollListener {
	
	private static final String DEBUG_TAG = "Kalendar";
	private static final int UREDI_TERMIN = 0;
	ArrayAdapter<String> termini;
	ListView lista;
	Uri uriCalendar, uriEvent;
	ArrayList<TerminiDetalji> popisTermina;
	Cursor managedCursor;
	
	int prvaPozicija = 0;
	int zadnjaPozicija = 0;
	Boolean prviPut = true;
	TerminiListAdapter adapter;
	int maxUcitanih = 20;
	AdapterContextMenuInfo info;
	Bundle bu;


    int id, event_id, access, kalendar, naslov, sadrzaj, color, begin, end, mjesto, cijeli_dan;

	
	//https://code.google.com/p/android-calendar-provider-tests/source/browse/trunk/src/com/androidbook/androidcalendar/CalendarActivity.java
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_termini);
		// Show the Up button in the action bar.
		setupActionBar();
		
		verzijaAndroida();
		lista = (ListView) findViewById(R.id.lvTermini);
		registerForContextMenu(lista);
		lista.setOnScrollListener(this);
		//termini  = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		// Adapter se dodaje ListView
		//lista.setAdapter(termini);
		
		try {
            Log.i(DEBUG_TAG, "Starting Calendar Test");
            
            popisAktualnihDogadjaja();
            
            /*
            // change this when you know which calendar you want to use
            // If you create a new calendar, you may need to manually sync the
            // phone first
            if (iTestCalendarID != 0) {
            	
                Uri newEvent2 = MakeNewCalendarEntry2(iTestCalendarID);
                int eventID2 = Integer.parseInt(newEvent2.getLastPathSegment());
                ListCalendarEntry(eventID2);
                
                
                Uri newEvent = MakeNewCalendarEntry(iTestCalendarID);
                int eventID = Integer.parseInt(newEvent.getLastPathSegment());
                ListCalendarEntry(eventID);
                
                // uncomment these to show updating and deleting entries

                UpdateCalendarEntry(eventID);
                //ListCalendarEntrySummary(eventID);
                //DeleteCalendarEntry(eventID);
                
                //ListCalendarEntrySummary(eventID);
                //ListAllCalendarEntries(iTestCalendarID);
            } else {
                Log.i(DEBUG_TAG, "No 'Test' calendar found.");
            }

            Log.i(DEBUG_TAG, "Ending Calendar Test");
            */
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "General failure", e);
        }
		lista.setOnItemClickListener(this);
	}
	
	private void popisAktualnihDogadjaja() {
		popisTermina = new ArrayList<TerminiDetalji>();
		
		String[] projection = new String[] { "_id", "event_id", "title", "color", "begin", "end", "calendar_id", "allDay", "description"
				, "access_level", "eventLocation",  "dtstart", "duration", "lastDate", "dtend"};
        String selection = "selected=1";
        managedCursor = dohvatiKursor(null,
                selection, "events/" );
        
        String podaci = "";
        long startTime = System.currentTimeMillis();
        
        
        if (managedCursor != null && managedCursor.moveToFirst()) {

            Log.i(DEBUG_TAG, "Listing Selected Calendars Only");
            
            long vrijeme = System.currentTimeMillis();
            int i = 0;

            id = managedCursor.getColumnIndex("_id");
            event_id = managedCursor.getColumnIndex("event_id");
            access = managedCursor.getColumnIndex("access_level");
			kalendar = managedCursor.getColumnIndex("calendar_id");
            naslov = managedCursor.getColumnIndex("title");
            sadrzaj = managedCursor.getColumnIndex("description");
            color = managedCursor.getColumnIndex("color");
            begin = managedCursor.getColumnIndex("begin");
            end = managedCursor.getColumnIndex("end");
            mjesto = managedCursor.getColumnIndex("eventLocation");
            cijeli_dan = managedCursor.getColumnIndex("allDay");
            
    		TerminiDetalji detalji = new TerminiDetalji();
    		detalji.setNaslov("Prvi");
    		popisTermina.add(detalji);

            // startDay je Julian Day
            // begin je u milisekundama
            do{
            	if (managedCursor.getLong(begin)>=vrijeme){
            		if(prviPut){
            			prvaPozicija = managedCursor.getPosition();
            			prviPut = false;
            		}
            		detalji = new TerminiDetalji();
            		/*
                    String calName = managedCursor.getString(nameColumn);
                    String calId = managedCursor.getString(idColumn);
                    Long calDate = managedCursor.getLong(dtstartColumn);
                    int boja = managedCursor.getInt(color);
                    */
                    
            		detalji.setPozicija(managedCursor.getPosition());
            		detalji.setId(managedCursor.getString(id));
            		detalji.setEventId(managedCursor.getString(event_id));
            		detalji.setAccess(managedCursor.getString(access));
            		detalji.setKalendar(managedCursor.getString(kalendar));
                    detalji.setNaslov(managedCursor.getString(naslov));
                    //detalji.setPocetak(managedCursor.getLong(startDay));
                    detalji.setBoja(managedCursor.getInt(color));
                    detalji.setPocetak(managedCursor.getLong(begin));
                    detalji.setKraj(managedCursor.getLong(end));
                    detalji.setSadrzaj(managedCursor.getString(sadrzaj));
                    detalji.setMjesto(managedCursor.getString(mjesto));
                    detalji.setCijeli_dan(managedCursor.getString(cijeli_dan));
                    
                    popisTermina.add(detalji);
            		++i;
            		if (i == maxUcitanih){
            			zadnjaPozicija = managedCursor.getPosition();
            			break;
            		}
            	}
            }while(managedCursor.moveToNext());
            
    		detalji = new TerminiDetalji();
    		detalji.setNaslov("Zadnji");
    		popisTermina.add(detalji);
            
            /*
            do {
                String calName = managedCursor.getString(nameColumn);
                String calId = managedCursor.getString(idColumn);
                Long calDate = managedCursor.getLong(dtstartColumn);
                Date d = new Date (calDate);
                
                //Log.i(DEBUG_TAG, "Found Calendar '" + calName + "' (ID="
                        //+ calId + ")");
                if(startTime <= managedCursor.getLong(dtstartColumn)){
                	podaci = calId + " " + calName  + " " + d;
                	termini.add(podaci);
                	Log.i("Poèetno vrijeme", "" + calId + " " + calName  + " " + d + "\n");
                }
                
            } while (managedCursor.moveToNext());
            */
        } else {
            Log.i(DEBUG_TAG, "No Calendars");
        }
        adapter = new TerminiListAdapter(popisTermina, this);
        lista.setAdapter(adapter);
	}

	private Cursor dohvatiKursor(String[] projection, String selection,
			String path) {
        //Uri calendars = Uri.parse("content://calendar/" + path);

		//long now = System.currentTimeMillis();
		long now = new Date().getTime();
		long window = 1000*60*60*24*365;
        uriCalendar = Uri.parse("content://com.android.calendar/instances/when/" + (now - (2*DateUtils.YEAR_IN_MILLIS)) + "/" + (now + (2*DateUtils.YEAR_IN_MILLIS)));
        
        // startDay je Julian Day
        // begin je u milisekundama
        Cursor managedCursor = null;
        try {
        	managedCursor = getContentResolver().query(uriCalendar, projection, selection,
                    null, "begin ASC");
        } catch (IllegalArgumentException e) {
            Log.w(DEBUG_TAG, "Failed to get provider at ["
                    + uriCalendar.toString() + "]");
        }
        
        return managedCursor;
	}
    
    /*
    public Cursor dohvatiKursor(String[] projection,
            String selection, String path) {
        Uri calendars = Uri.parse("content://calendar/" + path);

        Cursor managedCursor = null;
        try {
        	managedCursor = getContentResolver().query(calendars, projection, selection,
                    null, "dtstart ASC");
        } catch (IllegalArgumentException e) {
            Log.w(DEBUG_TAG, "Failed to get provider at ["
                    + calendars.toString() + "]");
        }
        
        if (managedCursor == null) {
            // try again
            calendars = Uri.parse("content://com.android.calendar/" + path);
            try {
                managedCursor = getContentResolver().query(calendars, projection, selection,
                        null, "dtstart ASC");
            } catch (IllegalArgumentException e) {
                Log.w(DEBUG_TAG, "Failed to get provider at ["
                        + calendars.toString() + "]");
            }
        }
        return managedCursor;
    }
    */

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
		//android.os.Build.VERSION_CODES.GINGERBREAD
	}

	private void prethodniDogadjaji() {
		int i = 0;
		if(prvaPozicija>=0){
			if ((prvaPozicija-5) >= 0){
				prvaPozicija -=5;
			}else{
				prvaPozicija = 0;
			}
			if(managedCursor.moveToPosition(prvaPozicija) && (prvaPozicija != 0)){
				popisTermina = new ArrayList<TerminiDetalji>();
				
				maxUcitanih += 5;
				
				/*int id = managedCursor.getColumnIndex("_id");
				int event_id = managedCursor.getColumnIndex("event_id");
	            int access = managedCursor.getColumnIndex("access_level");
				int kalendar = managedCursor.getColumnIndex("calendar_id");
	            int naslov = managedCursor.getColumnIndex("title");
	            int sadrzaj = managedCursor.getColumnIndex("description");
	            int begin = managedCursor.getColumnIndex("begin");
	            int end = managedCursor.getColumnIndex("end");
	            
	            int color = managedCursor.getColumnIndex("color");
	            int mjesto = managedCursor.getColumnIndex("eventLocation");
	            int cijeli_dan = managedCursor.getColumnIndex("allDay");*/
	            
	    		TerminiDetalji detalji = new TerminiDetalji();
	    		detalji.setNaslov("Prvi");
	    		popisTermina.add(detalji);
				do{
					detalji = new TerminiDetalji();
					
					detalji.setId(managedCursor.getString(id));
					detalji.setEventId(managedCursor.getString(event_id));
					detalji.setAccess(managedCursor.getString(access));
            		detalji.setKalendar(managedCursor.getString(kalendar));
                    
            		detalji.setPozicija(managedCursor.getPosition());
                    detalji.setNaslov(managedCursor.getString(naslov));
                    detalji.setSadrzaj(managedCursor.getString(sadrzaj));
                    detalji.setPocetak(managedCursor.getLong(begin));
                    detalji.setKraj(managedCursor.getLong(end));
                    detalji.setBoja(managedCursor.getInt(color));
                    detalji.setMjesto(managedCursor.getString(mjesto));
                    detalji.setCijeli_dan(managedCursor.getString(cijeli_dan));
                    
                    popisTermina.add(detalji);
                    ++i;
                    if(i==maxUcitanih){
                    	break;
                    }
				}while(managedCursor.moveToNext());
				detalji = new TerminiDetalji();
				detalji.setNaslov("Zadnji");
				popisTermina.add(detalji);
				
				lista.setSelection(5);
				//lista.smoothScrollToPosition(15);
				maxUcitanih = i;
			}else{
				long godina = new DateUtils().YEAR_IN_MILLIS;
				godina = godina * 2;
				long danas = System.currentTimeMillis();
				danas -= godina;
				//String tekst = R.string.ucitani_su_dogadjaji_do;
				Toast.makeText(getApplicationContext(), this.getString(R.string.ucitani_su_dogadjaji_do) + 
						" " + new SimpleDateFormat("dd.MM.yyyy").format(danas), Toast.LENGTH_LONG).show();
				
			}
		}
		adapter = new TerminiListAdapter(popisTermina, this);
		lista.setAdapter(adapter);
	}

	private void novijiDogadjaji() {
		//try {
			int i = 0;
			int velicinaCursora = managedCursor.getCount();
			Log.d("NOVIJI DOGAÐAJI", "Ušao u fju");
			if(managedCursor.moveToPosition(prvaPozicija) && ((zadnjaPozicija+5)<= velicinaCursora)){
				maxUcitanih += 5;
				
				Log.d("NOVIJI DOGAÐAJI", "Ušao u prvi if");
				
				popisTermina = new ArrayList<TerminiDetalji>();

				/*int id = managedCursor.getColumnIndex("_id");
				int event_id = managedCursor.getColumnIndex("event_id");
	            int access = managedCursor.getColumnIndex("access_level");
				int kalendar = managedCursor.getColumnIndex("calendar_id");
			    int naslov = managedCursor.getColumnIndex("title");
			    int sadrzaj = managedCursor.getColumnIndex("description");
			    int begin = managedCursor.getColumnIndex("begin");
	            int end = managedCursor.getColumnIndex("end");
			    int color = managedCursor.getColumnIndex("color");
	            int mjesto = managedCursor.getColumnIndex("eventLocation");
	            int cijeli_dan = managedCursor.getColumnIndex("allDay");*/
	            
	    		TerminiDetalji detalji = new TerminiDetalji();
	    		detalji.setNaslov("Prvi");
	    		popisTermina.add(detalji);
				do{
					detalji = new TerminiDetalji();

					detalji.setId(managedCursor.getString(id));
					detalji.setEventId(managedCursor.getString(event_id));
					detalji.setAccess(managedCursor.getString(access));
            		detalji.setKalendar(managedCursor.getString(kalendar));
					
					detalji.setPozicija(managedCursor.getPosition());
			        detalji.setNaslov(managedCursor.getString(naslov));
                    detalji.setSadrzaj(managedCursor.getString(sadrzaj));
			        detalji.setPocetak(managedCursor.getLong(begin));
			        detalji.setKraj(managedCursor.getLong(end));
			        detalji.setBoja(managedCursor.getInt(color));
			        detalji.setMjesto(managedCursor.getString(mjesto));
			        detalji.setCijeli_dan(managedCursor.getString(cijeli_dan));
			        
			        popisTermina.add(detalji);
			        ++i;
			        if(i==maxUcitanih){
			        	zadnjaPozicija = managedCursor.getPosition();
			        	break;
			        }
				}while(managedCursor.moveToNext());
				detalji = new TerminiDetalji();
				detalji.setNaslov("Zadnji");
				popisTermina.add(detalji);
				
				adapter = new TerminiListAdapter(popisTermina, this);
				lista.setAdapter(adapter);
				lista.setSelection((maxUcitanih-10));
				
			} else if (managedCursor.moveToPosition(prvaPozicija) && (zadnjaPozicija<velicinaCursora)) {
				int brojac = velicinaCursora-maxUcitanih;
				maxUcitanih += brojac;

				Log.d("NOVIJI DOGAÐAJI", "Ušao u drugi if");
				popisTermina = new ArrayList<TerminiDetalji>();


				/*int id = managedCursor.getColumnIndex("_id");
				int event_id = managedCursor.getColumnIndex("event_id");
	            int access = managedCursor.getColumnIndex("access_level");
				int kalendar = managedCursor.getColumnIndex("calendar_id");
				
			    int naslov = managedCursor.getColumnIndex("title");
			    int sadrzaj = managedCursor.getColumnIndex("description");
			    int begin = managedCursor.getColumnIndex("begin");
			    int color = managedCursor.getColumnIndex("color");
	            int mjesto = managedCursor.getColumnIndex("eventLocation");*/
	            
	    		TerminiDetalji detalji = new TerminiDetalji();
	    		detalji.setNaslov("Prvi");
	    		popisTermina.add(detalji);
	    		
				do{
					detalji = new TerminiDetalji();

					detalji.setId(managedCursor.getString(id));
					detalji.setEventId(managedCursor.getString(event_id));
					detalji.setAccess(managedCursor.getString(access));
            		detalji.setKalendar(managedCursor.getString(kalendar));
					
					detalji.setPozicija(managedCursor.getPosition());
			        detalji.setNaslov(managedCursor.getString(naslov));
                    detalji.setSadrzaj(managedCursor.getString(sadrzaj));
			        detalji.setPocetak(managedCursor.getLong(begin));
			        detalji.setBoja(managedCursor.getInt(color));
			        detalji.setMjesto(managedCursor.getString(mjesto));
			        
			        popisTermina.add(detalji);
			        /*
			        ++i;
			        if(i==maxUcitanih){
			        	zadnjaPozicija = managedCursor.getPosition();
			        	break;
			        }*/
				}while(managedCursor.moveToNext());
				detalji = new TerminiDetalji();
				detalji.setNaslov("Zadnji");
				popisTermina.add(detalji);
				
				zadnjaPozicija = managedCursor.getPosition();
				adapter = new TerminiListAdapter(popisTermina, this);
				lista.setAdapter(adapter);
				//int pozListe = maxUcitanih-5;
				lista.setSelection((maxUcitanih));
			}else {
				long godina = new DateUtils().YEAR_IN_MILLIS;
				godina = godina * 2;
				long danas = System.currentTimeMillis();
				danas += godina;
				//String tekst = R.string.ucitani_su_dogadjaji_do;
				Toast.makeText(getApplicationContext(), this.getString(R.string.ucitani_su_dogadjaji_do) + " " + new SimpleDateFormat("dd.MM.yyyy").format(danas), Toast.LENGTH_LONG).show();
				Log.d("NOVI", "U elseu: " + zadnjaPozicija);
			}
	//	} catch (Exception e) {
			// TODO Auto-generated catch block
	//		e.printStackTrace();
	//	}

			Log.d("NOVIJI DOGAÐAJI", "zadnjaPozicija: " + zadnjaPozicija + " maxUcitanih: " + maxUcitanih + " Velièina cursora: " + velicinaCursora);
		
			Log.d("NOVIJI DOGAÐAJI", "Na kraju fje");
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		//Log.e("Scroll state changed", "Reagira na pomiccanje");
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		/*
		boolean ucitaj = firstVisibleItem + visibleItemCount >= totalItemCount;
		Log.e("Scroll", " First: " + firstVisibleItem + " Visible: " + visibleItemCount + "Total: " + totalItemCount);
		
		boolean ucitajGore = firstVisibleItem==1;
		
		if(ucitaj){
			//Log.e("Uèitavanje", "Brojaè: "+ brojac);
			brojac++;
			ucitaj=false;
		}*/
		/*
		if(firstVisibleItem==0){
			Log.e("Uèitaj gore", "Radi");
			ucitajGore=false;
			prethodniDogadjaji();
		}*/
	}
	
	// Stavio u komentar jer sam maknuo one tipke
	// Sad se dodatni dogaðaji uèitavaju odabirom u ListViewu
	/*public void klik(View v){
		switch(v.getId()){
		case R.id.bTerminiUcitajStarije:
			prethodniDogadjaji();
			break;
		case R.id.bTerminiUcitajNovije:
			novijiDogadjaji();
			break;
		}
	}*/

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
		getMenuInflater().inflate(R.menu.termini, menu);
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
	
	// Pogledati još o ContextMenu http://developer.android.com/guide/topics/ui/menus.html#FloatingContextMenu
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			// TODO Auto-generated method stub
			super.onCreateContextMenu(menu, v, menuInfo);
			//MenuInflater inflater = getMenuInflater();
			Log.e("Context", "Stvorio context");
		    getMenuInflater().inflate(R.menu.context_termini, menu);
			/*
			menu.add(0, DELETE_ID, 0, R.string.obrisi);
			menu.add(0, EDIT_ID, 0, R.string.uredi);
			*/
		}
		
		@Override
		public boolean onContextItemSelected(MenuItem item) {
			info = (AdapterContextMenuInfo) item.getMenuInfo();
			switch(item.getItemId()){
			case R.id.mUredi:
				dohvatiDetalje(info.position);
				
				if (bu.getString("access").equals("700")) {
					Intent i = new Intent(this, NoviTermin.class);
					bu.putBoolean("uredi", true);
					i.putExtras(bu);
					startActivityForResult(i, UREDI_TERMIN);
					finish();
				}else {

				    new AlertDialog.Builder(this)
				        .setIcon(android.R.drawable.ic_dialog_alert)
				        .setTitle(R.string.pozor)
				        .setMessage(R.string.uredi_termin)
				        .setPositiveButton(R.string.kopiraj, new DialogInterface.OnClickListener()
				    {
				        @Override
				        public void onClick(DialogInterface dialog, int which) {
				        	// Nije mi dozvolio da pozovem novu klasu odavdje, pa sam pozvao fju
				        	// Treba pripaziti na kalendar koji æe se ponuditi klasi NoviTermin
							Intent data = new Intent();
							data.putExtra("akcija", "zatvori");
							setResult(RESULT_OK, data);
							// Treba pripaziti kada se pokrene klasa NoviTermin da odabere odgovarajuæi kalendar
							urediTermin();
							finish();
				        }

				    })
				    .setNegativeButton(R.string.odustani, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					})
				    .show();
					
				}
				break;
			case R.id.mKopiraj:
				/*Intent i = new Intent(this, NoviTermin.class);
				i.putExtras(bu);
				startActivityForResult(i, UREDI_TERMIN);*/
				dohvatiDetalje(info.position);
				urediTermin();
				finish();
				break;
			case R.id.mObrisi:
				TerminiDetalji det = popisTermina.get(info.position);
				if(det.getAccess().equals("700")){
					try {
			        	getContentResolver().delete(uriEvent, "_id=" + det.getEventId(), null);
			        	maxUcitanih = 20;
			        	popisAktualnihDogadjaja();
					} catch (Exception e) {

						Toast.makeText(getApplicationContext(), this.getString(R.string.ovaj_termin_se_ne_moze_obrisati), Toast.LENGTH_LONG).show();
					}
				}else{
					Toast.makeText(getApplicationContext(), this.getString(R.string.ovaj_termin_se_ne_moze_obrisati), Toast.LENGTH_LONG).show();
				}
				break;
			}
			return super.onContextItemSelected(item);
		}
		
		private void urediTermin() {
			Intent i = new Intent(this, NoviTermin.class);
			i.putExtras(bu);
			startActivity(i);
			finish();
			
		}

		@Override
		public void onItemClick(AdapterView<?> adap, View v, int position,
				long id) {
			
			/*managedCursor.moveToPosition((int) id);
			int brojac = managedCursor.getColumnCount();
			for(int i = 0; i<brojac; i++){
				Log.d("Odabrani", managedCursor.getColumnName(i) + ": " + managedCursor.getString(i));
			}*/
			int velicina = popisTermina.size();
			if(position==0){
				Log.i("Pozicije", "Odabrano: " + position + " Velièina: " + velicina);
				prethodniDogadjaji();
			}else if (position==(velicina-1)) {
				Log.i("Pozicije", "Odabrano: " + position + " Velièina: " + velicina);
				novijiDogadjaji();
			}else {
				Log.i("Pozicije", "Odabrano: " + position + " Velièina: " + velicina);
				dohvatiDetalje(position);
				Intent i = new Intent(this, TerminPregled.class);
				i.putExtras(bu);
				startActivityForResult(i, UREDI_TERMIN);
			}
		}
		
	    @Override
		protected void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			// TODO Auto-generated method stub
			super.onActivityResult(requestCode, resultCode, data);
			if(resultCode==RESULT_OK && requestCode == UREDI_TERMIN){
				if(data.getExtras().getString("akcija").equals("zatvori")){
					finish();
				}else if(data.getExtras().getString("akcija").equals("ponovno")){
					maxUcitanih = 20;
					popisAktualnihDogadjaja();
				}
			}
		}
	    
	    private void dohvatiDetalje(int position){
			TerminiDetalji ter = popisTermina.get(position);
			bu = new Bundle();
			bu.putString("ID", ter.getId());
			bu.putString("event_id", ter.getEventId());
			bu.putString("kalendar", ter.getKalendar());
			bu.putString("access", ter.getAccess());
			bu.putBoolean("uredi", false);
			bu.putString("Naslov", ter.getNaslov());
			
			bu.putInt("calendar_id", managedCursor.getInt(managedCursor.getColumnIndex("calendar_id")));
			
			bu.putLong("Poèetak", ter.getPocetak());
			bu.putLong("kraj", ter.getKraj());
			bu.putInt("boja", ter.getBoja());
			bu.putString("sadržaj", ter.getSadrzaj());
			bu.putString("mjesto", ter.getMjesto());
			bu.putString("cijeli_dan", ter.getCijeli_dan());
	    	
	    }

		// Dohvaæa opis pojedinog eventa; može se izvuæi title, description, u kojem je kalendaru, koje boje, vrijeme...
	    public void ListCalendarEntry(int eventId) {
	        Cursor managedCursor = getCalendarManagedCursor(null, null, "events/" + eventId);
	        
	        if (managedCursor != null && managedCursor.moveToFirst()) {

	            Log.i(DEBUG_TAG, "Listing Calendar Event Details");

	            do {

	                Log.i(DEBUG_TAG, "**START Calendar Event Description**");

	                for (int i = 0; i < managedCursor.getColumnCount(); i++) {
	                	
	                	//Log.i(DEBUG_TAG, managedCursor.getColumnName(i) + "="
	                    //        + managedCursor.getString(i));
	                }
	                Log.i(DEBUG_TAG, "**END Calendar Event Description**");
	            } while (managedCursor.moveToNext());
	        } else {
	            Log.i(DEBUG_TAG, "No Calendar Entry");
	        }
	    }
	    
	    // Ovdje vrati samo ID, title i poèetak u milisekundama (takav je Query)
	    public void ListCalendarEntrySummary(int eventId) {
	        String[] projection = new String[] { "_id", "title", "dtstart" };
	        Cursor managedCursor = getCalendarManagedCursor(projection,
	                null, "events/" + eventId);
	        
	        String test = "";
	        
	        if (managedCursor != null && managedCursor.moveToFirst()) {

	            Log.i(DEBUG_TAG, "Listing Calendar Event Details");

	            do {

	                Log.i(DEBUG_TAG, "**START Calendar Event Description**");

	                for (int i = 0; i < managedCursor.getColumnCount(); i++) {
	                	test += managedCursor.getColumnName(i) + "="
	                            + managedCursor.getString(i) + "\n";
	                    //Log.i(DEBUG_TAG, managedCursor.getColumnName(i) + "="
	                    //        + managedCursor.getString(i));
	                }
	                Log.i(DEBUG_TAG, "**END Calendar Event Description**");
	            } while (managedCursor.moveToNext());
	        } else {
	            Log.i(DEBUG_TAG, "No Calendar Entry");
	        }
	    }
	    
	    // Naravno obriše željeni Event i vrati INT koliko ih je obrisao; nakon što se Eventi odreðenog ID-a obrišu, novi Eventi dobivaju te ID-jeve ukoliko ne postoji neki Event veæeg ID-a
	    // tada se Id nastavlja na najveæi
	    /*
	    public int DeleteCalendarEntry(int entryID) {
	        int iNumRowsDeleted = 0;

	        Uri eventsUri = Uri.parse(getCalendarUriBase()+"events");
	        Uri eventUri = ContentUris.withAppendedId(eventsUri, entryID);
	        
	        return getContentResolver().delete(eventUri, null, null);
	        //iNumRowsDeleted = getContentResolver().delete(eventUri, null, null);
	        //Log.i(DEBUG_TAG, "Deleted " + iNumRowsDeleted + " calendar entry.");
	        //return iNumRowsDeleted;
	    }
	    */
	    
	    /**
	     * @param projection
	     * @param selection
	     * @param path
	     * @return
	     */
		public Cursor getCalendarManagedCursor(String[] projection,
	            String selection, String path) {
			
			/*
			long now = System.currentTimeMillis();
			long window = 1000*60*60*24*365;
			*/
	        Uri calendars = Uri.parse("content://com.android.calendar/"+path);
	        
	        
	        Cursor managedCursor = null;
	        try {
	            managedCursor = getContentResolver().query(calendars, projection, selection,
	                    null, null);
	        } catch (IllegalArgumentException e) {
	            Log.w(DEBUG_TAG, "Failed to get provider at ["
	                    + calendars.toString() + "]");
	        }

	        if (managedCursor == null) {
	            // try again
	            calendars = Uri.parse("content://com.android.calendar/" + path);
	            try {
	                managedCursor = getContentResolver().query(calendars, projection, selection,
	                        null, null);
	            } catch (IllegalArgumentException e) {
	                Log.w(DEBUG_TAG, "Failed to get provider at ["
	                        + calendars.toString() + "]");
	            }
	        }
	        return managedCursor;
	    }

}
