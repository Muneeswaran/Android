package hr.diplomski.organizator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BiljeskeListAdapter extends BaseAdapter {
	
	private ArrayList<BiljeskeVrijednosti> vrijednosti;
	Context c;
	
	public BiljeskeListAdapter(ArrayList<BiljeskeVrijednosti> _vrijednosti, Context _c) {
		vrijednosti = _vrijednosti;
		c = _c;
	}

	@Override
	public int getCount() {
		return vrijednosti.size();
	}

	@Override
	public Object getItem(int arg0) {
		return vrijednosti.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		View v = arg1;
		if(v==null){
			LayoutInflater li = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.kalendar_redak, null);
		}
		TextView naslov = (TextView) v.findViewById(R.id.tvEventNaslov);
		TextView vrijeme = (TextView) v.findViewById(R.id.tvEventTrajanje);
		
		BiljeskeVrijednosti bilj = vrijednosti.get(arg0);
		naslov.setText(bilj.getNaslov());
		vrijeme.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm").format(bilj.getDatum()));
		
		return v;
	}

}
