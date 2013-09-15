package hr.zbc.remainder;

import java.util.GregorianCalendar;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		scheduleAlarm();
	}
	
	private void scheduleAlarm() {
		long time = new GregorianCalendar().getTimeInMillis()+10*1000;
        Intent intentAlarm = new Intent(this, AlarmReceiver.class);
        
        // create the object
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //set the alarm for particular time
        alarmManager.set(AlarmManager.RTC_WAKEUP,time, PendingIntent.getBroadcast(this,1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
        Toast.makeText(this, "Alarm Scheduled for Tommrrow", Toast.LENGTH_LONG).show();
		
	}

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

	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	*/
}
