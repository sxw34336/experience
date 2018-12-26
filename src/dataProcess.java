import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class dataProcess {

	
	
	
	public static List<User> dataGen(String url){
		BufferedReader bfr=null;
		try {
			bfr=new BufferedReader(new FileReader(url));
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<User> pointList=new ArrayList<User>();
		String line;
		QuerySpace querySpace=new QuerySpace(400,4300,21900,30800,200);
		try {
			while ((line=bfr.readLine())!=null) {
				String[] content=line.split("	");
				User point=new User(Double.parseDouble(content[1]), Double.parseDouble(content[2]),querySpace);
/*				int poiClass=new Random().nextInt(4);*/
				point.setPoiClass(Integer.parseInt(content[3]));
				point.setUserID(Integer.parseInt(content[0]));
				pointList.add(point);	
			}
			}catch(Exception e){
				e.printStackTrace();
			}
		return pointList;
	}

}
