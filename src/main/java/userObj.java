import java.util.ArrayList;
import java.util.Date;

public class userObj {
	private String tag;
	private Date dateJoined;
	private short infractionPoints;
	private int numInfractions;
	private ArrayList<String> reasons;
	public userObj(String tag, Date dateJoined, short infractionPoints, int numInfractions, ArrayList<String> reasons) {
		this.setTag(tag);
		this.setDateJoined(dateJoined);
		this.setInfractionPoints(infractionPoints);
		this.setNumInfractions(numInfractions);
	}

	public userObj(short infractPts, int infractionCount, ArrayList<String> reasons) {
		this.setInfractionPoints(infractPts);
		this.setNumInfractions(infractionCount);
		this.setReasons(reasons);
	}

	public int getInfractionPoints() {
		return infractionPoints;
	}

	public void setInfractionPoints(short infractionPoints) {
		this.infractionPoints = infractionPoints;
	}

	public Date getDateJoined() {
		return dateJoined;
	}

	public void setDateJoined(Date dateJoined) {
		this.dateJoined = dateJoined;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getNumInfractions() {
		return numInfractions;
	}

	public void setNumInfractions(int numInfractions) {
		this.numInfractions = numInfractions;
	}

	public ArrayList<String> getReasons() {
		return reasons;
	}

	public void setReasons(ArrayList<String> reasons) {
		this.reasons = reasons;
	}
}
