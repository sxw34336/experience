
public class Area {
	private int minx;
	private int maxx;
	private int maxy;
	private int miny;
	private int acreage;
	
	public int getAcreage(QuerySpace querySpace){
		this.acreage=(maxx-minx+1)*querySpace.getXgrid()*(maxy-miny+1)*querySpace.getYgrid();
		return acreage;
	}
	
	
	
	public int getAcreage() {
		return acreage;
	}
	public void setAcreage(int acreage) {
		this.acreage = acreage;
	}
	public int getMinx() {
		return minx;
	}
	public void setMinx(int minx) {
		this.minx = minx;
	}
	public int getMaxx() {
		return maxx;
	}
	public void setMaxx(int maxx) {
		this.maxx = maxx;
	}
	public int getMaxy() {
		return maxy;
	}
	public void setMaxy(int maxy) {
		this.maxy = maxy;
	}
	public int getMiny() {
		return miny;
	}
	public void setMiny(int miny) {
		this.miny = miny;
	}

}
