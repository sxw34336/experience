
public class Parameter {
	private int diatance;
	private int direction;
	private int timestamp;
	public Parameter(int distance,int direction,int timestamp){
		this.diatance=distance;
		this.direction=direction;
		this.timestamp=timestamp;
	}
	
	public int getDiatance() {
		return diatance;
	}
	public void setDiatance(int diatance) {
		this.diatance = diatance;
	}
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	public int getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

}
