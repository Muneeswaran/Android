package hr.diplomski.organizator;


import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;


public class Kontakti extends Activity {
	
	Intent intent;
	SharedPreferences profil;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kontakti);
		setupActionBar();
		
		profil = getSharedPreferences("hr.diplomski.organizator.PROFIL", 0);
	}
	
	public void klik(View v){
		switch(v.getId()){
		case R.id.bKontaktiProfil:
			intent = new Intent(this, Profil.class);
			startActivity(intent);
			break;
		case R.id.bKontaktiNaMail:
			Resources res = this.getResources();
			String[] arrayMob = res.getStringArray(R.array.array_mobitel);
			String[] arrayEmail = res.getStringArray(R.array.array_mail);
			String[] arrayIm = res.getStringArray(R.array.array_im);
			
			String textMail = getString(R.string.ime_prezime) + ": " + profil.getString("ime", "") + "\n";
			if(!profil.getString("broj", "").equals("")){
				String[] brojevi = profil.getString("broj", "").split(";");
				String[] vrstaBroja = profil.getString("broj_spinner", "").split(";");
				for(int i = 0; i<brojevi.length; i++){
					textMail += arrayMob[Integer.parseInt(vrstaBroja[i])] + ": " + brojevi[i] + "\n";
				}
				textMail += "\n";
			}
			if(!profil.getString("email", "").equals("")){
				String[] mailovi = profil.getString("email", "").split(";");
				String[] vrstaMaila = profil.getString("email_spinner", "").split(";");
				for (int i = 0; i < mailovi.length; i++) {
					textMail += arrayEmail[Integer.parseInt(vrstaMaila[i])] + ": " + mailovi[i] + "\n";
				}
				textMail += "\n";
			}
			if(!profil.getString("im", "").equals("")){
				String[] imovi = profil.getString("im", "").split(";");
				String[] vrstaIm = profil.getString("im_spinner", "").split(";");
				for (int i = 0; i < imovi.length; i++) {
					textMail += arrayIm[Integer.parseInt(vrstaIm[i])] + ": " + imovi[i] + "\n";
				}
			}
			textMail += "\n" + profil.getString("poruka", "") + "\n";

			Intent in = new Intent(Intent.ACTION_SEND);
			in.setType("plain/text");
			in.putExtra(Intent.EXTRA_EMAIL  , new String[]{""});
			in.putExtra(Intent.EXTRA_SUBJECT, "Moji kontakt podatci");
			in.putExtra(Intent.EXTRA_TEXT   , textMail);
			startActivity(Intent.createChooser(in, "Pošalji kontakt..."));
			break;
		case R.id.bKontaktiPregled:
			intent = new Intent(this, Pregled.class);
			startActivity(intent);
			break;
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
		getMenuInflater().inflate(R.menu.kontakti, menu);
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
