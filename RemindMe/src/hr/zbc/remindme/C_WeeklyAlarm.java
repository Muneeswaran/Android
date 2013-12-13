package hr.zbc.remindme;

import android.content.Context;
import android.content.SharedPreferences;

public class C_WeeklyAlarm implements I_AlarmSchedule {
	
	private final String PREFERENCES = "hr.zbc.remindme.SHARED_PREFS";
	
	SharedPreferences sharedPrefs;
	SharedPreferences.Editor editor;
	
	Context ctx;
	DAO_AlarmDetails alarmDetails;
	
	public C_WeeklyAlarm(Context context, DAO_AlarmDetails alarmDetails){
		this.alarmDetails = alarmDetails;
		this.ctx = context;
	}

	@Override
	public void startAlarm() {
		if(checkIfAlarmIsStarted()){
			
			long timeOfNextAlarm = getTimeOfNextAlarm();
			setTheAlarm(timeOfNextAlarm);
			addOneToCounter();
			
		}else{
			
		}
	}

	private void addOneToCounter() {
		// TODO Auto-generated method stub
		
	}

	private long getTimeOfNextAlarm() {
		// TODO Auto-generated method stub
		return 0;
	}

	private void setTheAlarm(long timeOfNextAlarm) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancelAlarm() {
		// TODO Auto-generated method stub

	}

	private boolean checkIfAlarmIsStarted() {
		// TODO Auto-generated method stub
		return false;
	}

}
