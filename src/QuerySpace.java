


public class QuerySpace {

	private int startx;//起始点x坐标
	private int starty;//起始点y坐标
	private int endx;//终止点x坐标
	private int endy;//终止点y坐标
	private int n;//网格单元数
	private int xlength;//查询区域宽度
	private int ylength;//查询区域长度
	private int ygrid;//查询区域单元格长度
	private int xgrid;//查询区域单元格宽度
	
	QuerySpace(int startx,int starty,int endx,int endy,int n){
		this.startx=startx;
		this.starty=starty;
		this.endx=endx;
		this.endy=endy;
		this.xlength=endx-startx;
		this.ylength=endy-starty;
		this.n=n;
		this.setYgrid(ylength/n);
		this.setXgrid(xlength/n);
				
	}
	
	//getter setter
	public int getStartx() {
		return startx;
	}
	public void setStartx(int startx) {
		this.startx = startx;
	}
	public int getStarty() {
		return starty;
	}
	public void setStarty(int starty) {
		this.starty = starty;
	}
	public int getEndx() {
		return endx;
	}
	public void setEndx(int endx) {
		this.endx = endx;
	}
	public int getEndy() {
		return endy;
	}
	public void setEndy(int endy) {
		this.endy = endy;
	}
	public int getN() {
		return n;
	}
	public void setN(int n) {
		this.n = n;
	}

	public int getXlength() {
		return xlength;
	}

	public void setXlength(int xlength) {
		this.xlength = xlength;
	}

	public int getYlength() {
		return ylength;
	}

	public void setYlength(int ylength) {
		this.ylength = ylength;
	}

	public int getXgrid() {
		return xgrid;
	}

	public void setXgrid(int xgrid) {
		this.xgrid = xgrid;
	}

	public int getYgrid() {
		return ygrid;
	}

	public void setYgrid(int ygrid) {
		this.ygrid = ygrid;
	}
}
