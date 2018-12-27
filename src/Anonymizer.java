import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;




public class Anonymizer {
	
	private Area cacheArea;//缓存当前查询用户的查询区域
	private Area queryArea;//缓存生成的匿名查询区域
	private int[] identifierOfGrid;//网格标识
	private int timestamp;//当期用户的时间戳
	private Map<Integer, List<User>> cacheSpace;//缓存内容：时间戳+result
	
	
	/**
	 * 生成匿名空间
	 * @param areaList 得到region集合
	 * @return 生成大的匿名空间
	 */
	public void createAnonymityArea(List<Area> areaList){
		int maxx=0;
		int maxy=0;
		int minx=Integer.MAX_VALUE;
		int miny=Integer.MAX_VALUE;
		Area kanonymityArea=new Area();
		for(Area area:areaList){
			int amaxx=area.getMaxx();
			int aminx=area.getMinx();
			int amaxy=area.getMaxy();
			int aminy=area.getMiny();
			if(amaxx>maxx){
				maxx=amaxx;
			}
			if(aminx<minx){
				minx=aminx;
			}
			if(amaxy>maxy){
				maxy=amaxy;
			}
			if(aminy<miny){
				miny=aminy;
			}
		}
		kanonymityArea.setMaxx(maxx);
		kanonymityArea.setMinx(minx);
		kanonymityArea.setMaxy(maxy);
		kanonymityArea.setMiny(miny);
		this.queryArea=kanonymityArea;
		
	}
	
	/**
	 * 生成查询MSG
	 * @param MSGu2a 得到来自用户的MSG
	 * @return 生成查询MSG
	 */
	public Map<String, Object> generateMSGA2L(Map<String, Object> MSGu2a){
		Map<String, Object> MSGa2l=new HashMap<String, Object>();
		MSGa2l.put("C-Region", this.queryArea);
		MSGa2l.put("KEY",MSGu2a.get("KEY"));
		MSGa2l.put("PARAMETER", MSGu2a.get("PARAMETER"));
		MSGa2l.put("grid_structure", MSGu2a.get("grid_structure"));
		MSGa2l.put("POI", MSGu2a.get("POI"));
		return MSGa2l;
	}
	
	/**
	 * 判断当前用户是否在缓存中
	 * @param user 当前查询用户
	 * @return 返回该用户是否在缓存中
	 */
	public boolean isCacheContains(User user){
		if(user.getParameter()!=null){
			return true;
		}else {
			return false;
		}
	
	}
	
	public void cacheUserInfo(User user,int r){
		this.timestamp=user.getParameter().getTimestamp();
		this.cacheArea=user.moveQueryArea(r);
	}
	public void updateCache(List<User> result){
		cacheSpace.put(timestamp,result);
	}
	
	//判断是否在用户的查询区域内
	private boolean isIN(Area cacheArea,User poi){
		int gridx=poi.getGridx();
		int gridy=poi.getGridy();
		if(gridx<=cacheArea.getMaxx()&&gridx>=cacheArea.getMinx()&&gridy<=cacheArea.getMaxy()&&gridy>=cacheArea.getMiny()){
			return true;
		}else{
			return false;
	 }
	}
	
	/**
	 * 结果过滤
	 * @return 返回查询用户查询区域内的结果
	 */
	public List<User> resultFilter(){
		List<User> cacheResult=(List<User>) cacheSpace.get(timestamp);
		List<User> filterResult=new ArrayList<>();
		for(User poi:cacheResult){
			if(isIN(cacheArea, poi)){
				filterResult.add(poi);
			}
		}
		return filterResult;
		
		
	}

	//getter,setter
	
	public Area getCacheArea() {
		return cacheArea;
	}

	public void setCacheArea(Area cacheArea) {
		this.cacheArea = cacheArea;
	}

	public Area getQueryArea() {
		return queryArea;
	}

	public void setQueryArea(Area queryArea) {
		this.queryArea = queryArea;
	}

	public int[] getIdentifierOfGrid() {
		return identifierOfGrid;
	}

	public void setIdentifierOfGrid(int[] identifierOfGrid) {
		this.identifierOfGrid = identifierOfGrid;
	}

	public Map<Integer, List<User>> getCacheSpace() {
		return cacheSpace;
	}

	public void setCacheSpace(Map<Integer, List<User>> cacheSpace) {
		this.cacheSpace = cacheSpace;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

}
