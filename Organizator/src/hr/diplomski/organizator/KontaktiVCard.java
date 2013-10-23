package hr.diplomski.organizator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

public class KontaktiVCard {

	Context context;

	public KontaktiVCard(Context c){
		this.context = c;
	}

	public void provjeraStarostiDatoteka(){
		String path = Environment.getExternalStorageDirectory().toString() + File.separator + "organizator-kontakti.vcf";
		File file = new File(path);
		int brojFile = 1;
		long mod;
		long dat = new Date().getTime();
		for(int i = 1; i<=5; i++){
			file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "organizator-kontakti-"+i+".vcf");
			brojFile = i;
			// Briše datoteke starije od 24h
			if(file.exists()){
				mod = file.lastModified();
				if((mod - dat) >= 86400000 ){
					file.delete();
				}
			}
		}

	}

	private int provjeraBrojaSljedeæeDatoteke(){
		int vrijednost = 1;
		String path = Environment.getExternalStorageDirectory().toString() + File.separator + "organizator-kontakti.vcf";
		File file = new File(path);
		Map<Long, Integer> brojDatoteke = new HashMap<Long, Integer>();
		try {
			for(int i = 1; i<=5; i++){
				file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "organizator-kontakti-"+i+".vcf");
				if(file.exists()){
					//Log.i("VRIJEME", "Vrijeme: " + file.lastModified() + "Integer: " + i);
					brojDatoteke.put(file.lastModified(), i);
				}else{
					return i;
				}
			}
			if(!brojDatoteke.isEmpty()){
				List<Long> lista = new ArrayList<Long>(brojDatoteke.keySet());
				Collections.sort(lista);
				vrijednost = brojDatoteke.get(lista.get(0));
				//Log.i("Int", "Vrijednost: " + vrijednost);
			}
		} catch (Exception e) {

		}
		return vrijednost;

	}

	public void posaljiVCard(Set<String> izabraniKontakti){
		//final String vFileAdresa = "organizator-kontakti.vcf";

		String[] str = izabraniKontakti.toArray(new String[0]);
		int count = str.length;
		String where = "";
		for(int i = 0; i<count-1; i++){
			where += Data.DISPLAY_NAME + "=? OR ";
		}
		where += Data.DISPLAY_NAME + "=?";
		Cursor c = context.getContentResolver().query(Contacts.CONTENT_URI, null, where, str, Data.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
		if(c.moveToFirst()){
			String path = Environment.getExternalStorageDirectory().toString() + File.separator + "organizator-kontakti-" + provjeraBrojaSljedeæeDatoteke() + ".vcf";
			File file = new File(path);
			provjeraBrojaSljedeæeDatoteke();
			String vCard = "";
			FileInputStream fis = null;
			do{
				String lookupKey = c.getString(c.getColumnIndex(Contacts.LOOKUP_KEY));
				Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
				AssetFileDescriptor fd;
				try {
					fd = context.getContentResolver()
							.openAssetFileDescriptor(uri, "r");
					fis = fd.createInputStream();
					byte[] b = new byte[(int) fd.getDeclaredLength()];
					fis.read(b);
					vCard += new String(b);
					//Log.i("---Test---", vCard);

				} catch (Exception e) {

				}
			}while(c.moveToNext());
			try {
				fis.close();
				FileOutputStream mFileOutputStream = new FileOutputStream(path, true);
				mFileOutputStream.write(vCard.toString().getBytes());
				mFileOutputStream.close();

				Intent emailIntent = new Intent(Intent.ACTION_SEND);
				emailIntent.setType("plain/text");
				emailIntent.putExtra(Intent.EXTRA_EMAIL  , "");
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
				emailIntent.putExtra(Intent.EXTRA_TEXT   , "");
				emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
				context.startActivity(Intent.createChooser(emailIntent, "..."));

				//Log.i("Intent", "Path: " + path + "Uri: " + Uri.fromFile(file));

				file.deleteOnExit();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void posaljiString(Set<String> izabraniKontakti){
		String[] str = izabraniKontakti.toArray(new String[0]);
		int count = str.length;
		String where = "";
		for(int i = 0; i<count-1; i++){
			where += Data.DISPLAY_NAME + "=? OR ";
		}
		where += Data.DISPLAY_NAME + "=?";
		Cursor c = context.getContentResolver().query(Phone.CONTENT_URI, null, where, str, Data.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
		Set<String> popis = new LinkedHashSet<String>();
		Map<String, Set<String>> konacnaMapa = new LinkedHashMap<String, Set<String>>();
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
						popis = new LinkedHashSet<String>();
						//Log.d("Mapa", "Spremio mapu: " + prethodni + " Velièina: " + popis.size());
					}
					//Log.i("Prethodni", "Prethodni: " + prethodni);
					prethodni = c.getString(ime);
					if(c.getString(broj) != null){
						unos = Phone.getTypeLabel(context.getResources(), c.getInt(brojVrsta), c.getString(label)) + ": " + c.getString(broj);
						popis.add(unos);
						//Log.d("Kontakt1", "Spremio kontakt: " + Phone.getTypeLabel(context.getResources(), c.getInt(brojVrsta), c.getString(label)) + ": " + c.getString(broj) );
					}
				}else{
					if(c.getString(broj) != null){
						unos = Phone.getTypeLabel(context.getResources(), c.getInt(brojVrsta), c.getString(label)) + ": " + c.getString(broj);
						popis.add(unos);
						//Log.d("Kontakt2", "Spremio kontakt: " + Phone.getTypeLabel(context.getResources(), c.getInt(brojVrsta), c.getString(label)) + ": " + c.getString(broj) );
					}
				}
			}while(c.moveToNext());
			konacnaMapa.put(prethodni, popis);
			//Log.d("Mapa3", "Spremio mapu: " + prethodni + " Velièina: " + popis.size());
		}

		c = context.getContentResolver().query(Email.CONTENT_URI, null, where, str, Data.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
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
						unos = Email.getTypeLabel(context.getResources(), c.getInt(mailVrsta), c.getString(labelm)) + ": " + c.getString(mail);
						popis.add(unos);
					}
				}else {
					if(c.getString(mail) != null){
						unos = Email.getTypeLabel(context.getResources(), c.getInt(mailVrsta), c.getString(labelm)) + ": " + c.getString(mail);
						popis.add(unos);
					}
				}
			}while(c.moveToNext());
			konacnaMapa.put(prethodni, popis);
		}
		c = context.getContentResolver().query(Data.CONTENT_URI, null, where , str, Data.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
		//Log.i("Pregled", "Query je prošao");
		if(c.moveToFirst()){
			int ime = c.getColumnIndex("display_name");
			int instant = c.getColumnIndex(Im.DATA);
			int imVrsta = c.getColumnIndex(Im.PROTOCOL);
			int labelm = c.getColumnIndex(Im.CUSTOM_PROTOCOL);
			String unos;
			String prethodni = "";
			int i = 0;
			do{
				//Log.i("Pregled", "IF: " + i++);
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
					if((c.getString(instant) != null) && (c.getInt(imVrsta) != 0)){
						unos = Im.getProtocolLabel(context.getResources(), c.getInt(imVrsta), c.getString(labelm)) + ": " + c.getString(instant);
						popis.add(unos);
					}
				}else {
					if((c.getString(instant) != null) && (c.getInt(imVrsta) != 0)){
						unos = Im.getProtocolLabel(context.getResources(), c.getInt(imVrsta), c.getString(labelm)) + ": " + c.getString(instant);
						popis.add(unos);
					}
				}
			}while(c.moveToNext());
			konacnaMapa.put(prethodni, popis);
		}

		String textMail = "";
		//String kljuc;

		List<String> imena = new ArrayList<String>(konacnaMapa.keySet());
		java.util.Collections.sort(imena);

		for(String temp : imena){
			textMail += temp + "\n";
			String[] konacniKontakti = konacnaMapa.get(temp).toArray(new String[0]);
			for(int i = 0; i<konacniKontakti.length; i++){
				textMail += konacniKontakti[i] + "\n";
			}
			textMail += "\n";

		}

		Intent in = new Intent(Intent.ACTION_SEND);
		in.setType("plain/text");
		in.putExtra(Intent.EXTRA_EMAIL  , new String[]{""});
		in.putExtra(Intent.EXTRA_SUBJECT, "Moji kontakt podatci");
		in.putExtra(Intent.EXTRA_TEXT   , textMail);
		context.startActivity(in);
		//Log.d("Odabrano", "Ime: " + c.getString(c.getColumnIndex("display_name")) + " Broj: " + c.getString(c.getColumnIndex(Phone.NUMBER)));
	}

}
