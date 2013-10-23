package hr.zbc.remindme;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.MediaStore;

public class ActPickFiles extends Activity implements OnItemClickListener{
	
	//CursorLoader cursor;
	//List<File> fileList;
	ArrayAdapter<String> mAdapter;
	Map fileMap;
	List<String> fileName, filePath;
	ListView fileListView;
	int reqCode;
	ClaFindFiles findClass = new ClaFindFiles(this);
	boolean firstLoad = true, listAdded = false, fileSaved = false;
	AsyncTaskRunner runner;
	SqlDatabaseHelper  db;
	String title = "";
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_files_layout);
		// Show the Up button in the action bar.
		setupActionBar();
		
		
		fileListView = (ListView) findViewById(R.id.lvFindFilesList);
		fileListView.setOnItemClickListener(this);
		
		//int reqCode = savedInstanceState.getInt("request_code");
		reqCode = getIntent().getExtras().getInt("request_code");
		
		if (reqCode == ActMainActivity.CODE_PICK) {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				AsyncTaskRunner runner = new AsyncTaskRunner();
				runner.execute(Environment.getExternalStorageDirectory());
			}
		} else if (reqCode == ActMainActivity.CODE_FIND) {
			listDirectories(Environment.getExternalStorageDirectory()
					.getPath());
		}
		
		
	}
	
	@SuppressWarnings("unchecked")
	private void sortForAdapter(Map<String, String> myMap) {
		fileMap = new LinkedHashMap();
		
		List temp = new ArrayList(myMap.entrySet());
		Collections.sort(temp, new Comparator() {
			
            public int compare(Object o1, Object o2) {
            	return ((Map.Entry) o1).getValue().toString().compareToIgnoreCase(((Map.Entry) o2).getValue().toString());
                //return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
            }
        });
		for (Iterator it = temp.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			fileMap.put(entry.getKey(), entry.getValue());
		}
		filePath = new ArrayList<String>(fileMap.keySet());
		fileName = new ArrayList<String>(fileMap.values());
		
		loadAdapter(fileName);
	}
	
	private void listDirectories(String dirPath){
		Map<String, String> tempMap = new LinkedHashMap<String, String>();
		tempMap.putAll(findClass.getDirectories(dirPath));
		tempMap.putAll(findClass.getFiles(dirPath));
		
		filePath = new ArrayList<String>(tempMap.keySet());
		fileName = new ArrayList<String>(tempMap.values());
		
		loadAdapter(fileName);
	}
	
	private void loadAdapter(List<String> names){
		
		// I'll need an custom adapter so I can have one row for the path and one for the file name
		mAdapter = new ArrayAdapter<String>(this, R.layout.row_find_files, R.id.tvRowFindFilesNames, names);
		fileListView.setAdapter(mAdapter);
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> av, View v, int position, long id) {

		final File file = new File(filePath.get(position));

		if(file.isDirectory()){
			listDirectories(filePath.get(position));
		}else{
			// Dialog to input list title
			final EditText input = new EditText(this);
			input.setHint(R.string.set_title);
			
			new AlertDialog.Builder(this)
			.setTitle(file.getName())
			.setView(input)
			.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(
						DialogInterface dialog,
						int which) {
					//setResult(Activity.RESULT_OK, new Intent().putExtra("file", file.getPath()));
					//finish();
					title = input.getText().toString();
					if(title.equals("")){
						Toast.makeText(ActPickFiles.this, R.string.error_no_title_set, Toast.LENGTH_LONG).show();
					}else {
						saveQuotes(title, file);
					}
				}
			}).show();

		}

	}
	
	private void saveQuotes(String str, File f){
		ArrayList<String> titles = new ArrayList<String>();
		//---------------------------
		db = new SqlDatabaseHelper(this);
		db.open();
		titles = db.getAllTitles();
		//-------------------------------
		if(titles.size()>0){
			if (titles.contains(str)) {
				Toast.makeText(ActPickFiles.this, R.string.title_already_exists,
						Toast.LENGTH_LONG).show();
			}else{
				Log.i("PICK FILES", "Inside else");
				if(f.exists() && f.canRead()){
					new FileToString().execute(f);
				}else{
					Toast.makeText(ActPickFiles.this, R.string.problem_with_file, Toast.LENGTH_LONG).show();
				}
			}
		}else{
			if(f.exists() && f.canRead()){
				new FileToString().execute(f);
			}else{
				Toast.makeText(ActPickFiles.this, R.string.problem_with_file, Toast.LENGTH_LONG).show();
			}
		}
	}
	
	private void saveTitle(){
			fileSaved = false;
			db.addTitle(title);
			listAdded = true;
			Toast.makeText(ActPickFiles.this, getResources().getString(R.string.list) + ": " + title + " "+ getResources().getString(R.string.saved), Toast.LENGTH_LONG).show();
			title = "";
			db.close();
	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent();
		// boolean 'listAdded' - confirm that list has been added
		// so the previous activity reQuerys the Database
		i.putExtra("list_added", listAdded);
		setResult(RESULT_OK, i);
		super.onBackPressed();
	}
/*	
	@Override
	protected void onPause() {
		super.onPause();
		firstLoad = false;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadAdapter(fileName);
	}
	*/
//-------------------------------------------------------------------------------------------------------------------------------------------------
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
		getMenuInflater().inflate(R.menu.find_files, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
//---------------------------------------------------------------------------------------------------------------------------------------------------------
	
	private class AsyncTaskRunner extends AsyncTask<File, String, Map<String, String>>{
		
		// Should change it to Progress bar
		ProgressDialog pDialog;
		private boolean isTaskCancelled = false;
		
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//pDialog = ProgressDialog.show(PickFiles.this, getResources().getString(R.string.searching_files), "");
			pDialog = new ProgressDialog(ActPickFiles.this);
			pDialog.setTitle(getResources().getString(R.string.searching_files));
			pDialog.setMessage("");
			//pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			/*
			pDialog.setButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					onCancelled();
				}
			});
			*/
			pDialog.show();
			//pDialog.setOnCancelListener(new ProgressDialog(context))
			//getResources().getString(R.string.cancel)
		}
		/*
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			isTaskCancelled = true;
		}
		*/
		@Override
		protected Map<String, String> doInBackground(File... dir) {
			return getListFiles(dir[0]);
		}
		
		@Override
		protected void onPostExecute(Map<String, String> result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			
			if (!result.isEmpty()) {
				sortForAdapter(result);
			}
		}
		
		protected void onProgressUpdate(final String... values) {
			super.onProgressUpdate(values);
			//bar.setMessage(values[0]);
			Runnable changeMessage = new Runnable() {
				public void run() {
					pDialog.setMessage(values[0]);
				}
			};
			runOnUiThread(changeMessage);
		}
		
		// http://stackoverflow.com/questions/9530921/list-all-the-files-from-all-the-folder-in-a-single-list
		private Map<String, String> getListFiles(File parentDir) {
			HashMap<String, String> inFiles = new HashMap<String, String>();
			File[] files = parentDir.listFiles();
			
			if(isTaskCancelled){
				return null;
			}
			
			// Publish progress to dialog
			publishProgress(parentDir.getName()+"/");
			
			for (File file:files){
				// Only not hidden and readable files will be checked
				if (!file.isHidden() && file.canRead()) {
					// Checks if file is directory
					if (file.isDirectory()) {
						// Checks if file has children
						if (file.list() != null) {
							inFiles.putAll(getListFiles(file));
						}
						
					} else {
						if(file.getName().endsWith(".txt")){
						inFiles.put(file.getPath(), file.getName());
						}
					}
				}

			}
			return inFiles;
		}
		
	}
	
	private class FileToString extends AsyncTask<File, Void, Boolean>{

		@Override
		protected Boolean doInBackground(File... f) {
			String readFile = "";
			boolean response = false;
			try {

				Log.i("FileToString", "Inside TRY");
				BufferedReader inputReader = new BufferedReader(
						new InputStreamReader(new FileInputStream(f[0])));
				String inputString2;
				StringBuffer stringBuffer2 = new StringBuffer();
				while ((inputString2 = inputReader.readLine()) != null) {
					stringBuffer2.append(inputString2 + "\n");
				}
				inputReader.close();
				readFile = stringBuffer2.toString();
				if(readFile.contains("\n")){
					Log.i("FileToString", "db.addQuotes");
					fileSaved = db.addQuotes(readFile.split("\n"), title);
				}
				return response;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return response;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.i("FileToString", "postExecute");
			if(fileSaved){
				fileSaved = false;
				saveTitle();
			}else{
				Toast.makeText(ActPickFiles.this, R.string.file_not_saved, Toast.LENGTH_LONG).show();
			}
		}
	}
}
