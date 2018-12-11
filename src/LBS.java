import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class LBS {
	public void search(){
		
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
