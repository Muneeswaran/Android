package hr.diplomski.organizator;



import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;

public class NovaBiljeska extends Activity {
	
    public static int numTitle = 1; 
    public static String curDate = "";
    public static String curText = ""; 
    private EditText mTitleText;
    private EditText mBodyText;
    private TextView mDateText;
    private Button spremi;
    private Long mRowId;

    private Cursor note;

    private BiljeskeDbAdapter mDbHelper;
    String stariTekst;
    String noviTekst;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nova_biljeska);
		// Show the Up button in the action bar.
		setupActionBar();
		
		inicijalizacija();
		
		// Pogledat što mu znaèi getSerializable
		mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(BiljeskeDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(BiljeskeDbAdapter.KEY_ROWID)
                                    : null;
        }
		
        dohvatiPodatke();
	}

	private void dohvatiPodatke() {
        if (mRowId != null) {
            note = mDbHelper.dohvatiBiljesku(mRowId);
            //startManagingCursor(note);
            mTitleText.setText(note.getString(
                 note.getColumnIndexOrThrow(BiljeskeDbAdapter.KEY_TITLE)));
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(BiljeskeDbAdapter.KEY_BODY)));
            curText = note.getString(
                    note.getColumnIndexOrThrow(BiljeskeDbAdapter.KEY_BODY));
        }
        stariTekst = mTitleText.getText().toString();
        stariTekst += mBodyText.getText().toString();
    }

	private void inicijalizacija() {
        
        mDbHelper = new BiljeskeDbAdapter(this);
        mDbHelper.open();        
        
        mTitleText = (EditText) findViewById(R.id.etNovaBiljeskaNaslov);
        mBodyText = (EditText) findViewById(R.id.etNovaBiljeskaSadrzaj);
        mDateText = (TextView) findViewById(R.id.tvNovaBiljeskaDatum);

        //long msTime = System.currentTimeMillis();  
        //Date curDateTime = new Date(System.currentTimeMillis());
        /*
        SimpleDateFormat formatter = new SimpleDateFormat("d'/'M'/'y");  
        curDate = formatter.format(curDateTime);        
        curDate = new SimpleDateFormat("dd.MM.yyyy").format(curDateTime);
        */
        mDateText.setText(""+ new SimpleDateFormat("dd.MM.yyyy HH:mm").format(System.currentTimeMillis()));
        /*
        spremi = (Button) findViewById(R.id.bNovaBiljeskaSpremi);
        spremi.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				spremiBiljesku();
			}
		});
		*/
	}
	
	// Dijalog kod zatvaranja
	@Override
	public void onBackPressed() {
		noviTekst = mTitleText.getText().toString();
		noviTekst +=  mBodyText.getText().toString();
		
	    if (noviTekst.equals(stariTekst) || noviTekst.equals("")) {
	    	finish();
		}else {
			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.pozor)
			.setMessage(R.string.spremanje_biljeske)
			.setPositiveButton(R.string.spremi,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							spremiBiljesku();
							finish();
						}

					})
			.setNegativeButton(R.string.odbaci,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							finish();
						}
					}).show();
		}
	    
	}

	protected void spremiBiljesku() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();

        if(mRowId == null){     
         long id = mDbHelper.stvoriBiljesku(title, body, System.currentTimeMillis());
         if(id > 0){ mRowId = id; }
        }else{
         mDbHelper.osvjeziBiljesku(mRowId, title, body, System.currentTimeMillis());
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
		getMenuInflater().inflate(R.menu.nova_biljeska, menu);
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
		case R.id.mBiljeskuObrisati:
			if(mRowId != null){
				mDbHelper.obrisiBiljesku(mRowId);
				finish();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}



/*
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;

public class NovaBiljeska extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nova_biljeska);
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
/*
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nova_biljeska, menu);
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
*/