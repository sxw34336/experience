import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;




public class Anonymizer {
	
	private Area cacheArea;
	private Area queryArea;
	private int[] identifierOfGrid;
	/**
	 * 生成匿名空间
	 * @param areaList 得到region集合
	 * @return 生成大的匿名空间
	 */
	public Area createAnonymityArea(List<Area> areaList){
		List<Integer> xList=new ArrayList<Integer>();
		List<Integer> yList=new ArrayList<Integer>();
		Area kanonymityArea=new Area();
		for(Area area:areaList){
			xList.add(area.getMaxx());
			xList.add(area.getMinx());
			yList.add(area.getMaxy());
			yList.add(area.getMiny());
		}
		Collections.sort(xList);
		Collections.sort(yList);
		kanonymityArea.setMaxx(xList.get(xList.size()-1));
		kanonymityArea.setMinx(xList.get(0));
		kanonymityArea.setMaxy(yList.get(yList.size()-1));
		kanonymityArea.setMiny(yList.get(0));
		return kanonymityArea;
		
	}
	
	/**
	 * 生成查询MSG
	 * @param MSGu2a 得到来自用户的MSG
	 * @return 生成查询MSG
	 */
	public Map<String, Object> generateMSGA2L(Map<String, Object> MSGu2a){
		List<Area> areaList=(List<Area>)MSGu2a.get("Region");
		Area c_region=createAnonymityArea(areaList);
		Map<String, Object> MSGa2l=new HashMap<String, Object>();
		MSGa2l.put("C-Region", c_region);
		MSGa2l.put("KEY",MSGu2a.get("KEY"));
		MSGa2l.put("grid_structure", MSGu2a.get("grid_structure"));
		MSGa2l.put("POI", MSGu2a.get("POI"));
		return MSGa2l;
	}
	
	//判断是否在缓存中
	public void isCacheContains(){
	
	}
	
	
	//更新缓存
	public void updateCache(Area anonymityArea){
		
	}

}
