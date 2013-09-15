package hr.zbc.remainder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;

public class BucketList extends Activity{

	private static int FILE_PICKING_CODE = 959000;
	TextView tekst;
	ListView list;
	AddDataToDb db;
	Cursor cursor;
	String retrievedText = "";

	public String getRetrievedText() {
		return retrievedText;
	}


	public void setRetrievedText(String retrievedText) {
		this.retrievedText = retrievedText;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bucket_list);
		// Show the Up button in the action bar.
		setupActionBar();

		//tekst = (TextView)findViewById(R.id.tvBucketText);
		list = (ListView)findViewById(R.id.lvBucketList);

		initArray();

		/*
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	    intent.setType("file/*");
	    startActivityForResult(intent, 0);
		 */
	}


	private void initArray() {
		db = new AddDataToDb(this);
		db.open();
		fillArray();
	}


	private void fillArray() {
		cursor = db.getBucketList();
		ArrayList<String> buckets = new ArrayList<String>();
		if(cursor.moveToFirst()){
			int index = cursor.getColumnIndex(MyDbHelperClass.COLUMN_QUOTE);
			do{
				buckets.add(cursor.getString(index));

			}while(cursor.moveToNext());
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.file_picking_row, buckets);
		list.setAdapter(adapter);

	}


	public void klik(View v) {
		switch (v.getId()) {
		case R.id.bBucketAdd:

			break;
		case R.id.bBucketImport:
			startActivityForResult(new Intent(this, FilePicking.class), FILE_PICKING_CODE);
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == FILE_PICKING_CODE && resultCode == Activity.RESULT_OK){
			//tekst.setText(data.getExtras().getString("file"));
			//fileToString(data.getExtras().getString("file"));
			//tekst.setText(fileToString(data.getExtras().getString("file")));
			//String retrievedText = fileToString(data.getExtras().getString("file"));
			new FileToString(this).execute(new String[]{data.getExtras().getString("file")});
			Log.i("OVDJE", "tu je");
		}
	}

	public void saveDataToSQL(String result){

		if(result.contains("\n")){
			db.open();
			db.addBucketList(result);
			fillArray();
			retrievedText = "";
		}
	}
	private String fileToString(String path) {
		String storageState = Environment.getExternalStorageState();
		try {
			if (storageState.equals(Environment.MEDIA_MOUNTED)) {
				File file = new File(getExternalFilesDir(null),
						path);


				BufferedReader inputReader = new BufferedReader(
						new InputStreamReader(new FileInputStream(file)));
				String inputString2;
				StringBuffer stringBuffer2 = new StringBuffer();
				while ((inputString2 = inputReader.readLine()) != null) {
					stringBuffer2.append(inputString2 + "\n");
				}
				inputReader.close();
				return stringBuffer2.toString();
				//lblTextViewOne.setText(stringBuffer2.toString());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		db.close();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		db.open();
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
		getMenuInflater().inflate(R.menu.buket_list, menu);
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

	public class FileToString extends AsyncTask<String, Void, String>{

		private ProgressDialog dialog;
		private Context ctx;
		
		public FileToString(Context context){
			ctx = context;
			dialog = new ProgressDialog(ctx);
		}

		protected void onPreExecute() {
			dialog.setMessage("Progress start");
			dialog.show();
		}


		@Override
		protected String doInBackground(String... params) {
			String storageState = Environment.getExternalStorageState();
			String response = "";
			try {
				if (storageState.equals(Environment.MEDIA_MOUNTED)) {
					File file = new File(getExternalFilesDir(null),
							params[0]);

					BufferedReader inputReader = new BufferedReader(
							new InputStreamReader(new FileInputStream(file)));
					String inputString2;
					StringBuffer stringBuffer2 = new StringBuffer();
					while ((inputString2 = inputReader.readLine()) != null) {
						stringBuffer2.append(inputString2 + "\n");
					}
					inputReader.close();
					//Log.i("aaaa", stringBuffer2.toString());
					response = stringBuffer2.toString();
					
					
					if(response.contains("\n")){
						db.open();
						db.addBucketList(response);
						retrievedText = "";
					}
					
					
					return response;
					//lblTextViewOne.setText(stringBuffer2.toString());
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			//super.onPostExecute(result);
			//Log.i("POST", result);
			if (dialog.isShowing()) {
				fillArray();
				dialog.dismiss();
			}
		}

	}

}
