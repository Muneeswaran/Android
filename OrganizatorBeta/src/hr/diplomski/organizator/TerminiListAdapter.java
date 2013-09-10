package hr.diplomski.organizator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.opengl.Visibility;
import android.text.Layout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TerminiListAdapter extends BaseAdapter{
	
	private ArrayList<TerminiDetalji> det;
	Context c;
	LayoutInflater li;
	Boolean novo = true;
	String danas, jucer;
	TextView naslov, vrijeme, seperator;
	LinearLayout pozadina;
	int broj_elemenata;
	
	
	public  TerminiListAdapter(ArrayList<TerminiDetalji> detalji, Context con) {
		det = detalji;
		c = con;
		li = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		broj_elemenata = det.size();
	}

	@Override
	public int getCount() {
		return det.size();
	}

	@Override
	public Object getItem(int position) {
		return det.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		//ViewHolder holder = null;
		// Izbacio sam ovaj "if" jer je kreirao zapravo samo onoliko redaka koliko stane na zaslon
		// i onda ih samo kopirao (i pozadinu i sve), a to mi smetalo jer onda nije dodavao onaj seperator
		//if(v==null){
			v = li.inflate(R.layout.kalendar_redak, null);
			/*
			holder = new ViewHolder();
			holder.naslov = (TextView) v.findViewById(R.id.tvEventNaslov);
			holder.vrijeme = (TextView) v.findViewById(R.id.tvEventTrajanje);
			holder.pozadina = (LinearLayout) v.findViewById(R.id.llRedakEvent);
			*/
		//}
	/*else{
			holder = (ViewHolder) convertView.getTag();
		}*/
		/*
		TerminiDetalji termin = det.get(position);
		holder.naslov.setText(termin.getNaslov());
		holder.vrijeme.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm").format(termin.getPocetak()));
		holder.pozadina.setBackgroundColor(termin.getBoja());
		*/
		
		
		naslov = (TextView) v.findViewById(R.id.tvEventNaslov);
		vrijeme = (TextView) v.findViewById(R.id.tvEventTrajanje);
		seperator = (TextView) v.findViewById(R.id.tvEventSeperator);
		pozadina = (LinearLayout) v.findViewById(R.id.llRedakEvent);
		
		TerminiDetalji termin = det.get(position);
		
		if(position==0){
			naslov.setText(c.getString(R.string.ucitaj_starije) + "...");
			naslov.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
			vrijeme.setVisibility(View.GONE);
		}else if (position==(broj_elemenata-1)) {
			naslov.setText(c.getString(R.string.ucitaj_novije) + "...");
			naslov.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
			vrijeme.setVisibility(View.GONE);
		}else {
			if (position == 1){
				seperator.setVisibility(View.VISIBLE);
				seperator.setText(new SimpleDateFormat("E, dd.MM.yyyy").format(termin.getPocetak()));
				//seperator.setText("Pozicija: " + det.size());
			}else {
				danas = new SimpleDateFormat("E, dd.MM.yyyy").format(det.get(position).getPocetak());
				jucer = new SimpleDateFormat("E, dd.MM.yyyy").format(det.get(position-1).getPocetak());
				if(!danas.equals(jucer)){
					seperator.setVisibility(View.VISIBLE);
					seperator.setText(danas);
				}
			}
			
			/*
			if(novo || (!noviDatum.equals(prethodniDatum))){
				novo = false;
				seperator.setText(noviDatum);
				prethodniDatum = noviDatum;
			}else {
				seperator.setVisibility(View.GONE);
			}
			*/
			pozadina.setBackgroundColor(termin.getBoja());
			
			try{
				LinearLayout alarmi = (LinearLayout) v.findViewById(R.id.llRedakEventAlarm);
				String alarm = termin.getNaslov();
				char ch = alarm.charAt(0);
				//alarm = Character.toString(ch);
				if(alarm.startsWith("!")){
					alarmi.setBackgroundColor(Color.RED);
				}//else{
					//alarmi.setBackgroundColor(Color.WHITE);
				//}
			}catch(Exception e){
				
			}
			
			naslov.setText(termin.getNaslov());
			if(termin.getCijeli_dan().equals("1")){
				vrijeme.setText(R.string.cijeli_dan);
			}else {
				if((termin.getKraj()-termin.getPocetak())>= 86400000){
					vrijeme.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm").format(termin.getPocetak()) + 
							" - " + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(termin.getKraj()));
				}else{
					vrijeme.setText(new SimpleDateFormat("HH:mm").format(termin.getPocetak()) + " - " + new SimpleDateFormat("HH:mm").format(termin.getKraj()));
				}
			}
		}
		return v;
	}
	
	public static class ViewHolder{
		public TextView naslov;
		public TextView vrijeme;
		public LinearLayout pozadina;
	}
	
	
	
}
