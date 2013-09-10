package hr.diplomski.organizator;

public class TerminiDetalji {
	int pozicija;
	String id;
	String eventId;
	String access;
	String kalendar;
	String naslov;
	String sadrzaj;
	String mjesto;
	long pocetak;
	long kraj;
	int boja;
	String cijeli_dan = "0";
	

	public String getCijeli_dan() {
		return cijeli_dan;
	}
	public void setCijeli_dan(String cijeli_dan) {
		this.cijeli_dan = cijeli_dan;
	}
	public String getAccess() {
		return access;
	}
	public void setAccess(String access) {
		this.access = access;
	}
	public String getMjesto() {
		return mjesto;
	}
	public void setMjesto(String mjesto) {
		this.mjesto = mjesto;
	}
	public String getKalendar() {
		return kalendar;
	}
	public void setKalendar(String kalendar) {
		this.kalendar = kalendar;
	}
	public int getPozicija() {
		return pozicija;
	}
	public void setPozicija(int pozicija) {
		this.pozicija = pozicija;
	}
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public int getBoja() {
		return boja;
	}
	public void setBoja(int boja) {
		this.boja = boja;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNaslov() {
		return naslov;
	}
	public void setNaslov(String naslov) {
		this.naslov = naslov;
	}
	public String getSadrzaj() {
		return sadrzaj;
	}
	public void setSadrzaj(String sadrzaj) {
		this.sadrzaj = sadrzaj;
	}
	public long getPocetak() {
		return pocetak;
	}
	public void setPocetak(long pocetak) {
		this.pocetak = pocetak;
	}
	public long getKraj() {
		return kraj;
	}
	public void setKraj(long kraj) {
		this.kraj = kraj;
	}

}
