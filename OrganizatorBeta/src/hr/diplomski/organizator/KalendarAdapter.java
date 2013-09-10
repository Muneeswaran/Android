package hr.diplomski.organizator;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

// Ovdje na�ao
// http://stackoverflow.com/questions/5300787/how-do-i-create-a-custom-cursor-adapter-for-a-listview-for-use-with-images-and-t
public class KalendarAdapter extends CursorAdapter{

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    View v;
    Cursor cur;
    
	@SuppressWarnings("deprecation")
	public KalendarAdapter(Context context, Cursor c) {
		super(context, c);
		cur = c;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
	}
	
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
	    v = mLayoutInflater.inflate(R.layout.kalendar_redak, parent, false);
	    Log.e("Inflate", "opet je napravio inflate");
        return v;
	}
	
	public static class ViewHolder {
	    public TextView naslov;
	    public TextView trajanje;
	}
	
	// Bolje koristiti ViewHolder umjesto stalno pozivati findViewBy
	// Bolje spremiti indexe nego pozivati c.getColumnIndex....
	// LayoutInflater spremiti, a ne stalno pozivati
	// http://stackoverflow.com/questions/13631075/listview-with-cursoradapter
	@Override
	public void bindView(View view, Context context, Cursor cur) {
			Log.e("bindView", "if");
			String naslov = cur.getString(cur.getColumnIndex(BiljeskeDbAdapter.KEY_TITLE));
			String pocetak = cur.getString(cur.getColumnIndex(BiljeskeDbAdapter.KEY_DATE));
			
			TextView tvNaslov = (TextView) v.findViewById(R.id.tvEventNaslov);
			tvNaslov.setText(naslov);
			
			TextView tvDatum = (TextView) v.findViewById(R.id.tvEventTrajanje);
			tvDatum.setText(pocetak);
	}

}
