import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.w3c.dom.UserDataHandler;
import org.w3c.dom.ls.LSException;


public class Main {
	public static void main(String[] args) throws FileNotFoundException {
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
			System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
		}*/
		/*QuerySpace querySpace=new QuerySpace(2700,4900,20900,30800,200);
		User user=new User(0, 0, querySpace);
		LBS lbs=new LBS();
		String content="12345"; String password = "123";
        System.out.println("加密之前：" + content);

        // 加密
        byte[] encrypt = user.encrypt(content, password);
        System.out.println("加密后的内容：" + new String(encrypt));
      //如果想要加密内容不显示乱码，可以先将密文转换为16进制
        String hexStrResult = ParseSystemUtil.parseByte2HexStr(encrypt);
        System.out.println("16进制的密文："  + hexStrResult);
        
        //如果的到的是16进制密文，别忘了先转为2进制再解密
        byte[] twoStrResult = ParseSystemUtil.parseHexStr2Byte(hexStrResult);
        // 解密
        byte[] decrypt = lbs.decrypt(encrypt, password);
        System.out.println("解密后的内容：" + new String(decrypt)); */ 
		int averageLastTime=0;
		int averagetime=0;
		int answerByA=0;
		for(int j=0;j<10;j++){
			dataProcess data=new dataProcess();
			QuerySpace querySpace=new QuerySpace(400,4300,21900,30800,200);
			List<User> userList=data.dataGen("src/now.txt",querySpace);			
			Anonymizer anonymizer=new Anonymizer();
			LBS lbs=new LBS();
			Map<Integer, List<User>> cacheSpace= new HashMap<>();
			anonymizer.setCacheSpace(cacheSpace);
			lbs.querySpace=querySpace;
			long startTime = System.currentTimeMillis();
			int radius=500;
			int k=45;
			int timestamp=0;
			long sumtime=0;//运行时间
			long lasttime=0;
			PrintWriter pw=new PrintWriter(new File("src/out.txt"));
			for(User user:userList){
				if(!(anonymizer.isCacheContains(user))){
					user.setParameter(new Parameter(400, 20, timestamp++));
					long start=System.currentTimeMillis();
					Map<String, Object> userMSG=user.generateMSG(radius, k, userList);//用户生成发送信息(r,k,userlist)
					pw.write(userMSG.toString());
					long time1 = System.currentTimeMillis();
					anonymizer.cacheUserInfo(user, radius);//匿名器存储用户查询信息
					//System.out.println((List<Area>) userMSG.get("Region"));
					anonymizer.createAnonymityArea((List<Area>) userMSG.get("Region"));				
					Map<String, Object> MSGa2l=anonymizer.generateMSGA2L(userMSG);//匿名器生成信息
					pw.write(MSGa2l.toString());
					long time2 = System.currentTimeMillis();
					List<User> result=lbs.search(MSGa2l, userList);//LBS查询得到结果
					for(User user2:result){
						pw.write(user2.getGridx()+user2.getGridy());
					}
					lbs.moveResult(result);
					//System.out.println(result.size());
					long time3 = System.currentTimeMillis();
					anonymizer.updateCache(result);					
					List<User> filterList=anonymizer.resultFilter();
					for(User user3:filterList){
						pw.write(user3.getGridx()+user3.getGridy());
					}
					long time4 = System.currentTimeMillis();
					long once =time4-time3+time2-time1;
					sumtime+=once;
					lasttime+=time4-start;
				}else {
					answerByA++;
					long time1 = System.currentTimeMillis();
					anonymizer.cacheUserInfo(user, radius);//匿名器存储用户查询信息
					Map<Integer, List<User>> cacheMap=anonymizer.getCacheSpace();//获取缓存
					List<User> beforeResult=cacheMap.get(user.getParameter().getTimestamp());
					//寻找缓存中的结果				
					anonymizer.resultFilter();
					long time2 = System.currentTimeMillis();
					long once =time2-time1;
					sumtime+=once;
					lasttime+=once;	
				}	
			}
			long end=System.currentTimeMillis();		
			averageLastTime+=lasttime;
			averagetime+=sumtime;
		}
		answerByA/=10;
		averageLastTime/=10;
		averagetime=averagetime/10;
		double hitratio=answerByA/10000.0;
		System.out.println("平均时间："+averagetime+" ms");
		System.out.println("缓存命中率"+hitratio);
		System.out.println("总运行时间："+averageLastTime+" ms");
		long endTime = System.currentTimeMillis();
	}
}
