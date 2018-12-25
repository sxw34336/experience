import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.UserDataHandler;


public class Main {
	public static void main(String[] args) {
		/*dataProcess data=new dataProcess();
		List<User> userList=data.dataGen("src/gendata.txt");
		QuerySpace querySpace=new QuerySpace(2700,4900,20900,30800,200);
		Anonymizer anonymizer=new Anonymizer();
		for(int i=10;i<=50;i=i+10){
			long startTime = System.currentTimeMillis();
			for(User user:userList){
				Map<String, Object> MSG1=new HashMap<String, Object>();
				MSG1=user.generateMSG(500, i, userList);
				Map<String, Object> MSG2=new HashMap<String, Object>();	
			}
			long endTime = System.currentTimeMillis();
			System.out.println("��������ʱ�䣺" + (endTime - startTime) + "ms");
		}*/
		/*QuerySpace querySpace=new QuerySpace(2700,4900,20900,30800,200);
		User user=new User(0, 0, querySpace);
		LBS lbs=new LBS();
		String content="12345"; String password = "123";
        System.out.println("����֮ǰ��" + content);

        // ����
        byte[] encrypt = user.encrypt(content, password);
        System.out.println("���ܺ�����ݣ�" + new String(encrypt));
      //�����Ҫ�������ݲ���ʾ���룬�����Ƚ�����ת��Ϊ16����
        String hexStrResult = ParseSystemUtil.parseByte2HexStr(encrypt);
        System.out.println("16���Ƶ����ģ�"  + hexStrResult);
        
        //����ĵ�����16�������ģ���������תΪ2�����ٽ���
        byte[] twoStrResult = ParseSystemUtil.parseHexStr2Byte(hexStrResult);
        // ����
        byte[] decrypt = lbs.decrypt(encrypt, password);
        System.out.println("���ܺ�����ݣ�" + new String(decrypt)); */ 
		dataProcess data=new dataProcess();
		List<User> userList=data.dataGen("src/gendata.txt");
		QuerySpace querySpace=new QuerySpace(2700,4900,20900,30800,200);
		Anonymizer anonymizer=new Anonymizer();
		Map<Integer, List<User>> cacheSpace= new HashMap<>();
		anonymizer.setCacheSpace(cacheSpace);
		LBS lbs=new LBS();
		lbs.querySpace=querySpace;
/*		long startTime = System.currentTimeMillis();*/
		int radius=0;
		int k=0;
		int timestamp=0;
		for(User user:userList){
			if(!(anonymizer.isCacheContains(user))){	
				user.setParameter(new Parameter(500, 0, timestamp++));
				Map<String, Object> userMSG=user.generateMSG(500, 50, userList);//�û����ɷ�����Ϣ(r,k,userlist)
				System.out.println(userMSG);
				anonymizer.cacheUserInfo(user, radius);//�������洢�û���ѯ��Ϣ
				System.out.println((List<Area>) userMSG.get("Region"));
				anonymizer.createAnonymityArea((List<Area>) userMSG.get("Region"));
				Map<String, Object> MSGa2l=anonymizer.generateMSGA2L(userMSG);//������������Ϣ
				List<User> result=lbs.search(MSGa2l, userList, querySpace);//LBS��ѯ�õ����
				anonymizer.updateCache(result);
				anonymizer.resultFilter();
				
			}else {
				anonymizer.cacheUserInfo(user, radius);//�������洢�û���ѯ��Ϣ
				Map<Integer, List<User>> cacheMap=anonymizer.getCacheSpace();//��ȡ����
				List<User> beforeResult=cacheMap.get(user.getParameter().getTimestamp());//Ѱ�һ����еĽ��
				anonymizer.resultFilter();
			}
			//System.out.println(user.getParameter().getTimestamp());
			
		}
/*		long endTime = System.currentTimeMillis();
		System.out.println("��������ʱ�䣺" + (endTime - startTime) + "ms");*/
}
}
