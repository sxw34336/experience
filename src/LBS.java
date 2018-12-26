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
	 * POI��ѯ
	 * @param MSGa2l LBS�յ���������������Ϣ
	 * @param poisList ��ͼ��Ȥ��
	 * @param querySpace ������ѯ�ռ�
	 * @return ���ز�ѯ���
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
	 * �ָ���ʵ����������
	 * @param feak ƫ����������
	 * @param distance ƫ�ƾ���
	 * @param direction ƫ�Ʒ���
	 * @return ������ʵ����������(������)
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
	 * �ж���Ȥ���Ƿ��ڲ�ѯ������
	 * @param queryArea ��ѯ������������
	 * @param poi ��ͼ�ϵ�������Ȥ��
	 * @return �����Ƿ��ڲ�ѯ�����ڣ�������
	 */
	private boolean isIN(Area queryArea,User poi){
		if(poi.getX()<=queryArea.getMaxx()&&poi.getX()>=queryArea.getMinx()&&poi.getY()<=queryArea.getMaxy()&&poi.getY()>=queryArea.getMiny()){
			return true;
		}else{
			return false;
	 }
	}

	
	/**
     * ����AES���ܹ����ַ���
     * 
     * @param content
     *            AES���ܹ���������
     * @param password
     *            ����ʱ������
     * @return ����
     */
    public static byte[] decrypt(byte[] content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");// ����AES��Key������
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();// �����û����룬����һ����Կ
            byte[] enCodeFormat = secretKey.getEncoded();// ���ػ��������ʽ����Կ
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");// ת��ΪAESר����Կ
            Cipher cipher = Cipher.getInstance("AES");// ����������
            cipher.init(Cipher.DECRYPT_MODE, key);// ��ʼ��Ϊ����ģʽ��������
            byte[] result = cipher.doFinal(content);  
            return result; // ����   
            
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
