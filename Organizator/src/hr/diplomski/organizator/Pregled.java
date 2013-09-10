package hr.diplomski.organizator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.accounts.OnAccountsUpdateListener;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract;

/*import android.content.ClipData.Item;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.provider.Contacts.Groups;
import android.provider.ContactsContract.CommonDataKinds;
import android.os.DropBoxManager.Entry;
import java.util.ResourceBundle.Control;
import java.util.Iterator;
import java.util.List;*/

public class Pregled extends Activity implements OnItemClickListener, OnAccountsUpdateListener {

	public static final String TAG = "Grupe";
	ContentResolver cr;
	ListView lv1;
	EditText pretrazi;
	ArrayAdapter<String> adapter;
	ArrayAdapter<Integer> adapInt;
	ListAdapter aint;
	Cursor cursor;
	TextView tv1;
	Spinner accountiSpinner;
    private ArrayList<AccountData> mAccounts;
    private AccountAdapter mAccountAdapter;
    private AccountData mSelectedAccount;
    Map<Long, Set<String>> mapa = new HashMap<Long, Set<String>>();
    //List<String> izabraniKontakti = new ArrayList<String>();
    Set<String> izabraniKontakti = new HashSet<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pregled);
		// Show the Up button in the action bar.
		setupActionBar();
		popisAccounta();
		
		// ListView trenutnog Activity-ja
		lv1 = (ListView) findViewById(R.id.lvPregledLista);
		// Kreiranje adaptera
		//adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		// Adapter se dodaje ListView
		lv1.setAdapter(adapter);
		// Omoguæuje pretraživanje popisa
		lv1.setOnItemClickListener(this);
		lv1.setTextFilterEnabled(true);
		//lv1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		//ispisiKontakte();
		
	}
	
	private void popisAccounta() {
		
		// Pripreme za account spinner
		accountiSpinner = (Spinner) findViewById(R.id.sPregledAccount);
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
		
	}

	private void ispisiKontakte() {
		try {
		
		//cursor = dohvatiKontakte(); 
		//cursor =  dohvatiGrupe();
			/*
			   String[] projection = new String[] {
					   ContactsContract.Groups._ID,
					   ContactsContract.Groups.TITLE,
					   ContactsContract.Groups.SUMMARY_COUNT
			   };
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME 
                + " COLLATE LOCALIZED ASC ";
                
		cursor = getContentResolver().query(ContactsContract.Groups.CONTENT_SUMMARY_URI, projection,
                null, null, null);
		*/
			/*
			   String[] projection = new String[] {
					   Data._ID,
					   Data.DISPLAY_NAME,
					   ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID
			   };
			   
			   cursor = getContentResolver().query(Data.CONTENT_URI, projection,
		                GroupMembership.GROUP_ROW_ID + "=8", null, null);
			*/
			//cursor = dohvatiGrupe();
		
		// Radi, ali bolje pogledaj malo više o Adapterima i Cursorima
		adapter.clear();
		String ime;
		int i = 0;
		Log.i("Cursor2", "Broj ovdje: " + cursor.getCount());
		if (cursor.moveToFirst()){
			do{
				//ime = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID));
				//ime = cursor.getString(cursor.getColumnIndex("ACCOUNT_NAME"));
				//ime = cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.Groups.TITLE)) + " : " + cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.Groups.SUMMARY_COUNT));
				ime = cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.Groups.TITLE));
				adapter.add(ime);
				
				i++;
				//Log.i("Test", cursor.getString(cursor.getColumnIndex("title")) + " : " 
						//+ cursor.getString(cursor.getColumnIndex(ContactsContract.Groups._ID)) + " : " );
				
				//Log.e(TAG, "ID unutar petlje: " + cursor.getPosition());
				Log.d("GRUPE" , "Ime: "  + cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.TITLE)) 
						+ "\nID: " + cursor.getString(cursor.getColumnIndex("_id"))
						+ "\nACCOUNT_NAME: " + cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.ACCOUNT_NAME))
						+ "\nACCOUNT_TYPE: " + cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.ACCOUNT_TYPE)));
			} while (cursor.moveToNext());
		}
		// Ne smije ga zatvoriti jer ga još trebam u onItemClick()
		//cursor.close();
		
		/* Radi, ali treba dugo; http://stackoverflow.com/questions/1721279/how-to-read-contacts-on-android-2-0?rq=1
		 * 
		Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null); 
		
		while (cursor.moveToNext()) {
			String contactId = cursor.getString(cursor.getColumnIndex( 
				   				ContactsContract.Contacts._ID)); 
		   //String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)); 
		      Cursor phones = getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, null, null); 
		      Log.e(TAG, "Phone");
		      while (phones.moveToNext()) { 
		         String phoneNumber = phones.getString(phones.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER));
		         adapter.add(phoneNumber);
		         Log.e(TAG, phoneNumber);
		      }
		   phones.close(); 
		   
		   Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null); 
		   while (emails.moveToNext()) { 
		      // This would allow you get several email addresses 
		      String emailAddress = emails.getString( 
		      emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)); 
		      adapter.add(emailAddress);
		      Log.e(TAG, emailAddress);
		   } 
		   emails.close();
		}
		cursor.close(); */
		
	} catch (Exception ex) {
		//Log.d(TAG, ex.toString());
	}
	}
	
	
	private Cursor dohvatiGrupe() {
		Uri uri = ContactsContract.Groups.CONTENT_URI;
		
		
		String[] projection = new String[] {
				ContactsContract.Groups._ID, ContactsContract.Groups.TITLE, ContactsContract.Groups.SUMMARY_COUNT};
				
		/*
		String[] projection = new String[] {
				ContactsContract.Groups._ID, ContactsContract.Groups.TITLE};
		*/
		//Na drugo mjesto staviti projection; null je jer želim pogeldati koje stupce sadrži
		return getContentResolver().query(uri, null, "ACCOUNT_NAME=? AND ACCOUNT_TYPE=?", 
				new String[] {mSelectedAccount.getName(), mSelectedAccount.getType()}, null);
	}

	public void klik(View v){
		switch(v.getId()){
		case R.id.bPregledOk:
			if(izabraniKontakti.size()>0){
				/*String[] str = izabraniKontakti.toArray(new String[0]);
				int count = str.length;
				String where = "";
				for(int i = 0; i<count-1; i++){
					where += Data.DISPLAY_NAME + "=? OR ";
				}
				where += Data.DISPLAY_NAME + "=?";
				Cursor c = getContentResolver().query(Phone.CONTENT_URI, null, where, str, Data.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
				Set<String> popis = new LinkedHashSet<String>();
				//List<String> popis = new ArrayList<String>();
				Map<String, Set<String>> konacnaMapa = new LinkedHashMap<String, Set<String>>();
				//Map<String, List<String>> konacnaMapa = new LinkedHashMap<String, List<String>>();
				if(c.moveToFirst()){
					int ime = c.getColumnIndex("display_name");
					int broj = c.getColumnIndex(Phone.NUMBER);
					int brojVrsta = c.getColumnIndex(Phone.TYPE);
					int label = c.getColumnIndex(Phone.LABEL);
					String prethodni = "";
					String unos;
					do{
						if(!prethodni.equals(c.getString(ime))){
							if(!prethodni.equals("")){
								konacnaMapa.put(prethodni, popis);
								//popis = new ArrayList<String>();
								popis = new LinkedHashSet<String>();
								Log.d("Mapa", "Spremio mapu: " + prethodni + " Velièina: " + popis.size());
							}
							Log.i("Prethodni", "Prethodni: " + prethodni);
							prethodni = c.getString(ime);
							if(c.getString(broj) != null){
								unos = Phone.getTypeLabel(this.getResources(), c.getInt(brojVrsta), c.getString(label)) + ": " + c.getString(broj);
								popis.add(unos);
								Log.d("Kontakt1", "Spremio kontakt: " + Phone.getTypeLabel(this.getResources(), c.getInt(brojVrsta), c.getString(label)) + ": " + c.getString(broj) );
							}
							// Treba riješiti što s kontaktima ako nemaju broj
						}else{
							if(c.getString(broj) != null){
								unos = Phone.getTypeLabel(this.getResources(), c.getInt(brojVrsta), c.getString(label)) + ": " + c.getString(broj);
								popis.add(unos);
								Log.d("Kontakt2", "Spremio kontakt: " + Phone.getTypeLabel(this.getResources(), c.getInt(brojVrsta), c.getString(label)) + ": " + c.getString(broj) );
							}
						}
					}while(c.moveToNext());
					konacnaMapa.put(prethodni, popis);
					Log.d("Mapa3", "Spremio mapu: " + prethodni + " Velièina: " + popis.size());
				}
				
				c = getContentResolver().query(Email.CONTENT_URI, null, where, str, Data.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
				if(c.moveToFirst()){
					int ime = c.getColumnIndex("display_name");
					int mail = c.getColumnIndex(Email.DATA);
					int mailVrsta = c.getColumnIndex(Email.TYPE);
					int labelm = c.getColumnIndex(Email.LABEL);
					String unos;
					String prethodni = "";
					do{
						if(!prethodni.equals(c.getString(ime))){
							if(!prethodni.equals("")){
								konacnaMapa.put(prethodni, popis);
								popis = new LinkedHashSet<String>();
							}
							prethodni = c.getString(ime);
							if(konacnaMapa.containsKey(prethodni)){
								popis = konacnaMapa.get(prethodni);
							}else {
								popis = new LinkedHashSet<String>();
							}
							if(c.getString(mail) != null){
								unos = Email.getTypeLabel(this.getResources(), c.getInt(mailVrsta), c.getString(labelm)) + ": " + c.getString(mail);
								popis.add(unos);
							}
						}else {
							if(c.getString(mail) != null){
								unos = Email.getTypeLabel(this.getResources(), c.getInt(mailVrsta), c.getString(labelm)) + ": " + c.getString(mail);
								popis.add(unos);
							}
						}
					}while(c.moveToNext());
					konacnaMapa.put(prethodni, popis);
					}
				
				// http://www.higherpass.com/Android/Tutorials/Working-With-Android-Contacts/
				// http://stackoverflow.com/questions/15243205/cant-get-the-email-address-from-contactscontract
				//c = getContentResolver().query(Data.CONTENT_URI, null, ContactsContract.Data.MIMETYPE + "=" + ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE + " AND " + where, str, Data.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
				//where = where + " AND " + ContactsContract.Data.MIMETYPE + "=?"; 
				//+ ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE;
				//String[] params = new String[str.length];
				//params = str;
				//params[str.length-1] = ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE;
				c = getContentResolver().query(Data.CONTENT_URI, null, where , str, Data.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
				Log.i("Pregled", "Query je prošao");
				if(c.moveToFirst()){
					int ime = c.getColumnIndex("display_name");
					int instant = c.getColumnIndex(Im.DATA);
					int imVrsta = c.getColumnIndex(Im.PROTOCOL);
					int labelm = c.getColumnIndex(Im.CUSTOM_PROTOCOL);
					String unos;
					String prethodni = "";
					int i = 0;
					do{
						Log.i("Pregled", "IF: " + i++);
						if(!prethodni.equals(c.getString(ime))){
							if(!prethodni.equals("")){
								konacnaMapa.put(prethodni, popis);
								popis = new LinkedHashSet<String>();
							}
							prethodni = c.getString(ime);
							if(konacnaMapa.containsKey(prethodni)){
								popis = konacnaMapa.get(prethodni);
							}else {
								popis = new LinkedHashSet<String>();
							}
							// iz nekog razloga dohvaæa opet sve (ime, broj,...), a pod imVrsta stavlja 'AIM'
							// na Android stranici za AIM stoji da je vrijednost 0; ne prikazuje AIM account
							if((c.getString(instant) != null) && (c.getInt(imVrsta) != 0)){
								//unos = Im.getTypeLabel(this.getResources(), c.getInt(imVrsta), c.getString(labelm)) + ": " + c.getString(instant);
								//unos = c.getString(labelm) + ": " + c.getString(instant);
								unos = Im.getProtocolLabel(this.getResources(), c.getInt(imVrsta), c.getString(labelm)) + ": " + c.getString(instant);
								popis.add(unos);
							}
						}else {
							if((c.getString(instant) != null) && (c.getInt(imVrsta) != 0)){
								//unos = Im.getTypeLabel(this.getResources(), c.getInt(imVrsta), c.getString(labelm)) + ": " + c.getString(instant);
								//unos = c.getString(labelm) + ": " + c.getString(instant);
								unos = Im.getProtocolLabel(this.getResources(), c.getInt(imVrsta), c.getString(labelm)) + ": " + c.getString(instant);
								popis.add(unos);
							}
						}
					}while(c.moveToNext());
					konacnaMapa.put(prethodni, popis);
					}
				
					String textMail = "";
					String kljuc;
					
					List<String> imena = new ArrayList<String>(konacnaMapa.keySet());
					//Set imena = new HashSet<String>(konacnaMapa.keySet());
					java.util.Collections.sort(imena);
					
					
					for(String temp : imena){
						textMail += temp + "\n";
						String[] konacniKontakti = konacnaMapa.get(temp).toArray(new String[0]);
						for(int i = 0; i<konacniKontakti.length; i++){
							textMail += konacniKontakti[i] + "\n";
						}
						textMail += "\n";
						
					}
					
					for(Map.Entry<String, Set<String>> entry : konacnaMapa.entrySet()){
						textMail += entry.getKey() + "\n";
						//Set<String> konacniSet = new HashSet<String>(entry.getValue());
						//String[] konacniKontakti = new String[konacniSet.size()];
						//konacniKontakti = konacniSet.toArray(new String[0]);
						String[] konacniKontakti = entry.getValue().toArray(new String[0]);
						for(int i = 0; i<konacniKontakti.length; i++){
							textMail += konacniKontakti[i] + "\n";
						}
						textMail += "\n";
					}
					
					for(HashMap.Entry<String, Set<String>> par: konacnaMapa.entrySet()){
						popis.clear();
						kljuc = par.getKey();
						Set<String> kontakti = par.getValue();
						textMail += kljuc + "\n";
						String[] vrijednosti = kontakti.toArray(new String[0]);
						for(int i = 0; i<vrijednosti.length; i++){
							textMail += vrijednosti[i] + "\n";
					}
					
					Intent in = new Intent(Intent.ACTION_SEND);
					in.setType("plain/text");
					in.putExtra(Intent.EXTRA_EMAIL  , new String[]{""});
					in.putExtra(Intent.EXTRA_SUBJECT, "Moji kontakt podatci");
					in.putExtra(Intent.EXTRA_TEXT   , textMail);
					startActivity(in);*/
				KontaktiVCard stringKarta = new KontaktiVCard(this);
				stringKarta.posaljiString(izabraniKontakti);
				finish();
					
				
				
				
				//c.moveToLast();
				//Log.d("Odabrano", "Ime: " + c.getString(c.getColumnIndex("display_name")) + " Broj: " + c.getString(c.getColumnIndex(Phone.NUMBER)));
			}else{
				Toast.makeText(this, R.string.nije_odabran_kontakt, Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.bPregledCancel:
			//finish();
			if(izabraniKontakti.size()>0){
				KontaktiVCard vKarta = new KontaktiVCard(this);
				vKarta.posaljiVCard(izabraniKontakti);
				finish();
				/*final String vFileAdresa = "organizator-kontakti.vcf";
				
				String[] str = izabraniKontakti.toArray(new String[0]);
				int count = str.length;
				String where = "";
				for(int i = 0; i<count-1; i++){
					where += Data.DISPLAY_NAME + "=? OR ";
				}
				where += Data.DISPLAY_NAME + "=?";
				Cursor c = getContentResolver().query(Contacts.CONTENT_URI, null, where, str, Data.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
				
				// Uvijek isto za vCard na Stacku
				// http://stackoverflow.com/questions/12046936/android-how-to-save-contacts-to-sdcard-as-vcard-without-duplicates?rq=1
				// http://stackoverflow.com/questions/8035841/how-to-get-android-contacts-in-vcard-format-using-android-native-api
				// http://stackoverflow.com/questions/8701727/extract-contact-list-in-vcf-format
				if(c.moveToFirst()){
                    //String path = Environment.getExternalStorageDirectory().toString() + File.separator + "organizator-kontakti-"+i+".vcf";
					// Provjerava da li veæ postoji file s tim imenom
					String path = Environment.getExternalStorageDirectory().toString() + File.separator + vFileAdresa;
					File file = new File(path);
					int brojFile = 1;
                	for(int i = 1; i<=5; i++){
                    	file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "organizator-kontakti-"+i+".vcf");
                		brojFile = i;
                    	if(!file.exists()){
                    		path = Environment.getExternalStorageDirectory().toString() + File.separator + "organizator-kontakti-"+ i +".vcf";
                    		// Postavljeno na 6 da ne bi ulazio na sljedeæi 'if'
                    		brojFile = 6;
                    		break;
                    	}
                	}
                	if(brojFile==5){
                		//file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "organizator-kontakti-"+brojFile+".vcf");
                		path = Environment.getExternalStorageDirectory().toString() + File.separator + "organizator-kontakti-"+ brojFile +".vcf";
                		file = new File(path);
                		file.delete();
                	}
                	
                    //String path = vFileAdresa;
                    String vCard = "";
					do{
				        String lookupKey = c.getString(c.getColumnIndex(Contacts.LOOKUP_KEY));
				        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
				        AssetFileDescriptor fd;
				        try {
			                fd = getContentResolver()
			                        .openAssetFileDescriptor(uri, "r");
			                FileInputStream fis = fd.createInputStream();
			                byte[] b = new byte[(int) fd.getDeclaredLength()];
			                fis.read(b);
			                //vCard = new String(b);
			                vCard += new String(b);
			                //Log.i("---Test---", vCard);
							
						} catch (Exception e) {
							// TODO: handle exception
						}
					}while(c.moveToNext());
                    try {
						FileOutputStream mFileOutputStream = new FileOutputStream(path, true);
                    	//FileOutputStream mFileOutputStream = openFileOutput(path, Context.MODE_WORLD_READABLE);
						mFileOutputStream.write(vCard.toString().getBytes());
						mFileOutputStream.close();
						
						Intent emailIntent = new Intent(Intent.ACTION_SEND);
						emailIntent.setType("plain/text");
						emailIntent.putExtra(Intent.EXTRA_EMAIL  , "");
						emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
						emailIntent.putExtra(Intent.EXTRA_TEXT   , "");
						//Uri uriMail = Uri.parse(path);
						//emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
						emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
						startActivity(Intent.createChooser(emailIntent, "..."));
						
						file.deleteOnExit();
						
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally{
						finish();
					}
				}*/
				
			}else{
				Toast.makeText(this, R.string.nije_odabran_kontakt, Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
	
	/*@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		try {
			new File(Environment.getExternalStorageDirectory().toString() + File.separator + "organizator-kontakti.vcf").delete();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		getMenuInflater().inflate(R.menu.pregled, menu);
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
	

	@Override
	public void onItemClick(AdapterView<?> adap, View v, int position, long id) {
		if(cursor.moveToPosition(position) && (cursor.getInt(cursor.getColumnIndex(android.provider.ContactsContract.Groups.SUMMARY_COUNT))>0)){
			//Log.i("Kontakti", "ID: " + cursor.getString(cursor.getColumnIndex("_id")) + " Title: " + cursor.getString(cursor.getColumnIndex("title")));
			Long idGrupe = cursor.getLong(cursor.getColumnIndex("_id"));
			if(!mapa.containsKey(idGrupe)){
				Cursor cur = getContentResolver().query(Data.CONTENT_URI, null, 
						GroupMembership.GROUP_ROW_ID + "=" + cursor.getString(cursor.getColumnIndex("_id")), null, Data.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
				Set<String> imena = new LinkedHashSet<String>();
				int ime = cur.getColumnIndex("display_name");
				if(cur.moveToFirst()){
					do{
						//Log.i("Data", "Ime: " + cur.getString(cur.getColumnIndex("display_name")) + " Broj: " + cur.getInt(cur.getColumnIndex(Phone.NUMBER)));
						imena.add(cur.getString(ime));
					}while(cur.moveToNext());
				}
				mapa.put(idGrupe, imena);
				odaberiKontakte(imena);
			}else{
				odaberiKontakte(mapa.get(idGrupe));
			}
		}else{
			Toast.makeText(this, R.string.grupa_ne_sadrzi_kontakte, Toast.LENGTH_SHORT).show();
		}
		
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
		cursor = getContentResolver().query(ContactsContract.Groups.CONTENT_SUMMARY_URI, null, "ACCOUNT_NAME=? AND ACCOUNT_TYPE=?", 
				new String[] {mSelectedAccount.getName(), mSelectedAccount.getType()}, android.provider.ContactsContract.Groups.TITLE + " COLLATE LOCALIZED ASC");
		mapa.clear();
		izabraniKontakti.clear();
		ispisiKontakte();
		Log.i("Cursor", "Broj kontakta: " + cursor.getCount());
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
    
    public void odaberiKontakte(Set<String> setImena){

    	int count = setImena.size();
    	boolean[] izbor = new boolean[count];
    	final String[] svaImena =setImena.toArray(new String[0]);
    	
    	
    	for (int i = 0; i<count; i++) {
			izbor[i] = izabraniKontakti.contains(svaImena[i]);
		}
    	
    	DialogInterface.OnMultiChoiceClickListener odabraniKontakti = new DialogInterface.OnMultiChoiceClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					izabraniKontakti.add(svaImena[which]);
				}else{
					izabraniKontakti.remove(svaImena[which]);
				}
			}
		};
    	 
    	  AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	  builder.setTitle(R.string.kontakti);
    	  // Dialog builder sm pravi Multi Choice izbornikom
    	  // imeGrupa je moralo biti String[] jer ne prihvaæa ArrayList
    	  builder.setMultiChoiceItems(svaImena, izbor, odabraniKontakti);
    	 
    	  AlertDialog dialog = builder.create();
    	  dialog.show();
    }

}
