package hr.diplomski.organizator;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.accounts.OnAccountsUpdateListener;
import android.annotation.TargetApi;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public class NoviKontakt extends Activity implements OnAccountsUpdateListener{

    public static final String TAG = "Novi kontakt";
    
	EditText ime_prezime, broj, email, im, edt;
	Spinner accountiSpinner, spMobitel, spMail, spIM, spn;
	LinearLayout llMobitel, llEmail, llIM;
	View layoutMob, layoutEmail, layoutIM;
	Button btn;
	LayoutInflater inflater;
	int iIM = 0;
	int iMob = 0;
	int iMail = 0;
	int i = 0;
	int mob = 0;
	int em = 0;
	Boolean noviMobitel = true;
	Boolean noviEmail = true;
	Boolean noviIM = true;
    private ArrayList<AccountData> mAccounts;
    private AccountAdapter mAccountAdapter;
    private AccountData mSelectedAccount;
    Cursor kursorGrupe;
    ArrayList<String> izabraneGrupe = new ArrayList<String>();
    String[] imeGrupa;
    Map<String, Long> map = new HashMap<String, Long>();
    //private Spinner mAccountSpinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_novi_kontakt);
		// Show the Up button in the action bar.
		setupActionBar();
		
		init();
		//dodajElement();
		//dodajElement();
		//EditText et = (EditText) findViewById(2);
		//ime_prezime.setText(et.getText().toString());
	}

	private void init() {
		ime_prezime = (EditText) findViewById(R.id.etImePrezime);
		broj = (EditText) findViewById(R.id.etMobitel);
		email = (EditText) findViewById(R.id.etEmail);
		im = (EditText) findViewById(R.id.etNoviKontaktIM);
		
		spMobitel = (Spinner) findViewById(R.id.sNoviKontaktMobitel);
		spMail = (Spinner) findViewById(R.id.sNoviKontaktEmail);
		spIM = (Spinner) findViewById(R.id.sNoviKontaktIM);
		
		// Pripreme za account spinner
		accountiSpinner = (Spinner) findViewById(R.id.sNoviKontaktAccount);
        mAccounts = new ArrayList<AccountData>();
        mAccountAdapter = new AccountAdapter(this, mAccounts);
        accountiSpinner.setAdapter(mAccountAdapter);

        // Prepare the system account manager. On registering the listener below, we also ask for
        // an initial callback to pre-populate the account list.
        AccountManager.get(this).addOnAccountsUpdatedListener(this, null, true);

        // Register handlers for UI elements
        accountiSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long i) {
                updateAccountSelection();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // We don't need to worry about nothing being selected, since Spinners don't allow
                // this.
            }
        });
		
        // Layouti unutar activity_novi_kontakt
        // Tu �e se dodavati odgavaraju�i Layouti kada se pritisne tipka '+'
		inflater = getLayoutInflater();
		llMobitel = (LinearLayout) findViewById(R.id.llNoviKontaktMobitel);
		llEmail = (LinearLayout) findViewById(R.id.llNoviKontaktEmail);
		llIM = (LinearLayout) findViewById(R.id.llNoviKontaktIM);
		
		broj.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(noviMobitel){
					//dodajMobitel();
					dodajMob();
					noviMobitel = false;
					broj.setId(1000);
					spMobitel.setId(1100);
				}
			}
		});
		email.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(noviEmail){
					dodajMail();
					noviEmail = false;
					email.setId(3000);
					spMail.setId(3100);
				}
				
			}
		});
		im.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(noviIM){
					dodajIM();
					noviIM = false;
					im.setId(5000);
					spIM.setId(5100);
				}
				
			}
		});
		
		
	}
	
	private void dodajMob(){
		if (mob<20) {
			++mob;
			layoutMob = inflater.inflate(R.layout.layout_za_mobitel, null);
			llMobitel.addView(layoutMob);
			layoutMob.setId(100 + mob);
			btn = (Button) findViewById(R.id.bNoviKontaktNoviMob);
			btn.setId(10 + mob);
			if (mob > 1) {
				for (int i = 1; i < mob; i++) {
					btn = (Button) findViewById(i + 10);
					btn.setText("-");
				}
			}
			edt = (EditText) findViewById(R.id.etNoviKontaktUnos1);
			edt.setId(1000 + mob);
			edt.setInputType(InputType.TYPE_CLASS_PHONE);
			spn = (Spinner) findViewById(R.id.sNoviKontaktSpinner1);
			spn.setId(1100 + mob);
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
			if (iMail > 1) {
				for (int i = 1; i < iMail; i++) {
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
			if (iIM > 1) {
				for (int i = 1; i < iIM; i++) {
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
	
	public void klik(View v){
		int id = v.getId();
		switch (id) {
		case R.id.bNoviKSpremi:
			spremi();
			break;
		case R.id.bNoviKPlus:
			Intent intentNoviK = new Intent(this, NoviKontaktPopUp.class);
			startActivityForResult(intentNoviK, 0);
			break;
		case R.id.bNoviKOdustani:
			finish();
			break;
		case R.id.bNoviKontaktGrupe:
			odaberiGrupe();
			break;
		default:
			if(id>=10 && id<30){
				if(id==(mob+10)){
					dodajMob();
				}else{
					layoutMob = (LinearLayout) findViewById(90+id);
					llMobitel.removeView(layoutMob);
					int max=mob+10;
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
					mob-=1;
					
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
	
	private void spremi() {
		/*
		String sime = ime.getText().toString();
		String sprezime = prezime.getText().toString();
		String sbroj = broj.getText().toString();
		String smail = email.getText().toString();
		*/
		
		/*
		// Ovdje je Insert or Edit
		Intent i = new Intent(Intent.ACTION_INSERT_OR_EDIT);
		//Intent i = new Intent(Intent.ACTION_INSERT);
		i.setType(Contacts.CONTENT_ITEM_TYPE);
		i.putExtra(Insert.NAME, sime);
		i.putExtra(Insert.PHONE, sbroj);
		i.putExtra(Insert.EMAIL, smail);
		i.putExtra(Insert.IM_PROTOCOL, "ajaja");
		
		i.putExtra(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
		i.putExtra(Email.TYPE, Email.TYPE_CUSTOM);
		i.putExtra(Email.LABEL, "Green Bot");
		i.putExtra(Email.ADDRESS, "android@android.com");
		startActivity(i);
		*/
		
		//Intent e = new Intent(this, Kontakti.class);
		//startActivity(e);
		//startActivity(Intent.createChooser(i, "Po�alji kontakt..."));
		
		
	        
	        // Ovo sam izbacio; upute na�o na http://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Im.html
	        /*
	                .withValue(ContactsContract.CommonDataKinds.Im.TYPE, ContactsContract.CommonDataKinds.Im.TYPE_CUSTOM)
	                .withValue(ContactsContract.CommonDataKinds.Im.LABEL, "Twitter")
	         */
	        
		 if (!ime_prezime.getText().toString().equals("")) {
			//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------
			// Ovo uzgleda radi, samo treba rije�iti s accountom ( http://stackoverflow.com/questions/4744187/how-to-add-new-contacts-in-android )
			// Accounti su opisani u ContactAdder primjeru
			// Mo�da ovo pogledati kako napraviti custome nazive za polja (Skype itd.) http://developer.android.com/reference/android/provider/ContactsContract.Intents.Insert.html
			//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
			// Na ovoj stranici se spominje da ContentProviderOperation kvalitetnije zamjenjue batch operacije insert, delete, update
			// http://developer.android.com/reference/android/provider/ContactsContract.Data.html
			ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.RawContacts.CONTENT_URI)
					.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, mSelectedAccount.getType())
					.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, mSelectedAccount.getName())
					.build());
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
							ime_prezime.getText().toString()).build());
			
			/*Log.i("Provjera", "Duljina: "+ime_i_prezime.length);
			if(ime_i_prezime.length > 1){
				Log.i("NoviKontakt", "U�ao je u if");
				ime = new String();
				for(int i = 1; i<ime_i_prezime.length; i++){
					ime += ime_i_prezime[i] + " ";
					Log.i("For", ime);
				}
				ops.add(ContentProviderOperation
						.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(
								ContactsContract.Data.RAW_CONTACT_ID, 0)
						.withValue(
								ContactsContract.Data.MIMETYPE,
								ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
						.withValue(
								ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
								"AaaaTest").build());
			}*/
			
			if(izabraneGrupe.size()>0){
				for(String grupa : izabraneGrupe){
					ops.add(ContentProviderOperation
							.newInsert(ContactsContract.Data.CONTENT_URI)
							.withValueBackReference(
									ContactsContract.Data.RAW_CONTACT_ID, 0)
							.withValue(
									ContactsContract.Data.MIMETYPE,
									ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE)
							.withValue(
									ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID,
									map.get(grupa)).build());
					
				}
			}
			
			for (int i = 0; i <= mob; i++) {
				edt = (EditText) findViewById(1000+i);
				spn = (Spinner) findViewById(1100+i);
				Object value = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
				try {
					switch (spn.getSelectedItemPosition()) {
					case 0:
						value = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
						break;
					case 1:
						value = ContactsContract.CommonDataKinds.Phone.TYPE_HOME;
						break;
					case 2:
						value = ContactsContract.CommonDataKinds.Phone.TYPE_WORK;
						break;
					case 3:
						value = ContactsContract.CommonDataKinds.Phone.TYPE_OTHER;
						break;
					}
				} catch (Exception e) {
					
				}
				if (!broj.getText().toString().equals("")) {
					ops.add(ContentProviderOperation
							.newInsert(ContactsContract.Data.CONTENT_URI)
							.withValueBackReference(
									ContactsContract.Data.RAW_CONTACT_ID, 0)
							.withValue(
									ContactsContract.Data.MIMETYPE,
									ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
							.withValue(
									ContactsContract.CommonDataKinds.Phone.NUMBER,
									edt.getText().toString())
							.withValue(ContactsContract.CommonDataKinds.Phone.TYPE, value).build());
				}
			}
			
			for (int i = 0; i <= iMail; i++) {
				edt = (EditText) findViewById(3000+i);
				spn = (Spinner) findViewById(3100+i);
				Object value = Email.TYPE_HOME;
				try {
					switch (spn.getSelectedItemPosition()) {
					case 0:
						value = Email.TYPE_HOME;
						break;
					case 1:
						value = Email.TYPE_WORK;
						break;
					case 2:
						value = Email.TYPE_OTHER;
						break;
					}
				} catch (Exception e) {
					
				}
				if (!email.getText().toString().equals("")) {
					ops.add(ContentProviderOperation
							.newInsert(ContactsContract.Data.CONTENT_URI)
							.withValueBackReference(
									ContactsContract.Data.RAW_CONTACT_ID, 0)
							.withValue(
									ContactsContract.Data.MIMETYPE,
									ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
							.withValue(
									ContactsContract.CommonDataKinds.Email.DATA,
									edt.getText().toString())
							.withValue(
									ContactsContract.CommonDataKinds.Email.TYPE,
									value).build());
							//.withValue(
								//	ContactsContract.CommonDataKinds.Email.LABEL,
									//"Bender's Big Score").build());
				}
			}
			
			for (int i = 0; i <= iIM; i++) {
				edt = (EditText) findViewById(5000+i);
				spn = (Spinner) findViewById(5100+i);
				String protokol = "Twitter";
				
				try {
					protokol = spn.getSelectedItem().toString();
				} catch (Exception e) {
					
				}
				
				if (!im.getText().toString().equals("")) {
					ops.add(ContentProviderOperation
							.newInsert(ContactsContract.Data.CONTENT_URI)
							.withValueBackReference(
									ContactsContract.Data.RAW_CONTACT_ID, 0)
							.withValue(
									ContactsContract.Data.MIMETYPE,
									ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)
							.withValue(
									ContactsContract.CommonDataKinds.Im.DATA,
									edt.getText().toString())
							.withValue(
									ContactsContract.CommonDataKinds.Im.PROTOCOL,
									ContactsContract.CommonDataKinds.Im.PROTOCOL_CUSTOM)
							.withValue(
									ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL,
									protokol).build());
				}
			}
			try {
				getContentResolver()
						.applyBatch(ContactsContract.AUTHORITY, ops);
				Toast.makeText(this, R.string.kontakt_je_spremljen,
						Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(this, "Exception: " + e.getMessage(),
						Toast.LENGTH_SHORT).show();
			}
			//---------------------------------------------------------------------------------------------------------------------------------------------------------------
		}else{
			Toast.makeText(this, R.string.upisite_ime,Toast.LENGTH_SHORT).show();
		}
		 
		// http://developer.android.com/reference/android/provider/ContactsContract.Intents.Insert.html#IM_PROTOCOL
		// Ru�i se
		/*
		ArrayList<ContentValues> data = new ArrayList<ContentValues>();
	
		  ContentValues row1 = new ContentValues();
		  row1.put(Data.MIMETYPE, Organization.CONTENT_ITEM_TYPE);
		  row1.put(Organization.COMPANY, "Android");
		  data.add(row1);
	
		  ContentValues row2 = new ContentValues();
		  row2.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
		  row2.put(Email.TYPE, Email.TYPE_CUSTOM);
		  row2.put(Email.LABEL, "Green Bot");
		  row2.put(Email.ADDRESS, "android@android.com");
		  data.add(row2);
	
		  Intent intent = new Intent(Intent.ACTION_INSERT, Contacts.CONTENT_URI);
		  intent.putParcelableArrayListExtra(Insert.DATA, data);
	
		  startActivity(intent);
		*/
		 
		/*
		ContentValues cv = new ContentValues();
		cv.put(ContactsContract.Data.RAW_CONTACT_ID, 400);
		cv.put(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
		cv.put(ContactsContract.CommonDataKinds.Phone.NUMBER, sbroj);
		
		ContentResolver contentResolver = getContentResolver();
		contentResolver.insert(ContactsContract.Data.CONTENT_URI, cv);
		*/
		 //Toast.makeText(getApplicationContext(), "Krivo pode�eno vrijeme", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 0){
			if(data.getExtras().getString("akcija").equals("profil")){
				//spremi();
				Intent i = new Intent(this, Kontakti.class);
				startActivity(i);
				finish();
			}
			if(data.getExtras().getString("akcija").equals("termin")){
				//spremi();
				Intent i = new Intent(this, NoviTermin.class);
				startActivity(i);
				finish();
			}
			if(data.getExtras().getString("akcija").equals("izadji")){
				//spremi();
				finish();
				// http://stackoverflow.com/questions/2042222/close-application-and-launch-home-screen-on-android
				moveTaskToBack(true);
			}
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
		getMenuInflater().inflate(R.menu.novi_kontakt, menu);
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
	
	/**
     * Called when this activity is about to be destroyed by the system.
     */
    @Override
    public void onDestroy() {
        // Remove AccountManager callback
        AccountManager.get(this).removeOnAccountsUpdatedListener(this);
        super.onDestroy();
    }

    /**
     * Updates account list spinner when the list of Accounts on the system changes. Satisfies
     * OnAccountsUpdateListener implementation.
     */
    public void onAccountsUpdated(Account[] a) {
        Log.i(TAG, "Account list update detected");
        // Clear out any old data to prevent duplicates
        mAccounts.clear();

        // Get account data from system
        AuthenticatorDescription[] accountTypes = AccountManager.get(this).getAuthenticatorTypes();

        // Populate tables
        for (int i = 0; i < a.length; i++) {
            // The user may have multiple accounts with the same name, so we need to construct a
            // meaningful display name for each.
            String systemAccountType = a[i].type;

            
            Log.i(TAG, "a: " + a[i]);
            Log.i(TAG, "systemAccountType(tj. 'a[i].type'): " + systemAccountType + "\nAccountType: " + accountTypes[i]);
            
            if (systemAccountType.equals("com.google")) {
				AuthenticatorDescription ad = getAuthenticatorDescription(
						systemAccountType, accountTypes);
				AccountData data = new AccountData(a[i].name, ad);
				mAccounts.add(data);
			}
        }

        // Update the account spinner
        mAccountAdapter.notifyDataSetChanged();
    }

    /**
     * Obtain the AuthenticatorDescription for a given account type.
     * @param type The account type to locate.
     * @param dictionary An array of AuthenticatorDescriptions, as returned by AccountManager.
     * @return The description for the specified account type.
     */
    private static AuthenticatorDescription getAuthenticatorDescription(String type,
            AuthenticatorDescription[] dictionary) {
        for (int i = 0; i < dictionary.length; i++) {
            if (dictionary[i].type.equals(type)) {
            	Log.i(TAG, "Dictionary: " + dictionary[i]);
                return dictionary[i];
            }
        }
        // No match found
        throw new RuntimeException("Unable to find matching authenticator");
    }

    /**
     * Update account selection. If NO_ACCOUNT is selected, then we prohibit inserting new contacts.
     */
    private void updateAccountSelection() {
        // Read current account selection
        mSelectedAccount = (AccountData) accountiSpinner.getSelectedItem();
        
        try {
			kursorGrupe = getContentResolver().query(ContactsContract.Groups.CONTENT_SUMMARY_URI, 
					new String[] {"_id", "title", ContactsContract.Groups.ACCOUNT_NAME, ContactsContract.Groups.ACCOUNT_TYPE}, 
					"ACCOUNT_NAME=? AND ACCOUNT_TYPE=?", new String[] {mSelectedAccount.getName(), mSelectedAccount.getType()}, "TITLE COLLATE LOCALIZED ASC");
			int title = kursorGrupe.getColumnIndex("title");
			int id = kursorGrupe.getColumnIndex("_id");
			int i = 0;
			imeGrupa = new String[kursorGrupe.getCount()];
			map.clear();
			izabraneGrupe.clear();
			if(kursorGrupe.moveToFirst()){
				do{
					imeGrupa[i] = kursorGrupe.getString(title);
					// Ovo mo�da i nije najbolje jer je mo�da mogu�e da postoje dvije grupe s istim imenom
					map.put( kursorGrupe.getString(title), kursorGrupe.getLong(id));
					++i;
					//imeGrupa.add(kursorGrupe.getString(index));
				}while(kursorGrupe.moveToNext());
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * A container class used to repreresent all known information about an account.
     */
    private class AccountData {
        private String mName;
        private String mType;
        private CharSequence mTypeLabel;
        private Drawable mIcon;

        /**
         * @param name The name of the account. This is usually the user's email address or
         *        username.
         * @param description The description for this account. This will be dictated by the
         *        type of account returned, and can be obtained from the system AccountManager.
         */
        public AccountData(String name, AuthenticatorDescription description) {
            mName = name;
            if (description != null) {
                mType = description.type;

                // The type string is stored in a resource, so we need to convert it into something
                // human readable.
                String packageName = description.packageName;
                PackageManager pm = getPackageManager();

                if (description.labelId != 0) {
                    mTypeLabel = pm.getText(packageName, description.labelId, null);
                    if (mTypeLabel == null) {
                        throw new IllegalArgumentException("LabelID provided, but label not found");
                    }
                } else {
                    mTypeLabel = "";
                }

                if (description.iconId != 0) {
                    mIcon = pm.getDrawable(packageName, description.iconId, null);
                    if (mIcon == null) {
                        throw new IllegalArgumentException("IconID provided, but drawable not " +
                                "found");
                    }
                } else {
                    mIcon = getResources().getDrawable(android.R.drawable.sym_def_app_icon);
                }
            }
        }

        public String getName() {
            return mName;
        }

        public String getType() {
            return mType;
        }

        public CharSequence getTypeLabel() {
            return mTypeLabel;
        }

        public Drawable getIcon() {
            return mIcon;
        }

        public String toString() {
            return mName;
        }
    }

    /**
     * Custom adapter used to display account icons and descriptions in the account spinner.
     */
    private class AccountAdapter extends ArrayAdapter<AccountData> {
        public AccountAdapter(Context context, ArrayList<AccountData> accountData) {
            super(context, android.R.layout.simple_spinner_item, accountData);
            setDropDownViewResource(R.layout.account_unos);
        }

        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            // Inflate a view template
            if (convertView == null) {
                LayoutInflater layoutInflater = getLayoutInflater();
                convertView = layoutInflater.inflate(R.layout.account_unos, parent, false);
            }
            TextView firstAccountLine = (TextView) convertView.findViewById(R.id.accountPrviRed);
            TextView secondAccountLine = (TextView) convertView.findViewById(R.id.accountDrugiRed);
            ImageView accountIcon = (ImageView) convertView.findViewById(R.id.accountIcon);

            // Populate template
            AccountData data = getItem(position);
            firstAccountLine.setText(data.getName());
            secondAccountLine.setText(data.getTypeLabel());
            Drawable icon = data.getIcon();
            if (icon == null) {
                icon = getResources().getDrawable(android.R.drawable.ic_menu_search);
            }
            accountIcon.setImageDrawable(icon);
            return convertView;
        }
    }
    
    public void odaberiGrupe(){
    	int count = imeGrupa.length;
    	boolean[] izbor = new boolean[count];
    	
    	for (int i = 0; i<count; i++) {
			izbor[i] = izabraneGrupe.contains(imeGrupa[i]);
		}
    	
    	DialogInterface.OnMultiChoiceClickListener odabraneGrupe = new DialogInterface.OnMultiChoiceClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					izabraneGrupe.add(imeGrupa[which]);
				}else{
					izabraneGrupe.remove(imeGrupa[which]);
				}
			}
		};
    	 
    	  AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	  builder.setTitle(R.string.grupe);
    	  // Dialog builder sm pravi Multi Choice izbornikom
    	  // imeGrupa je moralo biti String[] jer ne prihva�a ArrayList
    	  builder.setMultiChoiceItems(imeGrupa, izbor, odabraneGrupe);
    	 
    	  AlertDialog dialog = builder.create();
    	  dialog.show();
    }

	/*private void dodajMobitel() {
		noviMobitel = true;
		iMob +=1;
		LinearLayout noviRed = new LinearLayout(this);
		noviRed.setOrientation(LinearLayout.HORIZONTAL);
		
		Spinner sp = new Spinner(this);
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.array_mobitel, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setBackgroundResource(android.R.drawable.btn_default);
		sp.setAdapter(adapter);
		
		EditText et = new EditText(this);
		et.setSingleLine(true);
		et.setInputType(InputType.TYPE_CLASS_PHONE);
		et.setHint(R.string.mobitel);
	
		noviRed.addView(et, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 2));
		noviRed.addView(sp, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 5));
		llMobitel.addView(noviRed);
		
	}*/
	
/*
	private void dodajElement() {
		iIM += 1;
		LinearLayout noviRed = new LinearLayout(this);
		noviRed.setOrientation(LinearLayout.HORIZONTAL);
		
		Spinner sp = new Spinner(this);
		//sp.setOnItemSelectedListener(this);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter.add("Ostalo");
		sp.setAdapter(adapter);
		sp.setId(101);
		
		EditText et = new EditText(this);
		et.setId(iIM);
		et.setSingleLine(true);
		//et.setInputType(TextUtils.CAP_MODE_WORDS);
		//LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		et.setText("Dodano");
		//et.setLayoutParams(params);
		LinearLayout ll = (LinearLayout) findViewById(R.id.llNoviKontakt);
		noviRed.addView(sp);
		noviRed.addView(et, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		ll.addView(noviRed);
		
	}*/

}
