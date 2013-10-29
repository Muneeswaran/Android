package hr.zbc.remainder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ClaAlarmConfigurator {
	
	Context ctx;
	
	public ClaAlarmConfigurator(Context context){
		this.ctx = context;
	}
	
	protected String scheduleAlarm(){
		
		
		//long time = new GregorianCalendar().getTimeInMillis()+10*1000;
		Calendar cal = Calendar.getInstance();
		//cal.setTime(new Date());
		// Dodaje jedan današnjem danu (postavlja sutra)
		cal.add(Calendar.DAY_OF_MONTH, 1);
		
		Random r = new Random();
		
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), r.nextInt(14)+8, r.nextInt(59));
		
		
		long time = cal.getTimeInMillis();
		
        Intent intentAlarm = new Intent(ctx, SerAlarmReceiver.class);
		//Intent intentAlarm = new Intent("hr.zbc.remainder.AlarmService");
		
        // create the object
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

        //set the alarm for particular time
        alarmManager.set(AlarmManager.RTC_WAKEUP,time, PendingIntent.getBroadcast(ctx,ActMain.REQ_CODE,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
		
		return new SimpleDateFormat("dd.MM.yyyy HH:mm").format(cal.getTime());
	}
	
	protected boolean isAlarmSet(){
		
		if(PendingIntent.getBroadcast(ctx, ActMain.REQ_CODE, 
    			new Intent(ctx, SerAlarmReceiver.class), 
    			PendingIntent.FLAG_NO_CREATE) != null){
			return true;
		}else{
			return false;
		}
	}
	
	protected boolean cancelAlarm(){

    	PendingIntent.getBroadcast(ctx, ActMain.REQ_CODE, 
    			new Intent(ctx, SerAlarmReceiver.class), 
    			PendingIntent.FLAG_NO_CREATE).cancel();
    	
    	return false;
	}

}
