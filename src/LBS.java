import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.omg.CORBA.INTERNAL;


public class LBS {
	
	QuerySpace querySpace;
	/**
	 * POI查询
	 * @param MSGa2l LBS收到来自匿名器的信息
	 * @param poisList 地图兴趣点
	 * @param querySpace 整个查询空间
	 * @return 返回查询结果
	 */
	public List<User> search(Map<String, Object> MSGa2l,List<User> poisList,QuerySpace querySpace){
		Area queryArea = (Area) MSGa2l.get("C-Region");
		int poi_type = (int) MSGa2l.get("POI");
		Parameter parameter=(Parameter) MSGa2l.get("PARAMETER");
		int distance=parameter.getDiatance();
		int direction=parameter.getDirection();
		Area realArea=resolve(queryArea, distance,direction);
		//System.out.println("minx:"+realArea.getMinx()+"maxx:"+realArea.getMaxx());
		List<User> resultList=new ArrayList<User>();
		for(User poi:poisList){
			if(isIN(realArea, poi)&&poi.getPoiClass()==poi_type){
				User movepoi=new User(poi.getX()+distance*Math.cos(direction), poi.getY()+distance*Math.sin(direction), querySpace);
				resultList.add(poi);
			}
		}
		//System.out.println(resultList);
		return resultList;
	}
	
	/**
	 * 恢复真实的匿名区域
	 * @param feak 偏移匿名区域
	 * @param distance 偏移距离
	 * @param direction 偏移方向
	 * @return 返回真实的匿名区域(非网格)
	 */
	private Area resolve(Area feak,int distance,int direction){
		Area real = new Area();	
		int xgrid=querySpace.getXgrid();
		int ygrid=querySpace.getYgrid();
		real.setMaxx(feak.getMaxx()*xgrid-(int)(distance*Math.cos(direction)));
		real.setMaxy(feak.getMaxy()*ygrid-(int)(distance*Math.sin(direction)));
		real.setMinx(feak.getMinx()*xgrid-(int)(distance*Math.cos(direction)));
		real.setMiny(feak.getMiny()*ygrid-(int)(distance*Math.sin(direction)));
		return real;
	}
	
	/**
	 * 判断兴趣点是否在查询区域内
	 * @param queryArea 查询区域（匿名区域）
	 * @param poi 地图上的所有兴趣点
	 * @return 返回是否在查询区域内（非网格）
	 */
	private boolean isIN(Area queryArea,User poi){
		if(poi.getX()<=queryArea.getMaxx()&&poi.getX()>=queryArea.getMinx()&&poi.getY()<=queryArea.getMaxy()&&poi.getY()>=queryArea.getMiny()){
			return true;
		}else{
			return false;
	 }
	}

	
	/**
     * 解密AES加密过的字符串
     * 
     * @param content
     *            AES加密过过的内容
     * @param password
     *            加密时的密码
     * @return 明文
     */
    public static byte[] decrypt(byte[] content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥
            byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");// 转换为AES专用密钥
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化为解密模式的密码器
            byte[] result = cipher.doFinal(content);  
            return result; // 明文   
            
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
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
}
