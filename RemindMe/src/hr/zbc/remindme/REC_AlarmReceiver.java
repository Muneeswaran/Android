package hr.zbc.remindme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

public class REC_AlarmReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context ctx, Intent intent) {
		Cursor c;
		SQL_DatabaseHelper db = new SQL_DatabaseHelper(ctx);
		db.open();
		String title = intent.getExtras().getString("title");
		Toast.makeText(ctx, title, Toast.LENGTH_LONG).show();
		
		db.close();
	}

}
