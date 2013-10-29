package hr.zbc.remindme;

public class DaoAlarmDetails {
	
	private int reqCode, startTime, endTime, howManyRepetitions, dailyOrWeekly;
	private long id;
	private String title;
	
	public DaoAlarmDetails(long id, String title, int reqCode, int startTime, int endTime, int howManyRepetitions, int dailyOrWeekly){
		this.id = id;
		this.title = title;
		this.reqCode = reqCode;
		this.startTime = startTime;
		this.endTime = endTime;
		this.howManyRepetitions = howManyRepetitions;
		this.dailyOrWeekly = dailyOrWeekly;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getReqCode() {
		return reqCode;
	}

	public void setReqCode(int reqCode) {
		this.reqCode = reqCode;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public int getHowManyRepetitions() {
		return howManyRepetitions;
	}

	public void setHowManyRepetitions(int howManyRepetitions) {
		this.howManyRepetitions = howManyRepetitions;
	}

	public int getDailyOrWeekly() {
		return dailyOrWeekly;
	}

	public void setDailyOrWeekly(int dailyOrWeekly) {
		this.dailyOrWeekly = dailyOrWeekly;
	}
}
