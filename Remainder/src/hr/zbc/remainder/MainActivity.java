package hr.zbc.remainder;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
