package hr.diplomski.organizator;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
	
	Intent in;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Provjrava starost vCard datoteka koje spremam na SDcard
		// briše starije od 24h
		KontaktiVCard provjera = new KontaktiVCard(this);
		provjera.provjeraStarostiDatoteka();
		
	}
	
	public void klik(View v){
		switch (v.getId()) {
		case R.id.bGlavniNoviKontakt:
			in = new Intent(this, NoviKontakt.class);
			startActivity(in);
			break;
		case R.id.bGlavniKontakti:
			in = new Intent(this, Kontakti.class);
			startActivity(in);
			break;
		case R.id.bGlavniNoviTermin:
			in = new Intent(this, NoviTermin.class);
			startActivity(in);
			break;
		case R.id.bGlavniTermini:
			in = new Intent(this, Termini.class);
			startActivity(in);
			break;
		case R.id.bGlavniNovaBiljeska:
			in = new Intent(this, NovaBiljeska.class);
			startActivity(in);
			break;
		case R.id.bGlavniBiljeske:
			in = new Intent(this, Biljeske.class);
			startActivity(in);
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
