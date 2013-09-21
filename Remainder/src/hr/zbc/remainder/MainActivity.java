package hr.zbc.remainder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	static int REQ_CODE = 1024;
	AlarmConfigurator alarmConfig = new AlarmConfigurator(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
    	/*try {
			PendingIntent.getBroadcast(this, MainActivity.REQ_CODE,
					new Intent(this, AlarmReceiver.class),
					PendingIntent.FLAG_NO_CREATE).cancel();
		} catch (Exception e) {
			// TODO: handle exception
		}*/
		
		if (!alarmConfig.isAlarmSet()) {
			
			String date = alarmConfig.scheduleAlarm();
	        Toast.makeText(this, date, Toast.LENGTH_LONG).show();
			//scheduleAlarm();
		}
	}
	
	
/*
	private void scheduleAlarm() {
		//long time = new GregorianCalendar().getTimeInMillis()+10*1000;
		Calendar cal = Calendar.getInstance();
		//cal.setTime(new Date());
		// Dodaje jedan današnjem danu (postavlja sutra)
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Random r = new Random();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), r.nextInt(14)+8, r.nextInt(59));
		Log.i("VRIJEME", new SimpleDateFormat("dd.MM.yyyy HH:mm").format(cal.getTime()));
		long time = cal.getTimeInMillis();
		Intent intentAlarm = new Intent(this, AlarmReceiver.class);
		//Intent intentAlarm = new Intent("hr.zbc.remainder.AlarmService");
		// create the object
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		//set the alarm for particular time
		alarmManager.set(AlarmManager.RTC_WAKEUP,time, PendingIntent.getBroadcast(this,REQ_CODE,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
		Toast.makeText(this, new SimpleDateFormat("dd.MM.yyyy HH:mm").format(cal.getTime()), Toast.LENGTH_LONG).show();

	}
*/
	public void klik(View v){
		switch (v.getId()) {
		case R.id.bMainBucket:
			startActivity(new Intent(this, BucketList.class));
			break;
		case R.id.bMainSettings:
			
			break;
		case R.id.bMainAbout:
			
			break;
		}
		
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mMainCancelAlarm:
			if(alarmConfig.isAlarmSet()){
				alarmConfig.cancelAlarm();
				Toast.makeText(this, R.string.alarm_cancelled, Toast.LENGTH_SHORT).show();
			}
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	
	
}
