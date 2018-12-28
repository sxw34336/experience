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

	private int userID;//当前用户id
	private double x;//当前用户的x坐标
	private double y;//当前用户的y坐标
	private int poiClass;//当前用户的兴趣点类型
	private int gridx;//当前用户所在网格单元的x坐标
	private int gridy;//当前用户所在网格单元的y坐标
	private QuerySpace querySpace;//当前用户的查询空间
	private Parameter parameter;//当前用户的偏移参数

	/**
	 * 网格结构转换
	 * @param x 用户的x坐标（真实）
	 * @param y 用户的y坐标（真实）
	 */
	public User(double x,double y,QuerySpace querySpace) {
		this.x=x;
		this.y=y;
		this.querySpace=querySpace;
		this.gridx=(int) (x-querySpace.getStartx())/(querySpace.getXlength()/querySpace.getN());
		this.gridy=(int) (y-querySpace.getStarty())/(querySpace.getYlength()/querySpace.getN());
	}
	
	/**
	 * 计算当前用户查询区域
	 * @param r 设定的查询半径
	 * @return queryArea 得到用户的查询区域
	 */
	public Area getQueryArea(double x,double y,int r){
		Area queryArea=new Area();
		//网格坐标
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
	 * @param r 输入查询半径
	 * @return 获得偏移后的查询区域（调用getQueryArea）
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
		//System.out.println("偏移前：（"+this.x+","+this.y+")"+"   偏移后：（"+xx+","+yy+")");
		return moveArea;//网格坐标
		
		
	}

	/**
	 * 生成MSGU2A（包含构建匿名集合过程）
	 * @param r 用户的查询半径
	 * @param k 用户的k匿名需求
	 * @param userList 进行knn搜索的其他用户
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
	 * @param beforeresult 经过匿名器粗过滤后的结果
	 * @param r 查询半径
	 * @return 用户精过滤结果
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
			kanonymityList.add(candidate.get(count).moveQueryArea(r));
			count++;
		}
		return kanonymityList;
	}
	//改进top-k(MaxHeap)
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
			System.out.println("匿名失败");
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
		//设置k个用户的偏移参数
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
