package hr.diplomski.organizator;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentProviderOperation;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;

public class Termini extends Activity implements OnItemClickListener, OnScrollListener {
	
	//-----------------------------------------------------------------------------------------------------------------------------------
	SharedPreferences preferences;
	SharedPreferences.Editor e;
	public static final String PREFERENCES__TERMINI_KALENDARI = "hr.diplomski.organizator.TERMINI_KALENDARI";
	Map<String, Object> prefMapKalendari;
	ArrayList<String> listaSvihKalendara;
	String[] sviKalendari = null;
	Map<String, String> mapaSvihKalendara;
	int verzija;
	//-----------------------------------------------------------------------------------------------------------------------------------
	String pretragaPoNaslovu = "";
	
	private static final String DEBUG_TAG = "Kalendar";
	private static final int UREDI_TERMIN = 0;
	ArrayAdapter<String> termini;
	ListView lista;
	Uri uriCalendar, uriEvent;
	ArrayList<TerminiDetalji> popisTermina;
	Cursor managedCursor;
	
	int prvaPozicija = -1;
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
		
		verzija = android.os.Build.VERSION.SDK_INT;
		verzijaAndroida();
		//-----------------------------------------------------------------------------------------------------------------------------------
				listaSvihKalendara = popisSvihKalendara();
				// Dohvaæanje Set<> teko od API-ja 11
				// Zato sam napravio novi SharedPrefernces samo za kalendare koje onda sve dohvaæam u Map
				preferences = getSharedPreferences(PREFERENCES__TERMINI_KALENDARI, MODE_PRIVATE);
				e = preferences.edit();
				//preferences.getStringSet("popis_odabranih_kalendara", "");
				Log.d("NOVI TERMIN", "Kreira mapu iz SharedPreferencesa");
				prefMapKalendari = new HashMap<String, Object>(preferences.getAll());
				if(!prefMapKalendari.keySet().isEmpty() && !listaSvihKalendara.isEmpty()){
					Log.d("NOVI TERMIN", "Ušao u IF");
					// Ovo ne valja ovdje mi poziva Dijalog
					//odabirKalendara(listaSvihKalendara.toArray(new String[listaSvihKalendara.size()]), new ArrayList<String>(prefMapKalendari.keySet()));
					ArrayList<String> temp = new ArrayList<String>(prefMapKalendari.keySet());
					Collections.sort(temp, new Comparator<String>() {
						@Override
						public int compare(String o1, String o2) {
							return o1.compareToIgnoreCase(o2);
						}
					});
					e.clear();
					//imeKalendara.clear();
					for(String strTemp:temp){
						if (listaSvihKalendara.contains(strTemp)) {
							//imeKalendara.add(strTemp);
							e.putInt(strTemp, 1);
						}
					}
					e.commit();
					prefMapKalendari = new HashMap<String, Object>(preferences.getAll());
					Log.d("NOVI TERMIN", "Poslije poziva fje");
				}else{
					//prefMapKalendari = null;
					Log.d("NOVI TERMIN", "Ušao u ELSE");
				}
		//-----------------------------------------------------------------------------------------------------------------------------------
				
		
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
	//-----------------------------------------------------------------------------------------------------------------------------------
	private ArrayList<String> popisSvihKalendara(){
		Log.d("NOVI TERMIN", "Pozvana funkcija");
		Cursor cur = null;
		ArrayList<String> rez = null;
		if(verzija <= 10){
			String selection = null;
			String[] selectionArgs = null;
			String[] projection = new String[]{"_id", "name"};
			//mapaSvihKalendara = new HashMap<Long, String>();
			mapaSvihKalendara = new HashMap<String, String>();
			try {
				cur = getContentResolver().query(uriCalendar, projection, selection, selectionArgs, null);
				if(cur.moveToFirst()){
					//sviKalendari = new String[cur.getCount()];
					//int i = 0;
					int id = cur.getColumnIndex("_id");
					int calName = cur.getColumnIndex("name");
					do{
						//mapaSvihKalendara.put(cur.getLong(id), cur.getString(calName));
						mapaSvihKalendara.put(cur.getString(calName), cur.getString(id));
						Log.i("TAG", "Svi kal: " + cur.getLong(id));
					}while(cur.moveToNext());
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			rez = new ArrayList<String>(mapaSvihKalendara.keySet());
			Collections.sort(rez, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return o1.compareToIgnoreCase(o2);
				}
			});
			Log.d("NOVI TERMIN", "Promjena popisa kalendara");
		}
		return rez;
	}
	//-----------------------------------------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------------------
	private void odabirKalendara(String[] k, ArrayList<String> odabraniK){
		final String[] kalendari = k;
		final ArrayList<String> odabraniKalendari = odabraniK;
		int count = kalendari.length;
		boolean[] izborKalendara = new boolean[count];
		
		if (!odabraniKalendari.isEmpty()) {
			for (int i = 0; i < count; i++) {
				izborKalendara[i] = odabraniKalendari.contains(kalendari[i]);
			}
		}else{
			// Cijeli Arry se napuni s odabranom vrijednošæu
			Arrays.fill(izborKalendara, false);
		}
		DialogInterface.OnMultiChoiceClickListener listenerKalendari = new DialogInterface.OnMultiChoiceClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					odabraniKalendari.add(kalendari[which]);
				}else{
					odabraniKalendari.remove(kalendari[which]);
				}
			}
		};
    	 
    	  AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	  builder.setTitle(R.string.kalendari);
    	  builder.setCancelable(false);
    	  // Dialog builder sm pravi Multi Choice izbornikom
    	  // imeGrupa je moralo biti String[] jer ne prihvaæa ArrayList
    	  builder.setMultiChoiceItems(kalendari, izborKalendara, listenerKalendari);
    	  
    	  builder.setPositiveButton(R.string.spremi, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Collections.sort(odabraniKalendari, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return o1.compareToIgnoreCase(o2);
					}
				});
				//imeKalendara.clear();
				e.clear();
				for(String temp:odabraniKalendari){
					//imeKalendara.add(temp);
					e.putInt(temp, 1);
				}
				e.commit();
				prefMapKalendari = new HashMap<String, Object>(preferences.getAll());
				maxUcitanih = 20;
				popisAktualnihDogadjaja();
				dialog.dismiss();
			}
		});
    	  
    	  builder.setNegativeButton(R.string.odustani, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
    	 
    	  AlertDialog dialog = builder.create();
    	  dialog.show();
		
	}
	//-----------------------------------------------------------------------------------------------------------------------------------
	
	private void popisAktualnihDogadjaja() {
		popisTermina = new ArrayList<TerminiDetalji>();
		
		String[] projection = new String[] { "_id", "event_id", "title", "color", "begin", "end", "calendar_id", "allDay", "description"
				, "access_level", "eventLocation",  "dtstart", "duration", "lastDate", "dtend"};
        //String selection = "selected=1 AND (calendar_id=2 or calendar_id=4)";
        //String selection = "selected=1 AND calendar_id=2 ";
		String selection = new String();
        String[] selectionArgs = null;
        String sortOrder = "begin ASC";
        // Ovo sam preloše napravio jer mise više nije dalo
        // Iz nekog razloga ne želi raditi sa selectionArgs
        // zato pravim cijeli String u selection (?!)
        // možda bih trebao napraviti Array s long-om? (zato što je DB calendar_id=>long ; nemam više snage isprobavati ta sranja
        // idem nekako ovo riješiti i odmoriti
		//if(!prefMapKalendari.keySet().isEmpty()){
        // Ovaj If ne radi. Ne kužim što nije u redu s uvjetom
        // Nije ni bitno jer ako niti jedan kalendar nije odabran, onda nema šta ni prikazivati
		if(!prefMapKalendari.keySet().isEmpty()){
			int i = 0;
			int count = prefMapKalendari.size();
			selection += "(";
			for(String temp:prefMapKalendari.keySet()){
				selection += "calendar_id=" + mapaSvihKalendara.get(temp);
				i++;
				if(i<count){
					selection += " OR ";
				}
			}
			selection += ")";
			
			/*selectionArgs = new String[prefMapKalendari.size()];
			for(int i = 0; i<prefMapKalendari.size()-1; i++){
				selection += "calendar_id=" + ; 
			}
			selection += "calendar_id=?";
			Log.d("Selection", selection);
			int i=0;
			for(String temp:prefMapKalendari.keySet()){
				selectionArgs[i] = mapaSvihKalendara.get(temp);
				Log.d("ARG", "Arg: " + temp + " ID: " + selectionArgs[i]);
				i++;
			}*/
			//selectionArgs = prefMapKalendari.values().toArray(new String[prefMapKalendari.values().size()]);
			//selectionArgs = (String[]) prefMapKalendari.values().toArray();
			
		}else{
			//selection = null;
			selection = "";
		}
		// Ovdje ne znam što æe biti ako je selection = null;
		// Zato sam gore u Else stavio selection = "";
		if(!pretragaPoNaslovu.equals("")){
			Log.i("TERMINI-PRETRAGA", "Prvi If");
			if(!selection.equals("")){
				Log.i("TERMINI-PRETRAGA", "Drugi If");
				selection += " AND (title LIKE '%" + pretragaPoNaslovu + "%')";
				pretragaPoNaslovu = "";
			}else{
				Log.i("TERMINI-PRETRAGA", "Else");
				selection += " title LIKE '%" + pretragaPoNaslovu + "%'";
				pretragaPoNaslovu = "";
			}
		}
        // 'null' zamijenio s 'projection'
        managedCursor = dohvatiKursor(projection,
                selection, null, sortOrder );
        
        //managedCursor = getContentResolver().query(uriCalendar, projection, selection, selectionArgs, null);
        
        // Ovdje je sad problem što je moguæe da nema dogaðaja u budoænosti
        // i onda ništa neæe spremiti u adapter, tj. neæe se moæi ni stariji dogaðaji pregledati
        // Kada se pokušaju uèitati noviji dogaðaji (kojih nema) ne javlja da ih nema
        // uèini se kao da jednostavno želi listati naprijed
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
    		
    		Log.i("KAL", "Cal id: " + managedCursor.getString(kalendar));
    		
    		// Problemi kod filtriranja (vjerojatno i kod brisanja)
    		// Ako listam dogaðaja i u jednom trenutku dodam ili izbacim kalendar, pa zatražim novi Query
    		// pozicije podivljaju, baci na tko zna koji datum kada odaberem uèitavanje dodatnih dogaðaja
    		prviPut = true;
    		maxUcitanih = 20;
    		prvaPozicija = -1;
    		zadnjaPozicija = 0;
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
            zadnjaPozicija = managedCursor.getPosition();
            
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
			String[] selectionArgs, String sortOrder) {
		//Log.i("ARG", selectionArgs[0] + " " + selectionArgs[1] + " " + selectionArgs[2]);
		// Ovdje se ruši kada ništa nije odabrano, jer je selection=null
		//Log.d("TAG", selection);
		
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
        	managedCursor = getContentResolver().query(uriCalendar, projection, selection,
                    null, sortOrder);
        	//Kreirao dogaðaje: 
        	// 30 kn ruèak fast food hamburger
        	// 50 kn ruèak restoran
        	// 40 kn veèera pizza bistro
        	//managedCursor = getContentResolver().query(uriCalendar, projection, "title LIKE '%hamburger%' OR title LIKE '%bistro%'", null, sortOrder);
        	//managedCursor = getContentResolver().query(uriCalendar, projection, "title LIKE '%fast food%' AND title LIKE '%ruèak%'", null, sortOrder);
        	// query radi kao zamišljen; nema prepreka TAG-ovima
        	Log.i("TAG", "Velièina kursora: " + managedCursor.getCount());
        } catch (IllegalArgumentException e) {
            Log.w(DEBUG_TAG, "Failed to get provider at ["
                    + uriCalendar.toString() + "]");
        }
        if(managedCursor.getCount()==0){
			Toast.makeText(getApplicationContext(), this.getString(R.string.nema_dogadjaja_za_period) + 
					": " + new SimpleDateFormat("dd.MM.yyyy").format(now-(DateUtils.YEAR_IN_MILLIS)) + " - " + new SimpleDateFormat("dd.MM.yyyy").format(now+(DateUtils.YEAR_IN_MILLIS)), Toast.LENGTH_LONG).show();
        	
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
		if (verzija <= 8) {
			uriCalendar = Uri.parse("content://calendar/calendars");
			uriEvent = Uri.parse("content://calendar/events");
			//uriAttendee = Uri.parse("content://calendar/attendees");
		}
		else if (verzija <= 10) {
			uriCalendar = Uri.parse("content://com.android.calendar/calendars");
			uriEvent = Uri.parse("content://com.android.calendar/events");
			//uriAttendee = Uri.parse("content://com.android.calendar/attendees");
		}else {
			uriCalendar = Calendars.CONTENT_URI;
			//uriEvent = CalendarContract.Events.CONTENT_URI;
			uriEvent = Events.CONTENT_URI;
			//uriAttendee = Attendees.CONTENT_URI;
		}
		//android.os.Build.VERSION_CODES.GINGERBREAD
	}
	
	// Cijeli ovaj koncept je dosta klimav
	// Ne radi kako treba, ako nema dogaðaja koji se trebaju dogoditi
	// Bolje koristiti poziciciju Cursora i njegovu ukupno velièinu, pa to usporeðivati
	private void prethodniDogadjaji() {
		Log.i("TERMINI", "Poèetak fje. Count: " + managedCursor.getCount() + " Prva poz: " + prvaPozicija);
		int i = 0;
		if (prvaPozicija == -1) {
			Log.i("TERMINI", "Ušao u IF. Count: " + managedCursor.getCount());
			// Ovdje pripaziti da se ne bi rušilo ako je Cursor prazan
			if(managedCursor.getCount() >= 5){
				Log.i("TERMINI", "" + managedCursor.getCount());
				if(managedCursor.moveToPosition(managedCursor.getCount()-5)){
					Log.i("TERMINI", "Pomaknuo na prvu poz. Count: " + managedCursor.getCount());
					popisTermina = new ArrayList<TerminiDetalji>();
					
					prvaPozicija = managedCursor.getPosition();
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
					
				}
			}else if (managedCursor.getCount() > 0) {
				popisTermina = new ArrayList<TerminiDetalji>();
				managedCursor.moveToFirst();
				prvaPozicija = 0;
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
				
			}
		}else if(prvaPozicija>=0){
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
				//long godina = new DateUtils().YEAR_IN_MILLIS;
				//godina = godina * 2;
				long danas = System.currentTimeMillis();
				//danas -= godina;
				danas -= new DateUtils().YEAR_IN_MILLIS;
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
		Log.d("NOVIJI DOGAÐAJI", "Ušao u novijiDogadjaji. Zadnja pozicija: " + zadnjaPozicija);
			int i = 0;
			int velicinaCursora = managedCursor.getCount();
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
				//long godina = new DateUtils().YEAR_IN_MILLIS;
				//godina = godina * 2;
				long danas = System.currentTimeMillis();
				//danas += godina;
				danas += new DateUtils().YEAR_IN_MILLIS;
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
		case R.id.mTerminiKalendari:
			Log.i("NOVI_TAG", "Reakcija na Menu");
			odabirKalendara(listaSvihKalendara.toArray(new String[listaSvihKalendara.size()]), new ArrayList<String>(prefMapKalendari.keySet()));
			break;
		case R.id.mTerminiPretragaPoNaslovu:
			dialogPoNaslovu();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void dialogPoNaslovu() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.po_naslovu);
		
		final EditText input = new EditText(this);
		input.setText(pretragaPoNaslovu);
		alert.setView(input);
		
		alert.setPositiveButton(R.string.spremi, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			  pretragaPoNaslovu = input.getText().toString();
			  popisAktualnihDogadjaja();
			  }
			});

			alert.setNegativeButton(R.string.odustani, new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
				  //dialog.dismiss();
			  }
			});

			alert.show();
		// TODO Auto-generated method stub
		
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
	    	Log.i("POZICIJA", "Pozicija: " + position);
			TerminiDetalji ter = popisTermina.get(position);
			bu = new Bundle();
			bu.putString("ID", ter.getId());
			bu.putString("event_id", ter.getEventId());
			bu.putString("kalendar", ter.getKalendar());
			bu.putString("access", ter.getAccess());
			bu.putBoolean("uredi", false);
			bu.putString("Naslov", ter.getNaslov());
			
			// Ne znam što sam mislio kad sam ovo upisao
			// Ovdje æe zapravo uvijek isti calendar_id dohvaæati
			// jer se dohvaæa iz Cursora, a njegova se pozicija uopæe ne mijenja
			//Log.d("KAL ID", "calendar_id: " + managedCursor.getInt(managedCursor.getColumnIndex("calendar_id")));
			//bu.putInt("calendar_id", managedCursor.getInt(managedCursor.getColumnIndex("calendar_id")));
			
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
