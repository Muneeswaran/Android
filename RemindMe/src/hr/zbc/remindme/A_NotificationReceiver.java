package hr.zbc.remindme;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class A_NotificationReceiver extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a__notification_receiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.a__notification_receiver, menu);
		return true;
	}

}
