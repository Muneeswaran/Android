package hr.zbc.remainder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context ctx, Intent arg1) {
		Log.i("ALARM", "U Receiveru");
		AddDataToDb db = new AddDataToDb(ctx);
		Cursor cur;
		
		// http://www.wisdomitsol.com/Blog/Android/Sms/How-to-programmatically-save-sms-to-inbox-or-sent-in-android
		try {
			// Dohvaæa quote iz baze
			db.open();
			cur = db.getUnusedBucketList();
			if (cur.getCount()>1) {
				Random r = new Random();
				int rand = r.nextInt(cur.getCount() - 1);
				cur.moveToPosition(rand);
				
				// Kreira SMS
				ContentValues values = new ContentValues();
				values.put("address", "0958386463");
				values.put("body", cur.getString(cur.getColumnIndex(MyDbHelperClass.COLUMN_QUOTE)));
				//values.put("read", obj.getReadState());
				//values.put("date", obj.getTime());
				ctx.getContentResolver().insert(
						Uri.parse("content://sms/inbox"), values);
				
				// Svira zvuk notifikacije
				Uri notification = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				Ringtone tone = RingtoneManager.getRingtone(ctx, notification);
				tone.play();
				//ret = true;
				db.setUsed(cur.getString(cur.getColumnIndex(MyDbHelperClass.COLUMN_ID)));
				db.close();
			}else{
				// Ukoliko su veæ korišteni svi quoteovi. šalje odgovarajuæu poruku
				ContentValues values = new ContentValues();
				values.put("address", "0958386463");
				values.put("body", "No available quotes");
				//values.put("read", obj.getReadState());
				//values.put("date", obj.getTime());
				ctx.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
				
				// Svira zvuk notifikacije
				Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				Ringtone tone = RingtoneManager.getRingtone(ctx, notification);
				tone.play();

			}
	    } catch (Exception ex) {
	       // ret = false;
	    }finally{
	    	// Alarm is not set
	    	// http://stackoverflow.com/questions/4556670/how-to-check-if-alarmmamager-already-has-an-alarm-set
	    	AlarmConfigurator alarmConfig = new AlarmConfigurator(ctx);
	    	if(alarmConfig.isAlarmSet()){
	    		Log.i("TRUE", "Alarm is  set");
	    		
	    		alarmConfig.cancelAlarm();
	    		String date = alarmConfig.scheduleAlarm();
		        Toast.makeText(ctx, date, Toast.LENGTH_LONG).show();
	    	}
	    }
		
		/*
		 * http://learnandroideasily.blogspot.com/2013/06/scheduling-task-using-alarm-manager.html
		 * SMS se naplati
		TelephonyManager tMgr =(TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
		 String number = "0958386463";
		String message="Hi I will be there later, See You soon";// message to send
		SmsManager sms = SmsManager.getDefault(); 
		sms.sendTextMessage(number, null, message, null, null);
		// Show the toast  like in above screen shot
		Toast.makeText(ctx, "Alarm Triggered and SMS Sent", Toast.LENGTH_LONG).show();
		*/
	}

}
