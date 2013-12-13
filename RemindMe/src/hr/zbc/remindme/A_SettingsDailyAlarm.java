package hr.zbc.remindme;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class A_SettingsDailyAlarm extends Activity {
	
	TextView numberOfReminders, intervalBegin, intervalEnd;
	int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_settings_daily_alarm);
		this.position = getIntent().getExtras().getInt("position");
		
		initializeTextViews();
	}
	
	
	private void initializeTextViews() {
		numberOfReminders = (TextView) findViewById(R.id.tvSettingsNumberOfReminders);
		intervalBegin = (TextView) findViewById(R.id.tvSettingsIntervalBegin);
		intervalEnd = (TextView) findViewById(R.id.tvSettingsIntervalEnd);
	}


	public void settingsClick(View v){
		// check if Button or CheckBox is clicked
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
			numberOfRemindersDecrease(reminders);
			break;
		case R.id.bSettingsNumberOfRemindersPlus:
			numberOfRemindersIncrease(reminders);
			break;
		case R.id.bSettingsIntervalBeginMinus:
			intervalBeginDecrease(begin);
			break;
		case R.id.bSettingsIntervalBeginPlus:
			intervalBeginIncrease(begin, end);
			break;
		case R.id.bSettingsIntervalEndMinus:
			intervalEndDecrease(begin, end);
			break;
		case R.id.bSettingsIntervalEndPlus:
			intervalEndIncrease(end);
			break;
		case R.id.bSettingsStartThisList:
			sendDataToMainActivity(begin, end, reminders);
			break;
		}
		
	}


	private void numberOfRemindersDecrease(int reminders) {
		if(reminders > 1){
			--reminders;
			numberOfReminders.setText("" + reminders);
		}
	}


	private void numberOfRemindersIncrease(int reminders) {
		if(reminders < 6){
			++reminders;
			numberOfReminders.setText("" + reminders);
		}
	}


	private void intervalBeginDecrease(int begin) {
		if(begin > 0){
			--begin;
			intervalBegin.setText("" + begin);
		}
	}


	private void intervalBeginIncrease(int begin, int end) {
		if((begin+1)<end){
			++begin;
			intervalBegin.setText("" + begin);
		}
	}


	private void intervalEndDecrease(int begin, int end) {
		if((end-1) > begin){
			--end;
			intervalEnd.setText("" + end);
		}
	}


	private void intervalEndIncrease(int end) {
		if(end <= 23){
			++end;
			intervalEnd.setText("" + end);
		}
	}


	private void sendDataToMainActivity(int begin, int end, int reminders) {
		Intent i = new Intent();
		i.putExtra("position", position);
		i.putExtra("number_of_reminders", reminders);
		i.putExtra("begin", begin);
		i.putExtra("end", end);
		setResult(RESULT_OK, i);
		finish();
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
