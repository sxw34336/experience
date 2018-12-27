import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;




public class Anonymizer {
	
	private Area cacheArea;//���浱ǰ��ѯ�û��Ĳ�ѯ����
	private Area queryArea;//�������ɵ�������ѯ����
	private int[] identifierOfGrid;//�����ʶ
	private int timestamp;//�����û���ʱ���
	private Map<Integer, List<User>> cacheSpace;//�������ݣ�ʱ���+result
	
	
	/**
	 * ���������ռ�
	 * @param areaList �õ�region����
	 * @return ���ɴ�������ռ�
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
	 * ���ɲ�ѯMSG
	 * @param MSGu2a �õ������û���MSG
	 * @return ���ɲ�ѯMSG
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
	 * �жϵ�ǰ�û��Ƿ��ڻ�����
	 * @param user ��ǰ��ѯ�û�
	 * @return ���ظ��û��Ƿ��ڻ�����
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
	
	//�ж��Ƿ����û��Ĳ�ѯ������
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
	 * �������
	 * @return ���ز�ѯ�û���ѯ�����ڵĽ��
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
