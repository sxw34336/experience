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

	private int userID;//当前用户id
	private double x;//当前用户的x坐标
	private double y;//当前用户的y坐标
	private int poiClass;//当前用户的兴趣点类型
	private int gridx;//当前用户所在网格单元的x坐标
	private int gridy;//当前用户所在网格单元的y坐标
	private QuerySpace querySpace;//当前用户的查询空间

	/**
	 * 网格结构转换
	 * @param 起始点的x坐标
	 * @param 起始点的y坐标
	 * @param 每个网格单元的边长
	 */
	public User(double x,double y,QuerySpace querySpace) {
		this.querySpace=querySpace;
		this.x=x;
		this.y=y;
		this.gridx=(int) (x-querySpace.getStartx())/(querySpace.getXlength()/querySpace.getN());
		this.gridy=(int) (y-querySpace.getStarty())/(querySpace.getYlength()/querySpace.getN());
	}
	
	//当前用户的查询区域表示
	public Area getQueryArea(int r){
		Area querArea=new Area();
		//网格坐标
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
	 * 生成MSGU2A（包含构建匿名集合过程）
	 * @param r 用户的查询半径
	 * @param k 用户的k匿名需求
	 * @param userList 进行knn搜索的其他用户
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
	 * 计算两点之间距离
	 * @param point 输入其他用户
	 * @return 返回此用户与当前查询用户之间的距离
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
     * AES加密字符串
     * 
     * @param content
     *            需要被加密的字符串
     * @param password
     *            加密需要的密码
     * @return 密文
     */
    public static byte[] encrypt(String content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者

            kgen.init(128, new SecureRandom(password.getBytes()));// 利用用户密码作为随机数初始化出
                                                                    // 128位的key生产者
            //加密没关系，SecureRandom是生成安全随机数序列，password.getBytes()是种子，只要种子相同，序列就一样，所以解密只要有password就行

            SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥

            byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥，如果此密钥不支持编码，则返回
                                                            // null。

            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");// 转换为AES专用密钥

            Cipher cipher = Cipher.getInstance("AES");// 创建密码器

            byte[] byteContent = content.getBytes("utf-8");

            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化为加密模式的密码器

            byte[] result = cipher.doFinal(byteContent);// 加密

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
	 * 进行knn查询用户
	 * @param n k匿名数
	 * @return 返回匿名集成员列表
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
	//改进top-k
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
