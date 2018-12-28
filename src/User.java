import java.io.BufferedReader;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.omg.CORBA.PUBLIC_MEMBER;



public class User {

	private int userID;//��ǰ�û�id
	private double x;//��ǰ�û���x����
	private double y;//��ǰ�û���y����
	private int poiClass;//��ǰ�û�����Ȥ������
	private int gridx;//��ǰ�û���������Ԫ��x����
	private int gridy;//��ǰ�û���������Ԫ��y����
	private QuerySpace querySpace;//��ǰ�û��Ĳ�ѯ�ռ�
	private Parameter parameter;//��ǰ�û���ƫ�Ʋ���

	/**
	 * ����ṹת��
	 * @param x �û���x���꣨��ʵ��
	 * @param y �û���y���꣨��ʵ��
	 */
	public User(double x,double y,QuerySpace querySpace) {
		this.x=x;
		this.y=y;
		this.querySpace=querySpace;
		this.gridx=(int) (x-querySpace.getStartx())/(querySpace.getXlength()/querySpace.getN());
		this.gridy=(int) (y-querySpace.getStarty())/(querySpace.getYlength()/querySpace.getN());
	}
	
	/**
	 * ���㵱ǰ�û���ѯ����
	 * @param r �趨�Ĳ�ѯ�뾶
	 * @return queryArea �õ��û��Ĳ�ѯ����
	 */
	public Area getQueryArea(double x,double y,int r){
		Area queryArea=new Area();
		//��������
		int minx=(int) (x-r-querySpace.getStartx())/(querySpace.getXlength()/querySpace.getN());
		int maxx=(int) (x+r-querySpace.getStartx())/(querySpace.getXlength()/querySpace.getN());
		int miny=(int) (y-r-querySpace.getStarty())/(querySpace.getYlength()/querySpace.getN());
		int maxy=(int) (y+r-querySpace.getStarty())/(querySpace.getYlength()/querySpace.getN());
		queryArea.setMaxx(maxx);
		queryArea.setMaxy(maxy);
		queryArea.setMinx(minx);
		queryArea.setMiny(miny);
		return queryArea;
		
	}
	public Parameter getParameter() {
		return parameter;
	}

	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}

	/**
	 * 
	 * @param r �����ѯ�뾶
	 * @return ���ƫ�ƺ�Ĳ�ѯ���򣨵���getQueryArea��
	 */
	public Area moveQueryArea(int r){
		int direction=0;
		int distance=0;
		if(this.parameter!=null){
			direction=this.parameter.getDirection();
			distance=this.parameter.getDiatance();
		}
		Area moveArea = new Area();
		double xx=x+distance*Math.cos(direction);
		double yy=y+distance*Math.sin(direction);
		moveArea=getQueryArea(xx, yy, r);
		//System.out.println("ƫ��ǰ����"+this.x+","+this.y+")"+"   ƫ�ƺ󣺣�"+xx+","+yy+")");
		return moveArea;//��������
		
		
	}

	/**
	 * ����MSGU2A�����������������Ϲ��̣�
	 * @param r �û��Ĳ�ѯ�뾶
	 * @param k �û���k��������
	 * @param userList ����knn�����������û�
	 */
	public Map<String, Object> generateMSG(Integer r,int k,List<User> userList){
		List<Area> knnAreas=searchKnn2(r,k, userList);
		Map<String, Object> MSGu2a=new HashMap<String, Object>();
		MSGu2a.put("ID",userID );
		MSGu2a.put("Region", knnAreas);
		MSGu2a.put("KEY", "K1,K2,K3,K4");
		MSGu2a.put("S", moveQueryArea(r));
		MSGu2a.put("POI", poiClass);
		MSGu2a.put("grid_structure", querySpace);
		MSGu2a.put("PARAMETER", this.getParameter());
		MSGu2a.put("timestamp", this.getParameter().getTimestamp());
		return MSGu2a;
	}
	
	/**
	 * ��������֮�����
	 * @param point ���������û�
	 * @return ���ش��û��뵱ǰ��ѯ�û�֮��ľ���
	 */
	public double getDistance(User user){
		double x=user.getX();
		double y=user.getY();
		double distance=Math.sqrt(Math.pow(this.x-x, 2)+Math.pow(this.y-y, 2));
		return distance;
	}
	
	public void UpToDown(int i,int k,List<User> topkList){
		int t1,t2,pos;
		t1=2*i;
		t2=t1+1;
		if(t1>k)
			return;
		else{
			if(t2>k){
				pos=t1;
			}
			else{
				double d1=getDistance(topkList.get(t1-1));
				double d2=getDistance(topkList.get(t2-1));
				pos=d1>d2?t1:t2;
			}
			double dis1=getDistance(topkList.get(i-1));
			double dis2=getDistance(topkList.get(pos-1));
			if(dis1<dis2){
				Collections.swap(topkList, i-1, pos-1);
				UpToDown(pos, k, topkList);
			}
		}
	}
	
	public void crateHeap(int k,List<User> topkList){
		int i;
		int pos=k/2;
		for(i=pos;i>=1;i--){
			UpToDown(i, k, topkList);
		}
	}
	
	
	Comparator<User> comparator=new Comparator<User>() {
		@Override
		public int compare(User u1, User u2) {
			double distance1=getDistance(u1);
			double distance2=getDistance(u2);
			return distance1>distance2?1:-1;
		}
	};
	
	/**
	 * 
	 * @param beforeresult �����������ֹ��˺�Ľ��
	 * @param r ��ѯ�뾶
	 * @return �û������˽��
	 */
	public List<User> refine(List<User> beforeresult,int r){
		List<User> afterresult=new ArrayList<>();
		for(User poi:beforeresult){
			if(getDistance(poi)<=r){
				afterresult.add(poi);
			}
		}
		return afterresult;
	}

	/**
     * AES�����ַ���
     * 
     * @param content
     *            ��Ҫ�����ܵ��ַ���
     * @param password
     *            ������Ҫ������
     * @return ����
     */
    public static byte[] encrypt(String content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");// ����AES��Key������

            kgen.init(128, new SecureRandom(password.getBytes()));// �����û�������Ϊ�������ʼ����
                                                                    // 128λ��key������
            //����û��ϵ��SecureRandom�����ɰ�ȫ��������У�password.getBytes()�����ӣ�ֻҪ������ͬ�����о�һ�������Խ���ֻҪ��password����

            SecretKey secretKey = kgen.generateKey();// �����û����룬����һ����Կ

            byte[] enCodeFormat = secretKey.getEncoded();// ���ػ��������ʽ����Կ���������Կ��֧�ֱ��룬�򷵻�
                                                            // null��

            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");// ת��ΪAESר����Կ

            Cipher cipher = Cipher.getInstance("AES");// ����������

            byte[] byteContent = content.getBytes("utf-8");

            cipher.init(Cipher.ENCRYPT_MODE, key);// ��ʼ��Ϊ����ģʽ��������

            byte[] result = cipher.doFinal(byteContent);// ����

            return result;

        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
	
	/**
	 * ����knn��ѯ�û�
	 * @param n k������
	 * @return ������������Ա�б�
	 */
	public List<Area> searchKnn(int r,int k,List<User> userList){
		List<User> candidate=new ArrayList<User>();
		List<Area> kanonymityList=new ArrayList<Area>();
		int count=0;
		for(int i=0;i<userList.size();i++){
			User user=userList.get(i);
			if(user.getPoiClass()==this.poiClass&&user.getUserID()!=this.userID){
				candidate.add(user);
			}
		}
		if(candidate!=null&&candidate.size()>0){
			candidate.sort(comparator);
		}
		while (count<k) {
			kanonymityList.add(candidate.get(count).moveQueryArea(r));
			count++;
		}
		return kanonymityList;
	}
	//�Ľ�top-k(MaxHeap)
	public List<Area> searchKnn2(int r,int k,List<User> userList){
		List<User> candidate=new ArrayList<User>();
		List<User> kanonymityList=new ArrayList<User>();
		List<Area> kanonymityAreas=new ArrayList<>();
		List<User> topkList=new ArrayList<>();
		int count=0;
		for(int i=0;i<userList.size();i++){
			User user=userList.get(i);
			if(user.getPoiClass()==this.poiClass&&user.getUserID()!=this.userID){
				candidate.add(user);
			}
		}
		//System.out.println("candidate:"+candidate.size());
		if(candidate.size()<k){
			System.out.println("����ʧ��");
			return null;
		}
		for(int j=0;j<k;j++){
			topkList.add(candidate.get(j));
		}
		//System.out.println("topk:"+topkList.size());
		crateHeap(k, topkList);
		for(int z=k;z<candidate.size();z++){
			if(getDistance(candidate.get(z))<getDistance(topkList.get(0))){
				topkList.set(0, candidate.get(z));
				UpToDown(1, k, topkList);
			}
		}
		kanonymityList=topkList;
		//����k���û���ƫ�Ʋ���
		for(User user:userList){
			for(User topkUser:topkList){
				if(user.getUserID()==topkUser.getUserID()){
					user.setParameter(this.parameter);
				}
			}
			/*if(user.getParameter()!=null){
				System.out.println("ID:"+user.getUserID()+"  Time:"+user.getParameter().getTimestamp());
			}*/
			
		}
		for(User user:kanonymityList){
			kanonymityAreas.add(user.moveQueryArea(r));
		}
		//System.out.println(kanonymityList.size());
		return kanonymityAreas;
	}
	
	
	
	//getter setter
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public int getPoiClass() {
		return poiClass;
	}
	public void setPoiClass(int poiClass) {
		this.poiClass = poiClass;
	}
	public int getGridx() {
		return gridx;
	}
	public void setGridx(int gridx) {
		this.gridx = gridx;
	}
	public int getGridy() {
		return gridy;
	}
	public void setGridy(int gridy) {
		this.gridy = gridy;
	}

	public QuerySpace getQuerySpace() {
		return querySpace;
	}

	public void setQuerySpace(QuerySpace querySpace) {
		this.querySpace = querySpace;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}




}
