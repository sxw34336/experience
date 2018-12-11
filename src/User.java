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



public class User {

	private int userID;//��ǰ�û�id
	private double x;//��ǰ�û���x����
	private double y;//��ǰ�û���y����
	private int poiClass;//��ǰ�û�����Ȥ������
	private int gridx;//��ǰ�û���������Ԫ��x����
	private int gridy;//��ǰ�û���������Ԫ��y����
	private QuerySpace querySpace;//��ǰ�û��Ĳ�ѯ�ռ�

	/**
	 * ����ṹת��
	 * @param ��ʼ���x����
	 * @param ��ʼ���y����
	 * @param ÿ������Ԫ�ı߳�
	 */
	public User(double x,double y,QuerySpace querySpace) {
		this.querySpace=querySpace;
		this.x=x;
		this.y=y;
		this.gridx=(int) (x-querySpace.getStartx())/(querySpace.getXlength()/querySpace.getN());
		this.gridy=(int) (y-querySpace.getStarty())/(querySpace.getYlength()/querySpace.getN());
	}
	
	//��ǰ�û��Ĳ�ѯ�����ʾ
	public Area getQueryArea(int r){
		Area querArea=new Area();
		//��������
		int minx=(int) (x-r-querySpace.getStartx())/(querySpace.getXlength()/querySpace.getN());
		int maxx=(int) (x+r-querySpace.getStartx())/(querySpace.getXlength()/querySpace.getN());
		int miny=(int) (y-r-querySpace.getStarty())/(querySpace.getYlength()/querySpace.getN());
		int maxy=(int) (y+r-querySpace.getStarty())/(querySpace.getYlength()/querySpace.getN());
		querArea.setMaxx(maxx);
		querArea.setMaxy(maxy);
		querArea.setMinx(minx);
		querArea.setMiny(miny);
		return querArea;
		
	}

	/**
	 * ����MSGU2A�����������������Ϲ��̣�
	 * @param r �û��Ĳ�ѯ�뾶
	 * @param k �û���k��������
	 * @param userList ����knn�����������û�
	 */
	public Map<String, Object> generateMSG(Integer r,int k,List<User> userList){
		List<Area> knnAreas=searchKnn(r,k, userList);
		Map<String, Object> MSGu2a=new HashMap<String, Object>();
		MSGu2a.put("ID",userID );
		MSGu2a.put("Region", knnAreas);
		MSGu2a.put("KEY", "K1,K2,K3,K4");
		MSGu2a.put("S", getQueryArea(r));
		MSGu2a.put("POI", poiClass);
		MSGu2a.put("grid_structure", querySpace);
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
	
	Comparator<User> comparator=new Comparator<User>() {
		@Override
		public int compare(User u1, User u2) {
			double distance1=getDistance(u1);
			double distance2=getDistance(u2);
			return distance1>distance2?1:-1;
		}
	};
	

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
			kanonymityList.add(candidate.get(count).getQueryArea(r));
			count++;
		}
		return kanonymityList;
	}
	//�Ľ�top-k
	public List<Area> searchKnn2(int r,int k,List<User> userList){
		List<User> candidate=new ArrayList<User>();
		List<User> kanonymityList=new ArrayList<User>();
		List<Area> kanonymityAreas=new ArrayList<>();
		int count=0;
		for(int i=0;i<userList.size();i++){
			User user=userList.get(i);
			if(user.getPoiClass()==this.poiClass&&user.getUserID()!=this.userID){
				candidate.add(user);
			}
		}
		double mindistance=Double.POSITIVE_INFINITY;
		while (count<k) {
			int id=0;
			for(User user:candidate){
				double distance=getDistance(user);
				if((kanonymityList.contains(user)==false)&&distance<mindistance){
					id=user.getUserID();
					mindistance=distance;
				}
			}
			kanonymityList.add(userList.get(id-1));
			count++;			
		}
		for(User user:kanonymityList){
			kanonymityAreas.add(user.getQueryArea(r));
		}
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
