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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Build;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;

/*import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.CalendarContract.Attendees;
import android.content.ContentProviderOperation;*/

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
		//Log.d("NOVI TERMIN", "Kreira mapu iz SharedPreferencesa");
		prefMapKalendari = new HashMap<String, Object>(preferences.getAll());
		if(!prefMapKalendari.keySet().isEmpty() && !listaSvihKalendara.isEmpty()){
			Log.d("NOVI TERMIN", "Ušao u IF");
			ArrayList<String> temp = new ArrayList<String>(prefMapKalendari.keySet());
			Collections.sort(temp, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return o1.compareToIgnoreCase(o2);
				}
			});
			e.clear();
			for(String strTemp:temp){
				if (listaSvihKalendara.contains(strTemp)) {
					e.putInt(strTemp, 1);
				}
			}
			e.commit();
			prefMapKalendari = new HashMap<String, Object>(preferences.getAll());
			Log.d("NOVI TERMIN", "Poslije poziva fje");
		}else{
			Log.d("NOVI TERMIN", "Ušao u ELSE");
		}
		//-----------------------------------------------------------------------------------------------------------------------------------


		lista = (ListView) findViewById(R.id.lvTermini);
		registerForContextMenu(lista);
		lista.setOnScrollListener(this);

		try {
			Log.i(DEBUG_TAG, "Starting Calendar Test");

			popisAktualnihDogadjaja();

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
			mapaSvihKalendara = new HashMap<String, String>();
			try {
				cur = getContentResolver().query(uriCalendar, projection, selection, selectionArgs, null);
				if(cur.moveToFirst()){
					int id = cur.getColumnIndex("_id");
					int calName = cur.getColumnIndex("name");
					do{
						mapaSvihKalendara.put(cur.getString(calName), cur.getString(id));
						Log.i("TAG", "Svi kal: " + cur.getLong(id));
					}while(cur.moveToNext());
				}
			} catch (Exception e) {

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
			Arrays.fill(izborKalendara, false);
		}
		DialogInterface.OnMultiChoiceClickListener listenerKalendari = new DialogInterface.OnMultiChoiceClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
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
				e.clear();
				for(String temp:odabraniKalendari){
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
		String selection = new String();
		String[] selectionArgs = null;
		String sortOrder = "begin ASC";
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

		}else{
			selection = "";
		}
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
		managedCursor = dohvatiKursor(projection,
				selection, null, sortOrder );

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

			//Log.i("KAL", "Cal id: " + managedCursor.getString(kalendar));

			prviPut = true;
			maxUcitanih = 20;
			prvaPozicija = -1;
			zadnjaPozicija = 0;
			do{
				if (managedCursor.getLong(begin)>=vrijeme){
					if(prviPut){
						prvaPozicija = managedCursor.getPosition();
						prviPut = false;
					}
					detalji = new TerminiDetalji();

					detalji.setPozicija(managedCursor.getPosition());
					detalji.setId(managedCursor.getString(id));
					detalji.setEventId(managedCursor.getString(event_id));
					detalji.setAccess(managedCursor.getString(access));
					detalji.setKalendar(managedCursor.getString(kalendar));
					detalji.setNaslov(managedCursor.getString(naslov));
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
		} else {
			//Log.i(DEBUG_TAG, "No Calendars");
		}
		adapter = new TerminiListAdapter(popisTermina, this);
		lista.setAdapter(adapter);
	}

	private Cursor dohvatiKursor(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		long now = new Date().getTime();

		//long window = 1000*60*60*24*365;
		// now - (2*DateUtils.YEAR_IN_MILLIS)....
		// ne koristim Uri iz verzijaAndroida() jer sam tamo stavio krivi Uri; (/events), a meni trebaju instances
		// zato ne radi za 4.0
		uriCalendar = Uri.parse("content://com.android.calendar/instances/when/" + (now - (DateUtils.YEAR_IN_MILLIS)) + "/" + (now + (DateUtils.YEAR_IN_MILLIS)));

		Cursor managedCursor = null;
		try {
			managedCursor = getContentResolver().query(uriCalendar, projection, selection,
					null, sortOrder);
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

	@SuppressLint("NewApi")
	private void verzijaAndroida() {
		if (verzija <= 8) {
			uriCalendar = Uri.parse("content://calendar/calendars");
			uriEvent = Uri.parse("content://calendar/events");
		}
		else if (verzija <= 10) {
			uriCalendar = Uri.parse("content://com.android.calendar/calendars");
			uriEvent = Uri.parse("content://com.android.calendar/events");
		}else {
			uriCalendar = Calendars.CONTENT_URI;
			uriEvent = Events.CONTENT_URI;
		}
	}
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
				maxUcitanih = i;
			}else{
				long danas = System.currentTimeMillis();
				danas -= new DateUtils().YEAR_IN_MILLIS;
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

			}while(managedCursor.moveToNext());
			detalji = new TerminiDetalji();
			detalji.setNaslov("Zadnji");
			popisTermina.add(detalji);

			zadnjaPozicija = managedCursor.getPosition();
			adapter = new TerminiListAdapter(popisTermina, this);
			lista.setAdapter(adapter);
			lista.setSelection((maxUcitanih));
		}else {
			long danas = System.currentTimeMillis();
			danas += new DateUtils().YEAR_IN_MILLIS;
			Toast.makeText(getApplicationContext(), this.getString(R.string.ucitani_su_dogadjaji_do) + " " + new SimpleDateFormat("dd.MM.yyyy").format(danas), Toast.LENGTH_LONG).show();
			//Log.d("NOVI", "U elseu: " + zadnjaPozicija);
		}

		//Log.d("NOVIJI DOGAÐAJI", "zadnjaPozicija: " + zadnjaPozicija + " maxUcitanih: " + maxUcitanih + " Velièina cursora: " + velicinaCursora);

		//Log.d("NOVIJI DOGAÐAJI", "Na kraju fje");
	}

	// Ove dvije metode moraju ostati jer sam implementirao onScroll
	// Htio slušati kad se dogodi scroll pa da automatski uèita nove elemente, ali odustao od toga
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
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
			}
		});

		alert.show();

	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		//Log.e("Context", "Stvorio context");
		getMenuInflater().inflate(R.menu.context_termini, menu);
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
						Intent data = new Intent();
						data.putExtra("akcija", "zatvori");
						setResult(RESULT_OK, data);
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
		int velicina = popisTermina.size();
		if(position==0){
			//Log.i("Pozicije", "Odabrano: " + position + " Velièina: " + velicina);
			prethodniDogadjaji();
		}else if (position==(velicina-1)) {
			//Log.i("Pozicije", "Odabrano: " + position + " Velièina: " + velicina);
			novijiDogadjaji();
		}else {
			//Log.i("Pozicije", "Odabrano: " + position + " Velièina: " + velicina);
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
		//Log.i("POZICIJA", "Pozicija: " + position);
		TerminiDetalji ter = popisTermina.get(position);
		bu = new Bundle();
		bu.putString("ID", ter.getId());
		bu.putString("event_id", ter.getEventId());
		bu.putString("kalendar", ter.getKalendar());
		bu.putString("access", ter.getAccess());
		bu.putBoolean("uredi", false);
		bu.putString("Naslov", ter.getNaslov());

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

			//Log.i(DEBUG_TAG, "Listing Calendar Event Details");

			do {

				Log.i(DEBUG_TAG, "**START Calendar Event Description**");

				for (int i = 0; i < managedCursor.getColumnCount(); i++) {
				}
				//Log.i(DEBUG_TAG, "**END Calendar Event Description**");
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
				}
				Log.i(DEBUG_TAG, "**END Calendar Event Description**");
			} while (managedCursor.moveToNext());
		} else {
			Log.i(DEBUG_TAG, "No Calendar Entry");
		}
	}

	/**
	 * @param projection
	 * @param selection
	 * @param path
	 * @return
	 */
	public Cursor getCalendarManagedCursor(String[] projection,
			String selection, String path) {
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
