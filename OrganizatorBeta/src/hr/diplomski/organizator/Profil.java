package hr.diplomski.organizator;

import com.google.android.gms.internal.ed;

import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;

public class Profil extends Activity {
	
	SharedPreferences profil;
	SharedPreferences.Editor e;
	public static String PROFIL_PREFS = "hr.diplomski.organizator.PROFIL";
	private static int staticBMob = 10;
	private static int staticLLMob = 100;
	private static int staticETMob = 1000;
	private static int staticSMob = 1100;
	private static int staticBEmail = 30;
	private static int staticLLEmail = 300;
	private static int staticETEmail = 3000;
	private static int staticSEmail = 3100;
	private static int staticBIM = 50;
	private static int staticLLIM = 500;
	private static int staticETIM = 5000;
	private static int staticSIM = 5100;
	int iIM = 0;
	int iMob = 0;
	int iMail = 0;
	boolean noviMobitel = true;
	boolean noviMail = true;
	boolean noviIM = true;
	
	
	EditText ime, broj, email, twitter, poruka, edt;
	Spinner sMobitel, sMail, sIM, spn;
	Button btn;
	LinearLayout llMobitel, llEmail, llIM;
	View layoutMob;
	LayoutInflater inflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profil);
		// Show the Up button in the action bar.
		setupActionBar();
		
		profil = getSharedPreferences(PROFIL_PREFS, MODE_PRIVATE);
		e = profil.edit();
		init();
		podesiProfil();
	}

	private void init() {
		// UI elementi
		// EditText
		ime = (EditText) findViewById(R.id.etProfilImePrezime);
		broj = (EditText) findViewById(R.id.etProfilMobitel);
		broj.setInputType(InputType.TYPE_CLASS_PHONE);
		broj.setId(staticETMob);
		email = (EditText) findViewById(R.id.etProfilEmail);
		email.setId(staticETEmail);
		twitter = (EditText) findViewById(R.id.etProfilIM);
		twitter.setId(staticETIM);
		poruka = (EditText) findViewById(R.id.etProfilPoruka);
		
		//Spinner
		sMobitel = (Spinner) findViewById(R.id.sProfilMobitel);
		sMobitel.setId(staticSMob);
		sMail = (Spinner) findViewById(R.id.sProfilEmail);
		sMail.setId(staticSEmail);
		sIM = (Spinner) findViewById(R.id.sProfilIM);
		sIM.setId(staticSIM);
		
		//Button
		btn = (Button) findViewById(R.id.bProfilNoviMobitel);
		btn.setId(staticBMob);
		btn = (Button) findViewById(R.id.bProfilNoviEmail);
		btn.setId(staticBEmail);
		btn = (Button) findViewById(R.id.bProfilNoviIM);
		btn.setId(staticBIM);
		
		// Layouti
		inflater = getLayoutInflater();
		llMobitel = (LinearLayout) findViewById(R.id.llProfilMobitel);
		llEmail = (LinearLayout) findViewById(R.id.llProfilEmail);
		llIM = (LinearLayout) findViewById(R.id.llProfilIM);
		
		layoutMob = (LinearLayout) findViewById(R.id.llProfilM);
		layoutMob.setId(staticLLMob);
		layoutMob = (LinearLayout) findViewById(R.id.llProfilE);
		layoutMob.setId(staticLLEmail);
		layoutMob = (LinearLayout) findViewById(R.id.llProfilI);
		layoutMob.setId(staticLLIM);
		
		
		/*ime = (EditText) findViewById(R.id.etProfilIme);
		prezime = (EditText) findViewById(R.id.etProfilPrezime);
		broj = (EditText) findViewById(R.id.etProfilMobitel);
		email = (EditText) findViewById(R.id.etProfilEmail);
		twitter = (EditText) findViewById(R.id.etProfilTwitter);
		poruka = (EditText) findViewById(R.id.etProfilPoruka);*/
		
		//dohvaæanje teksta iz Shared Preferencesa
		/*ime.setText(profil.getString("ime", ""));
		prezime.setText(profil.getString("prezime", ""));
		broj.setText(profil.getString("broj", ""));
		email.setText(profil.getString("email", ""));
		twitter.setText(profil.getString("twitter", ""));
		poruka.setText(profil.getString("poruka", ""));*/
		
	}

	public void klik(View v){
		int id = v.getId();
		switch(id){
		case R.id.bProfilSpremi:
			//e.clear().commit();
			e.putString("ime", ime.getText().toString());
			
			//e.putString("broj", null);
			//e.putString("broj_spinner", null);
			String spremitiEDT = new String();
			String spremitiSPN = new String();
			String text = new String();
			for(int i = 0; i<=iMob; i++){
				edt = (EditText) findViewById(staticETMob+i);
				spn = (Spinner) findViewById(staticSMob+i);
				text = edt.getText().toString();
				if(!text.equals("")){
					spremitiEDT += text + ";";
					// Try sam dodao jer mi se ovdje uvijek rušilo
					// kasnije sam skužio da sam kod inicijalizacije krivo oznaèio spinnere za mobitel
					// a onda mi se nije dalo svugdje micati te blokove
					try {
						spremitiSPN += spn.getSelectedItemPosition() + ";";
					} catch (Exception e) {
						spremitiSPN +=  "0;";
					}
				}
			}
			e.putString("broj", spremitiEDT);
			e.putString("broj_spinner", spremitiSPN);
			
			spremitiEDT = new String();
			spremitiSPN = new String();
			text = new String();
			for(int i = 0; i<=iMail; i++){
				edt = (EditText) findViewById(staticETEmail+i);
				spn = (Spinner) findViewById(staticSEmail+i);
				text = edt.getText().toString();
				if(!text.equals("")){
					spremitiEDT += text + ";";
					try {
						spremitiSPN += spn.getSelectedItemPosition() + ";";
					} catch (Exception e) {
						spremitiSPN += "0;";
					}
				}
			}
			e.putString("email", spremitiEDT);
			e.putString("email_spinner", spremitiSPN);
			
			spremitiEDT = new String();
			spremitiSPN = new String();
			text = new String();
			for(int i = 0; i<=iIM; i++){
				edt = (EditText) findViewById(staticETIM+i);
				spn = (Spinner) findViewById(staticSIM+i);
				text = edt.getText().toString();
				if(!text.equals("")){
					spremitiEDT += text + ";";
					try {
						spremitiSPN += spn.getSelectedItemPosition() + ";";
					} catch (Exception e) {
						spremitiSPN += "0;";
					}
				}
			}
			e.putString("im", spremitiEDT);
			e.putString("im_spinner", spremitiSPN);
			
			e.putString("poruka", poruka.getText().toString());
			
			e.commit();
			finish();
			
			
			
			/*String[] splity = spremitiEDT.split(";");
			ime.setText(""+splity.length);
			ime.append(" " + splity[0]);*/
			
			/*String imeS = ime.getText().toString();
			String prezimeS = prezime.getText().toString();
			String brojS = broj.getText().toString();
			String emailS = email.getText().toString();
			String twitterS = twitter.getText().toString();
			String porukaS = poruka.getText().toString();*/
			
			/*e = profil.edit();
			e.putString("ime", imeS);
			e.putString("prezime", prezimeS);
			e.putString("broj", brojS);
			e.putString("email", emailS);
			e.putString("twitter", twitterS);
			e.putString("poruka", porukaS);
			e.commit()*/;
			//finish();
			
			break;
		case R.id.bProfilOdustani:
			finish();
			break;
		default:

			if(id>=10 && id<30){
				if(id==(iMob+10)){
					dodajMob();
				}else{
					layoutMob = (LinearLayout) findViewById(90+id);
					llMobitel.removeView(layoutMob);
					int max=iMob+10;
					// Nije mi dozvolio da ostane sam id
					for(int i = id+1; i<=max; i++){
						btn = (Button) findViewById(i);
						btn.setId(i-1);
						
						layoutMob = (LinearLayout) findViewById(90+i);
						layoutMob.setId(90+i-1);
						
						edt = (EditText) findViewById(990+i);
						edt.setId(990+i-1);
						
						spn = (Spinner) findViewById(1090+i);
						spn.setId(1090+i-1);
					}
					iMob-=1;
					
				}
			}else if (id>=30 && id<50) {
				if(id==(iMail+30)){
					dodajMail();
				}else {
					layoutMob = (LinearLayout) findViewById(270+id);
					llEmail.removeView(layoutMob);
					int max=iMail+30;
					// Nije mi dozvolio da ostane sam id
					for(int i = id+1; i<=max; i++){
						btn = (Button) findViewById(i);
						btn.setId(i-1);
						
						layoutMob = (LinearLayout) findViewById(270+i);
						layoutMob.setId(270+i-1);
						
						edt = (EditText) findViewById(2970+i);
						edt.setId(2970+i-1);
						
						spn = (Spinner) findViewById(3070+i);
						spn.setId(3070+i-1);
					}
					iMail-=1;
					
				}
				
			}else if (id>=50 && id<70) {
				if(id==(iIM+50)){
					dodajIM();
				}else {
					layoutMob = (LinearLayout) findViewById(450+id);
					llIM.removeView(layoutMob);
					int max=iIM+50;
					// Nije mi dozvolio da ostane sam id
					for(int i = id+1; i<=max; i++){
						btn = (Button) findViewById(i);
						btn.setId(i-1);
						
						layoutMob = (LinearLayout) findViewById(450+i);
						layoutMob.setId(450+i-1);
						
						edt = (EditText) findViewById(4950+i);
						edt.setId(4950+i-1);
						
						spn = (Spinner) findViewById(5050+i);
						spn.setId(5050+i-1);
					}
					iIM-=1;
				}
				
			}
			
			break;
		}
	}
	
	private void dodajMob(){
		if (iMob<20) {
			++iMob;
			layoutMob = inflater.inflate(R.layout.layout_za_mobitel, null);
			llMobitel.addView(layoutMob);
			layoutMob.setId(100 + iMob);
			btn = (Button) findViewById(R.id.bNoviKontaktNoviMob);
			btn.setId(10 + iMob);
			if (iMob >= 1) {
				for (int i = 0; i < iMob; i++) {
					btn = (Button) findViewById(i + 10);
					btn.setText("-");
				}
			}
			edt = (EditText) findViewById(R.id.etNoviKontaktUnos1);
			edt.setId(1000 + iMob);
			edt.setInputType(InputType.TYPE_CLASS_PHONE);
			spn = (Spinner) findViewById(R.id.sNoviKontaktSpinner1);
			spn.setId(1100 + iMob);
		}
		
	}
	
	private void dodajMail(){
		if (iMail<20) {
			++iMail;
			layoutMob = inflater.inflate(R.layout.layout_za_mobitel, null);
			llEmail.addView(layoutMob);
			layoutMob.setId(300 + iMail);
			btn = (Button) findViewById(R.id.bNoviKontaktNoviMob);
			btn.setId(30 + iMail);
			if (iMail >= 1) {
				for (int i = 0; i < iMail; i++) {
					btn = (Button) findViewById(i + 30);
					btn.setText("-");
				}
			}
			edt = (EditText) findViewById(R.id.etNoviKontaktUnos1);
			edt.setId(3000 + iMail);
			edt.setHint("E-mail");
			edt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			spn = (Spinner) findViewById(R.id.sNoviKontaktSpinner1);
			spn.setId(3100 + iMail);
			ArrayAdapter<CharSequence> adapter = ArrayAdapter
					.createFromResource(this, R.array.array_mail,
							android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spn.setBackgroundResource(android.R.drawable.btn_default);
			spn.setAdapter(adapter);
		}
		
	}
	
	private void dodajIM(){
		if (iIM<20) {
			++iIM;
			layoutMob = inflater.inflate(R.layout.layout_za_mobitel, null);
			llIM.addView(layoutMob);
			layoutMob.setId(500 + iIM);
			btn = (Button) findViewById(R.id.bNoviKontaktNoviMob);
			btn.setId(50 + iIM);
			if (iIM >= 1) {
				for (int i = 0; i < iIM; i++) {
					btn = (Button) findViewById(i + 50);
					btn.setText("-");
				}
			}
			edt = (EditText) findViewById(R.id.etNoviKontaktUnos1);
			edt.setId(5000 + iIM);
			edt.setHint(R.string.korisnicko_ime);
			edt.setInputType(InputType.TYPE_CLASS_TEXT);
			spn = (Spinner) findViewById(R.id.sNoviKontaktSpinner1);
			spn.setId(5100 + iIM);
			ArrayAdapter<CharSequence> adapter = ArrayAdapter
					.createFromResource(this, R.array.array_im,
							android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spn.setBackgroundResource(android.R.drawable.btn_default);
			spn.setAdapter(adapter);
		}
		
	}
	
	private void podesiProfil() {
		ime.setText(profil.getString("ime", ""));
		if (!profil.getString("broj", "").equals("")) {
			String[] sviBrojevi = profil.getString("broj", "").split(";");
			String[] sviBrojeviSpinner = profil.getString("broj_spinner", "").split(";");
			if (sviBrojevi != null) {
				edt = (EditText) findViewById(staticETMob);
				spn = (Spinner) findViewById(staticSMob);
				edt.setText(sviBrojevi[0]);
				spn.setSelection(Integer.parseInt(sviBrojeviSpinner[0]));
				//spn.setSelection(0);
				iMob = 0;
				for (int i = 1; i < sviBrojevi.length; i++) {
					dodajMob();
					edt = (EditText) findViewById(staticETMob + i);
					spn = (Spinner) findViewById(staticSMob + i);
					edt.setText(sviBrojevi[i]);
					spn.setSelection(Integer.parseInt(sviBrojeviSpinner[i]));
					//spn.setSelection(0);
				}
			}
		}
		if (!profil.getString("email", "").equals("")) {
			String[] sviMailovi = profil.getString("email", "").split(";");
			String[] sviMailoviSpinner = profil.getString("email_spinner", "").split(";");
			if (sviMailovi != null) {
				edt = (EditText) findViewById(staticETEmail);
				spn = (Spinner) findViewById(staticSEmail);
				edt.setText(sviMailovi[0]);
				spn.setSelection(Integer.parseInt(sviMailoviSpinner[0]));
				iMail = 0;
				for (int i = 1; i < sviMailovi.length; i++) {
					dodajMail();
					edt = (EditText) findViewById(staticETEmail + i);
					spn = (Spinner) findViewById(staticSEmail + i);
					edt.setText(sviMailovi[i]);
					spn.setSelection(Integer.parseInt(sviMailoviSpinner[i]));
				}
			}
		}
		
		if (!profil.getString("im", "").equals("")) {
			String[] sviIm = profil.getString("im", "").split(";");
			String[] sviImSpinner = profil.getString("im_spinner", "").split(";");
			if (sviIm != null) {
				edt = (EditText) findViewById(staticETIM);
				spn = (Spinner) findViewById(staticSIM);
				edt.setText(sviIm[0]);
				spn.setSelection(Integer.parseInt(sviImSpinner[0]));
				iIM = 0;
				for (int i = 1; i < sviIm.length; i++) {
					dodajIM();
					edt = (EditText) findViewById(staticETIM + i);
					spn = (Spinner) findViewById(staticSIM + i);
					edt.setText(sviIm[i]);
					spn.setSelection(Integer.parseInt(sviImSpinner[i]));
				}
			}
		}
		
		poruka.setText(profil.getString("poruka", ""));
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
		getMenuInflater().inflate(R.menu.profil, menu);
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
