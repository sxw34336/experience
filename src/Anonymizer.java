import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
	
	
	//���»���
	public void updateCache(List<User> result){
		cacheSpace.put(timestamp,result);
	}
	
	//�ж��Ƿ����û��Ĳ�ѯ������
	private boolean isIN(Area queryArea,User poi){
		if(poi.getGridx()<=queryArea.getMaxx()&&poi.getGridx()>=queryArea.getMinx()&&poi.getGridy()<=queryArea.getMaxy()&&poi.getGridy()>=queryArea.getMiny()){
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
		int count=0;
		for(User poi:cacheResult){
			if(isIN(cacheArea, poi)){
				filterResult.add(poi);
				count++;
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
