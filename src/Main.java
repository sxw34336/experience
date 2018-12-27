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
		
		int averagetime=0;
		
		for(int j=0;j<10;j++){
			dataProcess data=new dataProcess();
			List<User> userList=data.dataGen("src/now.txt");
			QuerySpace querySpace=new QuerySpace(400,4300,21900,30800,200);
			Anonymizer anonymizer=new Anonymizer();
			LBS lbs=new LBS();
			Map<Integer, List<User>> cacheSpace= new HashMap<>();
			anonymizer.setCacheSpace(cacheSpace);
			lbs.querySpace=querySpace;
	/*		long startTime = System.currentTimeMillis();*/
			int radius=0;
			int k=0;
			int timestamp=0;
			long sumtime=0;//����ʱ��
			int communicationcost=0;//�ݶ�ͨ�ſ���
			for(User user:userList){
				if(!(anonymizer.isCacheContains(user))){	
					user.setParameter(new Parameter(600, 30, timestamp++));
					Map<String, Object> userMSG=user.generateMSG(500, 50, userList);//�û����ɷ�����Ϣ(r,k,userlist)
					//System.out.println(userMSG);
					long time1 = System.currentTimeMillis();
					anonymizer.cacheUserInfo(user, radius);//�������洢�û���ѯ��Ϣ
					//System.out.println((List<Area>) userMSG.get("Region"));
					anonymizer.createAnonymityArea((List<Area>) userMSG.get("Region"));				
					Map<String, Object> MSGa2l=anonymizer.generateMSGA2L(userMSG);//������������Ϣ
					long time2 = System.currentTimeMillis();
					List<User> result=lbs.search(MSGa2l, userList, querySpace);//LBS��ѯ�õ����
					long time3 = System.currentTimeMillis();
					anonymizer.updateCache(result);					
					anonymizer.resultFilter();
					long time4 = System.currentTimeMillis();
					long once =time4-time3+time2-time1;
					sumtime+=once;
				}else {
					long time1 = System.currentTimeMillis();
					anonymizer.cacheUserInfo(user, radius);//�������洢�û���ѯ��Ϣ
					Map<Integer, List<User>> cacheMap=anonymizer.getCacheSpace();//��ȡ����
					List<User> beforeResult=cacheMap.get(user.getParameter().getTimestamp());//Ѱ�һ����еĽ��				
					anonymizer.resultFilter();
					long time2 = System.currentTimeMillis();
					long once =time2-time1;
					sumtime+=once;
				}	
				//System.out.println(user.getParameter().getTimestamp());
			}
			averagetime+=sumtime;
			System.out.println("������ʱ�俪����"+sumtime+" ms");
		}
		averagetime=averagetime/10;
		System.out.println("ƽ��ʱ�䣺"+averagetime);
/*		long endTime = System.currentTimeMillis();
		System.out.println("��������ʱ�䣺" + (endTime - startTime) + "ms");*/
}
}
