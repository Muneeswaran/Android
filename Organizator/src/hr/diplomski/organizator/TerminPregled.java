package hr.diplomski.organizator;

import java.text.SimpleDateFormat;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.database.Cursor;


public class TerminPregled extends Activity {
	
	String eventID = null;
	Bundle extra;
	TextView naslov, sati, datum, mjesto, kalendar, sadrzaj;
	Cursor managedCursor;
	Uri uriCalendar, uriEvent;
	int verzija;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_termin_pregled);
		// Show the Up button in the action bar.
		setupActionBar();

		verzija = android.os.Build.VERSION.SDK_INT;
		extra = new Bundle();
		extra = getIntent().getExtras();
		eventID = extra != null ? extra.getString("ID") : null;
		verzijaAndroid();
		dohvatiKalendar();
		
		inicijalizacija();
		
		podesiNaslov();
		podesiSadrzaj();
	}


	@SuppressLint("NewApi")
	private void verzijaAndroid() {
		if (verzija <= 8) {
			uriCalendar = Uri.parse("content://calendar/calendars");
			uriEvent = Uri.parse("content://calendar/events");
		}
		else if (verzija <= 10) {
			uriCalendar = Uri.parse("content://com.android.calendar/calendars");
			uriEvent = Uri.parse("content://com.android.calendar/events");
		}else {
			uriCalendar = CalendarContract.Calendars.CONTENT_URI;
			uriEvent = Events.CONTENT_URI;
		}
		
	}


	private void dohvatiKalendar() {
		//managedCursor = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
		
	}


	private void inicijalizacija() {
		naslov = (TextView) findViewById(R.id.tvTerminPregledNaslov);
		sati = (TextView) findViewById(R.id.tvTerminPregledSati);
		datum = (TextView) findViewById(R.id.tvTerminPregledDatum);
		mjesto = (TextView) findViewById(R.id.tvTerminPregledMjesto);
		kalendar = (TextView) findViewById(R.id.tvTerminPregledKalendar);
		sadrzaj = (TextView) findViewById(R.id.tvTerminPregledSadrzaj);
		
	}


	private void podesiNaslov() {
		naslov.setText(extra.getString("Naslov"));

		long pocetak = extra.getLong("Poèetak");
		long kraj = extra.getLong("kraj");
		
		if(extra.getString("cijeli_dan").equals("1")){
			sati.setText(R.string.cijeli_dan);
			datum.setText(new SimpleDateFormat("E, dd.MM.yyyy").format(pocetak));
		}else {
			if((extra.getLong("kraj")-extra.getLong("Poèetak"))>= 86400000){
				sati.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm").format(pocetak) + 
						" - " + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(kraj));
				datum.setVisibility(View.GONE);
			}else{
				sati.setText(new SimpleDateFormat("HH:mm").format(pocetak) + " - " + new SimpleDateFormat("HH:mm").format(kraj));
				datum.setText(new SimpleDateFormat("E, dd.MM.yyyy").format(pocetak));
			}
		}
		
		try {
			if(extra.getString("mjesto").equals("")){
				mjesto.setVisibility(View.GONE);
			}else{
				mjesto.setText(extra.getString("mjesto"));
			}
		} catch (Exception e) {
			mjesto.setVisibility(View.GONE);
		}
		
	}
	
	
	private void podesiSadrzaj() {
		
		try {
			Cursor kursor = getContentResolver().query(uriCalendar, new String[] {"_id", "name"}, "_id=" + extra.getString("kalendar"), null, null);
			kursor.moveToFirst();
			kalendar.setBackgroundColor(extra.getInt("boja"));
			kalendar.setText(kursor.getString(kursor.getColumnIndex("name")));
			sadrzaj.setText(extra.getString("sadržaj"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
		getMenuInflater().inflate(R.menu.termin_pregled, menu);
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
		case R.id.mTerminiPregledUredi:
			if(extra.getString("access").equals("700")){
				Intent data = new Intent();
				data.putExtra("akcija", "zatvori");
				setResult(RESULT_OK, data);
				Intent i = new Intent(this, NoviTermin.class);
				extra.putBoolean("uredi", true);
				i.putExtras(extra);
				startActivity(i);
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
		case R.id.mTerminiPregledKopiraj:
			Intent data = new Intent();
			data.putExtra("akcija", "zatvori");
			setResult(RESULT_OK, data);
			// Treba pripaziti kada se pokrene klasa NoviTermin da odabere odgovarajuæi kalendar
			urediTermin();
			finish();
			break;
		case R.id.mTerminiPregledObrisi:
			if(extra.getString("access").equals("700")){
		    new AlertDialog.Builder(this)
		        .setIcon(android.R.drawable.ic_dialog_alert)
		        .setTitle(R.string.pozor)
		        .setMessage(R.string.obrisati_termin)
		        .setPositiveButton(R.string.obrisi, new DialogInterface.OnClickListener()
		    {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		        	getContentResolver().delete(uriEvent, "_id=" + extra.getString("event_id"), null);
		        	// popisTermina();
		        	// ideja je bila da se onaj popis zatvori kada se odabere dogaðaja, da bi se onda ponovno pretražila baza kada se otvori
		        	// na kraju riješio sa startActivityForResult()
		        	Intent data = new Intent();
		        	data.putExtra("akcija", "ponovno");
		        	setResult(RESULT_OK, data);
		        	finish();
		        }

		    })
		    .setNegativeButton(R.string.odustani, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
		    .show();
				
			}else{
				Toast.makeText(getApplicationContext(), this.getString(R.string.ovaj_termin_se_ne_moze_obrisati), Toast.LENGTH_LONG).show();
				
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void urediTermin() {
		Intent i = new Intent(this, NoviTermin.class);
		i.putExtras(extra);
		startActivity(i);
		finish();
		
	}

}
