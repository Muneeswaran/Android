package hr.zbc.remindme;

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

public class REC_AlarmReceiver extends BroadcastReceiver{
	SQL_DatabaseHelper db;
	String title, message;
	Cursor cur;
	Context ctx;

	@Override
	public void onReceive(Context ctx, Intent intent) {
		this.ctx = ctx;
		prepareDbAndCursor(intent);
		
		determineWhichQuoteToUse();
		
		createNotification();
		db.close();
	}


	private void prepareDbAndCursor(Intent intent) {
		db = new SQL_DatabaseHelper(ctx);
		db.open();
		title = intent.getExtras().getString("title");
		cur = db.getAllUnusedQuotesForTitle(title);
	}


	private void determineWhichQuoteToUse() {
		int numberOfRows = cur.getCount();
		int quoteNumber;
		// Check if any unused quote left
		if (numberOfRows > 0) {
			quoteNumber = chooseAQuoteRandomly();
			cur.moveToPosition(quoteNumber);
			message = cur.getString(cur.getColumnIndex(SQL_DatabaseHelper.KEY_TEXT));
			db.changeQuoteToUsed(cur.getString(cur.getColumnIndex(SQL_DatabaseHelper.KEY_ID)));
		}else{
			// This is hard coded, has to change.
			title = "All out of Quotes!";
			message = "All quotes in this group have been used. If you want to start over select refresh from the menu inside the quotes list.";
		}
	}


	private int chooseAQuoteRandomly() {
		int numberOfRows = cur.getCount();
		
		if(numberOfRows > 1){
			Random r = new Random();
			int rand = r.nextInt(numberOfRows - 1);
			//c.moveToPosition(rand); 
			return rand;
		}else{
			return 0;
		}
	}

	private void createNotification() {
		Intent intent = new Intent(ctx, A_NotificationReceiver.class);
		// (Context, Req Code, Intent, Flags)
		PendingIntent pIntent = PendingIntent.getActivity(ctx, 0, intent, 0);

		// http://www.compiletimeerror.com/2013/10/status-bar-notification-example-in.html
		// ----- http://javapapers.com/android/android-notifications/  -----------------
		int icon = R.drawable.ic_launcher;
		
		Notification notification = new Notification (icon, title, System.currentTimeMillis());
		notification.setLatestEventInfo(ctx, title, message, pIntent);
		
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		
		// http://www.vogella.com/articles/AndroidNotifications/article.html
		// API Level 11
		
		NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(ctx.NOTIFICATION_SERVICE);
		// hide the notification after it has been selected
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		
		// 0 is the ID (Notification identifier); if neede
		notificationManager.notify((int) (System.currentTimeMillis()/1000), notification);
	}

}
