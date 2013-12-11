package hr.zbc.remindme;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class A_SettingsDailyAlarm extends Activity {
	
	TextView numberOfReminders, intervalBegin, intervalEnd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_settings_daily_alarm);
		
		initializeTextViews();
	}
	
	
	private void initializeTextViews() {
		numberOfReminders = (TextView) findViewById(R.id.tvSettingsNumberOfReminders);
		intervalBegin = (TextView) findViewById(R.id.tvSettingsIntervalBegin);
		intervalEnd = (TextView) findViewById(R.id.tvSettingsIntervalEnd);
	}


	public void settingsClick(View v){
		// check if Button or CheckBox clicked
		if("button".equals(v.getContentDescription())){
			buttonClicked(v);
		}else{
			checkBoxClicked(v);
		}
	}

	// Set number of reminders, the beginning and end of the interval
	private void buttonClicked(View v) {
		int begin = Integer.parseInt(intervalBegin.getText().toString());
		int end = Integer.parseInt(intervalEnd.getText().toString());
		int reminders = Integer.parseInt(numberOfReminders.getText().toString());
		
		switch (v.getId()) {
		case R.id.bSettingsNumberOfRemindersMinus:
			if(reminders > 1){
				--reminders;
				numberOfReminders.setText("" + reminders);
			}
			break;
		case R.id.bSettingsNumberOfRemindersPlus:
			if(reminders < 6){
				++reminders;
				numberOfReminders.setText("" + reminders);
			}
			break;
		case R.id.bSettingsIntervalBeginMinus:
			if(begin > 0){
				--begin;
				intervalBegin.setText("" + begin);
			}
			break;
		case R.id.bSettingsIntervalBeginPlus:
			if((begin+1)<end){
				++begin;
				intervalBegin.setText("" + begin);
			}
			break;
		case R.id.bSettingsIntervalEndMinus:
			if((end-1) > begin){
				--end;
				intervalEnd.setText("" + end);
			}
			break;
		case R.id.bSettingsIntervalEndPlus:
			if(end <= 23){
				++end;
				intervalEnd.setText("" + end);
			}
			break;
		case R.id.bSettingsStartThisList:
			Intent i = new Intent();
			i.putExtra("number_of_reminders", reminders);
			i.putExtra("begin", begin);
			i.putExtra("end", end);
			setResult(RESULT_OK, i);
			finish();
			break;
		}
		
	}
	
	private void checkBoxClicked(View v) {
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.a__settings_daily_alarm, menu);
		return true;
	}

}
