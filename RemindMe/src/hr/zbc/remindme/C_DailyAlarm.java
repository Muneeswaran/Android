package hr.zbc.remindme;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;


public class C_DailyAlarm implements I_AlarmSchedule{
	
	private final String PREFERENCES = "hr.zbc.remindme.SHARED_PREFS";
	
	SharedPreferences sharedPrefs;
	SharedPreferences.Editor editor;
	
	Context ctx;
	DAO_AlarmDetails alarmDetails;
	
	public C_DailyAlarm(Context context, DAO_AlarmDetails alarmDetails){
		this.ctx = context;
		this.alarmDetails = alarmDetails;
	}

	public void startAlarm() {
		if(!checkIfAlarmIsStarted()){
			
			long timeOfNextAlarm = getTimeOfNextAlarm();
			setTheAlarm(timeOfNextAlarm);
			//addOneToCounter();
			
			//Toast.makeText(ctx, new SimpleDateFormat("dd.MM.yyyy HH:mm").format(timeOfNextAlarm),Toast.LENGTH_SHORT).show();
			//return new SimpleDateFormat("dd.MM.yyyy HH:mm").format(cal.getTime());
		}else{
			
			// return "Alarm already set!";
		}
	}

	private void setTheAlarm(long timeOfNextAlarm) {
		Intent intentAlarm = new Intent(ctx, REC_AlarmReceiver.class);
		intentAlarm.putExtra("title", alarmDetails.getTitle());
		
		AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		//alarmManager.set(AlarmManager.RTC_WAKEUP,timeOfNextAlarm, PendingIntent.getBroadcast(ctx,(int) alarmDetails.getId(), 
			//	intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
		alarmManager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()+10000, PendingIntent.getBroadcast(ctx,(int) alarmDetails.getId(), 
				intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
		
	}
	
	private void addOneToCounter() {
		editor = sharedPrefs.edit();
		// maxRepetitions => how many alarms per day are supposed to happen. 
		// If the max amount is reached then the counter will be set to 0
		// and the next alarm will be set for tomorrow.s
		int maxRepetitions = alarmDetails.getNumberOfPlannedRepetitions();
		int currentRepetition = sharedPrefs.getInt(alarmDetails.getTitle() + "_OCCURED_REPETITIONS", 0);
		
		if((maxRepetitions-1) > currentRepetition){
			// Counter is reset and the next alarm will be tomorrow
			editor.putInt(alarmDetails.getTitle() + "_OCCURED_REPETITIONS", 0);
			editor.commit();
		}else{
			editor.putInt(alarmDetails.getTitle() + "_OCCURED_REPETITIONS", currentRepetition+1);
			editor.commit();
		}
		
	}

	private long getTimeOfNextAlarm() {
		// How many messages have already been shown during this cycle
		int numberOfOccuredRepetition = getNumberOfOccuredRepetitions();
		
		// Defined by user, how many messages per day does he want
		int numberOfPlannedRepetitions = alarmDetails.getNumberOfPlannedRepetitions();
		
		// Interval chosen by user, defines when an alarm can occur
		int startTime = alarmDetails.getStartTime();
		int endTime = alarmDetails.getEndTime();
		
		int periodBetweenStartAndEnd = endTime - startTime;
		int minutesBetweenEachAlarm = (periodBetweenStartAndEnd * 60)/numberOfPlannedRepetitions;
		
		// If the number of occured repetitions is set to 0, then the next alarm will tomorrow. Otherwise this function will return today's date
		Calendar calendar = getCorrectDateForAlarm(numberOfOccuredRepetition);
		int randomMinutesToAddToStartTime = getRandomMinutesToAddToStartTime(startTime, numberOfPlannedRepetitions, numberOfOccuredRepetition, minutesBetweenEachAlarm);
		int hoursToAddToStart = randomMinutesToAddToStartTime/60;
		int minutesToAddToStart = randomMinutesToAddToStartTime%60;
		
		// Set the date and time
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), startTime+hoursToAddToStart, minutesToAddToStart);

		return calendar.getTimeInMillis();
	}

	private int getRandomMinutesToAddToStartTime(int startTime,
			int numberOfPlannedRepetitions, int numberOfOccuredRepetition, int minutesBetweenEachAlarm) {
		
		Random r = new Random();
		
		// RANDOM => (Max - Min) + (Min)
		return r.nextInt((minutesBetweenEachAlarm*(numberOfOccuredRepetition+1)) - (minutesBetweenEachAlarm*numberOfOccuredRepetition)) 
				+ (minutesBetweenEachAlarm*numberOfOccuredRepetition);
	}

	private Calendar getCorrectDateForAlarm(int numberOfOccuredRepetition) {
		if(numberOfOccuredRepetition > 0){
			return Calendar.getInstance();
		}else{
			Calendar calendar = Calendar.getInstance();
			// Set date of tomorrow
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			return calendar;
		}
	}

	private int getNumberOfOccuredRepetitions() {
		// Get how many messages, in this cycle, have already been sent for this group of quotes
		sharedPrefs = ctx.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		return sharedPrefs.getInt(alarmDetails.getTitle() + "_OCCURED_REPETITIONS", 0);
	}

	public void cancelAlarm(){
		
	}

	private boolean checkIfAlarmIsStarted() {
		
		return false;
	}
	
	
}
